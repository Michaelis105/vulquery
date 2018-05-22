package xyz.vulquery.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.stereotype.Component;
import xyz.vulquery.dependency.Dependency;
import xyz.vulquery.util.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Handles all conversion operations of NVD NIST GOV data feed sources
 */
@Component("datafeedParser")
public class NVDNISTJSONParser implements DataFeedParser {

    private final Logger logger = LoggerFactory.getLogger(NVDNISTJSONParser.class);

    // Specific to NVD NIST DATA FEED
    private static final String CVE_ITEMS = "CVE_Items";

    // CVE and children
    private static final String CVE = "cve";
    private static final String AFFECTS = "affects";
    private static final String VENDOR = "vendor";
    private static final String VENDOR_DATA = "vendor_data";
    private static final String VENDOR_NAME = "vendor_name";

    private static final String PRODUCT = "product";
    private static final String PRODUCT_DATA = "product_data";
    private static final String PRODUCT_NAME = "product_name";

    private static final String VERSION = "version";
    private static final String VERSION_DATA = "version_data";
    private static final String VERSION_VALUE = "version_value";

    // Configurations and children
    private static final String CONFIGURATIONS = "configurations";

    // Impact and children
    private static final String IMPACT = "impact";

    private static final String PUBLISHED_DATE = "publishedDate";
    private static final String LAST_MODIFIED_DATE = "lastModifiedDate";

    private static final String BASE_METRIC_V2 = "baseMetricV2";
    private static final String CVSS_V2 = "cvssV2";
    private static final String BASE_SCOREV2 = "baseScore"; // For base metric V3

    private static final String BASE_METRIC_V3 = "baseMetricV3";
    private static final String CVSS_V3 = "cvssV3";
    private static final String BASE_SCOREV3 = "baseScore"; // For base metric V3



    /**
     * Decodes NVD NIST GOV data feed JSON string.
     *
     * @param data JSON String from NIST NVD GOV JSON data feed
     * @return list of decoded dependencies
     */
    @Override
    public List<Dependency> decode(String data) {
        if (StringUtils.isBlank(data)) {
            throw new IllegalArgumentException("JSON string is null or empty.");
        }

        Map<String, Dependency> dependencyMap = new HashMap<String, Dependency>();

        JsonParser jsonParser = new JsonParser();
        JsonArray CVEItems = jsonParser.parse(data).getAsJsonObject().get(CVE_ITEMS).getAsJsonArray();
        int i = 0;
        for (JsonElement cveElement : CVEItems) {
            logger.debug("Vulnerability: " + String.valueOf(i++));

            JsonObject CVEItem = cveElement.getAsJsonObject();

            // CVE and children.
            JsonElement cve = CVEItem.get(CVE);
            JsonElement config = CVEItem.get(CONFIGURATIONS);
            JsonElement impact = CVEItem.get(IMPACT);
            String publishedDate = CVEItem.get(PUBLISHED_DATE).getAsString();
            String lastModDate = CVEItem.get(LAST_MODIFIED_DATE).getAsString();

            double baseScore = getImpactBaseScore(impact);
            if (baseScore < 0) {
                continue; // CVE has no defined vulnerability metric.
            }

            // Affects and children.
            JsonArray vendors = cve.getAsJsonObject().get(AFFECTS)
                    .getAsJsonObject().get(VENDOR)
                    .getAsJsonObject().get(VENDOR_DATA).getAsJsonArray();

            if (vendors.size() == 0) {
                continue; // Vulnerability probably associated with resource/URL and not dependency.
            }

            int j = 0;
            for (JsonElement vendorElement : vendors) {
                logger.debug("Vendor: " + String.valueOf(j++));

                JsonObject vendorItem = vendorElement.getAsJsonObject();
                String vendorName = vendorItem.get(VENDOR_NAME).getAsString();

                // Product and children.
                JsonArray products = vendorItem.get(PRODUCT).getAsJsonObject().get(PRODUCT_DATA).getAsJsonArray();
                if (products.size() == 0) {
                    continue;
                }

                int k = 0;
                for (JsonElement productElement : products) {
                    logger.debug("Vendor: " + String.valueOf(k++));

                    JsonObject productItem = productElement.getAsJsonObject();
                    String productName = productItem.get(PRODUCT_NAME).getAsString();

                    // Version and children.
                    JsonArray versions = productItem.get(VERSION).getAsJsonObject().get(VERSION_DATA).getAsJsonArray();

                    int l = 0;
                    for (JsonElement versionElement : versions) {
                        logger.debug("Version: " + String.valueOf(l++));

                        JsonObject versionItem = versionElement.getAsJsonObject();
                        String versionValue = versionItem.get(VERSION_VALUE).getAsString();

                        Dependency dependency = new Dependency();
                        dependency.setGroupId(vendorName);
                        dependency.setArtifactId(productName);
                        dependency.setVersion(versionValue);

                        String fullName = dependency.getFullName();
                        if (dependencyMap.containsKey(fullName)) {
                            dependency.addBaseScore(dependencyMap.get(fullName).getAverageBaseScore());
                        }
                        dependencyMap.put(dependency.getFullName(), dependency);
                    }
                }
            }
        }

        return new LinkedList<Dependency>(dependencyMap.values());
    }

    /**
     * Retrieves vulnerability's base impact score using base metric CVSS V2
     * NOTE: Not sure if there are some elements missing V3 element.
     * @param impact JSONElement parent containing baseMetric and other score-related info as children
     * @return base score of vulnerability, -1 on null tree elements
     */
    private double getImpactBaseScore(JsonElement impact) {
        if (impact == null || impact.isJsonNull() || !impact.isJsonObject()) {
            throw new IllegalArgumentException("JSONElement Impact is null or not JSONObject.");
        }

        JsonElement baseMetricElement = impact.getAsJsonObject().get(BASE_METRIC_V2);
        if (baseMetricElement == null || baseMetricElement.isJsonNull() || !baseMetricElement.isJsonObject()) {
            return -1;
        }

        JsonElement CVSSElement = baseMetricElement.getAsJsonObject().get(CVSS_V2);
        if (CVSSElement == null || CVSSElement.isJsonNull() || !CVSSElement.isJsonObject()) {
            return -1;
        }

        JsonElement baseScoreElement = CVSSElement.getAsJsonObject().get(BASE_SCOREV2);
        if (baseScoreElement == null || baseScoreElement.isJsonNull()) {
            return -1;
        }

        return baseScoreElement.getAsJsonPrimitive().getAsDouble();

    }

}

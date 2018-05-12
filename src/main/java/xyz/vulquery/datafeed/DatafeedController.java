package xyz.vulquery.datafeed;

import com.google.gson.Gson;
import xyz.vulquery.dao.DependencyDAOIntf;
import xyz.vulquery.util.Utils;

//import com.google.gson.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class DatafeedController {

    private DependencyDAOIntf ddao;

    public static String getDependency(String groupId, String artifactId, String version) {
        if (Utils.isBlank(groupId)) {
            // throw exception
        }

        if (Utils.isBlank(artifactId)) {
            // throw exception
        }

        Dependency d = new Dependency();
        Gson gson = new Gson();

        //return gson.toJson(d);

        throw new NotImplementedException();


    }

    public static String getLastSyncDate() {
        throw new NotImplementedException();
    }

}

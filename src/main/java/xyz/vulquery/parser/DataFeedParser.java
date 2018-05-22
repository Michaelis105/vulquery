package xyz.vulquery.parser;

import xyz.vulquery.dependency.Dependency;
import java.util.List;

/**
 * Handles all conversion operations of downloaded dependency data feed sources.
 */
public interface DataFeedParser {

    List<Dependency> decode(String data);

}

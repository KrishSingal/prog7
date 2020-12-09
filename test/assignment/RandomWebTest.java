package assignment;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.util.concurrent.*;
import java.util.*;
import java.nio.file.Paths;

/**
 * Tests that the index generated during creation of the random web exactly
 * matches that built during external crawling
 */
public class RandomWebTest {
    WebQueryEngine wqe;

    @Test
    public void sampleWeb() throws IOException, ClassNotFoundException, InterruptedException {
        RandomWebGenerator test = new RandomWebGenerator();

        WebIndex index = test.generateWeb();

        String currPath  = Paths.get(".").toAbsolutePath().normalize().toString();
        System.out.println(currPath);

        // Crawl the random web network and extract the built index
        WebCrawler.main(new String[] {"file://localhost" + currPath + "/RandomWeb/RandomPage0.html"});

        wqe = WebQueryEngine.fromIndex((WebIndex) Index.load("index.db"));

        // Check to ensure every entry in the two indices are exactly the same
        String now;
        for (Map.Entry<String, HashMap<Page, HashSet<Integer>>> entry : index.invertedIndex.entrySet()){
            now = entry.getKey();
            for (Map.Entry<Page,HashSet<Integer>> second : entry.getValue().entrySet()){
                assertEquals(second.getValue(), wqe.index.invertedIndex.get(now).get(second.getKey()));
            }
        }
    }

}

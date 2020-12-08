package assignment;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.util.concurrent.*;
import java.util.*;
import java.nio.file.Paths;



public class RandomWebTest {
    WebQueryEngine wqe;

    @Test
    public void sampleWeb() throws IOException, ClassNotFoundException, InterruptedException {
        RandomWebGenerator test = new RandomWebGenerator();

        WebIndex index = test.generateWeb();


        //TimeUnit.SECONDS.sleep(15);
        WebCrawler.main(new String[] {"file://localhost/Users/Krish/Desktop/Data Structures + Algorithms (CS 314 H)/Assignment7/prog7/RandomWeb/RandomPage0.html"});

        wqe = WebQueryEngine.fromIndex((WebIndex) Index.load("index.db"));

        //System.out.println(index.invertedIndex + "\n" +  wqe.index.invertedIndex);

        //assertEquals(index.invertedIndex, wqe.index.invertedIndex);

        String now;
        for (Map.Entry<String, HashMap<Page, HashSet<Integer>>> entry : index.invertedIndex.entrySet()){
            now = entry.getKey();
            for (Map.Entry<Page,HashSet<Integer>> second : entry.getValue().entrySet()){
                assertEquals(second.getValue(), wqe.index.invertedIndex.get(now).get(second.getKey()));
            }
        }
    }

}

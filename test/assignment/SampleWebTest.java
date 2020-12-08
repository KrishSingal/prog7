package assignment;

import static org.junit.Assert.*;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.*;
import java.net.*;
import org.attoparser.simple.*;

public class SampleWebTest {

    @Test
    public void tests() throws IOException, ClassNotFoundException {
        WebCrawler.main(new String[] {"file://localhost/Users/Krish/Desktop/Data Structures + Algorithms (CS 314 H)/Assignment7/prog7/TestWeb/home.html"});

        WebQueryEngine wqe = WebQueryEngine.fromIndex((WebIndex) Index.load("index.db"));

        System.out.println(wqe.query("\"Here you can find all sorts of fun and interesting edge cases\""));
    }
}

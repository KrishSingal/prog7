package assignment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class bonk {
    public static void main(String args[]) throws MalformedURLException {
        HashSet<String> test = new HashSet<String>();
        test.add("krish");
        test.add("krish");
        System.out.println(test);


        HashSet<URL> visited = new HashSet<>();

        visited.add(new URL("https://www.javatpoint.com/java-threadpoolexecutor"));
        visited.add(new URL("https://www.javatpoint.com/java-threadpoolexecutor"));

        System.out.println(visited);
    }
}

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

        String sp = "H.i&| hE9ll^'o";
        System.out.println(sp.toLowerCase());

        double bonk = 0.000004958692;
        System.out.println(bonk);
        System.out.println(bonk*10000);
    }
}

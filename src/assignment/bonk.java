package assignment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.junit.BeforeClass;
import org.testng.annotations.Test;
import static org.junit.Assert.*;

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

        URL curr = new URL("file://localhost/Users/Krish/Desktop/tester.html");
        URL next = new URL(curr, "pages.html");
        URL qr = new URL(curr, "pages.html?name=networking#DOWNLOADING");
        System.out.println(next + " " + qr);
        System.out.println(next.equals(qr));
        System.out.println(next.hashCode() == qr.hashCode());
        System.out.println(next.getPath());
        assertEquals(4,5);
    }
}

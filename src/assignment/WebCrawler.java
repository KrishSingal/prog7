package assignment;

import java.io.*;
import java.net.*;
import java.util.*;

import org.attoparser.simple.*;
import org.attoparser.config.ParseConfiguration;

/**
 * The entry-point for WebCrawler; takes in a list of URLs to start crawling from and saves an index
 * to index.db.
 */
public class WebCrawler {

    /**
    * The WebCrawler's main method starts crawling a set of pages.  You can change this method as
    * you see fit, as long as it takes URLs as inputs and saves an Index at "index.db".
    */
    public static void main(String[] args) {
        // Basic usage information
        if (args.length == 0) {
            System.err.println("Error: No URLs specified.");
            System.exit(1);
        }

        // We'll throw all of the args into a queue for processing.
        Queue<URL> remaining = new LinkedList<>();
        for (String url : args) {
            try {
                remaining.add(new URL(url));
            } catch (MalformedURLException e) {
                // Throw this one out!
                System.err.printf("Error: URL '%s' was malformed and will be ignored!%n", url);
            }
        }

        // Create a parser from the attoparser library, and our handler for markup.
        ISimpleMarkupParser parser = new SimpleMarkupParser(ParseConfiguration.htmlConfiguration());
        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();

        int count =1;

        // Try to start crawling, adding new URLS as we see them.

        while (!remaining.isEmpty()) {

            // Try parsing next URL's page. If an error is found, the exception will be handled and the index generation continues
            try {
                // Parse the next URL's page
                URL curr = remaining.poll();
                handler.setCurrentPage(curr);
                parser.parse(new InputStreamReader(curr.openStream()), handler);

                ((WebIndex)handler.getIndex()).pages.add(new Page(curr));
                // Add any new URLs
                List<URL> newones = handler.newURLs();
                remaining.addAll(newones);
                count += newones.size();
            }
            // ignore any invalid URLs
            catch(Exception e){
                System.err.println("Error: Index generation failed!");
                e.printStackTrace();
                count--;
            }

        }

        // Save the generated index, any exceptions will be handled accordingly
        try {
            handler.getIndex().save("index.db");
        }
        catch(Exception e){
            System.err.println("Error: Index generation failed!");
            e.printStackTrace();
        }

        //System.out.println(count);
        //System.out.println(((WebIndex)handler.getIndex()).pages.size());
    }
}

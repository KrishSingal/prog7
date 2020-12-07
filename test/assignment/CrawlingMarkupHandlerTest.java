package assignment;

import static org.junit.Assert.*;
import org.junit.Test;

import static org.junit.Assert.*;
import java.util.*;
import java.net.*;
import org.attoparser.simple.*;

public class CrawlingMarkupHandlerTest {

    @Test
    public void OpenElementNewURLsTest () throws MalformedURLException {
        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();

        URL test = new URL("file://localhost/Users/Krish/Desktop/bogusWeb/testSource.html");

        handler.setCurrentPage(test);

        handler.handleDocumentStart(0, 0, 0);

        /*
        Case 1: basic href attribute
        */

        Map<String, String> attributes = new HashMap<>(){{
           put("href", "testFile1.html");
        }};
        handler.handleOpenElement("a", attributes, 0, 0);

        assertTrue(((WebIndex)handler.getIndex()).invertedIndex.keySet().isEmpty());
        assertTrue(handler.newURLs.contains(new URL("file://localhost/Users/Krish/Desktop/bogusWeb/testFile1.html")));
        assertEquals(handler.newURLs().size(), 1);
        assertFalse(handler.skip);
        handler.handleCloseElement("a", 0,0 );

        /*
        Case 2: addition of the 'title' attribute. No change in newURLs should be detected
         */
        attributes = new HashMap<>(){{
            put("href", "testFile2.html");
            put("title", "basic test");
        }};
        handler.handleOpenElement("a", attributes, 0, 0);

        assertTrue(((WebIndex)handler.getIndex()).invertedIndex.keySet().isEmpty());
        assertTrue(handler.newURLs.contains(new URL("file://localhost/Users/Krish/Desktop/bogusWeb/testFile2.html")));
        assertEquals(handler.newURLs().size(), 1);
        assertFalse(handler.skip);
        handler.handleCloseElement("a", 0,0 );

        /*
        Case 3: Case sensitivity of attribute names and element names
        */
        attributes = new HashMap<>(){{
            put("hrEf", "testFile3.html");
            put("title", "basic test");
        }};
        handler.handleOpenElement("A", attributes, 0, 0);

        assertTrue(((WebIndex)handler.getIndex()).invertedIndex.keySet().isEmpty());
        assertTrue(handler.newURLs.contains(new URL("file://localhost/Users/Krish/Desktop/bogusWeb/testFile3.html")));
        assertEquals(handler.newURLs().size(), 1);
        assertFalse(handler.skip);
        handler.handleCloseElement("A", 0,0 );

        /*
        Case 4: Repeated link found. Should not be added to newURLs
         */
        attributes = new HashMap<>(){{
            put("HREF", "testFile1.html");
        }};
        handler.handleOpenElement("a", attributes, 0, 0);

        assertTrue(((WebIndex)handler.getIndex()).invertedIndex.keySet().isEmpty());
        assertFalse(handler.newURLs.contains(new URL("file://localhost/Users/Krish/Desktop/bogusWeb/testFile1.html")));
        assertEquals(handler.newURLs().size(), 0);
        assertFalse(handler.skip);
        handler.handleCloseElement("a", 0,0 );

        /*
        Case 5: img tag. Ensure that 'pic.jpg' is not picked up as a URL
        */
        attributes = new HashMap<>(){{
            put("src", "images/pic.jpg");
        }};
        handler.handleOpenElement("img", attributes, 0, 0);

        assertTrue(((WebIndex)handler.getIndex()).invertedIndex.keySet().isEmpty());
        assertFalse(handler.newURLs.contains(new URL("file://localhost/Users/Krish/Desktop/bogusWeb/images/pic.jpg")));
        assertEquals(handler.newURLs().size(), 0);
        assertFalse(handler.skip);
        handler.handleCloseElement("img", 0,0 );

        /*
        Case 6: style tag. Ensure that it is skipped
        */
        attributes = new HashMap<>(){{

        }};
        handler.handleOpenElement("style", attributes, 0, 0);

        assertTrue(((WebIndex)handler.getIndex()).invertedIndex.keySet().isEmpty());
        assertTrue(handler.skip);
        assertEquals(handler.newURLs().size(), 0);
        handler.handleCloseElement("style", 0,0 );

        /*
        Case 7: script tag. Ensure that it is skipped
        */
        attributes = new HashMap<>(){{

        }};
        handler.handleOpenElement("script", attributes, 0, 0);

        assertTrue(((WebIndex)handler.getIndex()).invertedIndex.keySet().isEmpty());
        assertTrue(handler.skip);
        assertEquals(handler.newURLs().size(), 0);
        handler.handleCloseElement("script", 0,0 );

        /*
        Case 8: title tag
         */

        attributes = new HashMap<>(){{

        }};
        handler.handleOpenElement("title", attributes, 0, 0);

        assertTrue(((WebIndex)handler.getIndex()).invertedIndex.keySet().isEmpty());
        assertFalse(handler.skip);
        assertEquals(handler.newURLs().size(), 0);
        handler.handleCloseElement("title", 0,0 );

        /*
        Case 9: Nested anchor tags
         */

        attributes = new HashMap<>(){{
            put("href", "testFile4.html");
        }};
        handler.handleOpenElement("a", attributes, 0, 0);

        attributes = new HashMap<>(){{
            put("href", "testFile5.html");
        }};
        handler.handleOpenElement("a", attributes, 0, 0);

        assertTrue(((WebIndex)handler.getIndex()).invertedIndex.keySet().isEmpty());
        System.out.println(handler.newURLs);
        assertTrue(handler.newURLs.contains(new URL("file://localhost/Users/Krish/Desktop/bogusWeb/testFile4.html")));
        assertTrue(handler.newURLs.contains(new URL("file://localhost/Users/Krish/Desktop/bogusWeb/testFile5.html")));

        assertFalse(handler.skip);
        assertEquals(handler.newURLs().size(), 2);
        handler.handleCloseElement("a", 0,0 );
        handler.handleCloseElement("a", 0,0 );
    }

    @Test
    public void handleTextBasicTest() throws MalformedURLException {
        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();

        URL test = new URL("file://localhost/Users/Krish/Desktop/bogusWeb/testSource.html");

        handler.setCurrentPage(test);

        handler.handleDocumentStart(0, 0, 0);

        /*
        Tag 1: Basic Text. No repetition of words. No escape sequences
         */
        Map<String, String> attributes = new HashMap<>(){{
            put("href", "testFile1.html");
        }};
        handler.handleOpenElement("a", attributes, 0, 0);

        // Text: "WebCrawler is a fun assignment"

        char letters[] = "WebCrawler is a fun assignment".toCharArray();

        // Include partial word "WebCra"
        handler.handleText(letters, 0, 6, 0, 0);

        // Second part of word "wler" w/o the following space
        handler.handleText(letters, 6, 4, 0, 0);

        // Include the following space, complete word, and partial word " is a fun"
        handler.handleText(letters, 10, 9, 0,0 );

        // Just a space " "
        handler.handleText(letters, 19, 1, 0, 0);

        // "assign"
        handler.handleText(letters, 20, 6, 0, 0);

        // "ment"
        handler.handleText(letters, 26, 4, 0,0);

        handler.handleCloseElement("a", 0, 0);

        handler.handleDocumentEnd(0, 0 ,0, 0);

        // Check whether everything has been saved in index properly

        WebIndex saved = (WebIndex)handler.getIndex();

        HashMap<String, HashMap<Page, HashSet<Integer>>> map = saved.invertedIndex;

        // search for "WebCrawler" - case insensitivity
        assertTrue(map.get("webcrawler").keySet().contains(new Page(test)) );
        assertEquals(map.get("webcrawler").keySet().size(), 1 );
        assertTrue(map.get("webcrawler").get(new Page(test)).contains(0) );
        assertEquals(map.get("webcrawler").get(new Page(test)).size(), 1 );

        // search for "is"
        assertTrue(map.get("is").keySet().contains(new Page(test)) );
        assertEquals(map.get("is").keySet().size(), 1 );
        assertTrue(map.get("is").get(new Page(test)).contains(1) );
        assertEquals(map.get("is").get(new Page(test)).size(), 1 );

        // search for "a"
        assertTrue(map.get("a").keySet().contains(new Page(test)) );
        assertEquals(map.get("a").keySet().size(), 1 );
        assertTrue(map.get("a").get(new Page(test)).contains(2) );
        assertEquals(map.get("a").get(new Page(test)).size(), 1 );

        // search for "fun"
        assertTrue(map.get("fun").keySet().contains(new Page(test)) );
        assertEquals(map.get("fun").keySet().size(), 1 );
        assertTrue(map.get("fun").get(new Page(test)).contains(3) );
        assertEquals(map.get("fun").get(new Page(test)).size(), 1 );

        // search for "assignment"
        assertTrue(map.get("assignment").keySet().contains(new Page(test)) );
        assertEquals(map.get("assignment").keySet().size(), 1 );
        assertTrue(map.get("assignment").get(new Page(test)).contains(4) );
        assertEquals(map.get("assignment").get(new Page(test)).size(), 1 );

    }

    @Test
    public void handleTextEscapeSequencesTest() throws MalformedURLException {
        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();

        URL test = new URL("file://localhost/Users/Krish/Desktop/bogusWeb/testSource.html");

        handler.setCurrentPage(test);

        handler.handleDocumentStart(0, 0, 0);

        /*
        Tag 1: Text with escape sequences embedded
         */
        Map<String, String> attributes = new HashMap<>(){{

        }};
        handler.handleOpenElement("title", attributes, 0, 0);

        // Text: "WebCrawler is a fun assignment"

        char letters[] = "WebCraw\bler \t is a \"fu\rn\" assi\ngnment \f".toCharArray();

        // Include partial word "WebCra"
        handler.handleText(letters, 0, 6, 0, 0);

        // Second part of word "w\bler" w/o the following space
        handler.handleText(letters, 6, 5, 0, 0);

        // Include the following space, complete word, and partial word " \t is a \"fu\rn\""
        handler.handleText(letters, 11, 14, 0,0 );

        // Just a space " "
        handler.handleText(letters, 25, 1, 0, 0);

        // "assi\ngn"
        handler.handleText(letters, 26, 7, 0, 0);

        // "ment \f"
        handler.handleText(letters, 33, 6, 0,0);

        handler.handleCloseElement("title", 0, 0);

        // Check whether everything has been saved in index properly

        WebIndex saved = (WebIndex)handler.getIndex();

        HashMap<String, HashMap<Page, HashSet<Integer>>> map = saved.invertedIndex;

        // "WebCraw\bler \t is a \"fu\rn\" assi\ngnment \f"

        // search for "WebCraw\bler" - case insensitivity
        assertTrue(map.get("webcrawler").keySet().contains(new Page(test)) );
        assertEquals(map.get("webcrawler").keySet().size(), 1 );
        assertTrue(map.get("webcrawler").get(new Page(test)).contains(0) );
        assertEquals(map.get("webcrawler").get(new Page(test)).size(), 1 );

        // search for "is"
        assertTrue(map.get("is").keySet().contains(new Page(test)) );
        assertEquals(map.get("is").keySet().size(), 1 );
        assertTrue(map.get("is").get(new Page(test)).contains(1) );
        assertEquals(map.get("is").get(new Page(test)).size(), 1 );

        // search for "a"
        assertTrue(map.get("a").keySet().contains(new Page(test)) );
        assertEquals(map.get("a").keySet().size(), 1 );
        assertTrue(map.get("a").get(new Page(test)).contains(2) );
        assertEquals(map.get("a").get(new Page(test)).size(), 1 );

        // search for "fun"
        assertTrue(map.get("fun").keySet().contains(new Page(test)) );
        assertEquals(map.get("fun").keySet().size(), 1 );
        assertTrue(map.get("fun").get(new Page(test)).contains(3) );
        assertEquals(map.get("fun").get(new Page(test)).size(), 1 );

        // search for "assignment"
        assertTrue(map.get("assignment").keySet().contains(new Page(test)) );
        assertEquals(map.get("assignment").keySet().size(), 1 );
        assertTrue(map.get("assignment").get(new Page(test)).contains(4) );
        assertEquals(map.get("assignment").get(new Page(test)).size(), 1 );

        // ensure that escape sequences were not inserted into index
        assertNull(map.get("\b"));
        assertNull(map.get("\t"));
        assertNull(map.get("\r"));
        assertNull(map.get("\n"));
        assertNull(map.get("\f"));
        assertNull(map.get("\""));

    }

    @Test
    public void handleTextRepeatsTest() throws MalformedURLException {
        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();

        URL test = new URL("file://localhost/Users/Krish/Desktop/bogusWeb/testSource.html");

        handler.setCurrentPage(test);

        handler.handleDocumentStart(0, 0, 0);

        /*
        Tag 1: Text with escape sequences embedded
         */
        Map<String, String> attributes = new HashMap<>(){{

        }};
        handler.handleOpenElement("title", attributes, 0, 0);

        // Text: "WebCrawler is a fun assignment"

        char letters[] = "WebCraw\bler \t is is a \"fu\rn\" fun assi\ngnment \f".toCharArray();

        // Include partial word "WebCra"
        handler.handleText(letters, 0, 6, 0, 0);

        // Second part of word "w\bler" w/o the following space
        handler.handleText(letters, 6, 5, 0, 0);

        // Include the following space, complete word, and partial word " \t is is a \"fu\rn\""
        handler.handleText(letters, 11, 17, 0,0 );

        // Just a space " "
        handler.handleText(letters, 28, 1, 0, 0);

        // "fun"
        handler.handleText(letters, 29, 3, 0, 0);

        // "assi\ngn"
        handler.handleText(letters, 32, 7, 0, 0);

        // "ment \f"
        handler.handleText(letters, 39, 6, 0,0);

        handler.handleCloseElement("title", 0, 0);

        // Check whether everything has been saved in index properly

        WebIndex saved = (WebIndex)handler.getIndex();

        HashMap<String, HashMap<Page, HashSet<Integer>>> map = saved.invertedIndex;

        // "WebCraw\bler \t is is a \"fu\rn\" fun assi\ngnment \f"

        // search for "WebCraw\bler" - case insensitivity
        assertTrue(map.get("webcrawler").keySet().contains(new Page(test)) );
        assertEquals(map.get("webcrawler").keySet().size(), 1 );
        assertTrue(map.get("webcrawler").get(new Page(test)).contains(0) );
        assertEquals(map.get("webcrawler").get(new Page(test)).size(), 1 );

        // search for "is"
        assertTrue(map.get("is").keySet().contains(new Page(test)) );
        assertEquals(map.get("is").keySet().size(), 1 );
        assertTrue(map.get("is").get(new Page(test)).contains(1) );
        assertTrue(map.get("is").get(new Page(test)).contains(2) );
        assertEquals(map.get("is").get(new Page(test)).size(), 2 );

        // search for "a"
        assertTrue(map.get("a").keySet().contains(new Page(test)) );
        assertEquals(map.get("a").keySet().size(), 1 );
        assertTrue(map.get("a").get(new Page(test)).contains(3) );
        assertEquals(map.get("a").get(new Page(test)).size(), 1 );

        // search for "fun"
        assertTrue(map.get("fun").keySet().contains(new Page(test)) );
        assertEquals(map.get("fun").keySet().size(), 1 );
        assertTrue(map.get("fun").get(new Page(test)).contains(4) );
        assertTrue(map.get("fun").get(new Page(test)).contains(5) );
        assertEquals(map.get("fun").get(new Page(test)).size(), 2 );

        // search for "assignment"
        assertTrue(map.get("assignment").keySet().contains(new Page(test)) );
        assertEquals(map.get("assignment").keySet().size(), 1 );
        assertTrue(map.get("assignment").get(new Page(test)).contains(6) );
        assertEquals(map.get("assignment").get(new Page(test)).size(), 1 );

        // ensure that escape sequences were not inserted into index
        assertNull(map.get("\b"));
        assertNull(map.get("\t"));
        assertNull(map.get("\r"));
        assertNull(map.get("\n"));
        assertNull(map.get("\f"));
        assertNull(map.get("\""));

    }

    @Test
    public void handleTextRandomizedTest () throws MalformedURLException {
        String toInsert = "";
        int length = (int)(Math.random()*50);

        char viable[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '\n', '\t', '\r', '\f', ' ', '\"'};
        int numQuotes=0;
        String currWord ="";
        ArrayList<String> tokens = new ArrayList<>();
        char chars[] = new char[length];

        for(int i =0; i< length; i++){
            char rand = viable[(int)(Math.random()*viable.length)];
            if(i == length-1 && numQuotes %2 ==1){
                rand = '\"';
            }

            toInsert+= rand;
            if(rand == '\"'){
                numQuotes++;
            }
            chars[i] = rand;

            //tokenize
            if(Character.isLetterOrDigit(rand)){
                currWord += rand;
            }
            if(rand == ' '){
                tokens.add(currWord);
            }
        }

        // insertion using handleText()
        int index =0;

        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();

        URL test = new URL("file://localhost/Users/Krish/Desktop/bogusWeb/testSource.html");

        handler.setCurrentPage(test);

        handler.handleDocumentStart(0, 0, 0);

        Map<String, String> attributes = new HashMap<>(){{

        }};
        handler.handleOpenElement("title", attributes, 0, 0);

        while(index < toInsert.length()){
            int rand = (int)(Math.random()*10);

            int l = index+rand;
            if(l>= toInsert.length()){
                handler.handleText(chars, index, toInsert.length()-index, 0, 0);
                break;
            }
            handler.handleText(chars, index, rand, 0, 0);
            index = l;
        }

        handler.handleCloseElement("title", 0, 0);

        // Check that every word is accounted for

        WebIndex saved = (WebIndex)handler.getIndex();

        HashMap<String, HashMap<Page, HashSet<Integer>>> map = saved.invertedIndex;

        for(int i=0; i< tokens.size(); i++){
            String word = tokens.get(i);

            assertTrue(map.get(word).keySet().contains(new Page(test)));
            assertTrue(map.get(word).get(new Page(test)).contains(i));
        }

    }
}
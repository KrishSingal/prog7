package assignment;

import java.util.*;
import java.net.*;
import org.attoparser.simple.*;

/**
 * A markup handler which is called by the Attoparser markup parser as it parses the input;
 * responsible for building the actual web index.
 *
 * TODO: Implement this!
 */
public class CrawlingMarkupHandler extends AbstractSimpleMarkupHandler {

    int loc; // Stores the current location on the page being parsed
    Set<String> visited; // Cache of all visited URLs used in BFS over web graph
    Set<URL> newURLs; // Cache of new URLs linked on the current page
    WebIndex index; // Reference to generated index
    Page current; // Reference to current page being parsed
    String currWord; // Current word (excluding special characters) being parsed, can be unfinished due to inconsistent text parsing
    //String currSpecialWord; // Current word (including special characters) being parsed, can be unfinished due to inconsistent text parsing
    int pageCount; // Tracks total number of pages

    HashSet<String> skipTags = new HashSet<>() {{
        add("style");
        add("script");
    }};

    HashSet<Character> delimiters = new HashSet<>(){{
        add(',');
        add('.');
        add('?');
        add('!');
        add(' ');
    }};

    boolean skip;

    URL baseUrl; // Stores absolute parts of the current URL; Used to complete any relative URLs

    HashSet<String> ElementTypes;

    public CrawlingMarkupHandler() {

        // Initialization of all class variables
        loc =0;
        visited = new HashSet<String>();
        newURLs = new HashSet<URL>();
        index = new WebIndex();
        current = null;
        pageCount =0;
        baseUrl = null;
        currWord = "";
        //currSpecialWord = "";
        ElementTypes = new HashSet<>();
        skip = false;

    }

    /**
    * This method returns the complete index that has been crawled thus far when called.
    */
    public Index getIndex() {
        // TODO: Implement this!
        return index;
    }

    /**
     * Sets the current page being parsed
     * @param curr   The URL of the current page being parsed
     * @throws MalformedURLException
     */
    public void setCurrentPage(URL curr) throws MalformedURLException {
        // Instantiate new page with current URL and page number
        current = new Page(curr);
        current.setPageNum(pageCount++);

        //index.pages.add(current);

        //System.out.println(curr);

        // Extract absolute base of current URL for any relative URLs found during parsing
        baseUrl = new URL(curr.getProtocol() + "://" + curr.getHost() + curr.getFile());

        // Mark current page as visited to ensure no infinite looping occurs
        visited.add(curr.getPath());
    }

    /**
    * This method returns any new URLs found to the Crawler; upon being called, the set of new URLs
    * should be cleared.
    */
    public List<URL> newURLs() {
        // Transfer new URLs into list
        List<URL> ret = new ArrayList<URL>();
        for(URL i : newURLs){
            ret.add(i);
        }

        // Mark all the new URLs as visited and clear the newURLs cache
        newURLs.clear();

        return ret;
    }

    /**
    * These are some of the methods from AbstractSimpleMarkupHandler.
    * All of its method implementations are NoOps, so we've added some things
    * to do; please remove all the extra printing before you turn in your code.
    *
    * Note: each of these methods defines a line and col param, but you probably
    * don't need those values. You can look at the documentation for the
    * superclass to see all of the handler methods.
    */

    /**
    * Called when the parser first starts reading a document.
    * @param startTimeNanos  the current time (in nanoseconds) when parsing starts
    * @param line            the line of the document where parsing starts
    * @param col             the column of the document where parsing starts
    */
    public void handleDocumentStart(long startTimeNanos, int line, int col) {
        // Reset location to top of page
        loc = 0;
    }

    /**
    * Called when the parser finishes reading a document.
    * @param endTimeNanos    the current time (in nanoseconds) when parsing ends
    * @param totalTimeNanos  the difference between current times at the start
    *                        and end of parsing
    * @param line            the line of the document where parsing ends
    * @param col             the column of the document where the parsing ends
    */
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) {

        // Insert any leftover words from the unclosed final tag
        if(currWord != null && !currWord.equals("")) {
            index.insert(currWord, current, loc);

            loc++;
            currWord = "";

        }
    }

    /**
    * Called at the start of any tag.
    * @param elementName the element name (such as "div")
    * @param attributes  the element attributes map, or null if it has no attributes
    * @param line        the line in the document where this elements appears
    * @param col         the column in the document where this element appears
    */
    public void handleOpenElement(String elementName, Map<String, String> attributes, int line, int col) {

        // Skip over any tags that don't produce text on the page
        if(skipTags.contains(elementName.toLowerCase())){
            skip = true;
        }
        else
            ElementTypes.add(elementName);

        // Insert any leftover words from the unclosed previous tag
        if(currWord != null && !currWord.equals("")) {
            index.insert(currWord, current, loc);

            loc++;
            currWord = "";
        }

        // Locate any new linked URLs and add them to the newURLs cache

        URL found;
        // Only consider anchor tags
        if(attributes == null || !elementName.toLowerCase().equals("a")) {
            return;
        }

        // Find all 'href' attributes and add all linked URLs that we haven't seen before
        for (Map.Entry<String,String> entry : attributes.entrySet()) {
            if(entry.getKey().toLowerCase() .equals ("href")){
                try {
                    // Build absolute URL from base URL and relative URL

                    found = new URL(baseUrl, entry.getValue());

                    // If it's an '.html' file and hasn't been visited yet, we add it into the newURLs cache
                    if(!visited.contains(found.getPath()) &&
                            (found.getPath().substring(found.getPath().length()-5).equals(".html")
                                    || found.getPath().substring(found.getPath().length()-4).equals(".htm"))){
                        newURLs.add(found);
                        visited.add(found.getPath());
                    }
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    /**
    * Called at the end of any tag.
    * @param elementName the element name (such as "div").
    * @param line        the line in the document where this elements appears.
    * @param col         the column in the document where this element appears.
    */
    public void handleCloseElement(String elementName, int line, int col) {
        // TODO: Implement this.

        // Insert last word from tag
        if(currWord != null && !currWord.equals("")) {
            index.insert(currWord, current, loc);


            loc++;
            currWord = "";
        }

        skip = false; // Reset the skip flag
    }

    /**
    * Called whenever characters are found inside a tag. Note that the parser is not
    * required to return all characters in the tag in a single chunk. Whitespace is
    * also returned as characters.
    * @param ch      buffer containing characters; do not modify this buffer
    * @param start   location of 1st character in ch
    * @param length  number of characters in ch
    */
    public void handleText(char ch[], int start, int length, int line, int col) {

        for(int i = start; i < start + length; i++) {
            // Instead of printing raw whitespace, we're escaping it
            switch(ch[i]) {
                case '\\':
                    break;
                case '"':
                    break;
                case '\n':
                    break;
                case '\r':
                    break;
                case '\t':
                    break;
                default:
                    if(skip){
                        break;
                    }
                    // Add lowercased letters to the words
                    if(Character.isLetter(ch[i])){
                        currWord += Character.toLowerCase(ch[i]);
                    }
                    // Add numbers to the words
                    else if (Character.isDigit(ch[i])){
                        currWord += ch[i];
                    }
                    // When a delimiter is encountered, insert both words into index
                    // and increment location
                    else if ((delimiters.contains(ch[i])) && !currWord.equals("")){
                        index.insert(currWord, current, loc);

                        loc++;
                        currWord= "";
                    }
                    break;
            }
        }

    }
}

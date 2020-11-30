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

    int loc;
    Set<URL> visited;
    Set<URL> newURLs;
    WebIndex index;
    Page current;
    String currWord;
    String currSpecialWord;
    int pageCount;
    boolean title;
    String titleName;

    HashSet<String> skipTags = new HashSet<>() {{
        add("style");
        add("font");
    }};
    boolean skip;

    URL baseUrl;

    HashSet<String> ElementTypes;

    public CrawlingMarkupHandler() {

        loc =0;
        visited = new HashSet<URL>();
        newURLs = new HashSet<URL>();
        index = new WebIndex();
        current = null;
        pageCount =0;
        baseUrl = null;
        currWord = "";
        currSpecialWord = "";

        title = false;

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

    public void setCurrentPage(URL curr) throws MalformedURLException {
        current = new Page(curr);
        current.setPageNum(pageCount++);

        System.out.println(curr);

        baseUrl = new URL(curr.getProtocol() + "://" + curr.getHost() + curr.getFile());
        visited.add(curr);
    }

    /**
    * This method returns any new URLs found to the Crawler; upon being called, the set of new URLs
    * should be cleared.
    */
    public List<URL> newURLs() {
        // TODO: Implement this!
        List<URL> ret = new ArrayList<URL>();
        for(URL i : newURLs){
            ret.add(i);
        }

        visited.addAll(newURLs);

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
        // TODO: Implement this.
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
        // TODO: Implement this.
        //System.out.println("index: " + index);
        System.out.println("new URLs: " + newURLs);
    }

    /**
    * Called at the start of any tag.
    * @param elementName the element name (such as "div")
    * @param attributes  the element attributes map, or null if it has no attributes
    * @param line        the line in the document where this elements appears
    * @param col         the column in the document where this element appears
    */
    public void handleOpenElement(String elementName, Map<String, String> attributes, int line, int col) {
        // TODO: Implement this.



        if(skipTags.contains(elementName.toLowerCase())){
            skip = true;
        }
        else
            ElementTypes.add(elementName);

        if(currWord != null && !currWord.equals("")) {
            index.insert(currWord, current, loc);

            if(!currWord.equals(currSpecialWord)){
                index.insert(currSpecialWord, current, loc);
            }
            loc++;
            currWord = "";
            currSpecialWord = "";

        }

        if(elementName.equals("title")){
            title = true;
        }

        URL found;
        if(attributes == null || !elementName.toLowerCase().equals("a")) {
            return;
        }

        for (Map.Entry<String,String> entry : attributes.entrySet()) {
            if(entry.getKey().toLowerCase() .equals ("href")){
                try {
                    if(entry.getValue().indexOf("://") < 0){
                        found = new URL(baseUrl, entry.getValue());
                    }
                    else{
                        found = new URL (entry.getValue());
                    }

                    if(!visited.contains(found) && entry.getValue().indexOf(".html") >= 0){
                        newURLs.add(found);
                    }
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }
        }


        //System.out.println(attributes);

    }

    /**
    * Called at the end of any tag.
    * @param elementName the element name (such as "div").
    * @param line        the line in the document where this elements appears.
    * @param col         the column in the document where this element appears.
    */
    public void handleCloseElement(String elementName, int line, int col) {
        // TODO: Implement this.

        if(title){
            titleName += currSpecialWord;
            current.setTitle(titleName);
            title = false;
            titleName="";
        }

        if(currWord != null && !currWord.equals("")) {
            index.insert(currWord, current, loc);

            if(!currWord.equals(currSpecialWord)){
                index.insert(currSpecialWord, current, loc);
            }
            loc++;
            currWord = "";
            currSpecialWord = "";
        }

        skip = false;
    }

    /**
    * Called whenever characters are found inside a tag. Note that the parser is not
    * required to return all characters in the tag in a single chunk. Whitespace is
    * also returned as characters.
    * @param ch      buffer containint characters; do not modify this buffer
    * @param start   location of 1st character in ch
    * @param length  number of characters in ch
    */
    public void handleText(char ch[], int start, int length, int line, int col) {
        // TODO: Implement this.
        //System.out.print("Characters:    \"");

        for(int i = start; i < start + length; i++) {
            // Instead of printing raw whitespace, we're escaping it
            switch(ch[i]) {
                case '\\':
                    //System.out.print("\\\\");
                    break;
                case '"':
                    //System.out.print("\\\"");
                    break;
                case '\n':
                    //System.out.print("\\n");;
                case '\r':
                    //System.out.print("\\r");
                    break;
                case '\t':
                    //System.out.print("\\t");
                    break;
                default:
                    if(skip){
                        break;
                    }
                    if(Character.isLetter(ch[i])){
                        currWord += Character.toLowerCase(ch[i]);
                        currSpecialWord += Character.toLowerCase(ch[i]);
                    }
                    else if (Character.isDigit(ch[i])){
                        currWord += ch[i];
                        currSpecialWord += ch[i];
                    }
                    else if (ch[i] != ' '){
                        currSpecialWord += ch[i];
                    }
                    else if (ch[i] == ' ' && !currWord.equals("")){
                        index.insert(currWord, current, loc);

                        //System.out.println(currWord + " " + currSpecialWord);

                        if(!currWord.equals(currSpecialWord)){
                            index.insert(currSpecialWord, current, loc);
                        }

                        if(title){
                            titleName += currSpecialWord + " ";
                        }

                        loc++;
                        currWord= "";
                        currSpecialWord = "";
                    }
                    break;
            }
        }

    }
}

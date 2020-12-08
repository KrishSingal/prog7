package assignment;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.net.*;

/**
 * Class generates a random web used for testing on large inputs
 */
public class RandomWebGenerator {
    WebIndex key;
    int numPages;
    PrintWriter cp;
    Page current;
    String currFile;
    String baseURL;
    int currentPageNumber;

    String tags[] = {"a", "img", "h1", "h2", "h3", "p", "b"};

    char viable[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '\n', '\t', '\r', '\"'};

    public RandomWebGenerator(){
        // Instantiation of variables

        key = new WebIndex();
        numPages =0;
        key = new WebIndex();
        current = null;

        // Sets base path for the random web

        String currPath  = Paths.get(".").toAbsolutePath().normalize().toString();

        baseURL = "file://localhost" + currPath + "/RandomWeb";
    }

    public WebIndex generateWeb() throws IOException {


        numPages =10;


        for(int i =0; i< numPages; i++){
            currFile = baseURL + "/RandomPage"+i+".html";

            // Build page i
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(currFile.substring(16))));
            out.println("<html>");
            current = new Page(new URL(currFile));
            currentPageNumber = i;
            generateBody(out);
            out.print("</html>");

            out.close();
        }

        return key;
    }

    /**
     * Randomly generates the body of the html page
     * @param out       printwriter used to write to the page
     */
    public void generateBody(PrintWriter out){
        int numTags = (int)(Math.random()*30) +1;
        //int numTags =10;
        int loc = 0;

        // Create title and insert it into the index
        String title = generateWord();
        out.println("<title>" + title + "</title>");
        if(!clean(title).equals("") && clean(title).matches("^.*[a-z0-9]+.*$")) {
            key.insert(clean(title), current, 0);
            loc++;
        }

        // Link current page to the next via an anchor tag and 'href'
        // generate text to put inside the tag, and insert into index
        if(currentPageNumber<numPages-1) {
            String reference = generateWord();
            int nextPage = currentPageNumber + 1;
            out.println("<a href =" + "RandomPage" + nextPage + ".html>" + reference + "</a>");
            if(!clean(reference).equals("")&& clean(reference).matches("^.*[a-z0-9]+.*$")) {
                key.insert(clean(reference), current, 1);
                loc++;
            }
        }

        // randomly choose more tags to place in the body
        // randomly generate text to put inside each tag and insert it into the index
        for(int i=0; i< numTags; i ++){
            String tag = tags[(int)(Math.random()*tags.length)];
            out.println("<" + tag + ">");


            int numWords = (int)(Math.random()*10) + 1;
            for(int j=0; j< numWords; j++){
                String word = generateWord();
                if(!clean(word).equals("")&& clean(word).matches("^.*[a-z0-9]+.*$")) {
                    key.insert(clean(word), current, loc++);
                }
                out.print(word + " ");
            }
            out.println("</" + tag + ">");
        }


    }

    /**
     * Randomly generates a word based on the viable characters
     * @return      Randomly generated word
     */
    public String generateWord(){
        int length = (int)(Math.random()*5)+1;
        String word = "";

        for(int i=0; i< length; i++){
            int rand = (int)(Math.random()*viable.length);
            word += viable[rand];

        }

        return word;
    }

    /**
     * Filters the randomly generated word to be lower case and only contain letters and digits so that
     * they can be inserted into the index
     * @param word  word to be cleaned
     * @return      cleaned word
     */
    public String clean(String word){
        String ret="";

        for(int i=0; i< word.length(); i++){
            char now = word.charAt(i);
            if(Character.isLetterOrDigit(now)){
                ret+= now;
            }
        }
        return ret.toLowerCase();
    }
}

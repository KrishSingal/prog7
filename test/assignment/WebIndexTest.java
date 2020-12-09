package assignment;

import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Tests the functionality of the WebIndex methods by randomizing an index and querying all the
 * words and possible phrases
 */
public class WebIndexTest {
    static char viable[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    static WebIndex index = new WebIndex();

    static ArrayList<ArrayList<String>> pages; // Stores the sequence of words on each randomized page
    static String baseURL;

    @BeforeClass
    public static void setUpIndex() throws MalformedURLException {
        int numPages = (int)(Math.random()*10);

        pages = new ArrayList<ArrayList<String>>();
        for(int i=0; i< numPages; i++){
            pages.add(new ArrayList<String>());
        }

        String currPath  = Paths.get(".").toAbsolutePath().normalize().toString();

        baseURL = "file://localhost" + currPath;

        // Randomize the words on each page and build and index out of this randomization
        for(int i =0; i< numPages; i++) {
            int numWords = (int) (Math.random() * 10);
            for (int j = 0; j < numWords; j++) {
                String word = generateWord();
                pages.get(i).add(word);
                index.insert(word, new Page(new URL(baseURL + "RandomPage" + i + ".html")), j);
            }
        }
    }

    /**
     * Tests the insert() function by asserting that all every word is accounted for in the index
     * @throws MalformedURLException
     */
    @Test
    public void insertTest() throws MalformedURLException {
        // for every word, assert that the index contains the correct page and position for it

        for(int i=0; i< pages.size(); i++){
            for(int j=0; j< pages.get(i).size(); j++){
                String now = pages.get(i).get(j);
                Page rn = new Page(new URL(baseURL + "RandomPage" + i + ".html"));
                assertTrue(index.invertedIndex.get(now).get(rn).contains(j));
            }
        }

    }

    /**
     * Tests the query() function by asserting that the result of pages return for word
     * and phrase queries matches with the original web definition
     */
    @Test
    public void queryTest(){
        // Test word queries

        // for every word, assert that the returned set actually contains the word
        for(int i=0; i< pages.size(); i++) {
            for (int j = 0; j < pages.get(i).size(); j++) {
                String now = pages.get(i).get(j);
                assertTrue(checkCorrectWord((Set<Page>)index.query(now), now));
            }
        }

        // Test phrase queries

        // for every possible phrase, assert that the returned set actually contains the phrase
        for(int i=0; i< pages.size(); i++) {
            for (int j = 0; j < pages.get(i).size(); j++) {
                String now = pages.get(i).get(j);
                for(int k=j+1; k<pages.get(i).size(); k++){
                    now+= pages.get(i).get(k);
                }

                assertTrue(checkCorrectPhrase((Set<Page>)index.query(now), now));

            }
        }

    }

    /**
     * abstracted method that determines whether the specified word is
     * truly in each of the result set of pages
     * @param result       pages that are claimed to have instances of word
     * @param word         Specified word
     * @return             boolean indicating whether the pages truly contain word
     */
    public static boolean checkCorrectWord(Set<Page> result, String word){
        boolean correct = true;

        // for each page in the set, check that the original page definition contains the word
        for(Page i: result){
            URL curr = i.getURL();
            String path = curr.getPath();
            int num = Integer.parseInt(path.substring(path.indexOf(".")-1, path.indexOf(".")));

            if(!pages.get(num).contains(word)){
                correct = false;
            }

        }
        return correct;
    }

    /**
     * abstracted method that determines whether the specified phrase is
     * truly in each of the result set of pages
     * @param result       pages that are claimed to have instances of phrase
     * @param phrase       Specified phrase
     * @return             boolean indicating whether the pages truly contain phrase
     */
    public static boolean checkCorrectPhrase(Set<Page> result, String phrase){

        // Build list of the tokens in the phrase
        // this will be used as a sublist to search for in each page
        String phraseTokens[] = phrase.split(" ");
        ArrayList<String> sublist = new ArrayList<String>();

        for( int i=0; i< phraseTokens.length; i++){
            sublist.add(phraseTokens[i]);
        }

        boolean correct = true;

        // for each page in the set, check that the original page definition contains the phrase
        for(Page i: result){
            URL curr = i.getURL();
            String path = curr.getPath();
            int num = Integer.parseInt(path.substring(path.indexOf(".")-1, path.indexOf(".")));

            if(Collections.indexOfSubList(pages.get(num), sublist) < 0){
                correct = false;
            }

        }
        return correct;
    }

    /**
     * Randomly generates a word based on the viable characters
     * @return      Randomly generated word
     */
    public static String generateWord(){
        int length = (int)(Math.random()*5)+1;
        String word = "";

        for(int i=0; i< length; i++){
            int rand = (int)(Math.random()*viable.length);
            word += viable[rand];

        }

        return word;
    }
}
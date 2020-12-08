package assignment;

import java.io.*;
import java.util.*;

/**
 * A web-index which efficiently stores information about pages. Serialization is done automatically
 * via the superclass "Index" and Java's Serializable interface.
 *
 * TODO: Implement this!
 */
public class WebIndex extends Index {
    /**
     * Needed for Serialization (provided by Index) - don't remove this!
     */
    private static final long serialVersionUID = 1L;

    // TODO: Implement all of this! You may choose your own data structures an internal APIs.
    // You should not need to worry about serialization (just make any other data structures you use
    // here also serializable - the Java standard library data structures already are, for example).

    // Inverted index that stores a mapping from any given word to all the pages where it can be found, and each location on the page
    HashMap<String, HashMap<Page, HashSet<Integer>>> invertedIndex = new HashMap<String, HashMap<Page, HashSet<Integer>>>();
    HashSet<Page> pages = new HashSet<Page>(); // Cache of all pages in the web

    /**
     * Performs insertion of a word on a specific page and location into the inverted index cache
     * @param key   Word to cache
     * @param p     Page it is found on
     * @param spot  Location on the page
     */
    public void insert(String key, Page p, int spot){
        //pages.add(p); // Add page to cache


        HashMap<Page, HashSet<Integer>> curr = invertedIndex.get(key);

        // If we have never seen this word before, create a new map entry for it
        // including this page and location
        if(curr == null){
            invertedIndex.put(key, new HashMap<Page, HashSet<Integer>>(){{
                put(p, new HashSet<Integer>(){{
                    add(spot);
                }});
            }});
        }

        // We have seen the word before, so add to an existing cache
        else{
            HashSet<Integer> locations = curr.get(p);

            // If we have never seen this word on this specific page before, create a new entry for it
            if(locations == null){
                curr.put(p, new HashSet<Integer>(){{
                    add(spot);
                }});
            }
            // Otherwise, add to the existing cache of this word on this page
            else{
                locations.add(spot);
            }
        }
    }

    /**
     * Abstraction of the query function that returns a set of pages where the query can be found
     * @param query     The specified request
     * @return          Set of pages where the query can be found
     */
    public Collection<Page> query(String query){
        // Divide the query into two cases which will be handled differently
        // Word queries or phrase queries

        String[] split = query.split(" ");
        if(split.length ==1){
            return wordQuery(query);
        }
        else
            return phraseQuery(split);
    }

    /**
     * Finds all pages where the given word can be found
     * @param word      The specified word query
     * @return          Set of Pages where the word can be found
     */
    public Collection<Page> wordQuery(String word) {
        HashMap<Page, HashSet<Integer>> curr = invertedIndex.get(word);

        // If we have never seen this word before, return an empty set
        if(curr == null){
            return new HashSet<Page> ();
        }

        // We have seen it before, so return the set
        return curr.keySet();
    }

    /**
     * Finds all the pages where the given phrase can be found
     * @param words     The specified phrase query
     * @return          Set of Pages where the phrase can be found
     */
    public Collection<Page> phraseQuery(String words[]) {

        HashMap<Page, HashSet<Integer>> first = invertedIndex.get(words[0]);

        // If we have never seen the first word, the phrase cannot be present
        // Return an empty set
        if(first == null){
            return new HashSet<Page> ();
        }

        Set<Page> retained = first.keySet(); // Keeps track of the still viable pages
        //System.out.println(retained);

        // For each successive word, narrow down viable pages based on whether the i'th word exists in the (origin + i)th location
        // for all 'origins'
        // Origin - Position of the first word on a page
        for(int i =1; i< words.length; i++){
            HashMap<Page, HashSet<Integer>> next = invertedIndex.get(words[i]);

            // If we have never seen word i, the phrase cannot be present
            // Return an empty set
            if(next == null){
                return new HashSet<Page>();
            }

            HashSet<Page> temp = new HashSet<>(); // Keeps track of viable pages temporarily for each word iteration

            // For all still viable pages, get origin locations and check whether the i'th word exists in the (origin + i)th location
            for(Page p: retained){
                HashSet<Integer> originLocs = first.get(p);
                HashSet<Integer> nextLocs = next.get(p);

                // If word i is not present on this page, the phrase cannot be present here
                // move to next page
                if(nextLocs == null){
                    continue;
                }

                // Word i is present on this page, check to see whether there are any instances where it
                // falls in line with the phrase ordering
                for(int loc1: originLocs){
                    if(nextLocs.contains(loc1+i)){
                        temp.add(p);
                        break;
                    }
                }

            }

            retained = temp; // update retained pages cache to narrow search of pages for next iteration

        }

        return retained;
    }

    /**
     * Dictates how the index should behave when printed
     * @return  String representation of index
     */
    public String toString (){
        return "" + invertedIndex + "\n" +  pages;
    }

}
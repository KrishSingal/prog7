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

    HashMap<String, HashMap<Page, HashSet<Integer>>> invertedIndex = new HashMap<String, HashMap<Page, HashSet<Integer>>>();
    HashSet<Page> pages = new HashSet<Page>();

    public void insert(String key, Page p, int spot){
        pages.add(p);

        HashMap<Page, HashSet<Integer>> curr = invertedIndex.get(key);
        if(curr == null){
            invertedIndex.put(key, new HashMap<Page, HashSet<Integer>>(){{
                put(p, new HashSet<Integer>(){{
                    add(spot);
                }});
            }});
        }
        else{
            HashSet<Integer> locations = curr.get(p);

            if(locations == null){
                curr.put(p, new HashSet<Integer>(){{
                    add(spot);
                }});
            }
            else{
                locations.add(spot);
            }
        }
    }

    public Collection<Page> query(String query){
        String[] split = query.split(" ");
        if(split.length ==1){
            return wordQuery(query);
        }
        else
            return phraseQuery(split);
    }

    public Collection<Page> wordQuery(String word) {
        HashMap<Page, HashSet<Integer>> curr = invertedIndex.get(word);

        if(curr == null){
            return new HashSet<Page> ();
        }

        return curr.keySet();
    }

    public Collection<Page> phraseQuery(String words[]) {
        //System.out.println(words);
        HashMap<Page, HashSet<Integer>> first = invertedIndex.get(words[0]);
        if(first == null){
            return new HashSet<Page> ();
        }

        Set<Page> retained = first.keySet();
        System.out.println(retained);
        //System.out.println("Toronto: " + invertedIndex.get("Toronto"));
        //System.out.println("banker: " + invertedIndex.get("banker"));

        for(int i =1; i< words.length; i++){
            HashMap<Page, HashSet<Integer>> next = invertedIndex.get(words[i]);

            if(next == null){
                return new HashSet<Page>();
            }

            HashSet<Page> temp = new HashSet<>();

            for(Page p: retained){
                HashSet<Integer> currLocs = first.get(p);
                HashSet<Integer> nextLocs = next.get(p);

                if(nextLocs == null){
                    continue;
                }

                for(int loc1: currLocs){
                    if(nextLocs.contains(loc1+i)){
                        temp.add(p);
                        break;
                    }
                }

            }

            retained = temp;

        }

        return retained;
    }

    public String toString (){
        return "" + invertedIndex + "\n" +  pages;
    }

}
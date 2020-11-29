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

    Map<String, HashSet<location>> invertedIndex = new HashMap<>();
    HashSet<Page> pages = new HashSet<Page>();

    public void insert(String key, Page p, int spot){
        pages.add(p);

        HashSet<location> curr = invertedIndex.get(key);
        if(curr == null){
            invertedIndex.put(key, new HashSet<location>(){{
                add(new location(p, spot));
            }});
        }
        else{
            curr.add(new location(p, spot));
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

    public Collection<Page> wordQuery(String query) {
        HashSet<location> curr = invertedIndex.get(query);

        if(curr == null){
            return new HashSet<Page> ();
        }

        Set<Page> results = new HashSet<Page>();

        for(location i: curr){
            results.add(i.page);
        }

        return results;

    }

    public Collection<Page> phraseQuery(String words[]) {
        //System.out.println(words);
        HashSet<location> retained = invertedIndex.get(words[0]);
        System.out.println(retained);
        //System.out.println("Toronto: " + invertedIndex.get("Toronto"));
        //System.out.println("banker: " + invertedIndex.get("banker"));

        if(retained == null){
            return new HashSet<Page> ();
        }

        for(int i =1; i< words.length; i++){
            HashSet<location> currWord = invertedIndex.get(words[i]);
            HashSet<location> temp = new HashSet<>();

            System.out.println("" + words[i] + " " + currWord);

            if(currWord == null){
                return new HashSet<Page> ();
            }

            for(location j: retained){
                //System.out.println(new location(j.page, j.loc+i));

                if(currWord.contains(new location(j.page, j.loc+i))){
                    temp.add(j);
                }
            }

            //System.out.println(temp);
            retained = temp;
            System.out.println("retained: " + retained);
        }

        Set<Page> results = new HashSet<Page>();
        for(location i: retained){
            results.add(i.page);
        }

        return results;
    }

    public String toString (){
        return "" + invertedIndex + "\n" +  pages;
    }

}

class location implements Serializable {

    private static final long serialVersionUID = 2L;

    Page page;
    int loc;

    public location(Page page, int loc){
        this.page = page;
        this.loc = loc;
    }

    public boolean equals(Object other){
        if(other == null){
            return false;
        }

        if(this.getClass() != other.getClass()){
            return false;
        }

        if(!this.page.equals(((location)other).page) ){
            return false;
        }

        if(this.loc != ((location)other).loc){
            return false;
        }
        return true;
    }

    public String toString(){
        return "" + page + " " + loc + " " + hashCode();
    }

    public int hashCode (){
        int sum = page.getPageNum() + loc;
        return ((sum*(sum+1))/2 ) + loc;
    }
}
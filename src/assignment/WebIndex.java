package assignment;

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
            return phraseQuery(query);

    }

    public Collection<Page> wordQuery(String query) {
        HashSet<location> curr = invertedIndex.get(query);

        if(curr == null){
            return new LinkedList<Page>();
        }
        else{
            Set<Page> results = new HashSet<Page>();
            for(location i: curr){
                results.add(i.page);
            }
            return results;
        }
    }

    public Collection<Page> phraseQuery(String query) {

    }


}

class location{
    Page page;
    int loc;

    public location(Page page, int loc){
        this.page = page;
        this.loc = loc;
    }

    public boolean equals(location other){
        return (this.page.equals(other.page))&&(this.loc == other.loc);
    }
}
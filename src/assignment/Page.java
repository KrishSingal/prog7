package assignment;
import java.io.*;
import java.net.URL;

/**
 * The Page class holds anything that the QueryEngine returns to the server.  The field and method
 * we provided here is the bare minimum requirement to be a Page - feel free to add anything you
 * want as long as you don't break the getURL method.
 *
 * TODO: Implement this!
 */
public class Page implements Serializable {
    // The URL the page was located at
    private URL url;
    private int pageNum;


    private static final long serialVersionUID = 3L;

    /**
     * Creates a Page with a given URL.
     * @param url The url of the page.
     */
    public Page(URL url) {
        this.url = url;
    }

    /**
     * @return the URL of the page.
     */
    public URL getURL() { return url; }

    /**
     * Determines whether two pages are equal
     * @param other
     * @return whether two pages are equal
     */
    public boolean equals(Object other){
        if(other == null){
            return false;
        }

        if(this.getClass() != other.getClass()){
            return false;
        }

        if(!this.url.toString().equals (((Page)other).url.toString())){
            return false;
        }

        return true;
    }

    /**
     * toString function that dictates how the Page should be represented
     * @return  String representation of Page
     */
    public String toString(){
        return "" + this.url + " " + pageNum;
    }

    public void setPageNum(int n){
        pageNum = n;
    }

    /**
     * Overrides hashCode() of Page instance to be the same as the contained URL
     * @return      unique hashCode of this Page instance
     */
    public int hashCode(){
        return this.url.hashCode();
    }

}

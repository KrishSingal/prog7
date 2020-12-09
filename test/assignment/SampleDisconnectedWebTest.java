package assignment;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import java.nio.file.Paths;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.net.*;
import org.attoparser.simple.*;

/**
 * Testing class that evaluates the program on the custom disconnected test web that also has an empty file
 */
public class SampleDisconnectedWebTest {
    static Set<Page> empty;
    static Set<Page> set0;
    static Set<Page> set1;
    static Set<Page> set2;
    static Set<Page> set01;
    static Set<Page> set02;
    static Set<Page> set12;
    static Set<Page> set012;

    /**
     * Performs one time crawl and set instantiation, so that generated index can be used for
     * query testing
     * @throws MalformedURLException
     */
    @BeforeClass
    public static void crawl() throws MalformedURLException {
        String currPath  = Paths.get(".").toAbsolutePath().normalize().toString();

        // Crawl the custom web
        WebCrawler.main(new String[] {"file://localhost" + currPath + "/DisconnectedTestWeb/home.html"});

        URL zero = new URL("file://localhost" + currPath + "/DisconnectedTestWeb/home.html");
        URL one = new URL("file://localhost" + currPath + "/DisconnectedTestWeb/treaps.html");
        URL nothing = new URL("file://localhost" + currPath + "/DisconnectedTestWeb/howtocreate.htmlfiles.html?name=networking#hello");

        // Sets that will be used in assertion tests
        empty = new HashSet<>();
        set0 = new HashSet<Page> (){{
            add(new Page (zero));
        }};
        set1 = new HashSet<Page> (){{
            add(new Page (one));
        }};
        set2 = new HashSet<Page> (){{
            add(new Page (nothing));
        }};
        set01 = new HashSet<Page> (){{
            add(new Page (zero));
            add(new Page (one));
        }};
        set02 = new HashSet<Page> (){{
            add(new Page (zero));
            add(new Page (nothing));
        }};
        set12 = new HashSet<Page> (){{
            add(new Page (one));
            add(new Page (nothing));
        }};
        set012 = new HashSet<Page> (){{
            add(new Page (zero));
            add(new Page (one));
            add(new Page (nothing));
        }};
    }

    /**
     * Tests basic query functionality consisting only of word and &/| operators
     * Edges cases such as last word of a tag, last word of a sentence, etc. are tested
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Test
    public void BasicQueries() throws IOException, ClassNotFoundException {
        WebQueryEngine wqe = WebQueryEngine.fromIndex((WebIndex) Index.load("index.db"));

        // Testing basic common word
        assertEquals(wqe.query("the"), set01);

        // Testing Case sensiitivty in queries, word at the beginning of the sentence, & operation
        assertEquals(wqe.query("(Welcome & edge)"), set0);

        // Testing & operation, omission of punctuation from text
        assertEquals(wqe.query("(browse & around)"), set0);

        // Testing inconsistent spacing in text, | operation
        assertEquals(wqe.query("nutshell | 314H"), set01);

        // Testing words in anchor tags, words across all pages
        assertEquals(wqe.query("(treaps & tetris)"), set01);

        // Testing more complicated query structures and omission of " punctuation from text
        assertEquals(wqe.query("((undigestify & chunks) | treaps)"), set01);

        // Testing very complicated query structure
        assertEquals(wqe.query("((300 & split) | ((undigestify & chunks) | treaps))"), set01);

        // Testing inconsistent spacing and case sensitivity in query
        assertEquals(wqe.query("((uNdigEstify &   chunKs) | Treaps)"), set01);

        // Testing removal of apostrophes, ?, etc. from text
        assertEquals(wqe.query("((characters & weird) & deal)"), set1);

        // Testing false negative, ensures script tags are skipped
        assertNotEquals(wqe.query("(alongside & var)"), set2);

        // Testing that apostrophes are removed, inconsistent spacing in text
        assertEquals(wqe.query("(dont & forget)"), set1);

        // Testing beginning words of tags
        assertEquals(wqe.query("(Alongside | dont)"), set01);

        // Testing end words of tags
        assertEquals(wqe.query("(recursively & above)"), set1);

        // Testing last words of sentences
        assertEquals(wqe.query("(practice & beware)"), set0);

        // Testing last word of the document
        assertEquals(wqe.query("(well | beware)"), set01);

        // Testing last word in an unclosed paragraph tag
        assertEquals(wqe.query("paragraph"), set01);
    }

    /**
     * Tests negative query functionality
     * Edges cases such as empty files, last word of a tag, last word of a sentence, etc. are tested
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Test
    public void NegativeQueries() throws IOException, ClassNotFoundException {
        WebQueryEngine wqe = WebQueryEngine.fromIndex((WebIndex) Index.load("index.db"));

        System.out.println(wqe.index.pages);

        // Testing basic negated common word
        assertEquals(wqe.query("!the"), set2);

        // Testing Case sensitivity in queries, word at the beginning of the sentence, & operation
        // Testing empty set
        assertEquals(wqe.query("(Welcome & !edge)"), empty);

        // Testing & operation, omission of punctuation from text
        // Testing negation with | operator
        assertEquals(wqe.query("(!browse | around)"), set012);

        // Testing inconsistent spacing in text, | operation
        // Testing negation with & operator on file that satisfies one but not the other
        assertEquals(wqe.query("(forget & !314H)"), empty);

        // Testing words in anchor tags, words across all pages
        // Testing for | compatibility
        assertEquals(wqe.query("(!treaps | tetris)"), set012);

        // Testing more complicated query structures and omission of " punctuation from text
        // Negation that rules out a page
        assertEquals(wqe.query("(!undigestify & treaps)"), set1);

        // Testing more complicated query structures and omission of " punctuation from text
        // Negation with complicated structures
        assertEquals(wqe.query("((!undigestify & chunks) | treaps)"), set01);

        // Testing very complicated query structure
        assertEquals(wqe.query("((300 | !split) | ((undigestify & chunks) | treaps))"), set012);

        // Testing inconsistent spacing and case sensitivity in query
        assertEquals(wqe.query("((!uNdigEstify &   chunKs) | Treaps)"), set01);

        // Testing removal of apostrophes, ?, etc. from text
        assertEquals(wqe.query("((characters & weird) & !deal)"), empty);

        // Testing false negative, ensures script tags are skipped
        assertEquals(wqe.query("(alongside & !var)"), set0);

        // Testing that apostrophes are removed, inconsistent spacing in text
        assertEquals(wqe.query("(dont | !forget)"), set012);

        // Testing beginning words of tags
        assertEquals(wqe.query("(Alongside | !dont)"), set02);

        // Testing end words of tags
        // Testing false negative with negative queries
        assertEquals(wqe.query("((recursively & above) | !happy)"), set012);

        // Testing multiple negations
        assertEquals(wqe.query("((!300 & !split) & ((undigestify & chunks) | treaps))"), set1);
    }


    /**
     * Tests phrase query functionality
     * Edges cases such as empty files, last phrase of a tag, phrase across tags, etc. are tested
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Test
    public void PhraseQueries() throws IOException, ClassNotFoundException {
        WebQueryEngine wqe = WebQueryEngine.fromIndex((WebIndex) Index.load("index.db"));

        // Testing basic phrase query with inconsistent spacing and concatentation
        assertEquals(wqe.query("\"Here you can find all sorts of fun and interesting edge cases that will aid Krish " +
                "    in his testing procedure  \""), set0);

        // Testing phrase from beginning to end of sentence to ensure everything is accounted for
        assertEquals(wqe.query("\"i have split the file up into 10 chunks of around 300 lines each\""), set0);

        // Testing weird phrase
        assertEquals(wqe.query("\"somenewsreaders\""), set0);

        // Testing common phrase
        assertEquals(wqe.query("\"Dont forget to check out my blog\""), set1);

        // Testing common phrase with word and & operations
        assertEquals(wqe.query("(\"Dont forget to check out my blog\" & treaps)"), set1);

        // Testing common phrase with word, & operations, negations
        assertEquals(wqe.query("(\"Dont forget to check out my blog\" & !treaps)"), empty);

        // Testing common phrase with word, & operations, negations
        assertEquals(wqe.query("(!\"Dont forget to check out my blog\" & treaps)"), set0);

        // Testing common phrase with word, & operations, negations
        assertEquals(wqe.query("(!\"Dont forget to check out my blog\" & !treaps)"), set2);

        // Testing phrase at end of document in unclosed paragraph tag. Also tests phrase across tags and
        // punctuation removal
        assertEquals(wqe.query("\"tetris project as well\""), empty);

        // Testing phrase across tags and
        // unclosed paragraph tags
        assertEquals(wqe.query("\"here is the paragraph without a closing tag\""), set1);

        // Testing phrase across tags, unclosed paragraph tags, and multiple phrases with & operation
        assertEquals(wqe.query("(\"phrase across tags\" & \"implemented recursively\")"), set1);

        // Testing word as phrase
        assertEquals(wqe.query("\"treaps\""), set01);

        // Testing complicated query structure with phrases, words, operations, negation
        assertEquals(wqe.query("(((\"treaps\" | \"tetris\") & (\"dont forget to\" | \"one part\")) | !\"treaps\")"), set012);

        //Testing false negative with phrases
        assertNotEquals(wqe.query("(\"all my operations were not implemented recursively\")"), set1);
    }

    /**
     * Tests implicit AND query functionality
     * Edges cases such as empty files, phrase across tags, all possible implicit AND cases, etc. are tested
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Test
    public void ImplicitAndQueries() throws IOException, ClassNotFoundException {
        WebQueryEngine wqe = WebQueryEngine.fromIndex((WebIndex) Index.load("index.db"));

        // Testing basic implicit and with phrase and word query with inconsistent spacing and concatentation
        assertEquals(wqe.query("\"Here you can find all sorts of fun and interesting edge cases that will aid Krish " +
                "    in his testing procedure  \" welcome"), set0);

        // Testing implicit and with parenthesized operation and phrase
        assertEquals(wqe.query("(testris | hoot)\"i have split the file up into 10 chunks of around 300 lines each\""), set0);

        // Testing inconsistent spacing with implicit AND
        assertEquals(wqe.query("\"somenewsreaders\"   \"krish\""), set0);

        // Testing implicit AND on two parenthesized operations
        assertEquals(wqe.query("(\"Dont forget to check out my blog\" | welcome) (treaps & tetris)"), set01);

        // Testing phrase with parenthesized operation second
        assertEquals(wqe.query("\"Dont forget to check out my blog\" (treaps | tetris)"), set1);

        // Testing common phrase with word, & operations, negations, implict AND with parenthesized operation
        assertEquals(wqe.query("welcome (\"Dont forget to check out my blog\" & !treaps)"), empty);

        // Testing common phrase with word, & operations, negations
        assertEquals(wqe.query("(!\"Dont forget to check out my blog\" & treaps) welcome"), set0);

        // Testing two instances of implicit AND with negation as well
        assertEquals(wqe.query("(\"phrase across tags\" \"implemented recursively\") (\"Dont forget to check out my blog\" !welcome)"), set1);

        // Testing implicit AND with phrases and negation
        assertEquals(wqe.query("\"tetris project as well\" !trained"), empty);

        // Testing phrase across tags and negative query implict AND with parenthesized operation
        assertEquals(wqe.query("(\"trained a brain\" | welcome)!welcome"), empty);

        // Testing phrase across tags and negative query implict AND with parenthesized operation
        assertEquals(wqe.query("!welcome(\"trained a brain\" | welcome)"), empty);

        // Testing implicit AND with words and negation
        assertEquals(wqe.query("!devised !functionality"), set12);

        // Testing complicated query structure with phrases, words, operations, negation
        assertEquals(wqe.query("(((\"treaps\" | \"tetris\") (\"dont forget to\" | \"one part\")) | !\"treaps\")"), set012);

        //Testing false negative with phrases
        assertNotEquals(wqe.query("\"all my operations were not implemented recursively\" word"), set1);
    }
}


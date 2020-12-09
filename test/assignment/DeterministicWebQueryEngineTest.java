package assignment;

import org.junit.Test;

import static org.junit.Assert.*;
import java.util.*;

/**
 * Tests that intermediate structure of queries and processing of queries in WebQueryEngine is correct
 */
public class DeterministicWebQueryEngineTest {

    /**
     * Tests the tokenization of basic queries consisting of words with &/| operators and
     * various edge cases
     */
    @Test
    public void BasicQueriesTokenization() {
        WebQueryEngine wqe = new WebQueryEngine();

        // Regular word query with only letters
        assertEquals(wqe.tokenize("snuffles"), new ArrayList<String>
                (List.of("snuffles")));

        // Regular word query with only digits
        assertEquals(wqe.tokenize("1999"), new ArrayList<String>
                (List.of("1999")));

        // Regular word query with both letters and numbers
        assertEquals(wqe.tokenize("100snuffles"), new ArrayList<String>
                (List.of("100snuffles")));

        // Usage of "&" operator
        assertEquals(wqe.tokenize("(10snuffles & sick)"), new ArrayList<String>
                        (List.of("(", "10snuffles",  "&", "sick", ")")));

        // Usage of "|" operator
        assertEquals(wqe.tokenize("(10snuffles | sick)"), new ArrayList<String>
                (List.of("(", "10snuffles",  "|", "sick", ")")));

        // Multiple uses of operator "&" and complex parenthesization
        assertEquals(wqe.tokenize("((10snuffles & sniffles) & (sick & cold))"), new ArrayList<String>
                (List.of("(", "(", "10snuffles", "&", "sniffles", ")",  "&", "(", "sick", "&", "cold", ")", ")")));

        // Multiple uses of operator "|" and complex parenthesization
        assertEquals(wqe.tokenize("((10snuffles | sniffles) | (sick | cold))"), new ArrayList<String>
                (List.of("(", "(", "10snuffles", "|", "sniffles", ")",  "|", "(", "sick", "|", "cold", ")", ")")));

        // Multiple uses of both "&" and "|" operators and complex parenthesization
        assertEquals(wqe.tokenize("(99crawler | (((10snuffles & sniffles) | (sick | cold)) & web))"), new ArrayList<String>
                (List.of("(", "99crawler", "|", "(", "(", "(", "10snuffles", "&", "sniffles", ")",  "|", "(", "sick", "|", "cold", ")", ")", "&", "web", ")", ")")));

    }

    /**
     * Tests the tokenization of negative basic queries consisting of words with &,|,! operators and
     * various edge cases
     */
    @Test
    public void NegativeQueriesTokenization() {
        WebQueryEngine wqe = new WebQueryEngine();

        // Regular negative word query with only letters
        assertEquals(wqe.tokenize("!snuffles"), new ArrayList<String>
                (List.of("!", "snuffles")));

        // Regular negative word query with only digits
        assertEquals(wqe.tokenize("!1999"), new ArrayList<String>
                (List.of("!", "1999")));

        // Regular negative word query with both letters and numbers
        assertEquals(wqe.tokenize("!100snuffles"), new ArrayList<String>
                (List.of("!", "100snuffles")));

        // Usage of "&" and "!" operators
        assertEquals(wqe.tokenize("(10snuffles & !sick)"), new ArrayList<String>
                (List.of("(", "10snuffles",  "&", "!", "sick", ")")));

        // Usage of "&" and double negation
        assertEquals(wqe.tokenize("(!10snuffles & !sick)"), new ArrayList<String>
                (List.of("(", "!", "10snuffles",  "&", "!", "sick", ")")));

        // Usage of "|" and "!" operators
        assertEquals(wqe.tokenize("(!10snuffles | sick)"), new ArrayList<String>
                (List.of("(", "!", "10snuffles",  "|", "sick", ")")));

        // Multiple uses of operator "&" and "!" and complex parenthesization
        assertEquals(wqe.tokenize("((!10snuffles & !sniffles) & (sick & !cold))"), new ArrayList<String>
                (List.of("(", "(", "!", "10snuffles", "&", "!", "sniffles", ")",  "&", "(", "sick", "&", "!", "cold", ")", ")")));

        // Multiple uses of operator "|" and "!" and complex parenthesization
        assertEquals(wqe.tokenize("((10snuffles | !sniffles) | (!sick | !cold))"), new ArrayList<String>
                (List.of("(", "(", "10snuffles", "|", "!", "sniffles", ")",  "|", "(", "!", "sick", "|", "!", "cold", ")", ")")));

        // Multiple uses of both "&" and "|" and "!" operators and complex parenthesization
        assertEquals(wqe.tokenize("(!99crawler | (((10snuffles & sniffles) | (sick | cold)) & !web))"), new ArrayList<String>
                (List.of("(", "!", "99crawler", "|", "(", "(", "(", "10snuffles", "&", "sniffles", ")",  "|", "(", "sick", "|", "cold", ")", ")", "&", "!", "web", ")", ")")));
    }

    /**
     * Tests the tokenization of phrase queries and
     * various edge cases
     */
    @Test
    public void PhraseQueriesTokenization() {
        WebQueryEngine wqe = new WebQueryEngine();

        // Regular phrase query with letters and digits
        assertEquals(wqe.tokenize("\"I have 10 cars\""), new ArrayList<String>
                (List.of("I have 10 cars")));

        // Usage of "&" with phrases
        assertEquals(wqe.tokenize("(\"I have 10 cars\" & \"They are all super expensive\")"), new ArrayList<String>
                (List.of("(", "I have 10 cars", "&", "They are all super expensive", ")")));

        // Usage of "&" with phrase and words
        assertEquals(wqe.tokenize("(\"I have 10 cars\" & !cool)"), new ArrayList<String>
                (List.of("(", "I have 10 cars", "&", "!", "cool", ")")));

        // Usage of "|" phrase and words
        assertEquals(wqe.tokenize("(\"I have 10 cars\" | !cool)"), new ArrayList<String>
                (List.of("(", "I have 10 cars", "|", "!", "cool", ")")));

        // Multiple uses of operator "&" with phrases and words and complex parenthesization
        assertEquals(wqe.tokenize("(((\"I have 10 cars\" & !cool) & yeah) & \"They are all super expensive too\")"), new ArrayList<String>
                (List.of("(", "(", "(", "I have 10 cars", "&", "!", "cool", ")", "&", "yeah", ")", "&", "They are all super expensive too", ")")));

        // Multiple uses of operator "|" with phrases and words and complex parenthesization
        assertEquals(wqe.tokenize("(((\"I have 10 cars\" | !cool) | yeah) | \"They are all super expensive too\")"), new ArrayList<String>
                (List.of("(", "(", "(", "I have 10 cars", "|", "!", "cool", ")", "|", "yeah", ")", "|", "They are all super expensive too", ")")));

        // Multiple uses of operator "&" and "|" with phrases and words and complex parenthesization
        assertEquals(wqe.tokenize("(((\"I have 10 cars\" & !cool) & yeah) | \"They are all super expensive too\")"), new ArrayList<String>
                (List.of("(", "(", "(", "I have 10 cars", "&", "!", "cool", ")", "&", "yeah", ")", "|", "They are all super expensive too", ")")));
    }

    /**
     * Tests the tokenization of implicit AND based queries with an accumulation of all past components and
     * various edge cases
     */
    @Test
    public void ImplicitAndQueriesTokenization() {
        WebQueryEngine wqe = new WebQueryEngine();

        // Regular implicit AND with words
        assertEquals(wqe.tokenize("10 cars"), new ArrayList<String>
                (List.of("10", "&", "cars")));

        // Regular implicit AND with phrases
        assertEquals(wqe.tokenize("\"I have 10 cars\" \"They are all super expensive\""), new ArrayList<String>
                (List.of("I have 10 cars", "&", "They are all super expensive")));

        // implicit AND with phrase and word
        assertEquals(wqe.tokenize("\"I have 10 cars\" cool"), new ArrayList<String>
                (List.of( "I have 10 cars", "&", "cool")));

        // implicit AND with word and phrase
        assertEquals(wqe.tokenize("cool \"I have 10 cars\""), new ArrayList<String>
                (List.of("cool", "&", "I have 10 cars")));

        // implicit AND with phrase and negated word
        assertEquals(wqe.tokenize("\"I have 10 cars\" !cool"), new ArrayList<String>
                (List.of("I have 10 cars", "&", "!", "cool")));

        // implicit AND with negated word and phrase
        assertEquals(wqe.tokenize("!cool \"I have 10 cars\""), new ArrayList<String>
                (List.of("!", "cool", "&", "I have 10 cars")));

        // implicit AND with phrase, word, and parenthesized operations
        assertEquals(wqe.tokenize("\"I have 10 cars\" !cool (hello & !world)"), new ArrayList<String>
                (List.of("I have 10 cars", "&", "!", "cool", "&", "(", "hello", "&", "!", "world", ")")));

        // implicit AND with adjacent parenthesized operations
        assertEquals(wqe.tokenize("(\"I have 10 cars\" | !cool) (hello & !world)"), new ArrayList<String>
                (List.of("(", "I have 10 cars", "|", "!", "cool", ")", "&", "(", "hello", "&", "!", "world", ")")));

        // implicit AND with phrase and parenthesized operations
        assertEquals(wqe.tokenize("\"I have 10 cars\" (hello & !world)"), new ArrayList<String>
                (List.of("I have 10 cars", "&", "(", "hello", "&", "!", "world", ")")));

        // implicit AND with parenthesized operations and phrase
        assertEquals(wqe.tokenize("(hello & !world) \"I have 10 cars\""), new ArrayList<String>
                (List.of("(", "hello", "&", "!", "world", ")", "&", "I have 10 cars")));
    }

    /**
     * Tests the tokenization of all query types under inconsistent spacing
     */
    @Test
    public void InconsistentSpacingTokenization(){
        WebQueryEngine wqe = new WebQueryEngine();

        // Regular implicit AND with words
        assertEquals(wqe.tokenize("10   cars"), new ArrayList<String>
                (List.of("10", "&", "cars")));

        // Regular implicit AND with phrases
        assertEquals(wqe.tokenize("\"I    have 10  cars\"   \"They are all super expensive\""), new ArrayList<String>
                (List.of("I have 10 cars", "&", "They are all super expensive")));

        // implicit AND with phrase and word
        assertEquals(wqe.tokenize("\"I have 10 cars\"cool"), new ArrayList<String>
                (List.of( "I have 10 cars", "&", "cool")));

        // implicit AND with word and phrase
        assertEquals(wqe.tokenize("cool   \"I have 10 cars\""), new ArrayList<String>
                (List.of("cool", "&", "I have 10 cars")));

        // implicit AND with phrase and negated word
        assertEquals(wqe.tokenize("\"I have 10 cars\"!  cool"), new ArrayList<String>
                (List.of("I have 10 cars", "&", "!", "cool")));

        // implicit AND with negated word and phrase
        assertEquals(wqe.tokenize("!cool\"I have 10 cars\""), new ArrayList<String>
                (List.of("!", "cool", "&", "I have 10 cars")));

        // implicit AND with phrase, word, and parenthesized operations
        assertEquals(wqe.tokenize("\"I have 10 cars\" !cool( hello & !world  )"), new ArrayList<String>
                (List.of("I have 10 cars", "&", "!", "cool", "&", "(", "hello", "&", "!", "world", ")")));

        // implicit AND with adjacent parenthesized operations
        assertEquals(wqe.tokenize("(\"I have 10 cars \" | !cool)(hello & ! world)"), new ArrayList<String>
                (List.of("(", "I have 10 cars", "|", "!", "cool", ")", "&", "(", "hello", "&", "!", "world", ")")));

        // implicit AND with phrase and parenthesized operations
        assertEquals(wqe.tokenize("\" I have   10 cars \" (hello   &   !world)"), new ArrayList<String>
                (List.of("I have 10 cars", "&", "(", "hello", "&", "!", "world", ")")));

        // implicit AND with parenthesized operations and phrase
        assertEquals(wqe.tokenize("(hello & !world) \"I have 10 cars\""), new ArrayList<String>
                (List.of("(", "hello", "&", "!", "world", ")", "&", "I have 10 cars")));
    }

    /**
     * Tests the postfix generation of basic queries
     */
    @Test
    public void BasicQuerypostfix() {

        WebQueryEngine wqe = new WebQueryEngine();

        // Regular word query with only letters
        assertEquals(wqe.postfix("snuffles"), new ArrayList<String>
                (List.of("snuffles")));

        // Regular word query with only digits
        assertEquals(wqe.postfix("1999"), new ArrayList<String>
                (List.of("1999")));

        // Regular word query with both letters and numbers
        assertEquals(wqe.postfix("100snuffles"), new ArrayList<String>
                (List.of("100snuffles")));

        // Usage of "&" operator
        assertEquals(wqe.postfix("(10snuffles & sick)"), new ArrayList<String>
                (List.of("10snuffles", "sick", "&")));

        // Usage of "|" operator
        assertEquals(wqe.postfix("(10snuffles | sick)"), new ArrayList<String>
                (List.of( "10snuffles", "sick",  "|" )));

        // Multiple uses of operator "&" and complex parenthesization
        assertEquals(wqe.postfix("((10snuffles & sniffles) & (sick & cold))"), new ArrayList<String>
                (List.of( "10snuffles", "sniffles" , "&", "sick", "cold", "&",   "&" )));

        // Multiple uses of operator "|" and complex parenthesization
        assertEquals(wqe.postfix("((10snuffles | sniffles) | (sick | cold))"), new ArrayList<String>
                (List.of( "10snuffles", "sniffles" , "|",  "sick", "cold", "|",   "|" )));

        // Multiple uses of both "&" and "|" operators and complex parenthesization
        assertEquals(wqe.postfix("(99crawler | (((10snuffles & sniffles) | (sick | cold)) & web))"), new ArrayList<String>
                (List.of( "99crawler", "10snuffles", "sniffles" , "&",   "sick", "cold" , "|", "|",  "web", "&", "|"  )));

    }

    /**
     * Tests the postfix generation of negative queries
     */
    @Test
    public void NegativeQueriesPostfix() {
        WebQueryEngine wqe = new WebQueryEngine();

        // Regular negative word query with only letters
        assertEquals(wqe.postfix("!snuffles"), new ArrayList<String>
                (List.of( "snuffles", "!")));

        // Regular negative word query with only digits
        assertEquals(wqe.postfix("!1999"), new ArrayList<String>
                (List.of("1999", "!")));

        // Regular negative word query with both letters and numbers
        assertEquals(wqe.postfix("!100snuffles"), new ArrayList<String>
                (List.of( "100snuffles", "!")));

        // Usage of "&" and "!" operators
        assertEquals(wqe.postfix("(10snuffles & !sick)"), new ArrayList<String>
                (List.of("10snuffles", "sick", "!", "&")));

        // Usage of "|" and "!" operators
        assertEquals(wqe.postfix("(!10snuffles | sick)"), new ArrayList<String>
                (List.of("10snuffles", "!", "sick", "|")));

        // Multiple uses of operator "&" and "!" and complex parenthesization
        assertEquals(wqe.postfix("((!10snuffles & !sniffles) & (sick & !cold))"), new ArrayList<String>
                (List.of( "10snuffles", "!", "sniffles", "!", "&", "sick", "cold", "!", "&", "&")));

        // Multiple uses of operator "|" and "!" and complex parenthesization
        assertEquals(wqe.postfix("((10snuffles | !sniffles) | (!sick | !cold))"), new ArrayList<String>
                (List.of( "10snuffles", "sniffles", "!", "|",  "sick",  "!", "cold", "!", "|", "|")));

        // Multiple uses of both "&" and "|" and "!" operators and complex parenthesization
        assertEquals(wqe.postfix("(!99crawler | (((10snuffles & sniffles) | (sick | cold)) & !web))"), new ArrayList<String>
                (List.of("99crawler", "!", "10snuffles",  "sniffles", "&",  "sick",  "cold","|", "|",  "web", "!", "&", "|")));
    }

    /**
     * Tests the postfix generation of phrase queries and vrious combinations
     */
    @Test
    public void PhraseQueriesPostfix() {
        WebQueryEngine wqe = new WebQueryEngine();

        // Regular phrase query with letters and digits
        assertEquals(wqe.postfix("\"I have 10 cars\""), new ArrayList<String>
                (List.of("I have 10 cars")));

        // Usage of "&" with phrases
        assertEquals(wqe.postfix("(\"I have 10 cars\" & \"They are all super expensive\")"), new ArrayList<String>
                (List.of("I have 10 cars", "They are all super expensive", "&")));

        // Usage of "&" with phrase and words
        assertEquals(wqe.postfix("(\"I have 10 cars\" & !cool)"), new ArrayList<String>
                (List.of("I have 10 cars", "cool", "!", "&")));

        // Usage of "|" phrase and words
        assertEquals(wqe.postfix("(\"I have 10 cars\" | !cool)"), new ArrayList<String>
                (List.of("I have 10 cars", "cool", "!", "|")));

        // Multiple uses of operator "&" with phrases and words and complex parenthesization
        assertEquals(wqe.postfix("(((\"I have 10 cars\" & !cool) & yeah) & \"They are all super expensive too\")"), new ArrayList<String>
                (List.of("I have 10 cars", "cool", "!", "&",  "yeah", "&",  "They are all super expensive too", "&")));

        // Multiple uses of operator "|" with phrases and words and complex parenthesization
        assertEquals(wqe.postfix("(((\"I have 10 cars\" | !cool) | yeah) | \"They are all super expensive too\")"), new ArrayList<String>
                (List.of("I have 10 cars", "cool", "!", "|",  "yeah", "|",  "They are all super expensive too", "|")));

        // Multiple uses of operator "&" and "|" with phrases and words and complex parenthesization
        assertEquals(wqe.postfix("(((\"I have 10 cars\" & !cool) & yeah) | \"They are all super expensive too\")"), new ArrayList<String>
                (List.of("I have 10 cars", "cool", "!", "&",  "yeah", "&",  "They are all super expensive too", "|")));
    }

    /**
     * Tests the postfix generation of implicit AND queries
     */
    @Test
    public void ImplicitAndQueriesPostfix() {
        WebQueryEngine wqe = new WebQueryEngine();

        // Regular implicit AND with words
        assertEquals(wqe.postfix("10 cars"), new ArrayList<String>
                (List.of("10", "cars", "&")));

        // Regular implicit AND with phrases
        assertEquals(wqe.postfix("\"I have 10 cars\" \"They are all super expensive\""), new ArrayList<String>
                (List.of("I have 10 cars", "They are all super expensive", "&")));

        // implicit AND with phrase and word
        assertEquals(wqe.postfix("\"I have 10 cars\" cool"), new ArrayList<String>
                (List.of( "I have 10 cars", "cool", "&")));

        // implicit AND with word and phrase
        assertEquals(wqe.postfix("cool \"I have 10 cars\""), new ArrayList<String>
                (List.of("cool", "I have 10 cars", "&")));

        // implicit AND with phrase and negated word
        assertEquals(wqe.postfix("\"I have 10 cars\" !cool"), new ArrayList<String>
                (List.of("I have 10 cars", "cool", "!", "&")));

        // implicit AND with negated word and phrase
        assertEquals(wqe.postfix("!cool \"I have 10 cars\""), new ArrayList<String>
                (List.of( "cool", "!", "I have 10 cars", "&")));

        // implicit AND with phrase, word, and parenthesized operations
        assertEquals(wqe.postfix("\"I have 10 cars\" !cool (hello & !world)"), new ArrayList<String>
                (List.of("I have 10 cars",  "cool", "!", "&", "hello", "world", "!", "&", "&" )));

        // implicit AND with adjacent parenthesized operations
        assertEquals(wqe.postfix("(\"I have 10 cars\" | !cool) (hello & !world)"), new ArrayList<String>
                (List.of( "I have 10 cars", "cool", "!", "|", "hello",  "world", "!", "&", "&")));

        // implicit AND with phrase and parenthesized operations
        assertEquals(wqe.postfix("\"I have 10 cars\" (hello & !world)"), new ArrayList<String>
                (List.of("I have 10 cars", "hello",  "world", "!", "&", "&")));

        // implicit AND with parenthesized operations and phrase
        assertEquals(wqe.postfix("(hello & !world) \"I have 10 cars\""), new ArrayList<String>
                (List.of("hello",  "world", "!", "&",  "I have 10 cars", "&")));
    }

    /**
     * Tests the postfix generation of all query types under inconsistent spacing
     */
    @Test
    public void InconsistentSpacingPostfix(){
        WebQueryEngine wqe = new WebQueryEngine();

        // Regular implicit AND with words
        assertEquals(wqe.postfix("10   cars"), new ArrayList<String>
                (List.of("10", "cars", "&")));

        // Regular implicit AND with phrases
        assertEquals(wqe.postfix("\"I    have 10  cars\"   \"They are all super expensive\""), new ArrayList<String>
                (List.of("I have 10 cars", "They are all super expensive", "&")));

        // implicit AND with phrase and word
        assertEquals(wqe.postfix("\"I have 10 cars\"cool"), new ArrayList<String>
                (List.of( "I have 10 cars", "cool", "&")));

        // implicit AND with word and phrase
        assertEquals(wqe.postfix("cool   \"I have 10 cars\""), new ArrayList<String>
                (List.of("cool", "I have 10 cars", "&")));

        // implicit AND with phrase and negated word
        assertEquals(wqe.postfix("\"I have 10 cars\"!  cool"), new ArrayList<String>
                (List.of("I have 10 cars", "cool", "!", "&")));

        // implicit AND with negated word and phrase
        assertEquals(wqe.postfix("!cool\"I have 10 cars\""), new ArrayList<String>
                (List.of( "cool", "!", "I have 10 cars", "&")));

        // implicit AND with phrase, word, and parenthesized operations
        assertEquals(wqe.postfix("\"I have 10 cars\" !cool( hello & !world  )"), new ArrayList<String>
                (List.of("I have 10 cars",  "cool", "!", "&", "hello", "world", "!", "&", "&" )));

        // implicit AND with adjacent parenthesized operations
        assertEquals(wqe.postfix("(\"I have 10 cars \" | !cool)(hello & ! world)"), new ArrayList<String>
                (List.of( "I have 10 cars", "cool", "!", "|", "hello",  "world", "!", "&", "&")));

        // implicit AND with phrase and parenthesized operations
        assertEquals(wqe.postfix("\" I have   10 cars \" (hello   &   !world)"), new ArrayList<String>
                (List.of("I have 10 cars", "hello",  "world", "!", "&", "&")));

        // implicit AND with parenthesized operations and phrase
        assertEquals(wqe.postfix("(hello & !world) \"I have 10 cars\""), new ArrayList<String>
                (List.of("hello",  "world", "!", "&",  "I have 10 cars", "&")));
    }

}
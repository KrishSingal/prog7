package assignment;

import java.io.IOException;
import java.util.*;

public class ParseQueryTest {

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        /*
        System.out.println(terminators);
        System.out.println(operators);
        System.out.println(precedence);*/

        /*
        System.out.println(tokenize("snuffles"));
        System.out.println(tokenize("(!snuffles & sniffles)"));
        System.out.println(tokenize("(!(snuffles & sniffles) | gif)"));
        System.out.println(tokenize("!(snuffles & sniffles) | gif"));
        System.out.println(tokenize("!(\"snuffles are fun\" & sniffles) | gif"));

        System.out.println(tokenize("hi Krish hey Bob"));
        System.out.println(tokenize("Bobert is cool !cool"));
        System.out.println(tokenize("(\"hi Krish\") (hey Bob)"));
        System.out.println(tokenize("(hi Krish (boy | hey) Bob)"));

        //System.out.println(postfix(tokenize("snuffles")));
        //System.out.println(postfix(tokenize("(!snuffles & sniffles)")));
        //System.out.println(postfix(tokenize("(!(snuffles & sniffles) | gif)")));
        //System.out.println(postfix(tokenize("!(snuffles & sniffles) | gif")));
        //System.out.println(postfix(tokenize("!(\"snuffles are fun\" & sniffles) | gif")));

        //System.out.println(postfix(tokenize("hi Krish hey Bob")));
        //System.out.println(postfix(tokenize("Bobert is cool !cool")));
        //System.out.println(postfix(tokenize("(\"hi Krish\") (hey Bob)")));
        System.out.println(postfix(tokenize("(hi Krish (boy | hey) Bob)")));
        */

        //System.out.println(WebQueryEngine.postfix("yarsd | \"finishde ym reakfast\""));

        WebQueryEngine wqe = new WebQueryEngine();

        //System.out.println(Arrays.toString(wqe.getTwoQueries("(books & books)")));
        //System.out.println(wqe.simplify(wqe.tokenize("(books & books)")));
        System.out.println(Arrays.toString(wqe.getTwoQueries("(!books & !books)")));

        System.out.println(wqe.tokenize("(!books &!books)"));
        System.out.println(wqe.simplify(wqe.tokenize("(!books & !books)")));
        //System.out.println(wqe.simplify(wqe.tokenize("((!books & !papers) | (!books & !papers))")));
    }
}


package assignment;
import java.net.URL;
import java.util.*;

/**
 * A query engine which holds an underlying web index and can answer textual queries with a
 * collection of relevant pages.
 *
 */
public class WebQueryEngine {

    WebIndex index; // Internal index used to satisfy queries

    // Set of terminating characters
    Set<String> terminators = new HashSet<String>(){{
        add("|");
        add("&");
        add("!");
        add("(");
        add(")");
    }};

    // Set of operators
    Set<String> operators = new HashSet<String>(){{
        add("|");
        add("&");
        add("!");
    }};

    // Map from operations to precedence
    Map<String, Integer> precedence = new HashMap<>(){{
        put("!", 3);
        put("&", 2);
        put("|", 1);
    }};

    /**
     * Constructor for WebIndex
     * @param index     local WebIndex variable used to compute query results
     */
    public WebQueryEngine(WebIndex index){
        this.index = index;
    }

    public WebQueryEngine(){

    }
    /**
     * Returns a WebQueryEngine that uses the given Index to construct answers to queries.
     *
     * @param index The WebIndex this WebQueryEngine should use.
     * @return A WebQueryEngine ready to be queried.
     */
    public static WebQueryEngine fromIndex(WebIndex index) {
        return new WebQueryEngine(index);
    }

    /**
     * Returns a Collection of URLs (as Strings) of web pages satisfying the query.
     *
     * @param query A query.
     * @return A collection of web pages satisfying the query.
     */
    public Collection<Page> query(String query) {

        query = query.toLowerCase(); // Handle case sensitivity

        // Instantiate variables
        ArrayList<String> postfix = postfix(query);
        Stack<HashSet<Page>> ops = new Stack<>();
        int opcount = 0;

        // Evaluate postfix expression by performing set operations on the operands
        // in the order specified by the postfix notation
        while (!postfix.isEmpty()) {
            String next = postfix.remove(0); // Next token

            // if next token is an operand, push the set of pages that represent the operand
            if (!operators.contains(next)) {
                ops.push(new HashSet<Page>(index.query(next)));
            }
            // Otherwise, it must be an operator
            // Perform the operation
            else {
                performOperation(ops, next);
            }
        }

        // Final set in stack is the accumulation of all operations
        return ops.pop();

    }

    /**
     * Performs specified operation on the respective operands. Abtracts the operation handling to enable
     * cleaner code
     * @param ops           working stack
     * @param operation     specified operation to perform
     */
    public void performOperation(Stack<HashSet<Page>> ops, String operation){
        // All operations require at least the top set
        HashSet<Page> set1 = ops.pop();
        HashSet<Page> pushset = new HashSet<>(set1); // set to be operated on, will contain the results of the operation
        HashSet<Page> set2;

        switch(operation){
            case "&":
                set2 = ops.pop();

                // perform intersection of the operands
                pushset.retainAll(set2);
                ops.push(pushset);
                break;

            case "|":
                set2 = ops.pop();

                // perform union of operands
                pushset.addAll(set2);
                ops.push(pushset);
                break;

            case "!":
                pushset.clear();

                // populates 'pushSet' with the complement of the top set
                for(Page p: index.pages){
                    if (!set1.contains(p)){
                        pushset.add(p);
                    }
                }
                ops.push(pushset);
                break;
        }
    }

    /**
     * Tokenizes the query in infix notation
     * @param query     specified query
     * @return          tokenized infix representation of query
     */
    public ArrayList<String> tokenize(String query){
        ArrayList<String> tokens = new ArrayList<> ();
        int index = 0;
        String last = null;

        // traverse through query from left to right and build tokens
        while(index < query.length()){
            String c = query.substring(index, index+1);

            // skip over spaces
            if(c.equals(" ")){
                index++;
            }

            // current character is an operator or parentheses
            else if(terminators.contains(c)) {
                // explicitly add in '&' operator if the implicit AND is required
                // check whether current token signals the beginning of a new query component,
                // and last token signals the end of another token. Then an '&' is required to join them
                if(c.equals("!") && last != null && (last.equals(")") || !terminators.contains(last))){
                    tokens.add("&");
                }

                if (c.equals("(") && last != null && (last.equals(")") || !terminators.contains(last))) {
                    tokens.add("&");
                }
                tokens.add(c);
                index++;
                last = c;
            }

            // this token is not a parentheses or operator, so we parse it as a word/phrase query
            // phrase
            else if (c.equals("\"")){
                String phrase= "";
                index++;

                // Build phrase until the closing quotation marks are reached
                while(index < query.length() && !query.substring(index, index+1).equals("\"") ){
                    phrase+= query.substring(index, index+1);
                    index++;
                }

                // if last token signals an end query component, implicit AND is required
                if(last != null && (last.equals(")") || !terminators.contains(last))){
                    tokens.add("&");
                }

                phrase = phrase.trim().replaceAll(" +", " "); // handle inconsistent casing in phrases
                tokens.add(phrase);
                index++;
                last = phrase;
            }

            // Word
            else{
                String word= "";

                // Build word till a non-letter/non-digit is found
                while(index < query.length() && !query.substring(index, index+1).equals(" ")
                        && !query.substring(index, index+1).equals("\"") && !terminators.contains(query.substring(index, index+1))){
                    word+= query.substring(index, index+1);
                    index++;
                }

                // if last token signals an end query component, implicit AND is required
                String toAdd = word.trim();
                if(last != null && (last.equals(")") || !terminators.contains(last))){
                    tokens.add("&");
                }

                tokens.add(toAdd);
                last = toAdd;

            }

        }
        return tokens;
    }

    /**
     * Implements Shunting Yard Algorithm to generate a postfix representation of a given query
     * @param query     Specified query requested
     * @return          Postfix representation of query
     */
    public ArrayList<String> postfix(String query){

        ArrayList<String> tokens = tokenize(query); // Retrieves tokenized list from helper function
        Stack<String> stack = new Stack<>();
        ArrayList<String> output = new ArrayList<String>(); // Output queue with postfix representation

        for (int i = 0; i < tokens.size(); ++i) {

            String c = tokens.get(i);

            // If the curr token is an operand, add it directly to the output queue
            if (!terminators.contains(c))
                output.add(c);

            // Push left parens to the stack
            // This will signal the beginning of any grouping
            else if (c .equals("("))
                stack.push(c);

            // When a right paren is encountered, we know the grouping has ended
            // Pop from the stack until the group is over
            else if (c .equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("("))
                    output.add(stack.pop());

                stack.pop();
            }

            // If an operator is found, pop off any operators with higher precedence
            else {
                while (!stack.isEmpty() && precedence.get(stack.peek()) != null && precedence.get(c) <= precedence.get(stack.peek())  ) {

                    output.add(stack.pop());
                }
                stack.push(c);
            }
        }

        // pop all the remaining operators from
        // the stack and append them to output
        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }

        return output;
    }
}

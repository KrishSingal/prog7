package assignment;
import java.net.URL;
import java.util.*;

/**
 * A query engine which holds an underlying web index and can answer textual queries with a
 * collection of relevant pages.
 *
 * TODO: Implement this!
 */
public class WebQueryEngine {

    WebIndex index;

    Set<String> terminators = new HashSet<String>(){{
        add("|");
        add("&");
        add("!");
        add("(");
        add(")");
    }};
    Set<String> operators = new HashSet<String>(){{
        add("|");
        add("&");
        add("!");
    }};
    Map<String, Integer> precedence = new HashMap<>(){{
        put("!", 3);
        put("&", 2);
        put("|", 1);
    }};

    public WebQueryEngine(WebIndex index){
        this.index = index;
    }

    /**
     * Returns a WebQueryEngine that uses the given Index to constructe answers to queries.
     *
     * @param index The WebIndex this WebQueryEngine should use.
     * @return A WebQueryEngine ready to be queried.
     */
    public static WebQueryEngine fromIndex(WebIndex index) {
        // TODO: Implement this!
        return new WebQueryEngine(index);
    }

    /**
     * Returns a Collection of URLs (as Strings) of web pages satisfying the query query.
     *
     * @param query A query query.
     * @return A collection of web pages satisfying the query.
     */
    public Collection<Page> query(String query) {
        // TODO: Implement this!
        query = query.toLowerCase();
        ArrayList<String> postfix = postfix(query);
        Stack<HashSet<Page>> ops = new Stack<> ();
        int opcount = 0;

        Set<Page> curr = new HashSet<Page>();

        while(!postfix.isEmpty()){
            String next = postfix.remove(0);
            if(!operators.contains(next)){
                ops.push(new HashSet<Page> (index.query(next)));
            }
            else{
                performOperation(ops, next);
            }
        }

        return ops.pop();
    }

    public void performOperation(Stack<HashSet<Page>> ops, String operation){
        HashSet<Page> set1 = ops.pop();
        HashSet<Page> pushset = new HashSet<>(set1);
        HashSet<Page> set2;

        switch(operation){
            case "&":
                set2 = ops.pop();

                pushset.retainAll(set2);
                ops.push(pushset);
                break;

            case "|":
                set2 = ops.pop();

                pushset.addAll(set2);
                ops.push(pushset);
                break;

            case "!":
                pushset.clear();
                for(Page p: index.pages){
                    if (!set1.contains(p)){
                        pushset.add(p);
                    }
                }
                ops.push(pushset);
                break;
        }
    }


    public ArrayList<String> tokenize(String query){
        ArrayList<String> tokens = new ArrayList<> ();
        int index = 0;
        String last = null;

        while(index < query.length()){
            String c = query.substring(index, index+1);

            if(c.equals(" ")){
                index++;
            }

            else if(terminators.contains(c)) {
                if(c.equals("!") && last != null && !operators.contains(last)){
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

                while(index < query.length() && !query.substring(index, index+1).equals("\"") ){
                    phrase+= query.substring(index, index+1);
                    index++;
                }

                if(last != null && (last.equals(")") || !terminators.contains(last))){
                    tokens.add("&");
                }

                tokens.add(phrase);
                index++;
                last = phrase;
            }

            else{
                String word= "";

                while(index < query.length() && !query.substring(index, index+1).equals(" ") && !terminators.contains(query.substring(index, index+1))){
                    word+= query.substring(index, index+1);
                    index++;
                    //System.out.println(word);
                }

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

    public ArrayList<String> postfix(String query){
        // Initalizing an empty String
        // (for output) and an empty stack
        ArrayList<String> tokens = tokenize(query);
        Stack<String> stack = new Stack<>();
        ArrayList<String> output = new ArrayList<String>();

        // Iterating over tokens using inbuilt
        // .length() function
        for (int i = 0; i < tokens.size(); ++i) {
            // Finding character at 'i'th index
            String c = tokens.get(i);

            // If the scanned Token is an
            // operand, add it to output
            if (!terminators.contains(c))
                output.add(c);

                // If the scanned Token is an '('
                // push it to the stack
            else if (c .equals("("))
                stack.push(c);

                // If the scanned Token is an ')' pop and append
                // it to output from the stack until an '(' is
                // encountered
            else if (c .equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("("))
                    output.add(stack.pop());

                stack.pop();
            }

            // If an operator is encountered then taken the
            // furthur action based on the precedence of the
            // operator

            else {
                while (!stack.isEmpty() && precedence.get(stack.peek()) != null && precedence.get(c) <= precedence.get(stack.peek())  ) {
                    // peek() inbuilt stack function to
                    // fetch the top element(token)

                    output.add(stack.pop());
                }
                stack.push(c);
            }
            System.out.println();
            System.out.println("stack trace: " + stack);
            System.out.println("Queue trace: " + output);
        }

        // pop all the remaining operators from
        // the stack and append them to output
        while (!stack.isEmpty()) {
            if (stack.peek() .equals("("))
                throw new IllegalArgumentException("Invalid query!");
            output.add(stack.pop());
        }


        return output;

    }
}

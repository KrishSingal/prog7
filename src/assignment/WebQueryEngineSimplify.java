package assignment;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.*;

/**
 * A query engine which holds an underlying web index and can answer textual queries with a
 * collection of relevant pages.
 *
 * TODO: Implement this!
 */
public class WebQueryEngineSimplify {

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

    Set<String> andOr = new HashSet<> () {{
        add("&");
        add("|");
    }};

    boolean simplified = false;
    ArrayList<String> simplifiedTokens = new ArrayList<>();

    public WebQueryEngineSimplify(){
        this.index = index;

    }

    public WebQueryEngineSimplify(WebIndex index){
        this.index = index;
    }

    /**
     * Returns a WebQueryEngine that uses the given Index to constructe answers to queries.
     *
     * @param index The WebIndex this WebQueryEngine should use.
     * @return A WebQueryEngine ready to be queried.
     */
    public static WebQueryEngineSimplify fromIndex(WebIndex index) {
        // TODO: Implement this!
        return new WebQueryEngineSimplify(index);
    }

    /**
     * Returns a Collection of URLs (as Strings) of web pages satisfying the query query.
     *
     * @param query A query query.
     * @return A collection of web pages satisfying the query.
     */
    public Collection<Page> query(String query) throws IOException, ClassNotFoundException {
        // TODO: Implement this!
        simplified = false;
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

        ArrayList<Page> ret = new ArrayList<Page>(ops.pop());
        Collections.sort(ret, Collections.reverseOrder());
        return ret;
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


    public ArrayList<String> tokenize(String query) throws IOException, ClassNotFoundException {
        ArrayList<String> tokens = new ArrayList<> ();
        int index = 0;
        String last = null;

        while(index < query.length()){
            String c = query.substring(index, index+1);

            if(c.equals(" ")){
                index++;
            }

            else if(terminators.contains(c)) {
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

                while(index < query.length() && !query.substring(index, index+1).equals("\"") ){
                    phrase+= query.substring(index, index+1);
                    index++;
                }

                if(last != null && (last.equals(")") || !terminators.contains(last))){
                    tokens.add("&");
                }

                phrase = phrase.trim().replaceAll(" +", " ");
                tokens.add(phrase);
                index++;
                last = phrase;
            }

            else{
                String word= "";

                while(index < query.length() && !query.substring(index, index+1).equals(" ")
                        && !query.substring(index, index+1).equals("\"") && !terminators.contains(query.substring(index, index+1))){
                    word+= query.substring(index, index+1);
                    index++;

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


    /**
     * Implements Shunting Yard Algorithm to generate a postfix representation of a given query
     * @param query     Specified query requested
     * @return          Postfix representation of query
     */
    public ArrayList<String> postfix(String query) throws IOException, ClassNotFoundException {

        ArrayList<String> tokens = tokenize(query); // Retrieves tokenized list from helper function

        //tokens = simplifiedTokens = simplify(tokens);
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

    public ArrayList<String> simplify(ArrayList<String> tokens) throws IOException, ClassNotFoundException {

        String newquery = "";
        String last = "";

        for(int i=0; i< tokens.size(); i++){
            String now = tokens.get(i);
            if(now.equals("(") || now.equals(")") || !terminators.contains(now)){
                if(andOr.contains(last))
                    newquery += " ";

                newquery+= now;
                last = now;
            }
            else if (andOr.contains(now)){
                newquery+= " " + now;
                last = now;
            }
            else{
                if(andOr.contains(last))
                    newquery += " ";

                newquery+= now;
                last = now;
            }
        }


        return tokenize(simplifyRec(newquery));
    }

    public String[] getTwoQueries(String query){
        int firstParenIndex = query.indexOf("(");
        int index = firstParenIndex+1;
        int numParens=1;
        String query1= "";
        String query2 = "";
        String operator= "";

        while(index < query.length()){
            String now = query.substring(index, index+1);

            if(now.equals("(")){
                numParens ++;
            }
            else if(now.equals(")")){
                numParens--;
            }

            if(numParens ==1 && andOr.contains(now)){
                operator = now;
                break;
            }
            if(numParens!=0){
                query1+= now;
            }
            index++;
        }

        index++;
        while(index < query.length()){
            String now = query.substring(index, index+1);

            if(now.equals("(")){
                numParens ++;
            }
            else if(now.equals(")")){
                numParens--;
            }

            if(numParens==0 && now.equals(")")){
                break;
            }
            else{
                query2+= now;
            }

            index++;
        }

        String ret[] = {query1.trim(), operator, query2.trim()};

        return ret;
    }

    public String simplifyRec(String query){

        if(query.indexOf("(") < 0){
            return query;
        }

        String parts [] = getTwoQueries(query);


        if(parts[0].equals(parts[2])){

            return simplifyRec(parts[0]);
        }

        return "(" + simplifyRec(parts[0])+ " " + parts[1] + " " + simplifyRec(parts[2]) + ")";
    }
}



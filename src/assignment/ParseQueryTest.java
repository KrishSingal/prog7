package assignment;

import java.util.*;

public class ParseQueryTest {
    static Set<String> terminators = new HashSet<String>(){{
        add("|");
        add("&");
        add("!");
        add("(");
        add(")");
    }};
    static Set<String> operators = new HashSet<String>(){{
        add("|");
        add("&");
        add("!");
    }};
    static Map<String, Integer> precedence = new HashMap<>(){{
        put("!", 3);
        put("&", 2);
        put("|", 1);
    }};

    public static void main(String args[]){
        System.out.println(terminators);
        System.out.println(operators);
        System.out.println(precedence);

        /*
        System.out.println(tokenize("snuffles"));
        System.out.println(tokenize("(!snuffles & sniffles)"));
        System.out.println(tokenize("(!(snuffles & sniffles) | gif)"));
        System.out.println(tokenize("!(snuffles & sniffles) | gif"));
        System.out.println(tokenize("!(\"snuffles are fun\" & sniffles) | gif"));
        */
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
    }

    public static ArrayList<String> tokenize(String query){
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

    public static ArrayList<String> postfix(ArrayList<String> tokens){
        // Initalizing an empty String
        // (for output) and an empty stack
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


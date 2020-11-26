package assignment;

import assignment.Index;

import java.io.*;
import java.util.StringTokenizer;
import java.io.*;
import java.util.StringTokenizer;
import java.util.*;

public class wordFreq {

    public static void main(String args[]) throws IOException {
        HashSet<String> dict = new HashSet<String>();

        BufferedReader t = new BufferedReader(new FileReader("/Users/Krish/Desktop/words.txt"));
        String line;

        while((line = t.readLine()) != null) {
            dict.add(line);
        }

        HashMap<String, Long> freqs = new HashMap<String, Long>();

        BufferedReader f = new BufferedReader(new FileReader("/Users/Krish/Desktop/unigram_freq.csv"));
        //BufferedReader f = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer k = new StringTokenizer(f.readLine());


        while((line = f.readLine()) != null){
            k = new StringTokenizer(line);
            String[] items = line.split(",");
            String word = items[0];
            long freq = Long.parseLong(items[1]);

            if(dict.contains(word)) {
                freqs.put(word, freq);
            }

        }

        try(ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream("frequencies.db"))) {
            oout.writeObject(freqs);
        }
        //System.out.println(freqs);
    }
}

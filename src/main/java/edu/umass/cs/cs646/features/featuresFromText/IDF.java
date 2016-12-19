package edu.umass.cs.cs646.features.featuresFromText;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Valar Dohaeris on 12/14/16.
 */
public class IDF {

    public static double getWeight(IndexReader index, HashMap<String,Double> corpusStats, String term) throws IOException {
        int N = index.numDocs();
        double n = corpusStats.get(term);
        return (float) Math.log((N + 0.5) / (n + 0.5));
    }

    public static double getSumWeight(IndexReader index, HashMap<String,Double> corpusStats, List<String> terms) throws IOException {

        double totalTf = 0;
        for (String term : terms) {
            totalTf += getWeight(index, corpusStats, term);
        }
        return totalTf;
    }
}

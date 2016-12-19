package edu.umass.cs.cs646.features;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

import java.io.IOException;
import java.util.List;

/**
 * Valar Dohaeris on 11/26/16.
 */
public final class IDF {


    /**
     * The original IDF with 0.5 smoothing.
     */
    public static double getWeight(IndexReader index, String field, String term) throws IOException {
        int N = index.numDocs();
        int n = index.docFreq(new Term(field, term));
        return (float) Math.log((N + 0.5) / (n + 0.5));
    }


    public static double getSumWeight(IndexReader index, String field, List<String> terms) throws IOException {

        double totalTf = 0;
        for (String term : terms) {
            totalTf += getWeight(index, field, term);
        }
        return totalTf;
    }
}

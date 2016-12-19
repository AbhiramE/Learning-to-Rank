package edu.umass.cs.cs646.features.featuresFromText;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.List;

/**
 * Valar Dohaeris on 11/30/16.
 */

public final class BM25 {

    public static double k1;

    private static double getWeight(String text, String term)
            throws IOException {
        double tf=TF.getRawWeight(text,term);
        double numerator = (k1 + 1) * tf;
        double denominator = k1 + tf;

        return numerator / denominator;
    }

    public static double getSumWeight(String text, List<String> terms) throws IOException
    {
        double score=0;
        for (String term:terms)
            score+=getWeight(text,term);

        return score;
    }
}

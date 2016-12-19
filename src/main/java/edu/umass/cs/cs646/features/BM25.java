package edu.umass.cs.cs646.features;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.List;

/**
 * Valar Dohaeris on 11/30/16.
 */

public final class BM25{

    public static double k1=1.2;

    private static double getWeight(IndexReader index, PostingsEnum posting, String field, String term)
            throws IOException {
        double numerator = (k1 + 1) * posting.freq();
        double denominator = k1 + posting.freq();

        return numerator / denominator;
    }

    public static double getSumWeight(IndexReader index,String field, List<String> terms) throws IOException
    {
        double score=0;
        for (String term:terms)
        {
            PostingsEnum posting= MultiFields.getTermDocsEnum( index, field, new BytesRef( term ), PostingsEnum.FREQS );
            score+=getWeight(index,posting,field,term);
        }

        return score;
    }
}

package edu.umass.cs.cs646.features;

import org.apache.lucene.index.Term;

import java.util.List;

/**
 * Valar Dohaeris on 11/30/16.
 */
public final class QueryTerm {

    public static double getQueryTermNumber(List<String> terms)
    {
        return terms.size();
    }

    public static double getQueryTermRatio(List<String> terms)
    {
        return 1/terms.size();
    }
}

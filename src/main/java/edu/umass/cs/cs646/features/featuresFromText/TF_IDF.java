package edu.umass.cs.cs646.features.featuresFromText;


import org.apache.commons.math3.stat.StatUtils;
import org.apache.lucene.index.IndexReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Valar Dohaeris on 12/18/16.
 */
public final class TF_IDF {

    public static double getWeight(IndexReader index,String text, HashMap<String,Double> corpusStats, String term, 
                                   double length) throws IOException {
        return NormalizedTF.getWeight(text,term,length) * IDF.getWeight(index,corpusStats,term);
    }

    public static double getSumWeight(IndexReader index, String text, HashMap<String,Double> corpusStats, List<String> terms,
                                      Double length) throws IOException {

        double sumTfIdf=0;

        for (String term:terms)
        {
            sumTfIdf+=getWeight(index,text,corpusStats,term,length);
        }
        return sumTfIdf;
    }

    public static double getMinWeight(IndexReader index,String text, HashMap<String,Double> corpusStats, List<String> terms,
                                      Double length) throws IOException {

        double minTfIdf=Double.MAX_VALUE;

        for (String term:terms)
        {
            double tfIdf;
            if(minTfIdf < (tfIdf=getWeight(index,text,corpusStats,term,length)))
                minTfIdf=tfIdf;
        }
        return minTfIdf;
    }

    public static double getMaxWeight(IndexReader index,String text, HashMap<String,Double> corpusStats, List<String> terms,
                                      Double length) throws IOException {

        double maxTfIdf=Double.MIN_VALUE;

        for (String term:terms)
        {
            double tfIdf;
            if(maxTfIdf > (tfIdf=getWeight(index,text,corpusStats,term,length)))
                maxTfIdf=tfIdf;
        }
        return maxTfIdf;
    }

    public static double getMeanWeight(IndexReader index,String text, HashMap<String,Double> corpusStats, List<String> terms,
                                       Double length) throws IOException {

        double sumTfIdf=0;

        for (String term:terms)
        {
            sumTfIdf+=getWeight(index,text,corpusStats,term,length);
        }
        return sumTfIdf/terms.size();
    }

    public static double getVarianceWeight(IndexReader index,String text, HashMap<String,Double> corpusStats,
                                           List<String> terms, Double length) throws IOException {

        double[] tfIdfs=new double[terms.size()];

        for (int i=0;i<terms.size();i++)
        {
            tfIdfs[i]=getWeight(index,text,corpusStats,terms.get(i),length);
        }
        return StatUtils.variance(tfIdfs);
    }
}

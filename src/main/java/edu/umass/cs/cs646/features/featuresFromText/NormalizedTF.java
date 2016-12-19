package edu.umass.cs.cs646.features.featuresFromText;

import org.apache.commons.math3.stat.StatUtils;

import java.io.IOException;
import java.util.List;

/**
 * Valar Dohaeris on 12/18/16.
 */
public final class NormalizedTF{

    protected static double base;

    public NormalizedTF( double base ) {
        NormalizedTF.base = base;
    }

    public static double getWeight(String text,String term, double length) throws IOException {
        return TF.getRawWeight(text,term)/length;
    }

    public static double getSumWeight(String text, List<String> terms, double length) throws IOException {

        double sumTf=0;

        for (String term:terms)
        {
            sumTf+=getWeight(text,term,length);
        }
        return sumTf;
    }

    public static double getMinWeight(String text,List<String> terms, double length) throws IOException {

        double minTf=Double.MAX_VALUE;

        for (String term:terms)
        {
            double tf;
            if(minTf < (tf=getWeight(text,term,length)))
                minTf=tf;
        }
        return minTf;
    }

    public static double getMaxWeight(String text, List<String> terms, double length) throws IOException {

        double maxTf=Double.MIN_VALUE;

        for (String term:terms)
        {
            double tf;
            if(maxTf > (tf=getWeight(text,term,length)))
                maxTf=tf;
        }
        return maxTf;
    }

    public static double getMeanWeight(String text, List<String> terms, double length) throws IOException {

        double sumTf=0;

        for (String term:terms)
        {
            sumTf+=getWeight(text,term,length);;
        }
        return sumTf/terms.size();
    }

    public static double getVarianceWeight(String text,List<String> terms, double length) throws IOException {

        double[] tfs=new double[terms.size()];

        for (int i=0;i<terms.size();i++)
        {
            tfs[i]=getWeight(text,terms.get(i),length);;
        }
        return StatUtils.variance(tfs);
    }
}


package edu.umass.cs.cs646.features.featuresFromText;
import org.apache.commons.math3.stat.StatUtils;

import java.io.IOException;
import java.util.List;

/**
 * Valar Dohaeris on 12/7/16.
 */
public final class TF {

    public static double getRawWeight(String text, String term)
    {
        if (text.contains(term))
        {
            int lastIndex=0;
            int count=0;

            while( lastIndex != -1){
                lastIndex = text.indexOf(term,lastIndex);
                if(lastIndex != -1){
                    count ++;
                    lastIndex += term.length();
                }
            }
            return count;
        }else return 0;
    }

    public static double getSumWeight(String text, List<String> terms) throws IOException {

        double sumTf=0;

        for (String term:terms)
        {
            sumTf+=getRawWeight(text,term);
        }
        return sumTf;
    }

    public static double getMinWeight(String text, List<String> terms) throws IOException {

        double minTf=Double.MAX_VALUE;

        for (String term:terms)
        {
            double tf;
            if(minTf < (tf=getRawWeight(text,term)))
                minTf=tf;
        }
        return minTf;
    }

    public static double getMaxWeight(String text, List<String> terms) throws IOException {

        double maxTf=Double.MIN_VALUE;

        for (String term:terms)
        {
            double tf;
            if(maxTf > (tf=getRawWeight(text,term)))
                maxTf=tf;
        }
        return maxTf;
    }

    public static double getMeanWeight(String text, List<String> terms) throws IOException {

        double sumTf=0;

        for (String term:terms)
        {
            sumTf+=getRawWeight(text,term);
        }
        return sumTf/terms.size();
    }

    public static double getVarianceWeight(String text, List<String> terms) throws IOException {

        double[] tfs=new double[terms.size()];

        for (int i=0;i<terms.size();i++)
        {
            tfs[i]=getRawWeight(text,terms.get(i));
        }
        return StatUtils.variance(tfs);
    }
}

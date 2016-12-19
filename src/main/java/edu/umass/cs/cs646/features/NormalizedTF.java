package edu.umass.cs.cs646.features;

/**
 * Valar Dohaeris on 11/26/16.
 */

import edu.umass.cs.cs646.project.Evaluation;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.List;

/**
 * Implement your Normalized TF function here.
 */
public final class NormalizedTF{

    protected static double base;

    public NormalizedTF( double base ) {
        NormalizedTF.base = base;
    }

    public static double getWeight(IndexReader index, PostingsEnum posting, String field, String term,
                                   double length) throws IOException {
        if(posting.freq() > 0)
            return posting.freq()/ length;
        else
            return 0;
    }

    public static double getSumWeight(IndexReader index,String field, List<String> terms, double length) throws IOException {

        double sumTf=0;

        for (String term:terms)
        {
            PostingsEnum posting= MultiFields.getTermDocsEnum( index, field, new BytesRef( term ), PostingsEnum.FREQS );
            sumTf+=getWeight(index,posting,field,term,length);
        }
        return sumTf;
    }

    public static double getMinWeight(IndexReader index,String field, List<String> terms, double length) throws IOException {

        double minTf=Double.MAX_VALUE;

        for (String term:terms)
        {
            double tf;
            PostingsEnum posting=MultiFields.getTermDocsEnum( index, field, new BytesRef( term ), PostingsEnum.FREQS );
            if(minTf < (tf=getWeight(index,posting,field,term,length)))
                minTf=tf;
        }
        return minTf;
    }

    public static double getMaxWeight(IndexReader index,String field, List<String> terms, double length) throws IOException {

        double maxTf=Double.MIN_VALUE;

        for (String term:terms)
        {
            double tf;
            PostingsEnum posting=MultiFields.getTermDocsEnum( index, field, new BytesRef( term ), PostingsEnum.FREQS );
            if(maxTf > (tf=getWeight(index,posting,field,term,length)))
                maxTf=tf;
        }
        return maxTf;
    }

    public static double getMeanWeight(IndexReader index,String field,
                                       List<String> terms, double length) throws IOException {

        double sumTf=0;

        for (String term:terms)
        {
            PostingsEnum posting=MultiFields.getTermDocsEnum( index, field, new BytesRef( term ), PostingsEnum.FREQS );
            sumTf+=getWeight(index,posting,field,term,length);
        }
        return sumTf/terms.size();
    }

    public static double getVarianceWeight(IndexReader index,String field,
                                           List<String> terms, double length) throws IOException {

        double[] tfs=new double[terms.size()];

        for (int i=0;i<terms.size();i++)
        {
            PostingsEnum posting=MultiFields.getTermDocsEnum( index, field, new BytesRef( terms.get(i) ),
                    PostingsEnum.FREQS );
            tfs[i]=getWeight(index,posting,field,terms.get(i),length);
        }
        return StatUtils.variance(tfs);
    }
}

package edu.umass.cs.cs646.features;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.List;

/**
 * Valar Dohaeris on 11/26/16.
 */
public final class TF {

    public static double getRawWeight(IndexReader index, PostingsEnum posting, String field, String term) throws IOException {
        return posting.freq();
    }


    public static double getSumWeight(IndexReader index,  String field, List<String> terms) throws IOException {

        double sumTf=0;

        for (String term:terms)
        {
            PostingsEnum posting=MultiFields.getTermDocsEnum( index, field, new BytesRef( term ), PostingsEnum.FREQS );
            sumTf+=getRawWeight(index,posting,field,term);
        }
        return sumTf;
    }

    public static double getMinWeight(IndexReader index,  String field, List<String> terms) throws IOException {

        double minTf=Double.MAX_VALUE;

        for (String term:terms)
        {
            double tf;
            PostingsEnum posting=MultiFields.getTermDocsEnum( index, field, new BytesRef( term ), PostingsEnum.FREQS );
            if(minTf < (tf=getRawWeight(index,posting,field,term)))
                minTf=tf;
        }
        return minTf;
    }

    public static double getMaxWeight(IndexReader index,  String field, List<String> terms) throws IOException {

        double maxTf=Double.MIN_VALUE;

        for (String term:terms)
        {
            double tf;
            PostingsEnum posting=MultiFields.getTermDocsEnum( index, field, new BytesRef( term ), PostingsEnum.FREQS );
            if(maxTf > (tf=getRawWeight(index,posting,field,term)))
                maxTf=tf;
        }
        return maxTf;
    }

    public static double getMeanWeight(IndexReader index,  String field, List<String> terms) throws IOException {

        double sumTf=0;

        for (String term:terms)
        {
            PostingsEnum posting=MultiFields.getTermDocsEnum( index, field, new BytesRef( term ), PostingsEnum.FREQS );
            sumTf+=getRawWeight(index,posting,field,term);
        }
        return sumTf/terms.size();
    }

    public static double getVarianceWeight(IndexReader index,  String field, List<String> terms) throws IOException {

        double[] tfs=new double[terms.size()];

        for (int i=0;i<terms.size();i++)
        {
            PostingsEnum posting=MultiFields.getTermDocsEnum( index, field, new BytesRef( terms.get(i) ), PostingsEnum.FREQS );
            tfs[i]=getRawWeight(index,posting,field,terms.get(i));
        }
        return StatUtils.variance(tfs);
    }

}

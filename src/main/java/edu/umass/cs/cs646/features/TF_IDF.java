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
public final class TF_IDF {

    public static double getWeight(IndexReader index, PostingsEnum posting, String field, String term, double length) throws IOException {
        return NormalizedTF.getWeight(index,posting,field,term,length) * IDF.getWeight(index,field,term);
    }

    public static double getSumWeight(IndexReader index,String field, List<String> terms, Double length) throws IOException {

        double sumTfIdf=0;

        for (String term:terms)
        {
            PostingsEnum posting= MultiFields.getTermDocsEnum( index, field, new BytesRef( term ), PostingsEnum.FREQS );
            sumTfIdf+=getWeight(index,posting,field,term,length);
        }
        return sumTfIdf;
    }

    public static double getMinWeight(IndexReader index,String field, List<String> terms, Double length) throws IOException {

        double minTfIdf=Double.MAX_VALUE;

        for (String term:terms)
        {
            double tfIdf;
            PostingsEnum posting= MultiFields.getTermDocsEnum( index, field, new BytesRef( term ), PostingsEnum.FREQS );
            if(minTfIdf < (tfIdf=getWeight(index,posting,field,term,length)))
                minTfIdf=tfIdf;
        }
        return minTfIdf;
    }

    public static double getMaxWeight(IndexReader index,String field, List<String> terms, Double length) throws IOException {

        double maxTfIdf=Double.MIN_VALUE;

        for (String term:terms)
        {
            double tfIdf;
            PostingsEnum posting= MultiFields.getTermDocsEnum( index, field, new BytesRef( term ), PostingsEnum.FREQS );
            if(maxTfIdf > (tfIdf=getWeight(index,posting,field,term,length)))
                maxTfIdf=tfIdf;
        }
        return maxTfIdf;
    }

    public static double getMeanWeight(IndexReader index,String field, List<String> terms, Double length) throws IOException {

        double sumTfIdf=0;

        for (String term:terms)
        {
            PostingsEnum posting= MultiFields.getTermDocsEnum( index, field, new BytesRef( term ), PostingsEnum.FREQS );
            sumTfIdf+=getWeight(index,posting,field,term,length);
        }
        return sumTfIdf/terms.size();
    }

    public static double getVarianceWeight(IndexReader index,String field, List<String> terms, Double length) throws IOException {

        double[] tfIdfs=new double[terms.size()];

        for (int i=0;i<terms.size();i++)
        {
            PostingsEnum posting= MultiFields.getTermDocsEnum( index, field, new BytesRef( terms.get(i) ), PostingsEnum.FREQS );
            tfIdfs[i]=getWeight(index,posting,field,terms.get(i),length);
        }
        return StatUtils.variance(tfIdfs);
    }
}

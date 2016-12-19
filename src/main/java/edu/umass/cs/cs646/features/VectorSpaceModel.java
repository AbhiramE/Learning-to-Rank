package edu.umass.cs.cs646.features;

import edu.umass.cs.cs646.utils.LuceneUtils;
import edu.umass.cs.cs646.utils.SearchResult;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Valar Dohaeris on 11/30/16.
 */
public final class VectorSpaceModel {

    public static List<SearchResult> searchVSMCosine(IndexReader index, String field, List<String> queryTerms,int top) throws IOException {

        List<String> uniqueQueryTerms = queryTerms.stream().distinct().collect(Collectors.toList());
        List<Integer> queryTermFrequencies = new ArrayList<>();
        List<List<Double>> documentTermFrequencies = intialiseDfs(index,queryTerms);
        List<Double> sumOfDocumentTermFrequenciesSquare = initialiseList(index,new ArrayList<>());

        //Compute Query Frequencies if there are duplicate terms in query
        if (uniqueQueryTerms.size() < queryTerms.size()) {
            for (int i = 0; i < uniqueQueryTerms.size(); i++) {
                String uniqueTerm = uniqueQueryTerms.get(i);
                int count = 0;
                for (String queryTerm : queryTerms) {
                    if (uniqueTerm.equals(queryTerm))
                        count++;
                }
                queryTermFrequencies.add(i, count);
            }
        } else
            queryTermFrequencies.addAll(uniqueQueryTerms.stream().map(uniqueTerm -> 1).collect(Collectors.toList()));


        //Find total term frequency of each query term in a doc
        for (int i=0;i<queryTerms.size();i++) {
            PostingsEnum posting = MultiFields.getTermDocsEnum(index, field,
                    new BytesRef(queryTerms.get(i)), PostingsEnum.FREQS);
            if (posting != null) {
                int docid;
                while ((docid = posting.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
                    int frequency = posting.freq();
                    documentTermFrequencies.get(docid).set(i,(double)frequency);
                }
            }
        }

        for(int i=0;i<index.numDocs();i++) {
            double doclen=0;
            if (index.getTermVector(i, field) != null) {
                TermsEnum termsEnum = index.getTermVector(i, field).iterator();
                while (termsEnum.next() != null) {
                    doclen += Math.pow(termsEnum.totalTermFreq(),2);
                }
            }
            sumOfDocumentTermFrequenciesSquare.set(i,doclen);
        }

        //Find the sum of query frequencies
        double sumOfQueryTermFrequencies = 0;
        for (int queryTf : queryTermFrequencies)
            sumOfQueryTermFrequencies += Math.pow(queryTf, 2);

        List<Double> scores = initialiseList(index,new ArrayList<>());

        //Computer Cosine Similarity for all docs
        for (int i = 0; i < top; i++) {
            double product = 0;
            for (int j = 0; j < uniqueQueryTerms.size(); j++) {
                product += queryTermFrequencies.get(j) * documentTermFrequencies.get(i).get(j);
            }

            if(sumOfDocumentTermFrequenciesSquare.get(i)>0) {
                double denominator=Math.sqrt(sumOfDocumentTermFrequenciesSquare.get(i)) * Math.sqrt(sumOfQueryTermFrequencies);
                double score=product / denominator;
                scores.set(i,score);
            }
        }

        List<SearchResult> results = new ArrayList<>();
        for (int i = 0; i < index.maxDoc(); i++) {
            results.add(new SearchResult(i,
                    LuceneUtils.getDocno(index, "docno", i),
                    scores.get(i)));
        }

        //Sort
        results.sort((a, b) -> b.getScore().compareTo(a.getScore()));

        return results;
    }

    private static List<Double> initialiseList(IndexReader index,List<Double> scores)
    {
        for(int i=0;i<index.maxDoc();i++)
        {
            scores.add((double)0);
        }
        return scores;
    }

    private static List<List<Double>> intialiseDfs(IndexReader index,List<String> queryTerms){
        List<List<Double>> dfs=new ArrayList<>();
        for(int i=0;i<index.maxDoc();i++) {
            List<Double> list = queryTerms.stream().map(queryTerm -> (double) 0).collect(Collectors.toList());
            dfs.add(list);
        }
        return dfs;
    }

}

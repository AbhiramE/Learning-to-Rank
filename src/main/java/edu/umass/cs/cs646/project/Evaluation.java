package edu.umass.cs.cs646.project;

import edu.umass.cs.cs646.features.*;
import edu.umass.cs.cs646.utils.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Valar Dohaeris on 11/26/16.
 */
public class Evaluation extends AbstractQLSearcher{

    public static double getLength(String pathIndex, int docId, String search_field) throws IOException {
        LuceneQLSearcher searcher = new LuceneQLSearcher( pathIndex );
        String content = searcher.index.document( docId ).get( search_field );
        return content.length();
    }

    public static void main(String[] args)
    {
        String pathIndex = "/home/abhis3798/codebase/646/Project/index_robust04";
        Analyzer analyzer = LuceneUtils.getAnalyzer(LuceneUtils.Stemming.Krovetz);

        String pathQueries = "/home/abhis3798/codebase/646/Project/queries_robust04_validate";
        String pathQrels = "/home/abhis3798/codebase/646/Project/qrels_robust04";
        String pathStopwords = "/home/abhis3798/codebase/646/Project/stopwords_inquery";
        String outputPath = "/home/abhis3798/codebase/646/Project/robust04_data_validate";

        String field_docno = "docno";

        List<String> fields=new ArrayList<>();
        fields.add("body");
        fields.add("anchor");
        fields.add("title");
        fields.add("url");
        fields.add("content");


        try {

            Map<String, String> queries = EvalUtils.loadQueries(pathQueries);
            Map<String, Set<String>> qrels = EvalUtils.loadQrels(pathQrels);

            Evaluation searcher = new Evaluation(pathIndex);
            searcher.setStopwords(pathStopwords);

            BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));
            int top=1000;

            int ix = 0;
            for (String qid : queries.keySet()) {

                String query = queries.get(qid);
                List<String> terms = LuceneUtils.tokenize(query, analyzer);

                Hashtable<String,List<SearchResult>> searchResultsDIR=new Hashtable<>();
                Hashtable<String,List<SearchResult>> searchResultsVSM=new Hashtable<>();
                Hashtable<String,List<SearchResult>> searchResultsJM=new Hashtable<>();
                HashMap<String,List<Integer>> docIdsMap=new HashMap<>();


                for (int i=0;i<5;i++) {
                    String field_search = fields.get(i);


                    if(MultiFields.getTerms(searcher.getIndex(),field_search)!=null)
                    {
                        double mu=1000;
                        LuceneQLSearcher qlSearcher = new LuceneQLSearcher(pathIndex);
                        searchResultsDIR.put(field_search,qlSearcher.search(field_search,terms,mu,top));
                        SearchResult.dumpDocno(searcher.getIndex(),field_docno,searchResultsDIR.get(field_search));

                        double lamda=0.4;
                        QLJMSmoothing qljmSmoothing=new QLJMSmoothing(pathIndex);
                        searchResultsJM.put(field_search,qljmSmoothing.search(field_search,terms,top,lamda,pathStopwords));
                    }
                    docIdsMap=dumpDocIds(searchResultsDIR);

                    if(MultiFields.getTerms(searcher.getIndex(),field_search)!=null)
                    {
                        searchResultsVSM.put(field_search,
                                VectorSpaceModel.searchVSMCosine(searcher.index,field_search,terms,top));
                    }
                }



                for (int d=0;d<1000 && d< docIdsMap.get(fields.get(4)).size();d++) {

                    String data="";

                    //Feature 1-5
                    for (int i = 1; i < 6; i++) {

                        String field_search = fields.get((i-1)%5);

                        int target=0;
                        if(searchResultsDIR.get(fields.get(4)).get(d).getDocno()!=null)
                            target=qrels.get(qid).contains(searchResultsDIR.get(fields.get(4)).get(d).getDocno())?1:0;

                        data=data.concat(String.valueOf(target)+" ");
                        data=data.concat("qid:"+qid+" ");

                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null)
                            score=QueryTerm.getQueryTermNumber(terms);
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 6-10
                    for (int i = 6; i < 11; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null)
                            score=QueryTerm.getQueryTermRatio(terms);
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 11-15
                    for (int i = 11; i < 16; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null)
                            score=getLength(pathIndex,docIdsMap.get(field_search).get(d),field_search);
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 16-20
                    for (int i = 16; i < 21; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null)
                            score=IDF.getSumWeight(searcher.index,field_search,terms);
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 21-25
                    for (int i = 21; i < 26; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            score = TF.getSumWeight(searcher.index,field_search, terms);
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 26-30
                    for (int i = 26; i < 31; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            score = TF.getMinWeight(searcher.index,field_search, terms);
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 31-35
                    for (int i = 31; i < 36; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            score = TF.getMaxWeight(searcher.index,field_search, terms);
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 36-40
                    for (int i = 36; i < 41; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            score = TF.getMeanWeight(searcher.index,field_search, terms);
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 41-45
                    for (int i = 41; i < 46; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            score = TF.getVarianceWeight(searcher.index,field_search, terms);
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 46-50
                    for (int i = 46; i < 51; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            double length=getLength(pathIndex,d,field_search);
                            score = NormalizedTF.getSumWeight(searcher.index,field_search, terms,length);
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 51-55
                    for (int i = 51; i < 56; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            double length=getLength(pathIndex,d,field_search);
                            score = NormalizedTF.getMinWeight(searcher.index,field_search, terms,length);
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 56-60
                    for (int i = 56; i < 61; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            double length=getLength(pathIndex,d,field_search);
                            score = NormalizedTF.getMaxWeight(searcher.index,field_search, terms,length);
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 61-65
                    for (int i = 61; i < 66; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            double length=getLength(pathIndex,d,field_search);
                            score = NormalizedTF.getMeanWeight(searcher.index,field_search, terms,length);
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 66-70
                    for (int i = 66; i < 71; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            double length=getLength(pathIndex,d,field_search);
                            score = NormalizedTF.getVarianceWeight(searcher.index,field_search, terms,length);
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 71-75
                    for (int i = 71; i < 76; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            double length=getLength(pathIndex,d,field_search);
                            score = TF_IDF.getSumWeight(searcher.index,field_search, terms,length);
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 76-80
                    for (int i = 76; i < 81; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            double length=getLength(pathIndex,d,field_search);
                            score = TF_IDF.getMinWeight(searcher.index,field_search, terms,length);
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 81-85
                    for (int i = 81; i < 86; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            double length=getLength(pathIndex,d,field_search);
                            score = TF_IDF.getMaxWeight(searcher.index,field_search, terms,length);
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 86-90
                    for (int i = 86; i < 91; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            double length=getLength(pathIndex,d,field_search);
                            score = TF_IDF.getMeanWeight(searcher.index,field_search, terms,length);
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 91-95
                    for (int i = 91; i < 96; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            double length=getLength(pathIndex,d,field_search);
                            score = TF_IDF.getVarianceWeight(searcher.index,field_search, terms,length);
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 96-100
                    for (int i = 96; i < 101; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            score=BooleanModel.getScore();
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 101-105
                    for (int i = 101; i < 106; i++) {
                        String field_search = fields.get((i-1)%5);
                        List<SearchResult> scoresVSM=searchResultsVSM.get(field_search);

                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(),field_search) != null) {
                            for (SearchResult scoreVSM:scoresVSM)
                            {
                                if(scoreVSM.getDocid()==docIdsMap.get(field_search).get(d))
                                {
                                    score=scoreVSM.getScore();
                                    break;
                                }
                            }
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 106-110
                    for (int i = 106; i < 111; i++) {
                        String field_search = fields.get((i-1)%5);
                        double score = 0;
                        BM25.k1=1.2;
                        if (MultiFields.getTerms(searcher.getIndex(), field_search) != null) {
                            score=BM25.getSumWeight(searcher.getIndex(),field_search,terms);
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 116-120
                    for (int i = 116; i < 121; i++) {
                        String field_search = fields.get((i-1)%5);
                        List<SearchResult> scoresDIR=searchResultsDIR.get(field_search);

                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(),field_search) != null) {
                            for (SearchResult scoreDIR:scoresDIR)
                            {
                                if(scoreDIR.getDocid()==docIdsMap.get(field_search).get(d))
                                {
                                    score=scoreDIR.getScore();
                                    break;
                                }
                            }
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Feature 121-125
                    for (int i = 121; i < 126; i++) {
                        String field_search = fields.get((i-1)%5);
                        List<SearchResult> scoresJM=searchResultsJM.get(field_search);

                        double score = 0;
                        if (MultiFields.getTerms(searcher.getIndex(),field_search) != null) {
                            for (SearchResult scoreJM:scoresJM)
                            {
                                if(scoreJM.getDocid()==docIdsMap.get(field_search).get(d))
                                {
                                    score=scoreJM.getScore();
                                    break;
                                }
                            }
                        }
                        data=data.concat(i+":"+score+" ");
                    }

                    //Post Features Addition
                    String field_search = fields.get(4);
                    data=data.concat("#docid = "+searchResultsDIR.get(field_search).get(d).getDocno());

                    bw.write(data);
                    bw.newLine();
                }
                System.out.println(qid);
            }

                bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected File dirBase;
    protected Directory dirLucene;
    public IndexReader index;
    public Map<String, DocLengthReader> doclens;

    public Evaluation(String dirPath) throws IOException {
        this(new File(dirPath));
    }

    public Evaluation(File dirBase) throws IOException {
        this.dirBase = dirBase;
        this.dirLucene = FSDirectory.open(this.dirBase.toPath());
        this.index = DirectoryReader.open(dirLucene);
        this.doclens = new HashMap<>();
    }

    public IndexReader getIndex() {
        return this.index;
    }

    public PostingList getPosting(String field, String term) throws IOException {
        return new LuceneTermPostingList(index, field, term);
    }

    public DocLengthReader getDocLengthReader(String field) throws IOException {
        DocLengthReader doclen = doclens.get(field);
        if (doclen == null) {
            doclen = new FileDocLengthReader(this.dirBase, field);
            doclens.put(field, doclen);
        }
        return doclen;
    }

    public void close() throws IOException {
        index.close();
        dirLucene.close();
        for (DocLengthReader doclen : doclens.values()) {
            doclen.close();
        }
    }

    public static HashMap<String,List<Integer>> dumpDocIds(Hashtable<String,List<SearchResult>> searchResults)
    {
        HashMap<String,List<Integer>> results=new HashMap<>();
        for (String field:searchResults.keySet())
        {
            List<Integer> docIds=new ArrayList<>();
            for (SearchResult list:searchResults.get(field))
            {
                docIds.add(list.getDocid());
            }
            results.put(field,docIds);
        }
        return results;
    }
}

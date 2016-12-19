package edu.umass.cs.cs646.project;

import edu.umass.cs.cs646.utils.EvalUtils;
import edu.umass.cs.cs646.utils.LuceneQLSearcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Valar Dohaeris on 12/14/16.
 */
public class ComputeWT10GCorpusStats {

    public static void main(String args[]) throws Exception
    {
        String pathIndex = "/home/abhis3798/codebase/646/Project/index_wt10g";
        String pathQueries = "/home/abhis3798/codebase/646/Project/queries_wt10g_train";
        String pathQrels = "/home/abhis3798/codebase/646/Project/qrels_wt10g";
        String outputPath=  "/home/abhis3798/codebase/646/Project/corpus_stats_wt10g_body";

        Map<String, String> queries = EvalUtils.loadQueries(pathQueries);
        HashMap<String,Double> words=new HashMap<>();

        for (String qId:queries.keySet())
        {
            for (String term:queries.get(qId).split(" "))
                words.put(term, (double) 0);
        }

        LuceneQLSearcher searcher = new LuceneQLSearcher( pathIndex );
        for (double i=0; i<searcher.getIndex().maxDoc(); i++)
        {
            String html = searcher.index.document( (int) i ).get( "html" );
            Document doc = Jsoup.parseBodyFragment(html);
            String text=doc.body().text();
            for (String term:words.keySet())
            {
                if(text.contains(term))
                {
                    words.put(term,words.get(term)+1);
                }
            }
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));
        for (String term:words.keySet()) {
            String line=term+" "+words.get(term);
            bw.write(line);
            bw.newLine();
        }
        bw.close();
    }
}

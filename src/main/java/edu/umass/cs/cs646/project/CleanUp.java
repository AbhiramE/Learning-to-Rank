package edu.umass.cs.cs646.project;

import edu.umass.cs.cs646.features.QueryTerm;
import edu.umass.cs.cs646.utils.EvalUtils;
import org.apache.lucene.index.MultiFields;

import java.io.*;
import java.util.Map;
import java.util.Set;

/**
 * Valar Dohaeris on 12/19/16.
 */
public class CleanUp {

    public static void main(String []args) {

        try{
        String pathQueries = "/home/abhis3798/codebase/646/Project/queries_trec1-3";
        String pathQrels = "/home/abhis3798/codebase/646/Project/qrels_trec1-3";
        String outputPath = "/home/abhis3798/codebase/646/Project/trec-train_final";
        String inputPath = "/home/abhis3798/codebase/646/Project/trec-train.txt";

        Map<String, String> queries = EvalUtils.loadQueries(pathQueries);

        BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));
        BufferedReader br = new BufferedReader(new FileReader(inputPath));

        String line;
        while ((line=br.readLine())!=null) {
                    String[] array=line.split(" ");
                    String qid=array[1].replace("qid:","");
                    String finalLine = "";
                    String deadline = "";
                    int target = Integer.parseInt(String.valueOf(line.charAt(0)));

                    for (int j = 1; j <= 4; j++) {
                        double score = 0;
                        finalLine = finalLine.concat(String.valueOf(target) + " ");
                        finalLine = finalLine.concat("qid:" + qid + " ");

                        if (j == 1) {
                            deadline = deadline.concat(String.valueOf(target) + " ");
                            deadline = deadline.concat("qid:" + qid + " ");
                        }

                        finalLine = finalLine.concat(j + ":" + score + " ");
                        deadline = deadline.concat(j + ":" + score + " ");
                    }

                    finalLine = finalLine.concat(String.valueOf(target) + " ");
                    finalLine = finalLine.concat("qid:" + qid + " ");

                    String replaced = line.replaceAll(finalLine, deadline);
                    bw.write(replaced);
                    bw.newLine();
            }
        bw.close();
        br.close();
    }catch (Exception e)
        {
        e.printStackTrace();}
    }
}

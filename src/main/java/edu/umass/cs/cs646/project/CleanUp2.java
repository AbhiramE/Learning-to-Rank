package edu.umass.cs.cs646.project;

import edu.umass.cs.cs646.utils.EvalUtils;

import java.io.*;
import java.util.Map;

/**
 * Valar Dohaeris on 12/19/16.
 */
public class CleanUp2 {

    public static void main(String [] args) throws Exception {
        String pathQueries = "/home/abhis3798/codebase/646/Project/queries_robust04_train";
        String inputPath = "/home/abhis3798/codebase/646/Project/robust04_data_train_final";
        String outputPath = "/home/abhis3798/codebase/646/Project/robust04_data_train_final_2";
        String val = "116:0.0";
        char val2 ='#';

        Map<String, String> queries = EvalUtils.loadQueries(pathQueries);


        BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));
        BufferedReader br = new BufferedReader(new FileReader(inputPath));

        String line="";
        while ((line=br.readLine())!=null) {
                        //String replaced = line.replaceAll(val, "111:0.0 112:0.0 113:0.0 114:0.0 115:0.0 " + val);
                         String replaced2 = line.replaceAll(String.valueOf(val2),"# ");
                        bw.write(replaced2);
                        bw.newLine();
            }
        }
    }

package edu.umass.cs.cs646.project;

import org.apache.commons.math3.stat.inference.TTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Valar Dohaeris on 12/21/16.
 */
public class SignificanceTest {

    public static void main(String[] args)
    {

        List<List<Double>> letorStats=new ArrayList<>();
        letorStats.add(new ArrayList<>());
        letorStats.add(new ArrayList<>());
        letorStats.add(new ArrayList<>());

        //Trec1-3
        letorStats.get(0).add(0.0); //MAP
        letorStats.get(0).add(0.0); //ERR
        letorStats.get(0).add(0.0); //NDCG@10

        //Robust04
        letorStats.get(1).add(0.2247);
        letorStats.get(1).add(0.0);
        letorStats.get(1).add(0.0);

        //WT10G
        letorStats.get(2).add(0.1904);
        letorStats.get(2).add(0.0);
        letorStats.get(2).add(0.0);

        List<List<Double>> qLDMStats=new ArrayList<>();
        qLDMStats.add(new ArrayList<>());
        qLDMStats.add(new ArrayList<>());
        qLDMStats.add(new ArrayList<>());

        //Trec1-3
        qLDMStats.get(0).add(0.0); //MAP
        qLDMStats.get(0).add(0.0); //ERR
        qLDMStats.get(0).add(0.0); //NDCG@10

        //Robust04
        qLDMStats.get(1).add(0.2247);
        qLDMStats.get(1).add(0.0);
        qLDMStats.get(1).add(0.0);

        //WT10G
        qLDMStats.get(2).add(0.1904);
        qLDMStats.get(2).add(0.0);
        qLDMStats.get(2).add(0.0);

        List<List<Double>> pl2Stats=new ArrayList<>();
        pl2Stats.add(new ArrayList<>());
        pl2Stats.add(new ArrayList<>());
        pl2Stats.add(new ArrayList<>());

        //Trec1-3
        pl2Stats.get(0).add(0.0); //MAP
        pl2Stats.get(0).add(0.0); //ERR
        pl2Stats.get(0).add(0.0); //NDCG@10

        //Robust04
        pl2Stats.get(1).add(0.2247);
        pl2Stats.get(1).add(0.0);
        pl2Stats.get(1).add(0.0);

        //WT10G
        pl2Stats.get(2).add(0.1904);
        pl2Stats.get(2).add(0.0);
        pl2Stats.get(2).add(0.0);

        List<List<Double>> dl2Stats=new ArrayList<>();
        dl2Stats.add(new ArrayList<>());
        dl2Stats.add(new ArrayList<>());
        dl2Stats.add(new ArrayList<>());

        //Trec1-3
        dl2Stats.get(0).add(0.0); //MAP
        dl2Stats.get(0).add(0.0); //ERR
        dl2Stats.get(0).add(0.0); //NDCG@10

        //Robust04
        dl2Stats.get(1).add(0.2247);
        dl2Stats.get(1).add(0.0);
        dl2Stats.get(1).add(0.0);

        //WT10G
        dl2Stats.get(2).add(0.1904);
        dl2Stats.get(2).add(0.0);
        dl2Stats.get(2).add(0.0);

        List<List<Double>> cBDMStats=new ArrayList<>();
        cBDMStats.add(new ArrayList<>());
        cBDMStats.add(new ArrayList<>());
        cBDMStats.add(new ArrayList<>());

        //Trec1-3
        cBDMStats.get(0).add(0.0); //MAP
        cBDMStats.get(0).add(0.0); //ERR
        cBDMStats.get(0).add(0.0); //NDCG@10

        //Robust04
        cBDMStats.get(1).add(0.2247);
        cBDMStats.get(1).add(0.0);
        cBDMStats.get(1).add(0.0);

        //WT10G
        cBDMStats.get(2).add(0.1904);
        cBDMStats.get(2).add(0.0);
        cBDMStats.get(2).add(0.0);


        /*


                for (int j=0; j<letorStats.size();j++)
                {
                    TTest tTest=new TTest();
                    double pValue=tTest.pairedTTest(letorStats.get(0).get(j),qLDMStats.get(j).get(j));
                    if(pValue<0.001)
                        count001++;
                    if(pValue<0.01)
                        count01++;
                    if(pValue<0.05)
                        count05++;
                }

            System.out.println(" "+count05+" "+count01+" "+count001);
            */
        }
    }

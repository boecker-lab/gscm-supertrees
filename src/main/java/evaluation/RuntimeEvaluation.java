package evaluation;

import epos.model.tree.Tree;
import epos.model.tree.io.Newick;
import epos.model.tree.treetools.TreeUtilsBasic;
import scmAlgorithm.CalculateSupertree;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anika on 14.05.2015.
 */
public class RuntimeEvaluation {

    InputStream in;
    BufferedReader re;
    List<Tree> alltrees;
    CalculateSupertree calc;
    Tree supertree;
    double time;
    //String path;

    public void readData (String taxa, String scaffold, int num){
        String path = "/" + taxa + "/" + scaffold + "/Source_Trees/" + "sm." + num + ".sourceTrees_OptSCM-Rooting.tre";
        in = RuntimeEvaluation.class.getResourceAsStream(path);
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        System.out.println("\n"+"\n"+taxa+" "+scaffold+" "+num+":");
    }

    public void calculateSupertree (String kind){
        if (alltrees.isEmpty()) System.err.println("Choose a file first");
        else {
            calc = new CalculateSupertree(kind, "off");
            double time1, time2 = 0.0;
            time1 = System.currentTimeMillis();
            supertree = calc.getSupertree(alltrees);
            time2 = System.currentTimeMillis();
            time = time2-time1;
        }
    }



    public void evaluate (){
        //calculateSupertree("resolution");
        //System.out.println("time resolution "+time);

        calculateSupertree("overlap");
        System.out.println("time overlap "+time);

        calculateSupertree("resolution");
        System.out.println("time resolution "+time);
    }

    public void readAndProcess (String taxa, String scaffold, List<Integer> notthere, int number){
        for (int iter=0; iter<number; iter++){
            if (!notthere.contains(iter)){
                readData(taxa, scaffold, iter);
                evaluate();
            }
        }
    }

    public static void main (String[] args){
        RuntimeEvaluation ev = new RuntimeEvaluation();
        Integer[] hundert_zwanzig = {5, 13};
        List empty = new ArrayList();
        Integer[] fuenfhundert_zwanzig = {4, 5, 7, 16, 21, 22};
        Integer[] tausend_zwanzig = {2, 5, 6};
        Integer[] tausend_fuenfzig = {6};
        ev.readAndProcess("100", "20", Arrays.asList(hundert_zwanzig), 30);
        ev.readAndProcess("100", "50", empty, 30);
        ev.readAndProcess("100", "75", empty, 30);
        ev.readAndProcess("100", "100", empty, 30);
        ev.readAndProcess("500", "20", Arrays.asList(fuenfhundert_zwanzig), 30);
        ev.readAndProcess("500", "50", empty, 30);
        ev.readAndProcess("500", "75", empty, 30);
        ev.readAndProcess("500", "100", empty, 30);
        ev.readAndProcess("1000", "20", Arrays.asList(tausend_zwanzig), 10);
        ev.readAndProcess("1000", "50", Arrays.asList(tausend_fuenfzig), 10);
        ev.readAndProcess("1000", "75", empty, 10);
        ev.readAndProcess("1000", "100", empty, 10);
    }

}

package evaluation;

import epos.model.tree.Tree;
import epos.model.tree.io.Newick;
import epos.model.tree.treetools.FN_FP_RateComputer;
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
public class ResolutionEvaluation {

    InputStream in;
    BufferedReader re;
    List<Tree> alltrees;
    CalculateSupertree calc;
    Tree supertree;
    Tree modeltree;
    double resolution;
    //String path;

    public void readData (String taxa, String scaffold, int num){
        String path = "/" + taxa + "/" + scaffold + "/Model_Trees/" + "sm_data." + num + ".model_tree";
        in = ResolutionEvaluation.class.getResourceAsStream(path);
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        modeltree = alltrees.get(0);
        path = "/" + taxa + "/" + scaffold + "/Source_Trees/" + "sm." + num + ".sourceTrees_OptSCM-Rooting.tre";
        in = ResolutionEvaluation.class.getResourceAsStream(path);
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        System.out.println("\n"+"\n"+taxa+" "+scaffold+" "+num+":");
    }

    public void calculateSupertree (String kind){
        if (alltrees.isEmpty()) System.err.println("Choose a file first");
        else {
            calc = new CalculateSupertree(kind, "off");
            supertree = calc.getSupertree(alltrees);
        }
    }

    public void evaluate (){
        calculateSupertree("resolution");
        resolution = TreeUtilsBasic.calculateTreeResolution(supertree.getNumTaxa(), supertree.vertexCount());
        System.out.println("resolution resolution "+resolution);
        System.out.println(supertree.getNumTaxa() +" "+ supertree.vertexCount());

        calculateSupertree("overlap");
        resolution = TreeUtilsBasic.calculateTreeResolution(supertree.getNumTaxa(), supertree.vertexCount());
        System.out.println("resolution overlap "+resolution);
        System.out.println(supertree.getNumTaxa() +" "+supertree.vertexCount());

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
        ResolutionEvaluation ev = new ResolutionEvaluation();
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

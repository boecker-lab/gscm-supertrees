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
    double resolutionresolution = 0.0;
    double resolutionoverlap = 0.0;

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
    }

    public void calculateSupertree (String kind){
        if (alltrees.isEmpty()) System.err.println("Choose a file first");
        else {
            if (kind.equalsIgnoreCase("resolution")) calc = new CalculateSupertree(CalculateSupertree.Type.RESOLUTION, CalculateSupertree.Info.OFF);
            else calc = new CalculateSupertree(CalculateSupertree.Type.OVERLAP, CalculateSupertree.Info.OFF);
            supertree = calc.getSupertree(alltrees);
        }
    }

    //prints supertree resolution, averaged arithmetically over all calculated examples
    //in each scaffold factor, results for resolution and overlap criterion
    public void evaluate (boolean exampledone, int notthere, int number){
        calculateSupertree("resolution");
        resolution = TreeUtilsBasic.calculateTreeResolution(supertree.getNumTaxa(), supertree.vertexCount());
        resolutionresolution += resolution;

        calculateSupertree("overlap");
        resolution = TreeUtilsBasic.calculateTreeResolution(supertree.getNumTaxa(), supertree.vertexCount());
        resolutionoverlap += resolution;

        if (exampledone){
            int actualnumber = number-notthere;
            System.out.println("\n"+"resolution of resolution supertree ");
            System.out.print((resolutionresolution/actualnumber) + " ");
            System.out.println("\n"+"resolution of overlap supertree ");
            System.out.print((resolutionoverlap/actualnumber) + " ");
            System.out.println("\n");
            resolutionresolution = 0.0;
            resolutionoverlap = 0.0;
        }
    }

    public void readAndProcess (String taxa, String scaffold, List<Integer> notthere, int number){
        for (int iter=0; iter<number; iter++){
            System.out.print(iter+" ");
            if (number-1 == iter){
                System.out.println("\n\n"+taxa+" "+scaffold+": ");
                readData(taxa, scaffold, iter);
                evaluate(true, notthere.size(), number);
            }
            else {
                if (!notthere.contains(iter)){
                    readData(taxa, scaffold, iter);
                    evaluate(false, notthere.size(), number);
                }
            }
        }
    }

    public static void main (String[] args){
        ResolutionEvaluation ev = new ResolutionEvaluation();
        //arrays contain the examples for which no source trees were available
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

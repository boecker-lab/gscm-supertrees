package evaluation;

import phyloTree.io.Newick;
import phyloTree.model.tree.Tree;
import phyloTree.treetools.FN_FP_RateComputer;
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
public class FNFPEvaluation {

    InputStream in;
    BufferedReader re;
    List<Tree> alltrees;
    CalculateSupertree calc;
    Tree supertree;
    Tree modeltree;
    Tree swensonsupertree;
    double[] rates;
    boolean swenson;
    double[] FNFPsourceover = new double[]{0, 0, 0, 0};
    double[] FNFPsourceres = new double[]{0, 0, 0, 0};
    double[] FNFPmodelover = new double[]{0, 0, 0, 0};
    double[] FNFPmodelres = new double[]{0, 0, 0, 0};
    double[] FNFPmodelswenson;
    double[] FNFPsourceswenson;

    FNFPEvaluation(){
        this.swenson = false;
    }

    //if swenson is true, evaluation is also done for an already calculated overlap result
    //this can be used for comparison of results
    FNFPEvaluation(boolean swenson)
    {
        this.swenson = swenson;
        if (swenson){
            FNFPmodelswenson = new double[]{0, 0, 0, 0};
            FNFPsourceswenson = new double[]{0, 0, 0, 0};
        }
    }

    public void readData (String taxa, String scaffold, int num){
        String path = "/" + taxa + "/" + scaffold + "/Model_Trees/" + "sm_data." + num + ".model_tree";
        in = FNFPEvaluation.class.getResourceAsStream(path);
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        modeltree = alltrees.get(0);
        if (swenson){
            path = "/" + taxa + "/" + scaffold + "/Super_Trees/" + "sm." + num + ".sourceTrees.scmTree.tre_OptRoot.tre";
            in = FNFPEvaluation.class.getResourceAsStream(path);
            re = new BufferedReader(new InputStreamReader(in));
            alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
            swensonsupertree = alltrees.get(0);
        }
        path = "/" + taxa + "/" + scaffold + "/Source_Trees/" + "sm." + num + ".sourceTrees_OptSCM-Rooting.tre";
        in = FNFPEvaluation.class.getResourceAsStream(path);
        re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
    }

    public void calculateSupertree (String kind, String onoff){
        if (alltrees.isEmpty()) System.err.println("Choose a file first");
        else {
            if (onoff.equalsIgnoreCase("on")){
                if (kind.equalsIgnoreCase("resolution")) calc = new CalculateSupertree(CalculateSupertree.Type.RESOLUTION, CalculateSupertree.Info.ON);
                else calc = new CalculateSupertree(CalculateSupertree.Type.OVERLAP, CalculateSupertree.Info.ON);

            }
            else {
                if (kind.equalsIgnoreCase("resolution")) calc = new CalculateSupertree(CalculateSupertree.Type.RESOLUTION, CalculateSupertree.Info.OFF);
                else calc = new CalculateSupertree(CalculateSupertree.Type.OVERLAP, CalculateSupertree.Info.OFF);
            }
            supertree = calc.getSupertree(alltrees);
        }
    }

    //prints out FNFP number and rates for each scaffold factor in each taxa number, results are
    //averaged arithmetically over all calculated supertree examples of one scaffold factor, results for
    //overlap and resoslution criterion, FNFP for supertree-modeltree and sum of FNFP for supertree-source trees
    public void evaluate (boolean exampledone, String taxa, String scaffold, int notthere, int number){
        double curr = 0.0;
        calculateSupertree("resolution", "off");
        rates = FN_FP_RateComputer.calculateSumOfRates(supertree, alltrees.toArray(new Tree[alltrees.size()]));
        for (int iter=0; iter<4; iter++){
            curr = FNFPsourceres[iter];
            FNFPsourceres[iter] = curr + rates[iter];
        }
        rates = FN_FP_RateComputer.calculateRates(supertree, modeltree, false);
        for (int iter=0; iter<4; iter++){
            curr = FNFPmodelres[iter];
            FNFPmodelres[iter] = curr + rates[iter];
        }
        calculateSupertree("overlap", "off");
        rates = FN_FP_RateComputer.calculateSumOfRates(supertree, alltrees.toArray(new Tree[alltrees.size()]));
        for (int iter=0; iter<4; iter++){
            curr = FNFPsourceover[iter];
            FNFPsourceover[iter] = curr + rates[iter];
        }
        rates = FN_FP_RateComputer.calculateRates(supertree, modeltree, false);
        for (int iter=0; iter<4; iter++){
            curr = FNFPmodelover[iter];
            FNFPmodelover[iter] = curr + rates[iter];
        }
        if (swenson){
            rates = FN_FP_RateComputer.calculateSumOfRates(swensonsupertree, alltrees.toArray(new Tree[alltrees.size()])); //sum FP has to be 0 for any SCM result [1]
            for (int iter=0; iter<4; iter++){
                curr = FNFPsourceswenson[iter];
                FNFPsourceswenson[iter] = curr + rates[iter];
            }
            rates = FN_FP_RateComputer.calculateRates(swensonsupertree, modeltree, false);
            for (int iter=0; iter<4; iter++){
                curr = FNFPmodelswenson[iter];
                FNFPmodelswenson[iter] = curr + rates[iter];
            }
        }
        if (exampledone){
            int actualnumber = number-notthere;
            System.out.println("\n"+"FNFP source trees resolution ");
            for (double a : FNFPsourceres){
                System.out.print((a/actualnumber) + " ");
            }
            System.out.println("\n"+"FNFP modeltree resolution ");
            for (double a : FNFPmodelres){
                System.out.print((a/actualnumber) + " ");
            }
            System.out.println("\n"+"FNFP source trees overlap ");
            for (double a : FNFPsourceover){
                System.out.print((a/actualnumber) + " ");
            }
            System.out.println("\n"+"FNFP modeltree overlap ");
            for (double a : FNFPmodelover){
                System.out.print((a/actualnumber) + " ");
            }
            if (swenson){
                System.out.println("\n"+"FNFP source trees swenson ");
                for (double a : FNFPsourceswenson){
                    System.out.print((a/actualnumber) + " ");
                }
                System.out.println("\n"+"FNFP modeltree swenson ");
                for (double a : FNFPmodelswenson){
                    System.out.print((a/actualnumber) + " ");
                }
                FNFPsourceswenson = new double[]{0, 0, 0, 0};
                FNFPmodelswenson = new double[]{0, 0, 0, 0};
            }
            System.out.println("\n");
            FNFPsourceres = new double[]{0, 0, 0, 0};
            FNFPmodelres = new double[]{0, 0, 0, 0};
            FNFPsourceover = new double[]{0, 0, 0, 0};
            FNFPmodelover = new double[]{0, 0, 0, 0};

        }
    }

    public void readAndProcess (String taxa, String scaffold, List<Integer> notthere, int number){
        for (int iter=0; iter<number; iter++){
            System.out.print(iter+" ");
            if (number-1 == iter){
                System.out.println("\n\n"+taxa+" "+scaffold+": ");
                readData(taxa, scaffold, iter);
                evaluate(true, taxa, scaffold, notthere.size(), number);
            }
            else {
                if (!notthere.contains(iter)){
                    readData(taxa, scaffold, iter);
                    evaluate(false, taxa, scaffold, notthere.size(), number);
                }
            }
        }
    }

    public static void main (String[] args){
        FNFPEvaluation ev = new FNFPEvaluation(false);
        //those arrays contain the examples for which no source trees were available
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

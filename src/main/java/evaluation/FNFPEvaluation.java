package evaluation;

import epos.model.tree.Tree;
import epos.model.tree.io.Newick;
import epos.model.tree.treetools.FN_FP_RateComputer;
import scmAlgorithm.CalculateSupertree;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    //String path;

    FNFPEvaluation(){
        this.swenson = false;
    }

    FNFPEvaluation(boolean swenson){
        this.swenson = swenson;
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
        //System.out.println("\n"+"\n"+taxa+" "+scaffold+" "+num+":");
    }

    public void calculateSupertree (String kind, String onoff){
        if (alltrees.isEmpty()) System.err.println("Choose a file first");
        else {
            if (onoff.equalsIgnoreCase("on")){
                calc = new CalculateSupertree(kind, "on");
            }
            else calc = new CalculateSupertree(kind, "off");
            supertree = calc.getSupertree(alltrees);
        }
    }

    public void evaluate (boolean printresolution, boolean printoverlap, boolean exampledone, String taxa, String scaffold, int notthere, int number){
        double curr = 0.0;
        if (printresolution){
            calculateSupertree("resolution", "on");
        }
        else calculateSupertree("resolution", "off");
        rates = FN_FP_RateComputer.calculateSumOfRates(supertree, alltrees.toArray(new Tree[alltrees.size()])); //sum FP has to be 0 for any SCM result [1]
        for (int iter=0; iter<4; iter++){
            curr = FNFPsourceres[iter];
            FNFPsourceres[iter] = curr + rates[iter];
        }
        /*System.out.println("\n"+"FNFP source trees resolution");
        for (double a : rates){
            System.out.print(a + " ");
        }*/
        rates = FN_FP_RateComputer.calculateRates(supertree, modeltree, false);
        for (int iter=0; iter<4; iter++){
            curr = FNFPmodelres[iter];
            FNFPmodelres[iter] = curr + rates[iter];
        }
        /*System.out.println("\n"+"FNFP modeltree resolution");
        for (double a : rates){
            System.out.print(a + " ");
        }*/
        if (printoverlap){
           calculateSupertree("overlap", "on");
        }
        else calculateSupertree("overlap", "off");
        rates = FN_FP_RateComputer.calculateSumOfRates(supertree, alltrees.toArray(new Tree[alltrees.size()])); //sum FP has to be 0 for any SCM result [1]
        for (int iter=0; iter<4; iter++){
            curr = FNFPsourceover[iter];
            FNFPsourceover[iter] = curr + rates[iter];
        }
        /*System.out.println("\n"+"FNFP source trees overlap");
        for (double a : rates){
            System.out.print(a + " ");
        }*/
        rates = FN_FP_RateComputer.calculateRates(supertree, modeltree, false);
        for (int iter=0; iter<4; iter++){
            curr = FNFPmodelover[iter];
            FNFPmodelover[iter] = curr + rates[iter];
        }
        /*System.out.println("\n"+"FNFP modeltree overlap");
        for (double a : rates){
            System.out.print(a + " ");
        }*/
        if (swenson){
            rates = FN_FP_RateComputer.calculateSumOfRates(swensonsupertree, alltrees.toArray(new Tree[alltrees.size()])); //sum FP has to be 0 for any SCM result [1]
            System.out.println("\n"+"FNFP source trees swenson");
            for (double a : rates){
                System.out.print(a + " ");
            }
            rates = FN_FP_RateComputer.calculateRates(swensonsupertree, modeltree, false);
            System.out.println("\n"+"FNFP modeltree swenson");
            for (double a : rates){
                System.out.print(a + " ");
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
                evaluate(false, false, true, taxa, scaffold, notthere.size(), number);
            }
            else {
                if (!notthere.contains(iter)){
                    readData(taxa, scaffold, iter);
                    //if (iter == 0){
                        //for (Tree x : alltrees){
                        //    System.out.println("Tree "+Newick.getStringFromTree(x));
                        //}
                        //evaluate(false, true);
                        //evaluate(true, false);
                        //evaluate(false, false, false);
                    //}
                    //else {
                        evaluate(false, false, false, taxa, scaffold, notthere.size(), number);
                    //}
                }
            }
        }
    }

    public static void main (String[] args){
        FNFPEvaluation ev = new FNFPEvaluation(false);
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

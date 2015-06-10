package scmAlgorithm;

import epos.model.tree.Tree;
import epos.model.tree.io.Newick;
import epos.model.tree.treetools.FN_FP_RateComputer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Anika on 13.05.2015.
 */
public class CalculateSupertree {

    String type = "";
    String info = "";
    //TODO
    List<Tree> inputtrees;
    HashMap<Tree, Tree[]> scmorigins = new HashMap<Tree, Tree[]>();
    HashMap<Tree, Integer[]> scmsourcesnumbers = new HashMap<Tree, Integer[]>();
    HashMap<Tree, Tree[]> scmsources = new HashMap<Tree, Tree[]>();

    //public enum type{}

    public CalculateSupertree(String type, String info){
        if (type.equalsIgnoreCase("overlap")) this.type = "overlap";
        else if (type.equalsIgnoreCase("resolution")) this.type = "resolution";
        if (info.equalsIgnoreCase("on")) this.info = "on";
        else if (info.equalsIgnoreCase("off")) this.info = "off";
    }

    public CalculateSupertree(int type, String info){
        if (type == 0) this.type = "overlap";
        else if (type == 1) this.type = "resolution";
        if (info.equalsIgnoreCase("on")) this.info = "on";
        else if (info.equalsIgnoreCase("off")) this.info = "off";
        //else System.err.println()
    }

    public String getType(){
        return this.type;
    }

    public void setType(String type){
        this.type = type;
    }

    public void helpFillScmSources (Tree[] input, Tree scm){
        List<Tree> sourcetrees = new ArrayList<Tree>();
        int[] sourcetreeindices;
        List<Tree> curr = new ArrayList<Tree>();
        List<Tree> work = new ArrayList<Tree>();
        for (Tree x : input){
            curr.add(x);
            while (!curr.isEmpty()){
                work = new ArrayList<>(curr);
                for (Tree act : work){
                    if (inputtrees.contains(act)) {
                        sourcetrees.add(act);
                        curr.remove(act);
                    }
                    else{
                        curr.addAll(Arrays.asList(scmorigins.get(act)));
                        curr.remove(act);
                    }
                }
            }
            List<Tree> betweentree = new ArrayList<Tree>();
            List<Integer> betweenint = new ArrayList<Integer>();
            sourcetreeindices = new int[sourcetrees.size()];
            for (int iter=0; iter<sourcetrees.size(); iter++){
                sourcetreeindices[iter] = inputtrees.indexOf(sourcetrees.get(iter))+1;
            }
            if (scmsourcesnumbers.containsKey(scm)){
                betweenint.addAll(Arrays.asList(scmsourcesnumbers.get(scm)));
                for (int iter=0; iter<sourcetreeindices.length; iter++){
                    betweenint.add(sourcetreeindices[iter]);
                }
                //betweenint.addAll(Arrays.asList(sourcetreeindices));
            }
            else {
                for (int iter=0; iter<sourcetreeindices.length; iter++){
                    betweenint.add(sourcetreeindices[iter]);
                }
                //betweenint = Arrays.asList(sourcetreeindices);
            }
            scmsourcesnumbers.put(scm, betweenint.toArray(new Integer[betweenint.size()]));
            if (scmsources.containsKey(scm)){
                betweentree.addAll(Arrays.asList(scmsources.get(scm)));
                betweentree.addAll(sourcetrees);
            }
            else betweentree = sourcetrees;
            scmsources.put(scm, betweentree.toArray(new Tree[betweentree.size()]));
            sourcetrees.clear();
        }
    }


    public Tree getSupertree (List<Tree> input) {
        for (Tree curr : input){
            scmsourcesnumbers.put(curr, new Integer[]{input.indexOf(curr)+1});
            scmsources.put(curr, new Tree[]{curr});
        }
        inputtrees = new ArrayList<Tree> (input);
        Scorer sc;
        if (getType() == "overlap"){
            sc = new OverlapScorer();
        }
        else {
            sc = new ResolutionScorer();
        }

        //List<Tree> output = new ArrayList<Tree> (input.subList(0, input.size()-1));
        List<Tree> output = new ArrayList<Tree> (input);
        //TODO
        if (this.info == "on"){
            for (Tree t : output){
                System.out.print("\n"+"Tree "+t+" "+Newick.getStringFromTree(t));
            }
        }

        List<Tree> save;
        Tree one;
        Tree two;
        Tree three;
        while (output.size() > 1){
            save = sc.getTreeswithbiggestScoreandSCM(output);

            one = save.get(0);
            two = save.get(1);
            three = save.get(2);
            scmorigins.put(three, new Tree[]{one, two});
            helpFillScmSources(new Tree[]{one, two}, three);

            if (this.info == "on") {
                List<Tree> consistsof = new ArrayList<Tree>();
                Integer[]consistsofnumbers;
                System.out.println("\n"+"Got one SCM: " + Newick.getStringFromTree(three));
                //System.out.println("named "+three);
                System.out.println("aus Tree "+one+": "+Newick.getStringFromTree(one));
                consistsofnumbers = scmsourcesnumbers.get(one);
                System.out.print("aus den sourcetrees ");
                for (Integer i : consistsofnumbers){
                    System.out.print(i+" ");
                }
                System.out.println("\n"+"und Tree " + two + ": " + Newick.getStringFromTree(two));
                consistsofnumbers = scmsourcesnumbers.get(two);
                System.out.print("aus den sourcetrees ");
                for (Integer i : consistsofnumbers){
                    System.out.print(i+" ");
                }

                //double[] a = FN_FP_RateComputer.calculateSumOfRates(three, inputtrees.toArray(new Tree[inputtrees.size()]));
                double[] a = FN_FP_RateComputer.calculateSumOfRates(three, scmsources.get(three));
                System.out.println();
                for (double pr : a){
                    System.out.print(pr+" ");
                }
                System.out.println("\n"+output.size() + " left.");
                System.out.println("\n"+"\n");
            }
            output.remove(one);
            output.remove(two);
            output.add(three);
        }
        return output.get(0);
    }


}

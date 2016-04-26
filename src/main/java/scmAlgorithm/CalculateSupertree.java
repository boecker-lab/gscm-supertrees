package scmAlgorithm;

import phyloTree.io.Newick;
import phyloTree.model.tree.Tree;
import phyloTree.treetools.FN_FP_RateComputer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Anika on 13.05.2015.
 */
public class CalculateSupertree {

    Type type;
    Info info;
    List<Tree> inputtrees;
    //contains for every SCM tree the two trees that were merged to calculate it
    HashMap<Tree, Tree[]> scmorigins = new HashMap<Tree, Tree[]>();
    //contains for every SCM tree the numbers of the source tree it was made from
    HashMap<Tree, Integer[]> scmsourcesnumbers = new HashMap<Tree, Integer[]>();
    //contains for every SCM tree the source trees it was made from
    HashMap<Tree, Tree[]> scmsources = new HashMap<Tree, Tree[]>();

    //if info is "on", information on the current SCM tree and the trees it was made from will be printed
    public CalculateSupertree(Type type, Info info){
        this.info = info;
        this.type = type;
    }

    public enum Info {ON, OFF};

    public enum Type {RESOLUTION, OVERLAP};

    public Type getType(){
        return this.type;
    }

    public void setType(Type type){
        this.type = type;
    }

    //fills scmsources and scmsourcesnumbers, given an scm and the two trees that were merged to calculate it
    public void helpFillScmSources (Tree[] input, Tree scm){
        List<Tree> sourcetrees = new ArrayList<Tree>();
        int[] sourcetreeindices;
        List<Tree> curr = new ArrayList<Tree>();
        List<Tree> work = new ArrayList<Tree>();
        for (Tree x : input){
            curr.add(x);
            while (!curr.isEmpty()){
                work = new ArrayList<Tree>(curr);
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
            }
            else {
                for (int iter=0; iter<sourcetreeindices.length; iter++){
                    betweenint.add(sourcetreeindices[iter]);
                }
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

    //calculates a supertree from a given list of source trees
    public Tree getSupertree (List<Tree> input) {
        for (Tree curr : input){
            scmsourcesnumbers.put(curr, new Integer[]{input.indexOf(curr)+1});
            scmsources.put(curr, new Tree[]{curr});
        }
        inputtrees = new ArrayList<Tree> (input);
        Scorer sc;

        if (getType() == Type.OVERLAP){
            sc = new OverlapScorer();
        }
        else sc = new ResolutionScorer();

        List<Tree> output = new ArrayList<Tree> (input);
        if (this.info == Info.ON){
            for (Tree t : output){
                System.out.print("\n"+"Tree "+t+" "+Newick.getStringFromTree(t));
            }
        }
        List<Tree> save;
        Tree one;
        Tree two;
        Tree three;
        //while there are more than one tree in the list, two trees with highest score are merged to an SCM tree
        while (output.size() > 1){
            save = sc.getTreeswithbiggestScoreandSCM(output);
            one = save.get(0);
            two = save.get(1);
            three = save.get(2);
            scmorigins.put(three, new Tree[]{one, two});
            helpFillScmSources(new Tree[]{one, two}, three);

            if (this.info == Info.ON) {
                List<Tree> consistsof = new ArrayList<Tree>();
                Integer[]consistsofnumbers;
                System.out.println("\n"+"Got one SCM: " + Newick.getStringFromTree(three));
                System.out.println("from Tree "+one+": "+Newick.getStringFromTree(one));
                consistsofnumbers = scmsourcesnumbers.get(one);
                System.out.print("made from sourcetrees ");
                for (Integer i : consistsofnumbers){
                    System.out.print(i+" ");
                }
                System.out.println("\n"+"and Tree " + two + ": " + Newick.getStringFromTree(two));
                consistsofnumbers = scmsourcesnumbers.get(two);
                System.out.print("made from sourcetrees ");
                for (Integer i : consistsofnumbers){
                    System.out.print(i+" ");
                }

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

package scmAlgorithm;


import epos.model.tree.Tree;
import epos.model.tree.treetools.TreeUtilsBasic;
import scmAlgorithm.treeSelector.TreePair;
import scmAlgorithm.treeSelector.TreeSelector;

import java.util.Arrays;

/**
 * Created by fleisch on 10.02.15.
 */
public class GreedySCMAlgorithm extends AbstractSCMAlgorithm {
    private final boolean rootOptimization; //todo remove


    private final boolean externalSingleTaxonReduction; //todo remove

//    private Tree[] input; //todo remove

    public GreedySCMAlgorithm(TreeSelector selector) {
        this(selector, false);
    }



    public GreedySCMAlgorithm(TreeSelector selector, boolean rootOptimization) {
        this(selector,rootOptimization,false);
    }

    public GreedySCMAlgorithm(TreeSelector selector, boolean rootOptimization, boolean externalSingleTaxonReduction) {
        super(selector);
        this.rootOptimization = rootOptimization;
        this.externalSingleTaxonReduction = externalSingleTaxonReduction;
    }


    @Override
    protected void run() {
//        input = ((GreedyTreeSelector)selector).getInputCopy();
        Tree superCandidate = null;
        TreePair pair;
        while((pair = selector.pollTreePair()) != null){
//            superCandidate = mergeTrees(pair);
            superCandidate =  pair.getConsensus(selector.scorer.getConsensusAlgorithm());
            selector.addTree(superCandidate);
        }
        TreeUtilsBasic.cleanTree(superCandidate); //remove inner labels and branch lengths //todo create useful branch length?!

        superTrees = Arrays.asList(superCandidate);

    }

    private Tree mergeTrees(TreePair pair) {
        //todo what is that good for?
        int pc = 0;
        Tree consensus;
        if (pair.t1.vertexCount() <= pair.getNumCommonLeafes() + 1){
            consensus = pair.t2;
        }else if (pair.t2.vertexCount() <= pair.getNumCommonLeafes() + 1){
            consensus = pair.t1;
        }else{
            consensus = pair.getConsensus(selector.scorer.getConsensusAlgorithm());
//            pc += pair.pc; //todo remove --> Debug stuff
        }

        /*double[] r = FN_FP_RateComputer.calculateSumOfRates(consensus, new Tree[]{pair.t1, pair.t2});
        if (Double.compare(0d,r[1]) != 0){//todo remove --> debug
            System.out.println("False positives during taxa insertion: " + r[3]);
            System.out.println("False positives prevention during taxa insertion: " + pair.pc);

            System.out.println(Newick.getStringFromTree(consensus));
            System.out.println(Newick.getStringFromTree(pair.t1));
            System.out.println(Newick.getStringFromTree(pair.t2));
}*/
        return consensus;
    }






}

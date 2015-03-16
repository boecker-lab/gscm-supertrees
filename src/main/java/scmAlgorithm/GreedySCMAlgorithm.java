package scmAlgorithm;


import epos.algo.consensus.ConsensusAlgorithm;
import epos.algo.consensus.adams.AdamsConsensus;
import epos.algo.consensus.loose.PairwiseLooseConsensus;
import epos.algo.consensus.nconsensus.NConsensus;
import epos.model.tree.Tree;
import epos.model.tree.io.Newick;
import epos.model.tree.treetools.FN_FP_RateComputer;
import epos.model.tree.treetools.TreeUtilsBasic;
import scmAlgorithm.treeSelector.GreedyTreeSelector;
import scmAlgorithm.treeSelector.TreePair;
import scmAlgorithm.treeSelector.TreeSelector;

import java.util.Arrays;

/**
 * Created by fleisch on 10.02.15.
 */
public class GreedySCMAlgorithm extends AbstractSCMAlgorithm {
    private final boolean rootOptimization;
    public static enum Methods {SEMI_STRICT, STRICT,MAJORITY,ADAMS}

    private final Methods METHOD;
    private final boolean externalSingleTaxonReduction;

//    private Tree[] input; //todo remove

    public GreedySCMAlgorithm(TreeSelector selector) {
        this(selector, false);
    }

    public GreedySCMAlgorithm(TreeSelector selector, boolean rootOptimization) {
        this(selector,rootOptimization,Methods.STRICT);
    }

    public GreedySCMAlgorithm(TreeSelector selector, boolean rootOptimization, Methods method) {
        this(selector,rootOptimization,false,method);
    }

    public GreedySCMAlgorithm(TreeSelector selector, boolean rootOptimization, boolean externalSingleTaxonReduction, Methods method) {
        super(selector);
        this.rootOptimization = rootOptimization;
        this.externalSingleTaxonReduction = externalSingleTaxonReduction;
        METHOD = method;
    }


    @Override
    protected void run() {
//        input = ((GreedyTreeSelector)selector).getInputCopy();
        Tree superCandidate = null;
        TreePair pair;
        while((pair = selector.pollTreePair()) != null){
            superCandidate = mergeTrees(pair);
            selector.addTree(superCandidate);
        }
        TreeUtilsBasic.cleanTree(superCandidate); //remove inner labels and branch lengths //todo create useful branch length?!

        superTrees = Arrays.asList(superCandidate);

    }

    private Tree mergeTrees(TreePair pair) {
        if (rootOptimization)
            if (!pair.buildCompatibleRoots())
                System.out.println("WARNING:  no compatible root found --> inefficient scm calculation");

        pair.pruneToCommonLeafes(externalSingleTaxonReduction);
        Tree consensus;
        if (pair.t1.vertexCount() <= pair.getCommonLeafes().size()+1){
            consensus = pair.t2;
        }else if (pair.t2.vertexCount() <= pair.getCommonLeafes().size()+1){
            consensus = pair.t1;
        }else{
            final ConsensusAlgorithm consMaker =  getConsensusAlgorithm(pair.t1,pair.t2);
            consensus = consMaker.getConsensusTree();
        }
//        double[] r = FN_FP_RateComputer.calculateSumOfRates(consensus,pair.clones);
//        if (Double.compare(0d,r[1]) != 0){//todo remove --> debug
//            System.out.println("False positives  BEFORE taxa insertion!");
//        }

        pair.reinsertSingleTaxa(consensus);

//        r = FN_FP_RateComputer.calculateSumOfRates(consensus,pair.clones);
//        if (Double.compare(0d,r[1]) != 0){//todo remove --> debug
//            System.out.println("False positives AFTER taxa insertion!");
//            System.out.println(Newick.getStringFromTree(consensus));
//            System.out.println(Newick.getStringFromTree(pair.clones[0]));
//            System.out.println(Newick.getStringFromTree(pair.clones[1]));
//        }

        if (rootOptimization)
            TreeUtilsBasic.deleteRootNode(consensus,false);

        return consensus;
    }

    private ConsensusAlgorithm getConsensusAlgorithm(Tree t1, Tree t2){
        switch (METHOD){
            case SEMI_STRICT:
                return new PairwiseLooseConsensus(t1, t2, false);
            case STRICT:
                return  new NConsensus(new Tree[]{t1, t2},NConsensus.METHOD_STRICT);
            case MAJORITY:
                return new NConsensus(new Tree[]{t1, t2},NConsensus.METHOD_MAJORITY); // is same as strict for 2 trees...
            case ADAMS:
                return  new AdamsConsensus(new Tree[]{t1, t2});
            default: return null;
        }
    }




}

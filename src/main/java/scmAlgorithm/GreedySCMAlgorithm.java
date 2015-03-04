package scmAlgorithm;


import epos.algo.consensus.ConsensusAlgorithm;
import epos.algo.consensus.adams.AdamsConsensus;
import epos.algo.consensus.loose.PairwiseLooseConsensus;
import epos.algo.consensus.nconsensus.NConsensus;
import epos.model.tree.Tree;
import epos.model.tree.treetools.TreeUtilsBasic;
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

    public GreedySCMAlgorithm(TreeSelector selector) {
        this(selector, false);
    }

    public GreedySCMAlgorithm(TreeSelector selector, boolean rootOptimization) {
        this(selector,rootOptimization,Methods.STRICT);
    }

    public GreedySCMAlgorithm(TreeSelector selector, boolean rootOptimization, Methods method) {
        super(selector);
        this.rootOptimization = rootOptimization;
        METHOD = method;
    }


    @Override
    protected void run() {
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
        pair.pruneToCommonLeafes();
        /*System.out.println("Treepair: ");
        System.out.println(Newick.getStringFromTree(pair.t1));
        System.out.println(Newick.getStringFromTree(pair.t2));
        System.out.println();
        System.out.println();*/
        Tree consensus;
        if (pair.t1.vertexCount() <= pair.getCommonLeafes().size()+1){
            consensus = pair.t2;
        }else if (pair.t2.vertexCount() <= pair.getCommonLeafes().size()+1){
            consensus = pair.t1;
        }else{
            final ConsensusAlgorithm consMaker =  getConsensusAlgorithm(pair.t1,pair.t2);
            consensus = consMaker.getConsensusTree();
        }

        pair.reinsertSingleTaxa(consensus);
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
                return  new AdamsConsensus(new Tree[]{t1, t2}); // todo is adams semi strict?
            default: return null;
        }
    }




}

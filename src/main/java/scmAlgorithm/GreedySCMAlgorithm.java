package scmAlgorithm;


import epos.model.tree.Tree;
import scmAlgorithm.treeSelector.TreePair;
import scmAlgorithm.treeSelector.TreeSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fleisch on 10.02.15.
 */
public class GreedySCMAlgorithm extends AbstractSCMAlgorithm implements GreedySCMCalculation {
    public GreedySCMAlgorithm(TreeSelector selector) {
        super(selector);
    }

    @Override
    protected List<TreePair> calculateSuperTrees() {
        return new ArrayList<>(Arrays.asList(calculateSuperTree()));
    }

    protected TreePair calculateSuperTree() {
        return calculateGreedyConsensus(selector);
    }

    // todo move to defaut method to interface if java 8 is common
    /*@Override
    public TreePair calculateGreedyConsensus(TreeSelector selector) {
        TreePair superCandidatePair = null;
        TreePair pair;
        while((pair = selector.pollTreePair()) != null){
            Tree superCandidate = pair.getConsensus(selector.getScorer().getConsensusAlgorithm());
            selector.addTree(superCandidate);
            superCandidatePair =  pair;
        }
        return superCandidatePair;
    }*/
}

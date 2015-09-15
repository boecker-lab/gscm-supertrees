package scmAlgorithm;

import epos.model.tree.Tree;
import scmAlgorithm.treeSelector.TreePair;
import scmAlgorithm.treeSelector.TreeSelector;

/**
 * Created by fleisch on 25.03.15.
 */
public interface GreedySCMCalculation {

    default TreePair calculateGreedyConsensus(TreeSelector selector) {
        TreePair superCandidatePair = null;
        TreePair pair;
        double trees = selector.getNumberOfTrees()-1;
        double progress = 0;
        int pCount = 0;

        System.out.print("0% ");
        while ((pair = selector.pollTreePair()) != null) {
            Tree superCandidate = pair.getConsensus(selector.getScorer().getConsensusAlgorithm());
            selector.addTree(superCandidate);
            superCandidatePair = pair;
            double nuP = ((++pCount / trees)) * 72;
            double diff =  nuP - progress;
            if (diff >= 1) {
                while (diff >= 1) {
                    System.out.print("#");
                    diff--;
                    progress++;
                }
            }
        }
        System.out.println(" 100%");
        return superCandidatePair;
    }

}

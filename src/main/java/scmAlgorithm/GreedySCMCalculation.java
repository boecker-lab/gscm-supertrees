package scmAlgorithm;

import epos.model.tree.Tree;
import scmAlgorithm.treeSelector.TreePair;
import scmAlgorithm.treeSelector.TreeSelector;
import utils.CLIProgressBar;

/**
 * Created by fleisch on 25.03.15.
 */
public interface GreedySCMCalculation {

    default TreePair calculateGreedyConsensus(TreeSelector selector, final boolean progress) {
        TreePair superCandidatePair = null;
        TreePair pair;

        //progress bar stuff
        CLIProgressBar progressBar = null;
        if (progress)
            progressBar = new CLIProgressBar();
        int pCount = 0;
        int trees = selector.getNumberOfTrees()-1;

        while ((pair = selector.pollTreePair()) != null) {
            if (progress)
                progressBar.update(pCount++,trees);
            Tree superCandidate = pair.getConsensus();
            selector.addTree(superCandidate);
            superCandidatePair = pair;

        }
        return superCandidatePair;
    }

}

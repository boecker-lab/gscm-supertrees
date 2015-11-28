package scm.algorithm;


import gnu.trove.map.hash.TObjectDoubleHashMap;
import phyloTree.algorithm.SupertreeAlgorithm;
import phyloTree.model.tree.Tree;
import phyloTree.model.tree.TreeUtils;
import scm.algorithm.treeSelector.TreeScorer;
import scm.algorithm.treeSelector.TreeSelector;
import utils.progressBar.CLIProgressBar;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

/**
 * Created by fleisch on 05.02.15.
 */
public abstract class AbstractSCMAlgorithm extends SupertreeAlgorithm {
    private List<Tree> superTrees;
    protected int threads;

    public AbstractSCMAlgorithm(Logger logger, ExecutorService executorService) {
        super(logger, executorService);
    }

    public AbstractSCMAlgorithm(Logger logger) {
        super(logger);
    }

    public AbstractSCMAlgorithm() {
        super();
    }

    protected abstract List<Tree> calculateSuperTrees();

    public abstract void setInput(Tree... trees);

    @Override
    public void run() {
        superTrees = calculateSuperTrees();
        TreeResolutionComparator comp = new TreeResolutionComparator();
        Collections.sort(superTrees, comp);
    }


    public Tree calculateGreedyConsensus(TreeSelector selector) {
        return calculateGreedyConsensus(selector, new CLIProgressBar());
    }

    public Tree calculateGreedyConsensus(TreeSelector selector, boolean printProgress) {
        return calculateGreedyConsensus(selector, new CLIProgressBar(CLIProgressBar.DISABLE_PER_DEFAULT && printProgress));
    }

    private Tree calculateGreedyConsensus(TreeSelector selector, final CLIProgressBar progressBar) {

        Tree superCandidate = null;
        Tree pair;

        //progress bar stuff
        int pCount = 0;


        selector.init();
        int trees = selector.getNumberOfTrees() - 1;
        while ((pair = selector.pollTreePair()) != null) {
            progressBar.update(pCount++, trees);
            selector.addTree(pair);
            superCandidate = pair;
        }
        return superCandidate;
    }

    public abstract void setScorer(TreeScorer scorer);

    @Override
    public List<Tree> getResults() {
        if (superTrees == null || superTrees.isEmpty())
            return null;
        return superTrees;
    }

    @Override
    public Tree getResult() {
        if (superTrees == null || superTrees.isEmpty())
            return null;
        return superTrees.get(0);
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }


    //Descending comparator
    protected class TreeResolutionComparator implements Comparator<Tree> {
        //caches scores of already known trees
        private TObjectDoubleHashMap<Tree> scores = new TObjectDoubleHashMap<>();

        @Override
        public int compare(Tree o1, Tree o2) {
            double s1 = scores.get(o1);
            if (s1 == scores.getNoEntryValue()) {
                s1 = caclulateTreeResolution(o1);
                scores.put(o1, s1);
            }

            double s2 = scores.get(o2);
            if (s2 == scores.getNoEntryValue()) {
                s2 = caclulateTreeResolution(o2);
                scores.put(o2, s2);
            }

            return Double.compare(s2, s1);//ATTENTION: wrong order to create a descending comparator
        }

        private double caclulateTreeResolution(Tree tree) {
            return TreeUtils.calculateTreeResolution(tree.getNumTaxa(), tree.vertexCount());
        }

        public double put(Tree tree, double resolution) {
            return scores.put(tree, resolution);
        }
    }

}

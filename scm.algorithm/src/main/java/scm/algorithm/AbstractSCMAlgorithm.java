package scm.algorithm;

import epos.model.algo.SupertreeAlgorithm;
import epos.model.tree.Tree;
import epos.model.tree.treetools.TreeUtilsBasic;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import org.apache.log4j.Logger;
import scm.algorithm.treeSelector.TreeSelector;
import utils.CLIProgressBar;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;

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

    @Override
    public void run() {
        superTrees = calculateSuperTrees();
        TreeResolutionComparator comp = new TreeResolutionComparator();
        Collections.sort(superTrees, comp);
    }


    protected Tree calculateGreedyConsensus(TreeSelector selector, final boolean progress) {
        return calculateGreedyConsensus(selector, false, progress);
    }

    protected Tree calculateGreedyConsensus(final boolean multiRun, TreeSelector selector) {
        return calculateGreedyConsensus(selector, multiRun, false);
    }

    protected Tree calculateGreedyConsensus(TreeSelector selector){
        return calculateGreedyConsensus(selector,false,false);
    }

    private Tree calculateGreedyConsensus(TreeSelector selector, final boolean multiRun, final boolean progress) {
        Tree superCandidate = null;
        Tree pair;

        //progress bar stuff
        CLIProgressBar progressBar = null;
        if (progress)
            progressBar = new CLIProgressBar();
        int pCount = 0;

        selector.init(!multiRun);
        int trees = selector.getNumberOfTrees() - 1;
        while ((pair = selector.pollTreePair()) != null) {
            if (progress)
                progressBar.update(pCount++, trees);
            selector.addTree(pair);
            superCandidate = pair;
        }
        return superCandidate;
    }

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
            return TreeUtilsBasic.calculateTreeResolution(tree.getNumTaxa(), tree.vertexCount());
        }

        public double put(Tree tree, double resolution) {
            return scores.put(tree, resolution);
        }
    }

}

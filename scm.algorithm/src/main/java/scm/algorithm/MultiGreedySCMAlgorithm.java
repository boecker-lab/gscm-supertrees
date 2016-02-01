package scm.algorithm;

import phyloTree.model.tree.Tree;
import scm.algorithm.treeSelector.GreedyTreeSelector;
import scm.algorithm.treeSelector.TreeScorer;
import scm.algorithm.treeSelector.TreeSelector;
import utils.parallel.ParallelUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

/**
 * Created by fleisch on 15.06.15.
 */
public class MultiGreedySCMAlgorithm extends AbstractMultipleResultsSCMAlgorithm {

    public MultiGreedySCMAlgorithm() {
        super();
    }

    public MultiGreedySCMAlgorithm(TreeScorer... scorer) {
        super(scorer);
    }

    public MultiGreedySCMAlgorithm(Tree[] trees) {
        super(trees);
    }

    public MultiGreedySCMAlgorithm(Tree[] trees, TreeScorer... scorer) {
        super(trees, scorer);
    }

    public MultiGreedySCMAlgorithm(Logger logger, ExecutorService executorService) {
        super(logger, executorService);
    }

    public MultiGreedySCMAlgorithm(Logger logger) {
        super(logger);
    }

    @Override
    protected int numOfJobs() {
        return scorerArray.length;
    }

    @Override
    protected List<Tree> calculateSequencial() {
        final TreeSelector selector = new GreedyTreeSelector();
        selector.setInputTrees(inputTrees);
        List<Tree> superTrees = new ArrayList<>(scorerArray.length);
        for (int i = 0; i < scorerArray.length; i++) {
            TreeScorer scorer = scorerArray[i];
            selector.setScorer(scorer);

            superTrees.add(calculateGreedyConsensus(selector, false));
        }
        return superTrees;
    }

    @Override
    protected List<Tree> calculateParallel() {
        GSCMCallableFactory factory = new GSCMCallableFactory(GreedyTreeSelector.getFactory(), inputTrees);
        try {
            List<Tree> supertrees = ParallelUtils.parallelForEachResults(executorService, factory, Arrays.asList(scorerArray));
            return supertrees;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String name() {
        return getClass().getSimpleName();
    }
}

package gscm.algorithm;

import gscm.algorithm.treeSelector.*;
import phyloTree.model.tree.Tree;
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
public class MultiGreedySCMAlgorithm extends MultiResultsSCMAlgorithm {

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
    protected List<Tree> calculateSequencial() throws InsufficientOverlapException {
        final TreeSelector selector = GreedyTreeSelector.FACTORY.getNewSelectorInstance();
        selector.setInputTrees(inputTrees);
        List<Tree> superTrees = new ArrayList<>(scorerArray.length);
        for (int i = 0; i < scorerArray.length; i++) {
            TreeScorer scorer = scorerArray[i];
            selector.setScorer(scorer);

            superTrees.add(calculateGreedyConsensus(selector, false));
        }
        TreeSelectorFactory.shutdown(selector);
        return superTrees;
    }

    @Override
    protected List<Tree> calculateParallel() {
        GSCMCallableFactory factory = new GSCMCallableFactory(GreedyTreeSelector.FACTORY, inputTrees);
        List<Tree> supertrees = null;
        try {
            supertrees = ParallelUtils.parallelForEachResults(executorService, factory, Arrays.asList(scorerArray));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        factory.shutdownSelectors();
        return supertrees;


    }

    @Override
    protected String name() {
        return getClass().getSimpleName();
    }
}

package scmAlgorithm;

import epos.model.tree.Tree;
import org.apache.log4j.Logger;
import parallel.ParallelUtils;
import scmAlgorithm.treeSelector.GreedyTreeSelector;
import scmAlgorithm.treeSelector.TreePair;
import scmAlgorithm.treeSelector.TreeScorer;
import scmAlgorithm.treeSelector.TreeSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

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
    protected List<TreePair> calculateSequencial() {
        final TreeSelector selector = new GreedyTreeSelector();
        List<TreePair> superTrees = new ArrayList<>(scorerArray.length);
        for (int i = 0; i < scorerArray.length; i++) {
            TreeScorer scorer = scorerArray[i];
            selector.setScorer(scorer);
            superTrees.add(calculateGreedyConsensus(selector, false));
        }
        return superTrees;
    }

    @Override
    protected List<TreePair> calculateParallel() {
        GSCMCallableFactory factory = new GSCMCallableFactory(GreedyTreeSelector.getFactory(), inputTrees);
        try {
            List<TreePair> supertrees = ParallelUtils.parallelForEachResults(executorService, factory, Arrays.asList(scorerArray));
            return supertrees;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}

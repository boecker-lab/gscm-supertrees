package scm.algorithm;

import epos.algo.consensus.loose.LooseConsensus;
import phyloTree.model.tree.Tree;

import utils.parallel.DefaultIterationCallable;
import utils.parallel.IterationCallableFactory;

import scm.algorithm.treeSelector.TreeSelectorFactory;
import scm.algorithm.treeSelector.TreeScorer;
import scm.algorithm.treeSelector.TreeSelector;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by fleisch on 29.10.15.
 */
public abstract class AbstractMultipleResultsSCMAlgorithm extends AbstractSCMAlgorithm {
    protected Tree[] inputTrees;
    protected TreeScorer[] scorerArray = null;

    private Tree mergedSupertree = null;

    public AbstractMultipleResultsSCMAlgorithm() {
        super();
    }

    public AbstractMultipleResultsSCMAlgorithm(TreeScorer... scorer) {
        this(null, scorer);
    }

    public AbstractMultipleResultsSCMAlgorithm(Tree[] trees, TreeScorer... scorer) {
        super();
        this.inputTrees = trees;
        if (scorer != null && scorer.length > 0)
            this.scorerArray = scorer;
        else
            this.scorerArray = null;


    }

    public AbstractMultipleResultsSCMAlgorithm(Logger logger, ExecutorService executorService) {
        super(logger, executorService);
    }

    public AbstractMultipleResultsSCMAlgorithm(Logger logger) {
        super(logger);
    }

    public AbstractMultipleResultsSCMAlgorithm(Logger logger,Tree[] trees, TreeScorer... scorer) {
        super(logger);
        this.inputTrees = trees;
        if (scorer != null && scorer.length > 0)
            this.scorerArray = scorer;
        else
            this.scorerArray = null;
    }


    @Override
    public void setInput(List<Tree> trees) {
        setInput(trees.toArray(new Tree[trees.size()]));
    }

    @Override
    public void setInput(Tree... trees) {
        inputTrees = trees;
    }


    @Override
    public void setScorer(TreeScorer scorer) {
        setScorer(new TreeScorer[]{scorer});
    }

    public void setScorer(TreeScorer... scorer) {
        this.scorerArray = scorer;
    }

    /**
     * Returns the merged Supertree, based on the supertreeList
     *
     * @return
     */
    @Override
    public Tree getResult() {
        return getMergedSupertree();
    }

    public Tree getMergedSupertree() {
        if (mergedSupertree == null) {
            List<Tree> superTrees = getResults();
            if (superTrees.size() > 1) {
                LooseConsensus cons = new LooseConsensus();
                cons.setInput(superTrees);
                cons.run();
                mergedSupertree = cons.getResult();
            } else {
                mergedSupertree = superTrees.get(0);
            }
        }
        return mergedSupertree;
    }


    @Override
    protected List<Tree> calculateSuperTrees() {
        mergedSupertree = null;
        final int neededThreads = Math.min(threads, numOfJobs());
        if (neededThreads > 1) {
            if (executorService == null)
                executorService = Executors.newFixedThreadPool(threads);
            List<Tree> superTrees = calculateParallel();
            if (superTrees != null)
                return superTrees;
            else
                LOGGER.severe("parallel execution failed! Calculating tree sequential...");
        }
        return calculateSequencial();
    }

    //abstracts
    protected abstract int numOfJobs();

    protected abstract List<Tree> calculateSequencial();

    protected abstract List<Tree> calculateParallel();


    //helper classes for parallel computing
    class GSCMCallableFactory implements IterationCallableFactory<GSCMCallable, TreeScorer> {
        final TreeSelectorFactory selectorFactory;
        final Tree[] inputTrees;
        final TreeScorer scorer;

        public GSCMCallableFactory(TreeSelectorFactory factory, Tree[] inputTrees) {
            this(factory, null, inputTrees);
        }

        public GSCMCallableFactory(TreeSelectorFactory factory, TreeScorer scorer) {
            this(factory, scorer, null);
        }

        public GSCMCallableFactory(TreeSelectorFactory factory, TreeScorer scorer, Tree[] inputTrees) {
            this.selectorFactory = factory;
            this.inputTrees = inputTrees;
            this.scorer = scorer;
        }

        @Override
        public GSCMCallable newIterationCallable(List<TreeScorer> list) {
            TreeSelector s = selectorFactory.newTreeSelectorInstance();
            if (scorer != null)
                s.setScorer(scorer);
            if (inputTrees != null)
                s.setInputTrees(inputTrees);
            return new GSCMCallable(list, s);
        }
    }

    private class GSCMCallable extends DefaultIterationCallable<TreeScorer, Tree> {
        final TreeSelector selector;

        GSCMCallable(List<TreeScorer> jobs, TreeSelector selector) {
            super(jobs);
            this.selector = selector;
        }

        @Override
        public Tree doJob(TreeScorer scorer) {
            selector.setScorer(scorer);
            selector.setClearScorer(false);
            return calculateGreedyConsensus(selector,false);
        }
    }

}

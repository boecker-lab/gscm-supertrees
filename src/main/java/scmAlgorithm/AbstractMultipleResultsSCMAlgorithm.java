package scmAlgorithm;

import epos.algo.consensus.loose.LooseConsensus;
import epos.model.tree.Tree;
import org.apache.log4j.Logger;
import parallel.DefaultIterationCallable;
import parallel.IterationCallableFactory;
import scmAlgorithm.treeSelector.TreeScorer;
import scmAlgorithm.treeSelector.TreeSelector;
import scmAlgorithm.treeSelector.TreeSelectorFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public AbstractMultipleResultsSCMAlgorithm(Tree[] trees) {
        this(trees, null);
    }

    public AbstractMultipleResultsSCMAlgorithm(Tree[] trees, TreeScorer... scorer) {
        this();
        this.inputTrees = trees;
        this.scorerArray = scorer;
    }

    public AbstractMultipleResultsSCMAlgorithm(Logger logger, ExecutorService executorService) {
        super(logger, executorService);
    }

    public AbstractMultipleResultsSCMAlgorithm(Logger logger) {
        super(logger);
    }




    @Override
    public void setInput(List<Tree> trees) {
        setInput(trees.toArray(new Tree[trees.size()]));
    }

    public void setInput(Tree... trees) {
        inputTrees = trees;
    }

    public void setScorer(TreeScorer... scorer) {
        this.scorerArray = scorer;
    }

    /**
     * Returns the merged Supertree, based on the supertreeList
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
            }else{
                mergedSupertree = superTrees.get(0);
            }
        }
        return mergedSupertree;
    }



    @Override
    protected List<Tree> calculateSuperTrees() {
        mergedSupertree =  null;
        final int neededThreads = Math.min(threads, numOfJobs());
        if (neededThreads > 1) {
            if (executorService == null)
                executorService = Executors.newFixedThreadPool(threads);
            List<Tree> superTrees = calculateParallel();
            if (superTrees != null)
                return superTrees;
            else
                System.err.println("paralle execution failed! Calculating tree sequential...");
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

        public GSCMCallableFactory(TreeSelectorFactory factory,Tree[] inputTrees) {
            this(factory,null,inputTrees);
        }

        public GSCMCallableFactory(TreeSelectorFactory factory, TreeScorer scorer) {
            this(factory,scorer,null);
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
            return calculateGreedyConsensus(true,selector);
        }
    }

}

/*
 * GSCM-Project
 * Copyright (C)  2016. Chair of Bioinformatics, Friedrich-Schilller University Jena.
 *
 * This file is part of the GSCM-Project.
 *
 * The GSCM-Project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The GSCM-Project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GSCM-Project.  If not, see <http://www.gnu.org/licenses/>;.
 *
 */
package gscm.algorithm.treeMerger;

import epos.algo.consensus.Consensus;
import phyloTree.algorithm.exceptions.InsufficientOverlapException;
import phyloTree.model.tree.Tree;
import utils.parallel.DefaultIterationCallable;
import utils.parallel.IterationCallableFactory;
import utils.parallel.ParallelUtils;
import utils.progressBar.CLIProgressBar;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 23.10.15.
 */

/**
 * Base Method for greedy tree merger algorithms
 * all tree merger implementation should extend this Method
 *
 * @author Markus Fleischauer (markus.fleischauer@gmail.com)
 * @since version 1.0
 */
public abstract class TreeMerger {

    private int threads = 1;
    private ExecutorService executor;
    private TreePairScoringCallableFactory factory;
    private Consensus.ConsensusMethod method;

    protected boolean clearScorer = true;

    TreeScorer scorer;
    Tree[] inputTrees;


    protected TreeMerger(Consensus.ConsensusMethod method) {
        this.method = method;
    }

    protected TreeMerger() {
        this(Consensus.ConsensusMethod.STRICT);
    }


    protected void clearScorer() {
        Set<Tree> ts = new HashSet<>(inputTrees.length);
        Collections.addAll(ts, inputTrees);
        scorer.clearCache(ts);
    }

    protected void createPairs() {
        if (threads > 1) {
            try {
                createPairsParallel();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            createPairsSequencial();
        }
    }

    private void createPairsSequencial() {
        for (int i = 0; i < inputTrees.length - 1; i++) {
            Tree t1 = inputTrees[i];
            for (int j = i + 1; j < inputTrees.length; j++) {
                Tree t2 = inputTrees[j];
                final TreePair pair = new TreePair(t1, t2, scorer, method);
                if (!pair.isInsufficient()) {
                    addTreePair(pair);
                }
            }
        }
    }

    private void createPairsParallel() throws ExecutionException, InterruptedException {
        final int size = (inputTrees.length * (inputTrees.length - 1)) / 2;
        final List<TreePair> pairsToAdd = new ArrayList<>(size);

        //sequential pair creation
        for (int i = 0; i < inputTrees.length - 1; i++) {
            Tree t1 = inputTrees[i];
            for (int j = i + 1; j < inputTrees.length; j++) {
                Tree t2 = inputTrees[j];
                pairsToAdd.add(
                        new TreePair(t1, t2, method));
            }
        }
        // parallel scoring --> we have to score the pairs before adding them into the SORTED data structure
        scoreAndAddPairsParallel(pairsToAdd);
    }

    // adds tree and its paires to the data structure (O(2nlog(n)))
    private boolean addTree(final Tree tree) {
        try {
            if (threads > 1) {
                return addTreeParallel(tree);
            } else {
                return addTreeSequencial(tree);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean addTreeParallel(final Tree tree) throws ExecutionException, InterruptedException {
        final Collection<Tree> remainingTrees = getRemainingTrees();
        if (remainingTrees.isEmpty())
            return false;
        //iterate over trees (O(n)) to add to new list and refresh old entries
        List<TreePair> pairsToAdd = new ArrayList<>(remainingTrees.size());
        for (Tree old : remainingTrees) {
            pairsToAdd.add(
                    new TreePair(tree, old, method));
        }

        //parralel scoring O(n))
        scoreAndAddPairsParallel(pairsToAdd);
        return true;
    }


    private void scoreAndAddPairsParallel(final List<TreePair> pairsToAdd) throws ExecutionException, InterruptedException {
        //parralel scoring
        if (executor == null)
            executor = Executors.newFixedThreadPool(threads);
        if (factory == null)
            factory = new TreePairScoringCallableFactory();
        List<Future<List<TreePair>>> submittedJobs = ParallelUtils.parallelBucketForEach(executor, factory, pairsToAdd, threads);

        //sequential adding
        for (Future<List<TreePair>> futures : submittedJobs) {
            for (TreePair pair : futures.get()) {
                if (pair != null && !pair.isInsufficient()) {
                    addTreePair(pair);
                }
            }
        }

    }

    private boolean addTreeSequencial(final Tree tree) {
        final Collection<Tree> remainingTrees = getRemainingTrees();
        if (remainingTrees.isEmpty())
            return false;
        //iterate over trees (O(n)) to add to new list and refresh old entries
        for (Tree old : remainingTrees) {
            TreePair pair = new TreePair(tree, old, scorer, method);
            if (!pair.isInsufficient()) {
                addTreePair(pair);
            }
        }

        return true;
    }


    //polls treepair from data structure
    private Tree pollTreePair() {
        TreePair tp = getMax();
        if (tp == TreePair.MIN_VALUE) {
            return null;
        } else {
            removeTreePair(tp);
            return tp.getConsensus();
        }
    }

    /**
     * Calculates a greedy strict consensus merger supertree using the given scoring.
     * This should be used by all classes extending {@link TreeMerger}
     * to not have to reimplement the general workflow.
     *
     * This methods prints the progress to standard out
     *
     * @return gscm supertree
     * @throws InsufficientOverlapException
     */
    public Tree calculateGreedyConsensus() throws InsufficientOverlapException {
        return calculateGreedyConsensus(new CLIProgressBar());
    }

    /**
     * Calculates a greedy strict consensus merger supertree using the given scoring.
     * This should be used by all classes extending {@link TreeMerger}
     * to not have to reimplement the general workflow.
     *
     *This methods does not print progress
     *
     * @return gscm supertree
     * @throws InsufficientOverlapException
     */
    public Tree calculateGreedyConsensus(boolean printProgress) throws InsufficientOverlapException {
        return calculateGreedyConsensus(new CLIProgressBar(CLIProgressBar.DISABLE_PER_DEFAULT || !printProgress));
    }


    /**
     * @return Merged supertree
     */
    private Tree calculateGreedyConsensus(final CLIProgressBar progressBar) throws InsufficientOverlapException {
        Tree superCandidate = null;
        Tree pair;

        //progress bar stuff
        int pCount = 0;

        init();
        int trees = getNumberOfInputTrees() - 1;
        while ((pair = pollTreePair()) != null) {
            progressBar.update(pCount++, trees);
            addTree(pair);
            superCandidate = pair;
        }
        if (getNumberOfRemainingTrees() > 0) {
            throw new InsufficientOverlapException();
        } else {
            return superCandidate;
        }
    }


    protected abstract void init();

    /**
     * Returns the number of Trees have to be merged.
     * If this method return 1 the gscm algorithm is done
     *
     * @return Number of tree left
     */
    protected abstract int getNumberOfRemainingTrees();

    /**
     * Adds a tree pair to the data structure
     *
     * @param t tree for wich the p should be added
     * @param p pair to add into data structure
     */
    protected abstract void addTreePair(Tree t, TreePair p);
    protected void addTreePair(TreePair p){
        addTreePair(p.t1,p);
        addTreePair(p.t2,p);
    }

    /**
     * remove merged treepair from data structure
     *
     * @param pair pair to remove from data structure
     */
    protected abstract void removeTreePair(TreePair pair);

    /**
     * Returns the trees that are left and can be merged
     *
     * @return  remaining trees
     */
    protected abstract Collection<Tree> getRemainingTrees();

    /**
     * Returns the {@link TreePair} with the highest score
     *
     * @return  {@link TreePair} with highest score
     */
    protected abstract TreePair getMax();

    /**
     *
     * @return the given Scorer
     */
    public TreeScorer getScorer() {
        return scorer;
    }
    /**
     *
     * @param scorer scorer to score {@link TreePair}s
     */
    public void setScorer(TreeScorer scorer) {
        this.scorer = scorer;
    }

    /**
     *
     * @return Consensus algorithm to merge the {@link TreePair}s
     */
    public Consensus.ConsensusMethod getMethod() {
        return method;
    }

    /**
     *
     * @param threads number of thread that may be used
     */
    public void setThreads(int threads) {
        this.threads = threads;
    }

    /**
     *
     * @param executor ExecutorService for multi threaded computation
     */
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Specifies if Scorer cache has to be cleared after computation
     * @param clearScorer set true if Scorer cache has to be cleared
     */
    public void setClearScorer(boolean clearScorer) {
        this.clearScorer = clearScorer;
    }

    /**
     * shut all executor services down
     * @return true if executer service exists
     */
    public boolean shutdown() {
        if (executor != null) {
            executor.shutdown();
            return true;
        }
        return false;
    }

    /**
     * Sets the input trees to merge
     * @param inputTrees trees to merge
     */
    public void setInputTrees(Tree... inputTrees) {
        this.inputTrees = inputTrees;
    }

    /**
     * @return number of trees to merge
     */
    public int getNumberOfInputTrees() {
        return inputTrees.length;
    }


    private class TreePairScoringCallable extends DefaultIterationCallable<TreePair, TreePair> {
        protected TreePairScoringCallable(List<TreePair> jobs) {
            super(jobs);
        }

        @Override
        public TreePair doJob(TreePair pair) {
            return pair.calculateScore(scorer);
        }

    }

    private class TreePairScoringCallableFactory implements IterationCallableFactory<TreePairScoringCallable, TreePair> {
        @Override
        public TreePairScoringCallable newIterationCallable(List<TreePair> list) {
            return new TreePairScoringCallable(list);
        }
    }
}

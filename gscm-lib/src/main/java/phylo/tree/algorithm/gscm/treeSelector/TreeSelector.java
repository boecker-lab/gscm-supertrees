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
package phylo.tree.algorithm.gscm.treeSelector;

import core.utils.parallel.DefaultIterationCallable;
import core.utils.parallel.IterationCallableFactory;
import core.utils.parallel.ParallelUtils;
import core.utils.progressBar.CLIProgressBar;
import phylo.tree.algorithm.consensus.Consensus;
import phylo.tree.algorithm.exceptions.InsufficientOverlapException;
import phylo.tree.algorithm.gscm.SCMAlgorithm;
import phylo.tree.model.Tree;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 23.10.15.
 */
public abstract class TreeSelector<S extends TreeScorer> {

    private int threads = 1;
    private ExecutorService executor;
    private IterationCallableFactory<TreePairInitCallable, TreePair> factory;
    private Consensus.ConsensusMethod method;

    protected boolean clearScorer = true;

    S scorer;
    Tree[] inputTrees;


    protected TreeSelector(Consensus.ConsensusMethod method) {
        this.method = method;
    }

    protected TreeSelector() {
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
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
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
                final TreePair pair = initTreePair(new TreePair(t1, t2,method));
                if (pair != null && !pair.isInsufficient()) {
                    addTreePair(t1, pair);
                    addTreePair(t2, pair);
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

    protected TreePair initTreePair(TreePair pair) {
        return pair.calculateScore(scorer);
    }

    // adds tree and its paires to the data structure (O(2nlog(n)))
    private boolean addTree(final Tree tree) {
        try {
            if (threads > 1) {
                return addTreeParallel(tree);
            } else {
                return addTreeSequencial(tree);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
            factory = TreePairInitCallable::new;
        List<Future<List<TreePair>>> submittedJobs = ParallelUtils.parallelBucketForEach(executor, factory, pairsToAdd, threads);

        //sequential adding
        for (Future<List<TreePair>> futures : submittedJobs) {
            for (TreePair pair : futures.get()) {
                if (pair != null && !pair.isInsufficient()) {
                    addTreePair(pair.t1, pair);
                    addTreePair(pair.t2, pair);
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
            TreePair pair = initTreePair(new TreePair(tree, old,method));
            if (pair != null && !pair.isInsufficient()) {
                addTreePair(tree, pair);
                addTreePair(old, pair);
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

//todo proof comments
    /**
     * Calculates a greedy strict consensus merger supertree using the given scoring.
     * This should be used by all classes extending {@link SCMAlgorithm}
     * to not have to reimplement the workflow.
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
     * Calculates a greedy strict consensus merger supertree using the given {@link TreeSelector}.
     * This should be used by all classes extending {@link SCMAlgorithm}
     * to not have to reimplement the workflow.
     *
     * This methods does not print progress
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
     * Return the number of Trees have to be merged.
     * If this method return 1 the gscm algorithm is done
     *
     * @return Number of tree left
     */
    protected abstract int getNumberOfRemainingTrees();

    protected abstract void addTreePair(Tree t, TreePair p);
//    protected abstract TreePair initTreePair(TreePair pair);

    protected abstract void removeTreePair(TreePair pair);

    protected abstract Collection<Tree> getRemainingTrees();

    protected abstract TreePair getMax();

    public S getScorer() {
        return scorer;
    }

    public void setScorer(S scorer) {
        this.scorer = scorer;
    }

    public Consensus.ConsensusMethod getMethod() {
        return method;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void setClearScorer(boolean clearScorer) {
        this.clearScorer = clearScorer;
    }

    public boolean shutdown() {
        if (executor != null) {
            executor.shutdown();
            return true;
        }
        return false;
    }

    public void setInputTrees(Tree... inputTrees) {
        this.inputTrees = inputTrees;
    }

    public int getNumberOfInputTrees() {
        return inputTrees.length;
    }


    private class TreePairInitCallable extends DefaultIterationCallable<TreePair, TreePair> {
        protected TreePairInitCallable(List<TreePair> jobs) {
            super(jobs);
        }

        @Override
        public TreePair doJob(TreePair pair) {
            return initTreePair(pair);
        }

    }
}

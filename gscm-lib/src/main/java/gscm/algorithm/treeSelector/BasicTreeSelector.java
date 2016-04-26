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
package gscm.algorithm.treeSelector;

import phyloTree.algorithm.SupertreeAlgorithm;
import phyloTree.model.tree.Tree;
import utils.parallel.DefaultIterationCallable;
import utils.parallel.IterationCallableFactory;
import utils.parallel.ParallelUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 23.10.15.
 */
public abstract class BasicTreeSelector implements TreeSelector {
    private int threads = 1;
    private ExecutorService executor;
    private TreePairScoringCallableFactory factory;
    private ConsensusMethod method;

    protected boolean clearScorer = true;

    TreeScorer scorer;
    Tree[] inputTrees;


    protected BasicTreeSelector(ConsensusMethod method) {
        this.method = method;
    }

    protected BasicTreeSelector() {
        this(ConsensusMethod.STRICT);
    }


    @Override
    public void setInputTrees(Tree... inputTrees) {
        this.inputTrees = inputTrees;
    }

    void clearScorer() {
        Set<Tree> ts = new HashSet<>(inputTrees.length);
        Collections.addAll(ts, inputTrees);
        scorer.clearCache(ts);
    }

    void createPairs() {
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
                final TreePair pair = new TreePair(t1, t2, scorer, newConsensusCalculatorInstance(method));
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
                        new TreePair(t1, t2, newConsensusCalculatorInstance(method)));
            }
        }
        // parallel scoring --> we have to score the pairs before adding them into the SORTED data structure
        scoreAndAddPairsParallel(pairsToAdd);
    }

    // adds tree and its paires to the data structure (O(2nlog(n)))
    public boolean addTree(final Tree tree) {
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
                    new TreePair(tree, old, newConsensusCalculatorInstance()));
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
            TreePair pair = new TreePair(tree, old, scorer, newConsensusCalculatorInstance());
            if (pair != null && !pair.isInsufficient()) {
                addTreePair(tree, pair);
                addTreePair(old, pair);
            }
        }

        /*if (treeToPairs.get(tree) == null || treeToPairs.get(tree).isEmpty())
            throw new IllegalArgumentException("Input tree have insufficient Overlap to calculate a supertree");*/
        return true;
    }


    //polls treepair from data structure
    public Tree pollTreePair() {
        TreePair tp = getMax();
        if (tp == TreePair.MIN_VALUE) {
            return null;
        } else {
            removeTreePair(tp);
            return tp.getConsensus();
        }
    }

    abstract void addTreePair(Tree t, TreePair p);

    abstract void removeTreePair(TreePair pair);

    abstract Collection<Tree> getRemainingTrees();

    abstract TreePair getMax();

    @Override
    public TreeScorer getScorer() {
        return scorer;
    }

    @Override
    public void setScorer(TreeScorer scorer) {
        this.scorer = scorer;
    }

    public ConsensusMethod getMethod() {
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

    @Override
    public boolean shutdown() {
        if (executor != null) {
            executor.shutdown();
            return true;
        }
        return false;
    }

    @Override
    public int getNumberOfInputTrees() {
        return inputTrees.length;
    }

    SupertreeAlgorithm newConsensusCalculatorInstance() {
        return newConsensusCalculatorInstance(method);
    }
    //abstract classes


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

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
package gscm.algorithm;

import gscm.algorithm.treeSelector.InsufficientOverlapException;
import gscm.algorithm.treeSelector.TreeScorer;
import gscm.algorithm.treeSelector.TreeSelector;
import gscm.algorithm.treeSelector.TreeSelectorFactory;
import phylo.tree.algorithm.consensus.Consensus;
import phylo.tree.model.Tree;
import utils.parallel.DefaultIterationCallable;
import utils.parallel.IterationCallableFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 29.10.15.
 */

/**
 * This class defines the basic algorithm for greedy strict
 * consensus merger implemetations that handle multiple scorings
 * and return multiple results. It also merges them into a single supertree
 *
 * @author Markus Fleischauer (markus.fleischauer@gmail.com)
 * @since version 1.0
 */
public abstract class MultiResultsSCMAlgorithm extends SCMAlgorithm {
    protected Tree[] inputTrees;
    protected TreeScorer[] scorerArray = null;

    private Tree mergedSupertree = null;

    public MultiResultsSCMAlgorithm() {
        super();
    }

    public MultiResultsSCMAlgorithm(TreeScorer... scorer) {
        this(null, scorer);
    }

    public MultiResultsSCMAlgorithm(Tree[] trees, TreeScorer... scorer) {
        super();
        this.inputTrees = trees;
        if (scorer != null && scorer.length > 0)
            this.scorerArray = scorer;
        else
            this.scorerArray = null;


    }

    public MultiResultsSCMAlgorithm(Logger logger, ExecutorService executorService) {
        super(logger, executorService);
    }

    public MultiResultsSCMAlgorithm(Logger logger) {
        super(logger);
    }

    public MultiResultsSCMAlgorithm(Logger logger, Tree[] trees, TreeScorer... scorer) {
        super(logger);
        this.inputTrees = trees;
        if (scorer != null && scorer.length > 0)
            this.scorerArray = scorer;
        else
            this.scorerArray = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInput(List<Tree> trees) {
        inputTrees = trees.toArray(new Tree[trees.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScorer(TreeScorer scorer) {
        setScorer(new TreeScorer[]{scorer});
    }

    public void setScorer(TreeScorer... scorer) {
        this.scorerArray = scorer;
    }

    /**
     * Returns the merged Supertree,of all calculated supertrees
     *
     * @return merged supertree
     */
    @Override
    public Tree getResult() {
        return getMergedSupertree();
    }

    /**
     * Returns the merged Supertree,of all calculated supertrees
     *
     * @return merged supertree
     */
    public Tree getMergedSupertree() {
        if (mergedSupertree == null) {
            List<Tree> superTrees = getResults();
            if (superTrees.size() > 1) {
                mergedSupertree = Consensus.getLoosConsensus(superTrees);
            } else {
                mergedSupertree = superTrees.get(0);
            }
        }
        return mergedSupertree;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Tree> calculateSuperTrees() throws InsufficientOverlapException {
        mergedSupertree = null;
        final int neededThreads = Math.min(threads, numOfJobs());
        List<Tree> superTrees = null;
        if (neededThreads > 1) {
            if (executorService == null)
                executorService = Executors.newFixedThreadPool(threads);
            superTrees = calculateParallel();
            if (superTrees == null)
                LOGGER.severe("parallel execution failed! Calculating tree sequential...");
        }
        if (superTrees == null)
            superTrees = calculateSequencial();

        return superTrees;
    }

    //abstracts

    /**
     * @return number of jobs that can be done in parallel
     */
    protected abstract int numOfJobs();

    /**
     * Single threaded Implementation of the algorithm.
     * If there is no single threaded Implementation call the multithreaded implementation {@link MultiResultsSCMAlgorithm#calculateParallel()}
     *
     * @return List of supertrees
     */
    protected abstract List<Tree> calculateSequencial() throws InsufficientOverlapException;

    /**
     * Multi threaded Implementation of the algorithm.
     * If there is no multi threaded Implementation call the single threaded implementation {@link MultiResultsSCMAlgorithm#calculateSequencial()}
     *
     * @return List of supertrees
     */
    protected abstract List<Tree> calculateParallel() throws InsufficientOverlapException;


    //helper classes for parallel computing
    class GSCMCallableFactory implements IterationCallableFactory<GSCMCallable, TreeScorer> {
        private final Set<TreeSelector> selectors = new HashSet<>();
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
            TreeSelector s = selectorFactory.getNewSelectorInstance();
            selectors.add(s);
            if (scorer != null)
                s.setScorer(scorer);
            if (inputTrees != null)
                s.setInputTrees(inputTrees);
            return new GSCMCallable(list, s);
        }

        public void shutdownSelectors(){
            selectors.forEach(TreeSelectorFactory::shutdown);
            selectors.clear();
        }
    }

    private class GSCMCallable extends DefaultIterationCallable<TreeScorer, Tree> {
        final TreeSelector selector;

        GSCMCallable(List<TreeScorer> jobs, TreeSelector selector) {
            super(jobs);
            this.selector = selector;
        }

        @Override
        public Tree doJob(TreeScorer scorer) throws InsufficientOverlapException {
            selector.setScorer(scorer);
            selector.setClearScorer(false);
            return selector.calculateGreedyConsensus(false);
        }
    }



}

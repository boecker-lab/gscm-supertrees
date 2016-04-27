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
 * This is a wrapper executing the {@link gscm.algorithm.GreedySCMAlgorithm}
 * for multiple scoring functions and merges the resulting supertrees into a single supertree
 *
 * @author Markus Fleischauer (markus.fleischauer@gmail.com)
 * @since version 1.0
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected int numOfJobs() {
        return scorerArray.length;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected String name() {
        return getClass().getSimpleName();
    }
}

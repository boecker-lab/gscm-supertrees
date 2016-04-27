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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 10.02.15.
 */

/**
 * Classical greedy strict consensus merger implementation extending {@link gscm.algorithm.SCMAlgorithm}.
 *
 * @author Markus Fleischauer (markus.fleischauer@gmail.com)
 * @since version 1.0
 */
public class GreedySCMAlgorithm extends SCMAlgorithm {
    final TreeSelector selector;

    public GreedySCMAlgorithm() {
        super();
        this.selector = GreedyTreeSelector.FACTORY.getNewSelectorInstance();
    }

    public GreedySCMAlgorithm(Logger logger, ExecutorService executorService) {
        super(logger, executorService);
        this.selector = GreedyTreeSelector.FACTORY.getNewSelectorInstance();
    }

    public GreedySCMAlgorithm(Logger logger) {
        super(logger);
        this.selector = GreedyTreeSelector.FACTORY.getNewSelectorInstance();
    }

    public GreedySCMAlgorithm(TreeScorer scorer) {
        this();
        this.selector.setScorer(scorer);
    }

    public GreedySCMAlgorithm(TreeSelector selector) {
        super();
        this.selector = selector;
    }

    public GreedySCMAlgorithm(Logger logger, ExecutorService executorService, TreeSelector selector) {
        super(logger, executorService);
        this.selector = selector;
    }

    public GreedySCMAlgorithm(Logger logger, TreeSelector selector) {
        super(logger);
        this.selector = selector;
    }

    @Override
    protected String name() {
        return getClass().getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScorer(TreeScorer scorer) {
        this.selector.setScorer(scorer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInput(List<Tree> trees) {
        selector.setInputTrees(trees.toArray(new Tree[trees.size()]));
    }

    /**
     * Classical  greedy strict consensus merger Implementation calculating a single supertree for a given
     * scoring function. running time highly depends on the scoring method
     *
     * @return a List containing single supertree
     * @throws InsufficientOverlapException
     */
    @Override
    protected List<Tree> calculateSuperTrees() throws InsufficientOverlapException {
        return new ArrayList<>(Arrays.asList(calculateSuperTree()));
    }

    /**
     * Classical  greedy strict consensus merger Implementation calculating a single supertree for a given
     * scoring function. running time highly depends on the scoring method
     *
     * @return supertree
     * @throws InsufficientOverlapException
     */
    Tree calculateSuperTree() throws InsufficientOverlapException {
        return calculateGreedyConsensus(selector);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shutdown() {
        TreeSelectorFactory.shutdown(selector);
        return super.shutdown();
    }
}

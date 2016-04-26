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


import gnu.trove.map.hash.TObjectDoubleHashMap;
import gscm.algorithm.treeSelector.InsufficientOverlapException;
import gscm.algorithm.treeSelector.TreeScorer;
import gscm.algorithm.treeSelector.TreeSelector;
import phyloTree.algorithm.SupertreeAlgorithm;
import phyloTree.model.tree.Tree;
import phyloTree.model.tree.TreeUtils;
import utils.progressBar.CLIProgressBar;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 05.02.15.
 */
public abstract class SCMAlgorithm extends SupertreeAlgorithm {
    private List<Tree> superTrees;
    protected int threads;

    public SCMAlgorithm(Logger logger, ExecutorService executorService) {
        super(logger, executorService);
    }

    public SCMAlgorithm(Logger logger) {
        super(logger);
    }

    public SCMAlgorithm() {
        super();
    }

    protected abstract List<Tree> calculateSuperTrees() throws Exception;

    public abstract void setInput(Tree... trees);

    @Override
    public SupertreeAlgorithm call() throws Exception {
        superTrees = calculateSuperTrees();
        TreeResolutionComparator comp = new TreeResolutionComparator();
        Collections.sort(superTrees, comp);
        return this;
    }


    public Tree calculateGreedyConsensus(TreeSelector selector) throws InsufficientOverlapException {
        return calculateGreedyConsensus(selector, new CLIProgressBar());
    }

    public Tree calculateGreedyConsensus(TreeSelector selector, boolean printProgress) throws InsufficientOverlapException {
        return calculateGreedyConsensus(selector, new CLIProgressBar(CLIProgressBar.DISABLE_PER_DEFAULT || !printProgress));
    }

    private Tree calculateGreedyConsensus(TreeSelector selector, final CLIProgressBar progressBar) throws InsufficientOverlapException {
        Tree superCandidate = null;
        Tree pair;

        //progress bar stuff
        int pCount = 0;

        selector.init();
        int trees = selector.getNumberOfInputTrees() - 1;
        while ((pair = selector.pollTreePair()) != null) {
            progressBar.update(pCount++, trees);
            selector.addTree(pair);
            superCandidate = pair;
        }
        if (selector.getNumberOfRemainingTrees() > 0) {
            throw new InsufficientOverlapException();
        } else {
            return superCandidate;
        }
    }

    public abstract void setScorer(TreeScorer scorer);

    @Override
    public List<Tree> getResults() {
        if (superTrees == null || superTrees.isEmpty())
            return null;
        return superTrees;
    }

    @Override
    public Tree getResult() {
        if (superTrees == null || superTrees.isEmpty())
            return null;
        return superTrees.get(0);
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }


    //Descending comparator
    protected class TreeResolutionComparator implements Comparator<Tree> {
        //caches scores of already known trees
        private TObjectDoubleHashMap<Tree> scores = new TObjectDoubleHashMap<>();

        @Override
        public int compare(Tree o1, Tree o2) {
            double s1 = scores.get(o1);
            if (s1 == scores.getNoEntryValue()) {
                s1 = caclulateTreeResolution(o1);
                scores.put(o1, s1);
            }

            double s2 = scores.get(o2);
            if (s2 == scores.getNoEntryValue()) {
                s2 = caclulateTreeResolution(o2);
                scores.put(o2, s2);
            }

            return Double.compare(s2, s1);//ATTENTION: wrong order to create a descending comparator
        }

        private double caclulateTreeResolution(Tree tree) {
            return TreeUtils.calculateTreeResolution(tree.getNumTaxa(), tree.vertexCount());
        }

        public double put(Tree tree, double resolution) {
            return scores.put(tree, resolution);
        }
    }

}

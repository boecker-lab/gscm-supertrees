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

import epos.algo.consensus.adams.AdamsConsensus;
import epos.algo.consensus.loose.LooseConsensus;
import epos.algo.consensus.nconsensus.NConsensus;
import phyloTree.algorithm.SupertreeAlgorithm;
import phyloTree.model.tree.Tree;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 14.10.15.
 */
public interface TreeSelector {
    enum ConsensusMethod {SEMI_STRICT, STRICT, MAJORITY, ADAMS}
    void setClearScorer(boolean clearScorer);
    void init();

    Tree pollTreePair ();
    int getNumberOfRemainingTrees();

    boolean addTree(Tree tree);

    void setInputTrees(Tree[] trees);
    int getNumberOfInputTrees();

    TreeScorer getScorer();
    void setScorer(TreeScorer scorer);

    default SupertreeAlgorithm newConsensusCalculatorInstance(final ConsensusMethod METHOD) {
        SupertreeAlgorithm a;
        switch (METHOD) {
            case STRICT:
                a = new NConsensus();
                ((NConsensus) a).setThreshold(1D);
                break;
            case MAJORITY:
                a = new NConsensus();
                ((NConsensus) a).setThreshold(0.5D);
                break; // is same as strict for 2 trees...
            case SEMI_STRICT:
                a = new LooseConsensus();
                break;
            case ADAMS:
                a = new AdamsConsensus();
                break;
            default:
                a = null;
        }
        return a;
    }

    boolean shutdown();


}

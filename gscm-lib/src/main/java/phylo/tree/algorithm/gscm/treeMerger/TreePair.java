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
package phylo.tree.algorithm.gscm.treeMerger;

import phylo.tree.algorithm.consensus.Consensus;
import phylo.tree.model.Tree;

/**
 * This class stores a Pair of trees and their scores
 * I does the pairwise merging (strict consensus merger) step
 * and caches the consensus tree and other interim result
 * depending on the given scoring function
 *
 * @author Markus Fleischauer (markus.fleischauer@gmail.com)
 * @since version 1.0
 */
class TreePair implements Comparable<TreePair> {
    final static TreePair MIN_VALUE = new TreePair();

    final Tree t1;
    final Tree t2;

    double score;
    double tieBreakingScore = 0d;

    //just to create min value
    private TreePair() {
        t1 = null;
        t2 = null;
        score = Double.NEGATIVE_INFINITY;
    }

    TreePair(final Tree t1, final Tree t2, TreeScorer scorer, Consensus.ConsensusMethod method) {
        this(t1, t2);
        calculateScore(scorer,method);
    }


    TreePair(final Tree t1, final Tree t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    TreePair calculateScore(TreeScorer scorer, Consensus.ConsensusMethod method) {
        scorer.scoreTreePair(this,method);
        return this;
    }

    //unchecked
    Tree getPartner(Tree t) {
        if (t.equals(t1))
            return t2;
        else if (t.equals(t2))
            return t1;
        return null;
    }


    boolean isInsufficient() {
        return score <= Double.NEGATIVE_INFINITY;
    }

    @Override
    public int compareTo(TreePair o) {
        int comp = Double.compare(o.score, score); //ATTENTION --> Descending ordering
        if (comp == 0) {
            comp = Double.compare(o.tieBreakingScore, tieBreakingScore);
        }
        return comp;
    }

    @Override
    public String toString() {
        return "TreePair{" +
                "t1=" + t1 +
                ", t2=" + t2 +
                ", score=" + score +
                '}';
    }
}
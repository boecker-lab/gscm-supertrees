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

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import phylo.tree.model.tree.Tree;
import phylo.tree.model.tree.TreeNode;
import phylo.tree.model.tree.TreeUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 06.02.15.
 */

/**
 * Basic TreeScorer template.
 * All scoring functions have to extend this Method
 *
 * @author Markus Fleischauer (markus.fleischauer@gmail.com)
 * @since version 1.0
 */
public abstract class TreeScorer {
    final Map<Tree, THashSet<String>> treeToTaxa;
    public final boolean synced;
    public final static boolean TIE_BREAKER = true;

    public TreeScorer() {
        this(true);
    }

    public TreeScorer(boolean syncedCache) {
        synced = syncedCache;
        if (synced) {
            treeToTaxa = new ConcurrentHashMap<>();
        } else {
            treeToTaxa = new THashMap<>();
        }

    }

    THashSet<String> calculateCommonLeafes(TreePair pair) {
        THashSet<String> ts1 = treeToTaxa.get(pair.t1);
        THashSet<String> ts2 = treeToTaxa.get(pair.t2);
        if (ts1 == null) {
            ts1 = createTreeEntry(pair.t1);
        }
        if (ts2 == null) {
            ts2 = createTreeEntry(pair.t2);
        }

        THashSet<String> tcommon = new THashSet<>(ts1.size());

        tcommon.addAll(ts1);
        tcommon.retainAll(ts2);

        return tcommon;
    }


    THashSet<String> getLeafLabels(Tree tree1) {
        THashSet<String> taxonSet = new THashSet<>(tree1.vertexCount());
        for (TreeNode taxon : tree1.getRoot().depthFirstIterator()) {
            if (taxon.isLeaf()) {
                taxonSet.add(taxon.getLabel());
            }
        }
        return taxonSet;
    }


    private THashSet<String> createTreeEntry(Tree tree1) {
        THashSet<String> s = getLeafLabels(tree1);
        treeToTaxa.put(tree1, s);
        return s;
    }

    /**
     * Score the given tree pair an calculates the set of common taxa.
     * The running time of the scoring highlly depends of the scoring function
     * implemetation. Some scoring may already calculate the consesus merger tree.
     * If that is the case the consensus tree is chached and not calculated a second tim
     *
     * Note: The given {@link gscm.algorithm.treeMerger.TreePair} gets modified.
     *
     * @param pair pair to score
     */
    public void scoreTreePair(TreePair pair) {
        Set<String> common = calculateCommonLeafes(pair);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            pair.score = Double.NEGATIVE_INFINITY;
        else {
            pair.score = calculateScore(pair);
            if (TIE_BREAKER)
                pair.tieBreakingScore = calculateTieBreakScore(pair);
        }
    }

    protected abstract double calculateScore(TreePair pair);

    protected double calculateTieBreakScore(TreePair pair) {
        return treeToTaxa.get(pair.t1).size() + treeToTaxa.get(pair.t2).size() - pair.commonLeafes.size();
    }

    public void clearCache() {
        treeToTaxa.clear();
    }

    public void clearCache(Set<Tree> keep) {
        Iterator<Tree> it = treeToTaxa.keySet().iterator();
        while (it.hasNext()) {
            Tree next = it.next();
            if (!keep.contains(next)) {
                it.remove();
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }


    //#################### Scoring helper Methods ##################
    //unchecked
    int getNumOfConsensusTaxa(TreePair pair) {
        return pair.consensusNumOfTaxa;
    }

    int getNumOfConsensusVertices(TreePair pair) {
        return pair.consensus.vertexCount();
    }

    //unchecked
    int getNumOfBackboneTaxa(TreePair pair) {
        return pair.commonLeafes.size();
    }

    //unchecked
    int getNumUniqueClades(TreePair pair) {
        int t1Clades = pair.t1.vertexCount() - treeToTaxa.get(pair.t1).size();
        int t2Clades = pair.t2.vertexCount() - treeToTaxa.get(pair.t2).size();
        int t1BackboneClades = pair.t1prunedVertexCount - pair.commonLeafes.size();
        int t2BackboneClades = pair.t2prunedVertexCount - pair.commonLeafes.size();

        return (t1Clades - t1BackboneClades) + (t2Clades - t2BackboneClades);
    }

    int getNumRemainingUniqueClades(TreePair pair) {
        return pair.consensus.vertexCount() - pair.backboneClades - pair.consensusNumOfTaxa;
    }

    //unchecked
    int getNumOfConsensusBackboneVertices(TreePair pair) {
        return pair.consensus.vertexCount();
    }

    //unchecked
    int getNumOfCollisionPoints(TreePair pair) {
        return pair.commonInsertionPointTaxa.size();
    }

    //unchecked
    int getNumOfCollisions(TreePair pair) {
        int collsions = 0;
        for (Set singleTaxonSet : pair.commonInsertionPointTaxa.values()) {
            collsions += singleTaxonSet.size();
        }
        return collsions;
    }

    //unchecked
    int getNumOfMultiCollisionPoints(TreePair pair) {
        int multiCollionsPoints = 0;
        for (Set singleTaxonSet : pair.commonInsertionPointTaxa.values()) {
            if (singleTaxonSet.size() > 2)
                multiCollionsPoints++;
        }
        return multiCollionsPoints;
    }

    //unchecked
    int getNumOfCollisionPointsMultiTieBreak(TreePair pair) {
        int multiCollionsPoints = 0;
        for (Set singleTaxonSet : pair.commonInsertionPointTaxa.values()) {
            multiCollionsPoints += 100000 + (singleTaxonSet.size());
        }
        return multiCollionsPoints;
    }

    //#################### Scorer Implementations ##################

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 16.06.15.
     */
    public static class BackboneCladeNumberScorer extends TreeScorer {
        BackboneCladeNumberScorer() {
            super();
        }

        BackboneCladeNumberScorer(boolean syncedCache) {
            super(syncedCache);
        }

        // for this score i use the resolution as a ty breaker. because i can!
        //OK
        @Override
        protected double calculateScore(TreePair pair) {

            pair.pruneToCommonLeafes();
            return (pair.t1prunedVertexCount - getNumOfBackboneTaxa(pair)) + (pair.t2prunedVertexCount - getNumOfBackboneTaxa(pair));
        }
    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 16.06.15.
     */
    public static class BackboneSizeScorer extends TreeScorer {
        BackboneSizeScorer() {
            super();
        }

        BackboneSizeScorer(boolean syncedCache) {
            super(syncedCache);
        }

        // for this score i use the resolution as a ty breaker. because i can!
        //OK
        @Override
        protected double calculateScore(TreePair pair) {

            pair.pruneToCommonLeafes();
            return pair.t1prunedVertexCount + pair.t2prunedVertexCount - getNumOfBackboneTaxa(pair);
        }

    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 16.06.15.
     */
    public static class CollisionMultiCollisionPointScorer extends TreeScorer {
        CollisionMultiCollisionPointScorer() {
            super();
        }

        CollisionMultiCollisionPointScorer(boolean syncedCache) {
            super(syncedCache);
        }

        //BAD
        @Override
        protected double calculateScore(TreePair pair) {


            pair.pruneToCommonLeafes();
            return (-getNumOfMultiCollisionPoints(pair));
        }
    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 15.06.15.
     */
    public static class CollisionNumberScorer extends TreeScorer {
        CollisionNumberScorer() {
            super();
        }

        CollisionNumberScorer(boolean syncedCache) {
            super(syncedCache);
        }

        // GOOOD
        @Override
        protected double calculateScore(TreePair pair) {
            pair.pruneToCommonLeafes();
            return (-getNumOfCollisions(pair));
        }
    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 15.06.15.
     */
    public static class CollisionPointNumberScorer extends TreeScorer {
        CollisionPointNumberScorer() {
            super();
        }

        CollisionPointNumberScorer(boolean syncedCache) {
            super(syncedCache);
        }

        //OK
        @Override
        protected double calculateScore(TreePair pair) {


            pair.pruneToCommonLeafes();
            return (-getNumOfCollisionPoints(pair));
        }
    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 01.04.15.
     */
    public static class ConsensusBackboneCladeNumberScorer extends TreeScorer {
        ConsensusBackboneCladeNumberScorer() {
            super();
        }

        ConsensusBackboneCladeNumberScorer(boolean syncedCache) {
            super(syncedCache);
        }

        // for this score i use the resolution as a ty breaker. because i can!
        @Override
        protected double calculateScore(TreePair pair) {


            pair.calculateConsensus();
            //        return ((pair.getNumOfConsensusBackboneVertices() - pair.getNumOfBackboneTaxa()) * 100) + TreeUtils.calculateTreeResolution(pair.getNumOfBackboneTaxa(), pair.getNumOfBackboneTaxa()) ;
            return getNumOfConsensusBackboneVertices(pair) - getNumOfBackboneTaxa(pair);
        }
    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 31.03.15.
     */
    public static class ConsensusBackboneResolutionScorer extends TreeScorer {
        ConsensusBackboneResolutionScorer() {
            super();
        }

        ConsensusBackboneResolutionScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateScore(TreePair pair) {


            pair.calculateConsensus();
            return TreeUtils.calculateTreeResolution(getNumOfBackboneTaxa(pair), getNumOfConsensusBackboneVertices(pair));
        }
    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 16.06.15.
     */
    public static class ConsensusBackboneSizeScorer extends TreeScorer {
        ConsensusBackboneSizeScorer() {
            super();
        }

        ConsensusBackboneSizeScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateScore(TreePair pair) {


            pair.calculateConsensus();
            return getNumOfConsensusBackboneVertices(pair);
        }
    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 31.03.15.
     */
    public static class ConsensusCladeNumberScorer extends TreeScorer {
        ConsensusCladeNumberScorer() {
            super();
        }

        ConsensusCladeNumberScorer(boolean syncedCache) {
            super(syncedCache);
        }

        // for this score i use the resolution as a ty breaker. because i can!
        @Override
        protected double calculateScore(TreePair pair) {


            pair.calculateConsensus();
            //        return ((pair.getNumOfConsensusVertices() - pair.getNumOfConsensusTaxa()) * 100) + TreeUtils.calculateTreeResolution(pair.getNumOfConsensusTaxa(), pair.getNumOfConsensusVertices()) ;
            return getNumOfConsensusVertices(pair) - getNumOfConsensusTaxa(pair);
        }
    }

    public static class ConsensusTaxonNumberScorer extends TreeScorer {
        ConsensusTaxonNumberScorer() {
            super();
        }

        ConsensusTaxonNumberScorer(boolean syncedCache) {
            super(syncedCache);
        }

        // for this score i use the resolution as a ty breaker. because i can!
        @Override
        protected double calculateScore(TreePair pair) {


            return getLeafLabels(pair.t1).size() + getLeafLabels(pair.t2).size() - pair.commonLeafes.size();
        }
    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 16.03.15.
     */
    public static class ConsensusResolutionScorer extends TreeScorer {
        ConsensusResolutionScorer() {
            super();
        }

        ConsensusResolutionScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateScore(TreePair pair) {


            pair.calculateConsensus();
            return TreeUtils.calculateTreeResolution(getNumOfConsensusTaxa(pair), getNumOfConsensusVertices(pair));
        }
    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 06.02.15.
     */
    public static class OverlapScorer extends TreeScorer {
        OverlapScorer() {
            super();
        }

        OverlapScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateScore(TreePair pair) {

            return getNumOfBackboneTaxa(pair);
        }
    }

    public static class OverlapScorerOrig extends OverlapScorer {
        OverlapScorerOrig() {
            super();
        }

        OverlapScorerOrig(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateTieBreakScore(TreePair pair) {
            return 0d;
        }
    }


    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 27.01.16.
     */
    public static class UniqueCladesNumberScorer extends TreeScorer {
        UniqueCladesNumberScorer() {
            super();
        }

        UniqueCladesNumberScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateScore(TreePair pair) {
            pair.pruneToCommonLeafes();
            return getNumUniqueClades(pair);
        }

    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 27.01.16.
     */
    public static class UniqueCladesRateScorer extends TreeScorer {
        UniqueCladesRateScorer() {
            super();
        }

        UniqueCladesRateScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateScore(TreePair pair) {
            pair.calculateConsensus();
            double uniqueCladesBefore = getNumUniqueClades(pair);
            double uniqueCladesAfter = getNumRemainingUniqueClades(pair);
            return uniqueCladesAfter / uniqueCladesBefore;
        }

        @Override
        protected double calculateTieBreakScore(TreePair pair) {
            return getNumOfConsensusVertices(pair) - getNumOfConsensusTaxa(pair);
        }
    }

    public static class UniqueCladesRemainingNumberScorer extends TreeScorer {
        UniqueCladesRemainingNumberScorer() {
            super();
        }

        UniqueCladesRemainingNumberScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateScore(TreePair pair) {
            pair.calculateConsensus();
            return getNumRemainingUniqueClades(pair);
        }

        @Override
        protected double calculateTieBreakScore(TreePair pair) {
            return getNumOfConsensusVertices(pair) - getNumOfConsensusTaxa(pair);
        }
    }

    public static class UniqueCladesLostNumberScorer extends TreeScorer {
        UniqueCladesLostNumberScorer() {
            super();
        }

        UniqueCladesLostNumberScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateScore(TreePair pair) {
            pair.calculateConsensus();
            int uniqueCladesBefore = getNumUniqueClades(pair);
            int uniqueCladesAfter = getNumRemainingUniqueClades(pair);

            return (uniqueCladesAfter - uniqueCladesBefore); //should be negative... best score is 0
        }

        @Override
        protected double calculateTieBreakScore(TreePair pair) {
            return getNumOfConsensusVertices(pair) - getNumOfConsensusTaxa(pair);
        }
    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 22.06.15.
     */
    public static class UniqueTaxaNumberScorer extends TreeScorer {
        UniqueTaxaNumberScorer() {
            super();
        }

        UniqueTaxaNumberScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateScore(TreePair pair) {
            return -((treeToTaxa.get(pair.t1).size() - pair.commonLeafes.size()) + (treeToTaxa.get(pair.t2).size() - pair.commonLeafes.size()));
        }
    }

    public static class UniqueTaxaNumberScorerOrig extends UniqueTaxaNumberScorer {
        UniqueTaxaNumberScorerOrig() {
            super();
        }

        UniqueTaxaNumberScorerOrig(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateTieBreakScore(TreePair pair) {
            return 0d;
        }
    }


    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 22.06.15.
     */
    public static class UniqueTaxaRateScorer extends TreeScorer {
        UniqueTaxaRateScorer() {
            super();
        }

        UniqueTaxaRateScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateScore(TreePair pair) {
            int tax1 = treeToTaxa.get(pair.t1).size();
            int tax2 = treeToTaxa.get(pair.t2).size();
            return -((tax1 - pair.commonLeafes.size()) / tax1 + (tax2 - pair.commonLeafes.size()) / tax2);
        }
    }

}

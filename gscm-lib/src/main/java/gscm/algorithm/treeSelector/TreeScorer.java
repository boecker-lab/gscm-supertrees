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

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import phyloTree.model.tree.Tree;
import phyloTree.model.tree.TreeNode;
import phyloTree.model.tree.TreeUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
// TreeScores have to be positive uncluding zero

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 06.02.15.
 */


public abstract class TreeScorer<T extends TreeScorer> {
    //    public static final boolean RELIABLE_MERGES = true;
    final Map<Tree, THashSet<String>> treeToTaxa;
    final Set<String> taxaCache;
    public final boolean synced;
    public final static boolean TIE_BREAKER = true;
//    double max;

    public TreeScorer() {
        this(true);
    }

    public TreeScorer(boolean syncedCache) {
        synced = syncedCache;
        if (synced) {
            treeToTaxa = new ConcurrentHashMap<>();
            taxaCache = Collections.newSetFromMap(new ConcurrentHashMap<>());
        } else {
            treeToTaxa = new THashMap<>();
            taxaCache = new THashSet<>();
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

    public double scoreTreePair(TreePair pair) {
        Set<String> common = calculateCommonLeafes(pair);
        pair.setCommonLeafes(common);
        if (common.size() < 3)
            pair.score = Double.NEGATIVE_INFINITY;
        else {
            pair.score = calculateScore(pair);
            if (TIE_BREAKER)
                pair.tieBreakingScore = calculateTieBreakScore(pair);
        }
        return pair.score;
    }

    protected abstract double calculateScore(TreePair pair);

    protected double calculateTieBreakScore(TreePair pair) {
        return treeToTaxa.get(pair.t1).size() + treeToTaxa.get(pair.t2).size() - pair.commonLeafes.size();
    }

    public void clearCache() {
        treeToTaxa.clear();
        taxaCache.clear();
    }

    public void clearCache(Set<Tree> keep) {
        taxaCache.clear();
        Iterator<Tree> it = treeToTaxa.keySet().iterator();
        while (it.hasNext()) {
            Tree next = it.next();
            if (!keep.contains(next)) {
                it.remove();
            } else {
                taxaCache.addAll(treeToTaxa.get(next));
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
        int t1BackboneClades = pair.t1pruned.vertexCount() - pair.commonLeafes.size();
        int t2BackboneClades = pair.t2pruned.vertexCount() - pair.commonLeafes.size();

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
    public static class BackboneCladeNumberScorer extends TreeScorer<BackboneCladeNumberScorer> {
        public BackboneCladeNumberScorer() {
            super();
        }

        public BackboneCladeNumberScorer(boolean syncedCache) {
            super(syncedCache);
        }

        // for this score i use the resolution as a ty breaker. because i can!
        //OK
        @Override
        protected double calculateScore(TreePair pair) {

            pair.pruneToCommonLeafes();
            return (pair.t1pruned.vertexCount() - getNumOfBackboneTaxa(pair)) + (pair.t2pruned.vertexCount() - getNumOfBackboneTaxa(pair));
        }
    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 16.06.15.
     */
    public static class BackboneSizeScorer extends TreeScorer<BackboneSizeScorer> {
        public BackboneSizeScorer() {
            super();
        }

        public BackboneSizeScorer(boolean syncedCache) {
            super(syncedCache);
        }

        // for this score i use the resolution as a ty breaker. because i can!
        //OK
        @Override
        protected double calculateScore(TreePair pair) {

            pair.pruneToCommonLeafes();
            return pair.t1pruned.vertexCount() + pair.t2pruned.vertexCount() - getNumOfBackboneTaxa(pair);
        }

    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 16.06.15.
     */
    public static class CollisionMultiCollisionPointScorer extends TreeScorer<CollisionMultiCollisionPointScorer> {
        public CollisionMultiCollisionPointScorer() {
            super();
        }

        public CollisionMultiCollisionPointScorer(boolean syncedCache) {
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
    public static class CollisionNumberScorer extends TreeScorer<CollisionNumberScorer> {
        public CollisionNumberScorer() {
            super();
        }

        public CollisionNumberScorer(boolean syncedCache) {
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
    public static class CollisionPointNumberScorer extends TreeScorer<CollisionPointNumberScorer> {
        public CollisionPointNumberScorer() {
            super();
        }

        public CollisionPointNumberScorer(boolean syncedCache) {
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
    public static class ConsensusBackboneCladeNumberScorer extends TreeScorer<ConsensusBackboneCladeNumberScorer> {
        public ConsensusBackboneCladeNumberScorer() {
            super();
        }

        public ConsensusBackboneCladeNumberScorer(boolean syncedCache) {
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
    public static class ConsensusBackboneResolutionScorer extends TreeScorer<ConsensusBackboneResolutionScorer> {
        public ConsensusBackboneResolutionScorer() {
            super();
        }

        public ConsensusBackboneResolutionScorer(boolean syncedCache) {
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
    public static class ConsensusBackboneSizeScorer extends TreeScorer<ConsensusBackboneSizeScorer> {
        public ConsensusBackboneSizeScorer() {
            super();
        }

        public ConsensusBackboneSizeScorer(boolean syncedCache) {
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
    public static class ConsensusCladeNumberScorer extends TreeScorer<ConsensusCladeNumberScorer> {
        public ConsensusCladeNumberScorer() {
            super();
        }

        public ConsensusCladeNumberScorer(boolean syncedCache) {
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

    public static class ConsensusTaxonNumberScorer extends TreeScorer<ConsensusTaxonNumberScorer> {
        public ConsensusTaxonNumberScorer() {
            super();
        }

        public ConsensusTaxonNumberScorer(boolean syncedCache) {
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
    public static class ConsensusResolutionScorer extends TreeScorer<ConsensusResolutionScorer> {
        public ConsensusResolutionScorer() {
            super();
        }

        public ConsensusResolutionScorer(boolean syncedCache) {
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
    public static class OverlapScorer extends TreeScorer<OverlapScorer> {
        public OverlapScorer() {
            super();
        }

        public OverlapScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateScore(TreePair pair) {

            return getNumOfBackboneTaxa(pair);
        }
    }

    public static class OverlapScorerOrig extends OverlapScorer {
        public OverlapScorerOrig() {
            super();
        }

        public OverlapScorerOrig(boolean syncedCache) {
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
    public static class SubsetUnitOverlapDifferenceScorer extends TreeScorer<SubsetUnitOverlapDifferenceScorer> {
        public SubsetUnitOverlapDifferenceScorer() {
            super();
        }

        public SubsetUnitOverlapDifferenceScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateScore(TreePair pair) {

            // is this a zero collision pair?
            Set<String> ts1 = treeToTaxa.get(pair.t1);
            Set<String> ts2 = treeToTaxa.get(pair.t2);
            double score = taxaCache.size();
            if (ts1.containsAll(ts2)) {
                score += taxaCache.size() + ts1.size();//todo better would be the number of all taxa...
            } else if (ts2.containsAll(ts1)) {
                score += taxaCache.size() + ts2.size();
            }
            score += (taxaCache.size() - (ts1.size() - pair.commonLeafes.size()) + (ts2.size() - pair.commonLeafes.size()));
            return score;
        }

    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 22.06.15.
     */
    public static class SubsetUnitOverlapRateScorer extends TreeScorer<SubsetUnitOverlapRateScorer> {
        public SubsetUnitOverlapRateScorer() {
            super();
        }

        public SubsetUnitOverlapRateScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateScore(TreePair pair) {

            // is this a zero collision pair?
            Set<String> ts1 = treeToTaxa.get(pair.t1);
            Set<String> ts2 = treeToTaxa.get(pair.t2);
            double score = taxaCache.size();
            if (ts1.containsAll(ts2)) {
                score += taxaCache.size() + ts1.size();//todo better would be the number of all taxa...
            } else if (ts2.containsAll(ts1)) {
                score += taxaCache.size() + ts2.size();
            }
            score += (double) taxaCache.size() / (double) (ts1.size() - pair.commonLeafes.size()) + (ts2.size() - pair.commonLeafes.size());
            return score;
        }

    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 22.06.15.
     */
    public static class SubsetUnitOverlapScorer extends TreeScorer<SubsetUnitOverlapScorer> {
        public SubsetUnitOverlapScorer() {
            super();
        }

        public SubsetUnitOverlapScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateScore(TreePair pair) {

            // is this a zero collision pair?
            Set<String> ts1 = treeToTaxa.get(pair.t1);
            Set<String> ts2 = treeToTaxa.get(pair.t2);
            double score = taxaCache.size();
            if (ts1.containsAll(ts2)) {
                score += taxaCache.size() + ts1.size();//todo better would be the number of all taxa...
            } else if (ts2.containsAll(ts1)) {
                score += taxaCache.size() + ts2.size();
            }
            score -= (ts1.size() - pair.commonLeafes.size()) + (ts2.size() - pair.commonLeafes.size());
            score += (double) pair.commonLeafes.size() / (double) taxaCache.size();

            return score;
        }

    }

    /**
     * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 27.01.16.
     */
    public static class UniqueCladesNumberScorer extends TreeScorer<UniqueTaxaNumberScorer> {
        public UniqueCladesNumberScorer() {
            super();
        }

        public UniqueCladesNumberScorer(boolean syncedCache) {
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
    public static class UniqueCladesRateScorer extends TreeScorer<UniqueTaxaNumberScorer> {
        public UniqueCladesRateScorer() {
            super();
        }

        public UniqueCladesRateScorer(boolean syncedCache) {
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

    public static class UniqueCladesRemainingNumberScorer extends TreeScorer<UniqueTaxaNumberScorer> {
        public UniqueCladesRemainingNumberScorer() {
            super();
        }

        public UniqueCladesRemainingNumberScorer(boolean syncedCache) {
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

    public static class UniqueCladesLostNumberScorer extends TreeScorer<UniqueTaxaNumberScorer> {
        public UniqueCladesLostNumberScorer() {
            super();
        }

        public UniqueCladesLostNumberScorer(boolean syncedCache) {
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
    public static class UniqueTaxaNumberScorer extends TreeScorer<UniqueTaxaNumberScorer> {
        public UniqueTaxaNumberScorer() {
            super();
        }

        public UniqueTaxaNumberScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        protected double calculateScore(TreePair pair) {
            return -((treeToTaxa.get(pair.t1).size() - pair.commonLeafes.size()) + (treeToTaxa.get(pair.t2).size() - pair.commonLeafes.size()));
        }
    }

    public static class UniqueTaxaNumberScorerOrig extends UniqueTaxaNumberScorer {
        public UniqueTaxaNumberScorerOrig() {
            super();
        }

        public UniqueTaxaNumberScorerOrig(boolean syncedCache) {
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
    public static class UniqueTaxaRateScorer extends TreeScorer<UniqueTaxaRateScorer> {
        public UniqueTaxaRateScorer() {
            super();
        }

        public UniqueTaxaRateScorer(boolean syncedCache) {
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

package scmAlgorithm.treeSelector;

import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
import epos.model.tree.treetools.TreeUtilsBasic;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fleisch on 06.02.15.
 */
public abstract class TreeScorer<T extends TreeScorer> {
    final Map<Tree, THashSet<String>> treeToTaxa;
    final Set<String> taxaCache;
    public final boolean synced;

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

    THashSet<String> calculateCommonLeafes(Tree tree1, Tree tree2) {
        THashSet<String> ts1 = treeToTaxa.get(tree1);
        THashSet<String> ts2 = treeToTaxa.get(tree2);
        if (ts1 == null) {
            ts1 = createTreeEntry(tree1);
        }
        if (ts2 == null) {
            ts2 = createTreeEntry(tree2);
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


    public abstract double scoreTreePair(TreePair pair);

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
    int getNumOfConsensusBackboneVertices(TreePair pair) {
        return pair.consensus.vertexCount();
    }

    //unchecked
    int getNumOfCollisionPoints(TreePair pair) {
        return pair.commenInsertionPointTaxa.size();
    }

    //unchecked
    int getNumOfCollisions(TreePair pair) {
        int collsions = 0;
        for (Set singleTaxonSet : pair.commenInsertionPointTaxa.values()) {
            collsions += singleTaxonSet.size();
        }
        return collsions;
    }

    //unchecked
    int getNumOfCollisionDestructedClades(TreePair pair) {
        int destructedClades = 0;
        for (Set singleTaxonSet : pair.commenInsertionPointTaxa.values()) {
            destructedClades += (singleTaxonSet.size() - 1);
        }
        return destructedClades;
    }

    //unchecked
    int getNumOfMultiCollisionPoints(TreePair pair) {
        int multiCollionsPoints = 0;
        for (Set singleTaxonSet : pair.commenInsertionPointTaxa.values()) {
            if (singleTaxonSet.size() > 2)
                multiCollionsPoints++;
        }
        return multiCollionsPoints;
    }

    //unchecked
    int getNumOfCollisionPointsMultiTieBreak(TreePair pair) {
        int multiCollionsPoints = 0;
        for (Set singleTaxonSet : pair.commenInsertionPointTaxa.values()) {
            multiCollionsPoints += 100000 + (singleTaxonSet.size());
        }
        return multiCollionsPoints;
    }

    //#################### Scorer INIT ##################
    public static TreeScorer newOverlapScorer(boolean synced){
        return new OverlapScorer(synced);
    }

    public static TreeScorer newUniqueTaxonScorer(boolean synced){
        return new UniqueTaxaNumberScorer(synced);
    }

    public static TreeScorer newTaxonScorer(boolean synced){
        return new ConsensusTaxonNumberScorer(synced);
    }

    public static TreeScorer newResolutionScorer(boolean synced){
        return new ConsensusResolutionScorer(synced);
    }

    public static TreeScorer newCollisionScorer(boolean synced){
        return new CollisionNumberScorer(synced);
    }



    public static TreeScorer[] CompleteScorerCombo(boolean synced) {
        return new TreeScorer[]{
                new TreeScorer.OverlapScorer(synced),
                new TreeScorer.CollisionPointNumberScorer(synced),
                new TreeScorer.CollisionLostCladesNumberScorer(synced),
                new TreeScorer.CollisionNumberScorer(synced),
                new TreeScorer.BackboneSizeScorer(synced),
                new TreeScorer.BackboneCladeNumberScorer(synced),
                new TreeScorer.UniqueTaxaNumberScorer(synced),
                new TreeScorer.UniqueTaxaRateScorer(synced),
                new TreeScorer.ConsensusBackboneCladeNumberScorer(synced),
                new TreeScorer.ConsensusBackboneResolutionScorer(synced),
                new TreeScorer.ConsensusBackboneSizeScorer(synced),
                new TreeScorer.ConsensusCladeNumberScorer(synced),
                new TreeScorer.ConsensusResolutionScorer(synced)
        };
    }

    public static TreeScorer[] newFastScorerCombo(boolean synced){
        return new TreeScorer[]{
                newOverlapScorer(synced),
                newUniqueTaxonScorer(synced),
                newTaxonScorer(synced)
        };
    }

    public static TreeScorer[] newGreenCombi(boolean synced) {
        return new TreeScorer[]{
                new TreeScorer.OverlapScorer(synced),//is automatic included
                new TreeScorer.CollisionPointNumberScorer(synced),
//            new CollisionLostCladesNumberScorer(),
                new TreeScorer.CollisionNumberScorer(synced),
//            new BackboneSizeScorer(),
//            new BackboneCladeNumberScorer(),
                new TreeScorer.UniqueTaxaNumberScorer(synced),
                new TreeScorer.UniqueTaxaRateScorer(synced),
//            new ConsensusBackboneCladeNumberScorer(),
                new TreeScorer.ConsensusBackboneResolutionScorer(synced),
//            new ConsensusBackboneSizeScorer(),
                new TreeScorer.ConsensusCladeNumberScorer(synced),
                new TreeScorer.ConsensusResolutionScorer(synced)
        };
    }


    public static TreeScorer[] newBest4Combo(boolean synced) {
        return new TreeScorer[]{
//            new OverlapScorer(),//is automatic included
                new TreeScorer.CollisionPointNumberScorer(synced),
//            new CollisionLostCladesNumberScorer(),
//            new CollisionNumberScorer(),
//            new BackboneSizeScorer(),
//            new BackboneCladeNumberScorer(),
                new TreeScorer.UniqueTaxaNumberScorer(synced),
//            new UniqueTaxaRateScorer(),
//            new ConsensusBackboneCladeNumberScorer(),
//            new ConsensusBackboneResolutionScorer(),
//            new ConsensusBackboneSizeScorer(),
                new TreeScorer.ConsensusCladeNumberScorer(synced),
                new TreeScorer.ConsensusResolutionScorer(synced)
        };
    }

    public static TreeScorer[] newMinimalCombo(boolean synced) {
        return new TreeScorer[]{
                new TreeScorer.OverlapScorer(synced),//is automatic included
                new TreeScorer.CollisionPointNumberScorer(synced),
//            new CollisionLostCladesNumberScorer(),
//            new CollisionNumberScorer(),
//            new BackboneSizeScorer(),
//            new BackboneCladeNumberScorer(),
                new TreeScorer.UniqueTaxaNumberScorer(synced),
//            new UniqueTaxaRateScorer(),
//            new ConsensusBackboneCladeNumberScorer(),
                new TreeScorer.ConsensusBackboneResolutionScorer(synced),
//            new ConsensusBackboneSizeScorer(),
//            new ConsensusCladeNumberScorer(),
                new TreeScorer.ConsensusResolutionScorer(synced)
        };
    }


    //#################### Scorer Implementations ##################

    /**
     * Created by fleisch on 16.06.15.
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
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;
            pair.pruneToCommonLeafes();
            return (pair.t1pruned.vertexCount() - getNumOfBackboneTaxa(pair)) + (pair.t2pruned.vertexCount() - getNumOfBackboneTaxa(pair));
        }
    }

    /**
     * Created by fleisch on 16.06.15.
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
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;
            pair.pruneToCommonLeafes();
            return pair.t1pruned.vertexCount() + pair.t2pruned.vertexCount() - getNumOfBackboneTaxa(pair);
        }

    }

    /**
     * Created by fleisch on 16.06.15.
     */
    public static class CollisionLostCladesNumberScorer extends TreeScorer<CollisionLostCladesNumberScorer> {
        public CollisionLostCladesNumberScorer() {
            super();
        }

        public CollisionLostCladesNumberScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;

            pair.pruneToCommonLeafes();
            return (-getNumOfCollisionDestructedClades(pair));
        }
    }

    /**
     * Created by fleisch on 16.06.15.
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
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;

            pair.pruneToCommonLeafes();
            return (-getNumOfMultiCollisionPoints(pair));
        }
    }

    /**
     * Created by fleisch on 15.06.15.
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
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;

            pair.pruneToCommonLeafes();
            return (-getNumOfCollisions(pair));
        }
    }

    /**
     * Created by fleisch on 16.06.15.
     */
    public static class CollisionPointMultiCollissionTieBreakScorer extends TreeScorer<CollisionPointMultiCollissionTieBreakScorer> {
        public CollisionPointMultiCollissionTieBreakScorer() {
            super();
        }

        public CollisionPointMultiCollissionTieBreakScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;

            pair.pruneToCommonLeafes();
            return (-getNumOfCollisionPointsMultiTieBreak(pair));
        }
    }

    /**
     * Created by fleisch on 15.06.15.
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
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;

            pair.pruneToCommonLeafes();
            return (-getNumOfCollisionPoints(pair));
        }
    }

    /**
     * Created by fleisch on 01.04.15.
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
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;

            pair.calculateConsensus();
            //        return ((pair.getNumOfConsensusBackboneVertices() - pair.getNumOfBackboneTaxa()) * 100) + TreeUtilsBasic.calculateTreeResolution(pair.getNumOfBackboneTaxa(), pair.getNumOfBackboneTaxa()) ;
            return getNumOfConsensusBackboneVertices(pair) - getNumOfBackboneTaxa(pair);
        }
    }

    /**
     * Created by fleisch on 31.03.15.
     */
    public static class ConsensusBackboneResolutionScorer extends TreeScorer<ConsensusBackboneResolutionScorer> {
        public ConsensusBackboneResolutionScorer() {
            super();
        }

        public ConsensusBackboneResolutionScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;

            pair.calculateConsensus();
            return TreeUtilsBasic.calculateTreeResolution(getNumOfBackboneTaxa(pair), getNumOfConsensusBackboneVertices(pair));
        }
    }

    /**
     * Created by fleisch on 16.06.15.
     */
    public static class ConsensusBackboneSizeScorer extends TreeScorer<ConsensusBackboneSizeScorer> {
        public ConsensusBackboneSizeScorer() {
            super();
        }

        public ConsensusBackboneSizeScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;

            pair.calculateConsensus();
            return getNumOfConsensusBackboneVertices(pair);
        }
    }

    /**
     * Created by fleisch on 31.03.15.
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
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;

            pair.calculateConsensus();
            //        return ((pair.getNumOfConsensusVertices() - pair.getNumOfConsensusTaxa()) * 100) + TreeUtilsBasic.calculateTreeResolution(pair.getNumOfConsensusTaxa(), pair.getNumOfConsensusVertices()) ;
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
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;

            return getLeafLabels(pair.t1).size() + getLeafLabels(pair.t2).size() - common.size();
        }
    }

    /**
     * Created by fleisch on 16.03.15.
     */
    public static class ConsensusResolutionScorer extends TreeScorer<ConsensusResolutionScorer> {
        public ConsensusResolutionScorer() {
            super();
        }

        public ConsensusResolutionScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;

            pair.calculateConsensus();
            return TreeUtilsBasic.calculateTreeResolution(getNumOfConsensusTaxa(pair), getNumOfConsensusVertices(pair));
        }
    }

    /**
     * Created by fleisch on 06.02.15.
     */
    public static class OverlapScorer extends TreeScorer<OverlapScorer> {
        public OverlapScorer() {
            super();
        }

        public OverlapScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;
            //        return pair.getNumOfBackboneTaxa();
            return getNumOfBackboneTaxa(pair);
        }
    }

    /**
     * Created by fleisch on 22.06.15.
     */
    public static class SubsetUnitOverlapDifferenceScorer extends TreeScorer<SubsetUnitOverlapDifferenceScorer> {
        public SubsetUnitOverlapDifferenceScorer() {
            super();
        }

        public SubsetUnitOverlapDifferenceScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;
            // is this a zero collision pair?
            Set<String> ts1 = treeToTaxa.get(pair.t1);
            Set<String> ts2 = treeToTaxa.get(pair.t2);
            double score = taxaCache.size();
            if (ts1.containsAll(ts2)) {
                score += taxaCache.size() + ts1.size();//todo better would be the number of all taxa...
            } else if (ts2.containsAll(ts1)) {
                score += taxaCache.size() + ts2.size();
            }
            score += (taxaCache.size() - (ts1.size() - common.size()) + (ts2.size() - common.size()));
            return score;
        }

    }

    /**
     * Created by fleisch on 22.06.15.
     */
    public static class SubsetUnitOverlapRateScorer extends TreeScorer<SubsetUnitOverlapRateScorer> {
        public SubsetUnitOverlapRateScorer() {
            super();
        }

        public SubsetUnitOverlapRateScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;
            // is this a zero collision pair?
            Set<String> ts1 = treeToTaxa.get(pair.t1);
            Set<String> ts2 = treeToTaxa.get(pair.t2);
            double score = taxaCache.size();
            if (ts1.containsAll(ts2)) {
                score += taxaCache.size() + ts1.size();//todo better would be the number of all taxa...
            } else if (ts2.containsAll(ts1)) {
                score += taxaCache.size() + ts2.size();
            }
            score += (double) taxaCache.size() / (double) (ts1.size() - common.size()) + (ts2.size() - common.size());
            return score;
        }

    }

    /**
     * Created by fleisch on 22.06.15.
     */
    public static class SubsetUnitOverlapScorer extends TreeScorer<SubsetUnitOverlapScorer> {
        public SubsetUnitOverlapScorer() {
            super();
        }

        public SubsetUnitOverlapScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;
            // is this a zero collision pair?
            Set<String> ts1 = treeToTaxa.get(pair.t1);
            Set<String> ts2 = treeToTaxa.get(pair.t2);
            double score = taxaCache.size();
            if (ts1.containsAll(ts2)) {
                score += taxaCache.size() + ts1.size();//todo better would be the number of all taxa...
            } else if (ts2.containsAll(ts1)) {
                score += taxaCache.size() + ts2.size();
            }
            score -= (ts1.size() - common.size()) + (ts2.size() - common.size());
            score += (double) common.size() / (double) taxaCache.size();

            return score;
        }

    }

    /**
     * Created by fleisch on 22.06.15.
     */
    public static class UniqueTaxaNumberScorer extends TreeScorer<UniqueTaxaNumberScorer> {
        public UniqueTaxaNumberScorer() {
            super();
        }

        public UniqueTaxaNumberScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;

            return -((treeToTaxa.get(pair.t1).size() - common.size()) + (treeToTaxa.get(pair.t2).size() - common.size()));
        }

    }

    /**
     * Created by fleisch on 22.06.15.
     */
    public static class UniqueTaxaRateScorer extends TreeScorer<UniqueTaxaRateScorer> {
        public UniqueTaxaRateScorer() {
            super();
        }

        public UniqueTaxaRateScorer(boolean syncedCache) {
            super(syncedCache);
        }

        @Override
        public double scoreTreePair(TreePair pair) {
            Set<String> common = calculateCommonLeafes(pair.t1, pair.t2);
            pair.setCommonLeafes(common);
            if (common.size() < 3)
                return Double.NEGATIVE_INFINITY;

            int tax1 = treeToTaxa.get(pair.t1).size();
            int tax2 = treeToTaxa.get(pair.t2).size();
            return -((tax1 - common.size()) / tax1 + (tax2 - common.size()) / tax2);
        }
    }

}

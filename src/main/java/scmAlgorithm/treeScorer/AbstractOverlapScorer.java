package scmAlgorithm.treeScorer;

import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import scmAlgorithm.treeSelector.TreePair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by fleisch on 06.02.15.
 */
public abstract class AbstractOverlapScorer<M extends Map<Tree, S>, S extends Set<String>> implements TreeScorer {
    private final M treeToTaxa = getMInstance();


    private S calculateCommonLeafes(Tree tree1, Tree tree2) {
        S ts1 = treeToTaxa.get(tree1);
        S ts2 = treeToTaxa.get(tree2);
        if (ts1 == null)
            ts1 = createTreeEntry(tree1);
        if (ts2 == null)
            ts2 = createTreeEntry(tree2);

        S tcommon =  getSInstance(ts1.size());

        tcommon.addAll(ts1);
        tcommon.retainAll(ts2);

        return tcommon;
    }

    @Override
    public double getPairwiseScore(Tree tree1, Tree tree2) {
        return calculateCommonLeafes(tree1, tree2).size();
    }

    @Override
    public TreePair getScoredTreePair(Tree tree1, Tree tree2) {
        S commonLeaves = calculateCommonLeafes(tree1, tree2);
        if (commonLeaves.isEmpty())
            return null;
        return new TreePair(tree1, tree2, commonLeaves.size(), commonLeaves);
    }

    private S createTreeEntry(Tree tree1) {
        TreeNode[] taxa = tree1.getLeaves();
        S taxonSet = getSInstance(taxa.length);
        for (TreeNode taxon : taxa) {
            taxonSet.add(taxon.getLabel());
        }
        treeToTaxa.put(tree1, taxonSet);
        return taxonSet;
    }

    @Override
    public void clear() {
        treeToTaxa.clear();
    }

    abstract M getMInstance();

    abstract S getSInstance(int size);


    public static class OverlapScorerTroveObject extends AbstractOverlapScorer<THashMap<Tree, THashSet<String>>, THashSet<String>> {

        @Override
        THashMap<Tree, THashSet<String>> getMInstance() {
            return new THashMap<>();
        }

        @Override
        THashSet<String> getSInstance(int size) {
            return new THashSet<>(size);
        }
    }

    public static class OverlapScorer extends AbstractOverlapScorer<HashMap<Tree, HashSet<String>>, HashSet<String>> {
        @Override
        HashMap<Tree, HashSet<String>> getMInstance() {
            return new HashMap<>();
        }

        @Override
        HashSet<String> getSInstance(int size) {
            return new HashSet<>(size);
        }
    }
}

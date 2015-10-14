package scmAlgorithm.treeSelector;

import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
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
    protected final Map<Tree, THashSet<String>> treeToTaxa;
    protected final Set<String> taxaCache;

    public TreeScorer() {
        this(true, true);
    }

    public TreeScorer(boolean cache, boolean syncedCache) {
        if (cache)
            if (syncedCache) {
                treeToTaxa = new ConcurrentHashMap<>();
            } else {
                treeToTaxa = new THashMap<>();
            }
        else
            treeToTaxa = null;

        if (syncedCache) {
            taxaCache = Collections.newSetFromMap(new ConcurrentHashMap<>());
        } else {
            taxaCache = new THashSet<>();
        }
    }

    protected THashSet<String> calculateCommonLeafes(Tree tree1, Tree tree2) {
        return calculateCommonCached(tree1, tree2);
    }

    protected THashSet<String> calculateCommonCached(Tree tree1, Tree tree2) {
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

   /* protected THashSet<String> calculateCommonNOCache(Tree tree1, Tree tree2){
        THashSet<String> ts1 = getLeafLabels(tree1);
        THashSet<String> ts2 = getLeafLabels(tree2);

        THashSet<String> tcommon =  new THashSet<>(ts1.size());

        tcommon.addAll(ts1);
        tcommon.retainAll(ts2);

        return tcommon;
    }*/


    private THashSet<String> getLeafLabels(Tree tree1) {
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
            }else{
                taxaCache.addAll(treeToTaxa.get(next));
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}

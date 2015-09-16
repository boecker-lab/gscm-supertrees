package scmAlgorithm.treeScorer;

import epos.algo.consensus.adams.AdamsConsensus;
import epos.algo.consensus.loose.LooseConsensus;
import epos.algo.consensus.nconsensus.NConsensus;
import epos.model.algo.SupertreeAlgorithm;
import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.apache.log4j.Logger;
import scmAlgorithm.treeSelector.TreePair;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fleisch on 06.02.15.
 */
public abstract class TreeScorer<T extends TreeScorer> {
    public static enum ConsensusMethods {SEMI_STRICT, STRICT,MAJORITY,ADAMS}
    protected Logger log;
    public final ConsensusMethods METHOD;
    protected final Map<Tree, THashSet<String>> treeToTaxa;

    public TreeScorer(ConsensusMethods method){
        this(method, Logger.getLogger(TreeScorer.class),true,true);
    }

    public TreeScorer(ConsensusMethods method, Logger log, boolean cache, boolean syncedCache){
        this.METHOD = method;
        this.log =  log;
        if(cache)
            if (syncedCache) {
                treeToTaxa = new ConcurrentHashMap<>();
            }else{
                treeToTaxa = new THashMap<>();
            }
        else
            treeToTaxa = null;
    }

    protected THashSet<String> calculateCommonLeafes(Tree tree1, Tree tree2) {
        if (treeToTaxa != null){
            return calculateCommonCached(tree1, tree2);
        }else{
            return calculateCommonNOCache(tree1, tree2);
        }
    }

    protected THashSet<String> calculateCommonCached(Tree tree1, Tree tree2){
        THashSet<String> ts1 = treeToTaxa.get(tree1);
        THashSet<String> ts2 = treeToTaxa.get(tree2);
        if (ts1 == null)
            ts1 = createTreeEntry(tree1);
        if (ts2 == null)
            ts2 = createTreeEntry(tree2);

        THashSet<String> tcommon =  new THashSet<>(ts1.size());

        tcommon.addAll(ts1);
        tcommon.retainAll(ts2);

        return tcommon;
    }

    protected THashSet<String> calculateCommonNOCache(Tree tree1, Tree tree2){
        THashSet<String> ts1 = getLeafLabels(tree1);
        THashSet<String> ts2 = getLeafLabels(tree2);

        THashSet<String> tcommon =  new THashSet<>(ts1.size());

        tcommon.addAll(ts1);
        tcommon.retainAll(ts2);

        return tcommon;
    }


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
        treeToTaxa.put(tree1,s);
        return s;
    }

    public SupertreeAlgorithm getConsensusAlgorithm(){
        SupertreeAlgorithm a;
        switch (METHOD){
            case STRICT:
                a = new NConsensus(log);
                ((NConsensus)a).setMethod(NConsensus.METHOD_STRICT);
                break;
            case MAJORITY:
                a = new NConsensus(log);
                ((NConsensus)a).setMethod(NConsensus.METHOD_MAJORITY);
                break; // is same as strict for 2 trees...
            case SEMI_STRICT:
                a = new LooseConsensus();
                break;
            case ADAMS:
                a =  new AdamsConsensus();
                break;
            default: a = null;
        }
        return a;
    }

//    public double scoreTreePair(Tree tree1, Tree tree2);
    public abstract double scoreTreePair(TreePair pair);
//    public TreePair getScoredTreePair(Tree tree1, Tree tree2);

    public void clearCache(){
        if (treeToTaxa != null)
            treeToTaxa.clear();
    }

    public void clearCache(Set<Tree> keep){
        if (treeToTaxa != null) {
            Iterator<Tree> it = treeToTaxa.keySet().iterator();
            while (it.hasNext()) {
                Tree next = it.next();
                if (!keep.contains(next)){
                    it.remove();
                }
            }
        }
    }

    @Override
    public String toString(){
        return getClass().getSimpleName();
    }
}

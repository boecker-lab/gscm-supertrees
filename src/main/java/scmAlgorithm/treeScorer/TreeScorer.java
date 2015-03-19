package scmAlgorithm.treeScorer;

import epos.algo.consensus.ConsensusAlgorithm;
import epos.algo.consensus.adams.AdamsConsensus;
import epos.algo.consensus.loose.PairwiseLooseConsensus;
import epos.algo.consensus.nconsensus.NConsensus;
import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import scmAlgorithm.treeSelector.TreePair;

/**
 * Created by fleisch on 06.02.15.
 */
public abstract class TreeScorer {
    public static enum ConsensusMethods {SEMI_STRICT, STRICT,MAJORITY,ADAMS}
    public final ConsensusMethods METHOD;

    public TreeScorer(ConsensusMethods method){
        this.METHOD = method;
    }

    protected final THashMap<Tree, THashSet<String>> treeToTaxa = new THashMap<>();

    protected THashSet<String> calculateCommonLeafes(Tree tree1, Tree tree2) {
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

    private THashSet<String> createTreeEntry(Tree tree1) {
        TreeNode[] taxa = tree1.getLeaves();
        THashSet<String> taxonSet = new THashSet<>(taxa.length);
        for (TreeNode taxon : taxa) {
            taxonSet.add(taxon.getLabel());
        }
        treeToTaxa.put(tree1, taxonSet);
        return taxonSet;
    }

    //resturns false if tree was already in map
    public void addConsensusTree(TreePair pair){
        Tree c =  pair.getConsensus(getConsensusAlgorithm());
        THashSet<String> s1 =  treeToTaxa.get(pair.t1);
        THashSet<String> s2 =  treeToTaxa.get(pair.t2);
        THashSet<String> taxa =  new THashSet<>(s1.size() + s2.size());
        taxa.addAll(s1);
        taxa.addAll(s2);
        treeToTaxa.put(c, taxa);
    }

    public ConsensusAlgorithm getConsensusAlgorithm(){
        ConsensusAlgorithm a;
        switch (METHOD){
            case STRICT:
                a = new NConsensus();
                ((NConsensus)a).setMethod(NConsensus.METHOD_STRICT);
                break;
            case MAJORITY:
                a = new NConsensus();
                ((NConsensus)a).setMethod(NConsensus.METHOD_MAJORITY);
                break; // is same as strict for 2 trees...
            case SEMI_STRICT:
                a = new PairwiseLooseConsensus(false);
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

    public void clear(){
        treeToTaxa.clear();
    };


}

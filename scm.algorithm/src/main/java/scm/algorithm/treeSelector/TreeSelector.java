package scm.algorithm.treeSelector;

import epos.algo.consensus.Consensus;
import epos.algo.consensus.adams.AdamsConsensus;
import epos.algo.consensus.loose.LooseConsensus;
import epos.algo.consensus.nconsensus.NConsensus;
import phyloTree.algorithm.SupertreeAlgorithm;
import phyloTree.model.tree.Tree;

/**
 * Created by fleisch on 14.10.15.
 */
public interface TreeSelector {
    enum ConsensusMethod {SEMI_STRICT, STRICT, MAJORITY, ADAMS}
    void setClearScorer(boolean clearScorer);
    void init();

    Tree pollTreePair();
    boolean addTree(Tree tree);

    void setInputTrees(Tree[] trees);
    int getNumberOfTrees();

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


}

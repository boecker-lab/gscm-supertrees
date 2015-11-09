package scmAlgorithm.treeSelector;

import epos.algo.consensus.adams.AdamsConsensus;
import epos.algo.consensus.loose.LooseConsensus;
import epos.algo.consensus.nconsensus.NConsensus;
import epos.model.algo.SupertreeAlgorithm;
import epos.model.tree.Tree;

import java.util.concurrent.ExecutorService;

/**
 * Created by fleisch on 14.10.15.
 */
public interface TreeSelector {
    enum ConsensusMethod {SEMI_STRICT, STRICT, MAJORITY, ADAMS}

    void init(boolean clearScorer);
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
                ((NConsensus) a).setMethod(NConsensus.METHOD_STRICT);
                break;
            case MAJORITY:
                a = new NConsensus();
                ((NConsensus) a).setMethod(NConsensus.METHOD_MAJORITY);
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

package scmAlgorithm;

import epos.algo.consensus.loose.LooseConsensus;
import epos.model.tree.Tree;

import java.util.List;

/**
 * Created by fleisch on 16.06.15.
 */
public interface MultiResultMergeSupertreeAlgorithm extends SupertreeAlgorithm {
    static final boolean DEBUG = true;

    default Tree getMergedSupertree() {
        List<Tree> superTrees = getSupertrees();
        LooseConsensus cons = new LooseConsensus();
        cons.setInput(superTrees);
        cons.run();
        Tree supertree = cons.getResult();
        return supertree;
    }

}

package scmAlgorithm;

import epos.model.tree.Tree;
import org.apache.log4j.Logger;
import scmAlgorithm.treeSelector.TreeScorer;
import scmAlgorithm.treeSelector.GreedyTreeSelector;
import scmAlgorithm.treeSelector.TreePair;
import scmAlgorithm.treeSelector.TreeSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by fleisch on 15.06.15.
 */
public class MultiGreedySCMAlgorithm extends GreedySCMAlgorithm implements MergedMultipleSCMResults {
    private final TreeScorer[] scorerList;
    private Tree mergedSupertree = null;


    public MultiGreedySCMAlgorithm(Logger logger, ExecutorService executorService, TreeSelector selector, TreeScorer[] scorerList) {
        super(logger, executorService, selector);
        this.scorerList = scorerList;
    }

    public MultiGreedySCMAlgorithm(Logger logger, TreeSelector selector, TreeScorer[] scorerList) {
        super(logger, selector);
        this.scorerList = scorerList;
    }

    public MultiGreedySCMAlgorithm(TreeSelector selector, TreeScorer[] scorerList) {
        super(selector);
        this.scorerList = scorerList;
    }

    @Override
    protected List<TreePair> calculateSuperTrees() {
        int numOfjobs =  Math.min(threads,scorerList.length);

        if (numOfjobs >1){
            return calculateParallel();
        }else{
            return calculateSequential();
        }

    }

    private List<TreePair> calculateSequential() {
        List<TreePair> superTrees =  new ArrayList<>(scorerList.length);
        for (int i = 0; i < scorerList.length; i++) {
            TreeScorer scorer = scorerList[i];
            selector.setScorer(scorer);
            superTrees.add(calculateGreedyConsensus(selector,false));
        }
        return superTrees;
    }

    private List<TreePair> calculateParallel() {
        //todo fill me
        return null;
    }

    /**
     * Returns the merged Supertree, based on the supertreeList
     * @return
     */
    @Override
    public Tree getResult() {
        if (mergedSupertree == null)
            mergedSupertree = getMergedSupertree();
        return mergedSupertree;
    }
}

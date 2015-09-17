package scmAlgorithm;

import epos.model.tree.Tree;
import org.apache.log4j.Logger;
import scmAlgorithm.treeScorer.TreeScorer;
import scmAlgorithm.treeSelector.GreedyTreeSelector;
import scmAlgorithm.treeSelector.TreePair;
import scmAlgorithm.treeSelector.TreeSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by fleisch on 15.06.15.
 */
public class MultiGreedySCMAlgorithm extends AbstractSCMAlgorithm implements GreedySCMCalculation, MultiResultMergeSCMAlgorithm {
    TreeScorer[] scorerList;
    final Tree[] trees;
    Tree mergedSupertree = null;


    public MultiGreedySCMAlgorithm(Tree[] trees, TreeScorer... scorerList) {
        super(new GreedyTreeSelector(scorerList[0],trees));
        this.scorerList = scorerList;
        this.trees = trees;
    }

    public MultiGreedySCMAlgorithm(Logger logger, ExecutorService executorService, TreeSelector selector, Tree[] trees, TreeScorer[] scorerList) {
        super(logger, executorService, selector);
        this.trees = trees;
        this.scorerList = scorerList;
    }

    public MultiGreedySCMAlgorithm(Logger logger, TreeSelector selector, Tree[] trees, TreeScorer[] scorerList) {
        super(logger, selector);
        this.trees = trees;
        this.scorerList = scorerList;
    }

    public MultiGreedySCMAlgorithm(TreeSelector selector, Tree[] trees, TreeScorer[] scorerList) {
        super(selector);
        this.trees = trees;
        this.scorerList = scorerList;
    }

    @Override
    protected List<TreePair> calculateSuperTrees() {
        List<TreePair> superTrees =  new ArrayList<>(scorerList.length);
        superTrees.add(calculateGreedyConsensus(selector,false));
        for (int i = 1; i < scorerList.length; i++) {
            TreeScorer scorer = scorerList[i];
            selector.setScorer(scorer);
            selector.init(trees);
            superTrees.add(calculateGreedyConsensus(selector,false));
        }
        return superTrees;
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

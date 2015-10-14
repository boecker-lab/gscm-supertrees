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
public class MultiGreedySCMAlgorithm extends AbstractSCMAlgorithm implements GreedySCMCalculation, MultiResultMergeSCMAlgorithm {
    private final TreeScorer[] scorerList;
    private final Tree[] inputTrees;
    private Tree mergedSupertree = null;


    public MultiGreedySCMAlgorithm(Tree[] inputTrees, TreeScorer... scorerList) {
        super(new GreedyTreeSelector(scorerList[0], inputTrees));
        this.scorerList = scorerList;
        this.inputTrees = inputTrees;
    }

    public MultiGreedySCMAlgorithm(Logger logger, ExecutorService executorService, TreeSelector selector, Tree[] inputTrees, TreeScorer[] scorerList) {
        super(logger, executorService, selector);
        this.inputTrees = inputTrees;
        this.scorerList = scorerList;
    }

    public MultiGreedySCMAlgorithm(Logger logger, TreeSelector selector, Tree[] inputTrees, TreeScorer[] scorerList) {
        super(logger, selector);
        this.inputTrees = inputTrees;
        this.scorerList = scorerList;
    }

    public MultiGreedySCMAlgorithm(TreeSelector selector, Tree[] inputTrees, TreeScorer[] scorerList) {
        super(selector);
        this.inputTrees = inputTrees;
        this.scorerList = scorerList;
    }

    @Override
    protected List<TreePair> calculateSuperTrees() {
        List<TreePair> superTrees =  new ArrayList<>(scorerList.length);
        superTrees.add(calculateGreedyConsensus(selector,false));
        for (int i = 1; i < scorerList.length; i++) {
            TreeScorer scorer = scorerList[i];
            selector.setScorer(scorer);
            selector.init(inputTrees);
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

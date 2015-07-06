package scmAlgorithm;

import epos.model.tree.Tree;
import scmAlgorithm.treeScorer.OverlapScorer;
import scmAlgorithm.treeScorer.TreeScorer;
import scmAlgorithm.treeSelector.GreedyTreeSelector;
import scmAlgorithm.treeSelector.TreePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fleisch on 15.06.15.
 */
public class MultiGreedySCMAlgorithm extends AbstractSCMAlgorithm implements GreedySCMCalculation, MultiResultMergeSupertreeAlgorithm {
    TreeScorer[] scorerList;
    final Tree[] trees;
    Tree supertree = null;


    public MultiGreedySCMAlgorithm(Tree[] trees, TreeScorer... scorerList) {
        super(new GreedyTreeSelector(scorerList[0],trees));
        this.scorerList = scorerList;
        this.trees = trees;
    }

    @Override
    protected List<TreePair> calculateSuperTrees() {
        List<TreePair> superTrees =  new ArrayList<>(scorerList.length);
        superTrees.add(calculateGreedyConsensus(selector));
        for (int i = 1; i < scorerList.length; i++) {
            TreeScorer scorer = scorerList[i];
            selector.setScorer(scorer);
            selector.init(trees);
            superTrees.add(calculateGreedyConsensus(selector));
        }
        return superTrees;
    }


    @Override
    public Tree getSupertree() {
        if (supertree == null)
            supertree = getMergedSupertree();
        return supertree;
    }
}

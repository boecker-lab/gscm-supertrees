package scm.algorithm;


import epos.model.tree.Tree;
import org.apache.log4j.Logger;
import scm.algorithm.treeSelector.TreeSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by fleisch on 10.02.15.
 */
public class GreedySCMAlgorithm extends AbstractSCMAlgorithm {
    final TreeSelector selector;

    public GreedySCMAlgorithm(TreeSelector selector) {
        super();
        this.selector = selector;
    }

    public GreedySCMAlgorithm(Logger logger, ExecutorService executorService, TreeSelector selector) {
        super(logger, executorService);
        this.selector = selector;
    }

    public GreedySCMAlgorithm(Logger logger, TreeSelector selector) {
        super(logger);
        this.selector = selector;
    }


    @Override
    public void setInput(List<Tree> trees) {
        setInput(trees.toArray(new Tree[trees.size()]));
    }

    public void setInput(Tree... trees) {
        selector.setInputTrees(trees);
    }

    @Override
    protected List<Tree> calculateSuperTrees() {
        return new ArrayList<>(Arrays.asList(calculateSuperTree()));
    }

    Tree calculateSuperTree() {
        return calculateGreedyConsensus(selector, true);
    }
}

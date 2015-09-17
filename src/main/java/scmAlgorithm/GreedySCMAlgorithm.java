package scmAlgorithm;


import org.apache.log4j.Logger;
import scmAlgorithm.treeSelector.TreePair;
import scmAlgorithm.treeSelector.TreeSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by fleisch on 10.02.15.
 */
public class GreedySCMAlgorithm extends AbstractSCMAlgorithm implements GreedySCMCalculation {
    public GreedySCMAlgorithm(TreeSelector selector) {
        super(selector);
    }

    public GreedySCMAlgorithm(Logger logger, ExecutorService executorService, TreeSelector selector) {
        super(logger, executorService, selector);
    }

    public GreedySCMAlgorithm(Logger logger, TreeSelector selector) {
        super(logger, selector);
    }

    @Override
    protected List<TreePair> calculateSuperTrees() {
        return new ArrayList<>(Arrays.asList(calculateSuperTree()));
    }

    protected TreePair calculateSuperTree() {
        return calculateGreedyConsensus(selector,true);
    }
}

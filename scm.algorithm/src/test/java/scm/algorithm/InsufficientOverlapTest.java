/*
package scm.algorithm;

import org.junit.runners.Parameterized;
import phyloTree.model.tree.Tree;
import scm.algorithm.treeSelector.TreeScorer;
import scm.algorithm.treeSelector.TreeScorers;
import scm.algorithm.treeSelector.TreeSelector;

import java.util.LinkedList;
import java.util.List;

*/
/**
 * Created by fleisch on 22.04.16.
 *//*

public class InsufficientOverlapTest {

    SCMAlgorithm algo;
    TreeScorer scorer;

    public InsufficientOverlapTest(Tree[] inputData, TreeScorer scorer) {
        this.method = method;
        this.scorer = scorer;
    }

    @Parameterized.Parameters
    public static List<Object[]> data() {
        List<Object[]> paras = new LinkedList<>();
        for (TreeScorer scorer : TreeScorers.getFullScorerArray(false)) {
            for (TreeSelector.ConsensusMethod method : TreeSelector.ConsensusMethod.values()) {
//                paras.add(new Object[]{LOCATIONS.newickInput1000(), LOCATIONS.newickSCM1000_NORoot(),TreeSelector.ConsensusMethod.STRICT , scorer});
                paras.add(new Object[]{LOCATIONS.newickInput100(), LOCATIONS.newickSCM100_NORoot(),method , scorer});
//                paras.add(new Object[]{LOCATIONS.newickInput1000(), LOCATIONS.newickSCM1000_NORoot(), method, scorer});
            }
        }
        return paras;
    }
}
*/

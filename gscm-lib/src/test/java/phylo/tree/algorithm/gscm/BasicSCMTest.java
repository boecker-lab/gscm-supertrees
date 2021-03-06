package phylo.tree.algorithm.gscm;

import phylo.tree.algorithm.gscm.treeSelector.TreeScorer;
import phylo.tree.algorithm.gscm.treeSelector.TreeScorers;
import phylo.tree.algorithm.consensus.loose.LooseConsensus;
import phylo.tree.model.Tree;

import java.util.Arrays;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 06.11.15.
 */
public abstract class BasicSCMTest {
    public static final Locations LOCATIONS = new Locations();

    public static final String[] scorerNames = getScorerNames();
    public static final String SEP = System.getProperty("line.separator");

    Tree[] inputData;
    Tree scmResult;

    public BasicSCMTest(Tree[] inputData, Tree scmResult) {
        this.inputData = inputData;
        this.scmResult = scmResult;
    }

    public BasicSCMTest() {
        this(null,null);
    }

    protected static String[] getScorerNames() {
        TreeScorer[] scorerArray = TreeScorers.getFullScorerArray(false);
        String[] names = new String[scorerArray.length];
        for (int i = 0; i < scorerArray.length; i++) {
            names[i] = scorerArray[i].toString();
        }
        return names;
    }


    protected static String getMultiScorerName(int[] indices) {
        TreeScorer[] scorerArray = TreeScorers.getFullScorerArray(false);
        String[] names = new String[indices.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = scorerArray[indices[i]].toString();
        }
        return Arrays.toString(names);
    }

    protected static Tree getSemiStrict(Tree[] tree, int[] indices) {
        if (indices == null || indices.length == 0)
            return null;

        if (indices.length == 1)
            return tree[indices[0]];

        Tree[] input = new Tree[indices.length];
        for (int i = 0; i < indices.length; i++) {
            input[i] = tree[indices[i]];
        }
        LooseConsensus c = new LooseConsensus();
        c.setInput(Arrays.asList(input));
        c.run();
        return c.getResult();
    }
}

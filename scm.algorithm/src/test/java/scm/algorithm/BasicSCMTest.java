package scm.algorithm;

import epos.algo.consensus.loose.LooseConsensus;
import epos.model.tree.Tree;
import epos.model.tree.io.Newick;
import scm.algorithm.treeSelector.TreeScorer;

import java.io.File;
import java.util.Arrays;

/**
 * Created by fleisch on 06.11.15.
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


    static String[] getScorerNames() {
        TreeScorer[] scorerArray = TreeScorer.CompleteScorerCombo(false);
        String[] names = new String[scorerArray.length];
        for (int i = 0; i < scorerArray.length; i++) {
            names[i] = scorerArray[i].toString();
        }
        return names;
    }


    static String getMultiScorerName(int[] indices) {
        TreeScorer[] scorerArray = TreeScorer.CompleteScorerCombo(false);
        String[] names = new String[indices.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = scorerArray[indices[i]].toString();
        }
        return Arrays.toString(names);
    }

    static Tree getSemiStrict(Tree[] tree, int[] indices) {
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


    public static class Locations {
        public Tree[] newickInput100(){
            return getTrees(getClass().getResource("/scm/algorithm/sm.13.sourceTrees_OptSCM-Rooting.tre").getFile());
        }
        public Tree newickSCM100(){
            return getTrees(getClass().getResource("/scm/algorithm/sm.13.sourceTrees.scmTree.tre_OptRoot.tre").getFile())[0];
        }
        public Tree[] newickInput100_NORoot(){
            return getTrees(getClass().getResource("/scm/algorithm/sm.13.sourceTrees.tre").getFile());
        }
        public Tree newickSCM100_NORoot(){
            return getTrees(getClass().getResource("/scm/algorithm/sm.13.sourceTrees.scmTree.tre").getFile())[0];
        }

        public Tree[] newickInput1000(){
            return getTrees(getClass().getResource("/scm/algorithm/sm.5.sourceTrees_OptSCM-Rooting.tre").getFile());
        }
        public Tree newickSCM1000(){
            return getTrees(getClass().getResource("/scm/algorithm/sm.5.sourceTrees.scmTree.tre_OptRoot.tre").getFile())[0];
        }
        public Tree[] newickInput1000_NORoot(){
            return getTrees(getClass().getResource("/scm/algorithm/sm.5.sourceTrees.tre").getFile());
        }
        public Tree newickSCM1000_NORoot(){
            return getTrees(getClass().getResource("/scm/algorithm/sm.5.sourceTrees.scmTree.tre").getFile())[0];
        }


        private Tree[] getTrees(String path){
            return Newick.getTreeFromFile(new File(path));
        }

        //tree constants
/*//    final static String sourceTreeLocation = Global.SM_SOURCE_TREES_RAXML_SCM_OPT;//Global.SM_SOURCE_TREES_RAXML_SCM;//
//    final static String sourceTreeLocation = Global.SM_SOURCE_TREES_RAXML;//Global.SM_SOURCE_TREES_RAXML_SCM;//
    final static String sourceTreeLocation = Global.SMOG_SOURCE_TREES_RAXML;//Global.SM_SOURCE_TREES_RAXML_SCM;//
    //        final static String alternateSourceTreeLocation =  Global.SM_SOURCE_TREES_RAXML_MODEL_LEAST;//Global.SM_SOURCE_TREES_RAXML_SCM;//
    final static String alternateSourceTreeLocation = Global.SMOG_SOURCE_TREES_RAXML;//Global.SM_SOURCE_TREES_RAXML_SCM;//
    //        final static String scmTreeLocation = Global.SM_SCM_TREES_RAXML_OPT_ROOTED; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//
//    final static String scmTreeLocation = Global.SM_SCM_TREES_RAXML; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//
//    final static String scmTreeLocation = Global.SMOG_SCM_TREES_SUPER_ROOTED; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//
    final static String scmTreeLocation = Global.SMOG_SCM_TREES; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//
    //    final static String modelTreeLocation = Global.SM_MODEL_TREE_PATH + "sm_data." + Global.TAG_INSTANCE + ".model_tree"; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//
    final static String modelTreeLocation = Global.SMOG_MODEL_TREES; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//*/


    }


}

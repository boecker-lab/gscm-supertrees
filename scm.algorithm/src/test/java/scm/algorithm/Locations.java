package scm.algorithm;

import phyloTree.io.Newick;
import phyloTree.model.tree.Tree;

import java.io.File;

/**
 * Created by fleisch on 13.11.15.
 */
public class Locations {
    public Tree[] newickInput100() {
        return getTrees(getClass().getResource("/scm/algorithm/sm.13.sourceTrees_OptSCM-Rooting.tre").getFile());
    }

    public Tree newickSCM100() {
        return getTrees(getClass().getResource("/scm/algorithm/sm.13.sourceTrees.scmTree.tre_OptRoot.tre").getFile())[0];
    }

    public Tree[] newickInput100_NORoot() {
        return getTrees(getClass().getResource("/scm/algorithm/sm.13.sourceTrees.tre").getFile());
    }

    public Tree newickSCM100_NORoot() {
        return getTrees(getClass().getResource("/scm/algorithm/sm.13.sourceTrees.scmTree.tre").getFile())[0];
    }

    public Tree[] newickInput1000() {
        return getTrees(getClass().getResource("/scm/algorithm/sm.5.sourceTrees_OptSCM-Rooting.tre").getFile());
    }

    public Tree newickSCM1000() {
        return getTrees(getClass().getResource("/scm/algorithm/sm.5.sourceTrees.scmTree.tre_OptRoot.tre").getFile())[0];
    }

    public Tree[] newickInput1000_NORoot() {
        return getTrees(getClass().getResource("/scm/algorithm/sm.5.sourceTrees.tre").getFile());
    }

    public Tree newickSCM1000_NORoot() {
        return getTrees(getClass().getResource("/scm/algorithm/sm.5.sourceTrees.scmTree.tre").getFile())[0];
    }

    public Tree[] newickInsufficientInput500() {
        return getTrees(getClass().getResource("/scm/algorithm/sm.4.sourceTrees_ModelLeastTaxaDeletion-Rooting.tre").getFile());
    }

    public Tree[] insufficientInputSimple() {
        return getTrees(getClass().getResource("/scm/algorithm/simpleInsufficient.tre").getFile());
    }

    private Tree[] getTrees(String path) {
        return Newick.getTreeFromFile(new File(path));
    }

    //tree constants
//    final static String sourceTreeLocation = Global.SM_SOURCE_TREES_RAXML_SCM_OPT;//Global.SM_SOURCE_TREES_RAXML_SCM;//
//    final static String sourceTreeLocation = Global.SM_SOURCE_TREES_RAXML;//Global.SM_SOURCE_TREES_RAXML_SCM;//
//final static String sourceTreeLocation = Global.SMOG_SOURCE_TREES_RAXML;//Global.SM_SOURCE_TREES_RAXML_SCM;//
//        final static String alternateSourceTreeLocation =  Global.SM_SOURCE_TREES_RAXML_MODEL_LEAST;//Global.SM_SOURCE_TREES_RAXML_SCM;//
//final static String alternateSourceTreeLocation = Global.SMOG_SOURCE_TREES_RAXML;//Global.SM_SOURCE_TREES_RAXML_SCM;//
//        final static String scmTreeLocation = Global.SM_SCM_TREES_RAXML_OPT_ROOTED; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//
//    final static String scmTreeLocation = Global.SM_SCM_TREES_RAXML; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//
//    final static String scmTreeLocation = Global.SMOG_SCM_TREES_SUPER_ROOTED; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//
//final static String scmTreeLocation = Global.SMOG_SCM_TREES; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//
//    final static String modelTreeLocation = Global.SM_MODEL_TREE_PATH + "sm_data." + Global.TAG_INSTANCE + ".model_tree"; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//
//final static String modelTreeLocation = Global.SMOG_MODEL_TREES; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//


}

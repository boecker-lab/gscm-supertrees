package treeUtils;

import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
import treeUtils.TreeUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anika on 13.05.2015.
 */
public class TreeEquals {

    Tree one;
    Tree two;


    public boolean getTreeEquals (Tree one, Tree two){
        if (one==two) return true;
        else {
            //compare number of vertices
            if (one.vertexCount()!=two.vertexCount()) return false;
            List<TreeNode> leaveslistone = new ArrayList<TreeNode>(Arrays.asList(one.getLeaves()));
            List<TreeNode> leaveslisttwo = new ArrayList<TreeNode>(Arrays.asList(two.getLeaves()));
            //compare number and labeling of leaves
            if (!TreeUtils.StringListsEquals(TreeUtils.helpgetLabelsFromNodes(leaveslistone), TreeUtils.helpgetLabelsFromNodes(leaveslisttwo))) return false;
            //compare TreeStructure
            if (!CompareTreeStructure(one, two)) return false;
        }
        return true;
    }

    private boolean helpCompareTreeStructure (Tree one, TreeNode nodone, Tree two, TreeNode nodtwo, List<String> list1, List<String> list2, List<TreeNode> childrenone, List<TreeNode> childrentwo){
        boolean foundcorrectiteration = false;
        list1 = TreeUtils.helpgetLabelsFromNodes(Arrays.asList(nodone.getLeaves()));
        list2 = TreeUtils.helpgetLabelsFromNodes(Arrays.asList(nodtwo.getLeaves()));
        if (!TreeUtils.StringListsEquals(list1, list2)) return false;
        else {
            childrenone = new ArrayList<TreeNode>(nodone.getChildren());
            childrentwo = new ArrayList<TreeNode>(nodtwo.getChildren());
            for (TreeNode iter1 : childrenone){
                foundcorrectiteration = false;
                for (TreeNode iter2 : childrentwo){
                    if (!iter1.isLeaf() && !iter2.isLeaf()){
                        if (helpCompareTreeStructure(one, iter1, two, iter2, list1, list2, childrenone, childrentwo)){
                            foundcorrectiteration = true;
                            break;
                        }
                    }
                    else if (iter1.getLabel().equalsIgnoreCase(iter2.getLabel())){
                        foundcorrectiteration = true;
                        break;
                    }
                }
                if (!foundcorrectiteration) return false;
            }
            return true;
        }
    }

    public boolean CompareTreeStructure (Tree one, Tree two){
        TreeNode start1 = one.getRoot();
        TreeNode start2 = two.getRoot();
        if (helpCompareTreeStructure(one, start1, two, start2, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<TreeNode>(), new ArrayList<TreeNode>())) return true;
        else return false;
    }



}

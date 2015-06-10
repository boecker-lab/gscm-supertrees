package treeUtils;

import epos.model.tree.Tree;
import epos.model.tree.TreeNode;

import java.util.*;

/**
 * Created by Anika on 13.05.2015.
 */
public class TreeUtils {

    public static ArrayList<String> getOverLappingNodes (Tree one, Tree two){
        List<TreeNode> leavesone = new ArrayList<TreeNode>(Arrays.asList(one.getLeaves()));
        List<TreeNode> leavestwo = new ArrayList<TreeNode>(Arrays.asList(two.getLeaves()));
        ArrayList<String> tokeep = new ArrayList<String>();
        for (TreeNode x : leavesone){
            for (TreeNode y : leavestwo){
                if (x.getLabel().equals(y.getLabel())){
                    tokeep.add(x.getLabel());
                }
            }
        }
        return tokeep;
    }

    public static List<String> generateCustomDepthFirstListChildren (Tree input){
        List<String> dnodes = new ArrayList<String>();
        TreeNode root = input.getRoot();
        helpgenerateCustomDepthFirstListChildren(input, root, dnodes);
        return dnodes;
    }

    private static void helpgenerateCustomDepthFirstListChildren(Tree input, TreeNode cur, List<String> dnodes){
        List<TreeNode> children = new ArrayList<TreeNode>(cur.getChildren());
        Stack<TreeNode> depth = new Stack<TreeNode>();
        for (TreeNode iter : children){
            if (iter.getChildren().isEmpty()) depth.add(iter);
        }
        for (TreeNode iter: children){
            if (!iter.getChildren().isEmpty()) depth.add(iter);
        }
        int size = depth.size();
        for (int iter=0; iter<size; iter++){
            TreeNode act = depth.pop();
            if (!act.isLeaf()){
                helpgenerateCustomDepthFirstListChildren(input, act, dnodes);
            }
            else dnodes.add(act.getLabel());
        }
    }

    //public static boolean treeNodeEqualsIgnoreLabel (TreeNode one, TreeNode two){
    //    if (one == two) return true;
    //
    //}

    public static List<String> helpgetLabelsFromNodes(List<TreeNode> input){
        List<String> output = new ArrayList<String>();
        String def = "";
        for (TreeNode nod : input){
            if (nod.getLabel()!=null){
                output.add(def.concat(nod.getLabel()));
            }
            else output.add(def.concat(""));
        }
        return output;
    }

    public static List<TreeNode> helpgetTreeNodesFromLabels(List<String> li, Tree input){
        List<TreeNode> output = new ArrayList<TreeNode>();
        for (String x: li){
            output.add(input.getVertex(x));
        }
        return output;
    }

    public static List<String> StringListMultipleElements (List<String> input){
        List<String> save = new ArrayList<String>();
        List<String> output = new ArrayList<String>();
        for (String s : input){
            if (save.contains(s)){
                output.add(s);
            }
            else save.add(s);
        }
        return output;
    }

    public static boolean StringListsEquals(List<String> one, List<String> two){
        if (one == null && two == null){
            return true;
        }
        if((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()){
            return false;
        }
        List copyone = new ArrayList<String>(one);
        List copytwo = new ArrayList<String>(two);
        Collections.sort(copyone);
        Collections.sort(copytwo);
        return copyone.equals(copytwo);
    }




}

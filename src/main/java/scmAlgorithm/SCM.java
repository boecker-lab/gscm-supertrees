package scmAlgorithm;


import epos.algo.consensus.nconsensus.NConsensus;
import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
import epos.model.tree.io.Newick;
import epos.model.tree.treetools.FN_FP_RateComputer;
import epos.model.tree.treetools.TreeUtilsBasic;
import org.apache.log4j.Level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.Random;
import org.jgrapht.traverse.*;
import treeUtils.TreeEquals;
import treeUtils.TreeUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Anika
 * Date: 27.10.14
 * Time: 13:13
 * To change this template use File | Settings | File Templates.
 */


public class SCM {

    private LinkedHashMap<String, List<String>> TreeCut1 = new LinkedHashMap<String, List<String>>();
    private LinkedHashMap<String, List<String>> TreeCut2 = new LinkedHashMap<String, List<String>>();
    private List<String> tolerated = new ArrayList<String>();
    //private List<String> store2pathnnodes = new ArrayList<String>();

    public Map.Entry<String,List<String>>[] getTreeCut1EntrySet (){
        Set<Map.Entry<String, List<String>>> mapValues = TreeCut1.entrySet();
        int maplength = mapValues.size();
        Map.Entry<String,List<String>>[] TreeCut1entries = new Map.Entry[maplength];
        return mapValues.toArray(TreeCut1entries);
    }

    public Map.Entry<String,List<String>>[] getTreeCut2EntrySet (){
        Set<Map.Entry<String, List<String>>> mapValues = TreeCut2.entrySet();
        int maplength = mapValues.size();
        Map.Entry<String,List<String>>[] TreeCut2entries = new Map.Entry[maplength];
        return mapValues.toArray(TreeCut2entries);
    }

    public List<String> getTolerated(){
        return this.tolerated;
    }

    public void setTolerated(List<String> tolerated){
        this.tolerated = tolerated;
    }


    /*public Tree getPaperConsensus (List<Tree> input) {
        List<Tree> output = input.subList(0, input.size()-1);
        List<Tree> save;
        Tree one;
        Tree two;
        Tree three;
        while (output.size() > 1){
            save = getTreesForPaperSCM(output);
            one = save.get(0);
            two = save.get(1);
            three = save.get(2);
            System.out.println("Got one SCM: " +Newick.getStringFromTree(three));
            System.out.println(output.size()+" left.");
            output.remove(one);
            output.remove(two);
            output.add(three);
        }
        return output.get(0);
    }*/

    /*public Tree getSuperfineConsensus (List<Tree> input){
        List<Tree> output = input.subList(0, input.size()-1);
        List<Tree> save;
        Tree one;
        Tree two;
        Tree three;
        while (output.size() > 1){
            save = getTreesWithBiggestOverlap(output);
            one = save.get(0);
            two = save.get(1);
            three = SCM(one, two);
            System.out.println("Got one SCM: " +Newick.getStringFromTree(three));
            System.out.println(output.size()+" left.");
            output.remove(one);
            output.remove(two);
            output.add(three);
        }
        return output.get(0);
    }*/

    /*public List<Tree> getTreesForPaperSCM (List<Tree> input){
        float curscore;
        float resolution;
        Tree scm;
        Tree maxscm = new Tree();
        float maxscore = 0;
        int mem1 = 0, mem2 = 0;
        int a, b, c = 0;
        List<Tree> output = new ArrayList<Tree>();
        for (Tree iter : input){
            for (Tree cur : input){
                if (!iter.equals(cur)){
                    //TODO is it possible that two trees have no overlap?
                    if (getOverLappingNodes(iter, cur).size()==0);
                    else {
                        //System.out.println("Calculating scm of tree "+Newick.getStringFromTree(iter)+" and "+Newick.getStringFromTree(cur)+".");
                        scm = SCM(iter, cur);
                        //TODO look up the definition of resolution
                        //a = scmAlgorithm.vertexCount();
                        //b = scmAlgorithm.getLeaves().length-3;
                        //c = scmAlgorithm.getLeaves().length;
                        resolution = scm.vertexCount() / (scm.getLeaves().length-3);
                        //resolution = a / b;
                        //TODO is this possible or do I have to take |L(Ti)UL(Tj)|?
                        curscore = resolution / scm.getLeaves().length;
                        //curscore = resolution / c;
                        if (curscore > maxscore){
                            maxscm = scm;
                            maxscore = curscore;
                            mem1 = input.indexOf(iter);
                            mem2 = input.indexOf(cur);
                        }
                    }
                }
            }
        }
        if (maxscore == 0 && input.size()>=2){
            output.add(input.get(0));
            output.add(input.get(1));
            System.err.println("In der Liste gibt es keine Bäume mit Scoring Ã¼ber 0");
        }
        else if (input.size()<2){
            System.err.println("Im Input existieren weniger als 2 Bäume");
        }
        else {
            output.add(input.get(mem1));
            output.add(input.get(mem2));
            output.add(maxscm);
        }
        return output;
    }*/

    /*public List<Tree> getTreesWithBiggestOverlap (List<Tree> input){
        int cursize;
        int maxsize = 0;
        int mem1 = 0, mem2 = 0;
        List<Tree> output = new ArrayList<Tree>();
        for (Tree iter : input){
            for (Tree cur : input){
                if (!iter.equals(cur)){
                    cursize = getOverLappingNodes(iter, cur).size();
                    if (cursize > maxsize){
                        maxsize = cursize;
                        mem1 = input.indexOf(iter);
                        mem2 = input.indexOf(cur);
                    }
                }
            }
        }
        if (maxsize == 0 && input.size()>=2){
            output.add(input.get(0));
            output.add(input.get(1));
            System.err.println("In der Liste gibt es keine Bäume mit Overlap");
        }
        else if (input.size()<2){
            System.err.println("Im Input existieren weniger als 2 Bäume");
        }
        else {
            output.add(input.get(mem1));
            output.add(input.get(mem2));
        }
        return output;
    }*/

    public void takeCareOfCollisions(Tree consensus, Tree one, Tree two){
        Set<Map.Entry<String, List<String>>> mapValues1 = TreeCut1.entrySet();
        Set<Map.Entry<String, List<String>>> mapValues2 = TreeCut2.entrySet();
        int maplength1 = mapValues1.size();
        int maplength2 = mapValues2.size();
        Map.Entry<String,List<String>>[] TreeCut1entries = new Map.Entry[maplength1];
        Map.Entry<String,List<String>>[] TreeCut2entries = new Map.Entry[maplength2];
        mapValues1.toArray(TreeCut1entries);
        mapValues2.toArray(TreeCut2entries);
        List<Map.Entry<String,List<String>>> TreeCut1list = Arrays.asList(TreeCut1entries);
        List<Map.Entry<String,List<String>>> TreeCut2list = Arrays.asList(TreeCut2entries);
        Map<List<String>, List<String>> order = new HashMap();
        List<String> currvalue;
        String currkey;
        List<String> currlist;
        Iterable<TreeNode> nodes = consensus.vertices();
        Iterator<TreeNode> iterator = nodes.iterator();
        List<TreeNode> innernodeleaves;
        List<String> innernodeleaveshelp;
        List<TreeNode> innernodechildren;
        List<String> innernodechildrenhelp;
        List<TreeNode> innernodet1children = new ArrayList<TreeNode>();
        List<TreeNode> innernodet2children = new ArrayList<TreeNode>();
        TreeNode curnode;
        TreeNode innernodet1;
        TreeNode innernodet2;
        //while (nodes.iterator().hasNext()){
        while (iterator.hasNext()){
            //TreeNode curnode = nodes.iterator().next();
            curnode = iterator.next();
            //if (curnode.getLabel().equalsIgnoreCase("t73")){
            //    System.out.print("");
            //}
            innernodet1children.clear();
            innernodet2children.clear();
            if (!curnode.isLeaf()){
                innernodeleaveshelp = TreeUtils.helpgetLabelsFromNodes(Arrays.asList(curnode.getLeaves()));
                //TODO !
                if (TreeUtils.StringListsEquals(innernodeleaveshelp, Arrays.asList(new String[]{"t31", "t55", "t86", "t63", "t46", "t70"}))){
                //    System.out.println("Hier");
                }
                innernodeleaves = Arrays.asList(curnode.getLeaves());
                innernodechildren = curnode.getChildren();
                innernodet1 = one.findLeastCommonAncestor(TreeUtils.helpgetTreeNodesFromLabels(innernodeleaveshelp, one));
                innernodet2 = two.findLeastCommonAncestor(TreeUtils.helpgetTreeNodesFromLabels(innernodeleaveshelp, two));
                int numinnernodechildren = innernodechildren.size();
                for (int iter=0; iter<numinnernodechildren; iter++){
                    innernodet1children.add(one.findLeastCommonAncestor(TreeUtils.helpgetTreeNodesFromLabels(TreeUtils.helpgetLabelsFromNodes(Arrays.asList(innernodechildren.get(iter).getLeaves())), one)));
                    innernodet2children.add(two.findLeastCommonAncestor(TreeUtils.helpgetTreeNodesFromLabels(TreeUtils.helpgetLabelsFromNodes(Arrays.asList(innernodechildren.get(iter).getLeaves())), two)));
                    //innernodet2children.add(two.findLeastCommonAncestor(innernodechildren.get(iter).getLeaves()));
                    //innernodet2children.add(two.findLeastCommonAncestor(TreeUtils.helpgetTreeNodesFromLabels(innernodechildrenhelp, two).get(iter).getLeaves()));
                }
                if (innernodet1children.contains("t10")){
                    System.out.print("");
                }
                int numinnernodet1children = innernodet1children.size();
                int numinnernodet2children = innernodet2children.size();
                for (int iter=0; iter<numinnernodet1children; iter++){
                    helpCollisionsFindNodesAtSameEdge (consensus, innernodechildren.get(iter), one, 1, innernodet1, innernodet1children.get(iter), order, TreeCut1list);
                }
                for (int iter=0; iter<numinnernodet2children; iter++){
                    helpCollisionsFindNodesAtSameEdge (consensus, innernodechildren.get(iter), two, 2, innernodet2, innernodet2children.get(iter), order, TreeCut2list);
                }
                //for (TreeNode child2 : innernodet2children) helpCollisionsFindNodesAtSameEdge (consensus, two, 2, innernodet2, child2, order, TreeCut2list);
            }
        }
        curnode = consensus.getRoot();
        innernodeleaveshelp = TreeUtils.helpgetLabelsFromNodes(Arrays.asList(curnode.getLeaves()));
        innernodet1 = one.findLeastCommonAncestor(TreeUtils.helpgetTreeNodesFromLabels(innernodeleaveshelp, one));
        innernodet2 = two.findLeastCommonAncestor(TreeUtils.helpgetTreeNodesFromLabels(innernodeleaveshelp, two));
        if (innernodet1.getParent() != null && innernodet2.getParent() != null){
            System.out.print("");
            helpCollisionsFindNodesOverRoot(consensus, one, 1, innernodet1, order, TreeCut1list);
            helpCollisionsFindNodesOverRoot(consensus, two, 2, innernodet2, order, TreeCut2list);
        }




        //System.out.println("Es gibt "+order.size()+" verschiedene Kanten - Collisions.");

        List<String> store2pathnodes = new ArrayList<String>();
        HashMap<String,List<String>> checkfordoubles = new HashMap<String, List<String>>();
        List<String> tolerated = new ArrayList<String>();
        HashMap<String, List<String>> special2nodes = new HashMap<String, List<String>>();
        List<String> store = new ArrayList<String>();
        List<String> val;
        for (List<String> key : order.keySet()){
            List<String> curkey = new ArrayList<String>(key);
            val = new ArrayList<String>(order.get(key));
            for (String iter : val){
                if (!iter.equalsIgnoreCase("Tree1")&&!iter.equalsIgnoreCase("Tree2")){
                    if (checkfordoubles.containsKey(iter)){
                        if (!store2pathnodes.contains(iter)) store2pathnodes.add(iter);
                        if (special2nodes.containsKey(iter)){
                            store = new ArrayList<String>(special2nodes.get(iter));
                            special2nodes.remove(iter);
                            store.addAll(curkey);
                            special2nodes.put(iter, store);
                        }
                        else {
                            store = new ArrayList<String>(checkfordoubles.get(iter));
                            store.add("Polytomy");
                            store.addAll(curkey);
                            special2nodes.put(iter, store);
                        }
                    }
                    else checkfordoubles.put(iter, curkey);
                }
            }
        }
        tolerated.addAll(store2pathnodes);
        for (String el : store2pathnodes){
            if (TreeCut2.containsKey(el)){
                TreeCut2.remove(el);
            }
            if (TreeCut1.containsKey(el)){
                TreeCut1.remove(el);
            }
        }
        for (List<String> ke : order.keySet()){
            List<String> list = new ArrayList<String> (order.get(ke));
            if (list.contains("Tree1")&&list.contains("Tree2")){
                while (list.contains("Tree1")){
                    list.remove("Tree1");
                }
                while (list.contains("Tree2")){
                    list.remove("Tree2");
                }
                List<String> copylist = new ArrayList<String>(list);
                for (String el : copylist){
                    if (TreeCut2.containsKey(el)){
                        TreeCut2.remove(el);
                    }
                    if (TreeCut1.containsKey(el)){
                        TreeCut1.remove(el);
                    }
                    if (store2pathnodes.contains(el)){
                        list.remove(el);
                        /*if (special2nodes.containsKey(el)){
                            store = special2nodes.get(el);
                            store.addAll(ke);
                            special2nodes.put(el, store);
                        }
                        else special2nodes.put(el, ke);*/
                    }
                    else tolerated.add(el);
                }
                List<String> newlist;
                List<String> work;
                //if (list.size() == 0){
                //    System.out.println("Hey");
                //}
                if (list.size() != 0){
                    for (int iter=list.size()-1; iter>0; iter--){
                        work = new ArrayList<String>(list);
                        newlist = work.subList(0,iter);
                        newlist.addAll(ke);
                        newlist.add("Polytomy");
                        TreeCut1.put(list.get(iter), newlist);
                    }
                    TreeCut1.put(list.get(0), ke);
                }
            }
        }
        for (String el : special2nodes.keySet()){
            TreeCut1.put(el, special2nodes.get(el));
        }
        setTolerated(tolerated);
    }


    private Map<List<String>, List<String>> helpCollisionsFindNodesOverRoot (Tree consensus, Tree input, int treenumber, TreeNode rootintree, Map<List<String>, List<String>> order, List<Map.Entry<String,List<String>>> treeentryset){
        List<TreeNode> currchildren = new ArrayList<TreeNode>();
        List<String> consensusleaves = TreeUtils.helpgetLabelsFromNodes(Arrays.asList(consensus.getLeaves()));
        TreeNode rootinconsensus = consensus.getRoot();
        TreeNode newparent = rootintree.getParent();
        List<String> currlist;
        List<String> currkey;
        boolean firstnewparent = true;
        TreeNode formernewparent = new TreeNode();
        while (newparent.getParent() != null){
            //currchildren are the child nodes of the node hanging at the path from child to parent at an inner node
            currchildren = newparent.getChildren();
            if (firstnewparent){
                currchildren.remove(rootintree);
            }
            else currchildren.remove(formernewparent);
            if (!currchildren.isEmpty()){
                for (TreeNode ch : currchildren){
                    //currkey contains all leaves of the child in the consensus-tree (or if it's a leaf, only the child)
                    //idea: if an element in currchildren is no leaf, we can find the one node in these nodes that will hang onto the child-polytomy
                    currkey = consensusleaves;
                    //make a new entry marking the edge we collect the nodes from
                    if (order.containsKey(currkey)){
                        currlist = order.get(currkey);
                    }
                    else currlist = new ArrayList<String>();
                    int currlistsize = currlist.size();
                    int counter = 0;
                    //if the found node is a leaf, we can just add it to the collection
                    if (ch.isLeaf()){
                        //if (!consensusleaves.contains(ch.getLabel())) currlist.add(ch.getLabel());
                        currlist.add(ch.getLabel());
                    }
                    else {
                        counter = 0;
                        //boolean inconsensus = false;
                        //for (TreeNode leave : ch.getLeaves()){
                        //    if (consensusleaves.contains(leave.getLabel())){
                        //        inconsensus = true;
                        //    }
                        //}
                        //if (!inconsensus){
                        for (TreeNode leave : ch.getLeaves()){
                            //TODO !
                            List<String> values = new ArrayList<String>();
                            for (Map.Entry<String, List<String>> en : treeentryset){
                                if (en.getKey().equalsIgnoreCase(leave.getLabel())) values = en.getValue();
                            }
                            //int index = treeentryset.indexOf(leave);
                            //if (treeentryset.get(treeentryset.indexOf(leave)).getValue().containsAll(currkey)) currlist.add(leave.getLabel());
                            //we search for the one leaf that hangs onto the child-polytomy
                            if (values.containsAll(currkey)){
                                currlist.add(leave.getLabel());
                                counter++;
                            }
                        }
                        //}

                    }
                    //TODO !
                    if (counter > 1) {
                        System.out.println("Neeeein! Waruuum?");
                    }
                    //TODO Bedingung korrekt?
                    if (!currlist.isEmpty() && currlistsize!=currlist.size()){
                        currlist.add("Tree".concat(String.valueOf(treenumber)));
                        order.put(currkey, currlist);
                    }
                }
            }
            formernewparent = newparent;
            firstnewparent = false;
            newparent = newparent.getParent();
        }
        return order;
    }


    private Map<List<String>, List<String>> helpCollisionsFindNodesAtSameEdge (Tree consensus, TreeNode childinconsensus, Tree input, int treenumber, TreeNode parent, TreeNode child, Map<List<String>, List<String>> order, List<Map.Entry<String,List<String>>> treeentryset){
        List<TreeNode> currchildren = new ArrayList<TreeNode>();
        List<String> consensusleaves = new ArrayList<String>(TreeUtils.helpgetLabelsFromNodes(Arrays.asList(consensus.getLeaves())));
        TreeNode newparent = child.getParent();
        List<String> currlist;
        List<String> currkey;
        boolean firstnewparent = true;
        TreeNode formernewparent = new TreeNode();
        //TODO ensure equalsNode does the right thing
        //while (!newparent.equalsNode(parent)){
        //boolean nodeon2childrenpaths = false;
        //boolean switchnodeon2childrenpaths = false;
        while (newparent != parent){
            //if (switchnodeon2childrenpaths) nodeon2childrenpaths = true;
            //currchildren are the child nodes of the node hanging at the path from child to parent at an inner node
            currchildren = newparent.getChildren();
            if (firstnewparent){
                currchildren.remove(child);
            }
            else currchildren.remove(formernewparent);
            if (!currchildren.isEmpty()){
                for (TreeNode ch : currchildren){

                    //currkey contains all leaves of the child in the consensus-tree (or if it's a leaf, only the child)
                    //idea: if an element in currchildren is no leaf, we can find the one node in these nodes that will hang onto the child-polytomy
                    if (childinconsensus.isLeaf()){
                        currkey = new ArrayList<String>();
                        currkey.add(childinconsensus.getLabel());
                    }
                    else currkey = TreeUtils.helpgetLabelsFromNodes(Arrays.asList(childinconsensus.getLeaves()));

                    //make a new entry marking the edge we collect the nodes from
                    if (order.containsKey(currkey)){
                        currlist = order.get(currkey);
                    }
                    else currlist = new ArrayList<String>();
                    int currlistsize = currlist.size();
                    int counter = 0;
                    //if the found node is a leaf, we can just add it to the collection
                    if (ch.isLeaf()){
                        if (!consensusleaves.contains(ch.getLabel())){
                            currlist.add(ch.getLabel());
                            //if (nodeon2childrenpaths){
                            //    store2pathnnodes.add(ch.getLabel());
                            //}
                        }
                        //else switchnodeon2childrenpaths = true;
                    }
                    else {
                        counter = 0;
                        boolean inconsensus = false;
                        for (TreeNode leave : ch.getLeaves()){
                            if (consensusleaves.contains(leave.getLabel())){
                                inconsensus = true;
                            }
                        }
                        if (!inconsensus){
                            for (TreeNode leave : ch.getLeaves()){
                                //TODO !
                                List<String> values = new ArrayList<String>();
                                for (Map.Entry<String, List<String>> en : treeentryset){
                                    if (en.getKey().equalsIgnoreCase(leave.getLabel())) values = en.getValue();
                                }
                                //int index = treeentryset.indexOf(leave);
                                //if (treeentryset.get(treeentryset.indexOf(leave)).getValue().containsAll(currkey)) currlist.add(leave.getLabel());
                                //we search for the one leaf that hangs onto the child-polytomy
                                if (values.containsAll(currkey)){
                                    currlist.add(leave.getLabel());
                                    //if (nodeon2childrenpaths){
                                    //    store2pathnnodes.add(leave.getLabel());
                                    //}
                                    counter++;
                                }
                            }
                        }
                        //else switchnodeon2childrenpaths = true;

                    }
                    //TODO !
                    if (counter > 1) {
                        System.out.println("Neeeein! Waruuum?");
                    }
                    //TODO Bedingung korrekt?
                    if (!currlist.isEmpty() && currlistsize!=currlist.size()){
                        currlist.add("Tree".concat(String.valueOf(treenumber)));
                        order.put(currkey, currlist);
                    }
                }
            }
            formernewparent = newparent;
            firstnewparent = false;
            newparent = newparent.getParent();
        }
        return order;
    }

    /*public void takeCareOfCollisions(){
        Set<Map.Entry<String, List<String>>> mapValues1 = TreeCut1.entrySet();
        Set<Map.Entry<String, List<String>>> mapValues2 = TreeCut2.entrySet();
        int maplength1 = mapValues1.size();
        int maplength2 = mapValues2.size();
        Map.Entry<String,List<String>>[] TreeCut1entries = new Map.Entry[maplength1];
        Map.Entry<String,List<String>>[] TreeCut2entries = new Map.Entry[maplength2];
        mapValues1.toArray(TreeCut1entries);
        mapValues2.toArray(TreeCut2entries);
        Map<List<String>, List<String>> order = new HashMap();
        List<String> currvalue;
        String currkey;
        List<String> currlist;

        for (int iter1=maplength1-1; iter1>=0; iter1--){
            currvalue = TreeCut1entries[iter1].getValue();
            currkey = TreeCut1entries[iter1].getKey();
            if (order.containsKey(currvalue)){
                currlist = order.get(currvalue);
            }
            else {
                currlist = new ArrayList<String>();
            }
            currlist.add(currkey);
            currlist.add("Tree1");
            order.put(currvalue, currlist);
        }


        for (int iter2=maplength2-1; iter2>=0; iter2--){
            currvalue = TreeCut2entries[iter2].getValue();
            currkey = TreeCut2entries[iter2].getKey();
            if (order.containsKey(currvalue)){
                currlist = order.get(currvalue);
            }
            else {
                currlist = new ArrayList<String>();
            }
            currlist.add(currkey);
            currlist.add("Tree2");
            order.put(currvalue, currlist);
        }

        System.out.println("Es gibt "+order.size()+" verschiedene Kanten - Collisions.");

        for (List<String> ke : order.keySet()){
            List<String> list = order.get(ke);
            if (list.contains("Tree1")&&list.contains("Tree2")){
                while (list.contains("Tree1")){
                    list.remove("Tree1");
                }
                while (list.contains("Tree2")){
                    list.remove("Tree2");
                }
                for (String el : list){
                    if (TreeCut2.containsKey(el)){
                        TreeCut2.remove(el);
                    }
                    if (TreeCut1.containsKey(el)){
                        TreeCut1.remove(el);
                    }
                }
                List<String> newlist;
                List<String> work;
                for (int iter=list.size()-1; iter>0; iter--){
                    work = new ArrayList<String>(list);
                    newlist = work.subList(0,iter);
                    newlist.addAll(ke);
                    newlist.add("Polytomy");
                    TreeCut1.put(list.get(iter), newlist);
                }
                TreeCut1.put(list.get(0), ke);
            }
        }

    }*/

    public boolean checkDirectionOfBackbone(List<String> nodes, Tree one, Tree two){
        List<String> dfiterator1 = TreeUtils.generateCustomDepthFirstListChildren(one);
        List<String> dfiterator2 = TreeUtils.generateCustomDepthFirstListChildren(two);
        boolean foundfirst = false;
        boolean foundlast = false;
        int iter = 0;
        String cur;
        int posf1;
        int posl1;
        String elf1 = "";
        String ell1 = "";
        //search for the first occurence of an element both trees have in common in depth first iterator of tree 1
        do {
            cur = dfiterator1.get(iter);
            if (nodes.contains(cur)) {
                foundfirst = true;
                //remember the element and its position
                posf1 = iter;
                elf1 = cur;
            }
            iter ++;
        } while (!foundfirst);
        //search for the last occurence of an element both trees have in common in depth first iterator of tree 1
        iter = dfiterator1.size()-1;
        do {
            cur = dfiterator1.get(iter);
            if (nodes.contains(cur)) {
                foundlast = true;
                //remember the element and its position
                posl1 = iter;
                ell1 = cur;
            }
            iter--;
        } while (!foundlast);
        //search for the first and last non-cut element from tree 1 in depth first iterator of tree 2
        int posf2 = dfiterator2.indexOf(elf1);
        int posl2 = dfiterator2.indexOf(ell1);
        //if the first appears later than the last, the list has to be rotated
        if (posf2 > posl2) return true;
        else return false;
    }


    public Tree getSCM (Tree one, Tree two, boolean print){
        ArrayList<String> nodesofboth = TreeUtils.getOverLappingNodes(one, two);
        Tree result;
        Tree between;
        NConsensus con = new NConsensus();
        con.getLog().setLevel(Level.OFF);
        if (nodesofboth.size()==0){
            System.err.println("Kein SCM möglich, da kein Overlap");
            return new Tree();
        }
        if (nodesofboth.size() == one.getLeaves().length && nodesofboth.size() == two.getLeaves().length){
            result = con.consesusTree(new Tree[]{one, two}, 1.0);
            //System.err.println("SCM der Bäume ergab Consensus, da gleiches Taxaset.");
        }
        else {
            boolean turn = checkDirectionOfBackbone(nodesofboth, one, two);
            turn = false;
            Tree originalone = one.cloneTree();
            Tree originaltwo = two.cloneTree();
            one = CutTree(one, nodesofboth, turn);
            two = CutTree(two, nodesofboth, turn);
            between = con.consesusTree(new Tree[]{one, two}, 1.0);
            if (print){
                //System.out.println("Cut one "+Newick.getStringFromTree(one));
                //System.out.println("Cut two "+Newick.getStringFromTree(two));
                //System.out.println("Consensus "+Newick.getStringFromTree(between));
                /*double[] printout = FN_FP_RateComputer.calculateSumOfRates(between, new Tree[]{originalone, originaltwo});
                System.out.print("\n"+"Consensus FNFP Rate ");
                for (double a : printout){
                    System.out.print(a+" ");
                }
                System.out.println();*/
            }
            takeCareOfCollisions(between, originalone, originaltwo);
            result = hangInNodes(between);
            setTolerated(new ArrayList<String>());
            //store2pathnnodes.clear();
        }
        return result;

    }

    /*public Tree hangInNodes (Tree input){
        Tree output = input.cloneTree();
        String currlabel;
        List<String> currlist;
        List<TreeNode> finallist = new ArrayList<TreeNode>();
        TreeNode lca = new TreeNode();
        TreeNode newvertex;
        TreeNode betw1;
        TreeNode betw2;


        Set<Map.Entry<String, List<String>>> mapValues = TreeCut1.entrySet();
        int maplength = mapValues.size();
        Map.Entry<String,List<String>>[] TreeCut1entries = new Map.Entry[maplength];
        mapValues.toArray(TreeCut1entries);
        for (int iter=maplength-1; iter>=0; iter--){
            //System.out.println(test[iter].getKey()+" "+test[iter].getValue());
            currlabel = TreeCut1entries[iter].getKey();
            currlist = TreeCut1entries[iter].getValue();
            if (currlist.contains("Polytomy")){
                currlist.remove("Polytomy");
                finallist = TreeUtils.helpgetTreeNodesFromLabels(currlist, output);
                lca = output.findLeastCommonAncestor(finallist);
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
            else {
                for (String x : currlist){
                    finallist.add(output.getVertex(x));
                }

                if (finallist.size()==1){
                    lca = finallist.get(0);
                }
                else {
                    lca = output.findLeastCommonAncestor(finallist);
                }
                if (lca.getParent()!=null){
                    betw1 = lca.getParent();
                    betw2 = new TreeNode();
                    output.removeEdge(betw1, lca);
                    output.addVertex(betw2);
                    output.addEdge(betw1, betw2);
                    output.addEdge(betw2, lca);
                    lca = betw2;
                }
                else {
                    betw1 = new TreeNode();
                    output.addVertex(betw1);
                    output.addEdge(betw1, lca);
                    output.setRoot(betw1);
                    lca = betw1;
                }
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
        }
        TreeCut1.clear();


        mapValues = TreeCut2.entrySet();
        maplength = mapValues.size();
        Map.Entry<String,List<String>>[] TreeCut2entries = new Map.Entry[maplength];
        mapValues.toArray(TreeCut2entries);
        for (int iter=maplength-1; iter>=0; iter--){
            currlabel = TreeCut2entries[iter].getKey();
            currlist = TreeCut2entries[iter].getValue();
            if (currlist.contains("Polytomy")){
                currlist.remove("Polytomy");
                for (String x : currlist){
                    finallist.add(output.getVertex(x));
                }
                lca = output.findLeastCommonAncestor(finallist);
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
            else {
                for (String x : currlist){
                    finallist.add(output.getVertex(x));
                }

                if (finallist.size()==1){
                    lca = finallist.get(0);
                }
                else {
                    lca = output.findLeastCommonAncestor(finallist);
                }
                if (lca.getParent()!=null){
                    betw1 = lca.getParent();
                    betw2 = new TreeNode();
                    output.removeEdge(betw1, lca);
                    output.addVertex(betw2);
                    output.addEdge(betw1, betw2);
                    output.addEdge(betw2, lca);
                    lca = betw2;
                }
                else {
                    betw1 = new TreeNode();
                    output.addVertex(betw1);
                    output.addEdge(betw1, lca);
                    output.setRoot(betw1);
                    lca = betw1;
                }
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
        }
        TreeCut2.clear();

        return output;
    }*/

    public Tree hangInNodes (Tree input){
        Tree output = input.cloneTree();
        Tree consensus = input.cloneTree();
        Set<Map.Entry<String, List<String>>> mapValues = TreeCut1.entrySet();
        int maplength = mapValues.size();
        Map.Entry<String,List<String>>[] TreeCut1entries = new Map.Entry[maplength];
        mapValues.toArray(TreeCut1entries);
        output = helphangInNodes(output, consensus, maplength, TreeCut1entries);
        List<String> tolerated = getTolerated();
        tolerated.addAll(TreeCut1.keySet());
        setTolerated(tolerated);
        TreeCut1.clear();
        //System.out.println("Baum nach hangin 1 "+Newick.getStringFromTree(output));

        mapValues = TreeCut2.entrySet();
        maplength = mapValues.size();
        Map.Entry<String,List<String>>[] TreeCut2entries = new Map.Entry[maplength];
        mapValues.toArray(TreeCut2entries);
        output = helphangInNodes(output, consensus, maplength, TreeCut2entries);
        TreeCut2.clear();

        return output;
    }


    private Tree helphangInNodes(Tree input, Tree consensus, int maplength, Map.Entry<String,List<String>>[] TreeCutentries){
        List<String> tolerated = getTolerated();
        Tree output = input.cloneTree();
        String currlabel;
        List<String> currlist;
        List<TreeNode> finallist = new ArrayList<TreeNode>();
        TreeNode lca = new TreeNode();
        TreeNode newvertex;
        TreeNode betw1;
        TreeNode betw2;
        List<String> chcopy;
        double memoryprint = 0;

        for (int iter=maplength-1; iter>=0; iter--){
            currlabel = TreeCutentries[iter].getKey();
            currlist = TreeCutentries[iter].getValue();
            if (currlist.contains("Polytomy")){
                currlist.remove("Polytomy");
                finallist = TreeUtils.helpgetTreeNodesFromLabels(currlist, output);
                //for (String x : currlist){
                //    finallist.add(output.getVertex(x));
                //}
                lca = output.findLeastCommonAncestor(finallist);
                //TODO muss das ueberhaupt bei Polytomien sein?
                List<String> lcaleaves = TreeUtils.helpgetLabelsFromNodes(Arrays.asList(lca.getLeaves()));
                if (!TreeUtils.StringListsEquals(lcaleaves,currlist)){
                    lcaleaves.removeAll(currlist);
                    if (!tolerated.containsAll(lcaleaves)){
                        //System.out.println("Ah!");
                        List<String> leavesinconsensus = new ArrayList<String>();
                        List<String> consensusleaves = new ArrayList<String>(TreeUtils.helpgetLabelsFromNodes(Arrays.asList(consensus.getLeaves())));
                        for (String i : currlist){
                            if (consensusleaves.contains(i)) leavesinconsensus.add(i);
                        }

                        TreeNode lcainconsensus = consensus.findLeastCommonAncestor(TreeUtils.helpgetTreeNodesFromLabels(leavesinconsensus, consensus));
                        lcaleaves.removeAll(tolerated);
                        List<String> part2expected = new ArrayList<String>();
                        List<String> clcaleavesactual = new ArrayList<String>(TreeUtils.helpgetLabelsFromNodes(Arrays.asList(lcainconsensus.getLeaves())));
                        for (String i : clcaleavesactual){
                            if (lcaleaves.contains(i)){
                                part2expected.add(i);
                            }
                        }
                        List<String> clcaleavesexpected = new ArrayList<String> ();
                        clcaleavesexpected.addAll(leavesinconsensus);
                        clcaleavesexpected.addAll(part2expected);
                        if (!TreeUtils.StringListsEquals(clcaleavesactual,clcaleavesexpected)){
                            System.out.println("Hier klappt was nicht");
                        }
                        //else{
                        //    done = true;
                        //}



                /*        List<TreeNode> neededchildren = new ArrayList<TreeNode>();
                        for (TreeNode ch : lca.getChildren()){
                            if (ch.isLeaf()){
                                if (finallist.contains(ch)){
                                    neededchildren.add(ch);
                                }
                            }
                            else {
                                chcopy = TreeUtils.helpgetLabelsFromNodes(Arrays.asList(ch.getLeaves()));
                                boolean contains = false;
                                for (String el : currlist){
                                    if (chcopy.contains(el)){
                                        contains = true;
                                        break;
                                    }
                                }
                                if (contains){
                                    neededchildren.add(ch);
                                }
                            }
                        }
                        TreeNode stretchnode = new TreeNode("");
                        output.addVertex(stretchnode);
                        for (TreeNode ch: neededchildren){
                            output.removeEdge(lca, ch);
                            output.addEdge(stretchnode, ch);
                        }
                        output.addEdge(lca, stretchnode);
                        lca = output.findLeastCommonAncestor(finallist);
                        if (lca != stretchnode){
                            System.out.println("Da müssen wir nochmal drüber reden..");
                        }*/
                    }
                }
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
            else {
                for (String x : currlist){
                    finallist.add(output.getVertex(x));
                }
                boolean allright = true;
                if (finallist.size()==1){
                    lca = finallist.get(0);
                }
                else {
                    lca = output.findLeastCommonAncestor(finallist);
                    List<String> lcaleaves = TreeUtils.helpgetLabelsFromNodes(Arrays.asList(lca.getLeaves()));
                    if (!TreeUtils.StringListsEquals(lcaleaves,currlist)){
                        lcaleaves.removeAll(currlist);
                        if (!tolerated.containsAll(lcaleaves)) {
                            allright = false;
                        }
                    }

                }
                boolean done = false;
                if (!allright){
                    //System.out.println("Ah!");
                    List<String> lcaleaves = TreeUtils.helpgetLabelsFromNodes(Arrays.asList(lca.getLeaves()));
                    lcaleaves.removeAll(currlist);
                    List<String> leavesinconsensus = new ArrayList<String>();
                    List<String> consensusleaves = new ArrayList<String>(TreeUtils.helpgetLabelsFromNodes(Arrays.asList(consensus.getLeaves())));
                    for (String i : currlist){
                        if (consensusleaves.contains(i)) leavesinconsensus.add(i);
                    }

                    TreeNode lcainconsensus = consensus.findLeastCommonAncestor(TreeUtils.helpgetTreeNodesFromLabels(leavesinconsensus, consensus));
                    lcaleaves.removeAll(tolerated);
                    List<String> part2expected = new ArrayList<String>();
                    List<String> clcaleavesactual = new ArrayList<String>(TreeUtils.helpgetLabelsFromNodes(Arrays.asList(lcainconsensus.getLeaves())));
                    for (String i : clcaleavesactual){
                        if (lcaleaves.contains(i)){
                            part2expected.add(i);
                        }
                    }
                    List<String> clcaleavesexpected = new ArrayList<String> ();
                    clcaleavesexpected.addAll(leavesinconsensus);
                    clcaleavesexpected.addAll(part2expected);
                    if (!TreeUtils.StringListsEquals(clcaleavesactual,clcaleavesexpected)){
                        System.out.println("Hier klappt was nicht");
                    }
                    else{
                        done = true;

                    }
                /*    List<TreeNode> neededchildren = new ArrayList<TreeNode>();
                    for (TreeNode ch : lca.getChildren()){
                        if (ch.isLeaf()){
                            if (finallist.contains(ch)){
                                neededchildren.add(ch);
                            }
                        }
                        else {
                            chcopy = TreeUtils.helpgetLabelsFromNodes(Arrays.asList(ch.getLeaves()));
                            boolean contains = false;
                            for (String el : currlist){
                                if (chcopy.contains(el)){
                                    contains = true;
                                    break;
                                }
                            }
                            if (contains){
                                neededchildren.add(ch);
                            }
                        }
                    }
                    TreeNode stretchnode = new TreeNode("");
                    output.addVertex(stretchnode);
                    for (TreeNode ch: neededchildren){
                        output.removeEdge(lca, ch);
                        output.addEdge(stretchnode, ch);
                    }
                    output.addEdge(lca, stretchnode);
                    lca = output.findLeastCommonAncestor(finallist);
                    if (lca != stretchnode){
                        System.out.println("Da müssen wir nochmal drüber reden..");
                    }*/
                }
                if (!done){
                    if (lca.getParent()!=null){
                        betw1 = lca.getParent();
                        betw2 = new TreeNode("");
                        output.removeEdge(betw1, lca);
                        output.addVertex(betw2);
                        output.addEdge(betw1, betw2);
                        output.addEdge(betw2, lca);
                        lca = betw2;
                    }
                    else {
                        betw1 = new TreeNode("");
                        output.addVertex(betw1);
                        output.addEdge(betw1, lca);
                        output.setRoot(betw1);
                        lca = betw1;
                    }
                }
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
                //tolerated.add(currlabel);
            }
            //if (currlabel.equalsIgnoreCase("t41")){
            //    System.out.println("41 eingefuegt "+Newick.getStringFromTree(output));
            //}
            //if (currlabel.equalsIgnoreCase("t2")){
            //    System.out.println("2 eingefuegt "+Newick.getStringFromTree(output));
            //}
            /*Tree one = Newick.getTreeFromString("((t10:0.08894775476215742,(t26:0.03985428974948799,t73:0.02821633593664641)100:0.0653062423018403)24:0.0017507689662846489,((t89:0.067542697327743,t68:0.08106961282357142)100:0.024137679166091212,(t56:0.09388933727299291,(t82:0.11451216649175316,(((t28:0.10067068632474534,(t6:0.062115972448305176,t53:0.07635689001187648)74:0.0069009548417048785)99:0.020728018848596656,(t58:0.055024742708669507,t75:0.06600512927120432)48:0.009395551583782975)17:1.34682794829244E-6,((t12:0.09742851288532818,(t88:0.029829151430107944,t54:0.05618494156896096)100:0.02136894992853153)99:0.009304169105916357,(((t84:0.010334702349869484,t14:0.008693031409483552)91:0.004684925785176115,t64:0.014192578890272554)100:0.055263204323444275,t69:0.04660129207720499)98:0.0230113921516879)87:0.00881020640032454)17:0.0020877436071069194)13:7.778354585524996E-4)16:0.006783841048411207)24:0.0017507689662846489);");
            Tree two = Newick.getTreeFromString("((t40:0.5642765986294135,((t27:0.04596738031684233,t10:0.04465944458541802)100:0.4935110285523345,t60:0.4620280998200701)85:0.13478395423074496)100:0.23474872431429836,((t58:0.6592918057435115,((t86:0.8168816361372218,(t15:0.45252525840942587,(t35:0.28624420241017584,(t75:0.12895321040121585,(t2:0.11106834046711006,(t95:0.01979516143462739,t20:0.044488496428575795)100:0.04241500044319846)100:0.13239931777943087)88:0.08244039997697482)100:0.25367982508916176)90:0.17778450724681977)95:0.1817789985350968,((t63:0.6236101319446528,t18:0.6719268474144552)100:0.37911686628118785,((t25:0.6577736716889722,(t30:0.4583775454001696,t19:0.42276665221644705)98:0.17329657672200122)99:0.23970620655211639,(t39:0.29827718792250596,(t24:0.0631652168412221,t36:0.09031965533238977)100:0.06989182537035858)100:0.6037301441285537)76:0.079287737318506)55:0.033845347351159766)40:0.030757867528538162)69:0.0677526626915482,(((t13:0.16950395875940258,t56:0.1613765889974485)100:0.6848772326521831,t37:0.9046113535243439)94:0.15318977472845294,((t65:0.03142040964381939,t98:0.060073042912420584)100:0.9658814885188209,(((t78:0.26821783448542424,(t89:0.10837468962143247,t96:0.017168179537107055)100:0.2417786978157017)100:0.6430575475267502,((t3:0.06530358850870746,t5:0.08193313113945493)100:0.5070082511849349,t68:0.38069035060385753)99:0.22537240004162176)100:0.34060701907377705,(((t80:0.29329005370672584,(t67:0.06810271804040097,t62:0.05309582294292591)100:0.2651898398255605)100:0.28580462601527445,(t31:0.5459109984234274,t71:0.44568163507259245)55:0.06316904797911133)100:0.7151053320769016,((t77:0.9593462482282171,((t99:0.8077413721408524,(t28:0.2851129545530663,t9:0.3703556907188251)100:0.7442255596798166)21:1.34842898143532E-6,((((t42:0.05022682389782012,t70:0.056505635675869276)100:0.06136569668686884,t53:0.19309249484117325)100:0.5202948194212101,t21:0.5461385140161974)100:0.44774728355019117,(t4:0.7663294960480718,t49:1.1347067447875487)55:0.07008232772202343)70:0.09550937315792586)56:0.02263377238778113)73:0.18306277757410466,((t79:0.21930509364269132,t82:0.1494072483902726)100:1.0858730021646081,(t87:1.2357743349435864,((((t69:0.5773170285457361,((t11:0.38286176851734943,t47:0.504941992846492)100:0.276413741212672,t61:0.6579610046703622)39:0.08521012399900478)44:0.049324647873264515,((t84:0.056443454073336424,t14:0.06154832969009686)100:0.17847126674136252,t64:0.12867808873957082)100:0.6732398877428349)100:0.3528066547955437,(t94:0.8736682683940032,t45:0.8187108526354588)100:0.5176146827378105)10:1.34842898143532E-6,(((t100:0.25445660258297986,(t85:0.03259945235570368,t66:0.06192521794200038)100:0.2645828324655557)100:0.7167574486833765,(t38:0.880971836112053,(((t93:0.019083893252804376,t57:0.014734095604819628)100:0.8117087583529794,t76:0.5782170346586399)95:0.138442604469865,(t51:0.8163036189862298,(t83:0.327523159744752,(t22:0.3442087061799547,(t41:0.19664909410041703,t17:0.1255134927777769)96:0.01016051852546475)100:0.2332958829940862)100:0.2355383765564445)83:0.14019123833728367)43:1.34842898143532E-6)85:0.08282860227584646)77:0.056824059465323006,(t74:0.7423263761854103,((t72:1.0460150810068918,((t88:0.18826846372441775,t59:0.12757108176959156)100:0.37983853624672637,(t54:0.6813251719851827,(t50:0.4004687419880292,t90:0.2824537814154379)100:0.2629304924027125)79:0.026044688086691588)97:0.12877576102046967)81:0.029741624397714096,(t32:0.7402804442589143,(t52:0.19633597506044315,t81:0.28292736010822134)100:0.38708998152984875)95:0.18546980512890965)57:0.04192872020144604)75:0.10225278886564688)22:0.0336146587995301)21:0.011570799712874016)91:0.11980606658790942)20:0.05501999338186062)13:1.34842898143532E-6)18:0.020796760454031976)14:0.03939710288230341)18:0.01332259376399016)79:0.10459134522251148)100:0.23474872431429836);");
            Tree three = Newick.getTreeFromString("((t40:0.03733907310781477,(t60:0.04974468606980605,(t10:0.004377282688809392,t27:0.0035497194875559247)100:0.04878540538739086)97:0.007080299861728854)100:0.017513671831767996,(((t37:0.08002346188613733,(t13:0.010756785878271051,t56:0.022077666215809356)100:0.04532466970980053)100:0.02258963804646646,(((t62:0.005262262239080322,t67:0.005690183622642661)100:0.02549022989348961,t80:0.02723876020478056)100:0.01859291240061531,(t71:0.02119713779969507,t31:0.04503977226718123)65:9.7089631289078E-7)100:0.056183351644041264)98:0.011537145936535431,(((t18:0.05256334950211299,t63:0.05351733808321262)100:0.031109431652734998,((t39:0.01703707515290678,(t36:0.005830869340027849,t24:0.004260114289259045)100:0.016449078752881895)100:0.054076188122161105,(t25:0.0435312698865738,(t19:0.02823734074787279,t30:0.05711272194641987)100:0.01212114806882504)100:0.02131646711623323)39:0.0020119574694396924)41:0.0016646835919856677,(t58:0.05586463182976597,(t86:0.07072206708419582,(t15:0.053391613987143045,(t35:0.026175856550720494,(((t20:0.0028886593314911015,t95:0.0014345349795915742)98:0.002873489485425033,t2:0.003635725829111734)100:0.008432933045373443,t75:0.006374803170708947)100:0.014546978846091905)100:0.019828672631333047)96:0.013456957536938052)96:0.012898248489683071)52:0.004779336234353997)37:0.002091010637787482)100:0.017513671831767996);");
            Tree four = Newick.getTreeFromString("((t87:0.45546907506485174,(((t94:0.3523030512073961,t45:0.2519746607394135)100:0.16349562367943737,(((((t81:0.1270483392639667,t52:0.08634047434721875)100:0.1523750741329205,t12:0.3587510950074339)36:0.006292889487145146,t32:0.2684754605220908)100:0.08759186457103565,(t72:0.3813833926838195,(t54:0.26213063354322685,((t59:0.06428013935544478,t88:0.07077038188966295)100:0.15895371078922876,(t90:0.11364523482627091,t50:0.15809623224616126)100:0.09276503992690989)41:1.35250398208499E-6)99:0.08011019561936077)42:0.014232718513404427)8:1.35250398208499E-6,(t74:0.2885904983681343,t16:0.4120626763152228)31:0.01863293156581301)86:0.030149711618985626)65:0.012080127389208243,((((t64:0.06108998878668425,(t14:0.03294737680531124,t84:0.03691237312020028)100:0.057014598573443126)100:0.25829525121351643,((t47:0.1803023815258389,t11:0.16830954119802458)100:0.13154862542619744,t61:0.2894709102747981)24:0.0013764345034373376)40:0.012896975456119746,t69:0.22249602652057102)100:0.12992345178926276,(((t38:0.35909799690340355,(t51:0.3179604781122556,(t83:0.1380141917410429,(t22:0.12355689889676288,(t41:0.08583766341198493,t17:0.06664142520262081)86:0.02833841566583851)100:0.10272393810141218)100:0.12200110220113695)86:0.04073038614147135)39:1.35250398208499E-6,((t57:0.0063988102546793674,t93:0.008467643712963048)100:0.34818209793075333,t76:0.2540023867059081)93:0.04902210461699647)86:0.022906994215120775,(t100:0.12597233888082485,(t66:0.023131889229564682,t85:0.02451002691246267)100:0.09518615433333819)100:0.25266545008107266)86:0.02482916122837473)31:9.495919573138993E-4)52:0.02874076059964088)100:0.08776357313643807,((t78:0.1149947581525899,(t96:0.010476181891934668,t89:0.04579279396428263)100:0.078721172151805)100:0.2207881671287491,(t68:0.17538774407844374,(t3:0.03196360555206687,t5:0.03940428515221167)100:0.220638559098164)100:0.11759672414717665)100:0.08776357313643807);");
            Tree five = Newick.getTreeFromString("(((t60:0.0490426627088724,(t27:0.0034680070109926026,t10:0.004290705199215468)100:0.048134290645483406)97:0.006699541713968005,t40:0.036468096659774435)100:0.01573349150938158,(((t31:0.0433026952919029,t71:0.021676079194882156)87:0.0017534262034075813,(t80:0.027385505143122754,(t62:0.005154965794287388,t67:0.0055841220310420435)100:0.024083518897501793)100:0.01677958302959055)100:0.06416310289457224,(((t63:0.05179676198078157,t18:0.05192025882736162)100:0.030225689857041008,((t39:0.01686137928927442,(t36:0.005705557057087696,t24:0.004166740235989693)100:0.01597514618325249)100:0.05322601247124448,(t25:0.043035486309823054,(t19:0.02791934881481156,t30:0.055998266821126574)100:0.01175769068808138)100:0.020969051401044846)33:0.002732077888561074)41:0.002153423307250629,((t86:0.06943119082207976,(t15:0.052074476142482624,(t35:0.02573308267563979,(t75:0.006222154278898244,((t20:0.002829805509374061,t95:0.0014028302470604273)100:0.0028152545857782086,t2:0.0035584904655703836)100:0.008290259045934658)100:0.014239809800308152)100:0.019537886347742426)99:0.01364951569503425)82:0.011141002767653918,t58:0.05560765529907733)48:0.004963697331687693)42:0.0038259635623478796)100:0.01573349150938158);");
            Tree six = Newick.getTreeFromString("((t94:0.060740485986834014,t45:0.040964775051944416)100:0.025722597113558857,(((t16:0.08061691577252479,t74:0.05673225570001672)49:0.0035897107483803554,(t32:0.04172797576787578,(t12:0.07631292987347321,(t81:0.027318522758643427,t52:0.01409328038242558)100:0.030676926791958367)76:0.0029844206266685007)100:0.016297728990450566)45:0.0016812980534722534,(t72:0.08332509396999005,((t90:0.026779436843129147,t50:0.03347899421676307)100:0.018920071564419348,(t54:0.05137852215496093,(t88:0.007198076423519194,t59:0.012374249699929228)100:0.02782433064160607)53:0.0012602158975253438)100:0.017224578778602263)90:0.006068636198073989)99:0.008436136595551475,((t38:0.08820269624369481,(((t57:1.45133972851916E-6,t93:1.45133972851916E-6)100:0.08403495719189008,t76:0.056778292903422574)71:0.009115631346830641,(t51:0.06555033746812054,(t83:0.03163642144815057,((t41:0.011328286922557164,t17:0.016035498857625688)72:0.0054313597149528384,t22:0.020094966773746444)100:0.02037340041258512)100:0.026422623606083478)82:0.008812314374267985)33:4.265441555331521E-4)80:0.003096857792858868,(t100:0.03075201580336611,(t66:0.005083665573264914,t85:0.0044584945037720765)100:0.005292630030146493)100:0.005292630030146493)99:0.005292630030146493);");


            double[] print = FN_FP_RateComputer.calculateSumOfRates(output, new Tree[]{one, two, three, four});
            if (print[3] > 0){
                if (print[3]>memoryprint){
                    System.out.println("Fp beim Einhängen von "+currlabel);
                }
                memoryprint = print[3];
            }*/
        }
        return output;
    }



    public Tree CutTree (Tree input, List<String> keep, boolean turn){
        Tree output = input.cloneTree();
        //makes a list that contains all leaves in the input tree
        List<String> originalleaves = new ArrayList<String>(TreeUtils.helpgetLabelsFromNodes(Arrays.asList(output.getLeaves())));
        if (keep.isEmpty()) return new Tree();

        List<String> depthFirst = TreeUtils.generateCustomDepthFirstListChildren(output);
        List<TreeNode> tokeep = new ArrayList<TreeNode>(TreeUtils.helpgetTreeNodesFromLabels(keep, output));
        List<TreeNode> cut = new ArrayList<TreeNode>(TreeUtils.helpgetTreeNodesFromLabels(depthFirst, output));
        if (turn){
            Collections.reverse(cut);
        }

        for (TreeNode iter : tokeep){
            cut.remove(iter);
        }
        boolean firstpass = true;
        int savewhere;
        if (TreeCut1.isEmpty()) savewhere = 1;
        else savewhere = 2;

        if (cut.size()==0) return output;

        List<TreeNode> between;
        List<TreeNode> between2;
        TreeNode par;
        String label;
        List<String> finallist;

        while (cut.size()!=0){
            for (TreeNode iter : cut){
                if (firstpass){
                    label = "";
                    par = iter.getParent();
                    finallist = new ArrayList<String>();
                    //TODO!
                    if (par == null){
                        System.out.println("Das muss noch weg");
                    }
                    between = new ArrayList<TreeNode>(Arrays.asList(par.getLeaves()));
                    between2 = new ArrayList<TreeNode>(between);
                    for (TreeNode el : between2){
                        if (!originalleaves.contains(el.getLabel())) between.remove(el);
                    }
                    while (between.size() <= 1 && between.get(0).getParent()!=null){
                        par = par.getParent();
                        between = new ArrayList<TreeNode>(Arrays.asList(par.getLeaves()));
                        between2 = new ArrayList<TreeNode>(between);
                        for (TreeNode el : between2){
                            if (!originalleaves.contains(el.getLabel())) between.remove(el);
                        }
                    }
                    between.remove(iter);
                    if (between.size()==0){
                        System.err.println("Fehler bei CutTree");
                    }
                    if (between.size()>1 && par.getChildren().size()>2){
                        int childcounter = 0;
                        List<String> chleaves = new ArrayList<String>();
                        for (TreeNode i : par.getChildren()){
                            chleaves.addAll(TreeUtils.helpgetLabelsFromNodes(Arrays.asList(i.getLeaves())));
                            for (String s : chleaves){
                                if (originalleaves.contains(s)){
                                    childcounter ++;
                                    break;
                                }
                            }
                            chleaves.clear();
                        }
                        if (childcounter > 2){
                            finallist.add("Polytomy");
                        }
                    }
                    for (TreeNode x : between){
                        finallist.add(x.getLabel());
                    }
                    if (savewhere == 1){
                        TreeCut1.put(label.concat(iter.getLabel()), finallist);
                    }
                    else {
                        TreeCut2.put(label.concat(iter.getLabel()), finallist);
                    }
                }
                output.removeVertex(iter);
            }
            firstpass = false;
            cut = new ArrayList<TreeNode>(Arrays.asList(output.getLeaves()));
            for (TreeNode iter : tokeep){
                cut.remove(iter);
            }
        }
        for (TreeNode x : output.vertices()){
            if (x.degree() == 2 && x!=output.getRoot()){
                for (TreeNode y : x.children()){
                    output.addEdge(x.getParent(),y);
                }
                output.removeVertex(x);

            }
        }
        TreeNode newroot = output.findLeastCommonAncestor(tokeep);
        if (!output.getRoot().equalsNode(newroot)){
            Tree finaltree = output.getSubtree(newroot);
            return finaltree;

        }
        return output;
    }



    public static void main (String args[]){
        List<String> bla = Arrays.asList(new String[]{"eins", "zwei", "drei", "vier", "fuenf"});
        List<String> bli = Arrays.asList(new String[]{"sechs", "sieben", "drei", "vier", "fuenf", "eins", "zwei"});
        bli.removeAll(bla);
        for (String x : bli){
            System.out.print(x+" ");
        }
        System.out.println(bli.toArray());

        /*GreedySCM hey = new GreedySCM();
        Tree b = Newick.getTreeFromString("((((a:1.0,b:1.0):1.0,c:1.0):1.0,(d:1.0,e:1.0):1.0):1.0,f:1.0)");
        List<String> tryout = hey.generateDepthFirstListChildren(b);
        for (String x : tryout){
            System.out.println(x);
        }*/

        /*LinkedHashMap<String, List<String>> list = new LinkedHashMap<String, List<String>>();
        ArrayList<String> abc = new ArrayList<String>();
        abc.add("a");
        abc.add("b");
        abc.add("c");
        ArrayList<String> ab = new ArrayList<String>();
        ArrayList<String> a = new ArrayList<String>();
        ab.add("a");
        ab.add("b");
        a.add("a");
        list.put("1", abc);
        list.put("2", a);
        list.put("3", abc);
        list.put("4", ab);
        list.put("5", ab);
        for (String str : list.keySet()){
            System.out.println(str + " "+ list.get(str));
        }

        for( Map.Entry<String,String> entry : list.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(key + " " + value);
        }

        Set<Map.Entry<String, List<String>>> mapValues = list.entrySet();
        int maplength = mapValues.size();
        Collection<List<String>> values = list.values();
        System.out.println(values.size());
        values.toArray();
        for (List<String> x : values){
            System.out.println(x);
        }*/
        //Map.Entry<String,String>[] test = new Map.Entry[maplength];
        //mapValues.toArray(test);
        //for (int iter=maplength-1; iter>=0; iter--){
        //    System.out.println(test[iter].getKey()+" "+test[iter].getValue());
        //}*/


        SCM hey = new SCM();



        //hey.testSCM();

        //Tree test1 = Newick.getTreeFromString("(((a:1.0,b:1.0):1.0,(c:1.0,d:1.0):1.0):1.0,(e:1.0,f:1.0):1.0)");
        //Tree test2 = Newick.getTreeFromString("(((a:1.0,b:1.0):1.0,c:1.0,d:1.0):1.0,(e:1.0,f:1.0):1.0)");

        /*double resolution = (test1.vertexCount()-1-test1.getLeaves().length) / (test1.getLeaves().length-2);
        System.out.println("Auflösung von test1 ist "+resolution);
        resolution = TreeUtilsBasic.calculateTreeResolution(test1.getNumTaxa(), test1.vertexCount());
        System.out.println("Auflösung von test1 ist "+resolution);
        int one = test2.vertexCount()-1-test2.getNumTaxa();
        int two = test2.getNumTaxa()-2;
        int three = 3/4;
        resolution = (test2.vertexCount()-1-test2.getNumTaxa()) / (test2.getNumTaxa()-2);
        System.out.println("Auflösung von test2 ist "+resolution);
        resolution = (test2.vertexCount()-1-test2.getLeaves().length) / (test2.getLeaves().length-2);
        System.out.println("Auflösung von test2 ist "+resolution);
        resolution = TreeUtilsBasic.calculateTreeResolution(test2.getNumTaxa(), test2.vertexCount());
        System.out.println("Auflösung von test2 ist "+resolution);*/

        //Tree a = Newick.getTreeFromString("((t169:0.02056467440428987,t108:0.02563947678634368)100:0.06741310372226965,(((t495:0.10917012927375173,((t195:0.10340996876048182,t165:0.11249025586572521)40:1.28060618246117E-6,(t131:0.07252526964244901,((t4:0.06020329392131871,t312:0.038910241371599416)87:0.008360685022321818,t321:0.06830399985073354)99:0.01817592679073262)100:0.033484662747521245)88:0.012555393545572216)100:0.022777591692494933,t450:0.15388230193294133)100:0.02961663027317288,((t371:0.1389421237592064,(t92:0.1104898929326545,(t262:0.06916278290604252,t189:0.07459192428798872)100:0.03799810581845313)74:0.010270676287150152)100:0.03459439554786425,((t53:0.14110769660303102,(t181:0.11864673721728795,(t444:0.0671338216135925,(t261:0.0064592410178400585,t188:0.0041711215026039835)100:0.045561424248944334)100:0.10528041235376885)22:1.28060618246117E-6)100:0.03138328605910244,((t184:0.07901238266767743,(t414:0.08025856413488873,t144:0.09485297213022711)82:0.022352501216084276)100:0.07144367623540092,(((t47:0.056946757852635504,(t347:0.051978139057718226,(t481:0.04487642130752454,t235:0.05146445129010096)98:0.014032739588420958)98:0.014494878675988742)100:0.08167640798578357,((t290:0.09380134252475787,t202:0.048200537341168005)100:0.06780361910503069,(t421:0.1864277665012198,(t38:0.07738252450442691,(t341:0.09682860678743666,(t17:0.05137261558852251,t413:0.03917676869216877)100:0.07572430436185493)96:0.015898650714822025)100:0.04607997971513339)72:0.001213390885099741)96:0.01440099617743572)98:0.02337555299190737,(((((t225:0.006079313520494399,t103:0.008628640410399334)100:0.154833547526221,((t340:0.1953872336483595,(((t356:0.1262352768726709,t146:0.12662551532687943)98:0.044577805358935595,t483:0.14897544322315748)94:0.010395074741298025,((t161:0.1093188866266506,t221:0.08462512319398661)100:0.055228146719204174,(t26:0.07836240081104719,t349:0.07879123175734319)100:0.05695631440031426)97:0.01767013450317087)98:0.012403251964888425)100:0.013753862469104433,(((t156:0.05693835336392994,(t392:0.017564930888416885,(t231:0.00699556786714038,t296:0.003804812892347839)100:0.01330816082971064)100:0.02565141211010015)100:0.07626977834655616,(t440:0.03035186316395571,(t425:0.01785688347618177,(t79:1.28060618246117E-6,t252:1.28060618246117E-6)100:0.009681268247012622)100:0.02158456240997149)100:0.09821598003253676)100:0.05676905338552688,(((((t186:0.11495119124464204,t407:0.1363001988749887)83:0.010639182448428305,t14:0.13329635082098512)99:0.023044511958417092,t384:0.15166839397357673)100:0.020633790051151128,(((t279:0.08731762674954793,t96:0.0792598555900757)100:0.04944195696395667,(t368:0.1251460829029792,((((t378:0.022165305791404008,t315:0.029860920562994334)79:0.0066808351074927885,(t381:0.04021282019037454,t376:0.034465952754769796)30:0.0015123688117842761)100:0.04765112417502503,((t398:0.04681819603171684,t138:0.046006940287527734)39:0.0034283827064993,t34:0.04665607553569118)100:0.0355865494168686)100:0.030375845471925386,((t224:0.024170594849546218,t247:0.02016989692954509)100:0.0731182445972318,t18:0.07005229651658874)98:0.01594451607962281)98:0.010981939864435374)100:0.0272707950361778)100:0.021578419380139362,(t460:0.15057955272098647,(t269:0.08662750293115233,t136:0.07907697770944722)100:0.07255907188506434)68:0.004267348273175963)94:0.01227417876092108)60:0.0010631874147168621,(((t193:0.11505204903157051,(t267:0.12034264613322426,t302:0.13471494438483753)56:0.005794552889002644)97:0.016097234495596583,(t428:0.07352944842925313,(t228:0.03999016331613156,t37:0.03456088479610629)100:0.030977018385112177)100:0.06132770131142908)95:0.011245806528194643,((t187:0.11607085005577038,t180:0.08705894592470814)100:0.023783168035472528,((t452:0.028186089194060945,t395:0.026722208476295285)100:0.07552406732622392,t248:0.13356369306812604)99:0.023335799574215034)100:0.030969981322034917)82:0.005541446723073026)87:0.006962607165375047)80:0.0026723596896034356)100:0.011530490816767526)30:0.003596166440894441,(((t170:0.18139284735075084,((t191:0.12645728264204895,t462:0.0988045073549861)99:0.04041844494472375,((((t328:0.06372607114752471,t245:0.07907983855139955)99:0.03465159146308438,t3:0.1375945063734816)29:0.004297750628000487,(t369:0.1325504178166916,(t408:0.0714687051026849,t90:0.07968994192553958)100:0.03417270648204351)100:0.037517704704581295)88:0.014066514148964324,t49:0.1330166055152802)43:0.0044448638031386765)92:0.014767617445623856)44:0.009182903818733861,(((((t227:0.08205837435802442,(t200:0.05121219110636751,t303:0.07546493751308447)99:0.022650245772673973)100:0.07235110626290213,(t117:0.09244019804230474,(t401:0.07097925346726283,t137:0.06225843077175492)100:0.05777769244971311)98:0.022054134965792605)71:0.005851372077016756,(((t276:0.043126005218409834,(t222:0.009067033328298413,t362:0.011514153398918979)100:0.04084202671388816)100:0.05057720972285346,(t270:0.03876101139619697,(t380:0.003980966850629527,t390:0.0017858735142845048)100:0.0277014109914246)100:0.08047783772528755)100:0.05050445344424672,t327:0.11666750321285278)46:0.005585447068656795)53:0.004552864949622901,(t406:0.15021547063390955,(t500:0.03150488929538157,t367:0.017283020603765655)100:0.15541308435636889)35:0.012595479115761846)92:0.01615101409934024,((t223:0.13774925763357904,(t467:0.07492314708577083,t326:0.0639541841389407)100:0.05020278846663257)96:0.028996429932755537,t461:0.150342270299734)89:0.007427905043916359)100:0.03456257083898455)10:1.28060618246117E-6,(((t419:0.12630518623726733,(t490:0.0425279752398842,t81:0.032707038752805215)100:0.08293209737575868)97:0.03268151176585493,(t466:0.1276641988332817,(t399:0.13906554866472629,t151:0.14774998424640592)41:1.28060618246117E-6)100:0.03335503299234976)94:0.014702170122309361,((((t268:0.07081231268621989,(t29:0.05314239800413978,t281:0.0378578942268506)100:0.010971664115323243)100:0.07373254172377845,((t83:0.0947179463694813,t113:0.06973493760494509)100:0.06571402833079533,((t101:0.11088184214596145,((t265:0.09281739031849372,(t336:0.061506203315363946,t354:0.07210017487481564)99:0.029238095321665117)98:0.025762800116516854,t118:0.1014108732141534)87:0.014733752875598086)53:0.002598101346617261,t12:0.11772707761399336)100:0.0323683046613994)62:0.002191362089417505)100:0.019563835970112152,(t218:0.12374986683485793,(t58:0.11188623244772537,(t106:0.07217544088033027,t66:0.058766603855361914)97:0.020793085712610522)93:0.020136491923846662)100:0.0464878795764104)34:1.28060618246117E-6,(((((t121:0.12924788308414653,t93:0.15741892500820695)48:0.014209198457489983,((t474:0.041567922183912025,(t443:0.02734134286427507,(t438:0.01194149372227841,t299:0.006588604579050394)100:0.03066520418672206)41:0.0014785114631333037)100:0.09044274864750067,((t62:0.02759939919459967,t119:0.03474186855473828)80:1.28060618246117E-6,t64:0.04133201473118848)100:0.0978040610590435)100:0.041438421249138366)13:1.28060618246117E-6,t204:0.12200047921642883)55:0.0045724471639626875,(t288:0.1084120167506601,(t496:0.018079855489872124,t45:0.016222585899287073)100:0.08583826362153472)100:0.04679808117271949)45:0.0059589899835981374,((t464:0.12094100431651676,t67:0.10789050286291058)100:0.07991960516598831,(t124:0.07927095559501597,(t488:0.012723461585328824,t190:0.020826342144274763)100:0.06987076099305793)100:0.06284623802428504)19:1.28060618246117E-6)47:0.006729742226452831)79:0.014540405825929236)20:1.28060618246117E-6)26:0.0010383543954154085)10:1.28060618246117E-6,(t387:0.20538009218891223,((t182:0.10750773749970059,(t348:0.07271193853990027,(t198:0.06173016953396456,t431:0.04313362132159189)91:0.012908774316778362)100:0.024326757924139977)100:0.06603975551178011,((t346:0.07901530468419846,t24:0.13008253203494713)86:0.013901492546563685,t427:0.11177236945076131)100:0.04469989998477104)67:0.005127223770719831)92:0.015676675244401247)20:9.819960638786517E-4,((((t491:0.004057606692687111,t291:0.005742348191867697)100:0.1806042929454614,t497:0.2037315064451476)26:1.28060618246117E-6,(((t201:0.06754884943478977,t23:0.07858095462910024)100:0.06481873580614268,((t454:0.05054594094182899,t373:0.03784461690682863)100:0.09931538450474162,(((t179:0.08974545354222763,(t50:0.05910134374913718,(t423:0.009744594974440367,t389:1.28060618246117E-6)100:0.05014941655881632)100:0.046809609580083886)88:0.010118310247607436,(t84:0.05966438136457367,t420:0.05177841475604175)100:0.05436377093702334)84:0.006534449373480624,(t343:0.09001051086099993,t55:0.11119858867753188)88:0.01753588443638785)100:0.0314713909919055)94:0.00751249734848586)99:0.01839491344455796,(((t42:0.15337280855607727,t148:0.12722870273522469)86:0.009016327931657386,(((t256:0.09285029043063348,t183:0.07465982516101954)99:0.01990165479300553,t404:0.1197365819711296)70:0.0032061546513792675,((t274:0.006002912980422437,t36:0.011908877280136572)100:0.09285931717084228,(t259:0.058916801034139134,t44:0.05154962566903489)100:0.05722433334460059)69:0.007584791552409706)100:0.02899418744823094)100:0.024746638897693442,((t432:0.14104820660010747,((t48:0.08538504026682084,(t128:0.09161574533532613,(t143:0.0669272240471088,t309:0.06768246825124911)90:0.007612315227007921)100:0.04094044435166495)100:0.02182864670849406,(t499:0.12330326240925596,t22:0.1343574145058776)71:0.0016018876381818608)100:0.02296722933545503)100:0.021228651301429313,((t375:0.08707345632559439,((t403:0.0746048794502126,t139:0.0900721509934046)76:1.28060618246117E-6,((t229:0.018900720380834794,t88:0.01898603458577885)100:0.03168180697299456,t147:0.054028080272321914)100:0.03579908920016309)98:0.017671839004118048)100:0.04696848733192598,(t25:0.10413680555977896,(t171:0.060605593795599565,t174:0.07100976449969716)100:0.0360396489468767)100:0.03150014553669344)100:0.02302293124654936)100:0.01317639641055689)100:0.008535576038117845)74:0.001614968699715294)87:0.01095415641889886,((((t8:0.11598922228238877,((t411:0.03285691675243413,t263:0.0721851818170049)100:0.08939924099637697,t40:0.09792413001391818)81:0.011542560106631428)98:0.02161665278970565,((t155:0.07214886766246043,(t159:0.10041757737781204,t391:0.07184333387561968)1 8:0.0018813766339229303)51:0.0065035594315923356,t243:0.12039069066540481)100:0.03482018392251097)83:0.0053643758329177775,(((t360:0.10137098292243726,((t434:0.03908774950820112,t441:0.037457497470598164)100:0.09444134063157548,(t104:0.05378227422548194,(t453:0.051480242869899634,t337:0.03757857164379937)100:0.011829227901388807)100:0.08383084336080066)49:1.28060618246117E-6)100:0.03255679045732144,(t220:0.1274025581866953,((t366:0.09296725273237437,t210:0.08771438404139376)100:0.03679518869045666,t355:0.13203925959832258)65:0.0061965187400838916)94:0.0083008015081342)96:0.011323366875271359,(t426:0.09213943454916147,(t197:0.08561585098374117,t319:0.05696590785020593)90:1.28060618246117E-6)100:0.0780582882030906)76:0.005249149022060012)100:0.030256378332758276,(((t482:0.12345564313438066,(t329:0.091628458013025,t339:0.15007705248853567)86:0.016910723487421068)78:0.009652274533722175,((t194:0.1287896085573261,(t95:0.10332302961004684,(t433:0.07847880709080887,t257:0.03937381788978546)100:0.05762504147869876)91:0.013317630031816095)41:1.28060618246117E-6,t393:0.14374224381631429)94:0.011795111115484059)100:0.0265886198780697,((t98:0.009164086209246698,t485:0.00446672248485915)100:0.14945007105963737,((t470:0.1285659328361837,(t365:0.09915869660120957,(t277:0.05191713799030193,t436:0.04661760908780692)100:0.08381852382419772)92:0.017398827571112028)99:0.01867680026522861,(((t287:0.033762859856260326,t451:0.029172486323449282)100:0.029011418512700025,(t294:0.0027206339031963433,t325:0.001084117903976444)100:0.11188303299813171)100:0.03378704967060549,t13:0.14871987688563515)99:0.02020468791066357)68:0.0023735160255263336)100:0.023938052497163652)99:0.01434631237193819)62:0.00751774574616681)19:1.28060618246117E-6)75:0.0037749039628434825)99:0.01097836922577562)94:0.008739769423865673)83:0.007499766326436678)35:1.28060618246117E-6)100:0.06741310372226965);");
        //Tree b = Newick.getTreeFromString("((((t170:1.0,t469:1.0):1.0,((t49:1.0,(((t328:1.0,(t145:1.0,(t245:1.0,t463:1.0):1.0):1.0):1.0,((t10:1.0,t3:1.0):1.0,t478:1.0,t353:1.0):1.0):1.0,(t369:1.0,(t408:1.0,t90:1.0):1.0):1.0):1.0):1.0,(((t462:1.0,t65:1.0):1.0,t383:1.0):1.0,t480:1.0):1.0):1.0):1.0,((((t335:1.0,((t119:1.0,t62:1.0):1.0,t64:1.0):1.0):1.0,(((t443:1.0,t135:1.0):1.0,((t474:1.0,t71:1.0):1.0,t157:1.0):1.0):1.0,(t226:1.0,(t438:1.0,t299:1.0):1.0):1.0):1.0):1.0,t308:1.0,(t212:1.0,(t93:1.0,(t59:1.0,t253:1.0):1.0):1.0,(t97:1.0,t121:1.0):1.0):1.0,t204:1.0,(((t344:1.0,(t496:1.0,t45:1.0):1.0):1.0,((t206:1.0,t288:1.0):1.0,t310:1.0):1.0):1.0,(t293:1.0,((t89:1.0,t289:1.0):1.0,t120:1.0):1.0):1.0):1.0):1.0,(((t268:1.0,t422:1.0,((t456:1.0,(t7:1.0,(t281:1.0,t72:1.0):1.0):1.0):1.0,t29:1.0):1.0):1.0,((t439:1.0,((t113:1.0,((t83:1.0,t127:1.0):1.0,t260:1.0):1.0):1.0,t19:1.0):1.0):1.0,((t292:1.0,t118:1.0,((t354:1.0,t336:1.0):1.0,(t242:1.0,t265:1.0):1.0):1.0):1.0,(((t46:1.0,t101:1.0):1.0,t492:1.0):1.0,(t12:1.0,t150:1.0):1.0):1.0):1.0):1.0):1.0,(((((t133:1.0,t58:1.0):1.0,(t192:1.0,t305:1.0):1.0):1.0,(((t106:1.0,t284:1.0):1.0,t285:1.0):1.0,t66:1.0):1.0):1.0,((t218:1.0,(t31:1.0,t475:1.0):1.0):1.0,t217:1.0):1.0):1.0,t435:1.0):1.0):1.0,(t190:1.0,(t488:1.0,t358:1.0):1.0):1.0):1.0):1.0,(((t396:1.0,t464:1.0):1.0,t2:1.0):1.0,(((t67:1.0,t168:1.0):1.0,t85:1.0):1.0,t278:1.0):1.0):1.0);");
        //Tree c = hey.SCM(a, b);
        //Tree a = Newick.getTreeFromString("((t169:0.02056467440428987,t108:0.02563947678634368)100:0.06741310372226965,(((t495:0.10917012927375173,((t195:0.10340996876048182,t165:0.11249025586572521)40:1.28060618246117E-6,(t131:0.07252526964244901,((t4:0.06020329392131871,t312:0.038910241371599416)87:0.008360685022321818,t321:0.06830399985073354)99:0.01817592679073262)100:0.033484662747521245)88:0.012555393545572216)100:0.022777591692494933,t450:0.15388230193294133)100:0.02961663027317288,((t371:0.1389421237592064,(t92:0.1104898929326545,(t262:0.06916278290604252,t189:0.07459192428798872)100:0.03799810581845313)74:0.010270676287150152)100:0.03459439554786425,((t53:0.14110769660303102,(t181:0.11864673721728795,(t444:0.0671338216135925,(t261:0.0064592410178400585,t188:0.0041711215026039835)100:0.045561424248944334)100:0.10528041235376885)22:1.28060618246117E-6)100:0.03138328605910244,((t184:0.07901238266767743,(t414:0.08025856413488873,t144:0.09485297213022711)82:0.022352501216084276)100:0.07144367623540092,(((t47:0.056946757852635504,(t347:0.051978139057718226,(t481:0.04487642130752454,t235:0.05146445129010096)98:0.014032739588420958)98:0.014494878675988742)100:0.08167640798578357,((t290:0.09380134252475787,t202:0.048200537341168005)100:0.06780361910503069,(t421:0.1864277665012198,(t38:0.07738252450442691,(t341:0.09682860678743666,(t17:0.05137261558852251,t413:0.03917676869216877)100:0.07572430436185493)96:0.015898650714822025)100:0.04607997971513339)72:0.001213390885099741)96:0.01440099617743572)98:0.02337555299190737,(((((t225:0.006079313520494399,t103:0.008628640410399334)100:0.154833547526221,((t340:0.1953872336483595,(((t356:0.1262352768726709,t146:0.12662551532687943)98:0.044577805358935595,t483:0.14897544322315748)94:0.010395074741298025,((t161:0.1093188866266506,t221:0.08462512319398661)100:0.055228146719204174,(t26:0.07836240081104719,t349:0.07879123175734319)100:0.05695631440031426)97:0.01767013450317087)98:0.012403251964888425)100:0.013753862469104433,(((t156:0.05693835336392994,(t392:0.017564930888416885,(t231:0.00699556786714038,t296:0.003804812892347839)100:0.01330816082971064)100:0.02565141211010015)100:0.07626977834655616,(t440:0.03035186316395571,(t425:0.01785688347618177,(t79:1.28060618246117E-6,t252:1.28060618246117E-6)100:0.009681268247012622)100:0.02158456240997149)100:0.09821598003253676)100:0.05676905338552688,(((((t186:0.11495119124464204,t407:0.1363001988749887)83:0.010639182448428305,t14:0.13329635082098512)99:0.023044511958417092,t384:0.15166839397357673)100:0.020633790051151128,(((t279:0.08731762674954793,t96:0.0792598555900757)100:0.04944195696395667,(t368:0.1251460829029792,((((t378:0.022165305791404008,t315:0.029860920562994334)79:0.0066808351074927885,(t381:0.04021282019037454,t376:0.034465952754769796)30:0.0015123688117842761)100:0.04765112417502503,((t398:0.04681819603171684,t138:0.046006940287527734)39:0.0034283827064993,t34:0.04665607553569118)100:0.0355865494168686)100:0.030375845471925386,((t224:0.024170594849546218,t247:0.02016989692954509)100:0.0731182445972318,t18:0.07005229651658874)98:0.01594451607962281)98:0.010981939864435374)100:0.0272707950361778)100:0.021578419380139362,(t460:0.15057955272098647,(t269:0.08662750293115233,t136:0.07907697770944722)100:0.07255907188506434)68:0.004267348273175963)94:0.01227417876092108)60:0.0010631874147168621,(((t193:0.11505204903157051,(t267:0.12034264613322426,t302:0.13471494438483753)56:0.005794552889002644)97:0.016097234495596583,(t428:0.07352944842925313,(t228:0.03999016331613156,t37:0.03456088479610629)100:0.030977018385112177)100:0.06132770131142908)95:0.011245806528194643,((t187:0.11607085005577038,t180:0.08705894592470814)100:0.023783168035472528,((t452:0.028186089194060945,t395:0.026722208476295285)100:0.07552406732622392,t248:0.13356369306812604)99:0.023335799574215034)100:0.030969981322034917)82:0.005541446723073026)87:0.006962607165375047)80:0.0026723596896034356)100:0.011530490816767526)30:0.003596166440894441,(((t170:0.18139284735075084,((t191:0.12645728264204895,t462:0.0988045073549861)99:0.04041844494472375,((((t328:0.06372607114752471,t245:0.07907983855139955)99:0.03465159146308438,t3:0.1375945063734816)29:0.004297750628000487,(t369:0.1325504178166916,(t408:0.0714687051026849,t90:0.07968994192553958)100:0.03417270648204351)100:0.037517704704581295)88:0.014066514148964324,t49:0.1330166055152802)43:0.0044448638031386765)92:0.014767617445623856)44:0.009182903818733861,(((((t227:0.08205837435802442,(t200:0.05121219110636751,t303:0.07546493751308447)99:0.022650245772673973)100:0.07235110626290213,(t117:0.09244019804230474,(t401:0.07097925346726283,t137:0.06225843077175492)100:0.05777769244971311)98:0.022054134965792605)71:0.005851372077016756,(((t276:0.043126005218409834,(t222:0.009067033328298413,t362:0.011514153398918979)100:0.04084202671388816)100:0.05057720972285346,(t270:0.03876101139619697,(t380:0.003980966850629527,t390:0.0017858735142845048)100:0.0277014109914246)100:0.08047783772528755)100:0.05050445344424672,t327:0.11666750321285278)46:0.005585447068656795)53:0.004552864949622901,(t406:0.15021547063390955,(t500:0.03150488929538157,t367:0.017283020603765655)100:0.15541308435636889)35:0.012595479115761846)92:0.01615101409934024,((t223:0.13774925763357904,(t467:0.07492314708577083,t326:0.0639541841389407)100:0.05020278846663257)96:0.028996429932755537,t461:0.150342270299734)89:0.007427905043916359)100:0.03456257083898455)10:1.28060618246117E-6,(((t419:0.12630518623726733,(t490:0.0425279752398842,t81:0.032707038752805215)100:0.08293209737575868)97:0.03268151176585493,(t466:0.1276641988332817,(t399:0.13906554866472629,t151:0.14774998424640592)41:1.28060618246117E-6)100:0.03335503299234976)94:0.014702170122309361,((((t268:0.07081231268621989,(t29:0.05314239800413978,t281:0.0378578942268506)100:0.010971664115323243)100:0.07373254172377845,((t83:0.0947179463694813,t113:0.06973493760494509)100:0.06571402833079533,((t101:0.11088184214596145,((t265:0.09281739031849372,(t336:0.061506203315363946,t354:0.07210017487481564)99:0.029238095321665117)98:0.025762800116516854,t118:0.1014108732141534)87:0.014733752875598086)53:0.002598101346617261,t12:0.11772707761399336)100:0.0323683046613994)62:0.002191362089417505)100:0.019563835970112152,(t218:0.12374986683485793,(t58:0.11188623244772537,(t106:0.07217544088033027,t66:0.058766603855361914)97:0.020793085712610522)93:0.020136491923846662)100:0.0464878795764104)34:1.28060618246117E-6,(((((t121:0.12924788308414653,t93:0.15741892500820695)48:0.014209198457489983,((t474:0.041567922183912025,(t443:0.02734134286427507,(t438:0.01194149372227841,t299:0.006588604579050394)100:0.03066520418672206)41:0.0014785114631333037)100:0.09044274864750067,((t62:0.02759939919459967,t119:0.03474186855473828)80:1.28060618246117E-6,t64:0.04133201473118848)100:0.0978040610590435)100:0.041438421249138366)13:1.28060618246117E-6,t204:0.12200047921642883)55:0.0045724471639626875,(t288:0.1084120167506601,(t496:0.018079855489872124,t45:0.016222585899287073)100:0.08583826362153472)100:0.04679808117271949)45:0.0059589899835981374,((t464:0.12094100431651676,t67:0.10789050286291058)100:0.07991960516598831,(t124:0.07927095559501597,(t488:0.012723461585328824,t190:0.020826342144274763)100:0.06987076099305793)100:0.06284623802428504)19:1.28060618246117E-6)47:0.006729742226452831)79:0.014540405825929236)20:1.28060618246117E-6)26:0.0010383543954154085)10:1.28060618246117E-6,(t387:0.20538009218891223,((t182:0.10750773749970059,(t348:0.07271193853990027,(t198:0.06173016953396456,t431:0.04313362132159189)91:0.012908774316778362)100:0.024326757924139977)100:0.06603975551178011,((t346:0.07901530468419846,t24:0.13008253203494713)86:0.013901492546563685,t427:0.11177236945076131)100:0.04469989998477104)67:0.005127223770719831)92:0.015676675244401247)20:9.819960638786517E-4,((((t491:0.004057606692687111,t291:0.005742348191867697)100:0.1806042929454614,t497:0.2037315064451476)26:1.28060618246117E-6,(((t201:0.06754884943478977,t23:0.07858095462910024)100:0.06481873580614268,((t454:0.05054594094182899,t373:0.03784461690682863)100:0.09931538450474162,(((t179:0.08974545354222763,(t50:0.05910134374913718,(t423:0.009744594974440367,t389:1.28060618246117E-6)100:0.05014941655881632)100:0.046809609580083886)88:0.010118310247607436,(t84:0.05966438136457367,t420:0.05177841475604175)100:0.05436377093702334)84:0.006534449373480624,(t343:0.09001051086099993,t55:0.11119858867753188)88:0.01753588443638785)100:0.0314713909919055)94:0.00751249734848586)99:0.01839491344455796,(((t42:0.15337280855607727,t148:0.12722870273522469)86:0.009016327931657386,(((t256:0.09285029043063348,t183:0.07465982516101954)99:0.01990165479300553,t404:0.1197365819711296)70:0.0032061546513792675,((t274:0.006002912980422437,t36:0.011908877280136572)100:0.09285931717084228,(t259:0.058916801034139134,t44:0.05154962566903489)100:0.05722433334460059)69:0.007584791552409706)100:0.02899418744823094)100:0.024746638897693442,((t432:0.14104820660010747,((t48:0.08538504026682084,(t128:0.09161574533532613,(t143:0.0669272240471088,t309:0.06768246825124911)90:0.007612315227007921)100:0.04094044435166495)100:0.02182864670849406,(t499:0.12330326240925596,t22:0.1343574145058776)71:0.0016018876381818608)100:0.02296722933545503)100:0.021228651301429313,((t375:0.08707345632559439,((t403:0.0746048794502126,t139:0.0900721509934046)76:1.28060618246117E-6,((t229:0.018900720380834794,t88:0.01898603458577885)100:0.03168180697299456,t147:0.054028080272321914)100:0.03579908920016309)98:0.017671839004118048)100:0.04696848733192598,(t25:0.10413680555977896,(t171:0.060605593795599565,t174:0.07100976449969716)100:0.0360396489468767)100:0.03150014553669344)100:0.02302293124654936)100:0.01317639641055689)100:0.008535576038117845)74:0.001614968699715294)87:0.01095415641889886,((((t8:0.11598922228238877,((t411:0.03285691675243413,t263:0.0721851818170049)100:0.08939924099637697,t40:0.09792413001391818)81:0.011542560106631428)98:0.02161665278970565,((t155:0.07214886766246043,(t159:0.10041757737781204,t391:0.07184333387561968)18:0.0018813766339229303)51:0.0065035594315923356,t243:0.12039069066540481)100:0.03482018392251097)83:0.0053643758329177775,(((t360:0.10137098292243726,((t434:0.03908774950820112,t441:0.037457497470598164)100:0.09444134063157548,(t104:0.05378227422548194,(t453:0.051480242869899634,t337:0.03757857164379937)100:0.011829227901388807)100:0.08383084336080066)49:1.28060618246117E-6)100:0.03255679045732144,(t220:0.1274025581866953,((t366:0.09296725273237437,t210:0.08771438404139376)100:0.03679518869045666,t355:0.13203925959832258)65:0.0061965187400838916)94:0.0083008015081342)96:0.011323366875271359,(t426:0.09213943454916147,(t197:0.08561585098374117,t319:0.05696590785020593)90:1.28060618246117E-6)100:0.0780582882030906)76:0.005249149022060012)100:0.030256378332758276,(((t482:0.12345564313438066,(t329:0.091628458013025,t339:0.15007705248853567)86:0.016910723487421068)78:0.009652274533722175,((t194:0.1287896085573261,(t95:0.10332302961004684,(t433:0.07847880709080887,t257:0.03937381788978546)100:0.05762504147869876)91:0.013317630031816095)41:1.28060618246117E-6,t393:0.14374224381631429)94:0.011795111115484059)100:0.0265886198780697,((t98:0.009164086209246698,t485:0.00446672248485915)100:0.14945007105963737,((t470:0.1285659328361837,(t365:0.09915869660120957,(t277:0.05191713799030193,t436:0.04661760908780692)100:0.08381852382419772)92:0.017398827571112028)99:0.01867680026522861,(((t287:0.033762859856260326,t451:0.029172486323449282)100:0.029011418512700025,(t294:0.0027206339031963433,t325:0.001084117903976444)100:0.11188303299813171)100:0.03378704967060549,t13:0.14871987688563515)99:0.02020468791066357)68:0.0023735160255263336)100:0.023938052497163652)99:0.01434631237193819)62:0.00751774574616681)19:1.28060618246117E-6)75:0.0037749039628434825)99:0.01097836922577562)94:0.008739769423865673)83:0.007499766326436678)35:1.28060618246117E-6)100:0.06741310372226965);");
        //Tree b = Newick.getTreeFromString("((((t170:1.0,t469:1.0):1.0,((t49:1.0,(((t408:1.0,t90:1.0):1.0,t369:1.0):1.0,((t328:1.0,(t145:1.0,(t463:1.0,t245:1.0):1.0):1.0):1.0,((t3:1.0,t10:1.0):1.0,t478:1.0,t353:1.0):1.0):1.0):1.0):1.0,(((t65:1.0,t462:1.0):1.0,t383:1.0):1.0,t480:1.0):1.0):1.0):1.0,(((((t344:1.0,(t45:1.0,t496:1.0):1.0):1.0,((t288:1.0,t206:1.0):1.0,t310:1.0):1.0):1.0,(t293:1.0,((t89:1.0,t289:1.0):1.0,t120:1.0):1.0):1.0):1.0,t308:1.0,t204:1.0,((((t62:1.0,t119:1.0):1.0,t64:1.0):1.0,t335:1.0):1.0,((t226:1.0,(t299:1.0,t438:1.0):1.0):1.0,((t135:1.0,t443:1.0):1.0,(t157:1.0,(t474:1.0,t71:1.0):1.0):1.0):1.0):1.0):1.0,((t93:1.0,(t253:1.0,t59:1.0):1.0):1.0,t212:1.0,(t97:1.0,t121:1.0):1.0):1.0):1.0,(t190:1.0,(t488:1.0,t358:1.0):1.0):1.0,((((((((t83:1.0,t127:1.0):1.0,t260:1.0):1.0,t113:1.0):1.0,t19:1.0):1.0,t439:1.0):1.0,((t292:1.0,((t354:1.0,t336:1.0):1.0,(t242:1.0,t265:1.0):1.0):1.0,t118:1.0):1.0,((t150:1.0,t12:1.0):1.0,((t101:1.0,t46:1.0):1.0,t492:1.0):1.0):1.0):1.0):1.0,(t422:1.0,t268:1.0,((t456:1.0,(t7:1.0,(t72:1.0,t281:1.0):1.0):1.0):1.0,t29:1.0):1.0):1.0):1.0,((((t66:1.0,(t285:1.0,(t284:1.0,t106:1.0):1.0):1.0):1.0,((t192:1.0,t305:1.0):1.0,(t133:1.0,t58:1.0):1.0):1.0):1.0,(t217:1.0,((t475:1.0,t31:1.0):1.0,t218:1.0):1.0):1.0):1.0,t435:1.0):1.0):1.0):1.0):1.0,(((t396:1.0,t464:1.0):1.0,t2:1.0):1.0,(((t67:1.0,t168:1.0):1.0,t85:1.0):1.0,t278:1.0):1.0):1.0);");
        //Tree c = hey.SCM(a, b);

        //hey.testCuttingAndInserting();
        //hey.testCompareTreeStructure();




        //hey.testStrictConsensus();

        /*Tree test = Newick.getTreeFromString("((a:1.0,(b:1.0,c:1.0):1.0):1.0,(d:1.0,e:1.0):1.0)");
        TreeNode a = test.findLeastCommonAncestor(test.getVertex("a"), test.getVertex("c"));
        System.out.println("Indegree vom LCA von a und c ist "+a.indegree());
        System.out.println("Degree vom LCA von a und c ist "+a.degree());
        */



        /*test.removeVertex(test.getVertex("a"));
        for (TreeNode x : test.vertices()){
            if (x.degree() == 2 && x!=test.getRoot()){
                for (TreeNode y : x.children()){
                    test.addEdge(x.getParent(),y);
                }
                test.removeVertex(x);
            }
        }*/

        /*TreeNode b = test.getVertex("b");
        TreeNode d = test.getVertex("d");
        TreeNode e = test.getVertex("e");
        List<TreeNode> tokeep = new ArrayList<TreeNode>();
        tokeep.add(b);
        tokeep.add(d);
        tokeep.add(e);
        test = hey.CutTree(test, tokeep);
        String print = Newick.getStringFromTree(test);
        System.out.println(print);*/

        //hey.testCutTree();




    }

}

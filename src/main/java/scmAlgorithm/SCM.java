package scmAlgorithm;


import epos.algo.consensus.nconsensus.NConsensus;
import phyloTree.io.Newick;
import phyloTree.model.tree.Tree;
import phyloTree.model.tree.TreeNode;
import treeUtils.TreeUtils;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Anika
 * Date: 27.10.14
 * Time: 13:13
 * To change this template use File | Settings | File Templates.
 */


public class SCM {

    //those contain the cut leaves as key and the leaves over whose LCA it should be hung in again as value
    //value may contain "polytomy" when the leaf should hang on the LCA, creating a polytomy
    private LinkedHashMap<String, List<String>> TreeCut1 = new LinkedHashMap<String, List<String>>();
    private LinkedHashMap<String, List<String>> TreeCut2 = new LinkedHashMap<String, List<String>>();
    //contains the nodes already hung in, neccessary for checking if the edge for hanging in is still there
    private List<String> tolerated = new ArrayList<String>();
    public static final boolean debug = false;

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



    //for every edge, this collects all nodes from both trees hanging at that edge
    //results are saved in a map
    private void takeCareOfCollisions(Tree consensus, Tree one, Tree two){
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
        //this map contains leaves specifying an edge as key and nodes that need to be hung at the
        //edge and the information which tree they come from as value
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

        //for every node in the consensus tree:
        while (iterator.hasNext()){
            curnode = iterator.next();
            innernodet1children.clear();
            innernodet2children.clear();
            if (!curnode.isLeaf()){
                innernodeleaveshelp = TreeUtils.helpgetLabelsFromNodes(Arrays.asList(curnode.getLeaves()));
                innernodeleaves = Arrays.asList(curnode.getLeaves());
                innernodechildren = curnode.getChildren();
                //corresponding inner node in tree 1 and tree 2
                innernodet1 = one.findLeastCommonAncestor(TreeUtils.helpgetTreeNodesFromLabels(innernodeleaveshelp, one));
                innernodet2 = two.findLeastCommonAncestor(TreeUtils.helpgetTreeNodesFromLabels(innernodeleaveshelp, two));
                int numinnernodechildren = innernodechildren.size();
                //search nodes in both trees corresponding to the children of the inner node in the consensus tree
                for (int iter=0; iter<numinnernodechildren; iter++){
                    innernodet1children.add(one.findLeastCommonAncestor(TreeUtils.helpgetTreeNodesFromLabels(TreeUtils.helpgetLabelsFromNodes(Arrays.asList(innernodechildren.get(iter).getLeaves())), one)));
                    innernodet2children.add(two.findLeastCommonAncestor(TreeUtils.helpgetTreeNodesFromLabels(TreeUtils.helpgetLabelsFromNodes(Arrays.asList(innernodechildren.get(iter).getLeaves())), two)));
                }
                int numinnernodet1children = innernodet1children.size();
                int numinnernodet2children = innernodet2children.size();
                //for each child of the inner node, search nodes hanging at the path from it to the parent, in both trees
                for (int iter=0; iter<numinnernodet1children; iter++){
                    helpCollisionsFindNodesAtSameEdge (consensus, innernodechildren.get(iter), one, 1, innernodet1, innernodet1children.get(iter), order, TreeCut1list);
                }
                for (int iter=0; iter<numinnernodet2children; iter++){
                    helpCollisionsFindNodesAtSameEdge (consensus, innernodechildren.get(iter), two, 2, innernodet2, innernodet2children.get(iter), order, TreeCut2list);
                }
            }
        }
        curnode = consensus.getRoot();
        innernodeleaveshelp = TreeUtils.helpgetLabelsFromNodes(Arrays.asList(curnode.getLeaves()));
        innernodet1 = one.findLeastCommonAncestor(TreeUtils.helpgetTreeNodesFromLabels(innernodeleaveshelp, one));
        innernodet2 = two.findLeastCommonAncestor(TreeUtils.helpgetTreeNodesFromLabels(innernodeleaveshelp, two));
        //if in both trees nodes are hanging over the consensus root node, save those nodes in a map
        if (innernodet1.getParent() != null && innernodet2.getParent() != null){
            System.out.print("");
            helpCollisionsFindNodesOverRoot(consensus, one, 1, innernodet1, order, TreeCut1list);
            helpCollisionsFindNodesOverRoot(consensus, two, 2, innernodet2, order, TreeCut2list);
        }


        List<String> store2pathnodes = new ArrayList<String>();
        HashMap<String,List<String>> checkfordoubles = new HashMap<String, List<String>>();
        List<String> tolerated = new ArrayList<String>();
        HashMap<String, List<String>> special2nodes = new HashMap<String, List<String>>();
        List<String> store = new ArrayList<String>();
        List<String> val;
        //here, nodes hanging at the path of two or more children to the parent are collected and
        //marked to hang at the LCA of those children
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
        //if there are nodes from both trees at an edge of the consensus tree, change their hanging in criteria
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
                //remove their original hanging in criteria
                for (String el : copylist){
                    if (TreeCut2.containsKey(el)){
                        TreeCut2.remove(el);
                    }
                    if (TreeCut1.containsKey(el)){
                        TreeCut1.remove(el);
                    }
                    //those nodes' criteria have already been changed
                    if (store2pathnodes.contains(el)){
                        list.remove(el);
                    }
                    else tolerated.add(el);
                }
                List<String> newlist;
                List<String> work;
                if (list.size() != 0){
                    //safe information to make a polytomy of the nodes at the right edge
                    //they will be hung in first
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

    //given the root in the consensus tree, find nodes that hang over the corresponding node in a specified tree
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
            //currchildren are the child nodes of a node hanging over the root corresponding node
            currchildren = newparent.getChildren();
            //remove the leaves already viewed
            if (firstnewparent){
                currchildren.remove(rootintree);
            }
            else currchildren.remove(formernewparent);
            //find the node for each child that has to be hung first at the edge over the root and save it in a map
            if (!currchildren.isEmpty()){
                for (TreeNode ch : currchildren){
                    //currkey specifies the edge over the root
                    //idea: if an element in currchildren is no leaf, one node in the leaves of it will first hang at the edge-polytomy, this node has to be found
                    currkey = consensusleaves;
                    if (order.containsKey(currkey)){
                        currlist = order.get(currkey);
                    }
                    else currlist = new ArrayList<String>();
                    int currlistsize = currlist.size();
                    int counter = 0;
                    //if the found child is a leaf, we can just add it to the nodes that shall hang at the polytomy over the root
                    if (ch.isLeaf()){
                        currlist.add(ch.getLabel());
                    }
                    else {
                        if (debug){ counter = 0; }

                        for (TreeNode leaf : ch.getLeaves()){
                            List<String> values = new ArrayList<String>();
                            for (Map.Entry<String, List<String>> en : treeentryset){
                                if (en.getKey().equalsIgnoreCase(leaf.getLabel())) values = en.getValue();
                            }
                            //we search for the one leaf that will first hang at the polytomy over the root
                            if (values.containsAll(currkey)){
                                currlist.add(leaf.getLabel());
                                if (debug) counter++;
                            }
                        }
                    }
                    if (debug){
                        if (counter > 1) {
                            System.out.println("more than one leaf wants to be the first to hang at the child polytomy");
                        }
                    }

                    //if new nodes hanging over the root are found, they are put as values of the root in a map,
                    //plus the information which tree they were found in
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

    //given a child node and a parent node in the consensus tree, nodes hanging at the corresponding path in a specified tree are found and saved in a map
    private Map<List<String>, List<String>> helpCollisionsFindNodesAtSameEdge (Tree consensus, TreeNode childinconsensus, Tree input, int treenumber, TreeNode parent, TreeNode child, Map<List<String>, List<String>> order, List<Map.Entry<String,List<String>>> treeentryset){
        List<TreeNode> currchildren = new ArrayList<TreeNode>();
        List<String> consensusleaves = new ArrayList<String>(TreeUtils.helpgetLabelsFromNodes(Arrays.asList(consensus.getLeaves())));
        TreeNode newparent = child.getParent();
        List<String> currlist;
        List<String> currkey;
        boolean firstnewparent = true;
        TreeNode formernewparent = new TreeNode();
        while (newparent != parent){
            //currchildren are the child nodes of a node hanging at the path from child to parent
            currchildren = newparent.getChildren();
            //remove the leaves already viewed
            if (firstnewparent){
                currchildren.remove(child);
            }
            else currchildren.remove(formernewparent);
            //find the node for each child that has to be hung first at the specified edge and save it in a map
            if (!currchildren.isEmpty()){
                for (TreeNode ch : currchildren){
                    //currkey specifies the edge
                    //idea: if an element in currchildren is no leaf, one node in the leaves of it will first hang at the edge-polytomy, this node has to be found
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
                    //if the found child is a leaf, we can just add it to the nodes that shall hang at the polytomy at the edge
                    if (ch.isLeaf()){
                        if (!consensusleaves.contains(ch.getLabel())){
                            currlist.add(ch.getLabel());
                        }
                    }
                    else {
                        if (debug) counter = 0;
                        boolean inconsensus = false;
                        //make sure that no nodes are viewed that are part of the consensus, those will be viewed later or have already been viewed
                        for (TreeNode leaf : ch.getLeaves()){
                            if (consensusleaves.contains(leaf.getLabel())){
                                inconsensus = true;
                            }
                        }
                        if (!inconsensus){
                            for (TreeNode leave : ch.getLeaves()){
                                List<String> values = new ArrayList<String>();
                                for (Map.Entry<String, List<String>> en : treeentryset){
                                    if (en.getKey().equalsIgnoreCase(leave.getLabel())) values = en.getValue();
                                }
                                //we search for the one leaf that will first hang at the polytomy at the specified edge
                                if (values.containsAll(currkey)){
                                    currlist.add(leave.getLabel());
                                    if (debug) counter++;
                                }
                            }
                        }
                    }
                    if (debug){
                        if (counter > 1) {
                            System.out.println("more than one leaf wants to be the first to hang at the child polytomy");
                        }
                    }

                    //if new nodes that hang at the edge are found, they are put as values of the edge in a map
                    //plus the information which tree they were found in
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

    //if print is enabled, the method will print both trees after they are cut, and the consensus tree
    public Tree getSCM (Tree one, Tree two, boolean print){
        ArrayList<String> nodesofboth = TreeUtils.getOverLappingNodes(one, two);
        Tree result;
        Tree between;
        NConsensus con = new NConsensus();
//        con.getLog().setLevel(Level.OFF);
        if (nodesofboth.size()==0){
            System.err.println("no overlapping nodes, consensus is not possible");
            return new Tree();
        }
        //no cutting and inserting neccessary, because the trees have the same leaves
        if (nodesofboth.size() == one.getLeaves().length && nodesofboth.size() == two.getLeaves().length){
            con.setInput(new Tree[]{one, two});
            con.setThreshold(1d);
            con.run();
            result =  con.getResult();
        }
        else {
            Tree originalone = one.cloneTree();
            Tree originaltwo = two.cloneTree();
            one = CutTree(one, nodesofboth);
            two = CutTree(two, nodesofboth);
            con.setInput(new Tree[]{one, two});
            con.setThreshold(1d);
            con.run();
            between =  con.getResult();
            if (print){
                System.out.println("Cut one "+ Newick.getStringFromTree(one));
                System.out.println("Cut two "+Newick.getStringFromTree(two));
                System.out.println("Consensus "+Newick.getStringFromTree(between));
            }
            takeCareOfCollisions(between, originalone, originaltwo);
            result = hangInNodes(between);
            setTolerated(new ArrayList<String>());
        }
        return result;

    }

    //the cut nodes are hung at the consensus tree, collision nodes first, then the nodes in reversed
    //order of their cutting
    public Tree hangInNodes (Tree input){
        Tree output = input.cloneTree();
        Tree consensus = input.cloneTree();
        Set<Map.Entry<String, List<String>>> mapValues = TreeCut1.entrySet();
        int maplength = mapValues.size();
        Map.Entry<String,List<String>>[] TreeCut1entries = new Map.Entry[maplength];
        mapValues.toArray(TreeCut1entries);
        //hangs in nodes cut out of tree one and collision nodes
        output = helphangInNodes(output, consensus, maplength, TreeCut1entries);
        List<String> tolerated = getTolerated();
        tolerated.addAll(TreeCut1.keySet());
        setTolerated(tolerated);
        TreeCut1.clear();

        mapValues = TreeCut2.entrySet();
        maplength = mapValues.size();
        Map.Entry<String,List<String>>[] TreeCut2entries = new Map.Entry[maplength];
        mapValues.toArray(TreeCut2entries);
        //hangs in nodes cut out of tree two except collision nodes
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

        //for every node that will be inserted
        for (int iter=maplength-1; iter>=0; iter--){
            currlabel = TreeCutentries[iter].getKey();
            currlist = TreeCutentries[iter].getValue();
            //for polytomies:
            if (currlist.contains("Polytomy")){
                currlist.remove("Polytomy");
                finallist = TreeUtils.helpgetTreeNodesFromLabels(currlist, output);
                lca = output.findLeastCommonAncestor(finallist);
                List<String> lcaleaves = TreeUtils.helpgetLabelsFromNodes(Arrays.asList(lca.getLeaves()));
                //if the lca contains more leaves than the insertion node memorized
                if (!TreeUtils.StringListsEquals(lcaleaves, currlist)){
                    lcaleaves.removeAll(currlist);
                    //this indicates that the wanted edge no longer exists due to structure change in the consensus tree
                    //the node will now be part of a polytomy
                    if (!tolerated.containsAll(lcaleaves)){
                        if (debug){
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
                                System.out.println("no consensus tree structure change issue, another problem exists");
                            }
                        }
                    }
                }
                //the node is inserted directly at the LCA node
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
            //no polytomy
            else {
                finallist = TreeUtils.helpgetTreeNodesFromLabels(currlist, output);
                boolean alright = true;
                //find the lca of the memorized nodes
                if (finallist.size()==1){
                    lca = finallist.get(0);
                }
                else {
                    lca = output.findLeastCommonAncestor(finallist);
                    List<String> lcaleaves = TreeUtils.helpgetLabelsFromNodes(Arrays.asList(lca.getLeaves()));
                    if (!TreeUtils.StringListsEquals(lcaleaves,currlist)){
                        lcaleaves.removeAll(currlist);
                        if (!tolerated.containsAll(lcaleaves)) {
                            alright = false;
                        }
                    }

                }
                boolean done = false;
                //if the wanted edge no longer exists due to structure change in the consensus tree, the node needs to be hung at a polytomy
                if (!alright){
                    done = true;

                    if (debug) {
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
                            System.out.println("no consensus tree structure change issue, another problem exists");
                        }
                        else{
                            done = true;
                        }
                    }
                }
                //if the node doesn't have to be inserted like a polytomy
                //create a node in the edge over the LCA where the node will hang
                if (!done){
                    //LCA is the root, so a new root needs to be created
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
                //node is hung at the new node in the edge or in a polytomy at the LCA
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
        }
        return output;
    }

    //cuts out all the leaves in the tree that aren't in list "keep"
    protected Tree CutTree (Tree input, List<String> keep){
        Tree output = input.cloneTree();
        //makes a list that contains all leaves in the input tree
        List<String> originalleaves = new ArrayList<String>(TreeUtils.helpgetLabelsFromNodes(Arrays.asList(output.getLeaves())));
        if (keep.isEmpty()) return new Tree();
        //creates a depth first order of the leaves in the tree for better overview
        List<String> depthFirst = TreeUtils.generateCustomDepthFirstListChildren(output);
        List<TreeNode> tokeep = new ArrayList<TreeNode>(TreeUtils.helpgetTreeNodesFromLabels(keep, output));
        List<TreeNode> cut = new ArrayList<TreeNode>(TreeUtils.helpgetTreeNodesFromLabels(depthFirst, output));
        cut.removeAll(tokeep);
        boolean firstpass = true;
        int savewhere;
        //determine the list where the nodes will be saved
        //if no nodes were cut from tree 1, those from tree 2 will be saved in TreeCut2
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
                    if (debug){
                        if (par == null){
                            System.out.println("mistake in the tree structure: one leaf has no parent");
                        }
                    }
                    between = new ArrayList<TreeNode>(Arrays.asList(par.getLeaves()));
                    between2 = new ArrayList<TreeNode>(between);
                    for (TreeNode el : between2){
                        if (!originalleaves.contains(el.getLabel())) between.remove(el);
                    }
                    //search for a parent node that has more of the original leaves than the node that is getting cut out
                    while (between.size() <= 1 && between.get(0).getParent()!=null){
                        par = par.getParent();
                        between = new ArrayList<TreeNode>(Arrays.asList(par.getLeaves()));
                        between2 = new ArrayList<TreeNode>(between);
                        for (TreeNode el : between2){
                            if (!originalleaves.contains(el.getLabel())) between.remove(el);
                        }
                    }
                    between.remove(iter);
                    if (debug){
                        if (between.size()==0){
                            System.err.println("problem in CutTree");
                        }
                    }

                    //find out whether the parent node has more than 2 children with original leaves, then it's a polytomy
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
            //leaves that became leaves because their children were removed, will be removed
            cut.removeAll(tokeep);
        }
        //remove nodes of degree 2
        for (TreeNode x : output.vertices()){
            if (x.degree() == 2 && x!=output.getRoot()){
                for (TreeNode y : x.children()){
                    output.addEdge(x.getParent(),y);
                }
                output.removeVertex(x);

            }
        }
        //set new root if neccessary
        TreeNode newroot = output.findLeastCommonAncestor(tokeep);
        if (!output.getRoot().equalsNode(newroot)){
            Tree finaltree = output.getSubtree(newroot);
            return finaltree;

        }
        return output;
    }


}

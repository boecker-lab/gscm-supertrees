package scmAlgorithm.treeSelector;

import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
import epos.model.tree.treetools.FN_FP_RateComputer;
import epos.model.tree.treetools.SingleTaxonReduction;
import epos.model.tree.treetools.TreeUtilsBasic;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.*;

/**
 * Created by fleisch on 10.02.15.
 */
public class TreePair implements Comparable<TreePair> {
    public final Tree t1;
    public final Tree t2;
//    public Tree[] clones = new Tree[2]; //todo remove --> debug
    public final double score;
    private Set<String> commonLeafes = null;

    private boolean first = true;
    private List<SingleTaxon> singleTaxa = null;
    private Map<Set<String>, Set<SingleTaxon>> commenInsertionPointTaxa = null;


    private SingleTaxonReduction singleTaxonReducer = null;

    public TreePair(final Tree t1, final Tree t2, final double score) {
        this.t1 = t1;
        this.t2 = t2;
        this.score = score;
    }

    public TreePair(Tree t1, Tree t2, double score, Set<String> commonLeafes) {
        this(t1, t2, score);
        this.commonLeafes = commonLeafes;
    }

    //unchecked
    public Tree getPartner(Tree t) {
        if (t.equals(t1))
            return t2;
        else
            return t1;
    }

    public Collection<String> getCommonLeafes() {
        return commonLeafes;
    }

    public void setCommonLeafes(Set<String> commonLeafes) {
        this.commonLeafes = commonLeafes;
    }

    //uncheked and uncached


    public void pruneToCommonLeafes() {
//        clones[0] = t1.cloneTree();//todo remove --> debug
//        clones[1] = t2.cloneTree();//todo remove --> debug

        singleTaxa = new ArrayList<>(t1.vertexCount() + t2.vertexCount()); // is an upper bound for the list --> no resizing
        commenInsertionPointTaxa = new THashMap<>(t1.vertexCount() + t2.vertexCount());

        pruneLeafes(t1);
        pruneLeafes(t2);
    }

    public void pruneToCommonLeafes(boolean singeTaxonReduction) {
//        clones[0] = t1.cloneTree();//todo remove --> debug
//        clones[1] = t2.cloneTree();//todo remove --> debug
        if (singeTaxonReduction) {
            System.out.println("Warning wrong taxa reinsertion method used");
            singleTaxonReducer = new SingleTaxonReduction();
            singleTaxonReducer.modify(Arrays.asList(t1, t2));
        } else {
            singleTaxa = new ArrayList<>(t1.vertexCount() + t2.vertexCount()); // is an upper bound vor list --> no resizing
            commenInsertionPointTaxa = new THashMap<>(t1.vertexCount() + t2.vertexCount());
            pruneLeafes(t1);
            pruneLeafes(t2);
        }
    }

    // NOTE: single taxon reduction optimized for 2 trees with known common taxa
    private void pruneLeafes(Tree t) {
        Map<Set<String>, Set<SingleTaxon>> commenInsertionPointTaxa;
        commenInsertionPointTaxa = new THashMap<>(t.vertexCount());

        Set<TreeNode> maxSingleSubtreeRoots = new THashSet<>();

        // search for maximum subtrees that contain only single taxa
        for (TreeNode node : t.getRoot().depthFirstIterator()) {
            if (node.isLeaf()) {
                if (!commonLeafes.contains(node.getLabel())) {
                    maxSingleSubtreeRoots.add(node);
                }
            } else {
                int singeChilds = 0;
                final int childCount = node.childCount();
                List<TreeNode> children = new ArrayList<>(childCount);
                for (TreeNode child : node.children()) {
                    children.add(child);
                    if (maxSingleSubtreeRoots.contains(child))
                        singeChilds++;
                }

                if (singeChilds == childCount) {
                    maxSingleSubtreeRoots.add(node);
                    maxSingleSubtreeRoots.removeAll(children); //we dont have to remove here, but surprisingly ist good for the performance.
                }
            }
        }

        // sorting nodes to delete via tree (O(2n) instead of O(nlog(n)))
        Deque<TreeNode> stack = new ArrayDeque<>(t.vertexCount());
//        List<TreeNode> toRemove = new ArrayList<>(maxSingleSubtreeRoots.size());

        stack.push(t.getRoot());
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            if (maxSingleSubtreeRoots.contains(node)) {
                SingleTaxon st;
                Set<String> siblingLeaves = new THashSet<>();
                int numOfSiblings = 0;
                for (TreeNode sibling : node.getParent().children()) {
                    if (!sibling.equals(node)) {
                        numOfSiblings++;
                        siblingLeaves.addAll(TreeUtilsBasic.getLeafLabels(sibling));
                    }
                }

                if (node.isInnerNode()) {
                    t.removeSubtree(node);
                } else {
                    t.removeVertex(node);
                }


                if (numOfSiblings == 1) {
                    Set<String> commonSiblingLeafes = new THashSet<>(siblingLeaves); //todo what todo with empty suff --> is part of subtree an not important for the collision case
                    commonSiblingLeafes.retainAll(commonLeafes);
                    st = new SingleTaxon(node, siblingLeaves, commonSiblingLeafes, numOfSiblings);

                    if (!commonSiblingLeafes.isEmpty()) {
                        Set<SingleTaxon> singles = commenInsertionPointTaxa.get(commonSiblingLeafes);
                        if (singles == null) {
                            if (first) {
                                singles = new THashSet<>();
                                commenInsertionPointTaxa.put(commonSiblingLeafes, singles);
                                singles.add(st);
                            } else {
                                singles = this.commenInsertionPointTaxa.get(commonSiblingLeafes);
                                if (singles != null) {
                                    commenInsertionPointTaxa.put(commonSiblingLeafes, singles);
                                    singles.add(st);
                                }
                            }
                        } else {
                            singles.add(st);
                        }

                    } else {
                        System.out.println("this schouldn't be possible");//todo this case schould not exist maybe remove if
                    }
                }else{
                    st = new SingleTaxon(node, siblingLeaves, new THashSet<String>(), numOfSiblings);
                }
                singleTaxa.add(st);

                TreeUtilsBasic.pruneDegreeOneNodes(t, false, false);
            } else {
                for (TreeNode child : node.children()) {
                    stack.push(child);
                }
            }
        }

        //this is to do the switch between first and second tree mode
        first = false;
        this.commenInsertionPointTaxa = commenInsertionPointTaxa;

    }

    //todo use only one version for perfomance reasons?
    public void reinsertSingleTaxa(Tree t) {
        if (singleTaxa != null) {
            reinsertSingleTaxaFast(t);
        } else if (singleTaxonReducer != null) {
            singleTaxonReducer.unmodify(Arrays.asList(t));
        }
    }


    private void reinsertSingleTaxaFast(Tree t) {
        Map<String, TreeNode> labelToNode = new THashMap<>();
        for (TreeNode leaf : t.getLeaves()) {
            labelToNode.put(leaf.getLabel(), leaf);
        }

        ListIterator<SingleTaxon> it = singleTaxa.listIterator(singleTaxa.size());
        while (it.hasPrevious()) {
            SingleTaxon singleTaxon = it.previous();
            List<TreeNode> siblingLeaves = new ArrayList<>(singleTaxon.siblingLeaves.size());
            for (String leaf : singleTaxon.siblingLeaves) {
                siblingLeaves.add(labelToNode.get(leaf));
            }
            TreeNode lca = t.findLeastCommonAncestor(siblingLeaves);
            TreeNode lcaParent = lca.getParent();


            //check ist we have to add a new inner node (removed from binary) or not (removed from polytomy)
            //todo not very nice --> maybe put all single taxa in such a map from begining on --> maybe less confusing code
            Set<SingleTaxon> singles = commenInsertionPointTaxa.get(singleTaxon.commonSiblingLeaves);
            if (singles == null) {
                singles = new THashSet<>(1);
                singles.add(singleTaxon);
            }


            //build insertion point for first occurence on this path...
            if (!singles.isEmpty()) {//if empty the taxon is already iserted before.
//                System.out.println("start inserting leafes");
                if (singleTaxon.numOfSiblings == 1) {
                    //todo is there a faster way
                    Set<String> s =  TreeUtilsBasic.getLeafLabels(lca);
                    s.retainAll(commonLeafes);
                    Set<String> s2 =  new HashSet<>(singleTaxon.siblingLeaves);
                    s2.retainAll(commonLeafes);

                    if (!s.equals(s2)){
//                        System.out.println("maybe removed clade!!!");
                        if (lca.isLeaf()) {//todo do we need that? because if there is more siblings than it should be, there are at least 2?
                            System.out.println("nu leaf insert point");
                            lca = lcaParent;
                        }
                    }else {
                        //check if we have to insert a new root
                        if (lcaParent != null) {
//                            System.out.println("standard");
                            TreeNode nuLcaParent = new TreeNode();
                            t.addVertex(nuLcaParent);
                            t.addEdge(nuLcaParent, lca);
                            t.addEdge(lcaParent, nuLcaParent);
                            t.removeEdge(lcaParent, lca);
                            lca = nuLcaParent;

                        } else { //insert new root
//                            System.out.println("nu ROOT");
                            TreeNode nuLcaParent = new TreeNode();
                            t.addVertex(nuLcaParent);
                            t.addEdge(nuLcaParent, lca);
                            t.setRoot(nuLcaParent);
                            lca = nuLcaParent;
                        }
                    }
                } else if (lca.isLeaf()) {
                    System.out.println("nu leaf insert point");
                    lca = lcaParent;//todo do we need that? because polytomi leafes have mor than one sibling?!
                }


                for (SingleTaxon st : singles) {
                    TreeNode nuNode = st.insertionPoint;
                    t.addVertex(nuNode);
                    t.addEdge(lca, nuNode);

                    if (st.isSubtree()) {
                        for (TreeNode node : nuNode.depthFirstIterator()) {
                            t.addVertex(node);
                            if (node.isLeaf()) {
                                labelToNode.put(node.getLabel(), node);
                            }
                        }
                    } else {
                        labelToNode.put(nuNode.getLabel(), nuNode);
                    }
                }
                /*double[] r = FN_FP_RateComputer.calculateSumOfRates(t, clones);//todo remove --> debug
                if (Double.compare(0d,r[1]) != 0){//todo remove --> debug
                    System.out.println("False positives  DURING taxa insertion!");
                }*/
                singles.clear();
//                System.out.println();
            }
        }
    }

    //todo just for debuging an testig
    public boolean buildCompatibleRootsNaive() {
        Map<TreeNode, Set<String>> innerNodeToSplit = new HashMap<>();
        for (TreeNode n1 : t1.vertices()) {
            if (n1.isInnerNode() && !n1.equals(t1.getRoot())) {
                Set<String> split1a = TreeUtilsBasic.getLeafLabels(n1);
                split1a.retainAll(commonLeafes);
                Set<String> split1b = new HashSet<>(commonLeafes);
                split1b.removeAll(split1a);
                for (TreeNode n2 : t2.vertices()) {
                    if (n2.isInnerNode() && !n2.equals(t2.getRoot())) {
                        Set<String> split2;
                        if (!innerNodeToSplit.containsKey(n2)) {
                            split2 = TreeUtilsBasic.getLeafLabels(n2);
                            split2.retainAll(commonLeafes);
                            innerNodeToSplit.put(n2, split2);
                        } else {
                            split2 = innerNodeToSplit.get(n2);
                        }
                        if (split2.equals(split1a) || split2.equals(split1a)) {
                            //possible root position found
                            rerootToNode(t1, n1);
                            rerootToNode(t2, n2);

                            /*//todo remove debug stuff
                            Set<Set<String>> s1 = new HashSet<>();
                            for (TreeNode node : t1.getRoot().children()) {
                                Set<String> s = TreeUtilsBasic.getLeafLabels(node);
                                s.retainAll(commonLeafes);
                                s1.add(s);
                            }
                            if (!s1.contains(split1a) || !s1.contains(split1b)) {
                                System.out.println("Error during rooting");
                            }
                            assert s1.contains(split1a);
                            assert s1.contains(split1b);

                            Set<Set<String>> s2 = new HashSet<>();
                            for (TreeNode node : t2.getRoot().children()) {
                                Set<String> s = TreeUtilsBasic.getLeafLabels(node);
                                s.retainAll(commonLeafes);
                                s2.add(s);
                            }
                            if (!s2.contains(split2)) {
                                System.out.println("Error during rooting");
                            }
                            assert s2.contains(split2);
*/
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean buildCompatibleRoots() {
        Map<TreeNode, Set<String>> lcaToLabels = new HashMap<>(t2.vertexCount());
        Map<Set<String>, TreeNode> labelsToLCA = new HashMap<>(t2.vertexCount());

        for (TreeNode node : t2.getRoot().depthFirstIterator()) {
            if (!node.equals(t2.getRoot()) && !node.getParent().equals(t2.getRoot())) {
                TreeNode p = node.getParent();

                if (!lcaToLabels.containsKey(p))
                    lcaToLabels.put(p, new HashSet<String>());

                if (node.isLeaf()) {
                    String l = node.getLabel();
                    if (commonLeafes.contains(l))
                        lcaToLabels.get(p).add(l);
                } else {
                    lcaToLabels.get(p).addAll(lcaToLabels.get(node));
                    labelsToLCA.put(lcaToLabels.get(node), node);
                }
            }
        }

        lcaToLabels = new HashMap<>(t1.vertexCount());
        TreeNode rootEdge1 = null;
        TreeNode rootEdge2 = null;

        for (TreeNode node : t1.getRoot().depthFirstIterator()) {
            if (!node.equals(t1.getRoot())) {
                TreeNode p = node.getParent();

                if (!lcaToLabels.containsKey(p))
                    lcaToLabels.put(p, new HashSet<String>());

                if (node.isLeaf()) {
                    String l = node.getLabel();
                    if (commonLeafes.contains(l))
                        lcaToLabels.get(p).add(l);
                } else {
                    if (!p.equals(t1.getRoot()))
                        lcaToLabels.get(p).addAll(lcaToLabels.get(node));

                    //check if current node is a possible root
                    Set<String> split1a = new HashSet<>(lcaToLabels.get(node));
                    split1a.retainAll(commonLeafes);
                    Set<String> split1b = new HashSet<>(commonLeafes);
                    split1b.removeAll(split1a);

                    rootEdge2 = labelsToLCA.get(split1a);
                    if (rootEdge2 == null)
                        rootEdge2 = labelsToLCA.get(split1b);

                    if (rootEdge2 != null) {
                        rootEdge1 = node;
                        break;

                    }
                }
            }
        }

        if (rootEdge2 != null && rootEdge1 != null) {
            //possible root position found
            rerootToNode(t1, rootEdge1);
            rerootToNode(t2, rootEdge2);
            return true;
        }
        return false;
    }

    private void rerootToNode(Tree t, TreeNode rootEdge) {
        TreeNode firstParent = rootEdge.getParent();
        Deque<TreeNode> nodes = new ArrayDeque<>();
        nodes.add(firstParent);
        while (!nodes.isEmpty()) {
            TreeNode p = nodes.peek().getParent();
            if (p != null) {
                nodes.push(p);
            } else {
                TreeNode n1 = nodes.poll();
                TreeNode n2 = nodes.peek();
                if (n2 != null) {
                    t.removeEdge(n1, n2);
                    t.addEdge(n2, n1);
                }
            }
        }

        TreeNode nuRoot = new TreeNode();
        t.addVertex(nuRoot);
        t.setRoot(nuRoot);
        t.removeEdge(firstParent, rootEdge);
        t.addEdge(nuRoot, rootEdge);
        t.addEdge(nuRoot, firstParent);
    }


    //todo just for debuging an testig
    private void pruneLeafes2(Tree t) {
        Collection<TreeNode> parents = new THashSet<>();
        for (TreeNode node : t.vertices()) {
            if (node.isLeaf()) {
                if (!commonLeafes.contains(node.getLabel())) {
                    parents.add(node.getParent());
                    t.removeVertex(node);
                }
            }
        }

        while (!parents.isEmpty()) {
            List<TreeNode> nuParents = new ArrayList<>(parents.size());
            for (TreeNode node : parents) {
                int count = node.childCount();
                if (count < 2) {
                    TreeNode parent = node.getParent();
                    nuParents.add(parent);
                    if (count == 1) {
                        TreeNode child = node.edges().iterator().next().getTarget();
                        t.removeEdge(node, child);
                        t.addEdge(parent, child);
                    }
                    t.removeVertex(node);
                }
            }
            parents = nuParents;
        }
    }

    @Override
    public int compareTo(TreePair o) {
        return Double.compare(o.score, score); //ATTENTION --> Descending ordering
    }

    @Override
    public String toString() {
        return "TreePair{" +
                "t1=" + t1 +
                ", t2=" + t2 +
                ", score=" + score +
                '}';
    }

    private class SingleTaxon {
        final TreeNode insertionPoint;
        //        final Tree subtree;
        final Set<String> siblingLeaves;
        final Set<String> commonSiblingLeaves;
        final int numOfSiblings;

        public SingleTaxon(TreeNode insertionPoint, Set<String> siblingLeaves, Set<String> commonSiblingLeaves, int numOfSiblings) {
            this.insertionPoint = insertionPoint;
//            this.subtree = subtree;
            this.siblingLeaves = siblingLeaves;
            this.numOfSiblings = numOfSiblings;
            this.commonSiblingLeaves = commonSiblingLeaves;
        }

        public boolean isLeaf() {
            return insertionPoint.isLeaf();
        }

        public boolean isSubtree() {
            return insertionPoint.isInnerNode();
        }

    }
}
package scmAlgorithm.treeSelector;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
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
    public final double score;
    private Set<String> commonLeafes = null;
    private List<SingleTaxon> singleTaxa = null;

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
        singleTaxa = new ArrayList<>(t1.vertexCount() + t2.vertexCount()); // is an upper bound vor list --> no resizing
        pruneLeafes(t1);
        pruneLeafes(t2);
    }

    public void pruneToCommonLeafes(SingleTaxonReduction reducer) {
        reducer.modify(new ArrayList<>(Arrays.asList(t1, t2)));
    }

    // NOTE: single taxon reduction optimized for 2 trees with known common taxa
    private void pruneLeafes(Tree t) {

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
                Set<String> lcaLeaves = new HashSet<>();
                int numOfSiblings = 0;
                for (TreeNode sibling : node.getParent().children()) {
                    if (!sibling.equals(node)) {
                        numOfSiblings++;
                        lcaLeaves.addAll(TreeUtilsBasic.getLeafLabels(sibling));
                    }
                }

                if (node.isInnerNode()) {
                    t.removeSubtree(node);
                    st = new SingleTaxon(node, lcaLeaves, numOfSiblings);

                } else {
                    t.removeVertex(node);
                    st = new SingleTaxon(node, lcaLeaves, numOfSiblings);
                }

//                toRemove.add(node);
                singleTaxa.add(st);
                TreeUtilsBasic.pruneDegreeOneNodes(t, false, false);

            } else {
                for (TreeNode child : node.children()) {
                    stack.push(child);
                }
            }
        }
//        TreeUtilsBasic.removeSubtreeFromTree(toRemove, t, false, false);
    }

    public void reinsertSingleTaxa(Tree t) {
        Map<String, TreeNode> labelToNode = new THashMap<>();
        Set<TreeNode> alreadyInsertedLCAParent = new THashSet<>();
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
            if (singleTaxon.numOfSiblings == 1) {
                //check if parent was inserted from another treee --> build polytomi
                if (lcaParent != null) {
                    if (alreadyInsertedLCAParent.contains(lcaParent)) {//should work because new nodes from same tree cant be a parent because of sorting step, if not use 2 single taxa lists
                        lca = lcaParent;
                    } else {
                        TreeNode nuLcaParent = new TreeNode();
                        t.addVertex(nuLcaParent);
                        alreadyInsertedLCAParent.add(nuLcaParent);
                        t.addEdge(nuLcaParent, lca);
                        t.addEdge(lcaParent, nuLcaParent);
                        t.removeEdge(lcaParent, lca);
                        lca = nuLcaParent;
                    }
                } else {
                    TreeNode nuLcaParent = new TreeNode();
                    t.addVertex(nuLcaParent);
                    alreadyInsertedLCAParent.add(nuLcaParent);
                    t.addEdge(nuLcaParent, lca);
                    t.setRoot(nuLcaParent);
                    lca = nuLcaParent;
                }
            } else if (lca.isLeaf()) {
                lca = lcaParent;
            }

            TreeNode nuNode = singleTaxon.insertionPoint;
            t.addVertex(nuNode);
            t.addEdge(lca, nuNode);

            if (singleTaxon.isSubtree()) {
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
        Map<Set<String>,TreeNode> labelsToLCA = new HashMap<>(t2.vertexCount());

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
                    labelsToLCA.put(lcaToLabels.get(node),node);
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
                    lcaToLabels.put(p, new HashSet<>());

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

        if (rootEdge2 != null && rootEdge1 != null){
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
        final int numOfSiblings;

        public SingleTaxon(TreeNode insertionPoint, Set<String> siblingLeaves, int numOfSiblings) {
            this.insertionPoint = insertionPoint;
//            this.subtree = subtree;
            this.siblingLeaves = siblingLeaves;
            this.numOfSiblings = numOfSiblings;
        }

        public boolean isLeaf() {
            return insertionPoint.isLeaf();
        }

        public boolean isSubtree() {
            return insertionPoint.isInnerNode();
        }

    }
}
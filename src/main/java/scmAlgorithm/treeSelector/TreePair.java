package scmAlgorithm.treeSelector;

import epos.algo.consensus.ConsensusAlgorithm;
import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
import epos.model.tree.treetools.TreeUtilsBasic;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import scmAlgorithm.treeScorer.TreeScorer;

import java.util.*;

/**
 * Created by fleisch on 10.02.15.
 */
public class TreePair implements Comparable<TreePair> {
    public final static TreePair MIN_VALUE =  new TreePair(); //todo node are in several tree druring the resolution scorer?????????

    public final Tree t1;
    public final Tree t2;

    private Tree t1pruned;
    private Tree t2pruned;


    private Tree consensus = null;
    private int consensusNumOfTaxa = -1;

    private int backboneNumOfVertices = -1;

    public final double score;
    private Set<String> commonLeafes;

    private boolean first = true;
    private List<SingleTaxon> singleTaxa = null;
    private Map<Set<String>, Set<SingleTaxon>> commenInsertionPointTaxa = null;

    //just to create min value
    private TreePair(){
        t1 = null;
        t2 = null;
        score = Double.NEGATIVE_INFINITY;
    }

    public TreePair(final Tree t1, final Tree t2, TreeScorer scorer) {
        this.t1 = t1;
        this.t2 = t2;
        score = scorer.scoreTreePair(this);
    }


    //unchecked
    public Tree getPartner(Tree t) {
        if (t.equals(t1))
            return t2;
        else
            return t1;
    }

    public void setCommonLeafes(Set<String> commonLeafes) {
        this.commonLeafes = commonLeafes;
    }

    //uncheked and uncached
    private void pruneToCommonLeafes() {
        t1pruned = t1.cloneTree();
        t2pruned = t2.cloneTree();

        singleTaxa = new ArrayList<>(t1.vertexCount() + t2.vertexCount()); // is an upper bound for the list --> no resizing
        commenInsertionPointTaxa = new THashMap<>(t1.vertexCount() + t2.vertexCount());

        pruneLeafes(t1pruned);
        pruneLeafes(t2pruned);//todo directly and with if? so that we only clone if is is nessesary
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
                    st = new SingleTaxon(node, siblingLeaves, new THashSet<>(), numOfSiblings);
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

    private int reinsertSingleTaxa(Tree t) {
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
                if (singleTaxon.numOfSiblings == 1) {
                    //todo is there a faster/more elegant way to proof that
                    Set<String> s =  new THashSet<>(TreeUtilsBasic.getLeafLabels(lca));
                    s.retainAll(commonLeafes);
                    Set<String> s2 =  new HashSet<>(singleTaxon.siblingLeaves);
                    s2.retainAll(commonLeafes);
                    //todo remove --> DEBUG

                    if (s.equals(s2)){
                        if (lcaParent != null) { //check if we have to insert a new root
                            TreeNode nuLcaParent = new TreeNode();
                            t.addVertex(nuLcaParent);
                            t.addEdge(nuLcaParent, lca);
                            t.addEdge(lcaParent, nuLcaParent);
                            t.removeEdge(lcaParent, lca);
                            lca = nuLcaParent;

                        } else { //insert new root
                            TreeNode nuLcaParent = new TreeNode();
                            t.addVertex(nuLcaParent);
                            t.addEdge(nuLcaParent, lca);
                            t.setRoot(nuLcaParent);
                            lca = nuLcaParent;
                        }
                    }
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
                singles.clear();
            }
        }
        //calculate consensus resolution because we have the information now and do not want to iterate over the tree again.
        return  labelToNode.size();
    }

    public Tree getConsensus(final ConsensusAlgorithm consensorator){
        if (consensus == null)
            calculateConsensus(consensorator);
        return consensus;
    }

    public Tree getConsensus(){
        return consensus;
    }

    public void calculateConsensus(final ConsensusAlgorithm consensorator) {
        if (commonLeafes.size() > 2) {
            pruneToCommonLeafes();
            consensus = consensorator.getConsensusTree(t1pruned,t2pruned);
            backboneNumOfVertices =  consensus.vertexCount();
            consensusNumOfTaxa = reinsertSingleTaxa(consensus);
        }
    }

    //unchecked
    public int getNumOfConsensusTaxa() {
        return consensusNumOfTaxa;
    }
    public int getNumOfConsensusVertices() {
        return consensus.vertexCount();
    }
    //unchecked
    public int getNumOfBackboneTaxa() {
        return commonLeafes.size();
    }
    //unchecked
    public int getNumOfBackboneVertices() {
        return backboneNumOfVertices;
    }

    public Tree getT1pruned() {
        return t1pruned;
    }

    public Tree getT2pruned() {
        return t2pruned;
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
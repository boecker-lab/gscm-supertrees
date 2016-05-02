/*
 * GSCM-Project
 * Copyright (C)  2016. Chair of Bioinformatics, Friedrich-Schilller University Jena.
 *
 * This file is part of the GSCM-Project.
 *
 * The GSCM-Project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The GSCM-Project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GSCM-Project.  If not, see <http://www.gnu.org/licenses/>;.
 *
 */
package gscm.algorithm.treeMerger;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import phylo.tree.algorithm.consensus.Consensus;
import phylo.tree.model.tree.Tree;
import phylo.tree.model.tree.TreeNode;
import phylo.tree.model.tree.TreeUtils;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 10.02.15.
 */

/**
 * This class stores a Pair of trees and their scores
 * I does the pairwise merging (strict consensus merger) step
 * and caches the consensus tree and other interim result
 * depending on the given scoring function
 *
 * @author Markus Fleischauer (markus.fleischauer@gmail.com)
 * @since version 1.0
 */
class TreePair implements Comparable<TreePair> {
    final static TreePair MIN_VALUE = new TreePair();

    private final Consensus.ConsensusMethod consensusMethod;

    final Tree t1;
    final Tree t2;

    private Tree t1pruned;
    private Tree t2pruned;

    int t1prunedVertexCount = Integer.MIN_VALUE;
    int t2prunedVertexCount = Integer.MIN_VALUE;


    double score;
    double tieBreakingScore = 0d;


    Tree consensus = null;
    int backboneClades = -1; // number of the clades in the strict consensus of t1pruned and t2pruned before reinserting singe taxa.
    int consensusNumOfTaxa = -1;

    Set<String> commonLeafes;
    Map<Set<String>, Set<SingleTaxon>> commonInsertionPointTaxa = null; //this null value is indicator if first or second pruning step was done null=first notnull=second

    private List<SingleTaxon> singleTaxa = null; //this null value is indicator if trees are already pruned to common leafs


    //just to create min value
    private TreePair() {
        t1 = null;
        t2 = null;
        score = Double.NEGATIVE_INFINITY;
        consensusMethod = null;
    }

    TreePair(final Tree t1, final Tree t2, TreeScorer scorer, final Consensus.ConsensusMethod method) {
        this(t1, t2, method);
        calculateScore(scorer);
    }

    TreePair(final Tree t1, final Tree t2, final Consensus.ConsensusMethod method) {
        this.t1 = t1;
        this.t2 = t2;
        consensusMethod = method;
    }

    TreePair calculateScore(TreeScorer scorer) {
        scorer.scoreTreePair(this);
        return this;
    }

    //unchecked
    Tree getPartner(Tree t) {
        if (t.equals(t1))
            return t2;
        else if (t.equals(t2))
            return t1;
        return null;
    }

    void setCommonLeafes(Set<String> commonLeafes) {
        this.commonLeafes = commonLeafes;
    }

    //uncheked and uncached
    void pruneToCommonLeafes() {
        singleTaxa = new ArrayList<>(t1.vertexCount() + t2.vertexCount()); // is an upper bound for the list --> no resizing

        t1pruned = t1.cloneTree();
        t2pruned = t2.cloneTree();

        pruneLeafes(t1pruned);
        pruneLeafes(t2pruned);

        t1prunedVertexCount = t1pruned.vertexCount();
        t2prunedVertexCount = t2pruned.vertexCount();
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
                    maxSingleSubtreeRoots.removeAll(children); //we dont have to remove here, but surprisingly its good for the performance.
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
                        siblingLeaves.addAll(TreeUtils.getLeafLabels(sibling));
                    }
                }

                if (node.isInnerNode()) {
                    t.removeSubtree(node);
                } else {
                    t.removeVertex(node);
                }

                if (numOfSiblings == 1) {
                    Set<String> commonSiblingLeafes = new THashSet<>(siblingLeaves);
                    commonSiblingLeafes.retainAll(commonLeafes);
                    st = new SingleTaxon(node, siblingLeaves, commonSiblingLeafes, numOfSiblings);

                    Set<SingleTaxon> singles = commenInsertionPointTaxa.get(commonSiblingLeafes);
                    if (singles == null) {
                        if (this.commonInsertionPointTaxa == null) {
                            singles = new THashSet<>();
                            commenInsertionPointTaxa.put(commonSiblingLeafes, singles);
                            singles.add(st);
                        } else {
                            singles = this.commonInsertionPointTaxa.get(commonSiblingLeafes);
                            if (singles != null) {
                                commenInsertionPointTaxa.put(commonSiblingLeafes, singles);
                                singles.add(st);
                            }
                        }
                    } else {
                        singles.add(st);
                    }
                } else {
                    st = new SingleTaxon(node, siblingLeaves, new THashSet<>(), numOfSiblings);
                }
                singleTaxa.add(st);

                TreeUtils.pruneDegreeOneNodes(t, false, false);
            } else {
                for (TreeNode child : node.children()) {
                    stack.push(child);
                }
            }
        }

        //this is to do the switch between first and second tree mode
        this.commonInsertionPointTaxa = commenInsertionPointTaxa;

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
            Set<SingleTaxon> singles = commonInsertionPointTaxa.get(singleTaxon.commonSiblingLeaves);
            if (singles == null) {
                singles = new THashSet<>(1);
                singles.add(singleTaxon);
            }


            //build insertion point for first occurence on this path...
            if (!singles.isEmpty()) {//if empty the taxon has already been inserted before.
                if (singleTaxon.numOfSiblings == 1) {
                    //todo is there a faster/more elegant way to proof that?
                    Set<String> s = new THashSet<>(TreeUtils.getLeafLabels(lca));
                    s.retainAll(commonLeafes);
                    Set<String> s2 = new HashSet<>(singleTaxon.siblingLeaves);
                    s2.retainAll(commonLeafes);

                    if (s.equals(s2)) {
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
        return labelToNode.size();
    }

    Tree getConsensus() {
        if (consensus == null)
            calculateConsensus();
        return consensus;
    }

    void calculateConsensus() {
        if (commonLeafes.size() > 2) {
            if (singleTaxa == null)
                pruneToCommonLeafes();
            consensus = Consensus.getConsensus(Arrays.asList(t1pruned, t2pruned), consensusMethod);
            backboneClades = consensus.vertexCount() - commonLeafes.size();
            consensusNumOfTaxa = reinsertSingleTaxa(consensus);
            t1pruned=null;
            t2pruned=null;

        } else {
            Logger.getGlobal().warning("Trees have nothing in common!");
        }
    }

    boolean isInsufficient() {
        return score <= Double.NEGATIVE_INFINITY;
    }

    @Override
    public int compareTo(TreePair o) {
        int comp = Double.compare(o.score, score); //ATTENTION --> Descending ordering
        if (comp == 0) {
            comp = Double.compare(o.tieBreakingScore, tieBreakingScore);
        }
        return comp;
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
        final Set<String> siblingLeaves;
        final Set<String> commonSiblingLeaves;
        final int numOfSiblings;

        private SingleTaxon(TreeNode insertionPoint, Set<String> siblingLeaves, Set<String> commonSiblingLeaves, int numOfSiblings) {
            this.insertionPoint = insertionPoint;
            this.siblingLeaves = siblingLeaves;
            this.numOfSiblings = numOfSiblings;
            this.commonSiblingLeaves = commonSiblingLeaves;
        }

        private boolean isLeaf() {
            return insertionPoint.isLeaf();
        }

        private boolean isSubtree() {
            return insertionPoint.isInnerNode();
        }

    }
}
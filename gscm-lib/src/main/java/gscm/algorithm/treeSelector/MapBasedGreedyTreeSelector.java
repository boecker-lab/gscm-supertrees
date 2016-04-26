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
package gscm.algorithm.treeSelector;

import phyloTree.model.tree.Tree;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 06.02.15.
 */
public abstract class MapBasedGreedyTreeSelector<M extends Map<Tree, S>, S extends Collection<TreePair>> extends BasicTreeSelector {
    M treeToPairs;


    protected MapBasedGreedyTreeSelector(ConsensusMethod method) {
        super(method);
    }
    protected MapBasedGreedyTreeSelector() {
        super();
    }

    @Override
    public int getNumberOfRemainingTrees() {
        return treeToPairs.size();
    }

    @Override
    public void init() {
        if (clearScorer)
            clearScorer();

        treeToPairs = getTreeToPairsInstance(inputTrees.length - 1);
        for (Tree tree : inputTrees) {
            treeToPairs.put(tree, getTreePairCollectionInstance(inputTrees.length - 1));
        }

        createPairs();
    }

    @Override
    Collection<Tree> getRemainingTrees() {
        return new LinkedList<>(treeToPairs.keySet());
    }

    //(O(2nlog(n))
    @Override
    void removeTreePair(TreePair pair) {
        Tree t1 = pair.t1; //O(1)
        Tree t2 = pair.t2; //O(1)
        removePair(t1, pair);
        removePair(t2, pair);
        S s1 = treeToPairs.remove(t1); //O(1)
        S s2 = treeToPairs.remove(t2); //O(1)
        //O(n)
        for (TreePair treePair : s1) {
            removePair(treePair.getPartner(t1), treePair);//O(log(n))
        }
        //O(n)
        for (TreePair treePair : s2) {
            removePair(treePair.getPartner(t2), treePair);//O(log(n))
        }
    }

    void addTreePair(Tree t, TreePair p) {
        S pairs = treeToPairs.get(t);
        if (pairs == null) {
            //add new to map O(1)
            pairs = getTreePairCollectionInstance(treeToPairs.size());
            treeToPairs.put(t, pairs);
        }
        pairs.add(p);
    }

    void removePair(Tree t, TreePair p) {
        treeToPairs.get(t).remove(p);
    }

    //abstract classes
    abstract M getTreeToPairsInstance(int size);

    abstract S getTreePairCollectionInstance(int size);
}


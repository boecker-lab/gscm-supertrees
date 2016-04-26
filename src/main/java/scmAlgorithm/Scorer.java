package scmAlgorithm;

import phyloTree.model.tree.Tree;

import java.util.List;

/**
 * Created by Anika on 13.05.2015.
 */
public abstract class Scorer {

    abstract double getScore(Tree one, Tree two);
    abstract List<Tree> getTreeswithbiggestScoreandSCM(List<Tree> treelist);
}

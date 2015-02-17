package scmAlgorithm;

import epos.model.tree.Tree;
import scmAlgorithm.treeSelector.TreeSelector;

import java.util.List;

/**
 * Created by fleisch on 05.02.15.
 */
public abstract class AbstractSCMAlgorithm{
    protected List<Tree> superTrees;

    protected TreeSelector selector;

    public AbstractSCMAlgorithm(TreeSelector selector) {
        this.selector = selector;
    }


    protected abstract void run();

    public List<Tree> getSupertrees() {
        if (superTrees == null || superTrees.isEmpty())
            run();
        return superTrees;
    }

    public Tree getSupertree() {
        return getSupertrees().get(0);
    }


    public static void main(String[] args){


    }

}

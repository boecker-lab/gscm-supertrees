package scm.algorithm.treeSelector;

/**
 * Created by fleisch on 22.10.15.
 */
@FunctionalInterface
public interface TreeSelectorFactory<T extends TreeSelector> {
    T newTreeSelectorInstance();
}

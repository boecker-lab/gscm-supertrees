package treeUtils;

import junit.framework.TestCase;
import org.junit.Test;
import phyloTree.io.Newick;
import phyloTree.model.tree.Tree;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TreeEqualsTest extends TestCase {

    @Test
    public void testCompareTreeStructure() throws Exception {
        List<Tree> alltrees = new ArrayList();
        InputStream in = TreeEqualsTest.class.getResourceAsStream("/500_50_sourceTree_sm.0.sourceTrees_OptSCM-Rooting.tre");
        BufferedReader re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));

        Tree tree = alltrees.get(1);
        Tree copy = tree.cloneTree();
        TreeEquals t = new TreeEquals();
        assertTrue(t.getTreeEquals(tree, copy));

        Tree one = Newick.getTreeFromString("(a:1.0,b:1.0,c:1.0,d:1.0);");
        Tree two = Newick.getTreeFromString("(a:1.0,d:1.0,b:1.0,c:1.0);");
        assertTrue(t.getTreeEquals(one, two));

        one = Newick.getTreeFromString("((a:1.0,b:1.0):1.0,(e:1.0,f:1.0,g:1.0):1.0);");
        two = Newick.getTreeFromString("((e:1.0,f:1.0,g:1.0):1.0,(b:1.0,a:1.0):1.0);");
        assertTrue(t.getTreeEquals(one, two));

    }
}
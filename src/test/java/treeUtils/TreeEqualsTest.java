package treeUtils;

import epos.model.tree.Tree;
import epos.model.tree.io.Newick;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class TreeEqualsTest extends TestCase {

    @Test
    public void testCompareTreeStructure() throws Exception {
        //File fi = new File ("C:\\Eigene Dateien\\Studium\\7. Semester\\Bachelorarbeit\\SMIDGen_Anika\\500\\50\\Source_Trees\\RaxML\\sm.0.sourceTrees_OptSCM-Rooting.tre");

        List<Tree> alltrees = new ArrayList();

        InputStream in = TreeEqualsTest.class.getResourceAsStream("/500_50_sourceTree_sm.0.sourceTrees_OptSCM-Rooting.tre");
        BufferedReader re = new BufferedReader(new InputStreamReader(in));
        alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));

        Tree tree = alltrees.get(1);
        Tree copy = tree.cloneTree();
        TreeEquals t = new TreeEquals();
        assertTrue(t.getTreeEquals(tree, copy));
    }
}
package scm.cli;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import phyloTree.io.TreeFileUtils;
import phyloTree.model.tree.Tree;
import scm.algorithm.treeSelector.TreeScorers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by fleisch on 01.12.15.
 */
@RunWith(Parameterized.class)
public class gscmTest {
    public static final Path nexIn = Paths.get(gscmTest.class.getResource("/scm.cli/sm.11.sourceTrees_OptSCM-Rooting.nex").getFile());
    public static final Path newIn =  Paths.get(gscmTest.class.getResource("/scm.cli/sm.11.sourceTrees_OptSCM-Rooting.tre").getFile());
    public static final Path out =  newIn.getParent().resolve("outFile");
    GSCMLauncher scm;
    String[] args;
    Path resultFile;
    private int numOfTrees;

    public gscmTest(String[] args, Path result, int  numOfTrees) {
        this.scm = new GSCMLauncher();
        this.args = args;
        this.resultFile = result;
        this.numOfTrees =  numOfTrees;
    }

    @Parameterized.Parameters
    public static List<Object[]> data() {
        List<Object[]> paras = new LinkedList<>();
        paras.add(new Object[]{
                new String[]{"-d", TreeFileUtils.FileType.NEWICK.toString(), "-o", out.toString(),  nexIn.toString()},
                out,
                1
        });
        paras.add(new Object[]{
                new String[]{"-d", TreeFileUtils.FileType.NEWICK.toString(),"-o", out.toString(),  newIn.toString()},
                out,
                1
        });

        paras.add(new Object[]{
                new String[]{"-s", TreeScorers.ScorerType.UNIQUE_TAXA.toString(),TreeScorers.ScorerType.OVERLAP.toString(), "-d", TreeFileUtils.FileType.NEWICK.toString(),"-o", out.toString(),  newIn.toString()},
                out,
                1
        });

        paras.add(new Object[]{
                new String[]{"-d", TreeFileUtils.FileType.NEWICK.toString(),"-O", out.toString(),  newIn.toString()},
                out,
                1
        });
        paras.add(new Object[]{
                new String[]{"-s", TreeScorers.ScorerType.UNIQUE_TAXA.toString(),TreeScorers.ScorerType.OVERLAP.toString(), "-d", TreeFileUtils.FileType.NEWICK.toString(),"-O", out.toString(),  newIn.toString()},
                out,
                3
        });
        return paras;
    }


    @Test
    public void testLauncher() {
        scm.main(args);
        assertTrue(Files.exists(resultFile));
        try {
            Tree[] trees = TreeFileUtils.parseFileToTrees(resultFile, TreeFileUtils.FileType.NEWICK);
            assertNotNull(trees);
            assertEquals(numOfTrees,trees.length);
        } catch (IOException e) {
            e.printStackTrace();
            assertNull(e);
        } finally {
            try {
                Files.deleteIfExists(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

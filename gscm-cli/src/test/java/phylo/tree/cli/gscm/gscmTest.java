package phylo.tree.cli.gscm;

import phylo.tree.algorithm.gscm.treeSelector.TreeScorers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import phylo.tree.io.TreeFileUtils;
import phylo.tree.model.Tree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 01.12.15.
 */
@RunWith(Parameterized.class)
public class gscmTest {
    public static final Path nexIn = Paths.get(gscmTest.class.getResource("/gscm/cli/sm.11.sourceTrees_OptSCM-Rooting.nex").getFile());
    public static final Path newIn = Paths.get(gscmTest.class.getResource("/gscm/cli/sm.11.sourceTrees_OptSCM-Rooting.tre").getFile());
    public static final Path badIn = Paths.get(gscmTest.class.getResource("/gscm/cli/simpleInsufficient.tre").getFile());
    public static final Path out = newIn.getParent().resolve("outFile");
    public static final Path time = newIn.getParent().resolve("timeFile");
    String[] args;
    Path resultFile;
    Path timeFile = null;
    private int numOfTrees;

//    @Rule
//    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    public gscmTest(String[] args, Path result, Path time, int numOfTrees) {
        this.args = args;
        this.resultFile = result;
        timeFile = time;
        this.numOfTrees = numOfTrees;
    }

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Parameterized.Parameters
    public static List<Object[]> data() {
        List<Object[]> paras = new LinkedList<>();
        paras.add(new Object[]{
                new String[]{"-d", TreeFileUtils.FileType.NEWICK.toString(), "-o", out.toString(), badIn.toString()},
                out,
                null,
                0
        });
        paras.add(new Object[]{
                new String[]{"-d", TreeFileUtils.FileType.NEWICK.toString(), "-o", out.toString(), nexIn.toString()},
                out,
                null,
                1
        });
        paras.add(new Object[]{
                new String[]{"-d", TreeFileUtils.FileType.NEWICK.toString(), "-F", time.toString(), "-o", out.toString(), nexIn.toString()},
                out,
                time,
                1
        });
        paras.add(new Object[]{
                new String[]{"-d", TreeFileUtils.FileType.NEWICK.toString(), "-o", out.toString(), newIn.toString()},
                out,
                null,
                1
        });

        paras.add(new Object[]{
                new String[]{"-s", TreeScorers.ScorerType.UNIQUE_TAXA.name(), TreeScorers.ScorerType.OVERLAP.name(), "-d", TreeFileUtils.FileType.NEWICK.toString(), "-o", out.toString(), newIn.toString()},
                out,
                null,
                1
        });

        paras.add(new Object[]{
                new String[]{"-s", TreeScorers.ScorerType.OVERLAP.toString(), "-d", TreeFileUtils.FileType.NEWICK.toString(), "-o", out.toString(), newIn.toString()},
                out,
                null,
                1
        });

        paras.add(new Object[]{
                new String[]{"-B", "-s", TreeScorers.ScorerType.OVERLAP.toString(), "-d", TreeFileUtils.FileType.NEWICK.toString(), "-o", out.toString(), newIn.toString()},
                out,
                null,
                1
        });

        paras.add(new Object[]{
                new String[]{"-d", TreeFileUtils.FileType.NEWICK.toString(), "-O", out.toString(), newIn.toString()},
                out,
                null,
                1
        });
        paras.add(new Object[]{
                new String[]{"-s", TreeScorers.ScorerType.UNIQUE_TAXA.toString(), TreeScorers.ScorerType.OVERLAP.toString(), "-d", TreeFileUtils.FileType.NEWICK.toString(), "-O", out.toString(), newIn.toString()},
                out,
                null,
                3
        });
        paras.add(new Object[]{
                new String[]{"-r", "-s", TreeScorers.ScorerType.UNIQUE_TAXA.toString(), TreeScorers.ScorerType.OVERLAP.toString(), "-d", TreeFileUtils.FileType.NEWICK.toString(), "-O", out.toString(), newIn.toString()},
                out,
                null,
                15
        });

        paras.add(new Object[]{
                new String[]{"-R", "10", "-s", TreeScorers.ScorerType.UNIQUE_TAXA.toString(), TreeScorers.ScorerType.OVERLAP.toString(), "-d", TreeFileUtils.FileType.NEWICK.toString(), "-O", out.toString(), newIn.toString()},
                out,
                null,
                23
        });

        paras.add(new Object[]{
                new String[]{"-B", "-R", "10", "-s", TreeScorers.ScorerType.UNIQUE_TAXA.toString(), TreeScorers.ScorerType.OVERLAP.toString(), "-d", TreeFileUtils.FileType.NEWICK.toString(), "-O", out.toString(), newIn.toString()},
                out,
                null,
                23
        });
        return paras;
    }


    @Test
    public void testLauncher() {
        if (numOfTrees > 0) {
            exit.expectSystemExitWithStatus(0);
            GSCMLauncher.main(args);
            assertTrue(Files.exists(resultFile));
            try {
                Tree[] trees = TreeFileUtils.parseFileToTrees(resultFile, TreeFileUtils.FileType.NEWICK);
                assertNotNull(trees);
                assertEquals(numOfTrees, trees.length);
            } catch (IOException e) {
                e.printStackTrace();
                assertNull(e);
            } finally {

                try {
                    Files.deleteIfExists(out);
                    Files.deleteIfExists(time);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            //bad tree
            exit.expectSystemExitWithStatus(2);
            GSCMLauncher.main(args);
            assertTrue(Files.notExists(resultFile));

        }
    }


}

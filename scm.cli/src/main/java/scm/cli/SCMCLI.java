package scm.cli;

import cli.EnumArrayOptionHandler;
import cli.Progressbar;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.Setter;
import phyloTree.SupertreeAlgortihmCLI;
import phyloTree.model.tree.Tree;
import scm.algorithm.AbstractSCMAlgorithm;
import scm.algorithm.GreedySCMAlgorithm;
import scm.algorithm.MultiGreedySCMAlgorithm;
import scm.algorithm.RandomizedSCMAlgorithm;
import scm.algorithm.treeSelector.TreeScorers;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by fleisch on 20.11.15.
 */
public class SCMCLI extends SupertreeAlgortihmCLI<AbstractSCMAlgorithm> implements Progressbar {


    public static class ScorerTypeArrayOptionHandler extends EnumArrayOptionHandler<TreeScorers.ScorerType>{
        public ScorerTypeArrayOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super TreeScorers.ScorerType> setter) {
            super(parser, option, setter, TreeScorers.ScorerType.class);
        }
    }

    @Option(name = "-s", aliases = {"--scorer"}, usage = "set of scores that should be used. standard scm can use only one", handler = ScorerTypeArrayOptionHandler.class)
    private  TreeScorers.ScorerType[] scorerTypes =  new TreeScorers.ScorerType[]{TreeScorers.ScorerType.UNIQUE_TAXA};

    @Option(name = "-r", aliases = {"--randomized"}, usage = "Enables randomization (standard iterations are numberOfTrees^2 per scoring)")
    private void setRandomized(boolean r){
        if (r){
            randomIterations = 0;
        }else {
            randomIterations = -1;
        }
    }

    @Option(name = "-R", aliases = {"--randomIterations"}, usage = "Enables randomization and specifies the number of iterations per scoring", forbids = "-r")
    private void setRandomIterations(int iter){
        if (iter < 0){
            randomIterations = -1;
        }else{
            randomIterations = iter;
        }
    }
    private int randomIterations = -1;

    @Option(name = "-O", aliases = {"--fullOutput"}, usage = "Appends the unmerged trees of all scorers and random iterations to the output file",forbids = "-o")
    private void setFullOutput(Path output){
        this.output =  output;
        appendUnmerged =  true;
    }
    private boolean appendUnmerged = false;

    public void writeOutput(Tree primaryResult, List<Tree> multiTreeList) throws IOException {
        List<Tree> results;
        if (appendUnmerged && multiTreeList.size()>1){
            results =  new ArrayList<>(multiTreeList.size() + 1);
            results.add(primaryResult);
            results.addAll(multiTreeList);
        }else{
            results =  Arrays.asList(primaryResult);
        }
        writeOutput(results);
    }

    public AbstractSCMAlgorithm getAlgorithmInstance(){
        AbstractSCMAlgorithm algo = null;
        if (randomIterations < 0){
            //non random
            if (scorerTypes.length > 1){
                //multi
                algo =  new MultiGreedySCMAlgorithm(TreeScorers.getScorerArray(isMultiThreaded(),scorerTypes));
            }else{
                //standard
                algo =  new GreedySCMAlgorithm(TreeScorers.getScorer(isMultiThreaded(),scorerTypes[0]));
            }
        }else {
            //randomized
            algo =  new RandomizedSCMAlgorithm(randomIterations, TreeScorers.getScorerArray(isMultiThreaded(),scorerTypes));
        }
        setParameters(algo);
        return algo;
    }

    @Override
    public String name() {
        return "gscm";
    }

    @Override
    protected void printUsage(PrintStream stream) {
        stream.println("Usage:");
        stream.println(" " + name() + " [options...] INPUT_TREES_FILE");
        stream.println("    The only required argument is the input tree file in newick/nexus format");
        stream.println();
    }




}

package scm.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import phyloTree.model.tree.Tree;
import scm.algorithm.AbstractSCMAlgorithm;

import java.io.IOException;
import java.util.List;

/**
 * Created by fleisch on 24.11.15.
 */
public class GSCMLauncher {
    private static final SCMCLI CLI = new SCMCLI();

    public static void main(String[] args) {
        double runtime = System.currentTimeMillis();
        final CmdLineParser parser = new CmdLineParser(CLI);

        try {
            // parse the arguments.
            parser.parseArgument(args);

            // configure algorithm
            AbstractSCMAlgorithm algorithm = CLI.getAlgorithmInstance();

            //parse input
            List<Tree> inputTrees = CLI.parseInput();
            algorithm.setInput(inputTrees);

            //starting calculation
            algorithm.run();

            //return results
            List<Tree> supertrees =  algorithm.getResults();
            Tree merged =  algorithm.getResult();

            //write them to file
            CLI.writeOutput(merged,supertrees);


        } catch (CmdLineException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}

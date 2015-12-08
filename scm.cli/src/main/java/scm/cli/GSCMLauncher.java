package scm.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.InterfaceCmdLineParser;
import phyloTree.model.tree.Tree;
import scm.algorithm.AbstractSCMAlgorithm;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fleisch on 24.11.15.
 */
public class GSCMLauncher {
//    private static Path PROPERTIES_FILE = Paths.get(GSCMLauncher.class.getResource("/application.properties").getFile());
    private static SCMCLI CLI;

    public static void main(String[] args) {
        CLI = new SCMCLI(SCMCLI.DEFAULT_PROPERTIES_FILE);
        double startTime = System.currentTimeMillis();
        CLI.LOGGER.info("Start calculation with following parameters: " + Arrays.toString(args));
        final CmdLineParser parser = new InterfaceCmdLineParser(CLI);

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
            CLI.writeOutput(merged, supertrees);

            double calcTime = (System.currentTimeMillis() - startTime)/1000d;
            CLI.LOGGER.info("Supertree calculation Done in: " + calcTime + "s");

        } catch (CmdLineException e) {
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            CLI.LOGGER.severe(e.getMessage());
            System.err.println();
            System.err.println();
            CLI.printHelp(parser, System.out);

            return;
        } catch (IOException e) {
            CLI.LOGGER.severe(e.getMessage());
            System.err.println();
            System.err.println();
            CLI.printHelp(parser, System.out);
        }


    }

}

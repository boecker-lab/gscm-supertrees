package scm.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.InterfaceCmdLineParser;
import phyloTree.model.tree.Tree;
import scm.algorithm.SCMAlgorithm;
import scm.algorithm.treeSelector.InsufficientOverlapException;
import scm.algorithm.treeSelector.TreeSelectorFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fleisch on 24.11.15.
 */
public class GSCMLauncher {
    private static SCMCLI CLI;

    public static void main(String[] args) {
        CLI = new SCMCLI(SCMCLI.DEFAULT_PROPERTIES_FILE);
        double startTime = System.currentTimeMillis();
        CLI.LOGGER.info("Start calculation with following parameters: " + Arrays.toString(args));
        final CmdLineParser parser = new InterfaceCmdLineParser(CLI);

        try {
            // parse the arguments.
            parser.parseArgument(args);
            // check for help
            if (CLI.isHelp() || CLI.isFullHelp()) {
                CLI.printHelp(parser);
                System.exit(0);
            }

            // configure algorithm
            SCMAlgorithm algorithm = CLI.getAlgorithmInstance();

            //parse input
            List<Tree> inputTrees = CLI.parseInput();
            try {
                algorithm.setInput(inputTrees);

                //starting calculation
                algorithm.call();

                //return results
                List<Tree> supertrees = algorithm.getResults();
                Tree merged = algorithm.getResult();

                //write them to file
                CLI.writeOutput(merged, supertrees);

                double calcTime = (System.currentTimeMillis() - startTime) / 1000d;
                CLI.LOGGER.info("Supertree calculation Done in: " + calcTime + "s");
                Path timeFile = CLI.getRuntimeFile();
                if (timeFile != null) {
                    Files.deleteIfExists(timeFile);
                    Files.write(timeFile, ("gscm " + Double.toString(calcTime) + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE_NEW);
                }
            } catch (InsufficientOverlapException e) {
                CLI.LOGGER.severe(e.getMessage());
                CLI.LOGGER.fine(e.getStackTrace().toString());
                System.exit(2);
            } finally {
                algorithm.shutdown(); //shut executor services of algorithm down
                TreeSelectorFactory.shutdownAll();//shut all tree selectors and their executors services down
                System.exit(0);
            }
        } catch (CmdLineException e) {
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            CLI.LOGGER.severe(e.getMessage());
            System.err.println();
            System.err.println();
            CLI.printHelp(parser, System.out);
            System.exit(1);

        } catch (IOException e) {
            CLI.LOGGER.severe(e.getMessage());
            CLI.LOGGER.config(e.getMessage());
            System.err.println();
            System.err.println();
            CLI.printHelp(parser, System.out);
            System.exit(1);
        } catch (Exception e) {
            CLI.LOGGER.severe(e.getMessage());
            CLI.LOGGER.severe(e.getStackTrace().toString());
            System.exit(-1);
        }
    }
}

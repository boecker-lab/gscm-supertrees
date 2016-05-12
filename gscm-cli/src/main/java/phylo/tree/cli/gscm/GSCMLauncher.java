/*
 * GSCM-Project
 * Copyright (C)  2016. Chair of Bioinformatics, Friedrich-Schilller University Jena.
 *
 * This file is part of the GSCM-Project.
 *
 * The GSCM-Project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The GSCM-Project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GSCM-Project.  If not, see <http://www.gnu.org/licenses/>;.
 *
 */
package phylo.tree.cli.gscm;

import core.cli.BasicAlgorithmCLI;
import phylo.tree.algorithm.gscm.SCMAlgorithm;
import phylo.tree.algorithm.gscm.treeSelector.TreeSelectorFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.InterfaceCmdLineParser;
import phylo.tree.algorithm.exceptions.InsufficientOverlapException;
import phylo.tree.model.Tree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 24.11.15.
 */
public class GSCMLauncher {

    public static void main(String[] args) {
        SCMCLI CLI = new SCMCLI(SCMCLI.DEFAULT_PROPERTIES_FILE);
        double startTime = System.currentTimeMillis();
        BasicAlgorithmCLI.LOGGER.info("Start calculation with following parameters: " + Arrays.toString(args));
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
                BasicAlgorithmCLI.LOGGER.info("Supertree calculation Done in: " + calcTime + "s");
                Path timeFile = CLI.getRuntimeFile();
                if (timeFile != null) {
                    Files.deleteIfExists(timeFile);
                    Files.write(timeFile, ("gscm " + Double.toString(calcTime) + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE_NEW);
                }
            } catch (InsufficientOverlapException e) {
                BasicAlgorithmCLI.LOGGER.severe(e.getMessage());
                e.printStackTrace();
                System.exit(2);
            } finally {
                algorithm.shutdown(); //shut executor services of algorithm down
                TreeSelectorFactory.shutdownAll();//shut all tree selectors and their executors services down
            }
        } catch (CmdLineException e) {
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            BasicAlgorithmCLI.LOGGER.severe(e.getMessage());
            System.err.println();
            System.err.println();
            CLI.printHelp(parser, System.out);
            System.exit(1);

        } catch (IOException e) {
            BasicAlgorithmCLI.LOGGER.severe(e.getMessage());
            BasicAlgorithmCLI.LOGGER.config(e.getMessage());
            System.err.println();
            System.err.println();
            CLI.printHelp(parser, System.out);
            System.exit(1);
        } catch (Exception e) {
            BasicAlgorithmCLI.LOGGER.severe(e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

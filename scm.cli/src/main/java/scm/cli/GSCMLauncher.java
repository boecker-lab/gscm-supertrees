package scm.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

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


        } catch (CmdLineException e) {
            e.printStackTrace();
        }


    }

}

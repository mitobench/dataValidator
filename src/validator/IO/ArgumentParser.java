package validator.IO;

import org.apache.commons.cli.*;

public class ArgumentParser {


    private String[] args;
    private static final String CLASS_NAME = "User option parser";
    private String meta;
    private String fasta;
    private String out;
    private String dir;


    public ArgumentParser(String[] args) {
        this.args = args;
        parse();
    }


    private void parse() {
        Options helpOptions = new Options();
        helpOptions.addOption("h", "help", false, "show this help page");
        Options options = new Options();
        options.addOption("h", "help", false, "show this help page");
        options.addOption(OptionBuilder.withLongOpt("input")
                .withArgName("INPUT")
                .withDescription("The input meta data file (csv file)")
                .hasArg()
                .create("i"));
        options.addOption(OptionBuilder.withLongOpt("fasta")
                .withArgName("FASTA")
                .withDescription("The fasta file with MT DNA sequences")
                .hasArg()
                .create("f"));
        options.addOption(OptionBuilder.withLongOpt("output")
                .withArgName("OUTPUT")
                .withDescription("Path to output folder.")
                .hasArg()
                .create("o"));
        options.addOption(OptionBuilder.withLongOpt("input_directory")
                .withArgName("INPUT_DIRECTORY")
                .withDescription("Path to input directory containing several meta-info and sequence file pairs.")
                .hasArg()
                .create("d"));


        HelpFormatter helpformatter = new HelpFormatter();
        CommandLineParser parser = new BasicParser();

        if (args.length < 2) {
            helpformatter.printHelp(CLASS_NAME, options);
            System.exit(0);
        }

        try {
            CommandLine cmd = parser.parse(helpOptions, args);
            if (cmd.hasOption('h')) {
                helpformatter.printHelp(CLASS_NAME, options);
                System.exit(0);
            }
        } catch (ParseException e1) {
        }

        try {
            CommandLine cmd = parser.parse(options, args);

            // input files

            if (cmd.hasOption('i')) {
                meta = cmd.getOptionValue('i');
            }
            if (cmd.hasOption('f')) {
                fasta = cmd.getOptionValue('f');
            }
            if (cmd.hasOption('o')) {
                out = cmd.getOptionValue('o');
            }
            if (cmd.hasOption('d')) {
                dir = cmd.getOptionValue('d');
            }

        } catch (ParseException e) {
            helpformatter.printHelp(CLASS_NAME, options);
            System.err.println(e.getMessage());
            System.exit(0);
        }
    }


    public String getMeta() {
        return meta;
    }

    public String getFasta() {
        return fasta;
    }

    public String getOut() {
        return out;
    }

    public String getDir() {
        return dir;
    }
}

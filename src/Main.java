import IO.ArgumentParser;
import IO.FastaReader;
import calculations.Validator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class Main {


    private static String mt_sequences_filepath;


    public static void main(String[] args) throws Exception {



        // init functionality
        ArgumentParser optionsParser = new ArgumentParser(args);
        Validator validator = new Validator();
        System.out.println("Reading fast file...");

        mt_sequences_filepath = optionsParser.getFasta();
        String[] fileName = mt_sequences_filepath.replaceFirst("[.][^.]+$", "").split("/");
        String fileNameWithoutExt = fileName[fileName.length-1];
        String data_template_filepath = optionsParser.getMeta();

        // init writer
        BufferedWriter logfile = new BufferedWriter(new FileWriter(fileNameWithoutExt +"_logfile.txt"));

        FastaReader fastaReader = new FastaReader(mt_sequences_filepath);
        List<String> fastaheaders = fastaReader.getDescription();

        logfile.write("Data validation report based on files:\n" + mt_sequences_filepath + "\n"+ data_template_filepath + "\n\n");

        System.out.println("Running validation...");

        validator.validate(data_template_filepath, fastaheaders, fastaReader.getLog_sequence_corretness(), mt_sequences_filepath);
        logfile.close();

        System.out.println("You can check the logfile now: \n" + new File(fileNameWithoutExt +"_logfile.txt").getAbsolutePath());


    }
}

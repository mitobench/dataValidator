import IO.ArgumentParser;
import IO.FastaReader;
import calculations.Validator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class Main {


    private static String mt_sequences_filepath;


    public static void main(String[] args) throws Exception {



        // init functionality
        ArgumentParser optionsParser = new ArgumentParser(args);
        Validator validator = new Validator();

        mt_sequences_filepath = optionsParser.getFasta();
        String[] fileName = mt_sequences_filepath.replaceFirst("[.][^.]+$", "").split("/");
        String fileNameWithoutExt = fileName[fileName.length-1];
        String data_template_filepath = optionsParser.getMeta();

        // init writer
        BufferedWriter logfile = new BufferedWriter(new FileWriter(fileNameWithoutExt +"_logfile.txt"));


        FastaReader fastaReader = new FastaReader(mt_sequences_filepath);
        List<String> fastaheaders = fastaReader.getDescription();

        validator.validate(data_template_filepath, logfile, fastaheaders);

        logfile.close();


    }
}

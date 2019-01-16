import IO.ArgumentParser;
import IO.FastaReader;
import calculations.Validator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {


    private static HashMap<String, String> header_to_fastaSequence_map = null;
    private static String mt_sequences_filepath;
    private static ArrayList<String> header_splitted_list;


    public static void main(String[] args) throws Exception {


        // init writer
        BufferedWriter logfile = new BufferedWriter(new FileWriter("logfile.txt"));

        // init functionality
        ArgumentParser optionsParser = new ArgumentParser(args);
        Validator validator = new Validator();

        mt_sequences_filepath = optionsParser.getFasta();
        String[] fileName = mt_sequences_filepath.replaceFirst("[.][^.]+$", "").split("/");
        String fileNameWithoutExt = fileName[fileName.length-1];
        String data_template_filepath = optionsParser.getMeta();

        header_to_fastaSequence_map = new HashMap<>();
        FastaReader fastaReader = new FastaReader(mt_sequences_filepath);
        List<String> fastaheaders = fastaReader.getDescription();

        validator.validate(data_template_filepath, logfile, fastaheaders);

        logfile.close();


    }
}

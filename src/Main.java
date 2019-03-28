import uploadGenerator.UploadRunner;
import validator.IO.ArgumentParser;
import validator.IO.FastaReader;
import validator.calculations.Validator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {


    private static String mt_sequences_filepath;
    private static Validator validator;


    public static void main(String[] args) throws IOException {


        // init functionality
        ArgumentParser optionsParser = new ArgumentParser(args);
        validator = new Validator();

        mt_sequences_filepath = optionsParser.getFasta();
        String data_template_filepath = optionsParser.getMeta();

        String out = optionsParser.getOut();
        if(!out.equals("")){
            out = out + File.separator;
        }

        String dir = optionsParser.getDir();
        // create folder for log files
        Files.createDirectories(new File(out + "logfiles" + File.separator).toPath());

        if(dir != null && Files.isDirectory(new File(dir).toPath())){
            Set<String> filenames = new HashSet<>();
            filenames = getFilenames(dir, filenames);
            int file_counter = 1;
            for(String s : filenames){
                try {
                    System.out.println("Processing file " + file_counter + "/" + filenames.size());
                    run(s + ".fasta",s + ".csv", out);
                    file_counter++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {


            try {
                run(mt_sequences_filepath, data_template_filepath, out);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }



    public static void run(String mt_sequences_filepath, String data_template_filepath, String out) throws Exception {

        System.out.println("Reading file " +  mt_sequences_filepath);

        String[] fileName = mt_sequences_filepath.replaceFirst("[.][^.]+$", "").split("/");
        String fileNameWithoutExt = fileName[fileName.length-1];

        // init writer
        BufferedWriter logfile = new BufferedWriter(new FileWriter(out + "logfiles" + File.separator +
                fileNameWithoutExt +"_logfile.txt"));

        FastaReader fastaReader = new FastaReader(mt_sequences_filepath);
        List<String> fastaheaders = fastaReader.getDescription();

        logfile.write("Data validation report based on files:\n" + mt_sequences_filepath+ "\n"+ data_template_filepath + "\n\n");

        System.out.println("Running validation...");

        validator.validate(data_template_filepath, logfile, fastaheaders, fastaReader.getLog_sequence_corretness());
        logfile.close();

        System.out.println("You can check the logfile now: \n" + new File(out + "logfiles" + File.separator +
                fileNameWithoutExt +"_logfile.txt").getAbsolutePath());


        // if file is correct, start database_uploader
        if (validator.isUploadPossible()){
            System.out.println("Running dataUploader...");

            UploadRunner uploadRunner = new UploadRunner();
            uploadRunner.run(data_template_filepath, mt_sequences_filepath, out);
        }

    }

    public static Set<String> getFilenames(String dir, Set<String> filenames){

        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {


                String filePath_without_extension = listOfFiles[i].getPath().substring(0, listOfFiles[i].getPath().lastIndexOf('.'));
                System.out.println("File " + filePath_without_extension);

                filenames.add(filePath_without_extension);

            } else if (listOfFiles[i].isDirectory()) {
                getFilenames(listOfFiles[i].getPath(), filenames);
            }
        }

        return filenames;
    }
}

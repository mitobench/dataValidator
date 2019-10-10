import uploadGenerator.UploadRunner;
import validator.IO.ArgumentParser;
import validator.IO.FastaReader;
import validator.calculations.Validator;

import java.io.File;
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
        int file_counter = 1;

        if(dir != null && Files.isDirectory(new File(dir).toPath())){
            Set<String> filenames = new HashSet<>();
            filenames = getFilenames(dir, filenames);


            for(String s : filenames){
                try {
                    //System.out.println("Processing file " + file_counter + "/" + filenames.size());
                    file_counter++;
                    run(s + ".fasta",s + ".csv", out, file_counter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } else {


            try {
                run(mt_sequences_filepath, data_template_filepath, out, file_counter);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }



    public static void run(String mt_sequences_filepath, String data_template_filepath, String out, int file_counter) throws Exception {
        validator.resetLogs();


       // System.out.println("Reading file " +  mt_sequences_filepath);

        String[] fileName = mt_sequences_filepath.replaceFirst("[.][^.]+$", "").split("/");
        String fileNameWithoutExt = fileName[fileName.length-1];

//        if(!Files.exists(new File(out + "logfiles" + File.separator + "FAILED_" +
//                fileNameWithoutExt +"_logfile.txt").toPath()) && !Files.exists(new File(out + "logfiles" +
//                File.separator + "PASSED_" + fileNameWithoutExt + "_" + file_counter + "_logfile.txt").toPath())) {

            FastaReader fastaReader = new FastaReader(mt_sequences_filepath);
            List<String> fastaheaders = fastaReader.getDescription();

            //System.out.println("Running validation...");

            validator.validate(data_template_filepath, fastaheaders, fastaReader.getLog_sequence_corretness(), mt_sequences_filepath);
            validator.writeLogFile(out, fileNameWithoutExt);

           // System.out.println("You can check the logfile now: \n" + new File(out + "logfiles" + File.separator +
           //         fileNameWithoutExt + "_" + file_counter  + "_logfile.txt").getAbsolutePath());


            // if file is correct, start database_uploader
            if (validator.isUploadPossible()) {
               // System.out.println("Running dataUploader...");

                UploadRunner uploadRunner = new UploadRunner();
                uploadRunner.run(data_template_filepath, mt_sequences_filepath, out, fastaReader, file_counter);
            }
//        }

    }

    public static Set<String> getFilenames(String dir, Set<String> filenames){

        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {


                String filePath_without_extension = listOfFiles[i].getPath().substring(0, listOfFiles[i].getPath().lastIndexOf('.'));
               // System.out.println("File " + filePath_without_extension);

                filenames.add(filePath_without_extension);

            } else if (listOfFiles[i].isDirectory()) {
                getFilenames(listOfFiles[i].getPath(), filenames);
            }
        }

        return filenames;
    }
}

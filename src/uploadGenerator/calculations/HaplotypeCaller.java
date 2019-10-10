package uploadGenerator.calculations;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;

public class HaplotypeCaller {


    private HashMap<String, String> header_to_fastaSequence_map;


    public HaplotypeCaller(HashMap<String, String> header_to_fastaSequence_map) {
        this.header_to_fastaSequence_map = header_to_fastaSequence_map;

    }

    public void call(String fastafile) throws IOException, InterruptedException {

        start(fastafile);

    }

    public void deleteTmpFiles() {

        try {
            if (Files.exists(new File("haplogroups.hsd").toPath()))
                Files.delete(new File("haplogroups.hsd").toPath());


            if (Files.exists(new File("haplogroups.hsd_lineage.txt").toPath()))
                Files.delete(new File("haplogroups.hsd_lineage.txt").toPath());


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void start(String f) throws IOException, InterruptedException {

        URL url = this.getClass().getResource("/haplogrep-2.1.25.jar");
        String dirpath = url.getPath();
        String[] command = new String[] { "java", "-jar", dirpath,
                "--format", "fasta",
                "--in",f,
                "--out", "haplogroups.hsd"};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        process.waitFor();
//
        while (process.isAlive()){
            // wait until haplogroups are calculated
        }
        System.out.println("Haplogroups are determined");

    }
}

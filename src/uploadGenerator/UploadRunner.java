package uploadGenerator;


import uploadGenerator.calculations.Calculator;
import uploadGenerator.calculations.HaplotypeCaller;
import uploadGenerator.calculations.LocationCompleter;
import uploadGenerator.io.HSDParser;
import uploadGenerator.io.MetaInfoReader;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class UploadRunner {

    private static HashMap<String, String> header_to_fastaSequence_map = null;



    public void run(String metaInfo_path, String sequences_path, String out_path, validator.IO.FastaReader fastaReader, int counter) throws Exception {

        LocationCompleter locationCompleter = new LocationCompleter();
        Calculator calculator = new Calculator();

        String[] fileName = sequences_path.replaceFirst("[.][^.]+$", "").split("/");
        String fileNameWithoutExt = fileName[fileName.length-1];

        BufferedWriter data_upload_query_meta_file = new BufferedWriter(new FileWriter(out_path +
                fileNameWithoutExt + "_" + counter + "_data_upload_queries_meta.sql"));

        String update_query = "";

        // read fasta file and write upload queries
        if(sequences_path != null){
            header_to_fastaSequence_map = new HashMap<>();
            //FastaReader fastaReader = new FastaReader(sequences_path);
            fastaReader.parseFasta();
            header_to_fastaSequence_map = fastaReader.getSequenceMap();

        }


        // parse meta info csv file and complete them
        boolean isheaderWritter = false;

        //calculate haplogroups / haplotypes
        HaplotypeCaller haplotypeCaller = new HaplotypeCaller(header_to_fastaSequence_map);
        haplotypeCaller.call(sequences_path);

        HSDParser hsdParser = new HSDParser();
        HashMap<String, ArrayList<String>> entryList = null;
        if(Files.exists(new File("haplogroups.hsd").toPath())){
            hsdParser.parseFile("haplogroups.hsd");
            entryList = hsdParser.getEntryList();
            haplotypeCaller.deleteTmpFiles();
        }



        // init reader
        MetaInfoReader metaInfoReader = new MetaInfoReader(metaInfo_path);
        metaInfoReader.read();
        String[] types = metaInfoReader.getTypes_list();
        String[] header = metaInfoReader.getHeader_list();

        locationCompleter.setHeader(header);
        locationCompleter.setIndexes();

        ArrayList<String> entries = metaInfoReader.getEntry_list();

        for (String entry : entries) {
            String[] meta_info = entry.split(",", types.length);

            // calculate stats
            String accessionID_with_version = meta_info[metaInfoReader.getAccessionIDIndex()].replace("\"","");
            String accessionID = accessionID_with_version.split("\\.")[0].trim();
            System.out.println(accessionID);
            String sequence = header_to_fastaSequence_map.get(accessionID);

            double completeness = -1;
            double percentageOfN = -1;
            if(sequence!=null){
                completeness = calculator.calculateCompleteness(sequence);
                percentageOfN = calculator.calculatePercentageOfN(sequence);
            } else {
                System.out.println("Sequence not in fasta: " + accessionID);
            }


            // determine user alias
            String user_alias = meta_info[metaInfoReader.getUserFirstNameIndex()].trim() + "" + meta_info[metaInfoReader.getUserSurnameIndex()].trim();

            String haplogroup="NULL";
            String haplotype="NULL";
            String quality="NULL";
            if(entryList == null){
                haplogroup = "NULL";
                haplotype = "NULL";
                quality = "NULL";
            } else {
                try {
                    haplogroup = entryList.get(accessionID).get(0).replace("'", "");
                    haplotype = entryList.get(accessionID).get(3);
                    quality = entryList.get(accessionID).get(1);
                } catch (Exception e) {
                    System.out.println("Sequence with accession id "+ accessionID + " not contained in Haplogrep2 result file");
                }

            }


            // complete geographic information
            locationCompleter.setEntry(entry.split(",", types.length));

            String[] entry_completed = locationCompleter.getCompletedInformation();
            meta_info = entry_completed;


            String meta_info_parsed = "";

            for (int i = 0; i < types.length; i++) {

                String type = types[i].replace("#", "").trim();
                String info;
                if( meta_info[i] == null){
                    info = "NULL";
                } else {
                    info = meta_info[i].replace("\"", "").trim();
                }

                if(info == null) {
                    meta_info_parsed += "NULL,";
                } else if (info.equals("NULL")) {
                    meta_info_parsed += "NULL,";
                }else if (info.equals("")) {
                    meta_info_parsed += "NULL,";
                } else if (info.contains("'")) {
                    String hg_tmp = info.replace("'", "");
                    meta_info_parsed += "'" + hg_tmp + "',";
                } else if (type.equals("String")) {
                    meta_info_parsed += "'" + info + "',";
                } else {
                    meta_info_parsed += info + ",";
                }
            }
            if (meta_info_parsed.endsWith(",")) {
                meta_info_parsed = meta_info_parsed.substring(0, meta_info_parsed.length() - 1);
            }

            // write new header
            if (!isheaderWritter) {
                metaInfoReader.addToHeader(",completeness,percentage_N,user_alias,haplogroup_current_versions,haplotype_current_versions,quality_haplotype_current_version, mt_sequence");
                metaInfoReader.addTotypes(",real,real,String,String,String,int,String");
                isheaderWritter = true;
            }

            // write everything to new metadata file

            String values = meta_info_parsed + "," + completeness + "," + percentageOfN + ",'" + user_alias + "','"
                    + haplogroup + "','" +haplotype + "'," + quality + ",'" + header_to_fastaSequence_map.get(accessionID) + "'";

            values = values.replace("'NULL'", "NULL");
            values = values.replace("'\"", "'");
            values = values.replace("\"'", "'");
            values = values.replace("\"", "");

            update_query = writeUpdateMeta(metaInfoReader.getHeader().substring(2), values, fileNameWithoutExt);


            String query = "insert into meta (" + metaInfoReader.getHeader().substring(2) + ") values ("
                    + values + ") ON CONFLICT (accession_id) DO UPDATE SET " + update_query +";\n";

            data_upload_query_meta_file.write(query);
        }

        data_upload_query_meta_file.close();
    }


    private static String writeUpdateMeta(String header, String values, String file ) {
        String update_tmp = "";
        String[] header_split = header.split(",");
        String[] values_split = values.split(",");

        if(header_split.length != values_split.length){
            System.out.println("File : " + file + "Header and values have not the same length.");
        } else {

            for(int i = 0; i < header_split.length; i++){

                if (i == header_split.length-1){
                    update_tmp += header_split[i] + "=" + values_split[i];
                } else {
                    update_tmp += header_split[i] + "=" + values_split[i] + ",";
                }
                           }
        }

        return update_tmp;


    }


}

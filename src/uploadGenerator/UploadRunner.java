package uploadGenerator;


import uploadGenerator.calculations.Calculator;
import uploadGenerator.calculations.HaplotypeCaller;
import uploadGenerator.calculations.LocationCompleter;
import uploadGenerator.io.FastaReader;
import uploadGenerator.io.HSDParser;
import uploadGenerator.io.MetaInfoReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class UploadRunner {

    private static HashMap<String, String> header_to_fastaSequence_map = null;

    public void run(String metaInfo_path, String sequences_path, String out_path) throws Exception {


        LocationCompleter locationCompleter = new LocationCompleter();
        Calculator calculator = new Calculator();

        String[] fileName = sequences_path.replaceFirst("[.][^.]+$", "").split("/");
        String fileNameWithoutExt = fileName[fileName.length-1];

        BufferedWriter data_upload_query_sequences_file = new BufferedWriter(new FileWriter(out_path +
                fileNameWithoutExt + "_data_upload_queries_sequences.sql"));
        BufferedWriter data_upload_query_meta_file = new BufferedWriter(new FileWriter(out_path +
                fileNameWithoutExt + "_data_upload_queries_meta.sql"));

        String update_query = "";

        // read fasta file and write upload queries
        if(sequences_path != null){
            header_to_fastaSequence_map = new HashMap<>();
            FastaReader fastaReader = new FastaReader(sequences_path);
            fastaReader.parseFasta();
            header_to_fastaSequence_map = fastaReader.getSequenceMap();

            for(String accession : header_to_fastaSequence_map.keySet()){
                update_query = accession + "='" + header_to_fastaSequence_map.get(accession) + "'";
                data_upload_query_sequences_file.write("insert into sequences (accession_id, mt_sequence) values ('"
                        + accession + "','" + header_to_fastaSequence_map.get(accession) +"') ON CONFLICT (accession_id) DO UPDATE SET accession_id='" +

                        accession + "',mt_sequence='" + header_to_fastaSequence_map.get(accession) + "';\n");
            }
        }


        // parse meta info csv file and complete them
        boolean isheaderWritter = false;


        //calculate haplogroups / haplotypes
        HaplotypeCaller haplotypeCaller = new HaplotypeCaller(header_to_fastaSequence_map);
        haplotypeCaller.call(sequences_path);

        HSDParser hsdParser = new HSDParser();
        hsdParser.parseFile("haplogroups.hsd");
        HashMap<String, ArrayList<String>> entryList = hsdParser.getEntryList();

        haplotypeCaller.deleteTmpFiles();


        // init reader
        MetaInfoReader metaInfoReader = new MetaInfoReader(metaInfo_path);
        metaInfoReader.read();
        String[] types = metaInfoReader.getTypes_list();
        String[] header = metaInfoReader.getHeader_list();

        locationCompleter.setHeader(header);
        locationCompleter.setIndexes();

        ArrayList<String> entries = metaInfoReader.getEntry_list();
        // get indexes of locations


        for (String entry : entries) {
            String[] meta_info = entry.split(",", types.length);

            // calculate stats
            String accessionID = meta_info[metaInfoReader.getAccessionIDIndex()].replace("\"","");
            String sequence = header_to_fastaSequence_map.get(accessionID);

            double completeness = -1;
            double percentageOfN = -1;
            if(sequence!=null){
                completeness = calculator.calculateCompleteness(sequence);
                percentageOfN = calculator.calculatePercentageOfN(sequence);
            }


            // determine user alias
            String user_alias = meta_info[metaInfoReader.getUserFirstNameIndex()].trim() + "" + meta_info[metaInfoReader.getUserSurnameIndex()].trim();

            // set macrogroup
            String haplogroup = entryList.get(accessionID).get(0).replace("'", "");

            // complete geographic information
            locationCompleter.setEntry(entry.split(",", types.length));

            String[] entry_completed = locationCompleter.getCompletedInformation();
            meta_info = entry_completed;


            String meta_info_parsed = "";

            for (int i = 0; i < types.length; i++) {

                String type = types[i].replace("#", "");
                String info = meta_info[i].replace("\"", "");

                if(info == null) {
                    meta_info_parsed += "NULL,";
                } else if (meta_info[i].equals("")) {
                    meta_info_parsed += "NULL,";
                } else if (info.contains("'")) {
                    String hg_tmp = info.replace("'", "");
                    meta_info_parsed += "'" + hg_tmp + "',";
                } else if (type.equals("String")) {
                    meta_info_parsed += "'" + meta_info[i] + "',";
                } else {
                    meta_info_parsed += info + ",";
                }
            }
            if (meta_info_parsed.endsWith(",")) {
                meta_info_parsed = meta_info_parsed.substring(0, meta_info_parsed.length() - 1);
            }

            // write new header
            if (!isheaderWritter) {
                metaInfoReader.addToHeader(",completeness,percentage_N,user_alias,haplogroup_current_versions,haplotype_current_versions,quality_haplotype_current_version\n");
                metaInfoReader.addTotypes(",real,real,String,String,String,int\n");
                isheaderWritter = true;
            }

            // write everything to new metadata file
            String values = meta_info_parsed + "," + completeness + "," + percentageOfN + ",'" + user_alias + "','"
                    + haplogroup + "','" + entryList.get(accessionID).get(3) + "',"
                    + entryList.get(accessionID).get(1);

            values = values.replace("'NULL'", "NULL");
            values = values.replace("'\"", "'");
            values = values.replace("\"'", "'");
            values = values.replace("\"", "");

            update_query = writeUpdateMeta(metaInfoReader.getHeader().substring(2), values);


            String query = "insert into meta (" + metaInfoReader.getHeader().substring(2) + ") values ("
                    + values + ") ON CONFLICT (accession_id) DO UPDATE SET " + update_query +";\n";

            data_upload_query_meta_file.write(query);
        }

        data_upload_query_sequences_file.close();
        data_upload_query_meta_file.close();

    }


    private static String writeUpdateMeta(String header, String values) {
        String update_tmp = "";
        String[] header_split = header.split(",");
        String[] values_split = values.split(",");

        if(header_split.length != values_split.length){
            System.out.println("Header and values have not the same length.");
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

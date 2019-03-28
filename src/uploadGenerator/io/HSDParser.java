package uploadGenerator.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class HSDParser {


    private String polys_not_found;
    private String polys_found;
    private String acc_in_remainings;
    private String polys_remaining;
    private String input_sample;
    private String id;
    private String group;
    private String quality;
    private HashMap<String, ArrayList<String>> entryList;

    public HSDParser(){
        entryList = new HashMap<>();
    }

    public void parseFile(String hsdfilepath) throws Exception {

        FileReader fr = new FileReader(new File(hsdfilepath));
        BufferedReader bfr = new BufferedReader(fr);

        String currline;
        boolean init = true;

        String[] header = bfr.readLine().replace("\"", "").split("\t");


        int polys_not_found_index = -1;
        int polys_found_index = -1;
        int polys_remaining_index = -1;
        int acc_in_remainings_index = -1;
        int input_sample_index = -1;

        int id_index = -1;
        int group_index = -1;
        int quality_index = -1;

        for (int i = 0; i < header.length; i++){
            switch (header[i]) {
                case "SampleID":  id_index = i;
                    break;
                case "Haplogroup":  group_index = i;
                    break;
                //case "Polymorphisms":  polys_found_index = i;
                //    break;
                case "Found_Polys":  polys_found_index = i;
                    break;
                case "Overall_Rank":  quality_index = i;
                    break;
                case "Not_Found_Polys":  polys_not_found_index = i;
                    break;
                case "Remaining_Polys":  polys_remaining_index = i;
                    break;
                case "AAC_In_Remainings":  acc_in_remainings_index = i;
                    break;
                case "Input_Sample":  input_sample_index = i;
                    break;
            }
        }


        while ((currline = bfr.readLine()) != null) {
            if(!header[0].equals("SampleID") && init){
                if(init){
                    throw new Exception("This is probably not a HSD input format file, as the header is missing.");
                }
            } else {

                String[] splitGroup = currline.split("\t");
                ArrayList<String> entry = new ArrayList<>();

                polys_not_found = "NULL";
                polys_found = "NULL";
                polys_remaining = "NULL";
                acc_in_remainings = "NULL";
                input_sample = "NULL";
                id = "NULL";
                group = "NULL";
                quality = "NULL";

                if(id_index!=-1){
                    id = splitGroup[id_index].trim();
                }
                if(group_index!=-1){
                    group = splitGroup[group_index].trim();
                }
                if(quality_index!=-1){
                    quality = round(Double.parseDouble(splitGroup[quality_index])*100,2);
                }
                if(polys_not_found_index!=-1) {
                    polys_not_found = splitGroup[polys_not_found_index].trim();
                }
                if(polys_found_index!=-1){
                    polys_found = splitGroup[polys_found_index].trim();
                }
                if(polys_remaining_index!=-1) {
                    polys_remaining = splitGroup[polys_remaining_index].trim();
                }
                if(acc_in_remainings_index!=-1) {
                    acc_in_remainings = splitGroup[acc_in_remainings_index].trim();
                }
                if(input_sample_index!=-1) {
                    input_sample = splitGroup[input_sample_index].trim();
                }

                entry.add(group);
                entry.add(quality);
                entry.add(polys_not_found);
                entry.add(polys_found);
                entry.add(polys_remaining);
                entry.add(acc_in_remainings);
                entry.add(input_sample);

                entryList.put(id.replace("\"","").split("\\.")[0], entry);

            }
        }

    }

    /**
     * This method rounds a double value on n digits.
     * @param value
     * @param numberOfDigitsAfterDecimalPoint
     * @return double rounded
     */
    public String round(double value, int numberOfDigitsAfterDecimalPoint) {
        if(Double.isNaN(value))
            return null;
        else if (Double.isInfinite(value))
            return "Inf";
        else {
            BigDecimal bigDecimal = new BigDecimal(value);
            bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint,
                    BigDecimal.ROUND_HALF_EVEN);
            return bigDecimal.doubleValue()+"";
        }

    }

    public HashMap<String, ArrayList<String>> getEntryList() {
        return entryList;
    }
}

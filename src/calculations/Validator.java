package calculations;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Validator {

    private List<String> country = Arrays.asList("Algeria","Egypt", "Libya", "Morocco", "Sudan", "Tunisia", "Western Sahara",
            "British Indian Ocean Territory", "Burundi", "Comoros", "Djibouti", "Eritrea", "Ethiopia", "French Southern Territories",
            "Kenya", "Madagascar", "Malawi", "Mauritius", "Mayotte", "Mozambique", "Reunion", "Rwanda", "Seychelles", "Somalia",
            "South Sudan", "Uganda", "United Republic of Tanzania", "Zambia", "Zimbabwe", "Angola", "Cameroon", "Central African Republic",
            "Chad", "Congo", "Democratic Republic of the Congo", "Equatorial Guinea", "Gabon", "Sao Tome and Principe", "Botswana",
            "Eswatini", "Lesotho", "Namibia", "South Africa", "Benin", "Burkina Faso", "Cape Verde", "Ivory Coast", "Gambia",
            "Ghana", "Guinea", "Guinea-Bissau", "Liberia", "Mali", "Mauritania", "Niger", "Nigeria", "Saint Helena", "Senegal",
            "Sierra Leone", "Togo", "Anguilla", "Antigua and Barbuda", "Aruba", "Bahamas", "Barbados", "Bonaire/Sint Eustatius/Saba",
            "British Virgin Islands", "Cayman Islands", "Cuba", "Curacao", "Dominica", "Dominican Republic", "Grenada", "Guadeloupe",
            "Haiti", "Jamaica", "Martinique", "Montserrat", "Puerto Rico", "Saint Barthelemy", "Saint Kitts and Nevis", "Saint Lucia",
            "Saint Martin (French Part)", "Saint Vincent and the Grenadines", "Sint Maarten (Dutch part)", "Trinidad and Tobago",
            "Turks and Caicos Islands", "United States Virgin Islands", "Belize", "Costa Rica", "El Salvador", "Guatemala",
            "Honduras", "Mexico", "Nicaragua", "Panama", "Argentina", "Bolivia (Plurinational State of)", "Bouvet Island",
            "Brazil", "Chile", "Colombia", "Ecuador", "Falkland Islands (Malvinas)", "French Guiana", "Guyana", "Paraguay",
            "Peru", "South Georgia and the South Sandwich Islands", "Suriname", "Uruguay", "Venezuela (Bolivarian Republic of)",
            "Bermuda", "Canada", "Greenland", "Saint Pierre and Miquelon", "United States of America", "Antarctica", "Kazakhstan",
            "Kyrgyzstan", "Tajikistan", "Turkmenistan", "Uzbekistan", "China", "China/Hong Kong Special Administrative Region",
            "China/Macao Special Administrative Region", "Taiwan", "Democratic Peoples Republic of Korea", "Japan", "Mongolia",
            "Republic of Korea", "Brunei Darussalam", "Cambodia", "Indonesia", "Lao People s Democratic Republic", "Malaysia",
            "Myanmar", "Philippines", "Singapore", "Thailand", "Timor-Leste", "Viet Nam", "Afghanistan", "Bangladesh", "Bhutan",
            "India", "Iran (Islamic Republic of)", "Maldives", "Nepal", "Pakistan", "Sri Lanka", "Armenia", "Azerbaijan", "Bahrain",
            "Cyprus", "Georgia", "Iraq", "Israel", "Jordan", "Kuwait", "Lebanon", "Oman", "Qatar", "Saudi Arabia", "State of Palestine",
            "Syrian Arab Republic", "Turkey", "United Arab Emirates", "Yemen", "Belarus", "Bulgaria", "Czechia", "Hungary",
            "Poland", "Republic of Moldova", "Romania", "Russian Federation", "Slovakia", "Ukraine", "Aland Islands", "Guernsey",
            "Jersey", "Denmark", "Estonia", "Faroe Islands", "Finland", "Iceland", "Ireland", "Isle of Man", "Latvia", "Lithuania",
            "Norway", "Svalbard and Jan Mayen Islands", "Sweden", "United Kingdom of Great Britain and Northern Ireland", "Albania",
            "Andorra", "Bosnia and Herzegovina", "Croatia", "Gibraltar", "Greece", "Holy See", "Italy", "Malta", "Montenegro",
            "Portugal", "San Marino", "Kosovo", "Serbia", "Slovenia", "Spain", "The former Yugoslav Republic of Macedonia", "Austria",
            "Belgium", "France", "Germany", "Liechtenstein", "Luxembourg", "Monaco", "Netherlands", "Switzerland", "Australia",
            "Christmas Island", "Cocos (Keeling) Islands", "Heard Island and McDonald Islands", "New Zealand", "Norfolk Island",
            "Fiji", "New Caledonia", "Papua New Guinea", "Solomon Islands", "Vanuatu", "Guam", "Kiribati", "Marshall Islands",
            "Micronesia (Federated States of)", "Nauru", "Northern Mariana Islands", "Palau", "United States Minor Outlying Islands",
            "American Samoa", "Cook Islands", "French Polynesia", "Niue", "Pitcairn", "Samoa", "Tokelau", "Tonga", "Tuvalu", "Wallis and Futuna Islands");



    private List<String> region = Arrays.asList("Africa", "Americas", "Antarctica", "Asia", "Europe", "Oceania");
    private List<String> subregion = Arrays.asList("Northern Africa", "Sub-Saharan Africa", "Latin America and the Caribbean",
            "Northern America", "Central Asia", "Eastern Asia", "South-eastern Asia", "Southern Asia", "Western Asia",
            "Eastern Europe", "Northern Europe", "Southern Europe", "Western Europe", "Australia and New Zealand",
            "Melanesia", "Micronesia", "Polynesia");


    private List<String> intermediate_region = Arrays.asList("Eastern Africa", "Middle Africa", "Southern Africa",
            "Western Africa", "Caribbean", "Central America", "South America", "Channel Islands");

    private List<String> publication_type = Arrays.asList("paper","peerPrint","direct submission to genbank","direct submission to mitoDB","article");
    private List<String> publication_status = Arrays.asList("published","protected","private","in press","in preparation","submitted");
    private List<String> sequencing_platform = Arrays.asList("Illumina","454","sanger","nanopore","pacbio");


    public boolean validate(String data_template, BufferedWriter logfile, List<String> fastaheaders) {
        BufferedReader br = null;
        String line;
        String delimiter = ",";
        HashMap<String, Integer> attribute_index_map = new HashMap<>();

        // parse header line
        try {

            br = new BufferedReader(new FileReader(data_template));

            String[] headerLine_array = br.readLine().replace("##", "").split(delimiter);
            List<String> headerList = Arrays.asList(headerLine_array);

            // get indexes
            int index_doi = headerList.indexOf("doi");
            attribute_index_map.put("doi", index_doi);

            int index_author = headerList.indexOf("author");
            attribute_index_map.put("author", index_author);

            int index_publication_date = headerList.indexOf("publication_date");
            attribute_index_map.put("publication_date", index_publication_date);

            int index_title = headerList.indexOf("title");
            attribute_index_map.put("title", index_title);

            int index_journal = headerList.indexOf("journal");
            attribute_index_map.put("journal", index_journal);

            int index_publication_type = headerList.indexOf("publication_type");
            attribute_index_map.put("publication_type", index_publication_type);

            int index_publication_status = headerList.indexOf("publication_status");
            attribute_index_map.put("publication_status", index_publication_status);

            int index_publication_comments = headerList.indexOf("publication_comments");
            attribute_index_map.put("publication_comments", index_publication_comments);

            int index_accession_id = headerList.indexOf("accession_id");
            attribute_index_map.put("accession_id", index_accession_id);

            int index_tissue_sampled = headerList.indexOf("tissue_sampled");
            attribute_index_map.put("tissue_sampled", index_tissue_sampled);

            int index_sampling_date = headerList.indexOf("sampling_date");
            attribute_index_map.put("sampling_date", index_sampling_date);

            int index_sequencing_platform = headerList.indexOf("sequencing_platform");
            attribute_index_map.put("sequencing_platform", index_sequencing_platform);

            int index_enrichment_method = headerList.indexOf("enrichment_method");
            attribute_index_map.put("enrichment_method", index_enrichment_method);

            int index_extraction_protocol = headerList.indexOf("extraction_protocol");
            attribute_index_map.put("extraction_protocol", index_extraction_protocol);

            int index_mean_coverage = headerList.indexOf("mean_coverage");
            attribute_index_map.put("mean_coverage", index_mean_coverage);

            int index_std_dev_coverage = headerList.indexOf("std_dev_coverage");
            attribute_index_map.put("std_dev_coverage", index_std_dev_coverage);

            int index_minimum_coverage = headerList.indexOf("minimum_coverage");
            attribute_index_map.put("minimum_coverage", index_minimum_coverage);

            int index_maximum_coverage = headerList.indexOf("maximum_coverage");
            attribute_index_map.put("maximum_coverage", index_maximum_coverage);

            int index_calibrated_date_range_from = headerList.indexOf("calibrated_date_range_from");
            attribute_index_map.put("calibrated_date_range_from", index_calibrated_date_range_from);

            int index_calibrated_date_range_to = headerList.indexOf("calibrated_date_range_to");
            attribute_index_map.put("calibrated_date_range_to", index_calibrated_date_range_to);

            int index_C14_age_BP = headerList.indexOf("C14_age_BP");
            attribute_index_map.put("C14_age_BP", index_C14_age_BP);

            int index_indirect_contextual_date = headerList.indexOf("indirect_contextual_date");
            attribute_index_map.put("indirect_contextual_date", index_indirect_contextual_date);

            int index_radiocarbon_lab_code = headerList.indexOf("radiocarbon_lab_code");
            attribute_index_map.put("radiocarbon_lab_code", index_radiocarbon_lab_code);

            int index_dating_comments = headerList.indexOf("dating_comments");
            attribute_index_map.put("dating_comments", index_dating_comments);

            int index_reference_genome = headerList.indexOf("reference_genome");
            attribute_index_map.put("reference_genome", index_reference_genome);

            int index_starting_np = headerList.indexOf("starting_np");
            attribute_index_map.put("starting_np", index_starting_np);

            int index_ending_np = headerList.indexOf("ending_np");
            attribute_index_map.put("ending_np", index_ending_np);

            int index_sequence_versions = headerList.indexOf("sequence_versions");
            attribute_index_map.put("sequence_versions", index_sequence_versions);

            int index_comments_sequence_versions = headerList.indexOf("comments_sequence_versions");
            attribute_index_map.put("comments_sequence_versions", index_comments_sequence_versions);

            int index_haplogroup_originally_published = headerList.indexOf("haplogroup_originally_published");
            attribute_index_map.put("haplogroup_originally_published", index_haplogroup_originally_published);

            int index_data_type = headerList.indexOf("data_type");
            attribute_index_map.put("data_type", index_data_type);

            int index_labsample_id = headerList.indexOf("labsample_id");
            attribute_index_map.put("labsample_id", index_labsample_id);

            int index_sex = headerList.indexOf("sex");
            int index_age = headerList.indexOf("age");
            int index_population_purpose = headerList.indexOf("population_purpose");
            int index_access = headerList.indexOf("access");
            int index_population = headerList.indexOf("population");

            int index_geographic_info_TMA_inferred_region = headerList.indexOf("geographic_info_TMA_inferred_region");
            int index_geographic_info_TMA_inferred_subregion = headerList.indexOf("geographic_info_TMA_inferred_subregion");
            int index_geographic_info_TMA_inferred_intermediate_region = headerList.indexOf("geographic_info_TMA_inferred_intermediate_region");
            int index_geographic_info_TMA_inferred_country = headerList.indexOf("geographic_info_TMA_inferred_country");
            int index_geographic_info_TMA_inferred_city = headerList.indexOf("geographic_info_TMA_inferred_city");
            int index_geographic_info_TMA_inferred_latitude = headerList.indexOf("geographic_info_TMA_inferred_latitude");
            int index_geographic_info_TMA_inferred_longitude = headerList.indexOf("geographic_info_TMA_inferred_longitude");

            int index_sample_origin_region = headerList.indexOf("sampling_region");
            int index_sample_origin_subregion = headerList.indexOf("sampling_subregion");
            int index_sample_origin_intermediate_region = headerList.indexOf("sampling_intermediate_region");
            int index_sample_origin_country = headerList.indexOf("sampling_country");
            int index_sample_origin_city = headerList.indexOf("sampling_city");
            int index_sample_origin_latitude = headerList.indexOf("sampling_latitude");
            int index_sample_origin_longitude = headerList.indexOf("sampling_longitude");

            int index_sampling_region = headerList.indexOf("sample_origin_region");
            int index_sampling_subregion = headerList.indexOf("sample_origin_subregion");
            int index_sampling_intermediate_region = headerList.indexOf("sample_origin_intermediate_region");
            int index_sampling_country = headerList.indexOf("sample_origin_country");
            int index_sampling_city = headerList.indexOf("sample_origin_city");
            int index_sampling_latitude = headerList.indexOf("sample_origin_latitude");
            int index_sampling_longitude = headerList.indexOf("sample_origin_longitude");

            int index_marriage_rules = headerList.indexOf("marriage_rules");
            int index_marriage_system = headerList.indexOf("marriage_system");
            int index_descent_system = headerList.indexOf("descent_system");
            int index_residence_system = headerList.indexOf("residence_system");
            int index_subsistence = headerList.indexOf("subsistence");
            int index_clan = headerList.indexOf("clan");
            int index_ethnicity = headerList.indexOf("ethnicity");
            int index_language = headerList.indexOf("language");
            int index_generations_to_TMA = headerList.indexOf("generations_to_TMA");

            int index_user_firstname = headerList.indexOf("user_firstname");
            int index_user_surname = headerList.indexOf("user_surname");
            int index_user_email = headerList.indexOf("user_email");
            int index_user_affiliation = headerList.indexOf("user_affiliation");

            // stat missing columns:
            logfile.write("----------------------------------------------------------\n");
            logfile.write("Missing attributes (columns) in the meta information file: \n");
            logfile.write("----------------------------------------------------------\n");
            if(index_doi == -1 )
                logfile.write("DOI.\n");
            if(index_author == -1)
                logfile.write("Author.\n");
            if(index_publication_date == -1)
                logfile.write("Publication date.\n");
            if(index_title == -1)
                logfile.write("Title.\n");
            if(index_journal == -1)
                logfile.write("Journal.\n");
            if(index_publication_type == -1)
                logfile.write("Publication type.\n");
            if(index_publication_status == -1)
                logfile.write("Publication status.\n");
            if(index_publication_comments == -1)
                logfile.write("Publication comment.\n");
            if(index_accession_id == -1)
                logfile.write("Accession id.\n");
            if(index_tissue_sampled == -1)
                logfile.write("Tissue sampled.\n");
            if(index_sampling_date == -1)
                logfile.write("Sampling date.\n");
            if(index_sequencing_platform == -1)
                logfile.write("Sequencing platform.\n");
            if(index_enrichment_method == -1)
                logfile.write("Enrichment method.\n");
            if(index_extraction_protocol == -1)
                logfile.write("Extraction protocol.\n");
            if(index_mean_coverage == -1)
                logfile.write("Mean coverage.\n");
            if(index_std_dev_coverage == -1)
                logfile.write("Std dev coverage.\n");
            if(index_minimum_coverage == -1)
                logfile.write("Minimum coverage.\n");
            if(index_maximum_coverage == -1)
                logfile.write("Maximum coverage.\n");
            if(index_calibrated_date_range_from == -1)
                logfile.write("Calibrated date lower limit.\n");
            if(index_calibrated_date_range_to == -1)
                logfile.write("Calibrated date upper limit.\n");
            if(index_C14_age_BP == -1)
                logfile.write("C14 are BP.\n");
            if(index_indirect_contextual_date == -1)
                logfile.write("Indirect contextual date.\n");
            if(index_radiocarbon_lab_code == -1)
                logfile.write("Radiocarbon lab code .\n");
            if(index_dating_comments == -1)
                logfile.write("Dating comment.\n");
            if(index_reference_genome == -1)
                logfile.write("Reference genome.\n");
            if(index_publication_type == -1)
                logfile.write("Reference type.\n");
            if(index_starting_np == -1)
                logfile.write("Starting np.\n");
            if(index_ending_np == -1)
                logfile.write("Ending np.\n");
            if(index_sequence_versions == -1)
                logfile.write("Sequence versions.\n");
            if(index_haplogroup_originally_published == -1)
                logfile.write("Haplogroup originally published.\n");
            if(index_data_type == -1)
                logfile.write("Data type.\n");
            if(index_labsample_id == -1)
                logfile.write("Labsample id.\n");
            if(index_sex == -1)
                logfile.write("Sex.\n");
            if(index_age == -1)
                logfile.write("Age.\n");
            if(index_population_purpose == -1)
                logfile.write("Population purpose.\n");
            if(index_access == -1)
                logfile.write("Access.\n");
            if(index_population == -1)
                logfile.write("Population.\n");
            if(index_geographic_info_TMA_inferred_region == -1)
                logfile.write("Geographic info TMA inferred region.\n");
            if(index_geographic_info_TMA_inferred_subregion == -1)
                logfile.write("Geographic info TMA inferred subregion.\n");
            if(index_geographic_info_TMA_inferred_intermediate_region == -1)
                logfile.write("Geographic info TMA inferred intermediate region.\n");
            if(index_geographic_info_TMA_inferred_country == -1)
                logfile.write("Geographic info TMA inferred country.\n");
            if(index_geographic_info_TMA_inferred_city == -1)
                logfile.write("Geographic info TMA inferred city.\n");
            if(index_geographic_info_TMA_inferred_latitude == -1)
                logfile.write("Geographic info TMA inferred latitude.\n");
            if(index_geographic_info_TMA_inferred_longitude == -1)
                logfile.write("Geographic info TMA inferred longitude.\n");
            if(index_sample_origin_region == -1)
                logfile.write("Sample origin region.\n");
            if(index_sample_origin_subregion == -1)
                logfile.write("Sample origin subregion.\n");
            if(index_sample_origin_intermediate_region == -1)
                logfile.write("Sample origin intermediate region.\n");
            if(index_sample_origin_country == -1)
                logfile.write("Sample origin country.\n");
            if(index_sample_origin_city == -1)
                logfile.write("Sample origin city.\n");
            if(index_sample_origin_latitude == -1)
                logfile.write("Sample origin latitude.\n");
            if(index_sample_origin_longitude == -1)
                logfile.write("Sample origin longitude.\n");
            if(index_sampling_region == -1)
                logfile.write("Sampling region.\n");
            if(index_sampling_subregion == -1)
                logfile.write("Sampling subregion.\n");
            if(index_sampling_intermediate_region == -1)
                logfile.write("Sampling intermediate region.\n");
            if(index_sampling_country == -1)
                logfile.write("Sampling country.\n");
            if(index_sampling_city == -1)
                logfile.write("Sampling city.\n");
            if(index_sampling_latitude == -1)
                logfile.write("Sampling latitude.\n");
            if(index_sampling_longitude == -1)
                logfile.write("Sampling longitude.\n");
            if(index_marriage_rules == -1)
                logfile.write("Marriage rules.\n");
            if(index_marriage_system == -1)
                logfile.write("Marriage system.\n");
            if(index_descent_system == -1)
                logfile.write("Descent system.\n");
            if(index_residence_system == -1)
                logfile.write("Residence system.\n");
            if(index_subsistence == -1)
                logfile.write("Subsistence.\n");
            if(index_clan == -1)
                logfile.write("Clan.\n");
            if(index_ethnicity == -1)
                logfile.write("Ethnicity.\n");
            if(index_language == -1)
                logfile.write("Language.\n");
            if(index_generations_to_TMA == -1)
                logfile.write("Generations to TMA.\n");
            if(index_user_firstname == -1)
                logfile.write("User first name.\n");
            if(index_user_surname == -1)
                logfile.write("User surname.\n");
            if(index_user_email == -1)
                logfile.write("User email.\n");
            if(index_user_affiliation == -1)
                logfile.write("User affiliation.\n");


            //logfile.write("----------------------------------------------------------\n");
            //logfile.write("Accession id given in meta file, but sequences missing in fasta file: \n");
            //logfile.write("----------------------------------------------------------\n");



            // iterate over all lines and check correct format and missing value
            logfile.write("--------------------------------------------------\n");
            logfile.write("--------------------------------------------------\n");

            while ((line = br.readLine()) != null) {
                if(!line.startsWith("#") && !line.startsWith("##")){


                    String[] line_splitted = line.split(",", headerLine_array.length);
                    // does sequence exists?
                    String accession = line_splitted[index_accession_id];

                    logfile.write("\n\nEntry to accession ID: " + accession + "\n");


                    String errorline_incorrect_format = "";
                    String errorline_missing_value = "";

                    if(!fastaheaders.contains(accession)){
                        logfile.write("Sequence with this accession ID: "+ accession +" does not exist in fasta file.\n");
                    }

                    // the mandatory fields has to be present:
                    if(line_splitted[index_accession_id] == null || line_splitted[index_accession_id].equals("")){
                        logfile.write("Accession ID must be set! (mandatory field)\n");
                    }
                    if(line_splitted[index_publication_status] == null || line_splitted[index_publication_status].equals("")){
                        logfile.write("Publication status must be set! (mandatory field)\n");
                    }
                    if(line_splitted[index_doi] == null || line_splitted[index_doi].equals("")){
                        logfile.write("DOI must be set! (mandatory field)\n");
                    }
                    if(line_splitted[index_reference_genome] == null || line_splitted[index_reference_genome].equals("")){
                        logfile.write("Reference genome must be set! (mandatory field)\n");
                    }
                    if(line_splitted[index_data_type] == null || line_splitted[index_data_type].equals("")){
                        logfile.write("Data type must be set! (mandatory field)\n");
                    }
                    if(line_splitted[index_user_email] == null || line_splitted[index_user_email].equals("")){
                        logfile.write("User Email must be set! (mandatory field)\n");
                    }

                    // test presence and correctness-----------------------------------------------------------------------------------------------

                    if(index_doi != -1){
                        String doi = line_splitted[index_doi].toLowerCase();
                        if(!doi.contains(".")){
                            errorline_incorrect_format += "Doi is not in correct format: " + doi + "\n";
                        }
                    }

                    if(index_mean_coverage != -1){
                        String number = line_splitted[index_mean_coverage];
                        if(number.equals("")){
                            errorline_missing_value += "Mean coverage is missing.\n";
                        } else {
                            try {
                                Double.parseDouble(number);
                            } catch(NumberFormatException e) {
                                errorline_incorrect_format += "Mean coverage is not in correct format: " + number + "\n";
                            }
                        }
                    }

                    if(index_std_dev_coverage != -1){
                        String number = line_splitted[index_std_dev_coverage];
                        if(number.equals("")){
                            errorline_missing_value += "Coverage standard deviation is missing.\n";
                        } else {
                            try {
                                Double.parseDouble(number);
                            } catch(NumberFormatException e) {
                                errorline_incorrect_format += "Coverage standard deviation is not in correct format: " + number + "\n";
                            }
                        }
                    }

                    if(index_minimum_coverage != -1){
                        String number = line_splitted[index_minimum_coverage];
                        if(number.equals("")){
                            errorline_missing_value += "Minimum coverage is missing\n";
                        } else if(!isStringInt(number)){
                            errorline_incorrect_format += "Minimum coverage is not in correct format: " + number + "\n";
                        }
                    }

                    if(index_maximum_coverage != -1){
                        String number = line_splitted[index_maximum_coverage];
                        if(number.equals("")){
                            errorline_missing_value += "Maximum coverage is missing\n";
                        } else if(!isStringInt(number)){
                            errorline_incorrect_format += "Maximum coverage is not in correct format: " + number + "\n";
                        }
                    }

                    if(index_starting_np != -1){
                        String number = line_splitted[index_starting_np];
                        if(number.equals("")){
                            errorline_missing_value += "Starting position is missing\n";
                        } else if(!isStringInt(number)){
                            errorline_incorrect_format += "Starting position is not in correct format: " + number + "\n";
                        }
                    }

                    if(index_ending_np != -1){
                        String number = line_splitted[index_ending_np];
                        if(number.equals("")){
                            errorline_missing_value += "Ending position is missing\n";
                        } else if(!isStringInt(number)){
                            errorline_incorrect_format += "Ending position is not in correct format: " + number + "\n";
                        }
                    }

                    if(index_data_type != -1){
                        String data_type = line_splitted[index_data_type].toLowerCase();
                        if(!data_type.equals("fullmt") && !data_type.equals("wholegenome")){
                            errorline_incorrect_format += "Data type is not in correct format: " + data_type + "\n";
                        }
                    }

                    if(index_age != -1){
                        String age = line_splitted[index_age].toLowerCase();
                        if (age.equals("")){
                            errorline_missing_value += "Age is missing.\n";
                        } else if(!isStringInt(age) && !age.equals("adult") && !age.equals("neonate") && !age.equals("subadult")
                                && !age.equals("infant") && !age.contains("-") && !age.contains("+")){
                            errorline_incorrect_format += "Age is not in correct format: " + age + "\n";
                        }
                    }


                    if(index_sex != -1){
                        String sex = line_splitted[index_sex].toLowerCase();
                        if (sex.equals("")){
                            errorline_missing_value += "Sex is missing.\n";
                        } else if(!sex.equals("f") && !sex.equals("m") && !sex.equals("u")){
                            errorline_incorrect_format += "Sex is not in correct format: " + sex + "\n";
                        }
                    }

                    if(index_population_purpose != -1){
                        String population_purpose = line_splitted[index_population_purpose].toLowerCase();
                        if (population_purpose.equals("")){
                            errorline_missing_value += "Population purpose is missing.\n";
                        } else if(!population_purpose.equals("true") && !population_purpose.equals("false") &&
                                !population_purpose.equals("t") && !population_purpose.equals("f")){
                            errorline_incorrect_format += "Population purpose is not in correct format: " + population_purpose + "\n";
                        }
                    }

                    if(index_access != -1){
                        String access = line_splitted[index_access].toLowerCase();
                        if (access.equals("")){
                            errorline_missing_value += "Access is missing.\n";
                        } else if(!access.equals("private") && !access.equals("public") ){
                            errorline_incorrect_format += "Access is not in correct format: " + access + "\n";
                        }
                    }

                    // --------------------------------------------------------------------------------------------------------------------
                    // GEOGRAPHIC INFO - TMA INFERRED

                    if(index_geographic_info_TMA_inferred_region != -1){
                        String geographic_info_TMA_inferred_region = line_splitted[index_geographic_info_TMA_inferred_region];
                        if (geographic_info_TMA_inferred_region.equals("")) {
                            errorline_missing_value += "Geographic info TMA inferred region is missing.\n";
                        } else if(!region.contains(geographic_info_TMA_inferred_region)){
                            errorline_incorrect_format += "Geographic info TMA inferred region is not in correct format: " + geographic_info_TMA_inferred_region + "\n";
                        }
                    }

                    if(index_geographic_info_TMA_inferred_subregion != -1){
                        String geographic_info_TMA_inferred_subregion = line_splitted[index_geographic_info_TMA_inferred_subregion];
                        if (geographic_info_TMA_inferred_subregion.equals("")) {
                            errorline_missing_value += "Geographic info TMA inferred subregion is missing.\n";
                        } else if(!subregion.contains(geographic_info_TMA_inferred_subregion)){
                            errorline_incorrect_format += "Geographic info TMA inferred subregion is not in correct format: " + geographic_info_TMA_inferred_subregion + "\n";
                        }
                    }

                    if(index_geographic_info_TMA_inferred_intermediate_region != -1){
                        String geographic_info_TMA_inferred_intermediate_region = line_splitted[index_geographic_info_TMA_inferred_intermediate_region];
                        if (geographic_info_TMA_inferred_intermediate_region.equals("")) {
                            errorline_missing_value += "Geographic info TMA inferred intermediate region is missing.\n";
                        } else if(!intermediate_region.contains(geographic_info_TMA_inferred_intermediate_region)){
                            errorline_incorrect_format += "Geographic info TMA inferred intermediate region is not in correct format: " + geographic_info_TMA_inferred_intermediate_region + "\n";
                        }
                    }


                    if(index_geographic_info_TMA_inferred_country != -1){
                        String geographic_info_TMA_inferred_country = line_splitted[index_geographic_info_TMA_inferred_country];
                        if (geographic_info_TMA_inferred_country.equals("")) {
                            errorline_missing_value += "Geographic info TMA inferred country is missing.\n";
                        } else if(!country.contains(geographic_info_TMA_inferred_country)){
                            errorline_incorrect_format += "Geographic info TMA inferred country is not in correct format: " + geographic_info_TMA_inferred_country + "\n";
                        }
                    }

                    if(index_geographic_info_TMA_inferred_latitude != -1){
                        String number = line_splitted[index_geographic_info_TMA_inferred_latitude];
                        if(number.equals("")){
                            errorline_missing_value += "Geographic info TMA inferred latitude is missing.\n";
                        } else {
                            try {
                                Double.parseDouble(number);
                            } catch(NumberFormatException e) {
                                errorline_incorrect_format += "Geographic info TMA inferred latitude is not in correct format: " + number + "\n";
                            }
                        }
                    }

                    if(index_geographic_info_TMA_inferred_longitude != -1){
                        String number = line_splitted[index_geographic_info_TMA_inferred_longitude];
                        if(number.equals("")){
                            errorline_missing_value += "Geographic info TMA inferred longitude is missing.\n";
                        } else {
                            try {
                                Double.parseDouble(number);
                            } catch(NumberFormatException e) {
                                errorline_incorrect_format += "Geographic info TMA inferred longitude is not in correct format: " + number + "\n";
                            }
                        }
                    }


                    // --------------------------------------------------------------------------------------------------------------------
                    // GEOGRAPHIC INFO - SAMPLE


                    if(index_sample_origin_region != -1){
                        String sample_origin_region = line_splitted[index_sample_origin_region];
                        if (sample_origin_region.equals("")) {
                            errorline_missing_value += "Sample origin region is missing.\n";
                        } else if(!region.contains(sample_origin_region)){
                            errorline_incorrect_format +="Sample origin region is not in correct format: " + sample_origin_region + "\n";
                        }
                    }


                    if(index_sample_origin_subregion != -1){
                        String sample_origin_subregion = line_splitted[index_sample_origin_subregion];
                        if (sample_origin_subregion.equals("")) {
                            errorline_missing_value += "Sample origin subregion is missing.\n";
                        } else if(!subregion.contains(sample_origin_subregion)){
                            errorline_incorrect_format += "Sample origin subregion is not in correct format: "+ sample_origin_subregion + "\n";
                        }
                    }

                    if(index_sample_origin_intermediate_region != -1){
                        String sample_origin_intermediate_region = line_splitted[index_sample_origin_intermediate_region];
                        if (sample_origin_intermediate_region.equals("")) {
                            errorline_missing_value += "Sample origin intermediate region is missing.\n";
                        } else if(!intermediate_region.contains(sample_origin_intermediate_region)){
                            errorline_incorrect_format += "Sample origin intermediate region is not in correct format: " + sample_origin_intermediate_region + "\n";
                        }
                    }

                    if(index_sample_origin_country != -1){
                        String sample_origin_country = line_splitted[index_sample_origin_country];
                        if (sample_origin_country.equals("")) {
                            errorline_missing_value += "Sample origin country is missing.\n";
                        } else if(!country.contains(sample_origin_country)){
                            errorline_incorrect_format += "Sample origin country is not in correct format: " + sample_origin_country + "\n";
                        }
                    }


                    if(index_sample_origin_latitude != -1){
                        String number = line_splitted[index_sample_origin_latitude];
                        if(number.equals("")){
                            errorline_missing_value += "Sample origin latitude is missing.\n";
                        } else {
                            try {
                                Double.parseDouble(number);
                            } catch(NumberFormatException e) {
                                errorline_incorrect_format += "Sample origin latitude is not in correct format: " + number + "\n";
                            }
                        }
                    }

                    if(index_sample_origin_longitude != -1){
                        String number = line_splitted[index_sample_origin_longitude];
                        if(number.equals("")){
                            errorline_missing_value += "Sample origin longitude is missing.\n";
                        } else {
                            try {
                                Double.parseDouble(number);
                            } catch(NumberFormatException e) {
                                errorline_incorrect_format += "Sample origin longitude is not in correct format: " + number + "\n";
                            }
                        }
                    }


                    // --------------------------------------------------------------------------------------------------------------------
                    // GEOGRAPHIC INFO - SAMPLING

                    if(index_sampling_region != -1){
                        String sampling_region = line_splitted[index_sampling_region];
                        if (sampling_region.equals("")) {
                            errorline_missing_value += "Sampling region is missing.\n";
                        } else if(!subregion.contains(sampling_region)){
                            errorline_incorrect_format += "Sampling region is not in correct format: "+ sampling_region + "\n";
                        }
                    }

                    if(index_sampling_subregion != -1){
                        String sampling_subregion = line_splitted[index_sampling_subregion];
                        if (sampling_subregion.equals("")) {
                            errorline_missing_value += "Sampling subregion is missing.\n";
                        } else if(!subregion.contains(sampling_subregion)){
                            errorline_incorrect_format += "Sampling subregion is not in correct format: "+ sampling_subregion + "\n";
                        }
                    }


                    if(index_sampling_intermediate_region != -1){
                        String sampling_intermediate_region = line_splitted[index_sampling_intermediate_region];
                        if (sampling_intermediate_region.equals("")) {
                            errorline_missing_value += "Sampling intermediate region is missing.\n";
                        } else if(!intermediate_region.contains(sampling_intermediate_region)){
                            errorline_incorrect_format += "Sampling intermediate region is not in correct format: "+ sampling_intermediate_region + "\n";
                        }
                    }

                    if(index_sampling_country != -1){
                        String sampling_contry = line_splitted[index_sampling_country];
                        if (sampling_contry.equals("")) {
                            errorline_missing_value += "Sampling country is missing.\n";
                        } else if(!country.contains(sampling_contry)){
                            errorline_incorrect_format += "Sampling country is not in correct format: "+ sampling_contry + "\n";
                        }
                    }

                    if(index_sampling_latitude != -1){
                        String number = line_splitted[index_sampling_latitude];
                        if(number.equals("")){
                            errorline_missing_value += "Sampling latitude is missing.\n";
                        } else {
                            try {
                                Double.parseDouble(number);
                            } catch(NumberFormatException e) {
                                errorline_incorrect_format += "Sampling latitude is not in correct format: " + number + "\n";
                            }
                        }
                    }

                    if(index_sampling_longitude != -1){
                        String number = line_splitted[index_sampling_longitude];
                        if(number.equals("")){
                            errorline_missing_value += "Sampling longitude is missing.\n";
                        } else {
                            try {
                                Double.parseDouble(number);
                            } catch(NumberFormatException e) {
                                errorline_incorrect_format += "Sampling longitude is not in correct format: " + number + "\n";
                            }
                        }
                    }


                    // --------------------------------------------------------------------------------------------------------------------
                    if(index_generations_to_TMA != -1){
                        String number = line_splitted[index_generations_to_TMA];
                        if(number.equals("")){
                            errorline_missing_value += "Generations to TMA is missing\n";
                        } else if(!isStringInt(number)){
                            errorline_incorrect_format += "Generations to TMA is not in correct format: " + number + "\n";
                        }
                    }


                    if(index_publication_status != -1){
                        String publication_status = line_splitted[index_publication_status].toLowerCase();
                        if(publication_status.equals("")){
                            errorline_missing_value += "Publication status is missing.\n";
                        } else if(!publication_status.equals("published") && !publication_status.equals("protected")
                                && !publication_status.equals("private") && !publication_status.equals("in press")
                                && !publication_status.equals("in preparation") && !publication_status.equals("submitted")){
                            errorline_incorrect_format += "Publication status is not in correct format: " + publication_status + "\n";
                        }
                    }

                    if(index_publication_date != -1){
                        if(line_splitted[index_publication_date].equals("")){
                            errorline_missing_value += "Publication date is missing.\n";
                        } else if(!isDateValid(line_splitted[index_publication_date])){
                            errorline_incorrect_format += "Publication date is not in correct format: " + line_splitted[index_publication_date] + "\n";
                        }
                    }

                    if(index_sampling_date != -1){

                        if(line_splitted[index_sampling_date].equals("")){
                            errorline_missing_value += "Sampling date is missing.\n";
                        } else if(!isDateValid(line_splitted[index_sampling_date])){
                            errorline_incorrect_format += "Sampling date is not in correct format: " + line_splitted[index_sampling_date] + "\n";
                        }
                    }

                    if(index_reference_genome != -1){
                        String reference_genome = line_splitted[index_reference_genome].toLowerCase();
                        if(reference_genome.equals("")){
                            errorline_missing_value += "Reference genome is missing.\n";
                        } else if(!reference_genome.equals("rsrs") && !reference_genome.equals("rcrs")){
                            errorline_incorrect_format += "Reference genome is not in correct format: " + reference_genome + "\n";
                        }
                    }

                    if(index_user_email != -1){
                        String user_email = line_splitted[index_user_email].toLowerCase();
                        if(!user_email.contains("@")){
                            errorline_incorrect_format += "User email is not in correct format: " + user_email + "\n";
                        }
                    }
                    if(index_publication_type != -1){
                        String publicationType = line_splitted[index_publication_type].toLowerCase();
                        if(publicationType.equals("")){
                            errorline_missing_value += "Publication type is missing.\n";
                        } else if(!publication_type.contains(publicationType)){
                            errorline_incorrect_format += "Publication type is not in correct format: " + publicationType + "\n";
                        }
                    }

                    if(index_publication_status != -1){
                        String publicationStatus = line_splitted[index_publication_status].toLowerCase();
                        if(publicationStatus.equals("")){
                            errorline_missing_value += "Publication status is missing.\n";
                        } else if(!publication_status.contains(publicationStatus)){
                            errorline_incorrect_format += "Publication status is not in correct format: " + publicationStatus + "\n";
                        }
                    }


                    if(index_sequencing_platform != -1){
                        String publicationPlatform = line_splitted[index_sequencing_platform].toLowerCase();
                        if(publicationPlatform.equals("")){
                            errorline_missing_value += "Sequencing platform is missing.\n";
                        } else if(!sequencing_platform.contains(publicationPlatform)){
                            errorline_incorrect_format += "Sequencing platform is not in correct format: " + publicationPlatform + "\n";
                        }
                    }

                    if(index_calibrated_date_range_from != -1){
                        String calibrated_date_range_from = line_splitted[index_calibrated_date_range_from].toLowerCase();
                        if(calibrated_date_range_from.equals("")){
                            errorline_missing_value += "Calibrated date lower limit is missing.\n";
                        } else if(!isStringInt(calibrated_date_range_from)){
                            errorline_incorrect_format += "Calibrated date lower limit is not in correct format: " + calibrated_date_range_from + "\n";
                        }
                    }


                    if(index_calibrated_date_range_to != -1){
                        String calibrated_date_range_to = line_splitted[index_calibrated_date_range_to].toLowerCase();
                        if(calibrated_date_range_to.equals("")){
                            errorline_missing_value += "Calibrated date upper limit is missing.\n";
                        } else if(!isStringInt(calibrated_date_range_to)){
                            errorline_incorrect_format += "Calibrated date upper limit is not in correct format: " + calibrated_date_range_to + "\n";
                        }
                    }

                    if(index_C14_age_BP != -1){
                        String c14_age_bp = line_splitted[index_C14_age_BP].toLowerCase();
                        if(c14_age_bp.equals("")){
                            errorline_missing_value += "C14 date BP is missing.\n";
                        } else if(!c14_age_bp.contains("+-")){
                            errorline_incorrect_format += "C14 date BP is not in correct format: " + c14_age_bp + "\n";
                        }
                    }

                    logfile.write("\nMissing values:\n\n");
                    logfile.write(errorline_missing_value);

                    logfile.write("\nIncorrect values:\n\n");
                    logfile.write(errorline_incorrect_format);

                    logfile.write("\n---------------------------------------------------\n");

                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // this will only be reached if file cannot be read
        return false;
    }

    private boolean isStringInt(String s)
    {
        try
        {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
    }

    public static boolean isDateValid(String date)
    {
        String DATE_FORMAT = "yyyy-mm-dd";
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}

# DataValidator

This tool validates the data before uploading to the mitoBench database. 
It checks, if
- all mandatory fields are set
- all values are in correct format
- number of sequences and meta information are equal

The glossary contains a definition for each attribute:
https://docs.google.com/spreadsheets/d/18BsU3wdWvpE5emqy7TUBUO5Si-m-X368D1b-E4s_n5g/edit?usp=sharing

It can either called within the mitoBench workbench or directly as a tool:

**How to run:**

*java -jar mitoBenchDataValidator.jar -i metaInfo.csv -f sequences.fasta*

**Output:** 

Report as text file. This report tells you if data can be uploaded ot not.

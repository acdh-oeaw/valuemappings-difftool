# valuemappings-difftool

## project goal
This project is related to the VLO-mapping project, published on github (see https://github.com/acdh-oeaw/VLO-mapping). The VLO-mapping project 
maps raw facet values, as they come from a CMDI input file, to normalized facet values which are charged to the VLO database. These mappings 
are defined in csv files, where each csv file contains the mappings for one particular source facet. 

Since the raw data alters continuously, each new data import might contain a number of raw facet values for which haven't been mapped to normalized 
values so far. Hence, for each csv mapping file the current facet values have to be looked up to identify the values for which no mapping has been 
defined so far. 
This is what the difftool is doing. It takes the name(s) of one or more cvs files as input parameter, looks up the facet values in the VLO database, 
identifies the values which are not mapped so far and concatenates these values as source values to a new cvs file, with one file per source facet. 

To prevent overwriting of the input file the output file name has the form <source facet name><current time in milliseconds>.csv.

## reuirements
- Java Runtume Environnement (JRE) version 8 or higher
- An internet connection
  

## how to use the tool

To run the difftool, open a text console and execute the following command:

`java -jar difftool [-c <configuration file>] -i <csv input file>[ <csv input file>]...`

The program has two parameters: 
- optional parameter -c followed by the name of one configuration file. 
If not set, the program will use a default configuration file which is included in the jar-archiv. 

**Attention: Since the database is now password protected, the difftool WILL NOT WORK unless you either change the parameter solr_url in the included configuration file or in the external configuration file in the form: 
https://<username>:<password>@<server-url>/solr/vlo-index**

- mandatory parameter -i followed by the name of at least one csv input file
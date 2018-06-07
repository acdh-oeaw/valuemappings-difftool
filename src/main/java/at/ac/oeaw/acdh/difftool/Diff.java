package at.ac.oeaw.acdh.difftool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Diff {
    private static final Logger _logger = LoggerFactory.getLogger(Diff.class);
    
    private Properties properties;
    
    public Diff() throws IOException {
        
        this.properties = new Properties();
        this.properties.load(ClassLoader.getSystemResourceAsStream("difftool.properties"));
    }
    
    public Diff(File configFile) throws FileNotFoundException, IOException {
        this.properties = new Properties();
        this.properties.load(new FileInputStream(configFile));
    }
    
    public static void main(String[] args) throws IOException, SolrServerException {

     // create the command line parser
        CommandLineParser parser = new DefaultParser();

        
        Option configFileName = Option.builder("c")
                .argName("configFileName")
                .desc("fully qualified name of the config file")
                .required(false)
                .hasArg()
                .build();
        
        Option inputFileNames = Option.builder("i")
                .argName("inputFileNames")
                .desc("fully qualified name(s) of the input file(s)")
                .required(true)
                .hasArgs()
                .build();
        
        Options options = new Options();
        
        options.addOption(configFileName);
        options.addOption(inputFileNames);
        
        Diff diff; 
        
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );

            
            if( line.hasOption( "c" ) ) {
                // print the value of block-size
                File configFile = new File(line.getOptionValue("c"));
                if(!configFile.exists()) {
                    _logger.error("configuration file {} doesn't exist", line.getOptionValue("c"));
                    System.exit(1);
                }
                
                diff = new Diff(configFile);
                

                
                
            }
            else {
                _logger.info("no configuration file set - loading default file");
                diff = new Diff();
            }
            
            //processing input files
            for(String inputFileName : line.getOptionValues("i")) {
                diff.process(inputFileName);
            }
        }
        catch( ParseException ex ) {
            _logger.error("", ex);
        }
    }
    
    private void process(String csvFileName) throws IOException, SolrServerException {
        
        SolrManager manager = new SolrManager(this.properties);
        List<FacetField> facetFields = manager.getFacetFields();
        
        CSVProcessor proc = new CSVProcessor(this.properties);
        

        TableModel model = proc.readCSVFile(csvFileName);
        
        List <String> headers = model.getHeaders();
        

        
        for(int column=1; column < headers.size(); column++) {
            if(headers.get(column).equalsIgnoreCase(headers.get(0))) {
                List<String> facetValues = SolrManager.getFacetValues(facetFields, headers.get(column));
                
                for(List<String> row : model.getRows()) {
                    if(row.get(column).equals("!") || row.get(column).isEmpty()) {
                        facetValues.remove(row.get(0));
                        continue;
                    }
                    for(String value : row.get(column).split(this.properties.getProperty("value-delimiter", ";")))
                        facetValues.remove(value.trim());
                }
                
                
                for(String value : facetValues) {
                    List<String> row = new ArrayList<String>();
                    
                    row.add(value);
                    
                    // fill up fields
                    for(int i=1; i < headers.size(); i++)
                        row.add("");
                    
                    model.addRow(row);
                }
                
                break;
            }
        }
        
        model.sortByColumn(0);
        
        proc.writeCSVFile(model, model.getHeaders().get(0).toLowerCase()+ System.currentTimeMillis() + ".csv");
      
        
    }
}

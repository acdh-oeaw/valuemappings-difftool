package at.ac.oeaw.acdh.difftool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Diff {
    private static final Logger _logger = LoggerFactory.getLogger(Diff.class);
    
    final Properties properties;
    
    public Diff() throws IOException {
        
        this.properties = new Properties();
        this.properties.load(ClassLoader.getSystemResourceAsStream("difftool.properties"));
    }
    
    public static void main(String[] args) throws IOException, SolrServerException {

        

        
        //SolrManager manager = new SolrManager(properties);
        
        if(args.length != 1) {
            _logger.equals("java -jar difftool.jar <cvs file>");
            System.exit(1);
        }
        
        File csvFile = new File(args[0]);
        
        if(!csvFile.exists()) {
            _logger.error("csv file {} doesn't exist", csvFile);
            System.exit(1);
        }
        
        new Diff().process(csvFile);
        
    }
    
    private void process(File csvFile) throws IOException, SolrServerException {
        
        SolrManager manager = new SolrManager(this.properties);
        List<FacetField> facetFields = manager.getFacetFields();
        
        CSVProcessor proc = new CSVProcessor(this.properties);
        
        TableModel model = proc.readCSVFile(csvFile);
        
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

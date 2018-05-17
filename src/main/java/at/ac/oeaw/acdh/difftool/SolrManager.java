package at.ac.oeaw.acdh.difftool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;


public class SolrManager {
    private final String SOLR_URL;
    private final String[] FACETS;
    
    public SolrManager(Properties properties) {
        this.SOLR_URL = properties.getProperty("solr-url");
        this.FACETS = properties.getProperty("facets").split(" *, *");
    }
    
    public List<FacetField> getFacetFields() throws SolrServerException, IOException {
        SolrClient solr = new HttpSolrClient.Builder(this.SOLR_URL).build();
        
        
        
        SolrQuery query = new SolrQuery().setQuery("*:*").setFacet(true); 
        
        query.addFacetField(this.FACETS);
        
        return solr.query(query).getFacetFields(); //.getFacetField(facetName).getValues().stream().map(FacetField.Count::getName).collect(Collectors.toList());
    }
    
    public static List<String> getFacetValues(List<FacetField> facetFields, String facetName) {
        ArrayList<String> list = new ArrayList<String>();
        
        for(FacetField ffield : facetFields) {
            if(ffield.getName().equalsIgnoreCase(facetName)) {
                for(FacetField.Count count : ffield.getValues()) {
                    list.add(count.getName());
                }
                
                break;
            }
        }
        
        return list;
    }
}
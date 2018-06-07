package at.ac.oeaw.acdh.difftool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class CSVProcessor {
    private final String FIELD_DELIMITER;
    private final String TEXT_DELIMITER;
    
    public CSVProcessor(Properties properties) {
        this.FIELD_DELIMITER = properties.getProperty("field-delimiter", ",");
        this.TEXT_DELIMITER  = properties.getProperty("text-delimiter", "\"");
    }
    
    
    public TableModel readCSVFile(String csvFileName) throws IOException {
        
        BufferedReader reader = new BufferedReader(new FileReader(csvFileName));
        
        String line;
        
        List<String> headers = getFields(reader.readLine());
        List<String> row;
        
        TableModel model = new TableModel(headers);

        
        while((line = reader.readLine()) != null){
            
            row = getFields(line);
            
            model.addRow(row);
        }
        
        reader.close();
        
        return model;
    }
    
    public void writeCSVFile(TableModel model, String csvFileName) throws IOException {
        File csvFile = new File(csvFileName);
        
        csvFile.createNewFile();
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));
        
        //write header
        writeRow(model.getHeaders(), writer);
        
        //write rows
        for(List<String> row : model.getRows()) {

                writer.newLine();
                writeRow(row, writer);
        }
        
        writer.close();
    }
    
    private void writeRow(List<String> row, BufferedWriter writer) throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        for(String field : row) {
            strBuilder.append(this.FIELD_DELIMITER);
            strBuilder.append("\"");
            strBuilder.append(field);
            strBuilder.append("\"");
        }
        
        writer.write(strBuilder.toString().substring(1));
    }
    
    private List<String> getFields(String line) {
        ArrayList<String> row = new ArrayList<String>();
        
        int startIndex = 0 ;
        int endIndex; 
        
        while(startIndex <= line.length()) {
            if(line.indexOf(TEXT_DELIMITER, startIndex) == startIndex) {
                endIndex = line.indexOf(TEXT_DELIMITER, startIndex +1);
                row.add(line.substring(startIndex +1, endIndex));
                startIndex = endIndex +2;
            }
            else {
                endIndex = line.indexOf(FIELD_DELIMITER, startIndex);
                if(endIndex == -1) {
                    row.add(line.substring(startIndex));
                    break;
                }
                else {
                    row.add(line.substring(startIndex, endIndex));
                    startIndex = endIndex +1;
                }
            }
        }
        
        return row;
    }
}

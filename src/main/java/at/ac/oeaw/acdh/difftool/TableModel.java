package at.ac.oeaw.acdh.difftool;

import java.util.ArrayList;
import java.util.List;

public class TableModel {
    private List<String> headers;
    
    private List<List<String>> rows;
    
    public TableModel(List<String> headers) {
        this.headers = headers;
        this.rows = new ArrayList<List<String>>();
    }
    
    public void sortByColumn(int columnNumber) {
        rows.sort((a, b) -> a.get(columnNumber).compareToIgnoreCase(b.get(columnNumber)));       
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<List<String>> getRows() {
        return this.rows;
    }

    public void setSetRows(List<List<String>> rows) {
        this.rows.addAll(rows);
    }
    
    public void addRow(List<String> row) {
        this.rows.add(row);
    }
}

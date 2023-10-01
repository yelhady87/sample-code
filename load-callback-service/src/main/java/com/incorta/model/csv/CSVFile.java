package com.incorta.model.csv;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CSVFile {
    private final List<String> headers;
    private final List<CSVRow> rows;
    private final String filePath;

    public Object[] getCSVHeaders() {
        return headers.toArray();
    }
}

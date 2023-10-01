package com.incorta.services;

import com.incorta.model.AddRecordRequest;
import com.incorta.model.csv.CSVFile;
import com.incorta.model.csv.CSVRow;
import com.incorta.util.DateUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CSVFileUtils {

    private static final Logger logger = LoggerFactory.getLogger(CSVFileUtils.class);


    public static CSVFile constructCSVFile(AddRecordRequest request) {
        String[] headers = request.getHeaders();
        List<String[]> updatedRows = request.getUpdatedRows();
        List<CSVRow> rows = new ArrayList<>();
        for (String[] row : updatedRows) {
            CSVRow csvRow = CSVRow.builder()
                    .data(row)
                    .uuid("uuid-" + UUID.randomUUID().toString())
                    .insightId(request.getInsightId())
                    .createdBy(request.getModifiedBy())
                    .creationDate(request.getModificationDate())
                    .modifiedBy(null)
                    .modificationDate(null)
                    .status("pending")
                    .build();
            rows.add(csvRow);
        }

        List<String> headersList = new ArrayList<>(Arrays.asList(headers));
        headersList.add("uuid");
        headersList.add("insight_id");
        headersList.add("created_by");
        headersList.add("creation_date_epoch");
        headersList.add("creation_date");
        headersList.add("modified_by");
        headersList.add("modification_date_epoch");
        headersList.add("modification_date");
        headersList.add("status");

        return CSVFile.builder()
                .headers(headersList)
                .rows(rows)
                .build();
    }

    public static byte[] readFileBytes(String fileName) {
        File file = new File(fileName);
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] result = new byte[(int) file.length()];
            fis.read(result);
            return result;

        } catch (IOException e) {
            logger.error("Cannot read file bytes", e);
            return new byte[] {0}; //return an empty array
        }

    }

    public static String flushToDisk(CSVFile file) throws IOException {
        String tmpFile = "out_" + DateUtils.toDateTimestamp(System.currentTimeMillis()) + ".csv";
        try (FileWriter fw = new FileWriter(tmpFile, false);
             CSVPrinter cp = new CSVPrinter(fw, CSVFormat.DEFAULT)) {
            cp.printRecord(file.getCSVHeaders());

            for (CSVRow row : file.getRows()) {
                cp.printRecord(row.toCSVRow());
            }
        }

        return tmpFile;
    }


    static Long readLongValue(String number) {
        try {
            return Long.valueOf(number);

        } catch (NumberFormatException e) {
            return null;
        }
    }


}

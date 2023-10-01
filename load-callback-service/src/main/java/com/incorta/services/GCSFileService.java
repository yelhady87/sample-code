package com.incorta.services;

import com.incorta.util.DateUtils;
import com.incorta.exceptions.GenericRuntimeException;
import com.incorta.model.AddRecordRequest;
import com.incorta.model.UpdateRecordRequest;
import com.incorta.model.csv.CSVFile;
import com.incorta.model.csv.CSVRow;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

import static com.incorta.services.CSVFileUtils.*;

@Component
public class GCSFileService {
    private static final Logger logger = LoggerFactory.getLogger(GCSFileService.class);

    @Autowired
    private GCSBucketFileSystem fs;

    public void addRecord(AddRecordRequest request) throws GenericRuntimeException {
        String tempFile = "in_" + DateUtils.toDateTimestamp(System.currentTimeMillis()) + ".csv";
        logger.info("Writing to temp file: {}", tempFile);
        CSVFile csvFile = constructCSVFile(request);

        // Write to a temp file on local disk before writing to GCS
        try (FileWriter fw = new FileWriter(tempFile, true);
             CSVPrinter cp = new CSVPrinter(fw, CSVFormat.DEFAULT)) {
            File file = new File(tempFile);
            boolean empty = file.exists() && file.length() == 0;
            if (empty) {
                cp.printRecord(csvFile.getCSVHeaders());
            }

            for (CSVRow row : csvFile.getRows()) {
                cp.printRecord(row.toCSVRow());
            }

        } catch (IOException e) {
            logger.error("Error writing to CSV file", e);
            throw new GenericRuntimeException("Error writing to CSV file", e);
        }

        // Write the final file contents to GCS
        flushToBucket(tempFile, request.getDataFile());
    }

    private void flushToBucket(String srcFilename, String destFilename) {
        byte[] content = readFileBytes(srcFilename);
        fs.writeContentToFile(destFilename, content);
    }

    public void updateRecords(UpdateRecordRequest request) throws IOException {
        CSVFile file = readCSVFileFromBucket(request.getDataFile());
        for (String uuid : request.getUuids()) {
            for (CSVRow row : file.getRows()) {
                if (row.getUuid().contentEquals(uuid)) {
                    row.setStatus(request.getStatus());
                    row.setModifiedBy(request.getModifiedBy());
                    row.setModificationDate(request.getModificationDate());
                    break;
                }
            }
        }
        String tmpFile = flushToDisk(file);
        flushToBucket(tmpFile, file.getFilePath());
    }

    private CSVFile readCSVFileFromBucket(String filePath) throws IOException {
        List<CSVRecord> records = new ArrayList<>();
        List<CSVRow> rows = new ArrayList<>();
        CSVFormat format = CSVFormat.DEFAULT.builder().build();

        //read the contents of the remote file and write them to a temp file
        String fileContents = fs.getFileContents(filePath);
        try (Reader reader = new StringReader(fileContents);
             CSVParser csvParser = new CSVParser(reader, format)) {
            for (CSVRecord csvRecord : csvParser) {
                records.add(csvRecord);
            }
        }

        //capture the headers
        CSVRecord header = records.get(0);
        for (int row = 1; row < records.size(); row++) {
            CSVRecord csvRecord = records.get(row);

            //Capture generated fields
            int generatedFieldsSize = 9;
            int colSize = csvRecord.size();

            int start = colSize - generatedFieldsSize;
            List<String> generatedFields = new ArrayList<>();
            for (; start < colSize; start++) {
                generatedFields.add(csvRecord.get(start));
            }

            // Capture actual data
            List<String> data = new ArrayList<>();
            int actualColSize = csvRecord.size() - generatedFieldsSize; //5 generated fields like uuid,insight_id,modified_by,modification_date,status
            for (int column = 0; column < actualColSize; column++) {
                data.add(csvRecord.get(column));
            }

            CSVRow csvRow = CSVRow.builder()
                    .data(data.toArray(new String[0]))
                    .uuid(generatedFields.get(0))
                    .insightId(generatedFields.get(1))
                    .createdBy(generatedFields.get(2))
                    .creationDate(readLongValue(generatedFields.get(3)))
                    //4 -> formatted creation date
                    .modifiedBy(generatedFields.get(5))
                    .modificationDate(readLongValue(generatedFields.get(6)))
                    //7 -> formatted modification date
                    .status(generatedFields.get(8))
                    .build();

            rows.add(csvRow);
        }

        return CSVFile.builder()
                .filePath(filePath)
                .headers(header.toList())
                .rows(rows)
                .build();

    }

}

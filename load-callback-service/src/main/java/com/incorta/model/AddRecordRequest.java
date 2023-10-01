package com.incorta.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddRecordRequest {
    private String insightId;
    private String tableName; //TODO: delete me //should be the fully qualified name of the table ex: SCHEMA.TABLE
    private String modifiedBy; //TODO: change to created by
    private long modificationDate; //TODO: change to creation date
    private String[] headers;
    private List<String[]> updatedRows;
    private String dataFile;
}

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
public class UpdateRecordRequest {
    private List<String> uuids;
    private String status;
    private String modifiedBy;
    private Long modificationDate;
    private String dataFile;
    private String tableName;
}

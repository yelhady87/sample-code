package com.incorta.model.csv;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class CSVRow {
    private String[] data;

    //Auto populated fields from the system
    private String uuid;
    private String insightId;
    private String createdBy;
    private Long creationDate;
    private String modifiedBy;
    private Long modificationDate;
    private String status;

    public Object[] toCSVRow() {
        List<String> data = new ArrayList<>();
        for (String s : this.data) {
            data.add(s);
        }

        data.add(uuid);
        data.add(insightId);
        data.add(createdBy);
        data.add(String.valueOf(creationDate));
        data.add(new Date(creationDate).toString());

        data.add(modifiedBy);
        if (modificationDate != null) {
            data.add(String.valueOf(modificationDate));
            data.add(new Date(modificationDate).toString());

        } else {
            data.add(""); //modified by epoch
            data.add(""); // modified by date
        }

        data.add(status);

        return data.toArray();
    }
}

package com.incorta.webapi.resources;

import com.incorta.IncortaLoaderService;
import com.incorta.config.IncortaConfiguration;
import com.incorta.config.WebAppConfiguration;
import com.incorta.exceptions.GenericRuntimeException;
import com.incorta.model.OperationResponse;
import com.incorta.model.UpdateRecordRequest;
import com.incorta.model.AddRecordRequest;
import com.incorta.services.GCSFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/push-data")
public class PushDataResource {

    @Autowired
    WebAppConfiguration config;

    @Autowired
    IncortaConfiguration incortaConfig;

    @Autowired
    IncortaLoaderService incorta;

    @Autowired
    GCSFileService repository;

    @PostMapping("/add-data")
    public OperationResponse addData(
            @RequestBody
            AddRecordRequest request) throws GenericRuntimeException {

        repository.addRecord(request);

        if (incortaConfig.isAlwaysLoad()) {
            IncortaLoaderService.LoadResponse response = incorta.startLoad(request.getTableName());
            Map<String, String> serviceResponse =  response.getLoadingStatus();
            if (serviceResponse != null && serviceResponse.get(request.getTableName()) != null) {
                String loadResult = serviceResponse.get(request.getTableName());
                String message = String.format("Data written successfully to [%s] and schema [%s] load state is [%s]", request.getDataFile(), request.getTableName(), loadResult);
                return OperationResponse.builder()
                        .message(message)
                        .build();
            }
        }

        return OperationResponse.builder()
                .message(String.format("Data consumed successfully and written to CSV file [%s].", request.getDataFile()))
                .build();
    }

    @PostMapping("/update-data")
    public OperationResponse updateFile(
            @RequestBody
            UpdateRecordRequest request
    ) throws IOException {

        repository.updateRecords(request);

        if (incortaConfig.isAlwaysLoad()) {
            IncortaLoaderService.LoadResponse response = incorta.startLoad(request.getTableName());
            Map<String, String> serviceResponse =  response.getLoadingStatus();
            if (serviceResponse != null && serviceResponse.get(request.getTableName()) != null) {
                String loadResult = serviceResponse.get(request.getTableName());
                String message = String.format("Data updated successfully to [%s] and schema [%s] load state is [%s]", request.getDataFile(), request.getTableName(), loadResult);
                return OperationResponse.builder()
                        .message(message)
                        .build();
            }
        }

        String message = String.format("File [%s] updated successfully.", request.getDataFile());
        return OperationResponse.builder()
                .message(message)
                .build();
    }

    @PostMapping("/load-data")
    public OperationResponse startLoad(
            @RequestBody
            Map<String, String> request) {

        String tableName = request.get("tableName");
        IncortaLoaderService.LoadResponse response = incorta.startLoad(tableName);
        Map<String, String> serviceResponse = response.getLoadingStatus();
        if (serviceResponse != null && serviceResponse.get(tableName) != null) {
            String loadResult = serviceResponse.get(tableName);
            String message = String.format("Schema [%s] load state is [%s]", tableName, loadResult);
            return OperationResponse.builder()
                    .message(message)
                    .build();
        }

        return OperationResponse.builder()
                .message("Invalid load data request!")
                .build();

    }
}

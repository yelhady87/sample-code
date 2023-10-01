# The backend for the Write Back Insight
The service performs the following operations:
- In case of calling `/add-data`:
  - the contents of the `updatedRows` are written to a CSV file under the provided path in `dataFile`
  - if `incorta.always-load=true` then the service will trigger a schema load using Incorta's Public API configured under `incrota.load-service-url`
- In case of calling `/update-data`:
  - the service will update the `status` column of the provided `uuids` list
- In case of calling `/load-data`:
  - the service will trigger a schema load on the provided schema name using the configured `incorta.load-service-url`




# To run locally
- rename the `application.yml.example` file to `application.yml` and add the appropriate configurations
- execute `> ./gradlew bootRun` from the command line.

The server starts on `http://localhost:8088`

---
# Below are sample CURL commands
### CURL Command to add data

```
curl --request POST \
  --url http://localhost:8088/push-data/add-data \
  --header 'Content-Type: application/json' \
  --header 'authorization: KN2wjt3qKGaMuN9xBo1E' \
  --data '{
	"insightId": "insight-1",
	"schemaName": "SALES",
	"tableName": "SCHEMA_NAME",
	"modifiedBy": "admin",
	"modificationDate": "1692543223775",
	"dataFile": "IncortaAnalytics/Tenants/demo/data/push_data_2.csv",
	"updatedRows": [
		["ACCOUNTING", "100000", "field_2"],
		["MARKETING", "20123456897", "field_3"],
		["MARKETING", "20123456897", "field_3"]
	],
	"headers": ["departement", "salary", "custom_field_header"]
}'
```

---
### CURL Command to update data

```
curl --request POST \
  --url http://localhost:8088/push-data/update-data \
  --header 'Content-Type: application/json' \
  --header 'authorization: KN2wjt3qKGaMuN9xBo1E' \
  --data '{
	"status": "approved",
	"dataFile": "IncortaAnalytics/Tenants/demo/data/push_data_2.csv",
	"modifiedBy": "ADHAM",
	"modificationDate": "1692543223775",
	"uuids": [
		"uuid-65303613-1cc4-4cd3-8197-49e487834535"
	],
	"tableName": "SCHEMA_NAME"
}'
```

---
### CURL Command to trigger a schema load

```
curl --request POST \
  --url http://localhost:8088/push-data/load-data \
  --header 'Content-Type: application/json' \
  --header 'authorization: KN2wjt3qKGaMuN9xBo1E' \
  --data '{
	"tableName": "SCHEMA_NAME"
}'
```

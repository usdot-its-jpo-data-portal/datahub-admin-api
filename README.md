# datahub-admin-api
DataHub Admin API
> Version: 1.3.0

The Admin API of DataHub has the function to administer the metadata information (documents) for the DataHub. The API connect to an ElasticSearch storage system. The API will do actions on the _configurations_ and _dataassets_ indexes. 

## Change Log
Changes related to the previous version.

> Previous Version: 1.2.0
- Update to Java 11

## Usage
Once the application is running on a configured port the API uses the standard REST verbs to manipulate the data.

- GET: List the available data.
- POST: Creates a new document/record.
- PUT: Updates an existing document/record.
- DELETE: Deletes one or more document(s).

Two sets of actions were defined to administer the data for each Elasticsearch index

---
## Header Token Validation
The API is expecting to receive a TOKEN in the header under the name DHTOKEN. This value needs to be provided as part of the configuration of the API. Failing to provide the DHTOKEN will return in FORBIDDEN.

```
HTTP/1.1 403 Forbidden
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
```

---

## Projects
The following entries are related to the Projects administration under the _configurations_.

### List Projects

```
GET /api/v1/configurations/projects HTTP/1.1
DHTOKEN: 123
```

### Add Project
```json
POST /api/v1/configurations/projects HTTP/1.1
Content-Type: application/json
DHTOKEN: 123

{
  "id" : "41bc872f-143c-4694-a253-b25051dbb329",
  "name" : "Project-1",
  "description" : "Description Project 1",
  "lastModified" : "2020-04-06T20:33:02.953+0000",
  "orderPopular" : 1,
  "imageFileName" : "http://example.com/image",
  "isPopular" : false,
  "isEnabled" : true
}
```

### Update Project
```json
PUT /api/v1/configurations/projects HTTP/1.1
Content-Type: application/json
DHTOKEN: 123

{
  "id" : "6aed36b6-02d0-4fef-8050-b612393cb247",
  "name" : "Project-1",
  "description" : "Description Project 1",
  "lastModified" : "2020-04-06T20:33:02.925+0000",
  "orderPopular" : 1,
  "imageFileName" : "http://example.com/image",
  "isPopular" : false,
  "isEnabled" : true
}
```

### Delete Project

 - Method: DELETE
 - URL: http://[host:port]/api/v1/repositories/{ID}
 - Content-Type: application/json

```
DELETE /api/v1/configurations/projects/343f646a-6db0-47bc-8ccb-f54a9b8cc2a8 HTTP/1.1
DHTOKEN: 123
```

---

## DataTypes
The following entries are related to the DataTypes administration under the _configurations_.

### List DataTypes

```
GET /api/v1/configurations/datatypes HTTP/1.1
DHTOKEN: 123
```

### Add DataType
```json
POST /api/v1/configurations/datatypes HTTP/1.1
Content-Type: application/json
DHTOKEN: 123

{
  "id" : "efd3f449-5a3e-45f0-b3b6-ead5c324598d",
  "name" : "DataType-1",
  "description" : "Description for DataType 1",
  "lastModified" : "2020-04-17T15:45:18.583+0000",
  "isEnabled" : true
}
```

### Update DataType
```json
PUT /api/v1/configurations/datatypes HTTP/1.1
Content-Type: application/json
DHTOKEN: 123

{
  "id" : "08dbc2d1-0dd7-4d90-b48e-76d76750bb3c",
  "name" : "DataType-1",
  "description" : "Description for DataType 1",
  "lastModified" : "2020-04-17T15:45:18.678+0000",
  "isEnabled" : true
}
```

### Delete DataType

```
DELETE /api/v1/configurations/datatypes/50bc6272-a91b-4c16-b5c1-a42f6f596bdb HTTP/1.1
DHTOKEN: 123
```

---

## Engagement Popup
The following entries are related to the Engagement Popup administration under the _configurations_.

### List Engagement Popups

```
GET /api/v1/configurations/engagementpopups HTTP/1.1
DHTOKEN: 123
```

### Add Engagement Popup
```json
POST /api/v1/configurations/engagementpopups HTTP/1.1
Content-Type: application/json
DHTOKEN: 123

{
  "id" : null,
  "name" : "EngagementPopup-1",
  "description" : "description 1",
  "lastModified" : "2020-05-06T16:03:43.474+0000",
  "content" : "content-1",
  "controlsColor" : "black",
  "controlsShadow" : "white",
  "isActive" : true
}
```

### Update Engagement Popup
```json
PUT /api/v1/configurations/engagementpopups HTTP/1.1
Content-Type: application/json
DHTOKEN: 123

{
  "id" : "3bb0fb6f-144d-419c-bf4d-dacd962e833d",
  "name" : "EngagementPopup-1",
  "description" : "description 1",
  "lastModified" : "2020-05-06T16:03:43.352+0000",
  "content" : "content-1",
  "controlsColor" : "black",
  "controlsShadow" : "white",
  "isActive" : true
}
```

### Delete Engagement Popup

```
DELETE /api/v1/configurations/engagementpopups/dd13fe91-5fb1-46c3-b653-f068bb50053c HTTP/1.1
DHTOKEN: 123
```

---
## Data Assets
The following entries are related to the DataAssets administration.

### List DataAssets

```
GET /api/v1/dataassets HTTP/1.1
DHTOKEN: 123
```

### List DataAsset by ID

```
GET /api/v1/dataassets/4c86297e-1b47-4d78-9071-96590278c38f HTTP/1.1
DHTOKEN: 123
```

### Update DataAsset

 ```json
PUT /api/v1/dataassets HTTP/1.1
Content-Type: application/json
DHTOKEN: 123

{
  "id" : "dd03dcda-3d15-4235-b55a-5ef051b9672f",
  "name" : "DataAsset-1",
  "description" : "Description",
  "accessLevel" : "Public",
  "lastUpdate" : "2020-04-06T20:33:03.511+0000",
  "tags" : [ "tag-1", "tag-2", "tag-3" ],
  "sourceUrl" : "http://to.source.url",
  "dhId" : "5de9bd66-fe3a-4bd3-b3c5-8c1d110ccb1c",
  "dhLastUpdate" : "2020-04-06T20:33:03.511+0000",
  "dhSourceName" : "source-name",
  "esScore" : 1.0,
  "metrics" : {
    "downloadsTotal" : 247,
    "pageViewsLastMonth" : 8,
    "pageViewsTotal" : 1025
  },
  "dhProjects" : [ "298de0d6-6b61-409a-b2b9-16f062658a40", "e3a81572-1bdb-4a45-829a-aab719033a40" ],
  "dhDataTypes" : [ "a3130497-ed01-4bca-a5db-0e3ad9c30989", "600d02a4-f770-48ae-a998-692348aee539" ]
}
```

## Configuration
The API requires the following environment variables

 
|Name   |Required   |Default   |Description|
|--|--|--|----|
|DATAHUB_ADMIN_API_ES_HOST|mandatory||Sets the host of the target ElasticSearch|
|DATAHUB_ADMIN_API_ES_PORT|mandatory||Sets the port that the target ElasticSearch is using|
|DATAHUB_ADMIN_API_ES_SCHEME|mandatory||Sets the protocol scheme used by the target ElasticSearch (http or https)|
|DATAHUB_ADMIN_API_SECURITY_TOKEN_NAME|optional|DHTOKEN|Token name for request authorization|
|DATAHUB_ADMIN_API_SECURITY_TOKEN_KEY|mandatory||Expected Token value for request authorization|
|DATAHUB_ADMIN_API_CONFIGURATIONS_INDEX|mandatory|configurations|Configurations Index name|
|DATAHUB_ADMIN_API_CONFIGURATIONS_DEFAULT|mandatory|datahub-default-configuration|Configuration name to be use by the API|
|DATAHUB_ADMIN_API_ES_DATAASSETS_INDEX|optional|dataassets|Index name in ElasticSearch that contains the DataAssets|
|DATAHUB_ADMIN_API_ES_SORT_BY|optional|lastUpdate|Field name that will be used for default sorting|
|DATAHUB_ADMIN_API_ES_SORT_ORDER|optional|desc|Sorting direction (asc, desc)|
|DATAHUB_ADMIN_API_ORIGINS|optional|*|Whitelist clients to avoid CORS|
|SERVER_SERVLET_CONTEXT-PATH|optional|/api|Set the DataHub Web API context path|
|SERVER_PORT|optional|3008|Sets the DataHub Admin API listening port|
|DATAHUB_ADMIN_API_CONFIGURATIONS_IMAGES_LIST|optional|none|URL to the file that contains the JSON array of image file names|
|DATAHUB_ADMIN_API_CONFIGURATIONS_IMAGES_PATH|optional|none|Base image path name|

## Installation
The API is a Java application and can be executed updating the values of the following command template.

```bash
sh -c java -Djava.security.egd=file:/dev/./urandom -jar /datahub-admin-api-1.3.0.jar"
```
It is important to setup the environment variables before to execute the application.

The API documentation is embedded in the application as static html file, this can be accessed using the following URL template (DHTOKEN needs to be provided).

```bash
  http://[host:port]/api/index.html
```

## File Manifest
* src/main : Contains the source code
* src/test : Contains the unit testing code.
* Dockerfile: Docker image definition file


## Development setup
> The API was developed using [Spring Tool Suite 4](https://spring.io/tools/) that is base on [Eclipse](https://www.eclipse.org/ide/)

1. Install and open Spring Tool Suit
2. Configure the required environment variables
3. Debug/Run as Spring Boot application, after this step the application will be running and ready to receive request.

## Docker Support
A [Docker](https://www.docker.com/) image can be build with the next command line.
```bash
  docker build -t datahub-admin-api:latest .
```

The following command with the correct values for the environment variable will start a Docker container.
```bash
docker run -p 3008:3008 --rm \
-e "SERVER_PORT=3008" \
-e "DATAHUB_ADMIN_API_TOKEN_KEY=[DHTOKEN]" \
-e "DATAHUB_ADMIN_API_ES_HOST=[HOST]" \
-e "DATAHUB_ADMIN_API_ES_PORT=[PORT]" \
-e "DATAHUB_ADMIN_API_ES_SCHEME=[SCHEME]" \
-e "JAVA_OPTS=-Xmx512M -Xms512M" \
-t -i datahub-admin-api:latest
```


## Release History
* 1.0.0
  * Initial version
* 1.1.0
  * Support for DataTypes
* 1.2.0
  * Support for Engagement Popups
* 1.3.0
  * Update to Java 11


## Contact information
Joe Doe : X@Y

Distributed under APACHE 2.0 license. See *LICENSE* for more information

## Contributing
1. Fork it (https://github.com/usdot-its-jpo-data-portal/datahub-admin-api/fork)
2. Create your feature branch (git checkout -b feature/fooBar)
3. Commit your changes (git commit -am 'Add some fooBar')
4. Push to the branch (git push origin feature/fooBar)
5. Create a new Pull Request

## Known Bugs
*

## Credits and Acknowledgment
Thank you to the Department of Transportation for funding to develop this project.

## CODE.GOV Registration Info
* __Agency:__ DOT
* __Short Description:__ DataHub Admin API to interface ITS DataHub ElasticSearch.
* __Status:__ 
* __Tags:__ DataHub, DOT, Spring Boot, Java, ElasticSearch
* __Labor Hours:__
* __Contact Name:__
* __Contact Phone:__

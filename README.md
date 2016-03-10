# simplexdb
MapDB/BerkeleyDB Database and Spring REST Interface

The goal of this project is to create a portable storage for moving multiple files providing the
lightest possible database with a REST interface.

**Master**

[![Build Status](https://travis-ci.org/lcappuccio/simplexdb.svg?branch=master)](https://travis-ci.org/lcappuccio/simplexdb)
[![codecov.io](https://codecov.io/github/lcappuccio/simplexdb/coverage.svg?branch=master)](https://codecov.io/github/lcappuccio/simplexdb?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/68207375c31d4510afbad94e3f3a543a)](https://www.codacy.com/app/leo_4/simplexdb)

**Develop**

[![Build Status](https://travis-ci.org/lcappuccio/simplexdb.svg?branch=develop)](https://travis-ci.org/lcappuccio/simplexdb)
[![codecov.io](https://codecov.io/github/lcappuccio/simplexdb/coverage.svg?branch=develop)](https://codecov.io/github/lcappuccio/simplexdb?branch=develop)

## Supported Databases
- **MapDB**
- **BerkeleyDB**

## Usage

### Install
Build with maven and run the artifact as any java application

- `mvn clean install`
- `java -jar $artifact-name.jar`

## Configuration
Create folder `config` and file `application.properties`, change the settings as you please.
Have a look at the properties file and specify the mandatory parameters:
- database.type
- database.filename
- storage.folder

Additional properties: [Spring Docs](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)

## Insert Data
To insert data massively use `insert_files.sh` or the included JMeter plan.

### JMeter Plan Usage
- Replace `$FILE_LIST_PATH` with the path where your files are located (e.g. /home/foo/data)
- Replace `$FILE_LIST_CSV` with the path pointing to a csv with a full file list (e.g. /home/foo/data/file_list.csv)

#### Example Variables
- `$FILE_LIST_PATH`: /home/foo/data
- `$FILE_LIST_CSV`: /home/foo/data/file_list.csv

#### file_list.csv
file1.txt

file2.txt

file3.txt

...

fileN.txt

## Endpoints
The application is deployed to http://host:port/simplexdb/$action where **host** and **port** depend on the
environment.

Replace $action with:
- **save**: stores a file in the database
- **findall**: obtain a full list of the stored data (ids only) in JSON format
- **findbyid**: will download and save to disk the file with the corresponding id, if the same id was previously saved
the old file will be renamed as `YYYYMMDDHHmmSS_$filename`
- **findbyname**: obtain a full list of the stored data in JSON format with name matching the searched string
- **delete**: deletes the specified id entry from the database
- **export**: will write to disk all data currently stored in the database

Automated documentation provided by Swagger: [API Documentation](http://localhost:8080/swagger-ui.html)

## Frontend
A [console](http://localhost:8080/simplexdb/view) is deployed automatically and embedded in the project.
It is basically a thymeleaf template with some simple javascript code and bootstrap css.

## Monitoring

Actuators are deployed (e.g.), verify `management.port` in `application.properties`:

* [autoconfig](http://localhost:8080/autoconfig)
* [beans](http://localhost:8080/beans)
* [metrics](http://localhost:8080/metrics)

Further info: [Spring Reference](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#production-ready-endpoints)

## Performance
- Testbed: ASUS K52F, Intel Core i3@2.13GHz, 8Gb RAM, Intel SSD X-25M 80Gb
- Uploaded with the JMeter plan running on the same machine
- **MapDB**: Throughput: 50 files/sec, 13Kb/sec
- **Berkeley DB**: Throughput: 125 files/sec, 33Kb/sec

# ToDo
- Authentication
- Spring actuators integration in UI
- Pending MapDB 3.0 release
# simplexdb
MapDB Database and Spring REST Interface

The goal of this project is to create a portable storage for moving multiple files providing the
lightest possible database with a REST interface.

**Master**

[![Build Status](https://travis-ci.org/lcappuccio/simplexdb.svg?branch=master)](https://travis-ci.org/lcappuccio/simplexdb)

**Develop**

[![Build Status](https://travis-ci.org/lcappuccio/simplexdb.svg?branch=develop)](https://travis-ci.org/lcappuccio/simplexdb)

## Usage

### Install
Build with maven and run the artifact as any java application

- `mvn clean install`
- `java -jar $artifact-name.jar`

### Insert Data

To insert data massively use `insert_files.sh` or the included JMeter test plan.

## Settings

Create folder `config` and file `application.properties`, change the settings as you please.

Additional properties: [Spring Docs](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)

## Endpoints

The application is deployed to http://host_id:port/simplexdb

- **save**: stores a file in the database
- **findall**: obtain a full list of the stored data (ids only) in JSON format
- **findbyid**: will download and save to disk the file with the corresponding id, if a the same id was previously saved
the old file will be renamed as `YYYYMMDDHHmmSS_$filename`
- **findbyname**: will scan the db to find all the ids that contain the string to search
- **delete**: deletes from the database the specified id
- **export**: will write to disk all data currently stored in the database
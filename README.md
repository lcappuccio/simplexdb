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

To insert data massively use `insert_files.sh` or the JMeter test plan.

## Settings

Create folder `config` and file `application.properties`, change properties as you please.
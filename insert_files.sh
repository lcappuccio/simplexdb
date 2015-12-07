#!/bin/bash
FILES=/home/lcappuccio/DataSets/dataset_insert_CON_2015/tallysheets/*
for f in $FILES
do
	echo "Insert file $f" 
	curl -F "file="$f http://localhost:8080/simplexdb/save 
done

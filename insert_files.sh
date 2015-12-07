#!/bin/bash
FILES=PATH_TO_FILES/*
for f in $FILES
do
	echo "Insert file $f" 
	curl -F "file="@$f http://localhost:8080/simplexdb/save 
done

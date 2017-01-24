#!/bin/bash

# DEBUG
# -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005

# Profile with Flight Recorder
# -ea -XX:+UnlockCommercialFeatures -XX:+FlightRecorder -XX:StartFlightRecording=name=test_results.jfr -XX:FlightRecorderOptions=settings=max-settings.jfc,defaultrecording=true,dumponexit=true,dumponexitpath=test_results.jfr

java -jar simplexdb-VERSION.jar

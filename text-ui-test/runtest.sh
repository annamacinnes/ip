#!/usr/bin/env bash

# Create bin directory if it doesn't exist
if [ ! -d "../bin" ]; then
    mkdir ../bin
fi

# Remove output from previous run
rm -f ACTUAL.TXT

# Compile all Java files recursively
if ! javac -Xlint:none -d ../bin $(find ../src/main/java -name "*.java"); then
    echo "********** BUILD FAILURE **********"
    exit 1
fi

# Run using fully qualified class name
java -cp ../bin chatty.Chatty < input.txt > ACTUAL.TXT

# Compare output
diff ACTUAL.TXT EXPECTED.TXT
if [ $? -eq 0 ]; then
    echo "Test result: PASSED"
    exit 0
else
    echo "Test result: FAILED"
    exit 1
fi

#!/usr/bin/env bash

# Create bin directory if it doesn't exist
if [ ! -d "../bin" ]; then
    mkdir ../bin
fi

# Remove output from previous run
if [ -e "./ACTUAL.TXT" ]; then
    rm ACTUAL.TXT
fi

# Compile the code (stop if compilation fails)
if ! javac -cp ../src/main/java -Xlint:none -d ../bin ../src/main/java/*.java; then
    echo "********** BUILD FAILURE **********"
    echo "********** BUILD FAILURE **********"
    exit 1
fi

# Run the program using input.txt and save output
java -classpath ../bin Chatty < input.txt > ACTUAL.TXT

# Compare actual output with expected output
diff ACTUAL.TXT EXPECTED.TXT
if [ $? -eq 0 ]; then
    echo "Test result: PASSED"
    exit 0
else
    echo "Test result: FAILED"
    exit 1
fi

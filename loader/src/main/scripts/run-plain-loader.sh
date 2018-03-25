#!/usr/bin/env bash
# This script is for invoking the generic command line loader for getting files into Cassandra
set -e

# fix the pathing later
CASSANDRA_LOADER_LIB_PATH=.

# this will put everything matching the wildcard into the classpath
java -cp $CASSANDRA_LOADER_LIB_PATH/loader-*.jar us.catalist.mdr.loader.cli.CommandLineLoader "$@"


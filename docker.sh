#!/usr/bin/env sh

GENERATED_CSV="${GENERATED_CSV:-rapidminer_output_data.csv}"

echo "Outputting to $GENERATED_CSV"

VERSION="$1"
shift

CONTAINER_NAME_AND_TAG="aiad-$VERSION"

docker build -t "$CONTAINER_NAME_AND_TAG" .
# Because of https://stackoverflow.com/a/47099098/5437511
touch "$GENERATED_CSV"
# Use -e ENV_VAR=value to override values in create_dataset.js
# Using a volume so that the csv sync to the host. Could also copy but that requires the container to be running and it is not when it is done
docker run --rm -v "$(pwd)/$GENERATED_CSV:/aiad/$GENERATED_CSV" --name "$CONTAINER_NAME_AND_TAG" -e GENERATED_CSV="$GENERATED_CSV" "$@" -it "aiad-$VERSION"


# AIAD miguel-xps 1:
# GENERATED_CSV=rapidminer_miguel-xps-1.csv ./docker.sh miguel-xps-1 -e VILLAGE_NR_MIN=2 -e VILLAGE_NR_MAX=4 -e INIT_RESOURCE_MIN=75 -e INIT_RESOURCE_MAX=75 -e PROD_RATE_MIN=8 -e PROD_RATE_MAX=8
# AIAD miguel-xps 2:
# GENERATED_CSV=rapidminer_miguel-xps-2.csv ./docker.sh miguel-xps-2 -e VILLAGE_NR_MIN=2 -e VILLAGE_NR_MAX=4 -e INIT_RESOURCE_MIN=75 -e INIT_RESOURCE_MAX=75 -e PROD_RATE_MIN=9 -e PROD_RATE_MAX=9

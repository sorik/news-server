#! /bin/bash

lein clean
lein test

./script/docker-build.sh

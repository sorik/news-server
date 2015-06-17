#! /bin/bash

echo "current build number is $BUILD_NUMBER, so the tag will be $BUILD_NUMBER."
echo "building the image..."
docker build sorik/news-server:$BUILD_NUMBER .

echo "pushing the image..."
docker push sorik/news-server:$BUILD_NUMBER

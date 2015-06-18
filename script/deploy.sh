#! /bin/bash

DEFINITION_FILE_NAME="aws-ecs-task-revision.json"
# remove the file if exists
rm $DEFINITION_FILE_NAME
# create the file
touch $DEFINITION_FILE_NAME

NEWS_SERVER_TAG=$BUILD_NUMBER
TASK_NAME="mongo-news-server"

MONGO_PORT=27017
MONGO_CONTAINER_NAME="news-mongo"
MONGO_IMAGE="mongo"

NEWS_SERVER_PORT=8005
NEWS_SERVER_CONTAINER_NAME="news-server"
NEWS_SERVER_IMAGE="sorik/news-server:$NEWS_SERVER_TAG"

TASK_DEFINITION="{
  \"containerDefinitions\": [
    {
      \"volumesFrom\": [],
      \"portMappings\": [
        {
          \"hostPort\": $MONGO_PORT,
          \"containerPort\": $MONGO_PORT
        }
      ],
      \"command\": [],
      \"environment\": [],
      \"essential\": false,
      \"entryPoint\": [],
      \"links\": [],
      \"mountPoints\": [],
      \"memory\": 256,
      \"name\": \"$MONGO_CONTAINER_NAME\",
      \"cpu\": 256,
      \"image\": \"$MONGO_IMAGE\"
    },
    {
      \"volumesFrom\": [],
      \"portMappings\": [
        {
          \"hostPort\": $NEWS_SERVER_PORT,
          \"containerPort\": $NEWS_SERVER_PORT
        }
      ],
      \"command\": [],
      \"environment\": [],
      \"essential\": true,
      \"entryPoint\": [],
      \"links\": [
        \"news-mongo:db\"
      ],
      \"mountPoints\": [],
      \"memory\": 256,
      \"name\": \"news-server\",
      \"cpu\": 256,
      \"image\": \"$NEWS_SERVER_IMAGE\"
    }
  ],
  \"volumes\": [],
  \"family\": \"$TASK_NAME\"
}"

# write json definition to the file
echo ">> writing task definition json file"
echo $TASK_DEFINITION > $DEFINITION_FILE_NAME

echo ">> registering new revision for task $TASK_NAME"
aws ecs register-task-definition --cli-input-json file://./$DEFINITION_FILE_NAME

CLUSTER="words-app"
if [ $? == 0 ]; then
  #new revision name
  NEW_REVISION_TASK=$(echo $(aws ecs list-task-definitions | grep mongo-news-server | tail -n1) | sed 's/,//g' | sed 's/"//g')
  echo ">> successfully created $NEW_REVISION_TASK"

  #stop running task
  echo ">> stopping the running tasks"
  RUNNING_TASK=$(echo $(aws ecs list-tasks --cluster $CLUSTER | grep arn:aws) | sed 's/"//g' | cut -d'/' -f 2)
  aws ecs stop-task --cluster $CLUSTER --task $RUNNING_TASK

  #running the new revision
  echo ">> running the task with new revision"
  aws ecs run-task --cluster $CLUSTER --task-definition $NEW_REVISION_TASK
else
  echo ">> failed to create a new revision for $TASK_NAME"
fi







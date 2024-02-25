#!/bin/sh

echo "docker-compose service has failed."

if [ -f /usr/bin/podman ]; then
   # detect graphroot from /etc/containers/storage.conf
   GRAPH_ROOT=`sed -n "s/graphroot.*=.*\"\(.*\)\"/\1/p" /etc/containers/storage.conf`
elif [ -f /usr/bin/docker ]; then
   GRAPH_ROOT="/var/lib/docker"
else
   echo "No container engine is installed on this filesystem."
   exit 1
fi

if [ ! -d "$GRAPH_ROOT" ]; then
   echo "Invalid graph root: $GRAPH_ROOT."
   exit 1
fi

if [ ! -f "/etc/docker/enable-integrity-checker" ]; then
   echo "Docker integrity checker is disabled. Create /etc/docker/enable-integrity-checker file to enable it."
   echo "Restarting docker-compose in 10 seconds..."
   sleep 10
   systemctl restart docker-compose
   exit 1
fi

docker-compose config >/dev/null
if [ $? != 0 ]; then
   echo "Invalid docker-compose.yml file."
   exit 1
fi

ping -c 1 -q google.com >/dev/null

if [ $? != 0 ]; then
   echo "Please check your internet connection."
   echo "Restarting docker-compose in 10 seconds..."
   sleep 10
   systemctl restart docker-compose
   exit 1
fi

if [ ! -e /tmp/recovery-attempt.txt ]; then
   echo "1" > /tmp/recovery-attempt.txt
   rm -f /tmp/validated-images.txt
fi

RECOVERY_ATTEMPT=$(cat /tmp/recovery-attempt.txt)

echo "Recovery attempt: $RECOVERY_ATTEMPT"

if [ "$RECOVERY_ATTEMPT" -le 10 ]; then

   IMAGE_SHA=$(docker-compose config | grep image | sed 's/^[^:]*://g')

   for IMAGE in $IMAGE_SHA
      do
         echo "Checking the integrity of the Docker images..."
         if grep -qs $IMAGE "/tmp/validated-images.txt"; then
            echo "Docker image $IMAGE is OK."
         else
            docker save "$IMAGE" > /dev/null
            if [ $? != 0 ]; then
               echo "Image $IMAGE seems to be corrupted or it could not be pulled."
               DIGEST=$(echo $IMAGE | sed 's/^[^@]*@//g')
               IMAGE_ID=$(echo $(docker images --format "{{.ID}}: {{.Digest}}" | grep $DIGEST | sed 's/:.*//'))
               docker rmi -f $IMAGE_ID
               if [ $? = 1 ]; then
                  docker rmi -f $IMAGE
               fi
               echo "Image $IMAGE was removed and will be re-pulled again."

               PULL_ATTEMPT=0
               PULL_LIMIT=3
               while [ $PULL_ATTEMPT -lt $PULL_LIMIT ]
               do
                  docker pull "$IMAGE"
                  if [ $? = 0 ]; then
                     echo "Corrupted image $IMAGE has been re-pulled successfully."
                     break
                  fi
                  PULL_ATTEMPT=$(expr $PULL_ATTEMPT + 1)
                  if [ $PULL_ATTEMPT = $PULL_LIMIT ]; then
                     echo "Error pulling image $IMAGE. Please check your internet connection."
                     break
                  fi
                  echo "Error pulling image $IMAGE. Attempt $PULL_ATTEMPT of $PULL_LIMIT."
               done
            else
               echo "Docker image $IMAGE is OK."
               echo "$IMAGE" >>/tmp/validated-images.txt
            fi
         fi
      done
   echo " Restarting docker-compose.service."
   systemctl restart docker-compose

elif [ "$RECOVERY_ATTEMPT" -eq 11 ]; then
   echo "Starting docker system prune."
   docker system prune -f
   echo "Restarting docker-compose.service."
   systemctl restart docker-compose
elif [ "$RECOVERY_ATTEMPT" -eq 12 ]; then
   echo "Starting docker system prune --all."
   docker system prune --all -f
   echo "Restarting docker-compose.service."
   systemctl restart docker-compose
elif [ "$RECOVERY_ATTEMPT" -eq 13 ]; then
   systemctl stop docker-compose
   echo "Restarting docker.service."
   systemctl stop docker
   systemctl start docker
   echo "Restarting docker-compose.service."
   systemctl restart docker-compose
elif [ "$RECOVERY_ATTEMPT" -eq 14 ]; then
   systemctl stop docker-compose
   echo "Backup Docker volumes to $GRAPH_ROOT/backup/."
   mkdir -p $GRAPH_ROOT/backup/volumes/ && cp -r $GRAPH_ROOT/volumes/. $GRAPH_ROOT/backup/volumes/
   echo "Starting docker system prune --all --volumes."
   docker system prune --all --volumes -f
   systemctl stop docker
   echo "Restoring Docker volumes."
   cp -r $GRAPH_ROOT/backup/volumes/. $GRAPH_ROOT/volumes/ && rm -r $GRAPH_ROOT/backup/volumes/
   echo "Restarting docker.service."
   systemctl start docker
   echo "Restarting docker-compose.service."
   systemctl restart docker-compose
elif [ "$RECOVERY_ATTEMPT" -gt 14 ]; then
   echo "Docker seems to be broken beyond repair. To manually recover your system, stop the Docker engine (systemctl stop docker), delete the entire $GRAPH_ROOT directory and restart Docker engine (systemctl start docker). WARNING: That can result in losing important data."
   exit 0
fi

echo $(expr $RECOVERY_ATTEMPT + 1) > /tmp/recovery-attempt.txt

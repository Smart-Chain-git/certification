#!/usr/bin/env bash

teztool() {
  docker run -it --name teztool -v $PWD:/mnt/pwd \
    -e MODE=dind -e DIND_PWD=$PWD \
    -v /var/run/docker.sock:/var/run/docker.sock \
    registry.gitlab.com/nomadic-labs/teztool:latest "$@"
}

start() {
  # Create node, baker, and endorser containers
  teztool carthagenet sandbox --baker bootstrap2 \
    --time-between-blocks 10 start 18731
  # Destroy teztool container once all containers are initialized
  docker rm teztool
}

stop() {
  # Delete all sandbox containers
  docker rm -f \
    teztool_sandbox_carthagenet_endorser_006-PsCARTHA \
    teztool_sandbox_carthagenet_baker_006-PsCARTHA \
    teztool_sandbox_carthagenet
}

reset() {
  stop
  # Delete all volumes
  docker volume rm \
    teztool_sandbox_carthagenet_client \
    teztool_sandbox_carthagenet_data
}

case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  reset)
    reset
    ;;
  *)
    exit 1
esac

exit 0

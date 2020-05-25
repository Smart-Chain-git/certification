#!/usr/bin/env bash

teztool() {
  docker run -it --name teztool -v $PWD:/mnt/pwd \
    -e DIND_PWD=$PWD -v /var/run/docker.sock:/var/run/docker.sock \
    registry.gitlab.com/nomadic-labs/teztool:latest "$@"
}

teztoolUpdate() {
  docker pull registry.gitlab.com/nomadic-labs/teztool:latest
}

start() {
  # Update teztool image
  # teztoolUpdate
  # Create node, baker, and endorser containers
  teztool create sandbox --network sandbox --rpc-port 18731  \
    --sandbox-time-between-blocks 10 --baker-identity bootstrap2 --historyMode archive
  # Destroy teztool container once all containers are initialized
  docker rm teztool
}

stop() {
  # Delete all sandbox containers
  docker rm -f \
    teztool_sandbox_endorser_006-PsCARTHA \
    teztool_sandbox_baker_006-PsCARTHA \
    teztool_sandbox
}

reset() {
  stop
  # Delete all volumes
  docker volume rm \
    teztool_sandbox_client \
    teztool_sandbox_data
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

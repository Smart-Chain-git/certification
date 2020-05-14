#!/usr/bin/env sh

alias teztool='docker run -it -v $PWD:/mnt/pwd -e MODE=dind \
    -e DIND_PWD=$PWD \
    -v /var/run/docker.sock:/var/run/docker.sock \
    registry.gitlab.com/nomadic-labs/teztool:latest'

teztool carthagenet sandbox --baker bootstrap2 \
--time-between-blocks 10 start 18731

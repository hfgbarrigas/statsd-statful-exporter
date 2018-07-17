Statsd metric exporter
======================

Reactor Netty based application that listens to metrics over tcp and udp and sends transformed metrics via UDP.
Main goal is to map satsd metric format into statful format. The implementation allows customization of senders as well
as metric receivers.

Inspired on: https://github.com/prometheus/statsd_exporter

    +----------+                         +-------------------+                        +--------------+
    |  StatsD  |---(UDP/TCP repeater)--->|  statsd_exporter  |--(UDP relay metrics)-->|  Relay       |
    +----------+                         +-------------------+                        +--------------+

Configuration
=============
An example configuration file

    tcp:
      port: 8080
    udp:
      port: 8125
      host: 127.0.0.1
    mappings:
    - match: test.timing.*.*.*
      name: "my_timer"
      action: "drop/match"
      tags:
        provider: "$2"
        outcome: "$3"
        "$1_job": "$1_server"
    statful:
      flushInterval: 5000
      flushSize: 10
      dryRun: false
      host: localhost
      port: 2013
      app: statsdexporter
      namespace: statsdexporter
      environment: local


Build
=====
    mvn clean install

Run
=====
    java -jar target/statsd-statful-exporter-*-jar-with-dependencies.jar /optional/absolute/path/to/config
    
If no path to external config is provided, by default the app looks for /tmp/config.json
    
Docker
======

    docker build -t statsdexporter .
    docker run -p 8125:8125/udp \ 
    -p 8080:8080> \ 
    -e CONFIG_PATH=/tmp/config.yaml \
    -e ENVIRONMENT=MY_ENV \
    -v $PWD/.config/config.yaml:/tmp/config.yaml \
    statsdexporter:latest

TODO
====
- Tests
- Improve documentation

Maintainer
==========
hugo.barrigas@mindera.com

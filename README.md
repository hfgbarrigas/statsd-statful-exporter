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
      - match: envoy.cluster.*.upstream_rq_*
        name: "http.requests.upstream"
        tags:
        upstream: "$1"
        status_code: "$2"
      - match: envoy.cluster.*.upstream_rq_time
        name: "http.requests.upstream"
        tags:
          upstream: "$1"
      - match: envoy.cluster.*.external.upstream_rq_*
        name: "http.external.requests.upstream"
        tags:
          upstream: "$1"
          status_code: "$2"
      - match: envoy.cluster.*.external.upstream_rq_time
        name: "http.external.requests.upstream"
        tags:
          upstream: "$1"
      - match: envoy.cluster.*.internal.upstream_rq_*
        name: "http.internal.requests.upstream"
        tags:
          upstream: "$1"
          status_code: "$2"
      - match: envoy.cluster.*.internal.upstream_rq_time
        name: "http.internal.requests.upstream"
        tags:
          upstream: "$1"
      - match: envoy.cluster.*.canary.upstream_rq_*
        name: "http.canary.requests.upstream"
        tags:
          upstream: "$1"
          status_code: "$2"
      - match: envoy.cluster.*.canary.upstream_rq_time
        name: "http.canary.requests.upstream"
        tags:
          upstream: "$1"
      - match: envoy.cluster.*.upstream_rq_active
        name: "active_requests"
        tags:
          upstream: "$1"
      - match: envoy.cluster.*.upstream_rq_pending_active
        name: "pending_active_requests"
        tags:
          upstream: "$1"
      - match: envoy.cluster.*.upstream_rq_pending_total
        name: "pending_total_requests"
        tags:
          upstream: "$1"
      - match: envoy.cluster.*.upstream_cx_active
        name: "active_connections"
        tags:
          upstream: "$1"
      - match: envoy.cluster.*.membership_healthy
        name: "membership_healthy"
        tags:
          upstream: "$1"
      - match: envoy.cluster.*.membership_total
        name: "membership_total"
        tags:
          upstream: "$1"
      - match: envoy.cluster.*.upstream_rq_timeout
        name: "requests_timeout"
        tags:
          upstream: "$1"
      - match: envoy.cluster.*.upstream_rq_total
        name: "requests_total"
        tags:
          upstream: "$1"
      - match: envoy.cluster.*.upstream_cx_http1_total
        name: "total_http1_requests"
        tags:
          upstream: "$1"
      - match: envoy.cluster.*.upstream_cx_http2_total
        name: "total_http2_requests"
        tags:
          upstream: "$1"
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

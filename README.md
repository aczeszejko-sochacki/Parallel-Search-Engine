# Parallel and distributed phrase search

## Run
```sbt "project search-server; run"```

will start a server with the default configuration from *application.conf*

```sbt "project search-server-grpc; run"```

will start a single gRPC server (see *application.conf*). Number and configuration of gRPC servers should match search server configuration for gRPC. 

## Search
* GET http://<host:port>/parSearch?phrase=phrase&parCompareEnabled=

to search multiple files from content path (default `search-server/src/main/resources/files`) in parallel
with parallel stream search. Optional boolean parameter parCompareEnabled turns on performing parallel comparison on phrase chunks (see *application.conf* defaults)

* GET http://<host:port>/search?phrase=phrase

to search files and their content fully sequentially

* GET http:/<host:port>/searchGrpc?phrase=phrase

to search files using gRPC. Each gRPC performs searching in the specified content path (see the defaults in application.conf)
Responses from different gRPC servers are merged and the result format is like below, regardless the endpoint.

## Result

Result format is like the following (json):
```
[
  {"file": <path-to-a-file>, "offset": <offset>}
]
```
where "offset" indicates the occurrence of the phrase in the file

## Notes

Tested on java 8.0.312-zulu because of gRPC. 

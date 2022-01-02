# Parallel phrase search

## Run
```sbt "project search-server; run"```

will start a server with the default configuration from application.conf 

## Search
* GET http://<host:port>/parSearch?phrase=<phrase>

to search multiple files from content path (default `search-server/src/main/resources/files`) in parallel
with parallel stream search

* GET http://<host:port>/search?phrase=<phrase>

to search files and their content fully sequentially

## Result

Result format is like the following (json):
```
[
  {"file": <path-to-a-file>, "offset": <offset>}
]
```
where "offset" indicates the occurrence of the phrase in the file

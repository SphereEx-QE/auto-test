# Auto Test
This is Automated testing framework for IT.

## How to use
### Requirements
* prepare a available proxy or DB instance
### How to run
```
// build from source code
mvn package
// unpack package built by source code(auto-test-1.0-SNAPSHOT-full.zip)
unzip unzip auto-test-1.0-SNAPSHOT-full.zip

cd auto-test-1.0-SNAPSHOT

./bin/auto-test.sh -h 127.0.0.1 -P 3309 -d sharding_db -u root -p root

//Help

./bin/auto-test.sh

//output

usage: auto-test.sh [-h ip] [-P port] [-d dbname] [-u user] [-p password] [-f feature] [-t tag] [-c casename]
  -h|--host  ip: shardingsphere proxy ip
  -P|--port: shardingsphere proxy port
  -d|--dbname: shardingsphere proxy dbname
  -u|--user: shardingsphere proxy user
  -p|--password: shardingsphere proxy password
  -f|--feature: run the cases of the the feature
  -t|--tag: run the cases of the tag
  -c|--casenames: run this cases 
```

### How to add case

* create a class extend `BaseCaseImpl` in package `come.sphereex.cases`
* add annotation `AutoTest` to class, please refer to `src/main/java/com/sphereex/cases/transaction/case1/Case1.java`

### Realization principle

Scan all classes decorated with `AutoTest` under the `come.sphereex.cases` package, then execute `start()`
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

./bin/auto-test.sh 127.0.0.1 3309 sharding_db root root

//Help

./bin/auto-test.sh

//output

usage: auto-test.sh [ip] [port] [dbname] [user] [password] [casename]
  ip: shardingsphere proxy ip, not null
  port: shardingsphere proxy port, not null
  dbname: shardingsphere proxy dbname, not null
  user: shardingsphere proxy user, not null
  password: shardingsphere proxy password, not null
  casename: separate case names with commas
```

### How to add case

* create a class extend `BaseCaseImpl` in package `come.sphereex.cases`
* add annotation `AutoTest` to class, please refer to `src/main/java/com/sphereex/cases/transaction/case1/Case1.java`

### Realization principle

Scan all classes decorated with `AutoTest` under the `come.sphereex.cases` package, then execute `start()`
# Basic WordCount example to test mapreduce

## Build Mapred Examples Project

```bash
mvn clean package
```

## Mount Hadoop NFS Gateway to upload example data

```bash
sudo mount -t nfs -o rw nfsgateway.gudari.io:/ ~/hdfs
```

## Upload test data in hdfs

```bash
mkdir -p ~/hdfs/mapred

```

## Execute WordCount job

```bash
hadoop jar mapredExamples-0.0.1-SNAPSHOT.jar org.gudari.mapred.WordCount.WordCount  /examples/mapred/SalesCountry/input /examples/mapred/SalesCountry/output
```

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
cp -R data ~/hdfs/mapred
mkdir -p ~/hdfs/mapred/result
```

## Execute WordCount job

```bash
hadoop jar mapredExamples-0.0.1-SNAPSHOT.jar org.gudari.mapred.WordCount  /mapred/data/wordCount /mapred/result/wordCount
```

## Execute WordCount2 job

```bash
hadoop jar mapredExamples-0.0.1-SNAPSHOT.jar org.gudari.mapred.WordCount2  /mapred/data/wordCount /mapred/result/wordCount2
```

## Execute WordCount2 job

```bash
hadoop jar mapredExamples-0.0.1-SNAPSHOT.jar org.gudari.mapred.WordCount2  /mapred/data/wordCount /mapred/result/wordCount2
```

## Execute MyMaxMin job

```bash
hadoop jar mapredExamples-0.0.1-SNAPSHOT.jar org.gudari.mapred.MyMaxMin  /mapred/data/myMaxMin /mapred/result/myMaxMin
```

## Execute SalesCountry job

```bash
hadoop jar mapredExamples-0.0.1-SNAPSHOT.jar org.gudari.mapred.SalesCountry  /mapred/data/salesCountry /mapred/result/salesCountry
```

## Execute HashTagCount job

```bash
hadoop jar mapredExamples-0.0.1-SNAPSHOT.jar org.gudari.mapred.HashTagCount  /mapred/data/tweets /mapred/result/tweetsHashTagCount
```

## Execute TopTenHashTag job

```bash
hadoop jar mapredExamples-0.0.1-SNAPSHOT.jar org.gudari.mapred.TopTenHashTag  /mapred/result/tweetsHashTagCount /mapred/result/tweetsTopTenHashTag
```

## Execute HashTagSentiment job

```bash
hadoop jar mapredExamples-0.0.1-SNAPSHOT.jar org.gudari.mapred.HashTagSentiment  /mapred/data/tweets /mapred/result/tweetsHashTagSentiment /mapred/data/opinionLexiconEnglish/positive-words.txt /mapred/data/opinionLexiconEnglish/negative-words.txt
```

## Execute HashTagSentimentChain job

```bash
hadoop jar mapredExamples-0.0.1-SNAPSHOT.jar org.gudari.mapred.HashTagSentimentChain  /mapred/data/tweets /mapred/result/tweetsHashTagSentimentChain /mapred/data/opinionLexiconEnglish/positive-words.txt /mapred/data/opinionLexiconEnglish/negative-words.txt
```

package org.gudari.mapred;

import java.io.* ;
import java.util.Set;
import java.util.HashSet;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class HashTagSentiment extends Configured implements Tool {

    public static class HashTagSentimentMapper extends Mapper<Object, Text, Text, Text> {

        private static final String BEGIN_COMMENT = ";";
        private static final String HASHTAG_PATTERN = "(?:\\s|\\A|^)[##]+([A-Za-z0-9-_]+)";

        private Set<String> positiveWords =  null;
        private Set<String> negativeWords = null;
        private Set<String> hashtags = new HashSet<>();

        private Text word = new Text();

        private HashSet<String> parseWordsList(FileSystem fs, Path wordListPath) {
            HashSet<String> words = new HashSet<>();
            try {
                if(fs.exists(wordListPath)){
                    FSDataInputStream fi = fs.open(wordListPath);

                    BufferedReader br = new BufferedReader(new InputStreamReader(fi));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        if(line.length() > 0 && !line.startsWith(BEGIN_COMMENT)){
                            words.add(line);
                        }
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }

            return words;
        }

        @Override
        public void setup(Context context) {
            Configuration conf = context.getConfiguration();

            try {
                FileSystem fs = FileSystem.get(conf);

                String positiveWordsLocation = conf.get("job.positivewords.path");
                String negativeWordsLocation = conf.get("job.negativewords.path");

                positiveWords = parseWordsList(fs, new Path(positiveWordsLocation));
                negativeWords = parseWordsList(fs, new Path(negativeWordsLocation));
            } catch ( IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void map(Object key, Text value, Context context ) throws IOException, InterruptedException {
            String[] words = value.toString().split(" ");
            Integer positiveCount = 0;
            Integer negativeCount = 0;

            Integer wordsCount = 0;

            for(String str: words){
                if (str.matches(HASHTAG_PATTERN)){
                    hashtags.add(str);
                }

                if (positiveWords.contains(str)){
                    positiveCount += 1;
                } else if (negativeWords.contains(str)){
                    negativeCount += 1;
                }
                wordsCount += 1;
            }

            Integer sentimentDifference = 0;
            if(wordsCount > 0 ) {
                sentimentDifference = positiveCount - negativeCount;
            }

            String stats;
            for(String hashtag: hashtags){
                word.set(hashtag);
                stats = String.format("%d %d", sentimentDifference, wordsCount);
                context.write(word, new Text(stats));
            }
        }
    }

    public class HashTagSentimentReducer extends Reducer<Text, Text, Text, DoubleWritable> {

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            double totalDifference = 0;
            double totalWords = 0;
            for(Text val: values) {
                String[] parts = val.toString().split(" ");
                totalDifference += Double.parseDouble(parts[0]);
                totalWords += Double.parseDouble(parts[1]);
            }
            context.write(key, new DoubleWritable(totalDifference/totalWords));
        }
    }

    public int run(String[] args) throws Exception {

        Configuration conf = getConf();

        args = new GenericOptionsParser(conf, args).getRemainingArgs();

        conf.set("job.positivewords.path", args[2]);
        conf.set("job.negativewords.path", args[3]);

        Job job = Job.getInstance(conf, "HashTagSentiment");

        job.setJarByClass(HashTagSentiment.class);
        job.setMapperClass(HashTagSentimentMapper.class);
        job.setReducerClass(HashTagSentimentReducer.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        return (job.waitForCompletion(true) ? 0 : 1);
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new HashTagSentiment(), args);
        System.exit(exitCode);
    }
}

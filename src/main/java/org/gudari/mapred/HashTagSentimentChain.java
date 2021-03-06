package org.gudari.mapred;

import java.io.IOException;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.fs.Path;

public class HashTagSentimentChain extends Configured implements Tool{

    public static class LowerCaseMapper extends Mapper<LongWritable, Text, IntWritable, Text>{
        private Text lowerCased = new Text();

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            lowerCased.set(value.toString().toLowerCase());
            context.write(new IntWritable(1), lowerCased);
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        args = new GenericOptionsParser(conf, args).getRemainingArgs();

        // location (on hdfs) of the positive words list
        conf.set("job.positivewords.path", args[2]);
        conf.set("job.negativewords.path", args[3]);

        Job job = Job.getInstance(conf, "HashTagSentimentChain");
        job.setJarByClass(HashTagSentimentChain.class);

        Configuration lowerCaseMapperConf = new Configuration(false);
        ChainMapper.addMapper(job, LowerCaseMapper.class, LongWritable.class, Text.class, IntWritable.class, Text.class, lowerCaseMapperConf);

        Configuration hashTagSentimentConf = new Configuration(false);
        ChainMapper.addMapper(job, HashTagSentiment.HashTagSentimentMapper.class, IntWritable.class, Text.class, Text.class, Text.class, hashTagSentimentConf);

        job.setReducerClass(HashTagSentiment.HashTagSentimentReducer.class);

        job.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));

        job.setOutputFormatClass(TextOutputFormat.class);
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        Integer exitCode = ToolRunner.run(new HashTagSentimentChain(), args);
        System.exit(exitCode);
    }
    
}

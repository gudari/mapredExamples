package org.gudari.mapred;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.conf.Configuration;

public class MyMaxMin {

    //Mapper

    public static class MaxTemperatureMapper extends Mapper<LongWritable, Text, Text, Text> {

        /**
        * @method map
        * This method takes the input as text data type.
        * Now leaving the first five tokens, it takes 6th token as tempMax and
        * 7th token is taken as tempMin. Now tempMax > 35 and tempMin < 10 are passed to the reducer.
         */
        public static final int MISSING = 9999;

        @Override
        public void map(LongWritable arg0, Text value, Context context)
            throws IOException, InterruptedException {
                String line = value.toString();

                if(line.length() > 0) {
                    String date = line.substring(6, 14);
                    float tempMax = Float.parseFloat(line.substring(39, 45).trim());
                    float tempMin = Float.parseFloat(line.substring(47, 53).trim());

                    if (tempMax > 35.0 && tempMax != MISSING) {
                        context.write(new Text("Hot Day " + date), new Text(String.valueOf(tempMax)));
                    }

                    if (tempMin < 10.0 && tempMin != MISSING) {
                        context.write(new Text("Cold Day " + date), new Text(String.valueOf(tempMin)));
                    }
                }

            }

    }
    
    public static class MaxTemperatureReducer extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterator<Text> values, Context context) throws IOException, InterruptedException {
            String temperature = values.next().toString();
            context.write(key, new Text(temperature));
        }

    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "weather example");

        job.setJarByClass(MyMaxMin.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setMapperClass(MaxTemperatureMapper.class);
        job.setReducerClass(MaxTemperatureReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        Path outputPath = new Path(args[1]);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        outputPath.getFileSystem(conf).delete(outputPath, true);
        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }
}

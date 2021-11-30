package org.gudari.mapred;

import org.gudari.mapred.salescountry.SalesCountryReducer;
import org.gudari.mapred.salescountry.SalesMapper;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;


public class SalesCountry {
    public static void main(String[] args) throws IOException {
        JobClient myClient = new JobClient();
        // Create a configuration object for the job
        JobConf jobConf = new JobConf(SalesCountry.class);

        // Set a name of the Job
        jobConf.setJobName("SalePerCountry");

        // Specify data type of output key and value
        jobConf.setOutputKeyClass(Text.class);
        jobConf.setOutputValueClass(IntWritable.class);

        // Specify names of Mapper and Reducer Class
        jobConf.setMapperClass(SalesMapper.class);
        jobConf.setReducerClass(SalesCountryReducer.class);

        // Specify formats of the data type of Input and output
        jobConf.setInputFormat(TextInputFormat.class);
        jobConf.setOutputFormat(TextOutputFormat.class);

        // Set input and output directories using command line arguments, 
        //arg[0] = name of input directory on HDFS, and arg[1] =  name of output directory to be created to store the output file.

        FileInputFormat.setInputPaths(jobConf, new Path(args[0]));
        FileOutputFormat.setOutputPath(jobConf, new Path(args[1]));

        myClient.setConf(jobConf);
        try {
            // Run the job 
            JobClient.runJob(jobConf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            myClient.close();
        }
    }
}

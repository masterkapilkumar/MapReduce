import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class CourseAvgGrade {

	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {

		private Text course_code = new Text();
		private IntWritable score = new IntWritable();

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens()) {
				itr.nextToken();
				course_code.set(itr.nextToken());
				score.set(Integer.parseInt(itr.nextToken()));
				context.write(course_code, score);
			}
		}
	}

	public static class AverageReducer extends Reducer<Text,IntWritable,Text,FloatWritable> {
		private FloatWritable result = new FloatWritable();

		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			int total_score = 0;
			int num_of_students = 0;
			for (IntWritable val : values) {
				num_of_students += 1;
				total_score += val.get();
			}
			result.set((float)total_score/num_of_students);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "course average grades");
		job.setJarByClass(CourseAvgGrade.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setReducerClass(AverageReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FloatWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
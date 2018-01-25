import java.rmi.*;
import java.util.*;
import java.math.*;
import java.text.ParseException;
import java.util.Random;
import java.lang.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.text.ParseException;
import java.io.PrintStream;
import java.io.FileOutputStream;

class Client
{
	static long data;
	static long time_taken;
	private static Random r = new Random();
	public static String key;
	public static JSONObject serializeJSON(String line)
	{
		String[] parts=line.split(":");
		JSONObject student = new JSONObject();
		//System.out.println(parts.length);
		String[] sub=parts[0].split(",");
		student.put("Name",sub[0]);
		student.put("RollNo",Long.valueOf(sub[1]));
		int j;
		JSONArray subarr = new JSONArray();

		for(j=1;j<parts.length;j++)
		{
			String[] sub2=parts[j].split(",");
			JSONObject marks=new JSONObject();
			marks.put("CourseScore",Long.valueOf(sub2[1]));
			marks.put("CourseName",sub2[0]);
			subarr.add(marks);

		}
		student.put("CourseMarks",subarr);
		return student;


	}

	public static void main(String args[])throws ParseException{
		try{ 
			Interface access=(Interface)Naming.lookup("rmi://localhost:1900"+"/RMI");
			List<String> lines = Files.readAllLines(Paths.get("input_sample"), StandardCharsets.UTF_8);
			int i;
			time_taken=0;
			JSONArray arr = new JSONArray();
			for(i=0;i<lines.size();i++)
			{
				long start = System.currentTimeMillis();
				arr.add(serializeJSON(lines.get(i)));
				data+=lines.get(i).length();
				time_taken+= System.currentTimeMillis()-start;
			}
			//      System.out.println(arr);
			System.out.println("Time taken for Serialization to JSON: " + time_taken + " ms");
			System.out.println("Rate of Serilization to JSON: " + ((double) data) / (time_taken) + " KBps");
			byte[] bArr = arr.toJSONString().getBytes();
			access.Receive(bArr);	
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}

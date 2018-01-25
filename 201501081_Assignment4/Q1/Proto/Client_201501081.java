import java.rmi.*;
import java.util.*;
import java.math.*;
import java.text.ParseException;
import java.util.Random;
import java.lang.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.text.ParseException;
import java.io.PrintStream;
import java.io.FileOutputStream;
import proto.ResultProto.CourseMarks;
import proto.ResultProto.Student;
import proto.ResultProto.Result;
import java.io.ByteArrayOutputStream;
import java.nio.*;
class Client
{
	static long data;
	static long time_taken;
	private static Random r = new Random();
	public static String key;
	public static Student serializePROTO(String line)
	{
		String[] parts=line.split(":");
		String[] sub=parts[0].split(",");
		Student.Builder student = Student.newBuilder();
		student.setName(sub[0]);
		student.setRollNum(Integer.parseInt(sub[1]));
		int j;
		
		
		for(j=1;j<parts.length;j++)
		{
			String[] sub2=parts[j].split(",");
			CourseMarks.Builder marks = CourseMarks.newBuilder();
			marks.setScore(Integer.parseInt(sub2[1]));
			marks.setName(sub2[0]);
			student.addMarks(marks);

		}
		return student.build();


	}

	public static void main(String args[])throws ParseException{
		try{ 
			Interface access=(Interface)Naming.lookup("rmi://localhost:1900"+"/RMI");
			List<String> lines = Files.readAllLines(Paths.get("input_sample"), StandardCharsets.UTF_8);
			int i;
			time_taken=0;
			Result.Builder arr = Result.newBuilder();
			for(i=0;i<lines.size();i++)
			{
				long start = System.currentTimeMillis();
				arr.addStudent(serializePROTO(lines.get(i)));
				data+=lines.get(i).length();
				time_taken+= System.currentTimeMillis()-start;
			}
			//      System.out.println(arr);
			System.out.println("Time taken for Serialization to Proto: " + time_taken + " ms");
			System.out.println("Rate of Serilization to Proto: " + ((double) data) / (time_taken) + " KBps");
			byte bArr[] = (arr.build()).toByteArray();
			access.Receive(bArr);	
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}

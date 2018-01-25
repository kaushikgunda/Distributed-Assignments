import java.math.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.util.Random.*;
import java.lang.*;
import java.io.*;
import java.nio.ByteBuffer;
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

class Server extends UnicastRemoteObject implements Interface
{ public Server()throws RemoteException {
	super();
					}
static long time_taken=0;
static long data=0;
public static String key;
public void Receive(byte[] inputbyte)
{
	try{
		time_taken=0;
		long startTime = System.currentTimeMillis();
		String instring=new String(inputbyte);
		Result array =Result.parseFrom( inputbyte);
		PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
		System.setOut(out);
		int i;
		//time_taken+=System.currentTimeMillis()-startTime;
		data=array.toString().length();
		for(Student obj:array.getStudentList())
		{
			long startTime2 = System.currentTimeMillis();
			//			JSONObject obj=(JSONObject)array.get(i);
			System.out.print(obj.getName()+",");
			System.out.print(obj.getRollNum()+":");
			int j;
			j=0;
			//CourseMarks arr1=(CourseMarks)obj.getCourseMarks;
			for(CourseMarks ob2:obj.getMarksList())
			{
		//		time_taken+=System.currentTimeMillis()-startTime2;
				if(j>0)
					System.out.print(":"+ob2.getName()+","+ob2.getScore());
				else
					System.out.print(ob2.getName()+","+ob2.getScore());

				j++;
			}
			 time_taken+=System.currentTimeMillis()-startTime2;

			System.out.println();
		}
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));   
		System.out.println("Time taken for Deserialization Proto: " + time_taken + " ms");
		System.out.println("Rate of Deserilization Proto: " + ((double)data) / (time_taken) + " KBps"); 

	}
	catch(Exception e){};


}
public static void main(String args[])throws Exception{
	Server server=new Server();
	Registry registry=LocateRegistry.createRegistry(1900);
	Naming.rebind("rmi://localhost:1900"+"/RMI", server);
	System.setProperty("java.security.policy","file:test.policy");
}
}


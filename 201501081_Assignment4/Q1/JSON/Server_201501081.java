import java.math.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.util.Random.*;
import java.lang.*;
import java.io.*;
import java.nio.ByteBuffer;
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

		JSONParser parser = new JSONParser();
		Object parseobj = parser.parse(instring);
		JSONArray array = (JSONArray)parseobj;
		PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
		System.setOut(out);
		int i;
		//time_taken+=System.currentTimeMillis()-startTime;
		data=array.toString().length();
		for(i=0;i<array.size();i++)
		{
			long startTime2 = System.currentTimeMillis();
			JSONObject studentobj=(JSONObject)array.get(i);
			System.out.print(studentobj.get("Name")+",");
			System.out.print(studentobj.get("RollNo")+":");
			int j;
			JSONArray arr1=(JSONArray)studentobj.get("CourseMarks");
			for(j=0;j<arr1.size();j++)
			{
				JSONObject marksob=(JSONObject)arr1.get(j);
			//	time_taken+=System.currentTimeMillis()-startTime2;
				if(j>0)
					System.out.print(":"+marksob.get("CourseName")+","+marksob.get("CourseScore"));
				else
					System.out.print(marksob.get("CourseName")+","+marksob.get("CourseScore"));

			}
			time_taken+=System.currentTimeMillis()-startTime2;
			System.out.println();
		}
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));   
		System.out.println("Time taken for Deserialization JSON: " + time_taken + " ms");
		System.out.println("Rate of Deserilization JSON: " + ((double)data) / (time_taken) + " KBps"); 

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


import java.rmi.*;
import java.util.*;
import java.math.*;
import java.text.ParseException;
import java.util.Random;
import java.lang.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Scanner;
class Client
{
	private static Random r = new Random();
	public static String key;
	public static int min(int a,int b)
	{
		if(a<b)
			return a;
		return b;
	}
	public static String trim(String x)
	{

		int len=x.length();
		int i=0;
		while(x.charAt(i)=='0')
			i++;
		String ans=x.substring(i);
		int temp=0;
		String req="";
		while(temp<7)
		{       
			int tempx=min(temp+ans.length(),8);
			req=req+ans.substring(0,tempx-temp);
			temp+=tempx;

		}
		return req;


	}
	public static byte[] Encode(String a,String key)throws UnsupportedEncodingException
	{
		/*	byte[] out = new byte[a.length()];
			for (int i = 0; i < a.length(); i++) {
			out[i] = (byte) (a.charAt(i)^key.charAt(i%key.length()));
			}
			return out;
		 */
		byte[] out = new byte[a.length()];
		//First convert the key into binary string and then trim it;
		//Now trim it;
		//This is the final string obtained trimmed and made to 8 bits length;
		//Now convert this string back to int and do
		int finalkey=Integer.parseInt(key,2);
		for (int i = 0; i < a.length(); i++) {
			int temp=(int)a.charAt(i);
			out[i] = (byte) ((int)(a.charAt(i))^finalkey);
		}
		return out;

	}
	public static String Decode(byte[] arr,String key)throws UnsupportedEncodingException
	{
		byte[] keys=key.getBytes();
		int i;
		int finalkey=Integer.parseInt(key,2);

		for(i=0;i<arr.length;i++)
			arr[i]=(byte)(arr[i]^finalkey);
		String str = new String(arr, "UTF-8");
		return str;
	}
	public static byte[] toBytes(int i)
	{
		byte[] result = new byte[4];

		result[0] = (byte) (i >> 24);
		result[1] = (byte) (i >> 16);
		result[2] = (byte) (i >> 8);
		result[3] = (byte) (i /*>> 0*/);

		return result;
	}

	private static int generateRandom(int min, int max) {
		return r.nextInt(max-min+1) + min;
	}
	public static void PrimalityTest(Interface access,String a)throws RemoteException,UnsupportedEncodingException	{
		System.out.println(Decode(access.PrimalityTest(Encode(a,key)),key));
	}
	public static void PalindromeTest(Interface access,String a)throws RemoteException,UnsupportedEncodingException	{
		System.out.println(Decode(access.PalindromeTest(Encode(a,key)),key));
	}	
	public static void StringCaseConverter(Interface access,String a)throws RemoteException,UnsupportedEncodingException{
		System.out.println(Decode(access.StringCaseConverter(Encode(a,key)),key));
	}
	public static void Fibnoacci(Interface access,int a)throws RemoteException,UnsupportedEncodingException{
		//byte[] result=toBytes(a);
		//  String str = new String(result);
		//System.out.println(a);
		System.out.println(Decode(access.Fibnoacci(Encode(Integer.toString(a),key)),key));
	}

	public static void main(String args[])throws ParseException{
		try{ 
			Interface access=(Interface)Naming.lookup("rmi://localhost:1900"+"/RMI");

			//It should have the value of both prime p,and generator g 
			int p=23;
			int g=5;
			int a=generateRandom(2,p-1);
			//Now Send the value of pow(g,a)mod(p) as A
			BigInteger generator=new BigInteger(Integer.toString(g));
			BigInteger exponent=new BigInteger(Integer.toString(a));
			BigInteger modulus=new BigInteger(Integer.toString(p));
			BigInteger A=generator.modPow(exponent,modulus);
			String sendA=A.toString();
			String B=access.DHKey(p,g,sendA);
			//Now calculate the key and print it
			BigInteger Bval=new BigInteger(B);
			BigInteger Key=Bval.modPow(exponent,modulus);

			//	int temp=Integer.parseInt(Key.toString());
			//	byte[] result=toBytes(temp);
			//              key = new String(result, "UTF-8");
			key=trim(Integer.toBinaryString(0x100 + Key.intValue()).substring(1));

			//Key is already in string so don't worry
			Scanner sc= new Scanner(System.in);
			String in;System.out.print(">>");
			while((in=sc.nextLine())!=null)
			{
				//System.out.println(in);
				String[] splited = in.split("\\s+");
				if(new String("Primality").equals(splited[0]) && splited.length==2)
					PrimalityTest(access,splited[1]);
				if(new String("Palindrome").equals(splited[0]) && splited.length==2)
					PalindromeTest(access,splited[1]);
				if(new String("StringCaseConverter").equals(splited[0]) && splited.length==2)
					StringCaseConverter(access,splited[1]);
				if(new String("Fibonacci").equals(splited[0]) && splited.length==2)
					Fibnoacci(access,Integer.parseInt(splited[1]));
				System.out.print(">>");
			}	

			PrimalityTest(access,"34");	
			PalindromeTest(access,"ababa");
			StringCaseConverter(access,"ABc");
			Fibnoacci(access,100);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}

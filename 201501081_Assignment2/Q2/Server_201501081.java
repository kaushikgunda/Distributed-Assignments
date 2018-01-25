import java.math.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.util.Random.*;
import java.lang.*;
import java.io.*;
import java.nio.ByteBuffer;
class Server extends UnicastRemoteObject implements Interface {
	public Server()throws RemoteException
	{
		super();
	}
	public static String key;
	//Now change encoding way
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
		/*      byte[] out = new byte[a.length()];
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
	public static Random r=new Random();
	private static int generateRandom(int min, int max) {
		return r.nextInt(max-min+1) + min;
	}
	public static Long fib[]= new Long[1000009];
	public String decode(String input)
	{
		return input;
	}
	public  byte[] PrimalityTest(byte[]arr)throws UnsupportedEncodingException{
		String input =Decode(arr,key);
		String decoded=decode(input);
		int a=Integer.parseInt(decoded);
		int i=2;
		for(i=2;i*i<=a;i++)
		{
			if(a%i==0)
				return Encode("False",key);

		}
		return Encode("True",key);

	}
	public byte[] PalindromeTest(byte[]arr)throws UnsupportedEncodingException{
		String decod=Decode(arr,key);
		int size=decod.length();
		int i;
		for(i=0;i<size;i++)
		{
			if(decod.charAt(i)!=decod.charAt(size-i-1))
				return Encode("False",key);
		}
		return Encode("True",key);
	}
	public Long Fibbi(int n)
	{
		if(n==0)
		{
			fib[n]=0L;return fib[n];}
		if(n==1)
		{
			fib[n]=1L;return fib[n];}
		if(fib[n]!=-1L)
			return fib[n];
		fib[n]=Fibbi(n-1)+Fibbi(n-2);
		return fib[n];

	}
	public byte[] Fibnoacci(byte[] input)throws UnsupportedEncodingException{
		String in=Decode(input,key);
		int n=Integer.parseInt(in);
		Long ans=Fibbi(n);
		return Encode(Long.toString(ans),key);
	}
	public byte[] StringCaseConverter(byte[] a)throws UnsupportedEncodingException{
		String input=Decode(a,key);
		int i;int size=input.length();
		for(i=0;i<size;i++)
		{
			if(input.charAt(i)>='a' && input.charAt(i)<='z')
			{
				char temp=(char)(input.charAt(i)-'a'+'A');
				input=input.substring(0,i)+temp+input.substring(i+1);

			}
			else
				if(input.charAt(i) >='A' && input.charAt(i)<='Z')
				{
					char temp=(char)(input.charAt(i)-'A'+'a');

					input=input.substring(0,i)+temp+input.substring(i+1);

				}


		}
		return Encode(input,key);


	}
	public String DHKey(int p,int g,String recv)
	{

		int range= p;
		int rand=generateRandom(2,p-1);
		int A=Integer.parseInt(recv);
		//Now calculate the value of b = pow(g,rand)%mod
		BigInteger generator=new BigInteger(Integer.toString(g));
		BigInteger random=new BigInteger(Integer.toString(rand));
		BigInteger modulus=new BigInteger(Integer.toString(p));
		BigInteger bval=generator.modPow(random,modulus);
		String b=bval.toString();
		BigInteger Aval=new BigInteger(recv);
		BigInteger Key=Aval.modPow(random,modulus);
		key=trim(Integer.toBinaryString(0x100 + Key.intValue()).substring(1));

		//System.out.println(Key);
		//Now here we have the value of both b,A;key is nothing but pow(A,b)mod(p);
		return b;
	}
	public static void main(String args[])throws Exception{
		Server server=new Server();
		Registry registry=LocateRegistry.createRegistry(1900);
		Naming.rebind("rmi://localhost:1900"+"/RMI", server);
		System.setProperty("java.security.policy","file:test.policy");
		Arrays.fill(fib, -1L);
	}
}


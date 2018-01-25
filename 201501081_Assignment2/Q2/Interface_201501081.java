import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.*;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.nio.ByteBuffer;
 interface Interface extends Remote{
String DHKey(int p,int g,String recv)throws RemoteException;
byte[] PrimalityTest(byte[] arr)throws RemoteException,UnsupportedEncodingException;
byte[] PalindromeTest(byte[] input)throws RemoteException,UnsupportedEncodingException;
byte[] StringCaseConverter(byte[] input)throws RemoteException,UnsupportedEncodingException;
byte[] Fibnoacci(byte[] input)throws RemoteException,UnsupportedEncodingException;
}

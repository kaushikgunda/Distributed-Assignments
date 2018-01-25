import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.*;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.nio.ByteBuffer;
 interface Interface extends Remote{
void Receive(byte[] input)throws RemoteException,UnsupportedEncodingException;
}

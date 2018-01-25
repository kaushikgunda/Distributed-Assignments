import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.*;
 interface Interface extends Remote{
String deposit(Long acc_no,double amt)throws RemoteException;
String withdraw(Long acc_no,double amt)throws RemoteException;
double balance(Long acc_no)throws RemoteException;
List<Transaction> transaction_details(Long account_no,Date start_date,Date end_date)throws RemoteException;//void transaction_details(String acc_no,Date start_date,Date end_date)throws RemoteException;
List<Transaction> transaction_details(Long account_no)throws RemoteException;
}

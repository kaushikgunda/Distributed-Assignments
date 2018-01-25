import java.rmi.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner.*;
import java.util.*;
import java.io.Serializable;


class Client 
{

	public static void deposit(Interface access,Long acc_no,int amount)throws RemoteException
	{
		System.out.println(access.deposit(acc_no,amount));
	}
	public static void withdraw(Interface access,Long acc_no,int amount)throws RemoteException
	{
		System.out.println(access.withdraw(acc_no,amount));
	}
	public static void balance(Interface access,Long acc_no)throws RemoteException
	{
		System.out.println(access.balance(acc_no));
	}
	public static void transaction_details(Interface access,Long acc_no,String start_date,String end_date)throws RemoteException,ParseException
	{
		List<Transaction> received=new ArrayList<Transaction>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = sdf.parse(start_date);
		Date date2 = sdf.parse(end_date	);
		received=access.transaction_details(acc_no,date1,date2);
		int i;
		for(i=0;i<14;i++)
			System.out.print(" ");
		System.out.println("***MINI-STATEMENT***\n");
		System.out.println(" Transaction_Id        Type            Final Amount");
		for(i=0;i<50;i++)
			System.out.print("_");
		System.out.println("\n");
		for(i=0;i<received.size();i++)
		{
			Transaction el=received.get(i);
			System.out.format("%15d | %15s |    %10f",el.transaction_id,el.type+"    ",el.final_amount);
			System.out.println("  ");
		}


	}
	public static void transaction_details(Interface access,Long acc_no)throws RemoteException{

		List<Transaction> received=new ArrayList<Transaction>();
		received=access.transaction_details(acc_no);
		int i;
		for(i=0;i<14;i++)
			System.out.print(" ");
		System.out.println("***MINI-STATEMENT***\n");
		System.out.println(" Transaction_Id        Type            Final Amount");
		for(i=0;i<50;i++)
			System.out.print("_");
		System.out.println("\n");
		for(i=0;i<received.size();i++)
		{
			Transaction el=received.get(i);
			System.out.format("%15d | %15s |    %10f",el.transaction_id,el.type+"    ",el.final_amount);
			System.out.println("  ");
		}


	}
	public static void main(String args[])throws ParseException
	{
		try{
			Interface access=(Interface)Naming.lookup("rmi://localhost:1900"+"/Bank");
			/*	deposit(access,12348L,88);
				withdraw(access,12348L,448);
				balance(access,12348L);
			//	transaction_details(access,12348L,args[0],args[1]);
			transaction_details(access,12348L);*/

			String in;
			Long acc_no=12348L;
			System.out.println("****Welcome to ATM****");
			System.out.print(">>");
			Scanner sc=new Scanner(System.in);
			while((in=sc.nextLine())!=null)
			{
				String[] splited = in.split("\\s+");
				if(new String("deposit").equals(splited[0]) && splited.length==2)
					deposit(access,acc_no,Integer.parseInt(splited[1]));
				if(new String("withdraw").equals(splited[0]) && splited.length==2)
					withdraw(access,acc_no,Integer.parseInt(splited[1]));
				if(new String("balance").equals(splited[0]) && splited.length==1)
					balance(access,acc_no);
				if(new String("transaction_details").equals(splited[0]) && splited.length==1)				
					transaction_details(access,acc_no);
				if(new String("transaction_details").equals(splited[0]) && splited.length==3)
				{
					transaction_details(access,acc_no,splited[1],splited[2]);	
				}
				System.out.print(">>");
			}


		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}

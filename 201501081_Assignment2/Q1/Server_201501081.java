import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.lang.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.io.Serializable;
class Transaction implements Serializable{
        int transaction_id;
        String type;//For whether deposit or withdrawl
        double amount_change;
        double final_amount;
        Date date;
        Long account_no;
        public Transaction(Long account_no,int transaction_id,String type,double amount_change,double final_amount,Date date)
        {
                this.account_no=account_no;
                this.transaction_id=transaction_id;
                this.type=type;
                this.amount_change=amount_change;
                this.final_amount=final_amount;
                this.date=date;
        }
}
 class Account{
        String name;
        Long account_no;
        String type;
        double balance;
        int portNo;
        List<Integer> transaction_list=new ArrayList<Integer>();
        public Account(String name,Long account_no,String type,double balance,int portNo)
        {
                this.name=name;
                this.account_no=account_no;
                this.type=type;
                this.balance=balance;
                this.portNo=portNo;
        }




}

 class Server extends UnicastRemoteObject implements Interface {
	public Server() throws RemoteException
	{
		super();
	}
	private static List<Account> accounts = new ArrayList<Account>();
	private static List<Transaction> transaction=new ArrayList<Transaction>();
	static HashMap<Long,Integer> map = new HashMap<Long,Integer>();
	//Create a class for each transaction and maintain a list of transaction_ids with each account_no as well
	int transaction_id=0;
	public String deposit(Long acc_no,double amount){
		int val=map.get(acc_no);
		Account element=accounts.get(val);
		double final_amount=element.balance+amount;
		element.balance=element.balance+amount;
		element.transaction_list.add(transaction_id);
		Date date=new Date();
		Transaction el=new Transaction(acc_no,transaction_id,"deposit",amount,final_amount,date);
		transaction.add(el);
		transaction_id=transaction_id+1;	
		return "Transaction Number -"+el.transaction_id+" Revised Balance="+element.balance;


	}
	public String withdraw(Long acc_no,double amount)
	{
		int val=map.get(acc_no);
		Account element=accounts.get(val);
		double final_amount=element.balance-amount;
		element.balance=element.balance-amount;
		element.transaction_list.add(transaction_id);
		Date date=new Date();
		Transaction el=new Transaction(acc_no,transaction_id,"withdraw",amount,final_amount,date);
		transaction.add(el);
		transaction_id=transaction_id+1;
		return "Transaction Number -"+el.transaction_id+" Revised Balance="+element.balance;

	}
	public double balance(Long acc_no)
	{
		int val=map.get(acc_no);
		Account element=accounts.get(val);
		return element.balance;	
	}
	public List<Transaction> transaction_details(Long account_no,Date start_date,Date end_date){

		int val=map.get(account_no);
		Account element=accounts.get(val);	
		int total_size=element.transaction_list.size();
		int i;
		List<Transaction> Answer=new ArrayList<Transaction>();
		try{
			for(i=0;i<total_size;i++)
			{
				int id=element.transaction_list.get(i);

				Transaction el=transaction.get(id);
				Date date=el.date;
				System.out.println(el.date.equals(end_date));
				if((date.before(end_date)|| date.equals(end_date)) &&( date.after(start_date)||date.equals(start_date)))
					Answer.add(el);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return Answer;

	}
	public List<Transaction> transaction_details(Long account_no){
		int val=map.get(account_no);
		Account element=accounts.get(val);
		int total_size=element.transaction_list.size();
		int i;
		List<Transaction> Answer=new ArrayList<Transaction>();
		for(i=0;i<total_size;i++)
		{
			int id=element.transaction_list.get(i);
			Transaction el=transaction.get(id);
			Answer.add(el);
		}
		return Answer;

	}

	public static void main(String args[]) throws Exception{
		try
		{
			Account account1=new Account("kaushik",12345L,"Basic",120,9999);
			Account account2=new Account("Ritu",12346L,"Basic",180,9998);
			Account account3=new Account("Mom",12347L,"Premium",3000,9997);
			Account account4=new Account("Dad",12348L,"Premium",3000,9996);
			accounts.add(account1);
			map.put(account1.account_no,0);
			accounts.add(account2);
			map.put(account2.account_no,1);
			accounts.add(account3);
			map.put(account3.account_no,2);
			accounts.add(account4);
			map.put(account4.account_no,3);

			Server bank=new Server();
			Registry registry=LocateRegistry.createRegistry(1900);
			Naming.rebind("rmi://localhost:1900"+"/Bank", bank); 
			System.setProperty("java.security.policy","file:test.policy");	
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

	}



}

import java.net.*;
import java.io.*;
import java.net.Socket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.concurrent.*;
import java.io.*;
public class Alice extends Thread {
	public ServerSocket serverSocket;
	public Scanner sc;
	public Alice(int port) throws IOException {
		serverSocket = new ServerSocket(port);

		//		serverSocket.setSoTimeout(10000);
	}

	public void print(long a,long total)
	{
		double val=(1-a/(double)total)*100;
		int j;
		for(j=1;j<10;j++)
		{
			if(val==j*10||val==j*10-5)
			{

				int k;
				System.out.print("[");
				for(k=1;k<j;k++)
					System.out.print("=");
				System.out.print("]");

				System.out.println("  "+val+"%");
			}	


		}



	}
	public int func(int a,int b)
{
if(a<b)
return a;
return b;
}
	public void run() {
		while(true) {
			try {
				System.out.println("Waiting for client on port " + 
						serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();

				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(server.getInputStream());
				sc= new Scanner(System.in);
				DataOutputStream out = new DataOutputStream(server.getOutputStream());

				String input;
				System.out.print(">>");
				while((input=sc.nextLine())!=null)
				{
					//System.out.println(in.readUTF());
					String[] splited = input.split("\\s+");
					int flag=0;
					try
					{
						if(new String("Sending").equals(splited[0]))
						{


							if(new String("TCP").equals(splited[2]) )
							{
								 File myFile = new File ("./"+splited[1]);
								                                                               FileInputStream fis = new FileInputStream(splited[1]);

								out.writeUTF("Sending");
								out.writeUTF("TCP");
								out.writeUTF(splited[1]);	
								BufferedInputStream bis = null;
								OutputStream os = null;
								byte [] buffer = new byte [1000];
								long remaining=myFile.length();
								out.writeLong(remaining);
								long total=remaining;
								while(fis.read(buffer,0,func(1000,(int)remaining))>0)
								{
									System.out.println(remaining);
									remaining-=1;
									
									print(remaining,total);
									String senddata=new String(buffer);
									out.write(buffer);			
								}
							}
							else
								if(new String("UDP").equals(splited[2]) && flag==0)
								{
									 File myFile = new File ("./"+splited[1]);
								                                                                FileInputStream fis = new FileInputStream(splited[1]);

									DatagramSocket ds= new DatagramSocket();
									out.writeUTF("Sending");
									out.writeUTF("UDP");
									out.writeUTF(splited[1]);      
									BufferedInputStream bis = null;
									byte [] buffer = new byte [1];
									long remaining=myFile.length();
									out.writeLong(remaining);
									long total=remaining;
									while(fis.read(buffer)>0)
									{
										remaining-=1;
										print(remaining,total);
										DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length,InetAddress.getByName("127.0.0.1"), serverSocket.getLocalPort()+10);
										ds.send(sendPacket);
									}
									ds.close();	
								}

							System.out.println("Sent File");
							System.out.print(">>");


						}
						else
						{
							out.writeUTF(input);
							System.out.print(">>");
						}
					}
					catch(ArrayIndexOutOfBoundsException e)
					{System.out.println("3 elements required          Format: Sending FileName TCP/UDP");
						System.out.print(">>");
					}
					catch(FileNotFoundException e)
					{
						System.out.println("No such File found");
					System.out.print(">>");
					}
					catch(ConnectException e)
					{
						System.out.println("Connection could not established");
						System.out.print(">>");
					}
				}

				server.close();

			}catch(SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			}catch(IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public static void main(String [] args) {
		int port = Integer.parseInt(args[0]);
		int port2= Integer.parseInt(args[1]);
		try {
			Thread t = new Alice(port);
			t.start();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		boolean scanning=true;
		while(true)
		{try
			{
				Socket client=new Socket("localhost",port2);	
				DatagramSocket UDPSocket=new DatagramSocket(port2+10);

				//                              client.connect(new InetSocketAddress("localhost", 5555));       
				InputStream inFromServer = client.getInputStream();
				DataInputStream in = new DataInputStream(inFromServer);
				while(true)
				{
					String read=in.readUTF();	
					String[] parse = read.split("\\s+");
					if(parse[0].equals("Sending"))      
					{
						try{
							String protocol = in.readUTF();
							if(new String("TCP").equals(protocol))
							{
								String fileName=in.readUTF();
								long filesize = in.readLong();
								long remaining=filesize;
								System.out.println(protocol+" "+fileName+" "+filesize);
								FileOutputStream fos = new FileOutputStream(fileName);
								byte[] buffer = new byte[1];
								long reads=0;
								long total=remaining;
								//while(remaining>0)
								while((reads = in.read(buffer)) > 0 ) {

									fos.write(buffer,0,(int)reads);

									remaining-=reads;
									double val=(1-remaining/(double)total)*100;
									int j;
									for(j=1;j<10;j++)
									{
										if(val==j*10|| val==j*10-5)
										{

											int k;
											System.out.print("[");
											for(k=1;k<j;k++)
												System.out.print("=");
											System.out.print("]");

											System.out.println("  "+val+"%");
										}


									}

									//	System.out.println(remaining);
									if(remaining==0)
										break;
								}
								System.out.println("Received File");
								System.out.print(">>");

								fos.close();	
							}
							else 
								if(new String("UDP").equals(protocol))
								{
									String fileName=in.readUTF();
									long filesize = in.readLong();
									long remaining=filesize;
									System.out.println("Receiving "+protocol+" "+fileName+" "+filesize);
									FileOutputStream fos = new FileOutputStream(fileName);
									byte[] buffer = new byte[1];
									long reads=0;	long total=remaining;
									//while(remaining>0)
									DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
									while( remaining > 0 ) {
										UDPSocket.receive(receivePacket);
										byte data[]=receivePacket.getData();
										fos.write(buffer,0,data.length);

										remaining-=data.length;
										double val=(1-remaining/(double)total)*100;
										int j;
										for(j=1;j<10;j++)
										{
											if(val==j*10 || val==j*10-5)
											{

												int k;
												System.out.print("[");
												for(k=1;k<j;k++)
													System.out.print("=");
												System.out.print("]");

												System.out.println("  "+val+"%");
											}


										}


										//      System.out.println(remaining);
										if(remaining==0)
											break;
									}
									System.out.println("Received File");
									System.out.print(">>");

									fos.close();


								}
						}
						catch(ConnectException e)
						{
							System.out.println("Connection could not established");
							System.out.print(">>");
						}

					}
					else
					{
						System.out.println("\nBob:" + read);
						System.out.print(">>");
					}
				}	
			}
			catch (ConnectException e) {
				try 

				{
					TimeUnit.SECONDS.sleep(1);

				} 
				catch(InterruptedException e2)
				{
					// this part is executed when an exception (in this example InterruptedException) occurs
				}
				//      System.out.println("Error while connecting. " );
			} catch (SocketTimeoutException e) {
				try 
				{
					TimeUnit.SECONDS.sleep(1);

				} 
				catch(InterruptedException e2)
				{
					// this part is executed when an exception (in this example InterruptedException) occurs
				}
				//  System.out.println("Connection: " + e.getMessage() + ".");
			} catch (IOException e) {
				try 
				{
					TimeUnit.SECONDS.sleep(1);

				} 
				catch(InterruptedException e2)
				{
					// this part is executed when an exception (in this example InterruptedException) occurs
				}
				//      e.printStackTrace();
			}

		}




	}
}

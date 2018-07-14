package com.adversary.KRA;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.*;
import com.tools.KRA.Message;
import com.tools.KRA.Utils;

public class Adversary {
//	public String[] str_padding(String[] str) {
//		char[] c = {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
//		for(int i = 0; i<str.length; i++) {
//			String p = new String(c, 0, 16 - str[i].length());
//			str[i] += p;
//		}
//		return str;
//	}
	
	public static Map<String, String[]> cypherbook() {
		String[] letters = {"HTTP            ", "POST            ", "GET             ", "INPUT           ", "OUTPUT          "};
		Map<String, String[]> book = new HashMap<String, String[]>();
		for(int i = 0; i<letters.length - 1; i++) {
			for(int j = i + 1; j<letters.length; j++) {
				byte[] xor_result = Utils.XOR(letters[i].getBytes(), letters[j].getBytes()); 
				String[] value = new String[2];
				value[0] = letters[i];
				value[1] = letters[j];
				book.put(Arrays.toString(xor_result), value);
			}
		}
//		System.out.println("The book contains these " + book.size() + " enterties:");
//		Set<Map.Entry<String, String[]>> set = book.entrySet();
//		for(Map.Entry<String, String[]> entery : set) {
//			System.out.print(entery.getKey() + " : " );
//			for(String str:entery.getValue()) {
//				System.out.print(str);
//			}
//			System.out.println();
//		}
		return book;
	}
	static Map<String, String[]> book = Adversary.cypherbook();
	public static void crackMsg(Vector<byte[]> v1, Vector<byte[]> v2) {
//		Adversary.cypherbook();
		String plaintext_1 = "";
		Vector<byte[]> plain_byte_1 = new Vector<byte[]>();
		Vector<byte[]> plain_byte_2 = new Vector<byte[]>();
		String plaintext_2 = "";
		Vector<byte[]> stream_code_1 = new Vector<byte[]>();
		Vector<byte[]> stream_code_2 = new Vector<byte[]>();
		Vector<String> v = new Vector<String>();
		for(int i = 0; i<v1.size(); i++) {
//			System.out.println("The size of v is : " + v1.size());
			v.add(Arrays.toString(Utils.XOR(v1.get(i), v2.get(i))));
		}
		//TODO look for the corresponding value in cypher book.
		int index = 0;
		for(String me : v) {
//			System.out.println("me : " + Arrays.toString(me));
//			byte[] test = {1, 26, 4, 5, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//			System.out.println("book.get([1, 26, 4, 5, 116, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]) : " + Adversary.cypherbook().get(test));
//			System.out.println("book.get(me) : " + book.get(me));
//			System.out.println("book : " + book);
//			System.out.println("The type of value of book's is : " + (book instanceof Map));
//			System.out.println("book.get(me)[0] is : " + book.get(me)[0]);
			stream_code_1.add(Utils.XOR(v1.get(index), book.get(me)[0].getBytes()));
			stream_code_2.add(Utils.XOR(v2.get(index), book.get(me)[1].getBytes()));
			index ++;
//			plaintext_1 += book.get(me)[0];
//			plaintext_2 += book.get(me)[1];
		}
		for(int i = 0; i<v1.size(); i++) {
			plain_byte_1.add(Utils.XOR(v1.get(i), stream_code_1.get(i)));
			plain_byte_2.add(Utils.XOR(v2.get(i), stream_code_2.get(i)));
		}
		for(byte[] b:plain_byte_1) {
			plaintext_1 += new String(b);
		}
		for(byte[] b:plain_byte_2) {
			plaintext_2 += new String(b);
		}
		System.out.println("The first plain text is : " + plaintext_1);
		System.out.println("The second plain text is : " + plaintext_2);
		System.out.println("The stream cipher code is : ");
		for(byte[] s:stream_code_1) {
			System.out.print(Arrays.toString(s));
		}
//		System.out.println();
//		System.out.println("The stream cipher code 2 is : ");
//		for(byte[] s:stream_code_2) {
//			System.out.print(Arrays.toString(s));
//		}
//		System.out.println();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String IP_ADDR = args[0];
//		int PORT1 = Integer.parseInt(args[1]);
//		int PORT1 = Integer.parseInt(args[2]);
		int PORT1 = 54321;
		int PORT2 = 12345;
		String IP_ADDR = "localhost";
		byte[] b1 = new byte[48];
		byte[] b2 = new byte[48];
		Vector<byte[]> v1 = new Vector<byte[]>();
		Vector<byte[]> v2 = new Vector<byte[]>();
		
		try {
			ServerSocket serverSocket = new ServerSocket(PORT1);
			while (true) {
				Socket client = serverSocket.accept();
				Socket ap = new Socket(IP_ADDR, PORT2);
				client.setKeepAlive(true);
				client.setTcpNoDelay(true);
				ap.setKeepAlive(true);
				ap.setTcpNoDelay(true);
				ObjectInputStream ois1 = null;
				ObjectOutputStream oos1 = null;
				ObjectInputStream ois2 = null;
				ObjectOutputStream oos2 = null;
				Object obj1 = null;
				Message msg1 = null;
				Message Msg1 = null; 
				Object obj2 = null;
				Message msg2 = null;
				Message Msg2 = null; 
				int flag = 0;
				int state = 0;

				
				while (true) {
					if (flag == 2) {
						ois1 = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
						obj1 = ois1.readObject();
						msg1 = (Message)obj1;
//						System.out.println("flag = " + flag + "************I'm here****************");
						System.out.println("Recieve from client:\n"+msg1.toString() + "\nBut I won't deliver it");
						
						
//						oos2 = new ObjectOutputStream(ap.getOutputStream());
//						oos2.writeObject(msg1);
//						oos2.flush();
						while(true) {
							ois1 = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
							obj1 = ois1.readObject();	
							msg1 = (Message)obj1;
							System.out.println("Recieve from client " + msg1.toString());	//read a packet from client.
							
			                if(msg1.type.equals("data")) {	//if this packet is a data block
			                	oos2 = new ObjectOutputStream(ap.getOutputStream());
//			                	System.out.println("Recieve from client: " + msg1.toString());
				                
				                //TODO load the data in vector1
				                v1.add(msg1.data);
				                //v1.add(msg1.data);	//load the data block in v1
//				                System.out.println("Now v1 is: ");
//				                for(byte[] me : v1) {
//				                	System.out.print(Arrays.toString(me));
//				                }
//				                System.out.println();
				                System.out.println("Send to ap: " + msg1.toString());
								oos2.writeObject(msg1);		//forward the data block to ap
								oos2.flush();
								ois2 = new ObjectInputStream(new BufferedInputStream(ap.getInputStream()));
				                obj2 = ois2.readObject();
				                msg2 = (Message)obj2;    
				                System.out.println("Recieve from ap: "+msg2.toString());	//read a packet from ap
				                oos1 = new ObjectOutputStream(client.getOutputStream());
				                System.out.println("Send to client " + msg2.toString());
								oos1.writeObject(msg2);	//write the packet to client, then go back to the beginning, waiting for another packet form client
								oos1.flush();
								continue;
			                }
			                else {	//means that client sends another Msg4 to AP, then it just forward Msg4 to ap
			                	oos2 = new ObjectOutputStream(ap.getOutputStream());
				                System.out.println("Write to ap " + msg2.toString());
								oos2.writeObject(msg1);	
								oos2.flush();
								flag++;
								break;
			                }
						}
						continue;
					}
					ois1 = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
					
					obj1 = ois1.readObject();
					msg1 = (Message)obj1;
//					System.out.println("flag = " + flag + "~~~~~~~~~~~~~~~~~~I'm here~~~~~~~~~~~~~~~~~~~");
					System.out.println("Recieve from client:"+msg1.toString());
					//TODO read other data from client until it's of v1's length
					if(v2.size() < v1.size()) {
						v2.add(msg1.data);
//						System.out.println("Now v2 is: ");
//						for(byte[] me : v2) {
//		                	System.out.print(Arrays.toString(me));
//		                }
//		                System.out.println();
					}
					oos2 = new ObjectOutputStream(ap.getOutputStream());
					System.out.println("Send to ap " + msg1.toString());
					oos2.writeObject(msg1);
					oos2.flush();
	                ois2 = new ObjectInputStream(new BufferedInputStream(ap.getInputStream()));
	                obj2 = ois2.readObject();
	                msg2 = (Message)obj2;
	                
	                System.out.println("Recieve from ap: "+msg2.toString());
	                
					
	                oos1 = new ObjectOutputStream(client.getOutputStream());
	                System.out.println("Send to client: " + msg2.toString());
					oos1.writeObject(msg2);	
					oos1.flush();
					
					flag++;
				}
			}
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			Adversary.crackMsg(v1, v2);
			System.out.println();
		}
		
	}

}

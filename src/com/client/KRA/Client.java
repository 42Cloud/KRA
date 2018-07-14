package com.client.KRA;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

import com.tools.KRA.Message;
import com.tools.KRA.Utils;

public class Client {

	public static final String IP_ADDR = "localhost";//服务器地址   
    public static final int PORT = 54321;//服务器端口号
    
	public static void main(String[] args) {
//		String IP_ADDR = args[0];
//		int PORT = Integer.parseInt(args[1]);
//		String MasterKey = args[2];
//		String filename = args[3];
		String MasterKey = "RAIN";
		String filename = "data.txt";
		
		System.out.println("客户端启动...");      
        
        InetAddress ia;
        byte[] mac = null;
		try {
			ia = InetAddress.getLocalHost();
			mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//    	System.out.println(Arrays.toString(mac));

        int Nonce = 0;
//        int r = 0;
        int ANonce;
        int CNonce;
        byte[] TK = null;
        int auth_flag = 0;
        Message msg = new Message();
        msg.type = "Authentication_request";
        
        while (true) {    
            Socket socket = null;  
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            
            try {  
                //创建一个流套接字并将其连接到指定主机上的指定端口号  
                socket = new Socket(IP_ADDR, PORT); 
                socket.setKeepAlive(true);
                socket.setTcpNoDelay(true);              
				
                if (auth_flag == 0) {
                	oos = new ObjectOutputStream(socket.getOutputStream());
                	
                	Message Msg = new Message();
                	if(msg.type.compareTo("Authentication_request") == 0) {
     
    					Msg.type = "Authentication_request";
//    					Msg.r = 0;
//    					Msg.code = null;
    					auth_flag = 1;
                    }
                	
                	System.out.println("client sends:\n" + Msg.toString());
                     
     				oos.writeObject(Msg);
     				oos.flush();
     				
     				ois = new ObjectInputStream(socket.getInputStream());
     				Object obj = ois.readObject();
     				if(obj != null) {
     					Message receive_msg = (Message)obj;
     					msg.type = receive_msg.type;
     					msg.r = receive_msg.r;
     					msg.code = receive_msg.code;
     					System.out.println("client receives\n:" + msg.toString());
     				}
                } 
                if (auth_flag == 1) {
                	Message Msg = new Message();
                	oos = new ObjectOutputStream(socket.getOutputStream());
                   
                	if(msg.type.compareTo("Msg1") == 0) {
                		Random random = new Random(2017);
                    	CNonce = random.nextInt();
                    	ANonce = Integer.parseInt(msg.code);
                    	TK = Utils.Hash(ANonce, CNonce, MasterKey);
                    	System.out.println("After recieving Msg1, the client has got these information: ");
                    	System.out.println("ANonce:" + ANonce +"\t"+ "CNonce:" + CNonce +"\t" + "Masterkey:" + MasterKey + "\t" + "TK Arrays:" + Arrays.toString(TK));
                    	Msg.type = "Msg2";
                    	Msg.r = msg.r;
                    	Msg.code = String.valueOf(CNonce);
                    	auth_flag = 3;
                	}	
                	System.out.println("client sends:\n" + Msg.toString());
                     
     				oos.writeObject(Msg);
     				oos.flush();
     				
     				ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
     				Object obj = ois.readObject();
     				if(obj != null) {
     					Message receive_msg = (Message)obj;
     					msg.type = receive_msg.type;
     					msg.r = receive_msg.r;
     					msg.code = receive_msg.code;
     					System.out.println("client receives:\n" + msg.toString());
     				}
                }
                if (auth_flag == 3) {
                	Message Msg = new Message();

                	if(msg.type.compareTo("Msg3") == 0) {
                    	Msg.type = "Msg4";
                    	Msg.r = msg.r;
                    	Msg.code = "ACK";
                    	Nonce = 0;
                    	auth_flag = 4;
                	}
                	System.out.println("client sends:\n" + Msg.toString());
                	oos = new ObjectOutputStream(socket.getOutputStream());
     				oos.writeObject(Msg);
     				oos.flush();
     				
                }
                if (auth_flag == 4) {
         	
            		String cipher;
                	String plain = "";
                	
                	plain = new String(Files.readAllBytes(Paths.get(filename)), "UTF-8");
                	System.out.println("After sending Msg4, the client starts to send message. Plain text of the message is:");
                	System.out.println(plain);
             
                	byte[] p_byte = plain.getBytes("UTF-8");
                	int len = (int)Math.ceil((double)p_byte.length/16);
                	
                	byte[] plain_msg = new byte[len * 16];
                	if(p_byte.length % 16 != 0) {
                		byte[] padding = new byte[16 - p_byte.length % 16];
                		System.arraycopy(p_byte, 0, plain_msg, 0, p_byte.length);
                		System.arraycopy(padding, 0, plain_msg, p_byte.length, padding.length);
                	} else {
                		System.arraycopy(p_byte, 0, plain_msg, 0, p_byte.length);
                	}
                		
                
                	for(int i=0; i<len; i++) {
                		byte[] msg_byte = new byte[16];
                		System.arraycopy(plain_msg, i*16, msg_byte, 0, 16);
                		byte[] key = Utils.Hash(mac, Nonce, TK);
                		Nonce++;
                		cipher = new String(Utils.XOR(msg_byte, key), "UTF-8");
                		Message Msg = new Message();
                		Msg.type = "data";
                		Msg.r = msg.r;
                		Msg.code = cipher;
//                		System.out.println("#####################msg_byte = " + Arrays.toString(msg_byte));
                		Msg.data = Utils.XOR(msg_byte, key);
                		try {
                			oos = new ObjectOutputStream(socket.getOutputStream());
	        				oos.writeObject(Msg);
	        				oos.flush();
	        				System.out.println("client sends:\n" + Msg.toString());
	        				Thread.sleep(1000);
                		} catch (Exception e) {
                			System.out.println("客户端发送异常:" + e.getMessage());
                		}
                		ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        				Object obj = ois.readObject();
        				msg = (Message)obj;
        				if(obj != null) {
        					System.out.println("client receives:\n" + msg.toString());
        					if(msg.type.compareTo("Msg3") == 0) {
                        		Msg = new Message();
                        		Msg.type = "Msg4";
                            	Msg.r = msg.r;
                            	Msg.code = "ACK";
                            	Nonce = 0;
                            	oos = new ObjectOutputStream(socket.getOutputStream());
                 				oos.writeObject(Msg);
                 				oos.flush();
                 				System.out.println("client sends:\n" + Msg.toString());
//                 				i = -1;
                 				Nonce = 0;
                        	} 
        					else {
        					}
        				}	
                	}   	
                }
                
              
				oos.close();
				ois.close();
              
            } catch (Exception e) {  
                System.out.println("客户端异常:" + e.getMessage());   
            } finally {  
                if (socket != null) {  
                    try {  
                        socket.close(); 
                        System.out.println("client关闭");
                    } catch (IOException e) {  
                        socket = null;   
                        System.out.println("客户端 finally 异常:" + e.getMessage());   
                    }  
                } 
                break;
            }  
        }   
        
        System.out.println("会话发送数据结束");
    }
}

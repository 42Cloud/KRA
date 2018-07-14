package com.ap.KRA;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

import com.tools.KRA.Message;
import com.tools.KRA.Utils;

public class TAP {

	public static int PORT = 12345;// 监听的端口号
	public static int auth_flag = 0;
	public static int count = 0;
	public static int ANonce = 0;
	public static int CNonce = 0;
	public static byte[] TK = null;
	public static String MasterKey;
	public static byte[] mac;
	public static int Nonce = 0;

	public static void main(String[] args) {
		/*
		 * String MasterKey = args[0]; String port = args[1];
		 */
		MasterKey = "RAIN";
		PORT = 12345;

		System.out.println("服务器启动...\n");

		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			while (true) {
				Socket socket = serverSocket.accept();
				InetAddress ia;
				try {
					ia = socket.getInetAddress().getLocalHost();
					mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
				} catch (UnknownHostException | SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					socket.setKeepAlive(true);
					socket.setTcpNoDelay(true);

					ObjectInputStream ois = null;
					ObjectOutputStream oos = null;
					Object obj = null;
					Message msg = null;

					if (auth_flag == 0) {
						ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
						oos = new ObjectOutputStream(socket.getOutputStream());

						obj = ois.readObject();
						msg = (Message) obj;

						if (msg.type.compareTo("Authentication_request") == 0) {
							Message Msg = new Message();
							count ++;
							Random random = new Random(2018);
							ANonce = random.nextInt();

							Msg.type = "Msg1";
							Msg.r = count;
							Msg.code = String.valueOf(ANonce);

							auth_flag += 1;

							oos.writeObject(Msg);
							oos.flush();

							System.out.println("ap receives:\n" + msg.toString());
							System.out.println("ap sends:\n" + Msg.toString());
						} else {

						}

					}
					if (auth_flag == 1) {
						ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
						oos = new ObjectOutputStream(socket.getOutputStream());

						obj = ois.readObject();
						msg = (Message) obj;

						if (msg.type.compareTo("Msg2") == 0) {
							Message Msg = new Message();
							count ++;
							CNonce = Integer.parseInt(msg.code);
							TK = Utils.Hash(ANonce, CNonce, MasterKey);
							Msg.type = "Msg3";
							Msg.r = count;
							Msg.code = "ACK";
							auth_flag += 2;
							oos.writeObject(Msg);
							oos.flush();
							long start = System.currentTimeMillis();
							System.out.println("ap receives:\n" + msg.toString());
							System.out.println("ap sends:\n" + Msg.toString());
							while (true) {
								try {
									ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
									obj = ois.readObject();
									msg = (Message) obj;
									System.out.println("ap receives:\n" + msg.toString());
									if (msg.type.compareTo("Msg4") == 0) {
										auth_flag = 4;
										count ++;
										oos = new ObjectOutputStream(socket.getOutputStream());
										Msg.type = "Msg4_ack";
										Msg.r = count;
										oos.writeObject(Msg);
										oos.flush();
										System.out.println("ap sends:\n" + Msg.toString());
										break;
									} else {
										long end = System.currentTimeMillis();
										// Error is Here , because of if delay < 2s , then it will wait to read , however client is also wait to read from ap.
										oos = new ObjectOutputStream(socket.getOutputStream());
										if (end - start > 2 * 1000) {
											// resend Msg3
											System.out.println("************overtime**************");
											Msg.type = "Msg3";
											count ++;
											Msg.r = count;
											System.out.println("This Msg3 is resent");
											start = System.currentTimeMillis();
										} else {
											Msg.type = "data_ack";
											count ++;
											Msg.r = count;
										}
										oos.writeObject(Msg);
										oos.flush();
										System.out.println("ap sends:\n" + Msg.toString());
									}
//									System.out.println("ap receives:\n" + msg.toString());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						} else {

						}
					}
					if (auth_flag == 3) {
						ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

						obj = ois.readObject();
						msg = (Message) obj;
						if (msg.type.compareTo("Msg4") == 0) {
						} else {
							System.out.println("ap receives:\n" + msg.toString());
						}
					}
					if (auth_flag == 4) {
						ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

						obj = ois.readObject();
						msg = (Message) obj;
						System.out.println("ap receives:\n" + msg.toString());
						if (msg.type.compareTo("data") == 0) {
							byte[] key = Utils.Hash(mac, Nonce, TK);
							String plain = new String(Utils.XOR(msg.data, key));
							System.out.println("The plain text is:");
							System.out.println(plain);

							while (true) {
								try {
									Message Msg = new Message();
									count ++;
									Msg.type = "data_ack";
									Msg.r = count;
									oos = new ObjectOutputStream(socket.getOutputStream());
									oos.writeObject(Msg);
									oos.flush();
									System.out.println("ap sends:\n" + Msg.toString());

									ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

									Object obj2 = ois.readObject();
									Message msg2 = (Message) obj2;
									System.out.println("ap receives:\n" + msg2.toString());
									Nonce++;
									key = Utils.Hash(mac, Nonce, TK);
									plain = new String(Utils.XOR(msg2.data, key));
									System.out.println("The plain text is: ");
									System.out.println(plain);
								} catch (EOFException e) {
									e.printStackTrace();
									System.out.println("end of data");
									break;
								}
							}
						}
						if (msg.type.compareTo("Msg4") == 0) {
							System.out.println("This output is just for test");
						}
					}

					ois.close();
					oos.close();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("服务器 run 异常: " + e.getMessage());
					e.printStackTrace();
				} finally {
					if (socket != null) {
						try {
							socket.close();
							System.out.println("AP关闭");
						} catch (Exception e) {
							socket = null;
							System.out.println("服务端 finally 异常:" + e.getMessage());
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

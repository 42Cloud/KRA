package com.tools.KRA;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
	public static byte[] Hash(int ANonce, int CNonce, String masterKey) {
		// TODO Auto-generated method stub
		String str = String.valueOf(ANonce) + String.valueOf(CNonce) + masterKey;
//		System.out.println(str);
		byte[] sha = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			sha = md.digest();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sha;
	}
	
	public static byte[] Hash(byte[] mac, int Nonce, byte[] TK) {
		byte[] N = toBytes(Nonce);
		byte[] d = new byte[N.length + mac.length + TK.length];
		System.arraycopy(mac, 0, d, 0, mac.length);
		System.arraycopy(N, 0, d, mac.length, N.length);
		System.arraycopy(TK, 0, d, N.length + mac.length, TK.length);
		byte[] sha = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(d);
			sha = md.digest();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sha;
		
	}
	
	public static byte[] XOR(byte[] b1, byte[] b2) {
		byte[] b = new byte[b1.length];
		for(int i=0; i<b1.length; i++) {
			b[i] = (byte) (0xff & (((int)b1[i]) ^ ((int)b2[i])));
		}
		return b;
	}
	
	private static byte[] toBytes(int i) {
		  byte[] result = new byte[4];

		  result[0] = (byte) (i >> 24);
		  result[1] = (byte) (i >> 16);
		  result[2] = (byte) (i >> 8);
		  result[3] = (byte) (i /*>> 0*/);

		  return result;
	}
}

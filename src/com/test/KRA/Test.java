package com.test.KRA;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.tools.KRA.Utils;

public class Test {
	public static Map cypherbook() {
		String[] letters = {"HTTP            ", "POST            ", "GET             ", "INPUT           ", "OUTPUT          "};
		Map book = new HashMap<byte[], String[]>();
		for(int i = 0; i<letters.length - 1; i++) {
			for(int j = i + 1; j<letters.length; j++) {
				byte[] xor_result = Utils.XOR(letters[i].getBytes(), letters[j].getBytes()); 
				String[] value = {letters[i], letters[j]};
				book.put(xor_result, value);
			}
		}
		System.out.println("The book contains these " + book.size() + " enterties:");
		Set<Map.Entry<byte[], String[]>> set = book.entrySet();
		for(Map.Entry<byte[], String[]> entery : set) {
			System.out.print(Arrays.toString(entery.getKey()) + " : " );
			for(String str:entery.getValue()) {
				System.out.print(str);
			}
			System.out.println();
		}
		return book;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int A = 1634881706;
		int C = 1634881706;
		String k = "RAIN";
		System.out.println(Arrays.toString(Utils.Hash(A, C, k)));
		System.out.println(Arrays.toString(Utils.Hash(A, C, k)));
		byte[] b = new byte[2];
		System.out.println(b.length);
		String str = "om in Wal-Mart. ";
		byte[] s = str.getBytes();
		System.out.println(s);
		System.out.println(s.toString());
		System.out.println(new String(s));
		Map<String, String[]> book = new HashMap<String, String[]>();
		String[] tmp = new String[2];
		tmp[0]="0";
		tmp[1]="1";
		book.put(Arrays.toString(s), tmp);
		b = str.getBytes();
		System.out.println(book.get(Arrays.toString(b))[0]);
//		byte[] key = new byte[16];
//		String tm = new String(Utils.XOR(str.getBytes(), key));
//		System.out.println(new String(Utils.XOR(tm.getBytes(), key)));
//		byte[] t = "[B@4f3f5b24".getBytes();
//		System.out.println("\"[B@4f3f5b24\".getBytes() = " + Arrays.toString(t));
//		byte[] a = {-40, 73, 26, 8, -48, 22, 24, 82, 27, 39, 39, -100, 94, 110, -68, -91};
//		System.out.println("[91, 66, 64, 52, 102, 51, 102, 53, 98, 50, 52]'s corresponding string is: " +  a.toString());
//		String test_str = "HTTP";
//		System.out.println("Test: " + Arrays.toString(test_str.getBytes()));
//		System.out.println("U's byte is : " + Arrays.toString("U".getBytes()));
//		System.out.println("T's byte is : " + Arrays.toString("T".getBytes()));
//		System.out.println("U's byte is : " + Arrays.toString("O".getBytes()));
//		System.out.println("O's byte is : " + Arrays.toString("N".getBytes()));
//		char h_char = 'H';
//		byte h_byte = (byte) h_char;
//		System.out.println("h_byte is : " + h_byte);
//		System.out.println("N xor T is : " + Arrays.toString(Utils.XOR("U".getBytes(), "T".getBytes())));
//		System.out.println("O xor N is : " + Arrays.toString(Utils.XOR("O".getBytes(), "N".getBytes())));
////		char[] test_char = {'H', 'T', 'T', 'P'};
////		System.out.println();
//		Test.cypherbook();
	}

}

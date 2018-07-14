package com.tools.KRA;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
	public String type;
	public int r;
	public String code;
	public byte[] data;
	public String toString() {
		return "This packet is of " + this.type + " type. " + "And it's r is: " + this.r + " It's payload is: " + Arrays.toString(this.data);
	}
}
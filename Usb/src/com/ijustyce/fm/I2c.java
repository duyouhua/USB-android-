package com.ijustyce.fm;

public class I2c {
	public native int read(String nodeName, int slave_adr, int reg_adr, int buf[], int Length);
	public native int write(String nodeName, int i2c_adr, int sub_adr, int buf[], int Length);
	public native int FMcontrol(String mode, int argv);
	public native String jniTest();
	
	static {
		System.loadLibrary("fm");
	}
	
}

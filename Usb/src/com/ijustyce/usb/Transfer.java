package com.ijustyce.usb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Transfer implements Runnable {

	public interface TransferRevMsgDelegate {
		public void transferRevMsgAction(String s);
		public void transferRevSocketDisconnectAction();
	}

	TransferRevMsgDelegate transferRevMsgDelegate;

	public void setTransferRevMsgDelegateAction(TransferRevMsgDelegate t) {
		transferRevMsgDelegate = t;
	}

	private Socket client;

	private BufferedOutputStream outputStream;

	public Transfer(Socket client) {

		this.client = client;
	}

	public void sendMsg(String s) {

		if (outputStream == null) {
			return;
		}

		try {
			outputStream.write(s.getBytes());
			outputStream.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void run() {

		BufferedOutputStream out;
		BufferedInputStream in;
		try {

			out = new BufferedOutputStream(client.getOutputStream());
			outputStream = out;
			in = new BufferedInputStream(client.getInputStream());
			while (true) {
				try {
					if (!client.isConnected()) {
						break;
					}

					String s = readCMDFromSocket(in);
					// if (this.transferRevMsgDelegate !=
					// null&&s!=null&&s.length()>0) {

					this.transferRevMsgDelegate.transferRevMsgAction(s);

					// }

				} catch (Exception e) {
					e.printStackTrace();
					this.transferRevMsgDelegate.transferRevSocketDisconnectAction();
					break;
				}
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (client != null) {
					client.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String readCMDFromSocket(InputStream in) throws Exception{
		int MAX_BUFFER_BYTES = 2048;
		String msg = "";
		byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
		int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
		msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");
		tempbuffer = null;
		return msg;
	}

}

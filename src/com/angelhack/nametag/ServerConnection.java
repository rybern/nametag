package com.angelhack.nametag;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class ServerConnection {
	//public static final String SERVER_ADDRESS = "attu4.cs.washington.edu";
	public static final String SERVER_ADDRESS = "162.243.139.224";
	public static final int SERVER_PORT = 12345;
	
	private Socket socket;
	public ServerConnection(){
		Log.v("SERVER_CONNECTION", "TRYING TO CONNECT TO SERVER!");
		
		Thread t = new Thread() {
			public void run() {
				try {
					socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String[] ask(final String request) {
		final byte[] buffer = new byte[2048];
		Thread t = new Thread() {
			public void run() {
				try {
					socket.getOutputStream().write(request.getBytes());
					socket.getInputStream().read(buffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String[] response = new String(buffer).split("\n");
		
		if(!response[0].equals("OK")) {
			Log.v("ServerConnection", "SERVER DID NOT REPLY WITH OK");
			return null;
		}

		Log.v("ServerConnection", "REVIEVED RESPONSE: "+new String(buffer));
		Log.v("ServerConnection", "REVIEVED RESPONSE LENGTH: "+response.length);
		
		return response;
	}
	
	public void close() {
		Thread t = new Thread() {
			public void run() {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

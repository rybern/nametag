package com.angelhack.nametag;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PushProfileActivity extends Activity{
	
	NameTagActivity parent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_card);
		
		Bundle b = getIntent().getExtras(); 
		final User usr = b.getParcelable("user");
		
		TextView nearMe = (TextView) findViewById(R.id.UserName);
		nearMe.setText(usr.getName());
		
		ImageView profImage = (ImageView) findViewById(R.id.profImage);
		profImage.setImageBitmap(usr.getImage());
		
		Button pushButton = (Button) findViewById(R.id.pushButton);
		pushButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				btPushProfile(usr.getAddress());
			}
		});
	}

	private void btPushProfile(String bt_addr) {
		NameTagActivity.bluetooth.cancelDiscovery();
		
		BluetoothDevice remote = NameTagActivity.bluetooth.getRemoteDevice(bt_addr);
		BluetoothSocket btSocket = null;
		try {
			btSocket = remote.createRfcommSocketToServiceRecord(NameTagActivity.STANDARD_UUID);
			btSocket.connect();

			OutputStream out = btSocket.getOutputStream();
			new ObjectOutputStream(out).writeObject(NameTagActivity.me);
			btSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

}


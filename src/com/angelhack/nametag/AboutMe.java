package com.angelhack.nametag;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AboutMe extends Activity {
	Context context;
	Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_me);
		
		Log.v("AboutMe", "GOT INTO ABOUTME");
		
		activity = this;
		context = this.getApplicationContext();
		
		final EditText name = (EditText) findViewById(R.id.fullName);
		final EditText email = (EditText) findViewById(R.id.emailAddress);
		final EditText fburl = (EditText) findViewById(R.id.fbURL);
		final EditText liurl = (EditText) findViewById(R.id.liURL);
		final EditText twurl = (EditText) findViewById(R.id.twitterURL);
		Button booty = (Button) findViewById(R.id.submitButton);
		
		User me = loadUser();
		
		if(me != null) {
			if(me.getName() != null)
				name.setText(me.getName());
			if(me.getEmail() != null)
				email.setText(me.getEmail());
			if(me.getFacebookURL() != null)
				fburl.setText(me.getFacebookURL());
			if(me.getLinkedinURL() != null)
				liurl.setText(me.getLinkedinURL());
			if(me.getTwitterURL() != null)
				twurl.setText(me.getTwitterURL());
		}
		
		booty.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				String nm = name.getText().toString();
				String em = email.getText().toString();
				String fb = fburl.getText().toString();
				String li = liurl.getText().toString();
				String tw = twurl.getText().toString();
				String addr = BluetoothAdapter.getDefaultAdapter().getAddress();
				
				User me = new User(nm, em, fb, li, tw, "", addr);
							
				saveUser(me, "me.dat");
				pushPublicProfile(addr, nm, em);

				NameTagActivity.me = me;
				Log.v("ALLLLLLLLLLLLLLLLLLLLLLLLLLLLLl", me.getName());
				
				Intent buildMe = new Intent(activity, NameTagActivity.class);
				startActivity(buildMe);
				activity.finish();
			}
		});
		
	}
	
	public User loadUser() {
		User me = null;
		String filename = "me.dat";
		FileInputStream fis;
		try {
			fis = getApplicationContext().openFileInput(filename);
			ObjectInputStream is = new ObjectInputStream(fis);
			me = (User) is.readObject();
			is.close();
		} catch (FileNotFoundException e) {
			Log.v("FNF", "StreamCorrupted");
		} catch (StreamCorruptedException e) {
			Log.v("NameTag", "StreamCorrupted");
		} catch (IOException e) {
			Log.v("NameTag", "IOErr");
		} catch (ClassNotFoundException e) {
			Log.v("NameTag", "CNFE");
		}
		return me;
	}
	
	public void saveUser(User user, String filepath) {
		String filename = "me.dat";
		FileOutputStream fos;
		try {
			fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(user);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void pushPublicProfile(String addr, String name, String email) {
		if (name.equals("failed load"))
			return;
		ServerConnection sc = new ServerConnection();
		sc.ask("post "+addr+" "+name+" "+email);
		sc.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about_me, menu);
		return true;
	}
}

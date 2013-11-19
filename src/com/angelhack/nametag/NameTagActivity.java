package com.angelhack.nametag;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.*;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class NameTagActivity extends FragmentActivity implements
		ActionBar.TabListener {
	public static User me;
	public static Object signaler;
	public static UUID STANDARD_UUID;
    public static ArrayAdapter<String> aa;
    public static ArrayList<User> users = new ArrayList<User>();
    public static ArrayList<User> filledUsers = new ArrayList<User>();
    public BroadcastReceiver btReceiver;
    public IntentFilter filter;
	public static BluetoothAdapter bluetooth;
	public NameTagActivity nameTagActivity;
	public static final int REQUEST_ENABLE_BT = 0;
	private Toast notToast;
	
	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		signaler = new Object();
		me = getMe();
		setContentView(R.layout.activity_name_tag);
		
		filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		btReceiver = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		    	//placeholder
		    }
		};
		STANDARD_UUID = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
		nameTagActivity = this;
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Context context = getApplicationContext();
		CharSequence text = "You have a new Card!";
		int duration = Toast.LENGTH_SHORT;

		notToast = Toast.makeText(context, text, duration);
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		initBluetooth();
		initBluetoothServer();
	}
	
	public User getMe() {
		if (me != null)
			return me;
			
		String filename = "me.dat";
		FileInputStream fis;
		try {
			fis = getApplicationContext().openFileInput(filename);
			ObjectInputStream is = new ObjectInputStream(fis);
			me = (User) is.readObject();
			is.close();
		} catch (FileNotFoundException e) {
			Log.v("NameTag", "FileNotFound");
			Intent buildMe = new Intent(this, AboutMe.class);
			startActivity(buildMe);
		} catch (StreamCorruptedException e) {
			Log.v("NameTag", "StreamCorrupted");
		} catch (IOException e) {
			Log.v("NameTag", "IOErr");
		} catch (ClassNotFoundException e) {
			Log.v("NameTag", "CNFE");
		}
		
		if (me == null) {
			Log.v("ALLLLLLLLLLLSDFSDF", "I'm in the null if");
		}
		
		return me;
	}
	
	private void initBluetooth() {
		bluetooth = BluetoothAdapter.getDefaultAdapter();
		if (bluetooth == null) {
			Log.v("shittyname", "no bluetooth! trying anyway");
		}
		
		if (!bluetooth.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}
	
	private void initBluetoothServer() {
		Thread s = new Thread() {
			public void run() {
				try {	
					BluetoothServerSocket btServerSocket = bluetooth.listenUsingRfcommWithServiceRecord("BT_SERVER", STANDARD_UUID);
					
					while(true) {
						final BluetoothSocket client = btServerSocket.accept();
						Thread t = new Thread() {
							public void run() {
								Log.v("BLUETOOTH SERVER", "RECIEVING CONNECION");
								recieveProfile(client);
							}
						};
						t.start();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		s.start();
	}

	private void recieveProfile(BluetoothSocket client) {
		if(bluetooth.isDiscovering())
			bluetooth.cancelDiscovery();
		
		User recieved = null;
		for(int i = 0; i < 4; i++) {
			try {
				InputStream in = client.getInputStream();
				while(in.available() == 0);
				recieved = (User)(new ObjectInputStream(in).readObject());
				break;
			} catch (IOException e) {
				Log.v("shittyname", "IOException on bluetooth transfer");
				continue;
			} catch (ClassNotFoundException e) {
				Log.v("shittyname", "Class not found over bluetooth stream");
				continue;
			}
		}

		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log.v("BLUETOOTH SERVER", "RECIEVED PROFILE: "+recieved.getName());
		if (recieved != null)
			handleRecievedProfile(recieved);
	}
	
	public synchronized void handleRecievedProfile(User profile) {
		if (profile == null)
			return;
		boolean match = false;
		for(User u:users)
			if(u.getAddress().equals(profile.getAddress()))
			{
				if(!u.fullyLoaded) {
					u.setFacebookURL(profile.getFacebookURL());
					u.setLinkedinURL(profile.getLinkedinURL());
					u.setTwitterURL(profile.getTwitterURL());
					u.fullyLoaded = true;
					
					
					try {
				        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
				        r.play();
				    } catch (Exception e) {}
					notToast.show();
					
					filledUsers.add(u);
				}
				match = true;
				break;
			} else {
				Log.v("HANDLE_RECIEVED_PROFILE", "recieved ("+profile.getName()+", "+profile.getAddress()+") have ("+u.getName()+", "+u.getAddress()+")");
			}
		if(!match) {
			Log.v("HANDLE_RECIEVED_PROFILE", "DID NOT FIND AN EXISTING USER WITH THE SAME ADDRESS AS INCOMING");
			filledUsers.add(profile);
			try {
		        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		        r.play();
		    } catch (Exception e) {}
			notToast.show();
		}
		Log.v("RECIEVEDPROFILE", profile.getName());
	}
	
	public void onPause() {
		super.onPause();
		unregisterReceiver(btReceiver);
	}
	
	public void onDestroy() {
		super.onPause();
		
		String filename = "inbox.dat";
		FileOutputStream fos;
		try {
			fos = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(filledUsers);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onResume() {
		super.onResume();
		registerReceiver(btReceiver, filter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.name_tag, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			switch (position)
			{
			default:
			case 1:
				Fragment fragment = new NearMeFragment();
				Bundle args = new Bundle();
				args.putInt(NearMeFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				return fragment;
			case 0:
				Fragment mefragment = new MeFragment();
				Bundle meArgs = new Bundle();
				meArgs.putInt(MeFragment.ARG_SECTION_NUMBER, position + 1);
				mefragment.setArguments(meArgs);
				return mefragment;
			case 2:
				Fragment _fragment = new InboxFragment();
				Bundle _args = new Bundle();
				_args.putInt(InboxFragment.ARG_SECTION_NUMBER, position + 1);
				_fragment.setArguments(_args);
				return _fragment;
			}
			
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			//Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return "Me";
			case 1:
				return "Near Me";
			case 2:
				return "Inbox";
			}
			return null;
		}
	}
}
	

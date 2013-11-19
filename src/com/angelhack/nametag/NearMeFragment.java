package com.angelhack.nametag;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;


/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
public class NearMeFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";

	public static final int REQUEST_ENABLE_BT = 0;
	public NameTagActivity parent;
	private ListView nearMe;
	
	public NearMeFragment() {
		parent = (NameTagActivity) getActivity();
	}
	
	public void onAttach(Activity activity) {
        super.onAttach(activity);
        parent = (NameTagActivity)activity;
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_near,
				container, false);
		
		nearMe = (ListView) rootView.findViewById(R.id.nearMeList);
		nearMe.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parentView, View view,
			    int position, long id) {
				  Intent loadPublicProfile = new Intent(view.getContext(), PushProfileActivity.class);
				  loadPublicProfile.putExtra("user", (Parcelable)parent.users.get(position));
				  startActivity(loadPublicProfile);
			  }
			});
		
		parent.btReceiver = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        // When discovery finds a device
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		            // Get the BluetoothDevice object from the Intent
		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		            // Add the name and address to an array adapter to show in a ListView
		            handleDiscoveredBT(device);
		        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
		            //unregister(this);
		        }
		    }
		};

		// Register the BroadcastReceiver
		parent.registerReceiver(parent.btReceiver, parent.filter); // Don't forget to unregister during onDestroy
		
		parent.bluetooth.startDiscovery();
		ArrayList<String> names = new ArrayList<String>();
		for (User person : parent.users) {
			names.add(person.getName());
		}
        parent.aa = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, names);
		nearMe.setAdapter(parent.aa);
//		Button twitterButton = (Button) rootView.findViewById(R.id.twitterButton);
//		fbButton.setOnClickListener(new OnClickListener() {
//			 
//			@Override
//			public void onClick(View arg0) {
//			  Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(parent.me.getFacebookURL()));
//			    startActivity(browserIntent);
//			}
//		});
	
		Button refreshButton = (Button) rootView.findViewById(R.id.refreshButton);
		refreshButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view)  {
				parent.users = new ArrayList<User>();
				parent.bluetooth.cancelDiscovery();
				parent.bluetooth.startDiscovery();
				updateViews();
			}
		});
		
		return rootView;
	}
	
	private void handleDiscoveredBT(final BluetoothDevice device) {
		new Thread() {
			public void run() {
				Log.v("shittyname", "DISCOVERED DEVICE: "+device.getName()+", "+device.getAddress());
				User p = User.loadPublicProfile(device.getAddress());
				if(p==null) {
					Log.v("shittyname", "PROFILE FOR ADDRESS "+device.getAddress()+" NOT FOUND");
				} else {
					Log.v("shittyname", "SUCCESSFULLY GOT PROFILE. "+device.getAddress()+"'s name is "+p.getName());
					handleRecievedPublicProfile(p);
				}
			}
		}.start();
	}
	
	private synchronized void handleRecievedPublicProfile(User p) {
		for (User u : parent.users) {
			if (u.getName().equals(p.getName()))
				return; //repeat
		}
		parent.users.add(p);
		updateViews();
	}
	

	public void updateViews() {
		Thread t = new Thread() {
			public void run() {
				ArrayList<String> names = new ArrayList<String>();
				for (User person : parent.users) {
					names.add(person.getName());
				}
		        parent.aa = new ArrayAdapter<String>(parent, android.R.layout.simple_list_item_1, names);
				nearMe.setAdapter(parent.aa);
			}
		};
		parent.runOnUiThread(t);
	}

	public void onActivityResult (int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_ENABLE_BT) {
			Log.v("shittyname", "got response code "+requestCode);
			//handle resonseCode, could be canceled etc.
		}
	}

	public Bitmap getCroppedBitmap(Bitmap bitmap) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	            bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
	            bitmap.getWidth() / 2, paint);
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
	    //return _bmp;
	    return output;
	}
}
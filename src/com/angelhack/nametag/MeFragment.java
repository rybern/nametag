package com.angelhack.nametag;

import java.io.IOException;
import java.io.InputStream;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
public class MeFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	private NameTagActivity parent;
	
	public MeFragment() {
		parent = (NameTagActivity) getActivity();
	}

	public void onAttach(Activity activity) {
        super.onAttach(activity);
        parent = (NameTagActivity)activity;
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
		
		View rootView = inflater.inflate(R.layout.activity_inbox_profile,
				container, false);
		ImageView imgView = (ImageView) rootView
				.findViewById(R.id.profImage);
		
		Log.v("BLAH", ""+parent.me);
		TextView usrName = (TextView) rootView.findViewById(R.id.UserName);
		if (parent.me == null) {
			parent.me = new User("failed load", parent.bluetooth.getAddress());
			Intent buildMe = new Intent(parent.getApplicationContext(), AboutMe.class);
			startActivity(buildMe);
		}
		usrName.setText(parent.me.getName());
		
		TextView email = (TextView) rootView.findViewById(R.id.email);
		email.setText(parent.me.getEmail());
		Button fbButton = (Button) rootView.findViewById(R.id.facebookButton);
		Button liButton = (Button) rootView.findViewById(R.id.linkedinButton);
		Button twitterButton = (Button) rootView.findViewById(R.id.twitterButton);
		ImageView pic = (ImageView) rootView.findViewById(R.id.profImage);
		User temp = User.loadPublicProfile(parent.bluetooth.getAddress());
		if (parent.bluetooth.getAddress() == null)
			Log.v("STEEEFFFAAAANNNN", "THE ADDRESS IS NULLLLLLL");
		if(temp != null) {
			Log.v("STEFFFFAANNNNNNN", "THE TEMP ISN'T NULL BUT THERES NO DMAN IMAGE.");
			pic.setImageBitmap(temp.getImage());
		}
		if (parent.me.getFacebookURL() == null) {
			fbButton.setClickable(false);
		} else {
			fbButton.setBackgroundColor(0xFF3B5998);
			fbButton.setTextColor(0xFFFFFFFF);
			fbButton.setOnClickListener(new OnClickListener() {
				 
				@Override
				public void onClick(View arg0) {
				  Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(parent.me.getFacebookURL()));
				    startActivity(browserIntent);
				}
			});
		}
		
		if (parent.me.getLinkedInURL() == null) {
			liButton.setClickable(false);
		} else {
			liButton.setBackgroundColor(0xFF4875B4);
			liButton.setTextColor(0xFFFFFFFF);
			liButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(parent.me.getLinkedInURL()));
					startActivity(browserIntent);
				}
			});
		}
		
		if (parent.me.getTwitterURL() == null) {
			twitterButton.setClickable(false);
		} else {
			twitterButton.setBackgroundColor(0xFF4099FF);
			twitterButton.setTextColor(0xFFFFFFFF);
			twitterButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
	 
				  Intent browserIntent = 
	                            new Intent(Intent.ACTION_VIEW, Uri.parse(parent.me.getLinkedInURL()));
				    startActivity(browserIntent);
				}
			});
		}
		
		
		
		

		return rootView;
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
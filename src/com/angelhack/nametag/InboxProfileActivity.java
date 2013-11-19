package com.angelhack.nametag;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class InboxProfileActivity extends Activity {
NameTagActivity parent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox_profile);
		
		Bundle b = getIntent().getExtras(); 
		final User usr = b.getParcelable("user");
		
		TextView nearMe = (TextView) findViewById(R.id.UserName);
		nearMe.setText(usr.getName());
		
		ImageView profImage = (ImageView) findViewById(R.id.profImage);
		profImage.setImageBitmap(usr.getImage());
		
		TextView emailText = (TextView) findViewById(R.id.email);
		emailText.setText(usr.getEmail());
		
		Button facebookButton = (Button) findViewById(R.id.facebookButton);
		if (usr.getFacebookURL() == null || usr.getFacebookURL().equals("")) {
			facebookButton.setClickable(false);
		} else {
			facebookButton.setBackgroundColor(0xFF3B5998);
			facebookButton.setTextColor(0xFFFFFFFF);
			facebookButton.setOnClickListener(new OnClickListener() {
				 
				@Override
				public void onClick(View arg0) {
	 
				  Intent browserIntent = 
	                            new Intent(Intent.ACTION_VIEW, Uri.parse(usr.getFacebookURL()));
				    startActivity(browserIntent);
	 
				}
	 
			});
		}
		
		Button liButton = (Button) findViewById(R.id.linkedinButton);
		if (usr.getLinkedInURL() == null || usr.getLinkedInURL().equals("")) {
			liButton.setClickable(false);
		} else {
			liButton.setBackgroundColor(0xFF4875B4);
			liButton.setTextColor(0xFFFFFFFF);
			liButton.setOnClickListener(new OnClickListener() {
				 
				@Override
				public void onClick(View arg0) {
	 
				  Intent browserIntent = 
	                            new Intent(Intent.ACTION_VIEW, Uri.parse(usr.getLinkedInURL()));
				    startActivity(browserIntent);
	 
				}
	 
			});
		}
		
		Button twitterButton = (Button) findViewById(R.id.twitterButton);
		if (usr.getTwitterURL() == null || usr.getTwitterURL().equals("")) {
			twitterButton.setClickable(false);
		} else {
			twitterButton.setBackgroundColor(0xFF4099FF);
			twitterButton.setTextColor(0xFFFFFFFF);
			twitterButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
				  Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(usr.getTwitterURL()));
				  startActivity(browserIntent);
				}
			});
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

}

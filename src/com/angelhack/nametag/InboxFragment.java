package com.angelhack.nametag;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class InboxFragment extends Fragment {
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	public NameTagActivity parent;
	
	public InboxFragment() {
		parent = (NameTagActivity) getActivity();
	}
	
	public void onAttach(Activity activity) {
        super.onAttach(activity);
        parent = (NameTagActivity)activity;
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_near, //can use the same fragment
				container, false);
		
		final ListView inbox = (ListView) rootView.findViewById(R.id.nearMeList);
		String fileName = "inbox.dat";

		ArrayList<User> loadedInbox = null;
		
		FileInputStream fis;
		try {
			fis = this.getActivity().getApplicationContext().openFileInput(fileName);
			ObjectInputStream is = new ObjectInputStream(fis);
			loadedInbox = (ArrayList<User>) is.readObject();
			is.close();
		} catch (FileNotFoundException e) {
			Log.v("ANGELHACK InboxFragment", "no inbox save found");
		} catch (StreamCorruptedException e) {
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
			Log.v("ANGELHACK InboxFragment", "no inbox save found");
		}

		if (loadedInbox != null) {
			for (User person : loadedInbox) {
				boolean found = false;
				for (User u:parent.filledUsers) {
					if (u.getName().equals(person.getName())) {
						found = true;
						break;
					}
				}
				if (!found)
					parent.filledUsers.add(person);
			}
		} 
		
		ArrayList<String> names = new ArrayList<String>();
		for (User person : parent.filledUsers) {
			names.add(person.getName());
		}
		
        parent.aa = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, names);

		inbox.setAdapter(parent.aa);
		
		inbox.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parentView, View view,
			    int position, long id) {
				  Intent loadPublicProfile = new Intent(view.getContext(), InboxProfileActivity.class);
				  loadPublicProfile.putExtra("user", (Parcelable)parent.filledUsers.get(position));
				  startActivity(loadPublicProfile);
			  }
			});

		Button refreshButton = (Button) rootView.findViewById(R.id.refreshButton);
		refreshButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view)  {
				ArrayList<String> names = new ArrayList<String>();
				for (User person : parent.filledUsers) {
					names.add(person.getName());
				}
				
		        parent.aa = new ArrayAdapter<String>(parent, android.R.layout.simple_list_item_1, names);

				inbox.setAdapter(parent.aa);
			}
		});
		
		return rootView;
	}
}

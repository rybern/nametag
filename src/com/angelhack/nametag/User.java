package com.angelhack.nametag;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class User implements Parcelable, Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String facebookURL;
	private String linkedinURL;
	private String twitterURL;
	private String pictureURL;
	private String email;
	private Bitmap picture;
	private String address;
	
	public boolean fullyLoaded;
	
	public static User loadPublicProfile(String btid) {
		ServerConnection sc;
		sc = new ServerConnection();
		String[] response = sc.ask("get "+btid);
		sc.close();
		
		//response should be [OK, NAME, IMAGE_URL]
		if(response == null || response[0].equals("NF") || response.length < 3) {
			return null;
		}
		
		for(String word:response) {
			Log.v("ANGELHACK_PARSER", word);
		}

		User p = new User(response[1], response[2], btid);
		return p;
	}
	
	public User(String name, String addr) {
		this.name = name;
		address = addr;
	}
	
	public User(String name, String pictureURL, String addr) {
		this.name = name;
		this.pictureURL = pictureURL;
		address = addr;
		fullyLoaded = false;
	}
	
	public User(String name, String email, String fb, String li, String tw, String pictureURL, String addr) {
		this.name = name;
		this.email = email;
		this.facebookURL = fb;
		this.linkedinURL = li;
		address = addr;
		this.twitterURL = tw;
		if (this.pictureURL != null)
			this.pictureURL = pictureURL;
		fullyLoaded = true;
	}
	
	public Bitmap getImage() {
		if (picture == null) {
			loadBitmap(pictureURL);
		}
		return picture;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getFacebookURL() {
		return this.facebookURL;
	}
	
	public String getLinkedInURL() {
		return this.linkedinURL;
	}
	
	public String getTwitterURL() {
		return this.twitterURL;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getLinkedinURL() {
		return linkedinURL;
	}

	public void setLinkedinURL(String linkedinURL) {
		this.linkedinURL = linkedinURL;
	}

	public void setFacebookURL(String facebookURL) {
		this.facebookURL = facebookURL;
	}

	public void setTwitterURL(String twitterURL) {
		this.twitterURL = twitterURL;
	}

	public String getEmail() {
		return this.email;
	}

    protected User(Parcel in) {
        name = in.readString();
        facebookURL = in.readString();
        linkedinURL = in.readString();
        twitterURL = in.readString();
        pictureURL = in.readString();
        email = in.readString();
        address = in.readString();
        picture = (Bitmap)in.readValue(null);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(facebookURL);
        dest.writeString(linkedinURL);
        dest.writeString(twitterURL);
        dest.writeString(pictureURL);
        dest.writeString(email);
        dest.writeString(address);
        dest.writeValue(picture);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
    
    private void loadBitmap(final String url) {
	    Thread t = new Thread() {
		    public void run() {
				int ioBufferSize = 1024;
			    InputStream in = null;
			    BufferedOutputStream out = null;
			    try {
			        in = new BufferedInputStream(new URL(url).openStream(), ioBufferSize);
		
			        final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			        out = new BufferedOutputStream(dataStream, ioBufferSize);
			        IOUtils.copy(in, out);
			        out.flush();
		
			        final byte[] data = dataStream.toByteArray();
			        BitmapFactory.Options options = new BitmapFactory.Options();
			        //options.inSampleSize = 1;
		
			        picture = BitmapFactory.decodeByteArray(data, 0, data.length,options);
			    } catch (IOException e) {
			        Log.e("shittyname", "Could not load Bitmap from: " + url);
			    } finally {
			        try {
			        	if(out != null)
			        		out.close();
						if(in != null)
							in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			    }
		    }
	    };
	    t.start();
	    try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    Log.v("USER", "tried to load bitmap from "+pictureURL+", got "+picture);
	}
}
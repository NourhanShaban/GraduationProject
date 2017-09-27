package at.graduation.project.facebook;

import android.os.Bundle;
import android.os.Handler;

import android.app.Activity;
import android.app.ProgressDialog;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.SessionStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class TestPost extends Activity{
	private Facebook mFacebook;
	private CheckBox mFacebookCb;
	private ProgressDialog mProgress;
	private ListView list;
	ArrayList<String> listdata = new ArrayList<String>();
	private Handler mRunOnUi = new Handler();
	
	private static final String APP_ID = "259732484519156";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.post);

		
		final EditText reviewEdit = (EditText) findViewById(R.id.revieew);
		mFacebookCb				  = (CheckBox) findViewById(R.id.cb_facebook);
		
		mProgress	= new ProgressDialog(this);
		
		mFacebook 	= new Facebook(APP_ID);
		
		SessionStore.restore(mFacebook, this);

		if (mFacebook.isSessionValid()) {
			mFacebookCb.setChecked(true);
				
			String name = SessionStore.getName(this);
			name		= (name.equals("")) ? "Unknown" : name;
				
			mFacebookCb.setText("  Facebook  (" + name + ")");
		}
		
		((Button) findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String review = reviewEdit.getText().toString();

				if (review.equals("")) return;

				if (mFacebookCb.isChecked()) postToFacebook(review);
			}
		});


		((Button) findViewById(R.id.button2)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {



				fetchMyFacebookStatuses();


			}
		});


	}



private void fetchMyFacebookStatuses()
	{
		try {
			String response = mFacebook.request("me");

			response = mFacebook.request("me/feed");

			Log.d("Tests", "got response: " + response);
			if (response == null || response.equals("") ||
					response.equals("false")) {
				Log.v("Error", response.toString());
			}
			else
			{



				JSONObject jsonObject = new JSONObject(response);
				try {
					JSONArray array = jsonObject.getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = (JSONObject) array.get(i);
						        listdata.add((String)object.get("message"));
						//Log.d( "Message id = "+object.get("id"),"Message = "+object.get("message"));


					}
				}
				catch (Exception e)
				{
					Log.i("Error in posts:",e.getMessage());



				}

			}
		}catch(Exception ex1)
		{
			Log.i("Error in posts:",ex1.getMessage());
		}
		list = (ListView) findViewById(R.id.list1);
		ArrayAdapter adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				listdata);
		list.setAdapter(adapter);

	}


	private void postToFacebook(String review) {	
		mProgress.setMessage("Posting ...");
		mProgress.show();
		
		AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(mFacebook);
		
		Bundle params = new Bundle();
    		
		params.putString("message", review);
		//params.putString("name", "Dexter");
		//params.putString("caption", "londatiga.net");
		//params.putString("link", "http://www.londatiga.net");
		//params.putString("description", "Dexter, seven years old dachshund who loves to catch cats, eat carrot and krupuk");
		//params.putString("picture", "http://twitpic.com/show/thumb/6hqd44");*/
		
		mAsyncFbRunner.request("me/feed", params, "POST", new WallPostListener());
	}

	private final class WallPostListener extends BaseRequestListener {
        public void onComplete(final String response) {
        	mRunOnUi.post(new Runnable() {
        		@Override
        		public void run() {
        			mProgress.cancel();
        			
        			Toast.makeText(TestPost.this, "Posted to Facebook", Toast.LENGTH_SHORT).show();
        		}
        	});
        }
    }
}
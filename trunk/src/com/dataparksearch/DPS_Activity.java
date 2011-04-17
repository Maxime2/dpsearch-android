package com.dataparksearch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dataparksearch.TCPClient;

import java.io.BufferedWriter;

public class DPS_Activity extends Activity {
	
    static final private int BACK_ID = Menu.FIRST;
    static final private int CLEAR_ID = Menu.FIRST + 1;

    EditText mEditor;
    HorizontalScrollView mView;
    LinearLayout browserFrame;
    ProgressBar mProgressBar;
    ImageView mSearchButton;
    
    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.main);

        // Find the text editor view inside the layout, because we
        // want to do various programmatic things with it.
        mEditor = (EditText) findViewById(R.id.editor);
        mView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView1);
        browserFrame = (LinearLayout) findViewById(R.id.linearLayout1);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        mSearchButton = (ImageView) findViewById(R.id.searchButton);
        // Hook up button presses to the appropriate event handler.
 //       ((Button) findViewById(R.id.search)).setOnClickListener(mSearchListener);
        mSearchButton.setOnClickListener(mSearchListener);
        mSearchButton.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
 
    	
    }

  //this is called when the screen rotates.
 // (onCreate is no longer called when screen rotates due to manifest, see: android:configChanges)
 @Override
 public void onConfigurationChanged(Configuration newConfig)
 {
     super.onConfigurationChanged(newConfig);
//     setContentView(R.layout.main);     
     
 //    InitializeUI();
 }
 
    /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Called when your activity's options menu needs to be created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // We are going to create two menus. Note that we assign them
        // unique integer IDs, labels from our string resources, and
        // given them shortcuts.
  //      menu.add(0, BACK_ID, 0, R.string.back).setShortcut('0', 'b');
   //     menu.add(0, CLEAR_ID, 0, R.string.clear).setShortcut('1', 'c');

        return true;
    }

    /**
     * Called right before your activity's option menu is displayed.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Before showing the menu, we need to decide whether the clear
        // item is enabled depending on whether there is text to clear.
    //    menu.findItem(CLEAR_ID).setVisible(mEditor.getText().length() > 0);

        return true;
    }
    

    /**
     * Called when a menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case BACK_ID:
            finish();
            return true;
        case CLEAR_ID:
   //         mEditor.setText("");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

 

    /**
     * A call-back for when the user presses the search button.
     */

    
    OnClickListener mSearchListener = new OnClickListener() {
        public void onClick(View v) {
        	
        	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
         	imm.hideSoftInputFromWindow(mEditor.getWindowToken(), 0);
             	
         	new asyncTaskUpdateProgress().execute();             
	       
           // mEditor.setText(responseText);

        }
    };
    
    
    public class asyncTaskUpdateProgress extends AsyncTask<Void, Integer, Void> {

    	int progress;
    	String SERP;
    	JSONObject response;
 //   	JSONObject jsonObject = new JSONObject();
    	
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		progress = 0;
    		
         	mSearchButton.setVisibility(View.GONE);
         	mProgressBar.setVisibility(View.VISIBLE);
         	
         	browserFrame.removeAllViews();
         	mView.scrollTo(0,0);  
    	}
    	
    	@Override
    	protected void onPostExecute(Void result) {
    		// TODO Auto-generated method stub
    		//   buttonStart.setClickable(true);
    		String rQuery= "", rTitle= "", rURL= "", rDate= "", rContent = ""; 
 
	        mProgressBar.setVisibility(View.GONE);
	        mSearchButton.setVisibility(View.VISIBLE);
	        
	        WindowManager w = getWindowManager(); 
	        Display d = w.getDefaultDisplay(); 
	        int width = d.getWidth(); 
	        int height = d.getHeight(); 
	        
    		try {
    			// Drill into the JSON response to find the content body
    			
    			JSONObject Data = response.getJSONObject("responseData");
    			JSONArray results = Data.getJSONArray("results");
    			int len = results.length();
    			
    			for(int i = 0; i < len; ++i) {

    				JSONObject sResult = results.getJSONObject(i);
    				Log.d("TCP", "C: Done.");
    				//    return Data.getString("query");
    				rQuery = Data.getString("query");
    				rTitle = sResult.getString("title");
    				rURL = sResult.getString("url");
    				rDate = sResult.getString("date");
    				rContent = sResult.getString("content");

    				WebView mWebView = new WebView(getApplicationContext());
    				mWebView.setLayoutParams(new LayoutParams(width, LayoutParams.FILL_PARENT));

    				//  webView = new MyWebView( getApplicationContext(), Settings.this, AsyncWebConnect.this);
    				// Loads html-string into webview
    				String dString = "<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><body><h3><a href=\""+rURL+"\">"+rTitle+"</a></h3><p>"+rContent+"</p><p>"+rDate+"</p></body></html>";
    				mWebView.loadData(dString, "text/html", "UTF-8");
    				browserFrame.addView(mWebView);
    				Log.d("View", "added view: "+i);
    			}

    		} catch (JSONException e) {
    			rQuery = ("Problem parsing API response" + e);

    		}
	

    	}

    	@Override
    	protected void onProgressUpdate(Integer... values) {
    		// TODO Auto-generated method stub
    		//    mprogressBar.setProgress(values[0]);
    	}

    	@Override
    	protected Void doInBackground(Void... arg0) {
    		// TODO Auto-generated method stub
    		/*	      while(progress<100){
	       progress++;
	       publishProgress(progress);
	       SystemClock.sleep(100); 	
	      }  
    		 */	      
    		TCPClient cli = new TCPClient();
    		cli.message = mEditor.getText().toString();
    		cli.hostname = "inet-sochi.ru";
    		SERP = cli.run();

    		try{
    			response = new JSONObject(SERP);
    		} catch (JSONException e) {
    //			rQuery = ("Problem parsing API response" + e);

    		}
    		return null;
    	}

    }
    
    
}
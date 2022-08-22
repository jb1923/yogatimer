package com.example.yogatimer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends Activity {
    private int pauseDelay ; // pause between loops
    private int loopTimer;// seconds counter for loop timer in t1View
    private int relaxationTimer = 0;// seconds counter for t2View relaxation timer
    private int loop1; // loop1 time = 45"
    private int loop2; // loop2 time = 60"
    private int loop0 = loop1; // loop0 =loop time in seconds ie loop1 or loop2 ie 45" or 60"
    private boolean runningT1;
    private boolean runningT2;
    private EditText editPause ;
    private TextView labelPause;
    private EditText editLoop1 ;
    private TextView labelLoop1;
    private EditText editLoop2 ;
    private TextView labelLoop2;
    private TextView t1View ;
    private TextView t2View ;
    private Button setupButton;
    private Button loop1Button;
    private Button loop2Button;
    private Button startT2Button;
    int colWhite=0xFFFFFFFF;
    int colGreen=0xFF00FF00;
    int buttonOffColor=colWhite;
    int buttonOnColor=0xFFFFFF00;
    int pauseColor=0xFFFF0000;
    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_SYSTEM, 100);
    public static final String MyPREFERENCES = "MyPrefs" ;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        loadData();// loads previous setup values between sessions
        // associate java variables with buttons/views from xml files
        setupButton = (Button) findViewById(R.id.setupButton);
        editPause = (EditText) findViewById(R.id.editPause);
        labelPause = (TextView) findViewById(R.id.labelPause);
        editLoop1 = (EditText) findViewById(R.id.editLoop1);
        labelLoop1 = (TextView) findViewById(R.id.labelLoop1);
        editLoop2 = (EditText) findViewById(R.id.editLoop2);
        labelLoop2 = (TextView) findViewById(R.id.labelLoop2);
        loop1Button = (Button) findViewById(R.id.loop1Button);
        loop2Button = (Button) findViewById(R.id.loop2Button);
        startT2Button = (Button) findViewById(R.id.startT2Button);
        t1View = (TextView)findViewById(R.id.t1TextView);
        t2View = (TextView)findViewById(R.id.t2TextView);
        // hide setup menu
        editPause.setVisibility(View.INVISIBLE);
        labelPause.setVisibility(View.INVISIBLE);
        editLoop1.setVisibility(View.INVISIBLE);
        labelLoop1.setVisibility(View.INVISIBLE);
        editLoop2.setVisibility(View.INVISIBLE);
        labelLoop2.setVisibility(View.INVISIBLE);
        // label buttons
        loop1Button.setText("loop " + Integer.toString(loop1) );//set the text on button
        loop2Button.setText("loop " + Integer.toString(loop2) );//set the text on button
        setupButton.setText("setup " );//set the text on button

        running_loopTimer();
    }

    public void onClickStartT2(View view)
    {
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,100);
        runningT1 = false;
        runningT2 = true;
		loopTimer = 0;
        startT2Button.setBackgroundColor(buttonOnColor);
        loop1Button.setBackgroundColor(buttonOffColor);
        loop2Button.setBackgroundColor(buttonOffColor);
    }

    public void onClickStop(View view)
    {
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,100);
        runningT1 = false;
        runningT2 = false;
        startT2Button.setBackgroundColor(buttonOffColor);
        loop1Button.setBackgroundColor(buttonOffColor);
        loop2Button.setBackgroundColor(buttonOffColor);
    }

    public void saveData() {
        // save set up values for next sessions
        SharedPreferences sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();;
        editor.putInt("loop1", loop1);
        editor.putInt("loop2", loop2);
        editor.putInt("pauseDelay", pauseDelay);
        editor.commit();
        editor.apply();
    }
    public void loadData() {
         // save set up values between sessions
        SharedPreferences sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        loop1=sharedPref.getInt("loop1", 45);// default to 45
        loop2=sharedPref.getInt("loop2", 60);// default to 60
        pauseDelay=sharedPref.getInt("pauseDelay", 7);// default to 7
        loopTimer = -pauseDelay;
    }

    public void onClickSetup(View view)
    {
         if( editLoop1.getVisibility()==View.VISIBLE)
        {   // save button has been clicked:- update loop1,loop2,pauseDelay, change button label to SETUP
            // convert to integer - get String - remove all but digits
            String pauseDelayStr = editPause.getText().toString().replaceAll("[^0-9]+",  "");
            if (pauseDelayStr.equals("")) pauseDelayStr ="7";
            pauseDelay = Integer.parseInt(pauseDelayStr);// only numbers
            //pauseDelay = Math.abs(pauseDelay);// only positive number
            loopTimer = -pauseDelay;
            loop1 = Integer.parseInt(editLoop1.getText().toString()
                    .replaceAll("[^0-9]+",  ""));// only numbers
            loop2 = Integer.parseInt(editLoop2.getText().toString()
                    .replaceAll("[^0-9]+",  ""));// only numbers
            saveData();
           // Close keyboard
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editPause.getWindowToken(), 0);

            //editPause.setImeOptions(EditorInfo.IME_ACTION_DONE);
            editPause.setVisibility(View.INVISIBLE);
            labelPause.setVisibility(View.INVISIBLE);
            editLoop1.setVisibility(View.INVISIBLE);
            labelLoop1.setVisibility(View.INVISIBLE);
            editLoop2.setVisibility(View.INVISIBLE);
            labelLoop2.setVisibility(View.INVISIBLE);
            setupButton.setText("setup " );//set the text on button
            loop1Button.setText("loop " + Integer.toString(loop1) );//set the text on button
            loop2Button.setText("loop " + Integer.toString(loop2) );//set the text on button
        }else
        { // setup button has been clicked:- change button label to SAVE, open edit texts
            setupButton.setText("Save  " );//set the text on button
            editLoop1.setText(" "+Integer.toString(loop1)+" ");// add spaces for easy selectin
            editLoop2.setText(" "+Integer.toString(loop2)+" ");// add spaces for easy selectin
            editPause.setText(" "+Integer.toString(pauseDelay)+" ");// add spaces for easy selectin
            labelPause.setVisibility(View.VISIBLE);
            editPause.setVisibility(View.VISIBLE);
            editLoop1.setVisibility(View.VISIBLE);
            labelLoop1.setVisibility(View.VISIBLE);
            editLoop2.setVisibility(View.VISIBLE);
            labelLoop2.setVisibility(View.VISIBLE);
            // Open keyboard
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editPause, InputMethodManager.SHOW_FORCED);
            editPause.setSelection(editPause.getText().length());
        }
    }

    public void onClickLoop1(View view)
    {
		toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
        runningT1 = true;
        runningT2 = true;
		loop0 = loop1;
        loopTimer = -pauseDelay;
        loop2Button.setBackgroundColor(buttonOffColor);
        loop1Button.setBackgroundColor(buttonOnColor) ;
        startT2Button.setBackgroundColor(buttonOffColor);
    }

    public void onClickLoop2(View view)
    {
        //Button loop2Button = (Button) findViewById(R.id.loop2Button);
		toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
        runningT1 = true;
        runningT2 = true;
		loop0 = loop2;
        loopTimer = -pauseDelay;
        loop1Button.setBackgroundColor(buttonOffColor);
        loop2Button.setBackgroundColor(buttonOnColor);
        startT2Button.setBackgroundColor(buttonOffColor);
    }

    public void onClickClear(View view)
    {
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,100);
		loopTimer = 0;
		relaxationTimer = -pauseDelay;
        t2View.setTextColor(pauseColor); 	}

     private void running_loopTimer()
    {
		final Handler handle1 = new Handler(Looper.getMainLooper());
		handle1.post(new Runnable() {
			@Override
			public void run()
            {
			// update loopTimer on screen loop0 = 45 or 60 loop
				int secs = loopTimer % 60;
				String time_t1 = String .format(Locale.getDefault(), "%02d", Math.abs(secs));
				t1View.setText(time_t1);
                if (runningT1) 
                {
                    if ( loopTimer < 0 ) {
                        t1View.setTextColor(pauseColor);}
                     else {  t1View.setTextColor(colWhite);}
					if ( loopTimer == 0 ) {
						toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);

					}
                    // When we reach loop count set timer to count down for pauseDelay seconds
					if ( loopTimer == loop0 ) {
						loopTimer = -pauseDelay; // pauseDelay is always +ive
						toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);  
					}
					loopTimer++; // update loopTimer if not stopped
                }
                else { // runningT1 = false. T1 stopped
					t1View.setText("**");
				} // end of if (runningT1) 

			// update relaxationTimer on screen
                int	hrs = relaxationTimer / 3600;
                int	mins =  (relaxationTimer % 3600) / 60;
				secs = relaxationTimer % 60;
                String time_t2;
                time_t2 = String .format("%02d:%02d:%02d", hrs,mins, Math.abs(secs));
				t2View.setText(time_t2);
                if  (runningT2) 
                {
                    if ( relaxationTimer < 0 ) {
                        t2View.setTextColor(pauseColor);}
                    else {  t2View.setTextColor(colWhite);}

					if  (runningT1 == false) { //  if t1 is stopped
                        if  ((relaxationTimer % 600) ==0 )// beep at 0, 10, 20, 30 mins
                        { toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);}
  					}
					relaxationTimer++; // update loopTimer as T2 running
                } // end of if (runningT2)
                handle1.postDelayed(this, 1000); // 1" time delay
            }
        }); // ) = end of handle1.post(new Runnable()
    }
} // end of MainActivity

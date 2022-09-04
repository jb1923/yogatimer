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
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//import androidx.appcompat.widget.AppCompatEditText;

import java.util.Locale;

class CustomEditText extends EditText
{
    public CustomEditText(Context context) {
        super(context);
    }
    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public int GetInt( int default1) {
        // gets integer from CustomEditText - if no valid int, returns default1
        // first remove all chars  except numbers
        String edTxtStr = getText().toString().replaceAll("[^0-9]+",  "");
        // if edTxtStr ="" return default1 else return user value as integer
        return  edTxtStr.equals("")? default1: Integer.parseInt(edTxtStr);
    }
    public void SetInt( int value )
    {//displays  integer value in CustomEditText
         setText(" "+Integer.toString(value)+" ");// add spaces for easy selectin
    }
}// end of CustomEditText class

public class MainActivity extends Activity {
    private int pauseDelay; // pause between loops
    private int loopTimer;// seconds counter for loop timer in t1View
    private int relaxationTimer = 0;// seconds counter for t2View relaxation timer
    private int loop1; // loop1 time = 45"
    private int loop2; // loop2 time = 60"
    private int loop0 = loop1; // loop0 =loop time in seconds ie loop1 or loop2 ie 45" or 60"
    private boolean runningT1;
    private boolean runningT2;
    private CustomEditText editPause;
    private TextView labelPause;
    private CustomEditText editLoop1;
    private TextView labelLoop1;
    private CustomEditText editLoop2;
    private TextView labelLoop2;
    private TextView t1View;
    private TextView t2View;
    private Button setupButton;
    private Button loop1Button;
    private Button loop2Button;
    private Button startT2Button;
    int colWhite = 0xFFFFFFFF;
    int buttonOffColor = colWhite;
    int buttonOnColor = 0xFFFFFF00;
    int pauseColor = 0xFFFF0000;
    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_SYSTEM, 100);
    public static final String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        loadData();// loads previous setup values between sessions
 // associate java variables with buttons/views from xml files
        setupButton = (Button) findViewById(R.id.setupButton);
        editPause = (CustomEditText) findViewById(R.id.editPause);
        labelPause = (TextView) findViewById(R.id.labelPause);
        editLoop1 = (CustomEditText) findViewById(R.id.editLoop1);
        labelLoop1 = (TextView) findViewById(R.id.labelLoop1);
        editLoop2 = (CustomEditText) findViewById(R.id.editLoop2);
        labelLoop2 = (TextView) findViewById(R.id.labelLoop2);
        loop1Button = (Button) findViewById(R.id.loop1Button);
        loop2Button = (Button) findViewById(R.id.loop2Button);
        startT2Button = (Button) findViewById(R.id.startT2Button);
        t1View = (TextView) findViewById(R.id.t1View);
        t2View = (TextView) findViewById(R.id.t2View);
 // hide setup menu
        editPause.setVisibility(View.INVISIBLE);
        labelPause.setVisibility(View.INVISIBLE);
        editLoop1.setVisibility(View.INVISIBLE);
        labelLoop1.setVisibility(View.INVISIBLE);
        editLoop2.setVisibility(View.INVISIBLE);
        labelLoop2.setVisibility(View.INVISIBLE);
 // label buttons
        loop1Button.setText("loop " + Integer.toString(loop1));//set the text on button ie loop45
        loop2Button.setText("loop " + Integer.toString(loop2));//set the text on button ie. loop60
        setupButton.setText("setup ");//set the text on button
// run app timer
        running_loopTimer();
    }

    public void onClickStartT2(View view) {
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 100);
        runningT1 = false;
        runningT2 = true;
        loopTimer = 0;
        startT2Button.setBackgroundColor(buttonOnColor);
        loop1Button.setBackgroundColor(buttonOffColor);
        loop2Button.setBackgroundColor(buttonOffColor);
    }

    public void onClickStop(View view) {
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 100);
        runningT1 = false;
        runningT2 = false;
        startT2Button.setBackgroundColor(buttonOffColor);
        loop1Button.setBackgroundColor(buttonOffColor);
        loop2Button.setBackgroundColor(buttonOffColor);
    }

    public void saveData() {
        // save set up values for next session
        SharedPreferences sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        ;
        editor.putInt("loop1", loop1); // save integer loop1 for next session
        editor.putInt("loop2", loop2); // save integer loop2 for next session
        editor.putInt("pauseDelay", pauseDelay); // save pauseDelay for next session
        editor.commit();
        editor.apply();
    }

    public void loadData() {
        // save set up values between sessions
        SharedPreferences sharedPref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        loop1 = sharedPref.getInt("loop1", 45);// get loop1 from previous session
        loop2 = sharedPref.getInt("loop2", 60);// get loop2 from previous session
        pauseDelay = sharedPref.getInt("pauseDelay", 7);//get pauseDelay from previous session
        loopTimer = -pauseDelay;
    }

    public void onClickSetup(View view) {
        if (editLoop1.getVisibility() == View.VISIBLE) {   // save button has been clicked:- update loop1,loop2,pauseDelay, change button label to SETUP
            pauseDelay = editPause.GetInt(7);// CustomEditText.GetInt
            loopTimer = -pauseDelay;// need -ive number for countdown
            loop1 = editLoop1.GetInt(45);// CustomEditText.GetInt
            loop2 = editLoop2.GetInt(60);// CustomEditText.GetInt
       //     loop0 = loop1;
            saveData();
            // Close keyboard
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editPause.getWindowToken(), 0);

            //editPause.setImeOptions(EditorInfo.IME_ACTION_DONE);
            editPause.setVisibility(View.INVISIBLE);
            labelPause.setVisibility(View.INVISIBLE);
            editLoop1.setVisibility(View.INVISIBLE);
            labelLoop1.setVisibility(View.INVISIBLE);
            editLoop2.setVisibility(View.INVISIBLE);
            labelLoop2.setVisibility(View.INVISIBLE);
            setupButton.setText("setup ");//set the text on button
            loop1Button.setText("loop " + Integer.toString(loop1));//set the text on button
            loop2Button.setText("loop " + Integer.toString(loop2));//set the text on button
        } else { // setup button has been clicked:- change button label to SAVE, open edit texts
            setupButton.setText("Save  ");//set the text on button
            editLoop1.SetInt(loop1);// CustomEditText.SetInt
            editLoop2.SetInt(loop2);// CustomEditText.SetInt
            editPause.SetInt(pauseDelay);// CustomEditText.SetInt
            labelPause.setVisibility(View.VISIBLE);
            editPause.setVisibility(View.VISIBLE);
            editLoop1.setVisibility(View.VISIBLE);
            labelLoop1.setVisibility(View.VISIBLE);
            editLoop2.setVisibility(View.VISIBLE);
            labelLoop2.setVisibility(View.VISIBLE);
            // Open keyboard
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editPause, InputMethodManager.SHOW_FORCED);
            editPause.setSelection(editPause.getText().length());
        }
    }

    public void onClickLoop1(View view) {
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
        runningT1 = true;
        runningT2 = true;
        loop0 = loop1;
        loopTimer = -pauseDelay;
        loop2Button.setBackgroundColor(buttonOffColor);
        loop1Button.setBackgroundColor(buttonOnColor);
        startT2Button.setBackgroundColor(buttonOffColor);
    }

    public void onClickLoop2(View view) {
        //Button loop2Button = (Button) findViewById(R.id.loop2Button);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
        runningT1 = true;
        runningT2 = true;
        loop0 = loop2;
        loopTimer = -pauseDelay;
        loop1Button.setBackgroundColor(buttonOffColor);
        loop2Button.setBackgroundColor(buttonOnColor);
        startT2Button.setBackgroundColor(buttonOffColor);
    }

    public void onClickClear(View view) {
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 100);
        loopTimer = 0;
        relaxationTimer = -pauseDelay;
        t2View.setTextColor(pauseColor);
    }

    private void running_loopTimer() {
        final Handler handle1 = new Handler(Looper.getMainLooper());
        handle1.post(new Runnable() {
            @Override
            public void run() {
                // update loopTimer on screen loop0 = 45 or 60 loop
                int secs = loopTimer % 60;
                int mins = loopTimer / 60;
                int hrs;
                String time_t1;
                if (loop0 > 600)
                {
                    t1View.setTextSize(140);
                    time_t1 = String.format(Locale.getDefault(), "%02d:%02d", mins, Math.abs(secs));
                } else if (loop0 > 60)
                {
                    t1View.setTextSize(160);
                    time_t1 = String.format(Locale.getDefault(), "%01d:%02d", mins, Math.abs(secs));
                } else
                {
                    t1View.setTextSize(200);
                    time_t1 = String.format(Locale.getDefault(), "%02d", Math.abs(secs));
                }
                t1View.setText(time_t1);
                if (runningT1)
                {   // set timer1 red for pauseDelay -7" then white for loop 45"
                    t1View.setTextColor((loopTimer < 0)?pauseColor:colWhite);
                    if (loopTimer == 0) toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                        // When we reach loop count set timer to count down for pauseDelay seconds
                    if (loopTimer == loop0) { // reached end of loop ie. 45"
                        loopTimer = -pauseDelay; // timer reset to -7" pauseDelay
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                    }
                    loopTimer++; // update loopTimer if not stopped
                } else
                { // if runningT1 = false. T1 stopped
                    t1View.setText("**");
                } // end of if (runningT1)

                // update relaxationTimer on screen
                hrs = relaxationTimer / 3600;
                mins = (relaxationTimer % 3600) / 60;
                secs = relaxationTimer % 60;
                String time_t2;
                time_t2 = String.format("%02d:%02d:%02d", hrs, mins, Math.abs(secs));
                t2View.setText(time_t2);
                if (runningT2)
                {   // set timer2 red for pauseDelay -7" then white
                    t2View.setTextColor((relaxationTimer < 0)?pauseColor:colWhite);
                    //  if t1 is stopped, then every 600" beep
                    if ((runningT1 == false) && ((relaxationTimer % 600) == 0))
                            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                     relaxationTimer++; // update loopTimer as T});2 running
                } // end of if (runningT2)
                handle1.postDelayed(this, 1000); // 1" time delay
            }  //= end of public void run
        });// end of handle1.post(new Runnable() should be  });//
    } //  end of loop timer
}// end of MainActivity

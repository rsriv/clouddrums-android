package com.clouddrums.clouddrums2;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public int hatId, kickId, snareId, ohatId, crashId;
    public int playCount = 0, valid = 0;
    public Thread t;
    final SoundPool hatSound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    final SoundPool kickSound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    final SoundPool snareSound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    final SoundPool ohatSound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    final SoundPool crashSound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    public int open = 0;
    private static final String TAG = "MainActivity";
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static int hitOrNo(int n) {
        int i = randInt(0, 9);
        if (i < n) return 1;
        else return 0;
    }

    public static boolean isNumeric(String str)
    {
        int bufferInt;
        try
        {
            bufferInt = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        if (bufferInt>0 && bufferInt<=10000) return true;
        else return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MainActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        setContentView(R.layout.activity_main);

        //Set volume control
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStop (){
        super.onStop();
        valid = 0;
        playCount = 0;
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setText(R.string.enter);
    }

    public void play(View view) {
        Vibrator vibe = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE) ;
        Button button1 = (Button) findViewById(R.id.button1);
        playCount++;

        //Stop
        if (playCount % 2 == 0) {
            button1.setText(R.string.enter);
            valid = 0;
            playCount = 0;
        }
        //Start
        else {
            valid = 1;
        }

        //Get arguments from form
        EditText bpmField1 = (EditText) findViewById(R.id.bpmField);
        String bpm1 = bpmField1.getText().toString();
        EditText loopField1 = (EditText) findViewById(R.id.loopField);
        String loopLength1 = loopField1.getText().toString();

        if (isNumeric(bpm1) && isNumeric(loopLength1)) {
            TextView badBpm = (TextView) findViewById(R.id.invalidTempo);
            badBpm.setText("");
            TextView badField = (TextView) findViewById(R.id.invalidLoop);
            badField.setText("");

            //Set kit type
            RadioGroup radioKitGroup = (RadioGroup) findViewById(R.id.kitType);
            int selectedId = radioKitGroup.getCheckedRadioButtonId();
            RadioButton radioKitButton = (RadioButton) findViewById(selectedId);
            String kitType = radioKitButton.getText().toString();

            if (kitType.equals("Rock Kit")) {
                if (randInt(0, 1) == 1)
                    hatId = hatSound.load(this, R.raw.rockhat, 1);
                else
                    hatId = hatSound.load(this, R.raw.rockride, 1);
                    kickId = kickSound.load(this, R.raw.rockkick, 1);
                    snareId = snareSound.load(this, R.raw.rocksnare, 1);
                    ohatId = ohatSound.load(this, R.raw.rockohat, 1);
                    crashId = crashSound.load(this, R.raw.rockcrash, 1);
            }

            if (kitType.equals("Bongos"))

            {
                hatId = hatSound.load(this, R.raw.bongohigh, 1);
                kickId = kickSound.load(this, R.raw.bongolow, 1);
                snareId = snareSound.load(this, R.raw.bongomid, 1);
            }

            if (kitType.equals("Hip Hop Kit")) {
                hatId = hatSound.load(this, R.raw.hiphophat, 1);
                kickId = kickSound.load(this, R.raw.hiphopkick, 1);
                snareId = snareSound.load(this, R.raw.hiphopsnare, 1);
                ohatId = ohatSound.load(this, R.raw.hiphopohat, 1);
            }

            t = new Thread(new Runnable() {
                public void run() {
                    //compute necessary data
                    EditText bpmField = (EditText) findViewById(R.id.bpmField);
                    long bpm = (long) Integer.parseInt(bpmField.getText().toString());
                    EditText loopField = (EditText) findViewById(R.id.loopField);
                    int loopLength = Integer.parseInt(loopField.getText().toString());
                    //float beatTimeSec = 15f / bpm; //beattime in seconds -> UPDATE: waste of memory
                    long beatTime = (long) (15000f/bpm); //beattime in milliseconds
                    int num16ths = loopLength * 16; //number of 16th notes per loopLength bars
                    int[] kick, snare, hiHat, oHat, crash;
                    kick = new int[num16ths];
                    snare = new int[num16ths];
                    hiHat = new int[num16ths];
                    oHat = new int[num16ths];
                    crash = new int[num16ths];
                    int beat = 1;

                    //piano roll generator
                    while (beat <= num16ths)

                    {
                        //kick
                        if (beat % 2 == 0) {
                            kick[beat - 1] = hitOrNo(2);
                        }
                        if ((beat + 1) % 4 == 0) {
                            kick[beat - 1] = hitOrNo(3);
                        }
                        if ((beat - 1) % 4 == 0) {
                            kick[beat - 1] = hitOrNo(1);
                        }
                        if ((beat - 1) % 8 == 0) {
                            kick[beat - 1] = 1;
                        }

                        //snare
                        if ((beat - 1) % 8 == 0) {
                            snare[beat - 1] = hitOrNo(1);
                        }
                        if (beat % 2 == 0 || (beat + 1) % 4 == 0) {
                            snare[beat - 1] = hitOrNo(2);
                        }
                        if ((beat + 3) % 8 == 0) {
                            snare[beat - 1] = 1;
                        }

                        //hiHat
                        if ((beat - 1) % 2 == 0) {
                            hiHat[beat - 1] = 1;
                        }
                        if ((beat + 2) % 4 == 0) {
                            hiHat[beat - 1] = hitOrNo(3);
                        }
                        if (beat % 4 == 0) {
                            hiHat[beat - 1] = hitOrNo(2);
                        }

                        //crash
                        if ((beat - 1) % 16 == 0) {
                            crash[beat - 1] = hitOrNo(5);
                        }

                        //oHat
                        if ((beat - 1) % 8 == 0) {
                            oHat[beat - 1] = hitOrNo(3);
                        }
                        beat += 1;
                    }

                    //player module
                    valid = 1;
                    int kickPlay;
                    int ohatPlay;
                    int crashPlay;
                    while (valid == 1) {
                        beat = 1;

                        while (beat <= (num16ths + 1) && valid == 1) {
                            kickPlay = 0;
                            ohatPlay = 0;
                            crashPlay = 0;
                            if (beat == (num16ths + 1)) {
                                break;
                            }

                            if (kick[beat - 1] == 1) {
                                kickPlay = 1;
                                kickSound.play(kickId, 1, 1, 0, 0, 1);
                            }

                            //play snare
                            if (snare[beat - 1] == 1 && kickPlay == 0) {
                                snareSound.play(snareId, 1, 1, 0, 0, 1);
                            }

                            //play crash
                            if (crash[beat - 1] == 1) {
                                crashSound.play(crashId, 1, 1, 0, 0, 1);
                            }
                            //play oHat
                            if (oHat[beat - 1] == 1) {
                                ohatSound.play(ohatId, 1, 1, 0, 0, 1);
                                ohatPlay = 1;
                            }

                            //play hiHat
                            if (hiHat[beat - 1] == 1 && ohatPlay == 0) {
                                hatSound.play(hatId, 1, 1, 0, 0, 1);
                            }

                            try {
                                //Wait beatTime seconds for next beat
                                Thread.sleep(beatTime);
                            } catch (InterruptedException e) {
                                Log.e(TAG,"thread sleep failed");
                            }
                            beat += 1;
                        }

                        if (playCount % 2 == 0) {
                            valid = 0;
                            playCount = 0;
                        }
                    }
                }
            });

            //Change button text on play and start player module
            if (playCount % 2 != 0) {
                button1.setText(R.string.stop);
                t.start();
                valid = 1;
            }
            //Change button text on stop
            else {
                button1.setText(R.string.enter);
                valid = 0;
                playCount = 0;
            }

        }

        //Invalid tempo but valid loop length
        else if (!isNumeric(bpm1) && isNumeric(loopLength1) ){
            TextView badBpm = (TextView) findViewById(R.id.invalidTempo);
            badBpm.setText(R.string.badTempo);
            TextView badField = (TextView) findViewById(R.id.invalidLoop);
            badField.setText("");
            vibe.vibrate(50);
        }

        //Invalid loop length but valid tempo
        else if (isNumeric(bpm1) && !isNumeric(loopLength1) ){
            TextView badBpm = (TextView) findViewById(R.id.invalidTempo);
            badBpm.setText("");
            TextView badField = (TextView) findViewById(R.id.invalidLoop);
            badField.setText(R.string.badLoop);
            vibe.vibrate(50);
        }

        //Invalid tempo and loop length
        else {
            TextView badBpm = (TextView) findViewById(R.id.invalidTempo);
            badBpm.setText(R.string.badTempo);
            TextView badField = (TextView) findViewById(R.id.invalidLoop);
            badField.setText(R.string.badLoop);
            vibe.vibrate(50);
        }

    }

}




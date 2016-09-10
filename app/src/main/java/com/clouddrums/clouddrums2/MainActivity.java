package com.clouddrums.clouddrums2;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public int hatId, kickId, snareId;
    public int playCount = 0, valid = 0;
    public Thread t;
    final SoundPool sp1 = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    final SoundPool sp2 = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    final SoundPool sp3 = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    public int open = 0;
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
        int d;
        try
        {
            d = Integer.parseInt(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        if (d>0 && d<=10000) return true;
        else return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MainActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        setContentView(R.layout.activity_main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }
    @Override
    protected void onStop (){
        super.onStop();
        valid = 0;
        playCount = 0;
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setText("Play");
    }

    public void play(View view) {
        Vibrator vibe = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE) ;
        Button button1 = (Button) findViewById(R.id.button1);
        playCount++;
        if (playCount % 2 == 0) {
            button1.setText("Play");
            valid = 0;
            playCount = 0;
        } else {
            valid = 1;
        }

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
                    hatId = sp1.load(this, R.raw.rockhat, 1);
                else
                    hatId = sp1.load(this, R.raw.rockride, 1);
                kickId = sp2.load(this, R.raw.rockkick, 1);
                snareId = sp3.load(this, R.raw.rocksnare, 1);
            }

            if (kitType.equals("Bongos"))

            {
                hatId = sp1.load(this, R.raw.bongohigh, 1);
                kickId = sp2.load(this, R.raw.bongolow, 1);
                snareId = sp3.load(this, R.raw.bongomid, 1);
            }

            if (kitType.equals("Hip Hop Kit")) {
                hatId = sp1.load(this, R.raw.hiphophat, 1);
                kickId = sp2.load(this, R.raw.hiphopkick, 1);
                snareId = sp3.load(this, R.raw.hiphopsnare, 1);
            }

            t = new Thread(new Runnable() {
                public void run() {
                    EditText bpmField = (EditText) findViewById(R.id.bpmField);
                    long bpm = (long) Integer.parseInt(bpmField.getText().toString());
                    EditText loopField = (EditText) findViewById(R.id.loopField);
                    int loopLength = Integer.parseInt(loopField.getText().toString());
                    float beatTime1 = 15f / bpm;
                    long beatTime = (long) (beatTime1 * 1000.00000f);
                    int num16ths = loopLength * 16;
                    int[] kick, snare, hiHat;
                    kick = new int[num16ths];
                    snare = new int[num16ths];
                    hiHat = new int[num16ths];
                    int beat = 1;
                    // generate piano roll
                    while (beat <= num16ths)

                    {
                        //kick
                        if (beat % 4 == 0 || (beat - 2) % 4 == 0) {
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
                            snare[beat - 1] = hitOrNo(10);
                        }

                        //hiHat
                        if ((beat - 1) % 2 == 0) {
                            hiHat[beat - 1] = hitOrNo(10);
                        }
                        if ((beat + 2) % 4 == 0) {
                            hiHat[beat - 1] = hitOrNo(3);
                        }
                        if (beat % 4 == 0) {
                            hiHat[beat - 1] = hitOrNo(2);
                        }

                        beat += 1;

                    }

                    valid = 1;
                    int kickPlay;
                    int wholeBeat = 1;
                    while (valid == 1) {
                        beat = 1;

                        while (beat <= (num16ths + 1) && valid == 1) {
                            kickPlay = 0;
                            if (beat == (num16ths + 1)) {
                                break;
                            }

                            if (kick[beat - 1] == 1) {
                                kickPlay = 1;
                                //Play Kick
                                //playKick.start();
                                sp2.play(kickId, 1, 1, 0, 0, 1);

                            }

                            //play snare
                            if (snare[beat - 1] == 1 && kickPlay == 0) {
                                //Play Snare
                                //playSnare.start();
                                sp3.play(snareId, 1, 1, 0, 0, 1);
                            }

                            //play hiHat
                            if (hiHat[beat - 1] == 1) {
                                //Play Hat
                                //playHat.start();
                                sp1.play(hatId, 1, 1, 0, 0, 1);
                            }
                            try {
                                //wait beatTime seconds for next beat
                                Thread.sleep(beatTime);
                            } catch (InterruptedException e) {

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

//Play
            if (playCount % 2 != 0) {
                button1.setText("Stop");
                t.start();
                valid = 1;
            }
            //Stop
            else {
                button1.setText("Play");
                valid = 0;
                playCount = 0;
            }

        }
        else if (!isNumeric(bpm1) && isNumeric(loopLength1) ){
            TextView badBpm = (TextView) findViewById(R.id.invalidTempo);
            badBpm.setText("Invalid Tempo");
            TextView badField = (TextView) findViewById(R.id.invalidLoop);
            badField.setText("");
            vibe.vibrate(50);
        }
        else if (isNumeric(bpm1) && !isNumeric(loopLength1) ){
            TextView badBpm = (TextView) findViewById(R.id.invalidTempo);
            badBpm.setText("");
            TextView badField = (TextView) findViewById(R.id.invalidLoop);
            badField.setText("Invalid Loop Length");
            vibe.vibrate(50);
        }
        else {
            TextView badBpm = (TextView) findViewById(R.id.invalidTempo);
            badBpm.setText("Invalid Tempo");
            TextView badField = (TextView) findViewById(R.id.invalidLoop);
            badField.setText("Invalid Loop Length");
            vibe.vibrate(50);
        }

    }



}




package kidouchi.mixerapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;

public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    String[] pressedButtonColors = {};
    private MixerDatabase mixerDB = MixerDatabase.getInstance(this);

    int[] buttonIds = {R.id.mixer_button_1, R.id.mixer_button_2};

//    int[] buttonIds = {
//            R.id.mixerButton1, R.id.mixerButton2, R.id.mixerButton3, R.id.mixerButton4,
//            R.id.mixerButton5, R.id.mixerButton6, R.id.mixerButton7, R.id.mixerButton8,
//            R.id.mixerButton9, R.id.mixerButton10, R.id.mixerButton11, R.id.mixerButton12,
//            R.id.mixerButton13, R.id.mixerButton14, R.id.mixerButton15, R.id.mixerButton16,
//            R.id.mixerButton17, R.id.mixerButton18, R.id.mixerButton19, R.id.mixerButton20,
//            R.id.mixerButton21, R.id.mixerButton22, R.id.mixerButton23, R.id.mixerButton24 };
    private MediaPlayer mPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mixerDB.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int buttonId : buttonIds) {
            final ImageButton button = (ImageButton) findViewById(buttonId);
            button.setOnTouchListener(new View.OnTouchListener() {
                GestureDetector gesture = new GestureDetector(MainActivity.this,
                        new MultiTouchGestureListener(button));
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gesture.onTouchEvent(event);
                    return true;
                }
            });
//            button.setClickable(true);
        }

//        final ImageButton button = (ImageButton) findViewById(R.id.mixer_button_1);
//        final ImageButton button2 = (ImageButton) findViewById(R.id.mixer_button_1);
        /* User DOUBLE TAPS button */

        // Set each button to show dialog
//        for (int i = 0; i < buttonIds.length; i++) {
//            ImageButton button = (ImageButton) findViewById(buttonIds[i]);
//            button.setOnLongClickListener(dialogHandler);
//        }

    }

    public void playSound(View v, boolean loop) {
        Log.d("DEBUGGING APP", "In playSound() from:" + v.getId());
        // Retrieve filepath
        Sound sound = (Sound) v.getTag();
        if (sound == null) {
            Log.d("DEBUGGING APP", "PLAYSOUND GOT SOUND: " + sound);
            mPlayer = new MediaPlayer();
//            Start by holding down on the button to record a new sound"
            new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle(R.string.no_sound_title)
                    .setMessage(R.string.no_sound_msg)
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .create().show();
        } else {
            Log.d("DEBUGGING APP", "PLAYSOUND GOT SOUND: " + sound.getSoundFilepath());
            String filename = sound.getSoundFilepath();

            // Play music
            mPlayer = new MediaPlayer();
            mPlayer.setLooping(loop);
            try {
                mPlayer.setDataSource(filename);
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() in playSound() failed");
            }
        }
    }

    private void stopSound() {
        Log.d("DEBUGGING APP", "In stopSound()");
        if (mPlayer != null) {
            Log.d("DEBUGGING APP", "mPlayer NOT NULL");
            mPlayer.release();
            mPlayer = null;
        } else {
            Log.d("DEBUGGING APP", "mPlayer IS NULL");
        }
    }

    @Override
    protected void onResume() {
        Log.d("DEBUGGING APP", "Activity RESUMED");
        super.onResume();
        try {
            mixerDB.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        Log.d("DEBUGGING APP", "Activity PAUSED");
        super.onPause();
        mixerDB.close();
        stopSound();
    }

    private class MultiTouchGestureListener extends GestureDetector.SimpleOnGestureListener {
        private ImageButton button;

        private MultiTouchGestureListener(ImageButton button) {
            this.button = button;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Toast.makeText(MainActivity.this, "LONG PRESS", Toast.LENGTH_LONG).show();
            final RecorderDialog dialog = RecorderDialog.newInstance(button.getId());
            dialog.show(getFragmentManager(), "Dialog");
            super.onLongPress(e);
        }

        // Play sound on infinite
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Toast.makeText(MainActivity.this, "CLICKED ONCE", Toast.LENGTH_LONG).show();
//            Animation pulse = AnimationUtils.loadAnimation(MainActivity.this, R.anim.pulse);
            if (mPlayer == null) {
                playSound(button, true);
//                button.startAnimation(pulse);
            } else {
                stopSound();
//                button.clearAnimation();
            }
            return super.onSingleTapConfirmed(e);
        }

//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//            Toast.makeText(MainActivity.this, "CLICKED TWICE", Toast.LENGTH_LONG).show();
//            if (mPlayer == null) {
//                playSound(button, false);
//            } else {
//                stopSound();
//            }
//            return true;
//        }
    }
}

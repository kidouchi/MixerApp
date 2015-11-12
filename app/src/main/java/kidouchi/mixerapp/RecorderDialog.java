package kidouchi.mixerapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by iuy407 on 10/5/15.
 */
public class RecorderDialog extends DialogFragment{
    private static final String LOG_TAG = RecorderDialog.class.getSimpleName();
    private MixerDatabase mixerDB = MixerDatabase.getInstance(getActivity());

    private MediaRecorder mRecorder = null;

    private String mFilename;

    private TextView mCountdownTextView = null;
    private ImageButton mRecorderButton = null;

    public static RecorderDialog newInstance(int buttonViewId) {
        RecorderDialog dialog = new RecorderDialog();

        Bundle args = new Bundle();
        args.putInt("buttonViewId", buttonViewId);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final int buttonViewId = getArguments().getInt("buttonViewId");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.recorder_dialog, null);
        builder.setView(view);

        mRecorderButton = (ImageButton) view.findViewById(R.id.recorderButton);
        mCountdownTextView = (TextView) view.findViewById(R.id.countdownTextView);

        mRecorderButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        /* get button to save music and pass to recording function */
                        startRecording((ImageButton) getActivity().findViewById(buttonViewId));
                        new CountDownTimer(5000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                mCountdownTextView.setText(
                                        millisUntilFinished / 1000 + " seconds left");
                            }
                            public void onFinish() {
                                stopRecording();
                                mCountdownTextView.setText("Done!");
                            }
                        }.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        stopRecording();
                        dismiss();
                        break;
                }
                return false;
            }
        });

        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RecorderDialog.this.getDialog().cancel();
            }
        });

        return builder.create();
    }

//    private void onRecord(boolean start) {
//        if (start) {
//            startRecording();
//        } else {
//            stopRecording();
//        }
//    }

    private void startRecording(ImageButton button) {
        Log.d("DEBUGGING APP", "In startRecording()");
        mFilename = Environment.getExternalStorageDirectory().getAbsolutePath();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mFilename += "/audiorecordtest_" + timeStamp + ".3gp";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFilename);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        int soundId = mixerDB.createSound(mFilename);
        button.setTag(new Sound(soundId, mFilename));

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        Log.d("DEBUGGING APP", "In stopRecording()");
        if (mRecorder != null) {
            Log.d("DEBUGGING APP", "mRecorder NOT NULL");
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        } else {
            Log.d("DEBUGGING APP", "mRecorder IS NULL");
        }
    }

}

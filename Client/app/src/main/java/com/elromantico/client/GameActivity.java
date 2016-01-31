package com.elromantico.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elromantico.client.gestures.GestureRecognitionService;
import com.elromantico.client.gestures.GestureRecognitionService.GestureRecognitionHandler;
import com.elromantico.client.gestures.classifier.Distribution;

public class GameActivity extends AppCompatActivity {

    private PowerManager.WakeLock mWakeLock;

    private RitualsHub hub;
    private TextView playersCountText, timeLeftText, roundText;
    private GIFView runeImage;
    private LinearLayout infoLayout;
    private int mPlayersCount;
    private int mRuneIndex;
    private boolean mIsPlaying;

    private int roundCounter = 1;

    private double minDistance = 100.0;

    private long elapsedMillis, lastTrackedMillis;

    private static double THRESHOLD = 8.5;

    private GestureRecognitionService recognitionService;
    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            recognitionService = ((GestureRecognitionService.GestureRecognitionServiceBinder) binder).getService();
            recognitionService.startClassificationMode("default");
            recognitionService.setHandler(new GestureRecognitionHandler() {

                @Override
                public void handle(final Distribution distribution) {
                    if (distribution.getBestDistance() < minDistance) {
                        minDistance = distribution.getBestDistance();
                        Log.d("RUNE", "min distance:" + minDistance + " , closest: " + distribution.getBestMatch() + " to " + mRuneIndex + " and "
                        + mIsPlaying);
                    }

                    if (mIsPlaying && mRuneIndex == distribution.getBestMatch() && distribution.getBestDistance() < THRESHOLD) {

                        Log.d("RUNE", "won (distance to target):" + distribution.getBestDistance());
                        infoLayout.setBackground(ContextCompat.getDrawable(GameActivity.this, R.drawable.winround));
                        infoLayout.setVisibility(View.VISIBLE);
                        hub.Success();
                        mIsPlaying = false;
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            recognitionService = null;
        }
    };

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            elapsedMillis = System.currentTimeMillis() - lastTrackedMillis;
            if (elapsedMillis > Constants.ANSWER_TIME_IN_MILLIS) {
                elapsedMillis = Constants.ANSWER_TIME_IN_MILLIS;
                hub.TimeoutExpired();
                timerHandler.removeCallbacks(timerRunnable);
                timeLeftText.setText("0:00");
                mIsPlaying = false;
                return;
            }

            int elapsedSeconds = (int) (elapsedMillis / 1000);

            String secondsString = "0:";
            int secondsLeft = Constants.ANSWER_TIME_IN_MILLIS / 1000 - elapsedSeconds;
            if (secondsLeft < 10) {
                secondsString += "0" + secondsLeft;
            } else {
                secondsString += secondsLeft;
            }
            timeLeftText.setText(secondsString);
            timerHandler.postDelayed(this, 1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        mIsPlaying = true;
        mPlayersCount = getIntent().getIntExtra(Constants.PLAYERS_COUNT_EXTRA, 0);
        mRuneIndex = getIntent().getIntExtra(Constants.RUNE_INDEX_EXTRA, 0);

        // Initialize rituals hub.
        hub = RitualsHub.Instance();

        Context context = this.getApplicationContext();
        Intent serviceIntent = new Intent(context, GestureRecognitionService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        hub.OnNextGame(new RitualsHub.NewGameHandler() {

            @Override
            public void Handle(int playersCount, int runeIndex) {
                mRuneIndex = runeIndex;
                mPlayersCount = playersCount;
                mIsPlaying = true;
                runeImage.setGIFResource(DrawablesMap.drawablesMap.get(runeIndex));

                roundCounter++;
                roundText.setText("Round " + roundCounter);

                lastTrackedMillis = System.currentTimeMillis();
                timerHandler.post(timerRunnable);
                Toast.makeText(GameActivity.this, "Next round starting!", Toast.LENGTH_LONG);
                infoLayout.setVisibility(View.GONE);
                playersCountText.setText("" + playersCount);
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), AudioMap.getRandomSound(AudioMap.newRoundSounds));
                mp.start();

                recognitionService.reset(runeIndex);
            }
        });

        hub.OnEndGame(new RitualsHub.EndGameHandler() {

            @Override
            public void Handle(boolean isWinner) {
                if (isWinner) {
                    infoLayout.setBackground(ContextCompat.getDrawable(GameActivity.this, R.drawable.wingame));
                    infoLayout.setVisibility(View.VISIBLE);
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), AudioMap.getRandomSound(AudioMap.winSounds));
                    mp.start();
                } else {
                    infoLayout.setBackground(ContextCompat.getDrawable(GameActivity.this, R.drawable.lose));
                    infoLayout.setVisibility(View.VISIBLE);
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), AudioMap.getRandomSound(AudioMap.failSounds));
                    mp.start();
                }
                mIsPlaying = false;
                timerHandler.removeCallbacks(timerRunnable);
            }
        });

        // Start connection to server.
        ServerConnection.Instance().Start();
        timerHandler.post(timerRunnable);
        lastTrackedMillis = System.currentTimeMillis();

        setContentView(R.layout.activity_game);

        playersCountText = (TextView) findViewById(R.id.players_count_text);
        playersCountText.setText("" + mPlayersCount);

        timeLeftText = (TextView) findViewById(R.id.time_left_text);
        roundText = (TextView) findViewById(R.id.round_text);

        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/gagalin.otf");
        timeLeftText.setTypeface(type);
        playersCountText.setTypeface(type);
        roundText.setTypeface(type);

        runeImage = (GIFView) findViewById(R.id.rune_image);
        runeImage.setGIFResource(DrawablesMap.drawablesMap.get(mRuneIndex));

        infoLayout = (LinearLayout) findViewById(R.id.info);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onDestroy() {
        this.mWakeLock.release();
        recognitionService.setHandler(null);
        recognitionService = null;
        unbindService(serviceConnection);
        super.onDestroy();
    }
}

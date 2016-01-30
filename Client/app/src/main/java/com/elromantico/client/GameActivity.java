package com.elromantico.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elromantico.client.gestures.GestureRecognitionService;
import com.elromantico.client.gestures.GestureRecognitionService.GestureRecognitionHandler;
import com.elromantico.client.gestures.classifier.Distribution;

public class GameActivity extends AppCompatActivity {

    private RitualsHub hub;
    private LinearLayout bottomBar;
    private TextView bottomText, playersCountText, timeLeftText;
    private GIFView runeImage;
    private int mPlayersCount;
    private int mRuneIndex;
    private boolean mIsPlaying;

    private double minDistance = 100.0;

    private long elapsedMillis, lastTrackedMillis;

    private static double THRESHOLD = 7.0;

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
                        Log.d("RUNE", "min distance:" + minDistance);
                    }

                    if (mIsPlaying && mRuneIndex == distribution.getBestMatch() && distribution.getBestDistance() < THRESHOLD) {

                        Log.d("RUNE", "won (distance to target):" + distribution.getBestDistance());
                        bottomBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, android.R.color.holo_blue_light));
                        bottomText.setText("PLEASE WAIT...");
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
                timeLeftText.setText("0 sec");
                bottomBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, android.R.color.holo_blue_light));
                bottomText.setText("PLEASE WAIT...");
                return;
            }

            int elapsedSeconds = (int) (elapsedMillis / 1000);

            timeLeftText.setText(Constants.ANSWER_TIME_IN_MILLIS / 1000 - elapsedSeconds + " sec");
            timerHandler.postDelayed(this, 1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIsPlaying = true;
        mPlayersCount = getIntent().getIntExtra(Constants.PLAYERS_COUNT_EXTRA, 0);
        mRuneIndex = getIntent().getIntExtra(Constants.RUNE_INDEX_EXTRA, 0);

        Context context = this.getApplicationContext();
        Intent serviceIntent = new Intent(context, GestureRecognitionService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        // Initialize rituals hub.
        hub = RitualsHub.Instance();

        hub.OnNextGame(new RitualsHub.NewGameHandler() {

            @Override
            public void Handle(int playersCount, int runeIndex) {

                mRuneIndex = runeIndex;
                mPlayersCount = playersCount;
                mIsPlaying = true;
                runeImage.setGIFResource(DrawablesMap.drawablesMap.get(runeIndex));

                lastTrackedMillis = System.currentTimeMillis();
                timerHandler.post(timerRunnable);
                Toast.makeText(GameActivity.this, "Next round starting!", Toast.LENGTH_LONG);
                bottomBar.setVisibility(View.GONE);
                playersCountText.setText("" + playersCount);

                recognitionService.reset(runeIndex);
            }
        });

        hub.OnEndGame(new RitualsHub.EndGameHandler() {

            @Override
            public void Handle(boolean isWinner) {
                bottomBar.setVisibility(View.VISIBLE);
                if (isWinner) {
                    bottomBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.green));
                    bottomText.setText("YOU WIN!");
                } else {
                    bottomBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.red));
                    bottomText.setText("YOU LOSE!");
                }
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

        runeImage = (GIFView) findViewById(R.id.rune_image);
        runeImage.setGIFResource(DrawablesMap.drawablesMap.get(mRuneIndex));

        bottomText = (TextView) findViewById(R.id.bottom_text);
        bottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        bottomBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, android.R.color.holo_blue_light));
                bottomText.setText("PLEASE WAIT...");
                hub.Success();
                timerHandler.removeCallbacks(timerRunnable);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onDestroy() {
        recognitionService.setHandler(null);
        recognitionService = null;
        unbindService(serviceConnection);
        super.onDestroy();
    }
}

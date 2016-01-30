package com.elromantico.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
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
    private ImageView runeImage;
    private int mPlayersCount;
    private int runeIndex;

    private GestureRecognitionService recognitionService;
    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            recognitionService = ((GestureRecognitionService.GestureRecognitionServiceBinder) binder).getService();
            recognitionService.startClassificationMode("default");
            recognitionService.setHandler(new GestureRecognitionHandler() {

                @Override
                public void handle(final Distribution distribution) {
                    if (runeIndex == distribution.getBestMatch()) {
                        hub.Success();
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            recognitionService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPlayersCount = getIntent().getIntExtra(Constants.PLAYERS_COUNT_EXTRA, 0);
        runeIndex = getIntent().getIntExtra(Constants.RUNE_INDEX_EXTRA, 0);

        Intent bindIntent = new Intent("com.elromantico.client.gestures.GESTURE_RECOGNIZER");
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        // Initialize rituals hub.
        hub = RitualsHub.Instance();

        hub.OnNextGame(new RitualsHub.NewGameHandler() {

            @Override
            public void Handle(int playersCount, int runeIndex) {
                bottomBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, android.R.color.holo_blue_light));
                bottomText.setText("KEEP GOING!");
                //change picture
                playersCountText.setText("" + playersCount);


            }
        });

        hub.OnUpdateCountdown(new RitualsHub.UpdateCountdownHandler() {

            @Override
            public void Handle(int timeLeft) {
                timeLeftText.setText(timeLeft + " sec");
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
            }
        });

        // Start connection to server.
        ServerConnection.Instance().Start();

        setContentView(R.layout.activity_game);

        playersCountText = (TextView) findViewById(R.id.players_count_text);
        playersCountText.setText("" + mPlayersCount);

        timeLeftText = (TextView) findViewById(R.id.time_left_text);

        runeImage = (ImageView) findViewById(R.id.rune_image);
//       Set image:
//      runeImage.setImageBitmap();

        bottomText = (TextView) findViewById(R.id.bottom_text);
        bottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        bottomBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hub.Success();
            }
        });
    }
}

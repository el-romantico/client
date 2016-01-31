package com.elromantico.client;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    private LinearLayout startButton;
    private TextView slogan;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_start);

        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/gagalin.otf");
        slogan = (TextView) findViewById(R.id.slogan);
        slogan.setTypeface(type);


        dialog = new AlertDialog.Builder(StartActivity.this)
                .setTitle("Waiting for server...")
                .setMessage("Please wait for the server to start the game...")
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert).create();

        RitualsHub.Instance().OnStartGame(new RitualsHub.NewGameHandler() {

            @Override
            public void Handle(int playersCount, int runeIndex) {
                dialog.hide();
                Intent intent = new Intent(StartActivity.this, GameActivity.class);
                intent.putExtra(Constants.PLAYERS_COUNT_EXTRA, playersCount);
                intent.putExtra(Constants.RUNE_INDEX_EXTRA, runeIndex);
                startActivity(intent);
            }
        });

        ServerConnection.Instance().Start();

        startButton = (LinearLayout) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), AudioMap.getRandomSound(AudioMap.connectSounds));
                mp.start();
                startButton.setBackground(ContextCompat.getDrawable(StartActivity.this, R.drawable.connect2));
                dialog.show();
                RitualsHub.Instance().Connect();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp = MediaPlayer.create(getApplicationContext(), AudioMap.getRandomSound(AudioMap.waitingSounds));
                        mp.start();
                    }
                });
            }
        });
    }
}

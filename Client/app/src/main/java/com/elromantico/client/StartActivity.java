package com.elromantico.client;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

public class StartActivity extends AppCompatActivity {

    private LinearLayout startButton;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        RitualsHub.Instance().OnStartGame(new RitualsHub.NewGameHandler() {

            @Override
            public void Handle(int runeIndex) {
                dialog.hide();
                Intent intent = new Intent(StartActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        ServerConnection.Instance().Start();

        startButton = (LinearLayout) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RitualsHub.Instance().Connect();
                dialog = new AlertDialog.Builder(StartActivity.this)
                        .setTitle("Waiting for server...")
                        .setMessage("Please wait for the server to start the game...")
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }
}

package com.elromantico.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

public class StartActivity extends AppCompatActivity {

    private LinearLayout startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        RitualsHub.Instance().OnStartGame(new RitualsHub.NewGameHandler() {

            @Override
            public void Handle(int runeIndex) {
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
            }
        });
    }
}

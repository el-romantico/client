package com.elromantico.client;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class GameActivity extends AppCompatActivity {

    RitualsHub hub;

    private LinearLayout bottomBar;
    private TextView bottomText, playersCountText;
    private ImageView runeImage;

    private int mPlayersCount, runeIndex;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPlayersCount = getIntent().getIntExtra(Constants.PLAYERS_COUNT_EXTRA, 0);
        runeIndex = getIntent().getIntExtra(Constants.RUNE_INDEX_EXTRA, 0);

        // Initialize rituals hub.
        hub = RitualsHub.Instance();

        hub.OnNextGame(new RitualsHub.NewGameHandler() {

            @Override
            public void Handle(int playersCount, int runeIndex) {
                bottomBar.setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.green));
                bottomText.setText("KEEP GOING!");
                //change picture
                playersCountText.setText("" + playersCount);
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
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Game Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.elromantico.client/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Game Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.elromantico.client/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

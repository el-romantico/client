package com.elromantico.client;

import android.content.OperationApplicationException;

import com.zsoft.signala.hubs.HubInvokeCallback;
import com.zsoft.signala.hubs.HubOnDataCallback;
import com.zsoft.signala.hubs.IHubProxy;

import org.json.JSONArray;

import java.util.ArrayList;

public class RitualsHub {

    public interface NewGameHandler {

        void Handle(int playersCount, int runeIndex);
    }

    public interface EndGameHandler {

        void Handle(boolean isWinner);
    }

    public interface UpdateCountdownHandler {

        void Handle(int timeLeft);
    }

    private static RitualsHub instance;

    private IHubProxy hub;

    public static RitualsHub Instance() {
        if (instance == null) {
            instance = new RitualsHub();
        }
        return instance;
    }

    private RitualsHub() {
        try {
            hub = ServerConnection.Instance().CreateHubProxy("RitualsHub");
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    public void Connect() {
        hub.Invoke("Connect", new ArrayList<>(), new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
            }

            @Override
            public void OnError(Exception ex) {
            }
        });
    }

    public void Success() {
        hub.Invoke("Success", new ArrayList<>(), new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
            }

            @Override
            public void OnError(Exception ex) {
            }
        });
    }

    public void OnStartGame(final NewGameHandler  handler) {
        hub.On("StartGame", new HubOnDataCallback() {

            @Override
            public void OnReceived(JSONArray args) {
                onNewGame(handler, args);
            }
        });
    }

    public void OnNextGame(final NewGameHandler  handler) {
        hub.On("NextGame", new HubOnDataCallback() {

            @Override
            public void OnReceived(JSONArray args) {
                onNewGame(handler, args);
            }
        });
    }

    public void OnEndGame(final EndGameHandler handler) {
        hub.On("EndGame", new HubOnDataCallback() {

            @Override
            public void OnReceived(JSONArray args) {
                try {
                    handler.Handle(args.getBoolean(0));
                } catch (Exception e) {
                    //TODO after the game jam
                }
            }

        });
    }

    public void OnUpdateCountdown(final UpdateCountdownHandler handler) {
        hub.On("UpdateCountdown", new HubOnDataCallback() {

            @Override
            public void OnReceived(JSONArray args) {
                try {
                    handler.Handle(args.getInt(0));
                } catch (Exception e) {
                    //TODO after the game jam
                }
            }

        });
    }

    private void onNewGame(final NewGameHandler handler, JSONArray args) {
        int playersCount = 0;
        int runeIndex = 0;
        try {
            playersCount = args.getInt(0);
            runeIndex = args.getInt(1);
        } catch (Exception e) {
            // TODO: Handle exception gracefully...
        }
        handler.Handle(playersCount, runeIndex);
    }
}

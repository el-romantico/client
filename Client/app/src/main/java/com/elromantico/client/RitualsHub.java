package com.elromantico.client;

import android.content.OperationApplicationException;

import com.zsoft.signala.hubs.HubInvokeCallback;
import com.zsoft.signala.hubs.HubOnDataCallback;
import com.zsoft.signala.hubs.IHubProxy;

import org.json.JSONArray;

import java.util.ArrayList;

public class RitualsHub {

    public interface NewGameHandler {

        void Handle(int runeIndex);
    }

    public interface EndGameHandler {

        void Handle(boolean isWinner);
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
                int runeIndex = 0;
                try {
                    runeIndex = args.getInt(0);
                } catch (Exception e) {
                    // TODO: Handle exception gracefully...
                }
                handler.Handle(runeIndex);
            }
        });
    }

    public void OnNextGame(final NewGameHandler  handler) {
        hub.On("NextGame", new HubOnDataCallback() {

            @Override
            public void OnReceived(JSONArray args) {
                int runeIndex = 0;
                try {
                    runeIndex = args.getInt(0);
                } catch (Exception e) {
                    // TODO: Handle exception gracefully...
                }
                handler.Handle(runeIndex);
            }
        });
    }

    public void OnEndGame(final EndGameHandler handler) {
        hub.On("EndGame", new HubOnDataCallback() {

            @Override
            public void OnReceived(JSONArray args) {
                handler.Handle(true);
            }
        });
    }
}

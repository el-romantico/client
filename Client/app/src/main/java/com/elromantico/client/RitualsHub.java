package com.elromantico.client;

import android.content.OperationApplicationException;

import com.zsoft.signala.hubs.HubInvokeCallback;
import com.zsoft.signala.hubs.HubOnDataCallback;
import com.zsoft.signala.hubs.IHubProxy;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class RitualsHub {

    public interface NextGameHandler {

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

    public void OnNextGame(final NextGameHandler  handler) {
        hub.On("NextGame", new HubOnDataCallback() {

            @Override
            public void OnReceived(JSONArray args) {
                handler.Handle(42);
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

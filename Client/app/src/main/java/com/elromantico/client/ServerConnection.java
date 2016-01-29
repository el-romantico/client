package com.elromantico.client;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.zsoft.signala.hubs.HubConnection;
import com.zsoft.signala.transport.StateBase;
import com.zsoft.signala.transport.longpolling.LongPollingTransport;

public class ServerConnection extends HubConnection {

    private static ServerConnection instance;

    private Context context;

    public static synchronized ServerConnection Instance() {
        if (instance == null) {
            instance = new ServerConnection(RitualsApplication.getContext());
        }
        return instance;
    }

    private ServerConnection(Context context) {
        super("https://ritualsserver.azurewebsites.net", context, new LongPollingTransport());
        this.context = context;
    }

    @Override
    public void OnStateChanged(StateBase oldState, StateBase newState) {
        Toast.makeText(context, oldState.getState() + " -> " + newState.getState(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnError(Exception exception) {
        Toast.makeText(context, "On error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
    }
}

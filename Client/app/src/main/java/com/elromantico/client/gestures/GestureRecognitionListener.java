package com.elromantico.client.gestures;

import com.elromantico.client.gestures.classifier.Distribution;

public interface GestureRecognitionListener {

    void onGestureRecognized(Distribution distribution);
}


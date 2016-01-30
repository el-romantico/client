package com.elromantico.client.gestures.recorder;

import java.util.List;

public interface GestureRecorderListener {

	void recognizeGesture(List<float[]> values);
}

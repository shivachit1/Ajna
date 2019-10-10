package app.shiva.ajna.activities;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

 class CustomSdpObserver implements SdpObserver {

       CustomSdpObserver(String observer) {
        }


@Override
public void onCreateSuccess(SessionDescription sessionDescription) {

}

@Override
public void onSetSuccess() {}

@Override
public void onCreateFailure(String s) {}

@Override
public void onSetFailure(String s) {}


}

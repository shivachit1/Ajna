package app.shiva.ajna.activities;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpTransceiver;

class PeerConnectionObserver implements PeerConnection.Observer {

private String logTag = this.getClass().getCanonicalName();

        PeerConnectionObserver(String logTag) {
        this.logTag = this.logTag+" "+logTag;
        }

@Override
public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        }

@Override
public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        }

    @Override
    public void onConnectionChange(PeerConnection.PeerConnectionState newState) {

    }

    @Override
public void onIceConnectionReceivingChange(boolean b) {
        }

@Override
public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
       }

@Override
public void onIceCandidate(IceCandidate iceCandidate) {
        }

@Override
public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        }

@Override
public void onAddStream(MediaStream mediaStream) {
       }

@Override
public void onRemoveStream(MediaStream mediaStream) {
        }

@Override
public void onDataChannel(DataChannel dataChannel) {
       }

@Override
public void onRenegotiationNeeded() {
        }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {

    }

    @Override
    public void onTrack(RtpTransceiver transceiver) {

    }
}
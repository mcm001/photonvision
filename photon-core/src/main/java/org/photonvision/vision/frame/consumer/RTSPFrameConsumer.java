package org.photonvision.vision.frame.consumer;

import dev.onvoid.webrtc.*;
import dev.onvoid.webrtc.media.*;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;
import org.photonvision.vision.opencv.CVMat;

public class RTSPFrameConsumer {

    VideoWriter writer = new VideoWriter();

    public RTSPFrameConsumer() {
        writer.open(
                // "appsrc ! videoconvert ! x264enc noise-reduction=10000 tune=zerolatency byte-stream=true threads=4 key-int-max=15 ! mpegtsmux ! udpsink host=localhost port=9999",
                "appsrc ! videoconvert ! video/x-raw,framerate=20/1 ! videoscale ! videoconvert ! x264enc tune=zerolatency speed-preset=superfast ! rtph264pay ! udpsink host=127.0.0.1 port=5000",
                0,
                (double) 15,
                new Size(640, 480),
                true);

        // if (!writer.isOpened()) {
        //     throw new RuntimeException();
        // }
    }

    public void accept(CVMat image) { 
        writer.write(image.getMat());
    }

    // PeerConnectionFactory factory = new PeerConnectionFactory();

    // // Define a STUN-server to provide ICE candidates to the remote peer.
    // RTCIceServer iceServer = new RTCIceServer();

    // RTCConfiguration config = new RTCConfiguration();

    // // Provide an simple PeerConnectionObserver with the only mandatory callback
    // // method.
    // RTCPeerConnection peerConnection;

    // public void accept(CVMat image) {
    // iceServer.urls.add("stun:stun.l.google.com:19302");
    // config.iceServers.add(iceServer);
    // peerConnection = factory.createPeerConnection(config, candidate -> {
    // /* transmit the candidate over a signaling channel */ });

    // for (RTCRtpSender sender : peerConnection.getSenders()) {
    // MediaStreamTrack track = sender.getTrack();

    // if (nonNull(track) && track.getId().equals(YOUR_TRACK_ID)) {
    // RTCRtpSendParameters sendParams = sender.getParameters();

    // for (var encoding : sendParams.encodings) {
    // // In this example the min/max bitrate is 100/300 kbit/s
    // encoding.minBitrate = 100 * 1000; // bits per second
    // encoding.maxBitrate = 300 * 1000;
    // encoding.maxFramerate = 20.0; // for video
    // }

    // sender.setParameters(sendParams);
    // }
    // }

    // }
}

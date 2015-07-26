package fr.pchab.webrtcclient;

public class PeerConnectionParameters {
    public final boolean videoCallEnabled;
    public final boolean loopback;
    public final int videoWidth;
    public final int videoHeight;
    public final int videoFps;
    public final int videoStartBitrate;
    public final String videoCodec;
    public final boolean videoCodecHwAcceleration;
    public final int audioStartBitrate;
    public final boolean noAudioProcessing;
    public final String audioCodec;
    public final boolean cpuOveruseDetection;

    /**
     *
     * @param videoCallEnabled whether enable video or not
     * @param loopback
     * @param videoWidth
     * @param videoHeight
     * @param videoFps
     * @param videoStartBitrate
     * @param videoCodec
     * @param videoCodecHwAcceleration
     * @param audioStartBitrate bit rate for audio transmission
     * @param audioCodec codec used for audio
     * @param noAudioProcessing true to disable audio processing
     * @param cpuOveruseDetection set true to detect CPI overuse
     */
    public PeerConnectionParameters(
            boolean videoCallEnabled, boolean loopback,
            int videoWidth, int videoHeight, int videoFps, int videoStartBitrate,
            String videoCodec, boolean videoCodecHwAcceleration,
            int audioStartBitrate, String audioCodec, boolean noAudioProcessing,
            boolean cpuOveruseDetection) {
        this.videoCallEnabled = videoCallEnabled;
        this.loopback = loopback;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.videoFps = videoFps;
        this.videoStartBitrate = videoStartBitrate;
        this.videoCodec = videoCodec;
        this.videoCodecHwAcceleration = videoCodecHwAcceleration;
        this.audioStartBitrate = audioStartBitrate;
        this.audioCodec = audioCodec;
        this.noAudioProcessing = noAudioProcessing;
        this.cpuOveruseDetection = cpuOveruseDetection;
    }
}
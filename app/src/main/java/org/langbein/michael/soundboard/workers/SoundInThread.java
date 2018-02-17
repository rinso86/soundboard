package org.langbein.michael.soundboard.workers;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SoundInThread extends Thread {

    private final int frequency;
    private boolean recording;
    private AudioRecord audioRecord;
    private short[] audiodata;
    private BlockingQueue<Short> inBuffer;

    public SoundInThread() {

        frequency = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);


        int bufferSizeInBytes = AudioRecord.getMinBufferSize(
                frequency,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.DEFAULT,
                frequency,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSizeInBytes);

        audiodata = new short[bufferSizeInBytes/4];
        inBuffer = new LinkedBlockingQueue<Short>();

        recording = true;
    }


    @Override
    public void run() {
        /**
         * put <------------ blocking
         * add <------------ exception
         * offer <---------- special value
         * offer(long) <---- timeout
         */
        audioRecord.startRecording();
        while (true) {
            if(recording){
                int readLength = audioRecord.read(audiodata, 0, audiodata.length);
                for(int i = 0; i < readLength; i++){
                    inBuffer.add(audiodata[i]);
                }
            }
        }
        //audioRecord.stop();
    }


    public short[] takeFromBuffer() {
        int n = inBuffer.size();
        return takeFromBuffer(n);
    }

    public short[] takeFromBuffer(int n) {
        /**
         * take <-------- blocking
         * remove <------ exception
         * poll <-------- special value
         * poll(long) <-- timeout
         */

        int l = inBuffer.size();
        short[] data;
        if(l < n){ // If we have been asked for more data than there is, give all we have
            data = new short[l];
            for(int i = 0; i < l; i++) {
                data[i] = inBuffer.remove();
            }
        } else { // Otherwise, give just enough
            data = new short[n];
            for(int i = 0; i < n; i++) {
                data[i] = inBuffer.remove();
            }
        }

        return data;
    }


    public void close() {
        stopRecording();
        audioRecord.stop();
    }

    public void stopRecording() {
        recording = false;
    }

    public void startRecording() {
        recording = true;
    }

    public int getFrequency() {
        return frequency;
    }
}

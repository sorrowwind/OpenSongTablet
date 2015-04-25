package com.garethevans.church.opensongtablet;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class AudioGenerator {

	private int sampleRate;
	private AudioTrack audioTrack;

	public AudioGenerator(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public double[] getSineWave(int samples,int sampleRate,double frequencyOfTone) {
		double[] sample = new double[samples];
		for (int i = 0; i < samples; i++) {
			sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/frequencyOfTone));
		}
		return sample;
	}

	public byte[] get16BitPcm(double[] samples) {
		byte[] generatedSound = new byte[2 * samples.length];
		int index = 0;
		for (double sample : samples) {
			// scale to maximum amplitude
			short maxSample = (short) ((sample * Short.MAX_VALUE));
			// in 16 bit wav PCM, first byte is the low order byte
			generatedSound[index++] = (byte) (maxSample & 0x00ff);
			generatedSound[index++] = (byte) ((maxSample & 0xff00) >>> 8);

		}
		return generatedSound;
	}

	@SuppressWarnings("deprecation")
	public void createPlayer(){
		//FIXME sometimes audioTrack isn't initialized
		boolean isready = false;
		
		while (!isready) {
			audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					sampleRate, AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT, sampleRate,
					AudioTrack.MODE_STREAM);
			if (audioTrack.getState()==AudioTrack.STATE_INITIALIZED) {
				isready = true;
			} else {
				audioTrack = null;
				Log.i("audioTrack","unable to initialise - trying again...");
			}
		}
		
		float leftVolume = FullscreenActivity.metronomevol;
		float rightVolume = FullscreenActivity.metronomevol;
		
		if (FullscreenActivity.metronomepan.equals("left")) {
			leftVolume = FullscreenActivity.metronomevol;	
			rightVolume = 0.0f;
		} else if (FullscreenActivity.metronomepan.equals("right")) {
			leftVolume = 0.0f;
			rightVolume = FullscreenActivity.metronomevol;	
		} 
		try {
			audioTrack.setStereoVolume(leftVolume, rightVolume);						
			audioTrack.play();
		} catch (Exception e) {
			// Catches temp errors
		}
	}

	@SuppressWarnings("deprecation")
	public void writeSound(double[] samples) {
		byte[] generatedSnd = get16BitPcm(samples);
		if (FullscreenActivity.metronomeonoff.equals("on") && audioTrack.getState()==AudioTrack.STATE_INITIALIZED && audioTrack.getPlayState()==AudioTrack.PLAYSTATE_PLAYING) {
			try {
				audioTrack.write(generatedSnd, 0, generatedSnd.length);
			} catch (Exception e) {
			     // This will catch any exception, because they are all descended from Exception
				Log.d("whoops","error writing sound");
			}
			if (FullscreenActivity.metronomepan.equals("left")) {
				try {
					audioTrack.setStereoVolume(FullscreenActivity.metronomevol, 0.0f);			
				} catch (Exception e) {
				     // This will catch any exception, because they are all descended from Exception
					Log.d("whoops","error setting volume");
				}
			} else if (FullscreenActivity.metronomepan.equals("right")) {
				try {
					audioTrack.setStereoVolume(0.0f, FullscreenActivity.metronomevol);						
				} catch (Exception e) {
				     // This will catch any exception, because they are all descended from Exception
					Log.d("whoops","error setting volume");
				}
			} else {
				try {
					audioTrack.setStereoVolume(FullscreenActivity.metronomevol, FullscreenActivity.metronomevol);						
				} catch (Exception e) {
				     // This will catch any exception, because they are all descended from Exception
					Log.d("whoops","error setting volume");
				}
			}
		}
	}

	public void destroyAudioTrack() {
		audioTrack.stop();
		audioTrack.release();
	}

}
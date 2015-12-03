package com.sorasoft.dicecam;

import com.sorasoft.dicecam.R;
import com.sorasoft.dicecam.setting.Settings;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class SoundProvider {

	private static SoundProvider instance = null;
	
	private SoundPool mSoundPool = null;
	private int mShutterSoundID;
	private boolean shutterSoundLoaded = false;
	private boolean playShutterSoundOnLoadComplete = false;

	public static SoundProvider sharedProvider() {
		if ( instance == null ) {
			return instance = new SoundProvider();
		}
		return instance;
	}
	
	protected SoundProvider() {
		mSoundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 0);
		mShutterSoundID = mSoundPool.load((DiceCamera)DiceCamera.mContext, R.raw.camera_click, 1);
		mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				Log.d("dicecam", "soundPool: " + soundPool);
				Log.d("dicecam", "sampleId: " + sampleId);
				Log.d("dicecam", "status: " + status);
				if ( status == 0 ) {
					// success
					shutterSoundLoaded = true;
					
					if ( playShutterSoundOnLoadComplete ) {
						playShutterSound();
					}
				}
			}
		});
	}
	
	private int getCurrentSystemVolume() {
		AudioManager audio = (AudioManager)((DiceCamera)DiceCamera.mContext).getSystemService(Context.AUDIO_SERVICE);
		return audio.getStreamVolume(AudioManager.STREAM_SYSTEM);
	}
	
	public void playShutterSound() {
		if ( shutterSoundLoaded == false ) {
			playShutterSoundOnLoadComplete = true;
			return;
		}
		mSoundPool.play(mShutterSoundID, 1.f, 1.f, 0, 0, 1.f);
	}
}

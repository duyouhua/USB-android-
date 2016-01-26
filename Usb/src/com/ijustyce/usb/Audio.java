package com.ijustyce.usb;

import java.io.IOException;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class Audio {
	MediaPlayer mMediaPlayer;
	Context context;
	
	public Audio(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		mMediaPlayer = new MediaPlayer();
	}
	
	public boolean isPlaying(){
		if (mMediaPlayer!=null&&mMediaPlayer.isPlaying()) {
			return true;
		}
		return false;
	}
	
	public void play(String name){
		if(mMediaPlayer.isPlaying() || name == null){
			return;
		}
		mMediaPlayer.reset();
		try {
			//Log.i(TAG, "test 1");
			AssetFileDescriptor fileDescriptor = context.getAssets().openFd(name);
			//Log.i(TAG, "test 2");
			mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(), fileDescriptor.getLength());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		//mMediaPlayer.set
		mMediaPlayer.setLooping(false); 
		try {
			mMediaPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMediaPlayer.start();
		System.out.println("Audio.play()");
	}
	
	public void stop(){
		if(mMediaPlayer.isPlaying()){
			mMediaPlayer.stop();
			//mMediaPlayer.release();
		}
	}
	
	public void release(){
		mMediaPlayer.release();
		mMediaPlayer = null;
	}
}

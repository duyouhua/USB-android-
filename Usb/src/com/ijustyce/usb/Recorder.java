package com.ijustyce.usb;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;

public class Recorder {
	
	
	public interface RecorderPlayingDelegate{
		public void recorderEndPlayingAction();
	}
	
	RecorderPlayingDelegate decorderPlayingDelegate;
	
	
	
	public void setDelegate(RecorderPlayingDelegate decorderPlayingDelegate){
		this.decorderPlayingDelegate = decorderPlayingDelegate;
	}
	
	
	MediaRecorder mMediaRecorder;
	MediaPlayer mediaPlayer;
	Context context;
	File mRecAudioFile;
	File mRecAudioPath;
	String strTempFile = "recaudio_";
	private static MediaRecorder m=null;
	public Recorder(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(new OnCompletionListener(){
			
		
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				
				System.out.println("++++++++++++++++++++++++======================");
				
				
				mp.stop();
				
				
	 
				
				if (mRecAudioFile!=null&&mRecAudioFile.exists()) {
					mRecAudioFile.delete();
				}
				if (mRecAudioPath!=null&&mRecAudioPath.exists()) {
					mRecAudioPath.delete();
				}
		 
				
				
				
				if (decorderPlayingDelegate!=null) {
					decorderPlayingDelegate.recorderEndPlayingAction();
				}
				
			}
			
			
		});
	}
	
	private MediaRecorder getInstance(){
		if (m==null) {
			m=new MediaRecorder();
		}
		return m;
	}

	public void startRecord() {
		try {
			if (!initRecAudioPath()) {
				return;
			}

			if (mMediaRecorder != null) {
				return;
			}

			mMediaRecorder = new MediaRecorder();
//			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);// …Ë÷√¬ÛøÀ∑Á
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// …Ë÷√¬ÛøÀ∑Á
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			try {
				mRecAudioFile = File.createTempFile(strTempFile, ".amr",
						mRecAudioPath);

			} catch (Exception e) {
				e.printStackTrace();
			}
			mMediaRecorder.setOutputFile(mRecAudioFile.getAbsolutePath());
			mMediaRecorder.prepare();
			mMediaRecorder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopRecord() {
		if (mRecAudioFile != null && mMediaRecorder != null) {

			try {
				mMediaRecorder.setOnErrorListener(null);
				mMediaRecorder.setPreviewDisplay(null);
				mMediaRecorder.stop();
				mMediaRecorder.release();
				mMediaRecorder = null;
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	public void playMusic() {
		/*
		 * Intent intent = new Intent();
		 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * intent.setAction(android.content.Intent.ACTION_VIEW);
		 * intent.setDataAndType(Uri.fromFile(mRecAudioFile), "audio");
		 * context.startActivity(intent);
		 */

		if (mMediaRecorder != null) {
			return;
		}

		try {

			mediaPlayer.reset();
			mediaPlayer.setDataSource(mRecAudioFile.getAbsolutePath());
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private boolean initRecAudioPath() {
		if (sdcardIsValid()) {
			String path = Environment.getExternalStorageDirectory().toString()
					+ File.separator + "record";// µ√µΩSDø®µ√¬∑æ∂
			mRecAudioPath = new File(path);
			if (!mRecAudioPath.exists()) {
				mRecAudioPath.mkdirs();
			}
		} else {
			mRecAudioPath = null;
		}
		return mRecAudioPath != null;
	}

	private boolean sdcardIsValid() {
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
		}
		return false;
	}

}

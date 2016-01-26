package com.ijustyce.fm;

import java.io.IOException;
import java.util.Arrays;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.app.Service;

public class FMService extends Service {
	private static final String TAG = "JNI";
	
	public  static final String FM_STAR = "com.ijustyce.fm.FM_STAR" ;
	private static final String FM_TUNE = "com.ijustyce.fm.FM_TUNE" ;
	private static final String FM_VOLUM = "com.ijustyce.fm.FM_VOLUM" ;
	private static final String FM_VOLUM_UP = "com.ijustyce.fm.FM_VOLUM_UP" ;
	private static final String FM_VOLUM_DOWN = "com.ijustyce.fm.FM_VOLUM_DOWN" ;
	public static final String FM_STOP = "com.ijustyce.fm.FM_STOP" ;
	public static final String FM_STOP_AUDIO_NOMAL = "com.ijustyce.fm.FM_STOP_AUDIO_NOMAL" ;
	private static final String AUDIO_MODE = "com.ijustyce.fm.AUDIO_MODE" ;
	
	private static int[] TUNE_LIST = {8750,8810,9010,9210,9410,9610,9810,10010,10210,10410,10610,10370};
	private static int mTune = 0;
	private static int mVolumn = 10;
	private static int mBoot_status = 0;
	private AudioManager mAudioManager;
	
	
	/**
	 * mode = 3 静音
	 * mode = 0 的时候才可以播放音乐
	 */
	
	public void onCreate() {  
        Log.v("JNI", "mikewang ServiceDemo onCreate");  
        super.onCreate();  
        IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(FM_STAR);
		intentFilter.addAction(FM_TUNE);
		intentFilter.addAction(FM_VOLUM);
		intentFilter.addAction(FM_VOLUM_UP);
		intentFilter.addAction(FM_VOLUM_DOWN);
		intentFilter.addAction(FM_STOP);
		intentFilter.addAction(AUDIO_MODE);
		intentFilter.addAction(FM_STOP_AUDIO_NOMAL);
		registerReceiver(dynamicReceiver, intentFilter);
		mAudioManager = (AudioManager) FMService.this.getSystemService(Context.AUDIO_SERVICE);
		/*
		try { 
		
			//Runtime.getRuntime().exec("tas5727_test qn1"); 
			
			//Runtime.getRuntime().exec("tas5727_test qn2");
			
			//Runtime.getRuntime().exec("tas5727_test qn4 10");
        } catch (Exception e) {  
                e.printStackTrace();  
        } 
		*/
		
		
    }  

/*	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.e("test", "mikewang = onCreate MainActivity\n");
		
			
		
		try { 
		
			Runtime.getRuntime().exec("tas5727_test qn1"); 
			
			Runtime.getRuntime().exec("tas5727_test qn2");

			Runtime.getRuntime().exec("tas5727_test qn3 8750"); 
			
			Runtime.getRuntime().exec("tas5727_test qn4 15"); 
        } catch (Exception e) {  
                e.printStackTrace();  
        } 

		int ret = -1;
		int[] buf = new int[4];

		super.onCreate(savedInstanceState);
		I2c i2c = new I2c();
		
//		String mode = "test";
//		int argv = 1010;
//		ret = i2c.FMcontrol(mode, argv);
//		if (ret > 0) {
//			Log.e(TAG, "------FMtest success-----");
//		} else {
//			Log.e(TAG, "----FMtest fail-------");
//		}
		
		String str = i2c.jniTest();
		
		Log.e(TAG, "------str-----"+str);

		////////////////////////////////////////////////---------FM_detect
		String mode = "qn1";
		int argv = 1010;

		ret = i2c.FMcontrol(mode, argv);
		if (ret > 0) {
			Log.e(TAG, "------FMcontrol success-----");
		} else {
			Log.e(TAG, "----FMcontrol fail-------");
		}
		////////////////////////////////////////////////----------FM_initial
		mode = "qn2";
		argv = 1010;

		ret = i2c.FMcontrol(mode, argv);
		if (ret > 0) {
			Log.e(TAG, "------FMcontrol success-----");
		} else {
			Log.e(TAG, "----FMcontrol fail-------");
		}

		////////////////////////////////////////////////-----------FM_tune
		mode = "qn3";
		argv = 8750;

		ret = i2c.FMcontrol(mode, argv);
		if (ret > 0) {
			Log.e(TAG, "------FMcontrol success-----");
		} else {
			Log.e(TAG, "----FMcontrol fail-------");
		}
		////////////////////////////////////////////////-----------FM_volumn
		mode = "qn4";
		argv = 10;

		ret = i2c.FMcontrol(mode, argv);
		if (ret > 0) {
			Log.e(TAG, "------FMcontrol success-----");
		} else {
			Log.e(TAG, "----FMcontrol fail-------");
		}
	

		
				
				IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(FM_STAR);
		intentFilter.addAction(FM_TUNE);
		intentFilter.addAction(FM_VOLUM);
		intentFilter.addAction(FM_VOLUM_UP);
		intentFilter.addAction(FM_VOLUM_DOWN);
		intentFilter.addAction(FM_STOP);
		registerReceiver(dynamicReceiver, intentFilter);
	}
*/
		private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {  
        
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("!!!!!!!!!!!!  dynamicReceiver");
			// TODO Auto-generated method stub
			if (intent.getAction().equals( FM_STAR )) {
				
				System.out.println("!!!!!!!!!!!!!!!!!!! +dynamicReceiver");
				if(mBoot_status==0){
					mBoot_status = 1;
					Log.e("JNI", "mikewang:BroadcastReceiver mBOOT"); 
					try { 	
						Runtime.getRuntime().exec("tas5727_test qn1"); 
						Runtime.getRuntime().exec("tas5727_test qn2");	
//						Runtime.getRuntime().exec("tas5727_test qn3 8750");						
						Runtime.getRuntime().exec("tas5727_test qn3 10370");						
						Runtime.getRuntime().exec("tas5727_test qn4 10"); 
					} catch (Exception e) {  
							e.printStackTrace();  
					} 
				}
				Log.e("JNI", "mikewang:channel: 8750");
				try {
					mTune = 0;
//					Runtime.getRuntime().exec("tas5727_test qn3 8750");
					Runtime.getRuntime().exec("tas5727_test qn3 10370");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				int [] items={AudioManager.MODE_CURRENT,AudioManager.MODE_IN_CALL,AudioManager.MODE_IN_COMMUNICATION,
						AudioManager.MODE_INVALID,AudioManager.MODE_NORMAL,AudioManager.MODE_RINGTONE};
				
				 AudioManager mAudioManager = (AudioManager) FMService.this.getSystemService(Context.AUDIO_SERVICE);
				int mode = mAudioManager.getMode();
//			    Log.d(TAG, "mikewang audio mode:"+mode+" !!!!!!!!!!!");
				System.out.println("!!!!!!!!!!!!!!!!!!"+mode);
			    for (int i : items) {
			    	 Log.d(TAG, "mikewang audio mode:"+i+" !!!!!!!!!!!");
				}
			    if(mode != 5){
					mAudioManager.setMode(5);
				} 
			}else if (intent.getAction().equals( FM_TUNE )){
				if(mBoot_status==0){
					mBoot_status = 1;
					Log.e("JNI", "mikewang:BroadcastReceiver mBOOT");
					try { 	
						Runtime.getRuntime().exec("tas5727_test qn1"); 
						Runtime.getRuntime().exec("tas5727_test qn2");	
//						Runtime.getRuntime().exec("tas5727_test qn3 8750");
						Runtime.getRuntime().exec("tas5727_test qn3 10370");
						Runtime.getRuntime().exec("tas5727_test qn4 10"); 
					} catch (Exception e) {  
							e.printStackTrace();  
					} 
				}
				if(mTune<TUNE_LIST.length-1){
					mTune++;
				}					
				else {
					mTune = 0;
				}
				String channel = "tas5727_test qn3 " + TUNE_LIST[mTune];
				Log.e("JNI", "mikewang:channel:"+channel);
				try {
					Runtime.getRuntime().exec(channel);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 AudioManager mAudioManager = (AudioManager) FMService.this.getSystemService(Context.AUDIO_SERVICE);
				int mode = mAudioManager.getMode();
			    Log.d(TAG, "mikewang audio mode:"+mode);
			    if(mode != 5){
					mAudioManager.setMode(5);
				} 			
			}else if (intent.getAction().equals( FM_VOLUM )){
				String msg = intent.getStringExtra("volumn");
				String volumn = "tas5727_test qn4 " + msg;
				Log.e("JNI", "mikewang:volumn:"+volumn);
				try {
					Runtime.getRuntime().exec(volumn);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//AudioManager mAudioManager = (AudioManager) MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
				int mode = mAudioManager.getMode();
			    Log.d(TAG, "mikewang audio mode:"+mode);
			    if(mode != 5){
					mAudioManager.setMode(5);
				}	
			}else if (intent.getAction().equals( FM_VOLUM_UP )){
				if(mVolumn<15){
					mVolumn += 2;
				}					
				else {
					mVolumn = 15;
				}
				String volumn = "tas5727_test qn4 " + Integer.toString(mVolumn);
				Log.e("JNI", "mikewang:volumn:"+volumn);
				try {
					Runtime.getRuntime().exec(volumn);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (intent.getAction().equals( FM_VOLUM_DOWN )){
				if(mVolumn>2){
					mVolumn -= 2;
				}					
				else {
					mVolumn = 0;
				}
				String volumn = "tas5727_test qn4 " + Integer.toString(mVolumn);
				Log.e("JNI", "mikewang:volumn:"+volumn);
				try {
					Runtime.getRuntime().exec(volumn);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (intent.getAction().equals( FM_STOP )){
				/*
				try {
					Runtime.getRuntime().exec("tas5727_test qn4 12");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
				
//				int mode=Integer.parseInt(intent.getStringExtra("arg"));
				int mode=3;
				System.out.println("!!!!!!########"+mode);
				
				 AudioManager mAudioManager = (AudioManager) FMService.this.getSystemService(Context.AUDIO_SERVICE);
//					int mode = mAudioManager.getMode();
				 mAudioManager.setMode(mode);
				 System.out.println("!!!!!! ++++++++++++++++++++"+mode);
			}else if (intent.getAction().equals( AUDIO_MODE )){
				int audio_mode = intent.getIntExtra("audio_mode", 0);
				Log.d(TAG, "mikewang set audio mode:"+audio_mode);
				//AudioManager mAudioManager = (AudioManager) MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
				int mode = mAudioManager.getMode();
				Log.d(TAG, "mikewang get audio mode start:"+mode);
				while(audio_mode!=mode){			
					if(audio_mode==5){
						mAudioManager.setMode(3);
					}					
					mode = mAudioManager.getMode();
					Log.d(TAG, "mikewang get audio mode mid:"+mode);					
					mAudioManager.setMode(audio_mode);
					mode = mAudioManager.getMode();
					Log.d(TAG, "mikewang get audio mode end:"+mode);
				}
				//mAudioManager.setMode(audio_mode);
			}else if (intent.getAction().equals( FM_STOP_AUDIO_NOMAL )) {
				int mode=0;
				System.out.println("!!!!!!########"+mode);
				 AudioManager mAudioManager = (AudioManager) FMService.this.getSystemService(Context.AUDIO_SERVICE);
				 mAudioManager.setMode(mode);
				 System.out.println("!!!!!! FM_STOP_AUDIO_NOMAL "+mode);
			}  
			
		}  
    }; 
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	} 
}

package com.ijustyce.fm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class FmBroadcastReceiver extends BroadcastReceiver{

	String FM_STAR = "com.ijustyce.fm.FM_STAR" ;
	String FM_TUNE = "com.ijustyce.fm.FM_TUNE" ;
	String FM_VOLUM = "com.ijustyce.fm.FM_VOLUM" ;
	String FM_STOP = "com.ijustyce.fm.FM_STOP" ;
	public static final String ACTION = "com.ijustyce.fm.fmbroadstart";
	@Override
	public void onReceive(Context context, Intent intent) {
		
//		Log.e("test", "mikewang = onReceive FmBroadcastReceiver\n");
		System.out.println("!!!!!!!!!!!!    FmBroadcastReceiver  hebiao");
		if (intent.getAction().equals(ACTION)){
			
			System.out.println("!!!!!!!!!!!!    FmBroadcastReceiver  hebiao"+ACTION);
			Intent sayHelloIntent = new Intent(context, FMService.class);
			sayHelloIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			//context.startActivity(sayHelloIntent);
			context.startService(sayHelloIntent);
		}
		// TODO Auto-generated method stub
		/*if (intent.getAction().equals( FM_STAR )) {
			try {
				Runtime.getRuntime().exec("tas5727_test qn3 8750");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (intent.getAction().equals( FM_TUNE )){
			try {
				Runtime.getRuntime().exec("tas5727_test qn3 8810");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (intent.getAction().equals( FM_VOLUM )){
			try {
				Runtime.getRuntime().exec("tas5727_test qn4 5");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (intent.getAction().equals( FM_STOP )){
			try {
				Runtime.getRuntime().exec("tas5727_test qn4 12");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}

}

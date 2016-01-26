package com.ijustyce.usb;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.ijustyce.fm.FMService;
import com.ijustyce.fm.FmBroadcastReceiver;
import com.ijustyce.pvutil.PrivateUtil;
import com.ijustyce.usb.Recorder.RecorderPlayingDelegate;
import com.ijustyce.wifi.WifiAdmin;
import com.ijustyce.wifi.WifiHotUtil;
 

/*
 * 
 * 
 *   <TextView
        android:id="@+id/textView1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/hello_world" />

    <Button
        android:id="@+id/button2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignLeft="@+id/Button01"
        android:layout_below="@+id/Button01"
        android:layout_marginTop="34dp"
        android:onClick="sendAction"
        android:text="点击发送消息给服务端" />

    <Button
        android:id="@+id/Button01"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignLeft="@+id/textView1"
        android:layout_below="@+id/textView1"
        android:layout_marginTop="28dp"
        android:onClick="connwfshang"
        android:text="连接WIFI" />
 * */
public class MainActivity extends Activity implements
		Transfer.TransferRevMsgDelegate,RecorderPlayingDelegate {
	
	
	
	public static final String APK_PACKNAME="com.iraylink.wifibox";

	ServerSocket serverSocket = null;
	final int SERVER_PORT = 10086;
	public static Boolean mainThreadFlag = true;


	Transfer transfer = null;

	Audio audio;
	Recorder recorder;
	WifiAdmin myWifiAdmin = null;
	WifiHotUtil wifiHotUtil = null;

	boolean b = true;

	 
	 
	 String micphone_type=null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		audio = new Audio(this);
		recorder = new Recorder(this);
		recorder.setDelegate(this);
		
		// addFMActionBroadcast();
		myWifiAdmin = new MyWifiAdmin(this);

		wifiHotUtil = new WifiHotUtil(this);
		
		
		
//		addSystemRebootFilter();
		
		new Thread() {
			public void run() {
				doListen();
			};
		}.start();
	}

	private void doListen() {
		serverSocket = null;
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			while (mainThreadFlag) {
				Socket socket = serverSocket.accept();
				transfer = new Transfer(socket);
				transfer.setTransferRevMsgDelegateAction(this);
				new Thread(transfer).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addSystemRebootFilter() {
		// TODO Auto-generated method stub
		 IntentFilter filter = new IntentFilter();
         filter.addAction(Intent.ACTION_REBOOT);
         registerReceiver(broadcastReceiver, filter);
	}
	
	BroadcastReceiver broadcastReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			 
			 System.out.println("###############################   android.intent.action.REBOOT");
		}
		
	};

	public void connwfshang(View v) {
		// myWiFIAdmin.addNetwork("HEBIAO_WIFI", "12345678",
		// WifiAdmin.TYPE_WPA);

		// System.out.println("!!!!!!!!!!!!! connwf");
		/*
		 * if (b) { recorder.startRecord(); } else { recorder.stopRecord(); }
		 * 
		 * b = !b;
		 */
		recorder.startRecord();
		new Thread(){
			public void run() {
				try {
					Thread.sleep(1000*10);
					recorder.stopRecord();
					Toast.makeText(MainActivity.this, "aaa", Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			};
		}.start();
		 	

	}

	public void sendAction(View v) {
		
//		audioRecord.startRecording();
		recorder.playMusic();
		// sendMsgString(TEST_TYPE_USB, MSG_STATUS_OK);

		/*
		 * System.out.println("############### wifiHotUtil.isWifiApEnabled()" +
		 * wifiHotUtil.isWifiApEnabled());
		 * 
		 * closeWIFI();
		 * System.out.println("############### wifiHotUtil.isWifiApEnabled()" +
		 * wifiHotUtil.isWifiApEnabled());
		 * 
		 * myWiFIAdmin.openWifi(); myWiFIAdmin.startScan(); //
		 * System.out.println
		 * ("!!!!!!!!!!!!!!!!=========="+myWiFIAdmin.getWifiList());
		 * System.out.println("###################  sendAction  ####"); if
		 * (myWiFIAdmin.getWifiList().size() > 0) { for (ScanResult scanResult :
		 * myWiFIAdmin.getWifiList()) {
		 * 
		 * System.out.println("######## " + scanResult.toString());
		 * System.out.println("#######################"); } }
		 */

		// System.out.println("  " + getInfo());
		// recorder.playMusic();
		// System.out.println("!!!!!!!+  sendAction");
		/*
		 * new Handler().postDelayed(new Runnable() {
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub
		 * 
		 * Toast.makeText(MainActivity.this, "show 30s", Toast.LENGTH_LONG)
		 * .show(); } }, 10 * 1000);
		 */
		 
	}

	private String getInfo() {

		/*
		 * WifiManager wifi = (WifiManager)
		 * getSystemService(Context.WIFI_SERVICE); WifiInfo info =
		 * wifi.getConnectionInfo(); String maxText = info.getMacAddress();
		 * String status = ""; if (wifi.getWifiState() ==
		 * WifiManager.WIFI_STATE_ENABLED) { status = "WIFI_STATE_ENABLED"; }
		 * String ssid = info.getSSID(); int networkID = info.getNetworkId();
		 * int speed = info.getLinkSpeed(); System.out.println("###########" +
		 * info.toString()); return "mac：" + maxText + " " + "ip：" + " " +
		 * "wifi status :" + status + " " + "ssid :" + ssid + " " +
		 * "net work id :" + networkID + " " + "connection speed:" + speed +
		 * " ";
		 */

		myWifiAdmin.openWifi();
		myWifiAdmin.startScan();
		List<ScanResult> list = myWifiAdmin.getWifiList();
		for (ScanResult scanResult : list) {
			System.out.println("--------------" + scanResult.toString());
		}

		return "";
	}

	private void closeWIFI() {
		wifiHotUtil.closeWifiAp();

	}

	@Override
	public void transferRevSocketDisconnectAction() {
		System.out
				.println("!!!!!!!!!!!!!!!!  transferRevSocketDisconnectAction");
		System.exit(0);
	}

	@Override
	public void transferRevMsgAction(String s) {
		// TODO Auto-generated method stub
		 
		if (s == null || s.length() == 0) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mainThreadFlag = false;

			return;
		}
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~ "+micphone_type+"  "+s);
		CAnyValue caAnyValue = new CAnyValue();
		caAnyValue.decodeJSON(s);
		revDoAction(caAnyValue.get(UsbMessage.MESSAGETYPE).asString(),
				caAnyValue.get(UsbMessage.MESSAGEACTION).asString(), caAnyValue
						.get(UsbMessage.ARG0).asString(),
				caAnyValue.get(UsbMessage.ARG1).asString());

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == 30) {
//				recorder.stopRecord();
				sendMsgString(UsbMessage.MESSAGETYPE_MICROPHONE,
						UsbMessage.MESSAGEACTION_MICROPHONE_STOPRECORDING,
						UsbMessage.MESSAGESTATUS_OK, "", "");
			}

		};
	};
	
	/*
	 * 固件版本 /data/data/version.txt  
	 * 格式跟之前一样
	 * 
	 * */
	private String getDataDataVersionAction(){
		
		 String sioioi5=(String) PrivateUtil.invoke(new Build(), "getString", new Class[] { String.class },  
	                new Object[] { "ro.fota.type" }); 
		    String sioioi6=(String) PrivateUtil.invoke(new Build(), "getString", new Class[] { String.class },  
	                new Object[] { "ro.fota.version" }); 
		    
//		    System.out.println("------------------ro.fota.type------#####"+sioioi+"  release="+sioioi2+"      fingerprint="+sioioi3+"         mode"+sioioi4+"              oem" +sioioi5   +"       ro.fota.version="+sioioi6 );
		   
		    
		    System.out.println("==================================+++++"+sioioi6+" =============="+sioioi5);
		    
		    StringBuffer stringBuffer=new StringBuffer(sioioi6);
		    
		    
		    
			if (stringBuffer.toString()==null||stringBuffer.toString().length()==0) {
				return "";
			}
			
			String s[] =stringBuffer.toString().split(" ");
			
			
			if (s.length==0) {
				return "";
			}
			
			 System.out.println("==================================+++++"+s[s.length -1]);
			
			return s[s.length -1];
			 
//			 getPackVersionInfo();
//		} catch (Exception e) {
			// TODO: handle exception
			
//			return "";
			
			
//		}
	
	}

	private void revDoAction(String messageType, String messageAction,
			String arg0, String arg1) {
		
		
		
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
//		deleteDatafile();

		if (UsbMessage.MESSAGETYPE_USB.equals(messageType)) {


			sendMsgString(UsbMessage.MESSAGETYPE_USB, "",
					UsbMessage.MESSAGESTATUS_OK, "", "");
			 
			new Thread() {
				public void run() {
					startFMService();
				};
			}.start();
			
			
			
		 

		} else if (UsbMessage.MESSAGETYPE_VERSION.equals(messageType)) {
// 			String v = android.os.Build.VERSION.RELEASE;
//			String v = android.os.Build.HARDWARE;

			sendMsgString(UsbMessage.MESSAGETYPE_VERSION, "",
					UsbMessage.MESSAGESTATUS_OK, getDataDataVersionAction(), getPackVersionInfo());

		} else if (UsbMessage.MESSAGETYPE_SPEAKER.equals(messageType)) {

			if (UsbMessage.MESSAGEACTION_SPEAKER_HIGH.equals(messageAction)) {
				playHighMusic();
			} else if (UsbMessage.MESSAGEACTION_SPEAKER_LOW
					.equals(messageAction)) {
				playLowMusic();
			} else if (UsbMessage.MESSAGEACTION_SPEAKER_STOP
					.equals(messageAction)) {
				stopMusic();
				sendMsgString(UsbMessage.MESSAGETYPE_SPEAKER, "",
						UsbMessage.MESSAGEACTION_SPEAKER_STOP, "", "");
			}

		} else if (UsbMessage.MESSAGETYPE_FM.equals(messageType)) {
			if (UsbMessage.MESSAGEACTION_FM_STARTORCHANGE.equals(messageAction)) {

				turnOnFM();
			} else if (UsbMessage.MESSAGEACTION_FM_STOP.equals(messageAction)) {
				turnOffFM();
				sendMsgString(UsbMessage.MESSAGETYPE_FM, "",
						UsbMessage.MESSAGETYPE_FM, "", "");
			}

		} else if (UsbMessage.MESSAGETYPE_MICROPHONE.equals(messageType)) {
			
			
			
			
			if (UsbMessage.MESSAGEACTION_MICROPHONE_STARTRECORDING
					.equals(messageAction)) {
				
				if (micphone_type==null) {
					stopMusic();
					turnOffFM();
				}
				
				micphone_type=arg0;
				
				
				new Thread(){
					public void run() {
						try {
							Thread.sleep(500);
							recorder.startRecord();
						} catch (Exception e) {
							// TODO: handle exception
						}
					};
				}.start();

			} else if (UsbMessage.MESSAGEACTION_MICROPHONE_STOPRECORDING
					.equals(messageAction)) {
 				recorder.stopRecord();
/*
 				sendMsgString(UsbMessage.MESSAGETYPE_MICROPHONE,
						UsbMessage.MESSAGEACTION_MICROPHONE_STOPRECORDING,
						UsbMessage.MESSAGESTATUS_OK, "", "");
						*/
 				
 				
			} else if (UsbMessage.MESSAGEACTION_MICROPHONE_PLAYRECORDING
					.equals(messageAction)) {
				recorder.playMusic();
				
				new Thread(){
					public void run() {
						
						try {
							Thread.sleep(1000*7);
						} catch (Exception e) {
							// TODO: handle exception
						}
						
						if (UsbMessage.MICROPHONE_RIGHT.equals(micphone_type)) {
							
							sendMsgString(UsbMessage.MESSAGETYPE_MICROPHONE,
									UsbMessage.MESSAGEACTION_MICROPHONE_PLAYRECORDINGEND,
									UsbMessage.MESSAGESTATUS_OK, UsbMessage.MICROPHONE_RIGHT, "");
							
							micphone_type=null;
							
						}else if (UsbMessage.MICROPHONE_LEFT.equals(micphone_type)) {
							sendMsgString(UsbMessage.MESSAGETYPE_MICROPHONE,
									UsbMessage.MESSAGEACTION_MICROPHONE_PLAYRECORDINGEND,
									UsbMessage.MESSAGESTATUS_OK, UsbMessage.MICROPHONE_LEFT, "");
							
						}
						
					};
				}.start();
				
			}

		} else if (UsbMessage.MESSAGETYPE_WIFI.equals(messageType)) {
			if (UsbMessage.MESSAGEACTION_WIFI_SEARCH.equals(messageAction)) {

				myWifiAdmin.openWifi();
				myWifiAdmin.startScan();
				List<ScanResult> list = myWifiAdmin.getWifiList();
				StringBuffer stringBuffer = new StringBuffer();

				for (int i = 0; i < list.size(); i++) {
					ScanResult scanResult = list.get(i);
					stringBuffer.append(scanResult.SSID);
					if (i != list.size() - 1) {
						stringBuffer.append(UsbMessage.SEPARATOR);
					}
				}

				sendMsgString(UsbMessage.MESSAGETYPE_WIFI,
						UsbMessage.MESSAGEACTION_WIFI_SEARCH,
						UsbMessage.MESSAGESTATUS_OK, stringBuffer.toString(),
						"");

			} else if (UsbMessage.MESSAGEACTION_WIFI_CONNECTED
					.equals(messageAction)) {

				myWifiAdmin.addNetwork(arg0, arg1, WifiAdmin.TYPE_WPA);
				String status = myWifiAdmin.getAddNetStatus() ? UsbMessage.MESSAGESTATUS_OK
						: UsbMessage.MESSAGESTATUS_FAIL;
				List<ScanResult> list = myWifiAdmin.getWifiList();

				String level = "100000";
				for (ScanResult scanResult : list) {
					if (arg0.equals(scanResult.SSID)) {
						level = scanResult.level + "";
						break;
					}
				}

				sendMsgString(UsbMessage.MESSAGETYPE_WIFI,
						UsbMessage.MESSAGEACTION_WIFI_CONNECTED, "", status,
						level);

			}

		} else if (UsbMessage.MESSAGETYPE_BUTTON.equals(messageType)) {
			
			
			
		}

	}
	
	private void deleteDatafile(){
		
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& !!!!!" );
		try {
			Runtime.getRuntime().exec("rm -rf  /data/misc/wifi/wpa_supplicant.conf"); 
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& "+e);
		}
		
	}
	
	
	@Override
	public void recorderEndPlayingAction() {
		// TODO Auto-generated method stub
		
		/*
		if (UsbMessage.MICROPHONE_RIGHT.equals(micphone_type)) {
			
			sendMsgString(UsbMessage.MESSAGETYPE_MICROPHONE,
					UsbMessage.MESSAGEACTION_MICROPHONE_PLAYRECORDINGEND,
					UsbMessage.MESSAGESTATUS_OK, UsbMessage.MICROPHONE_RIGHT, "");
			
			micphone_type=null;
			
		}else if (UsbMessage.MICROPHONE_LEFT.equals(micphone_type)) {
			sendMsgString(UsbMessage.MESSAGETYPE_MICROPHONE,
					UsbMessage.MESSAGEACTION_MICROPHONE_PLAYRECORDINGEND,
					UsbMessage.MESSAGESTATUS_OK, UsbMessage.MICROPHONE_LEFT, "");
			
		}
		*/
		
		
	}

	class MyWifiAdmin extends WifiAdmin {

		public MyWifiAdmin(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Intent myRegisterReceiver(BroadcastReceiver receiver,
				IntentFilter filter) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void myUnregisterReceiver(BroadcastReceiver receiver) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onNotifyWifiConnected() {
			// TODO Auto-generated method stub
			/*
			 * sendMsgString(UsbMessage.MESSAGETYPE_WIFI,
			 * UsbMessage.MESSAGEACTION_WIFI_CONNECTED,
			 * UsbMessage.MESSAGESTATUS_OK, "", "");
			 */

		}

		@Override
		public void onNotifyWifiConnectFailed() {
			// TODO Auto-generated method stub
			/*
			 * sendMsgString(UsbMessage.MESSAGETYPE_WIFI,
			 * UsbMessage.MESSAGEACTION_WIFI_CONNECTED,
			 * UsbMessage.MESSAGESTATUS_FAIL, "", "");
			 */

		}

	}

	/*
	 * private void revDoAction(String msgType, String msgValue) {
	 * 
	 * 
	 * if (TEST_TYPE_USB.equals(msgType)) { sendMsgString(msgType,
	 * MSG_VALUE_ON);
	 * 
	 * } else if (TEST_TYPE_SPEAKER.equals(msgType)) { // playMusic();
	 * 
	 * sendAction(null);
	 * 
	 * } else if (TEST_TYPE_FM.equals(msgType)) { if
	 * (MSG_VALUE_ON.equals(msgValue)) { turnOnFM(); } else if
	 * (MSG_VALUE_OFF.equals(msgValue)) { turnOffFM(); }
	 * 
	 * } else if (TEST_TYPE_WIFI.equals(msgType)) { startFMService(); }
	 * 
	 * 
	 * }
	 */
	private void sendMsgString(String messageType, String messageAction,
			String messageStatus, String arg0, String arg1) {

		CAnyValue caAnyValue = new CAnyValue();
		caAnyValue.put(UsbMessage.MESSAGETYPE, messageType);
		caAnyValue.put(UsbMessage.MESSAGEACTION, messageAction);
		caAnyValue.put(UsbMessage.MESSAGESTATUS, messageStatus);
		caAnyValue.put(UsbMessage.ARG0, arg0 != null ? arg0 : "");
		caAnyValue.put(UsbMessage.ARG1, arg1 != null ? arg1 : "");
		String sBuf = caAnyValue.encodeJSON();

		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!! encodeJSON" + sBuf);
		transfer.sendMsg(sBuf);

	}

	private void playHighMusic() {
		playMusic("vitas.mp3");
	}

	private void playLowMusic() {

		playMusic("pingfanzhilu.mp3");
	}

	private void stopMusic() {
		if (audio.isPlaying()) {
			audio.stop();
		}
	}

	private void playMusic(String name) {
		stopMusic();
		audio.play(name);
	}

	private void startFMService() {

		addFMActionBroadcast();

		sendBroadcastWithName(FmBroadcastReceiver.ACTION);
	}

	private void turnOnFM() {
		sendBroadcastWithName(FMService.FM_STAR);
	}

	private void turnOffFM() {
		sendBroadcastWithName(FMService.FM_STOP);
		sendBroadcastWithName(FMService.FM_STOP_AUDIO_NOMAL);
	}

	private void sendBroadcastWithName(String name) {
		Intent intent = new Intent(name);
		sendBroadcast(intent);
		System.out.println(" sendBroadcastWithName !!!" + name);
	}

	private void addFMActionBroadcast() {
		FmBroadcastReceiver broadcastReceiver = new FmBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(FmBroadcastReceiver.ACTION); // 只有持有相同的action的接受者才能接收此广播
		registerReceiver(broadcastReceiver, filter);

	}
	/**
	 * 说客 APP 版本 
	 * 注意包名必须一致
	 * @return
	 */
	private String getPackVersionInfo() {
		PackageManager packageManager = MainActivity.this.getPackageManager();
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
		for (PackageInfo packageInfo : packageInfos) {
			// packageInfo.versionName
//			System.out.println("=================="+packageInfo.versionName);
			
//			System.out.println("================="+packageInfo.packageName+"======"+packageInfo.versionName);
			
			if (APK_PACKNAME.equals(packageInfo.packageName)) {
				
				
			 
					System.out.println("==================="+packageInfo.toString());
				 
				return packageInfo.versionName;
			}
		
		}
		
		return "";
	}

	/*
	 * @Override public boolean onKeyLongPress(int keyCode, KeyEvent event) { //
	 * TODO Auto-generated method stub // if (keyCode==KeyEvent.KEYCODE_Q) {
	 * System
	 * .out.println("--------------------my eyes are opend -  onKeyLongPress ！！！！"
	 * + keyCode + "     event" + event.getAction());
	 * 
	 * if (keyCode == 24) {
	 * 
	 * sendMsgString(UsbMessage.MESSAGETYPE_BUTTON,
	 * UsbMessage.MESSAGEACTION_BUTTON_LONGCLICK, UsbMessage.MESSAGESTATUS_OK,
	 * "", "");
	 * 
	 * 
	 * flag = false; flag2 = true; return true; }
	 * 
	 * // return false; return super.onKeyLongPress(keyCode, event); }
	 * 
	 * @Override public boolean onKeyUp(int keyCode, KeyEvent event) { // TODO
	 * Auto-generated method stub // if (keyCode == KeyEvent.KEYCODE_Q) {
	 * 
	 * System.out.println("--------------------my eyes are opend -  onKeyUp ！！！！"
	 * + keyCode + "     event" + event.getAction());
	 * 
	 * if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
	 * 
	 * 
	 * if (!isPress) { sendMsgString(UsbMessage.MESSAGETYPE_BUTTON,
	 * UsbMessage.MESSAGEACTION_BUTTON_CLICK, UsbMessage.MESSAGESTATUS_OK, "",
	 * ""); }
	 * 
	 * isPress=true;
	 * 
	 * event.startTracking(); if (flag) { Log.d("Test", "Short"); } flag = true;
	 * flag2 = false; return true; }
	 * 
	 * return super.onKeyUp(keyCode, event); // return false; }
	 * 
	 * // onKeyUp ，onKeyDown keyCode==24 表示，网络板块上的按键
	 * 
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { // TODO
	 * Auto-generated method stub // if (keyCode==KeyEvent.KEYCODE_Q) {
	 * System.out
	 * .println("--------------------my eyes are opend -  onKeyDown ！！！！" +
	 * keyCode + "     event" + event.getAction());
	 * 
	 * if (keyCode == 24) {
	 * 
	 * 
	 * 
	 * 
	 * event.startTracking(); if (flag2 == true) { flag = false; } else { flag =
	 * true; flag2 = false; }
	 * 
	 * return true;
	 * 
	 * }
	 * 
	 * return super.onKeyDown(keyCode, event); // return false; }
	 * 
	 * 
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
	 * 
	 * System.out.println("------------------------    onKeyDown"); if (keyCode
	 * == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
	 * { event.startTracking(); return true; } return super.onKeyDown(keyCode,
	 * event); }
	 * 
	 * @Override public boolean onKeyLongPress(int keyCode, KeyEvent event) {
	 * 
	 * System.out.println("---------------------       onKeyLongPress");
	 * 
	 * if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
	 * 
	 * return true; } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
	 * 
	 * return true; } return super.onKeyLongPress(keyCode, event); }
	 * 
	 * @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
	 * 
	 * System.out.println("---------------   onKeyUp "); if ((event.getFlags() &
	 * KeyEvent.FLAG_CANCELED_LONG_PRESS) == 0) { if (keyCode ==
	 * KeyEvent.KEYCODE_VOLUME_UP) { return true; } else if (keyCode ==
	 * KeyEvent.KEYCODE_VOLUME_DOWN) { return true; } } return
	 * super.onKeyUp(keyCode, event); }
	 */

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		audio.stop();

	}

	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		System.out.println("---------------------!!  onKeyDown");

		sendMsgString(UsbMessage.MESSAGETYPE_BUTTON,
					UsbMessage.MESSAGEACTION_BUTTON_CLICK,
					UsbMessage.MESSAGESTATUS_OK, "", "");
		/*
		pressCount++;
		if (pressCount==50) {
			sendMsgString(UsbMessage.MESSAGETYPE_BUTTON,
					UsbMessage.MESSAGEACTION_BUTTON_LONGCLICK,
					UsbMessage.MESSAGESTATUS_OK, "", "");
		}
		*/
		
		return true;
//		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		System.out.println("---------------------!!  onKeyLongPress");
		return super.onKeyLongPress(keyCode, event);
//		return false;
		
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		System.out.println("---------------------!!  onKeyUp");
		return super.onKeyUp(keyCode, event);
	}



}

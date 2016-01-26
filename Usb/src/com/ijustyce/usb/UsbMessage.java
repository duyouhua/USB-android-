package com.ijustyce.usb;

public class UsbMessage {
	
	/**
	 * 消息成功或失败标致
	 */
	public final static String MESSAGESTATUS="messagestatus";
	public final static String MESSAGESTATUS_OK="messagestatus_ok";
	public final static String MESSAGESTATUS_FAIL="messagestatus_fail";
	public final static String ARG0="arg0";
	public final static String ARG1="arg1";
	
	
	
	public final static String SEPARATOR="🍎";
	
	
	public final static String MICROPHONE_RIGHT="microphone_right";
	public final static String MICROPHONE_LEFT="microphone_left";
	
	/**
	 * 消息类型标示
	 */
	public final static String MESSAGETYPE="messagetype";
	public final static String MESSAGETYPE_USB="messagetype_usb";
	public final static String MESSAGETYPE_VERSION="messagetype_version";
	public final static String MESSAGETYPE_SPEAKER="messagetype_speaker";
	public final static String MESSAGETYPE_FM="messagetype_fm";
	public final static String MESSAGETYPE_MICROPHONE="messagetype_microphone";
	public final static String MESSAGETYPE_WIFI="messagetype_wifi";
	public final static String MESSAGETYPE_BUTTON="messagetype_button";
	
	/**
	 * 消息动作标示
	 */
	public final static String MESSAGEACTION="messageaction";
	public final static String MESSAGEACTION_USB_CONNECTEDUSB="messageaction_usb_connectedusb";
	public final static String MESSAGEACTION_SPEAKER_HIGH="messageaction_speaker_high";
	public final static String MESSAGEACTION_SPEAKER_LOW="messageaction_speaker_low";
	public final static String MESSAGEACTION_SPEAKER_STOP="messageaction_speaker_stop";
	public final static String MESSAGEACTION_FM_STARTORCHANGE="messageaction_fm_startorchange";
	public final static String MESSAGEACTION_FM_STOP="messageaction_fm_stop";
	public final static String MESSAGEACTION_MICROPHONE_STARTRECORDING="messageaction_microphone_startrecording";
	public final static String MESSAGEACTION_MICROPHONE_STOPRECORDING="messageaction_microphone_stoprecording";
	public final static String MESSAGEACTION_MICROPHONE_PLAYRECORDING="messageaction_microphone_playrecording";
	public final static String MESSAGEACTION_MICROPHONE_PLAYRECORDINGEND="messageaction_microphone_playrecordingend";
	public final static String MESSAGEACTION_WIFI_SEARCH="messageaction_wifi_search";
	public final static String MESSAGEACTION_WIFI_CONNECTED="messageaction_wifi_connected";
	public final static String MESSAGEACTION_BUTTON_CLICK="messageaction_button_click";
	public final static String MESSAGEACTION_BUTTON_LONGCLICK="messageaction_button_longclick";
	
	
	
}

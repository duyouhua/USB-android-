#include <jni.h>
#include <string.h>

#include <stdio.h>
#include <android/log.h>
#include <fcntl.h>
#include <linux/i2c.h>
#include <memory.h>
#include <malloc.h>

#define  LOG_TAG    "i2c"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

struct i2c_rdwr_ioctl_data {
        struct i2c_msg __user *msgs;    /* pointers to i2c_msgs */
        __u32 nmsgs;                    /* number of i2c_msgs */
};

JNIEXPORT jstring* Java_com_iraylink_wifibox_fm_I2c_jniTest(JNIEnv* env, jobject thiz)

{
	return (*env)->NewStringUTF(env, "hello from jni by  ndkTest!");
}

JNIEXPORT jint JNICALL Java_com_iraylink_wifibox_fm_I2c_FMcontrol(JNIEnv * env,
		jobject obj, jstring mode, jint argv) {
	char devs[16];
	const jbyte *str;

	str = (*env)->GetStringUTFChars(env, mode, NULL);
	if (str == NULL) {
		LOGE("Can't get mode name!");
		return -1;
	}
	sprintf(devs, "%s", str);
	LOGE("Set FM mode %s, argv %d", devs, argv);
	(*env)->ReleaseStringUTFChars(env, mode, str);

	if (!strcasecmp(devs, "qn1")) {
	    QN8035_Read_ID();
	}
	else if (!strcasecmp(devs, "qn2")) {
		init_QN8035();
	}
	else if (!strcasecmp(devs, "qn3")) {
		set_fre_QN8035(argv);
	}
	else if (!strcasecmp(devs, "qn4")) {
		QND_SetVol(argv);
	}
	else if (!strcasecmp(devs, "test")) {
		LOGE("Set FM mode test");
		system("echo 0 > /data/test");
	}

	return 1;
}


JNIEXPORT jint JNICALL Java_com_iraylink_wifibox_fm_I2c_read(JNIEnv * env,
		jobject obj, jstring file, jint slaveAddr, jint regAddr, jintArray bufArr,
		jint len) {
	char devs[16];
	const jbyte *str;

	str = (*env)->GetStringUTFChars(env, file, NULL);
	if (str == NULL) {
		LOGI("Can't get file name!");
		return -1;
	}
	sprintf(devs, "%s", str);
	LOGI("will open i2c device node %s", devs);
	(*env)->ReleaseStringUTFChars(env, file, str);


	jint *bufInt;
	char *bufByte;
	int res = 0, i = 0, j = 0;

	if (len <= 0) {
		LOGE("I2C: buf len <=0");
		goto err0;
	}

	bufInt = (jint *) malloc(len * sizeof(int));
	if (bufInt == 0) {
		LOGE("I2C: nomem");
		goto err0;
	}
	bufByte = (char*) malloc(len);
	if (bufByte == 0) {
		LOGE("I2C: nomem");
		goto err1;
	}

	(*env)->GetIntArrayRegion(env, bufArr, 0, len, bufInt);

	LOGE("Read slave before: %x reg %x, len:%d\n", slaveAddr, regAddr, len);
	memset(bufByte, '\0', len);

	res = i2c_read_reg(devs, bufByte, slaveAddr, regAddr, len);
	if (res <= 0) {
		LOGE("I2C: Can't set slave address");
		goto err2;
	}

	for (i = 0; i < len; i++)
			bufInt[i] = bufByte[i];

	(*env)->SetIntArrayRegion(env, bufArr, 0, len, bufInt);

	LOGE("Read slave %x reg %x, read value:%x\n", slaveAddr, regAddr, bufInt[0]);

	free(bufByte);
	free(bufInt);

	return 1;

	err2: free(bufByte);
	err1: free(bufInt);
	err0: return -1;
}

JNIEXPORT jint JNICALL Java_com_iraylink_wifibox_fm_I2c_write(JNIEnv *env,
		jobject obj, jstring file, jint slaveAddr, jint regAddr,
		jintArray bufArr, jint len) {
	char devs[16];
	const jbyte *str;

	str = (*env)->GetStringUTFChars(env, file, NULL);
	if (str == NULL) {
		LOGI("Can't get file name!");
		return -1;
	}
	sprintf(devs, "%s", str);
	LOGI("will open i2c device node %s", devs);
	(*env)->ReleaseStringUTFChars(env, file, str);


	jint *bufInt;
	char *bufByte;
	int res = 0, i = 0, j = 0;

	if (len <= 0) {
		LOGE("I2C: buf len <=0");
		goto err0;
	}

	bufInt = (jint *) malloc(len * sizeof(int));
	if (bufInt == 0) {
		LOGE("I2C: nomem");
		goto err0;
	}
	bufByte = (char*) malloc(len + 1);
	if (bufByte == 0) {
		LOGE("I2C: nomem");
		goto err1;
	}

	(*env)->GetIntArrayRegion(env, bufArr, 0, len, bufInt);
	for (i = 0; i < len; i++)
		bufByte[i] = bufInt[i];

	LOGE("Write slave before %x reg %x, write value:%x\n, len:%d", slaveAddr, regAddr, bufByte[0], len);
	res = i2c_write_reg(devs, bufByte, slaveAddr, regAddr, len);
	if (res <= 0) {
		LOGE("I2C: Can't set slave address");
		goto err2;
	}

	LOGE("Write slave %x reg %x, write value:%x\n", slaveAddr, regAddr, bufByte[0]);
	free(bufByte);
	free(bufInt);

	return 1;

	err2: free(bufByte);
	err1: free(bufInt);
	err0: return -1;
}


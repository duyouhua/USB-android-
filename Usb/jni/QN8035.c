/*--------------------------------------------------------------------------*/
/**@file     qn8035.c
   @brief    qn8035收音底层驱动
   @details
   @author
   @date   2011-11-24
   @note
*/
/*----------------------------------------------------------------------------*/
#include "QN8035.h"
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <strings.h>
#include <math.h>
#include <linux/i2c.h>
#include <android/log.h>

#define QN8035
#ifdef	QN8035

#define  LOG_TAG    "i2c"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

struct i2c_rdwr_ioctl_data {
    struct i2c_msg *msgs;
    unsigned int nmsgs;
};

#define AT(code)
#define __root

//if antenna match circuit is used a inductor，macro USING_INDUCTOR will be set to 1
#define USING_INDUCTOR				1

#define INVERSE_IMR						1

//if using san noise floor as CCA algorithm,macro SCAN_NOISE_FLOOR_CCA will be set to 1
#define SCAN_NOISE_FLOOR_CCA 	1
//if using pilot as CCA algorithm,macro PILOT_CCA will be set to 1
#define PILOT_CCA							1


//长天线配置:
u8 qnd_PreNoiseFloor,qnd_NoiseFloor;
int qn8035_online;
//短天线配置:
//UINT8 _xdata qnd_PreNoiseFloor = 35,qnd_NoiseFloor = 35;

/*----------------------------------------------------------------------------*/
/**@brief    QN8035 读寄存器函数
   @param    addr：寄存器地址
   @return   无
   @note     u8 QND_ReadReg(u8 addr)
*/
/*----------------------------------------------------------------------------*/
#if 0
u8 QND_ReadReg(u8 addr) AT(QN0835_CODE)
{
	u8  byte;

    iic_busy = 1;
    iic_start();                    //I2C启动
    iic_sendbyte(0x20);             //写命令
    iic_sendbyte(addr);         //写地址
    iic_start();                    //写转为读命令，需要再次启动I2C
    iic_sendbyte(0x21);             //读命令
    byte = iic_revbyte(1);
    iic_stop();                     //I2C停止
    iic_busy = 0;
    return  byte;
}


u8 sd_iic_readQN(u8 addr) AT(QN0835_CODE)
{
     u8 p_status;
     u8 res;

    /*Wait SD I/O release*/
    if (device_active == DEVICE_SDMMCA)
        p_status = pause_decode(0);
    sdmmca_force_idle();
    sdmmcb_force_idle();
    while(check_sd_controller());   //根据SD ID等待控制器繁忙
    IO_MC0 &= ~BIT(3);

    /*IIC Communicate*/
    res = QND_ReadReg(addr);

    /*Reset SD I/O*/
    IO_MC0 |= BIT(3);
    unlock_cmd_chk();
    if ((p_status == MAD_PLAY)&&(device_active == DEVICE_SDMMCA))
        start_decode();

    return res;
}
#endif

static int qnd_i2c_write(unsigned char *i2c_buf, int len)
{
	int i=0;
    int i2c_fd = open("/dev/i2c-0", 0, O_RDWR);
    if (i2c_fd < 0) {
        LOGE("ERR: device open failed!");
        return -1;
    }

    int err;
	//unsigned char i2c_buf[256];
    struct i2c_rdwr_ioctl_data data;
    struct i2c_msg msg;
	//i2c_buf[0]=0x06;
	//i2c_buf[1]=0x07;

    data.nmsgs = 1;
    data.msgs  = &msg;
    msg.addr  = 0x20>>1;
    msg.flags = !I2C_M_RD;
    msg.len   = len;
    msg.buf   = i2c_buf;

    err = ioctl (i2c_fd, I2C_RDWR, (unsigned long)&data);
    if (err < 0) {
        LOGE("ERR: I2C_RDWR => %d\n", err);
    }

	for(i=0; i<len; i++) {
		LOGE("%02x ", i2c_buf[i]);
	}
	LOGE("\n");
	if (i2c_fd > 0) {
		close(i2c_fd);
		i2c_fd = -1;
	}
	return 0;
}

static int qnd_i2c_read(unsigned char *i2c_buf, int len)
{
	int i=0;

    int i2c_fd = open("/dev/i2c-0", 0, O_RDWR);
    if (i2c_fd < 0) {
        LOGE("ERR: device open failed!");
        return -1;
    }

    int err;
	//int len;
	//unsigned char i2c_buf[256];
    struct i2c_rdwr_ioctl_data data;
    struct i2c_msg msg[2];
		data.nmsgs = 2;
    data.msgs  = msg;

    msg[0].flags = !I2C_M_RD;
    msg[0].addr  = 0x20>>1;
    msg[0].len   = 1;      //data address
    msg[0].buf   = i2c_buf;

    msg[1].flags = I2C_M_RD;
    msg[1].addr  = 0x20>>1;
    msg[1].len   = len-1;
    msg[1].buf   = i2c_buf+1;


    err = ioctl (i2c_fd, I2C_RDWR, (unsigned long)&data);
    if (err < 0) {
        LOGE("ERR: I2C_RDWR => %d\n", err);
    }

	for(i=0; i<len; i++) {
		LOGE("%02x ", i2c_buf[i]);
	}
	LOGE("\n");

	if (i2c_fd > 0) {
		close(i2c_fd);
		i2c_fd = -1;
	}
	return 0;


}

/*----------------------------------------------------------------------------*/
/**@brief    QN8035 写寄存器函数
   @param    addr：寄存器地址 data：写入数据
   @return   无
   @note     void QND_WriteReg(u8 addr,u8 data)
*/
/*----------------------------------------------------------------------------*/
void QND_WriteReg(u8 addr,u8 data) AT(QN0835_CODE)
{
	//app_IIC_write(0x20, addr, &data, 1);
	unsigned char buf[32];
	int len = 0;

  buf[0] = addr;
  buf[1] = data;
  len = 2;
	qnd_i2c_write(buf, len);
}

u8 QN_IIC_read(u8 addr) AT(QN0835_CODE)
{
	unsigned char buf[32];
	int len = 0;

  buf[0] = addr;
  len = 2;
	qnd_i2c_read(buf, len);
	return buf[1];
}

/**********************************************************************
void QNF_SetCh(UINT16 start,UINT16 stop,UINT8 step)
**********************************************************************
Description: set channel frequency
Parameters:
    freq:  channel frequency to be set,frequency unit is 10KHZ
Return Value:
    None
**********************************************************************/
void QNF_SetCh(u16 start,u16 stop,u8 step) AT(QN0835_CODE)
{
    u8 temp;

	 start = FREQ2CHREG(start);
	 stop = FREQ2CHREG(stop);
	//writing lower 8 bits of CCA channel start index
	QND_WriteReg(CH_START, (u8)start);
	//writing lower 8 bits of CCA channel stop index
	QND_WriteReg(CH_STOP, (u8)stop);
	//writing lower 8 bits of channel index
	QND_WriteReg(CH, (u8)start);
	//writing higher bits of CCA channel start,stop and step index
	temp = (u8) ((start >> 8) & CH_CH);
	temp |= ((u8)(start >> 6) & CH_CH_START);
	temp |= ((u8) (stop >> 4) & CH_CH_STOP);
	temp |= (step << 6);
	QND_WriteReg(CH_STEP, temp);
}


/**********************************************************************
int QND_Delay()
**********************************************************************
Description: Delay for some ms, to be customized according to user
             application platform

Parameters:
        ms: ms counts
Return Value:
        None

**********************************************************************/
void delay_n10ms(u16 ms)
{
    unsigned int us = ms*1000*100;
    usleep(us);
}

void QND_Delay(u16 ms) AT(QN0835_CODE)
{
//	Delay(25*ms);   		//rc
	//delay16(1500*ms);
    unsigned int us = ms*1000*100;
    usleep(us);
}

void qn8035_mute1(int On) AT(QN0835_CODE)
{
	QND_WriteReg(0x4a,  On?0x30:0x10);
  	//QND_WriteReg(0x4a, 0x30);
}
#define QNF_SetMute(x) 	QN8035_mute(x)
/**********************************************************************
void QNF_SetMute(u8 On)
**********************************************************************
Description: set register specified bit

Parameters:
On:        1: mute, 0: unmute
Return Value:
None
**********************************************************************/
void QN8035_mute(u8 On) AT(QN0835_CODE)
{
	QND_WriteReg(REG_DAC, On?0x1B:0x10);
	//QND_WriteReg(0x4a,  On?0x30:0x10);
}

#if SCAN_NOISE_FLOOR_CCA
 /***********************************************************************
Description: scan a noise floor from 87.5M to 108M by step 200K
Parameters:
Return Value:
 1: scan a noise floor successfully.
 0: chip would not normally work.
**********************************************************************/
u8 QND_ScanNoiseFloor(u16 start,u16 stop) AT(QN0835_CODE)
{
	u8 regValue;
	u8 timeOut = 255; //time out is 2.55S

	QND_WriteReg(CCA_SNR_TH_1,0x00);
	QND_WriteReg(CCA_SNR_TH_2,0x05);
	QND_WriteReg(0x40,0xf0);
	//config CCS frequency rang by step 200KHZ
	QNF_SetCh(start,stop,2);
/*
	QND_WriteReg(CH_START,0x26);
	QND_WriteReg(CH_STOP,0xc0);
	QND_WriteReg(CH_STEP,0xb8);
*/
	//enter CCA mode,channel index is decided by internal CCA
	QND_WriteReg(SYSTEM1,0x12);
    while(1)
    {
        regValue = QN_IIC_read(SYSTEM1);
        //if it seeks a potential channel, the loop will be quited
        if((regValue & CHSC) == 0) break;
        delay_n10ms(1);   //delay 10ms
        //if it was time out,chip would not normally work.
        if((timeOut--) == 0)
        {
        		QND_WriteReg(0x40,0x70);
        		return 0;
        }
    }
	QND_WriteReg(0x40,0x70);
	qnd_NoiseFloor = QN_IIC_read(0x3f);
	if(((qnd_PreNoiseFloor-qnd_NoiseFloor) > 2) ||((qnd_NoiseFloor-qnd_PreNoiseFloor) > 2))
	{
		qnd_PreNoiseFloor = qnd_NoiseFloor;
	}
	//TRACE("NF:%d,timeOut:%d\n",qnd_NoiseFloor,255-timeOut);
	return 1;
}
#endif
/**********************************************************************
void QNF_SetRegBit(u8 reg, u8 bitMask, u8 data_val)
**********************************************************************
Description: set register specified bit

Parameters:
    reg:        register that will be set
    bitMask:    mask specified bit of register
    data_val:    data will be set for specified bit
Return Value:
  None
***********************************************************************/
void QND_RXSetTH(void) AT(QN0835_CODE)
{
	u8 rssi_th;

	rssi_th = qnd_PreNoiseFloor + 8-28 ;   //10	越小台多0-
	///increase reference PLL charge pump current.
	QND_WriteReg(REG_REF,0x7a);
	//NFILT program is enabled
	QND_WriteReg(0x1b,0x78);
	//using Filter3
	QND_WriteReg(CCA1,0x75);
	//setting CCA IF counter error range value(768).
	QND_WriteReg(CCA_CNT2,0x03);  //0x01	  大台多 1-5
#if PILOT_CCA
	QND_WriteReg(PLT1,0x00);
#endif
	//selection the time of CCA FSM wait SNR calculator to settle:20ms
	//0x00:	    20ms(default)
	//0x40:	    40ms
	//0x80:	    60ms
	//0xC0:	    100m
	//    QNF_SetRegBit(CCA_SNR_TH_1 , 0xC0, 0x00);
	//selection the time of CCA FSM wait RF front end and AGC to settle:20ms
	//0x00:     10ms
	//0x40:     20ms(default)
	//0x80:     40ms
	//0xC0:     60ms
	//    QNF_SetRegBit(CCA_SNR_TH_2, 0xC0, 0x40);
	//    QNF_SetRegBit(CCA, 30);  //setting CCA RSSI threshold is 30
	QND_WriteReg(CCA_SNR_TH_2,0x85);
	QND_WriteReg(CCA, (QN_IIC_read(CCA)&0xc0)|rssi_th);
#if PILOT_CCA
	QND_WriteReg(CCA_SNR_TH_1,0x88); //setting SNR threshold for CCA
#else
	QND_WriteReg(CCA_SNR_TH_1,8); //小台多 8-12 //setting SNR threshold for CCA  9
#endif
}



/*----------------------------------------------------------------------------*/
/**@brief    QN0835 初始化
   @param    无
   @return   无
   @note     void init_QN8035(void)
*/
/*----------------------------------------------------------------------------*/
__root void init_QN8035(void) AT(QN0835_CODE)
{
	/*Gobal variable initial*/
    qnd_PreNoiseFloor = 40;
    qnd_NoiseFloor = 40;

    QND_WriteReg(0x00, 0x81);
	delay_n10ms(1);

	/*********User sets chip working clock **********/
	//Following is where change the input clock wave type,as sine-wave or square-wave.
	//default set is 32.768KHZ square-wave input.
#if !defined FUNC_P05_32768_EN
	QND_WriteReg(0x01, QND_SINE_WAVE_CLOCK);
#endif
	//QND_WriteReg(0x01,QND_SINE_WAVE_CLOCK);
	//Following is where change the input clock frequency.
	//QND_WriteReg(XTAL_DIV0, QND_XTAL_DIV0);
	//QND_WriteReg(XTAL_DIV1, QND_XTAL_DIV1);
	//QND_WriteReg(XTAL_DIV2, QND_XTAL_DIV2);
	/********User sets chip working clock end ********/

	QND_WriteReg(0x54, 0x47);//mod PLL setting
	//select SNR as filter3,SM step is 2db
	QND_WriteReg(0x19, 0xc4);
	QND_WriteReg(0x33, 0x9e);//set HCC and SM Hystersis 5db
	QND_WriteReg(0x2d, 0xd6);//notch filter threshold adjusting
	QND_WriteReg(0x43, 0x10);//notch filter threshold enable
	QND_WriteReg(0x47,0x39);
	//QND_WriteReg(0x57, 0x21);//only for qn8035B test
	//enter receiver mode directly
	QND_WriteReg(0x00, 0x11);
	//Enable the channel condition filter3 adaptation,Let ccfilter3 adjust freely
	QND_WriteReg(0x1d,0xa9);
	QND_WriteReg(0x4f, 0x40);//dsiable auto tuning
	QND_WriteReg(0x34,SMSTART_VAL); ///set SMSTART
	QND_WriteReg(0x35,SNCSTART_VAL); ///set SNCSTART
	QND_WriteReg(0x36,HCCSTART_VAL); ///set HCCSTART
	QNF_SetMute(0);
}


/*----------------------------------------------------------------------------*/
/**@brief    关闭 QN0835的电源
   @param    无
   @return   无
   @note     void QN8035_powerdown(void)
*/
/*----------------------------------------------------------------------------*/
__root void QN8035_powerdown(void) AT(QN0835_CODE)
{
//	QND_SetSysMode(0);
    QND_WriteReg(SYSTEM1, 0x20);
}
/**********************************************************************
void QND_TuneToCH(UINT16 ch)
**********************************************************************
Description: Tune to the specific channel.
Parameters:
	ch:Set the frequency (10kHz) to be tuned,
eg: 101.30MHz will be set to 10130.
Return Value:
	None
**********************************************************************/
void QND_TuneToCH(u16 ch) AT(QN0835_CODE)
{
	u8 reg;

	//increase reference PLL charge pump current.
	QND_WriteReg(REG_REF,0x7a);

	/********** QNF_RXInit ****************/
	QND_WriteReg(0x1b,0x70);	//Let NFILT adjust freely
	QND_WriteReg(0x2c,0x52);
	QND_WriteReg(0x45,0x50);	//Set aud_thrd will affect ccfilter3's tap number
	QND_WriteReg(0x40,0x70);	//set SNR as SM,SNC,HCC MPX
	QND_WriteReg(0x41,0xca);
	/********** End of QNF_RXInit ****************/
#if INVERSE_IMR
	reg = QN_IIC_read(CCA)&~0x40;
	if((ch == 9340)||(ch == 9390)||(ch == 9530)||(ch == 9980)||(ch == 10480))
	{
		reg |= 0x40;	// inverse IMR.
	}
	else
	{
		reg &= ~0x40;
	}
	QND_WriteReg(CCA, reg);
#endif
	QNF_SetMute(1);
	QNF_SetCh(ch,ch,1);
	//enable CCA mode with user write into frequency
	QND_WriteReg(0x00, 0x13);
#if USING_INDUCTOR
	//Auto tuning
	QND_WriteReg(0x4f, 0x80);
	reg = QN_IIC_read(0x4f);
	reg >>= 1;
	QND_WriteReg(0x4f, reg);
#endif
	///avoid the "POP" noise.
	//QND_Delay(CH_SETUP_DELAY_TIME);
	//QND_Delay(500);
	delay_n10ms(50);
	///decrease reference PLL charge pump current.
	QND_WriteReg(REG_REF,0x70);
	QNF_SetMute(0);
}

__root int QND_Auto(u16 freq) AT(QN0835_CODE)
{
	u8 regValue;
	u8 timeOut;
	u8 isValidChannelFlag;
	u8 isValidScan;
//	UINT8 snr,snc,temp1,temp2;

	//Auto tuning
	QND_WriteReg(0x00, 0x11);
	QND_WriteReg(0x4f, 0x80);
	regValue = QN_IIC_read(0x4f);
	regValue = (regValue >> 1);
	QND_WriteReg(0x4f, regValue);

	//entering into RX mode and CCA mode,channels index decide by CCA.
	QND_WriteReg(0x00, 0x12);
	timeOut = 100;  // time out is 100ms
	while(1)
	{
		regValue = QN_IIC_read(SYSTEM1);        
		//if it seeks a potential channel, the loop will be quited     
		if((regValue & CHSC) == 0) break;
		delay_n10ms(1);   //delay 5ms
		//if it was time out,chip would not normally work.
		if((timeOut--) == 0) return 0;
	}   
	//reading out the rxcca_fail flag of RXCCA status
	isValidChannelFlag = (QN_IIC_read(STATUS1) & RXCCA_FAIL ? 0:1);  
	LOGE("isValidChannelFlag is %d\n", isValidChannelFlag);
	if(isValidChannelFlag)
	{
		return 1;
	}
	return 0;
}

/*----------------------------------------------------------------------------*/
/**@brief    设置一个频点QN0835
   @param    fre 频点  875~1080
   @return   1：当前频点有台，0：当前频点无台
   @note     bool set_fre_QN8035(u16 freq)
*/
/*----------------------------------------------------------------------------*/
__root int QND_RXValidCH(u16 freq) AT(QN0835_CODE)
{
	u8 regValue;
	u8 timeOut;
	u8 isValidChannelFlag;
	u8 isValidScan;
//	UINT8 snr,snc,temp1,temp2;
#if PILOT_CCA
	u8 snr,readCnt,stereoCount=0;
#endif
	//freq = freq * 10;
	
	LOGE("freq is %d\n", freq);

#if SCAN_NOISE_FLOOR_CCA
	switch(freq)
	{
	case 8750:
		isValidScan = QND_ScanNoiseFloor(8750,8800);	
		QND_RXSetTH();
		break;
	case 8810:
		isValidScan = QND_ScanNoiseFloor(8810,9000);	
		QND_RXSetTH();
		break;
	case 9010:
		isValidScan = QND_ScanNoiseFloor(9010,9200);	
		QND_RXSetTH();
		break;
	case 9210:
		isValidScan = QND_ScanNoiseFloor(9210,9400);	
		QND_RXSetTH();
		break;
	case 9410:
		isValidScan = QND_ScanNoiseFloor(9410,9600);
		QND_RXSetTH();
		break;
	case 9610:
		isValidScan = QND_ScanNoiseFloor(9610,9800);
		QND_RXSetTH();
		break;
	case 9810:
		isValidScan = QND_ScanNoiseFloor(9810,10000);
		QND_RXSetTH();
		break;
	case 10010:
		isValidScan = QND_ScanNoiseFloor(10010,10200);
		QND_RXSetTH();
		break;
	case 10210:
		isValidScan = QND_ScanNoiseFloor(10210,10400);
		QND_RXSetTH();
		break;
	case 10410:
		isValidScan = QND_ScanNoiseFloor(10410,10600);
		QND_RXSetTH();
		break;
	case 10610:
		isValidScan = QND_ScanNoiseFloor(10610,10800);
		QND_RXSetTH();
		break;	
	default:
		//QND_Delay(350); 
		break;
	}
#endif
	LOGE("isValidScan is %d\n", isValidScan);
	
	//QNF_SetCh(freq,freq,1);
#if USING_INDUCTOR
	//Auto tuning
	QND_WriteReg(0x00, 0x11);
	QND_WriteReg(0x4f, 0x80);
	regValue = QN_IIC_read(0x4f);
	regValue = (regValue >> 1);
	QND_WriteReg(0x4f, regValue);
#endif 
	//entering into RX mode and CCA mode,channels index decide by CCA.
	QND_WriteReg(0x00, 0x12);
	timeOut = 100;  // time out is 100ms
	while(1)
	{
		regValue = QN_IIC_read(SYSTEM1);        
		//if it seeks a potential channel, the loop will be quited     
		if((regValue & CHSC) == 0) break;
		delay_n10ms(1);   //delay 5ms
		//if it was time out,chip would not normally work.
		if((timeOut--) == 0) return 0;
	}   
	//reading out the rxcca_fail flag of RXCCA status
	isValidChannelFlag = (QN_IIC_read(STATUS1) & RXCCA_FAIL ? 0:1);  
	LOGE("isValidChannelFlag is %d\n", isValidChannelFlag);
	if(isValidChannelFlag)
	{
		return 1;
#if PILOT_CCA
/*		delay_n10ms(10);
		snr = QN_IIC_read(SNR);
		if(snr> 25) return 1;
		for(readCnt=10;readCnt>0;readCnt--)
		{
			delay_n10ms(1);
			stereoCount += ((QN_IIC_read(STATUS1) & ST_MO_RX) ? 0:1);
			if(stereoCount >= 3) return 1;
		}*/
#else
		
#endif
	}
	return 0;
}	


/*----------------------------------------------------------------------------*/
/**@brief    设置一个频点QN0835
   @param    fre 频点  875~1080
   @return   1：当前频点有台，0：当前频点无台
   @note     bool set_fre_QN8035(u16 freq)
*/
/*----------------------------------------------------------------------------*/
__root int QND_AutoTuneCH(u16 freq) AT(QN0835_CODE)
{
	u8 regValue;
	u8 timeOut;
	u8 isValidChannelFlag;
	u8 isValidScan;
//	UINT8 snr,snc,temp1,temp2;
#if PILOT_CCA
	u8 snr,readCnt,stereoCount=0;
#endif
	//freq = freq * 10;
	
	LOGE("freq is %d\n", freq);

#if SCAN_NOISE_FLOOR_CCA
	if((freq>=8750)&&(freq<=10800)){
		isValidScan = QND_ScanNoiseFloor(freq,freq+200);	
		QND_RXSetTH();
	}
#endif
	LOGE("isValidScan is %d\n", isValidScan);
	
	//QNF_SetCh(freq,freq,1);
#if USING_INDUCTOR
	//Auto tuning
	QND_WriteReg(0x00, 0x11);
	QND_WriteReg(0x4f, 0x80);
	regValue = QN_IIC_read(0x4f);
	regValue = (regValue >> 1);
	QND_WriteReg(0x4f, regValue);
#endif 
	//entering into RX mode and CCA mode,channels index decide by CCA.
	QND_WriteReg(0x00, 0x12);
	timeOut = 100;  // time out is 100ms
	while(1)
	{
		regValue = QN_IIC_read(SYSTEM1);        
		//if it seeks a potential channel, the loop will be quited     
		if((regValue & CHSC) == 0) break;
		delay_n10ms(1);   //delay 5ms
		//if it was time out,chip would not normally work.
		if((timeOut--) == 0) return 0;
	}   
	//reading out the rxcca_fail flag of RXCCA status
	isValidChannelFlag = (QN_IIC_read(STATUS1) & RXCCA_FAIL ? 0:1);  
	LOGE("isValidChannelFlag is %d\n", isValidChannelFlag);
	if(isValidChannelFlag)
	{
		return 1;
#if PILOT_CCA
/*		delay_n10ms(10);
		snr = QN_IIC_read(SNR);
		if(snr> 25) return 1;
		for(readCnt=10;readCnt>0;readCnt--)
		{
			delay_n10ms(1);
			stereoCount += ((QN_IIC_read(STATUS1) & ST_MO_RX) ? 0:1);
			if(stereoCount >= 3) return 1;
		}*/
#else
		
#endif
	}
	return 0;
}

/**/
int set_fre_QN8035(u16 fre)
{
	u8 regValue;
	if (QND_RXValidCH(fre))
	//if (QND_AutoTuneCH(fre))	
	//if (QND_Auto(fre))
    {
		regValue = QN_IIC_read(CH);   
		LOGE("CH Value is %d\n", regValue);
        //QND_TuneToCH(fre*10);
        //return true;
		LOGE("%d is OK\n", fre);
		return regValue+fre;
    } else {
    	LOGE("%d is NG\n", fre);
		return 0; 
    }
    //QND_TuneToCH(fre*10);
    
    
}


void QND_SetVol(u8 vol) AT(QN0835_CODE)
{
	u8 sVol;
	u8 regVal;
	sVol=vol*3+2;
    regVal = QN_IIC_read(VOL_CTL);
	regVal = (regVal&0xC0)|(sVol/6)|(5-sVol%6<<3);
	QND_WriteReg(VOL_CTL,regVal);
}


/*----------------------------------------------------------------------------*/
/**@brief   FM模块检测，获取QN0835 模块ID
   @param   无
   @return  检测到QN0835模块返回1，否则返回0
   @note    bool QN8035_Read_ID(void)
*/
/*----------------------------------------------------------------------------*/
__root int QN8035_Read_ID(void) AT(QN0835_CODE)
{
	u8 cChipID;

	cChipID = QN_IIC_read(CID2);
    cChipID &= 0xfc;
    if (0x84 == cChipID )
    {
        qn8035_online = 1;
	    return 1;
    }
    else
    {
        qn8035_online = 0;
        return 0;
    }
}

__root void QN8035_setch(u8 db)  AT(QN0835_CODE)
{
	//db = db;
    QND_RXSetTH();
}

#endif

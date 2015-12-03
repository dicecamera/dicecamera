package com.sorasoft.dicecam.setting;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.sorasoft.dicecam.BuildConfig;
import com.sorasoft.dicecam.CollageProvider;
import com.sorasoft.dicecam.DiceCamera;
import com.sorasoft.dicecam.PublishInfo;
import com.sorasoft.dicecam.engine.filter.DicecamLens;
import com.sorasoft.dicecam.util.EncryptionProvider;

public class Settings {

	public static String FLURRY_KEY = null;

	public static String ADCOLONY_APP_ID = null;
	public static String ADCOLONY_ZONE_ID = null;

	public static String ADMOB_UNIT_ID = null;
	public static String ADMOB_ALT_UNIT_ID = null;
	public static String ADMOB_INTERSTITIAL_UNIT_ID = null;

	private static Settings mInstance = null;
	private static Activity mDefaultActivity = null;

	private SharedPreferences mPreference = null;

	public static synchronized Settings getInstance() {
		if (mInstance == null) {
			synchronized (java.lang.Object.class) {
				if (mInstance == null)
					mInstance = new Settings();
			}
		}
		return mInstance;
	}

	public Settings() {
		mPreference = ((DiceCamera) DiceCamera.mContext).getPreferences(Context.MODE_PRIVATE);
		initStrings();
	}

	private void initStrings() {
		int source = PublishInfo.source;

		// initFlurryKey(source);
		// initAdColonyAppID(source);
		// initAdColonyZoneID(source);
		// initAdMobUnitID(source);
		// initProUpgradeKey();

		if (BuildConfig.DEBUG) {
			Log.d("aes", "FLURRY_KEY: " + FLURRY_KEY);
			Log.d("aes", "ADCOLONY_APP_ID: " + ADCOLONY_APP_ID);
			Log.d("aes", "ADCOLONY_ZONE_ID: " + ADCOLONY_ZONE_ID);
			// Log.d("aes", "REVMOB_APP_ID: " + REVMOB_APP_ID);
			Log.d("aes", "ADMOB_UNIT_ID: " + ADMOB_UNIT_ID);
			Log.d("aes", "ADMOB_ALT_UNIT_ID: " + ADMOB_ALT_UNIT_ID);
			Log.d("aes", "ADMOB_INTERSTITIAL_UNIT_ID: " + ADMOB_INTERSTITIAL_UNIT_ID);
			Log.d("aes", "kPRO_UPGRADE: " + kPRO_UPGRADE);
		}
	}

	private void initFlurryKey(int s) {
		if (s == PublishInfo.SOURCE__AMAZON_APPSTORE_FOR_ANDROID) {
			byte[] b = new byte[32];
			b[0] = (byte) 0x39;
			b[1] = (byte) 0x44;
			b[2] = (byte) 0x93;
			b[3] = (byte) 0xdb;
			b[4] = (byte) 0x0d;
			b[5] = (byte) 0xac;
			b[6] = (byte) 0x5e;
			b[7] = (byte) 0x58;
			b[8] = (byte) 0xa3;
			b[9] = (byte) 0x8c;
			b[10] = (byte) 0x29;
			b[11] = (byte) 0x6e;
			b[12] = (byte) 0xbd;
			b[13] = (byte) 0x21;
			b[14] = (byte) 0x8f;
			b[15] = (byte) 0x7d;
			b[16] = (byte) 0x57;
			b[17] = (byte) 0x7b;
			b[18] = (byte) 0x03;
			b[19] = (byte) 0xcf;
			b[20] = (byte) 0x7d;
			b[21] = (byte) 0x46;
			b[22] = (byte) 0x2b;
			b[23] = (byte) 0x0f;
			b[24] = (byte) 0xa3;
			b[25] = (byte) 0x79;
			b[26] = (byte) 0x9b;
			b[27] = (byte) 0x9b;
			b[28] = (byte) 0xcf;
			b[29] = (byte) 0x88;
			b[30] = (byte) 0xb6;
			b[31] = (byte) 0x7e;
			FLURRY_KEY = EncryptionProvider.decrypt(b);
		} else if (s == PublishInfo.SOURCE__INTEL_PROVIDER) {
			byte[] b = new byte[32];
			b[0] = (byte) 0xf2;
			b[1] = (byte) 0xa9;
			b[2] = (byte) 0xf8;
			b[3] = (byte) 0x32;
			b[4] = (byte) 0x18;
			b[5] = (byte) 0xaf;
			b[6] = (byte) 0xa3;
			b[7] = (byte) 0x63;
			b[8] = (byte) 0x1f;
			b[9] = (byte) 0x9d;
			b[10] = (byte) 0xdb;
			b[11] = (byte) 0x66;
			b[12] = (byte) 0x4b;
			b[13] = (byte) 0xb4;
			b[14] = (byte) 0xc2;
			b[15] = (byte) 0x52;
			b[16] = (byte) 0x4e;
			b[17] = (byte) 0x55;
			b[18] = (byte) 0x50;
			b[19] = (byte) 0xa0;
			b[20] = (byte) 0x08;
			b[21] = (byte) 0xb6;
			b[22] = (byte) 0x91;
			b[23] = (byte) 0xfd;
			b[24] = (byte) 0x46;
			b[25] = (byte) 0x1a;
			b[26] = (byte) 0x40;
			b[27] = (byte) 0x6d;
			b[28] = (byte) 0x7b;
			b[29] = (byte) 0x0f;
			b[30] = (byte) 0x8a;
			b[31] = (byte) 0xde;
			FLURRY_KEY = EncryptionProvider.decrypt(b);
		} else {
			byte[] b = new byte[32];
			b[0] = (byte) 0xd5;
			b[1] = (byte) 0x28;
			b[2] = (byte) 0x4f;
			b[3] = (byte) 0xa4;
			b[4] = (byte) 0xa4;
			b[5] = (byte) 0x1b;
			b[6] = (byte) 0x1a;
			b[7] = (byte) 0x10;
			b[8] = (byte) 0xcb;
			b[9] = (byte) 0x31;
			b[10] = (byte) 0x9a;
			b[11] = (byte) 0xa0;
			b[12] = (byte) 0xbd;
			b[13] = (byte) 0x70;
			b[14] = (byte) 0x26;
			b[15] = (byte) 0x05;
			b[16] = (byte) 0x94;
			b[17] = (byte) 0xd0;
			b[18] = (byte) 0x27;
			b[19] = (byte) 0x4e;
			b[20] = (byte) 0x88;
			b[21] = (byte) 0x7a;
			b[22] = (byte) 0x17;
			b[23] = (byte) 0xb6;
			b[24] = (byte) 0xda;
			b[25] = (byte) 0xa9;
			b[26] = (byte) 0xaf;
			b[27] = (byte) 0xd3;
			b[28] = (byte) 0x3f;
			b[29] = (byte) 0xd4;
			b[30] = (byte) 0x32;
			b[31] = (byte) 0xe4;
			FLURRY_KEY = EncryptionProvider.decrypt(b);
		}
	}

	private void initAdColonyAppID(int s) {
		if (s == PublishInfo.SOURCE__AMAZON_APPSTORE_FOR_ANDROID) {
			byte[] b = new byte[32];
			b[0] = (byte) 0x44;
			b[1] = (byte) 0x28;
			b[2] = (byte) 0xcd;
			b[3] = (byte) 0x7e;
			b[4] = (byte) 0x36;
			b[5] = (byte) 0x3a;
			b[6] = (byte) 0x14;
			b[7] = (byte) 0x69;
			b[8] = (byte) 0x63;
			b[9] = (byte) 0x43;
			b[10] = (byte) 0xad;
			b[11] = (byte) 0xae;
			b[12] = (byte) 0x7e;
			b[13] = (byte) 0x32;
			b[14] = (byte) 0xa4;
			b[15] = (byte) 0x12;
			b[16] = (byte) 0xca;
			b[17] = (byte) 0xce;
			b[18] = (byte) 0xd3;
			b[19] = (byte) 0xee;
			b[20] = (byte) 0x91;
			b[21] = (byte) 0xe9;
			b[22] = (byte) 0x89;
			b[23] = (byte) 0x1c;
			b[24] = (byte) 0xad;
			b[25] = (byte) 0x96;
			b[26] = (byte) 0xb3;
			b[27] = (byte) 0xa5;
			b[28] = (byte) 0x3c;
			b[29] = (byte) 0x35;
			b[30] = (byte) 0xe8;
			b[31] = (byte) 0xee;
			ADCOLONY_APP_ID = EncryptionProvider.decrypt(b);
		} else {
			byte[] b = new byte[32];
			b[0] = (byte) 0x25;
			b[1] = (byte) 0x5a;
			b[2] = (byte) 0xe6;
			b[3] = (byte) 0xb2;
			b[4] = (byte) 0x4a;
			b[5] = (byte) 0xcc;
			b[6] = (byte) 0x62;
			b[7] = (byte) 0x4c;
			b[8] = (byte) 0x47;
			b[9] = (byte) 0x3f;
			b[10] = (byte) 0xd7;
			b[11] = (byte) 0x9a;
			b[12] = (byte) 0xbf;
			b[13] = (byte) 0xdb;
			b[14] = (byte) 0x23;
			b[15] = (byte) 0x56;
			b[16] = (byte) 0x81;
			b[17] = (byte) 0x31;
			b[18] = (byte) 0x5c;
			b[19] = (byte) 0x32;
			b[20] = (byte) 0x5f;
			b[21] = (byte) 0xce;
			b[22] = (byte) 0xfe;
			b[23] = (byte) 0x7b;
			b[24] = (byte) 0xf6;
			b[25] = (byte) 0x9e;
			b[26] = (byte) 0x67;
			b[27] = (byte) 0xd1;
			b[28] = (byte) 0x1e;
			b[29] = (byte) 0x56;
			b[30] = (byte) 0x1a;
			b[31] = (byte) 0x11;
			ADCOLONY_APP_ID = EncryptionProvider.decrypt(b);
		}
	}

	private void initAdColonyZoneID(int s) {
		if (s == PublishInfo.SOURCE__AMAZON_APPSTORE_FOR_ANDROID) {
			byte[] b = new byte[32];
			b[0] = (byte) 0x4c;
			b[1] = (byte) 0x20;
			b[2] = (byte) 0xc1;
			b[3] = (byte) 0x4e;
			b[4] = (byte) 0xd6;
			b[5] = (byte) 0x41;
			b[6] = (byte) 0x78;
			b[7] = (byte) 0x49;
			b[8] = (byte) 0x7c;
			b[9] = (byte) 0xad;
			b[10] = (byte) 0x23;
			b[11] = (byte) 0x91;
			b[12] = (byte) 0xd5;
			b[13] = (byte) 0xf7;
			b[14] = (byte) 0x45;
			b[15] = (byte) 0xb6;
			b[16] = (byte) 0xf8;
			b[17] = (byte) 0x24;
			b[18] = (byte) 0x34;
			b[19] = (byte) 0xed;
			b[20] = (byte) 0x9a;
			b[21] = (byte) 0xed;
			b[22] = (byte) 0x29;
			b[23] = (byte) 0x62;
			b[24] = (byte) 0x42;
			b[25] = (byte) 0xcf;
			b[26] = (byte) 0xf6;
			b[27] = (byte) 0x30;
			b[28] = (byte) 0x31;
			b[29] = (byte) 0xaf;
			b[30] = (byte) 0x1b;
			b[31] = (byte) 0x1c;
			ADCOLONY_ZONE_ID = EncryptionProvider.decrypt(b);
		} else {
			byte[] b = new byte[32];
			b[0] = (byte) 0xfd;
			b[1] = (byte) 0x9e;
			b[2] = (byte) 0x12;
			b[3] = (byte) 0xd9;
			b[4] = (byte) 0xf9;
			b[5] = (byte) 0xf1;
			b[6] = (byte) 0x39;
			b[7] = (byte) 0x32;
			b[8] = (byte) 0x56;
			b[9] = (byte) 0x0b;
			b[10] = (byte) 0xdd;
			b[11] = (byte) 0x65;
			b[12] = (byte) 0x9d;
			b[13] = (byte) 0xc3;
			b[14] = (byte) 0x3e;
			b[15] = (byte) 0x83;
			b[16] = (byte) 0x70;
			b[17] = (byte) 0x14;
			b[18] = (byte) 0xdb;
			b[19] = (byte) 0xf3;
			b[20] = (byte) 0xc6;
			b[21] = (byte) 0xdd;
			b[22] = (byte) 0x28;
			b[23] = (byte) 0x9b;
			b[24] = (byte) 0xe6;
			b[25] = (byte) 0x91;
			b[26] = (byte) 0x9d;
			b[27] = (byte) 0x95;
			b[28] = (byte) 0xe1;
			b[29] = (byte) 0x66;
			b[30] = (byte) 0x8c;
			b[31] = (byte) 0x08;
			ADCOLONY_ZONE_ID = EncryptionProvider.decrypt(b);
		}
	}

	private void initAdMobUnitID(int s) {
		{
			byte[] b = new byte[48];
			b[0] = (byte) 0x26;
			b[1] = (byte) 0xe5;
			b[2] = (byte) 0x4b;
			b[3] = (byte) 0x0b;
			b[4] = (byte) 0x72;
			b[5] = (byte) 0xda;
			b[6] = (byte) 0x22;
			b[7] = (byte) 0x66;
			b[8] = (byte) 0xdd;
			b[9] = (byte) 0x88;
			b[10] = (byte) 0x94;
			b[11] = (byte) 0x31;
			b[12] = (byte) 0xec;
			b[13] = (byte) 0x1f;
			b[14] = (byte) 0x74;
			b[15] = (byte) 0x31;
			b[16] = (byte) 0x9b;
			b[17] = (byte) 0x96;
			b[18] = (byte) 0x77;
			b[19] = (byte) 0x8b;
			b[20] = (byte) 0x09;
			b[21] = (byte) 0x14;
			b[22] = (byte) 0xf5;
			b[23] = (byte) 0x3e;
			b[24] = (byte) 0xe7;
			b[25] = (byte) 0x46;
			b[26] = (byte) 0xb8;
			b[27] = (byte) 0xef;
			b[28] = (byte) 0x6d;
			b[29] = (byte) 0x30;
			b[30] = (byte) 0xff;
			b[31] = (byte) 0x39;
			b[32] = (byte) 0x74;
			b[33] = (byte) 0x27;
			b[34] = (byte) 0xae;
			b[35] = (byte) 0x26;
			b[36] = (byte) 0x5d;
			b[37] = (byte) 0xe2;
			b[38] = (byte) 0x02;
			b[39] = (byte) 0xf4;
			b[40] = (byte) 0x90;
			b[41] = (byte) 0x37;
			b[42] = (byte) 0x4d;
			b[43] = (byte) 0x44;
			b[44] = (byte) 0xb6;
			b[45] = (byte) 0xde;
			b[46] = (byte) 0x77;
			b[47] = (byte) 0x1a;
			ADMOB_UNIT_ID = EncryptionProvider.decrypt(b);
		}
		{
			byte[] b = new byte[48];
			b[0] = (byte) 0x26;
			b[1] = (byte) 0xe5;
			b[2] = (byte) 0x4b;
			b[3] = (byte) 0x0b;
			b[4] = (byte) 0x72;
			b[5] = (byte) 0xda;
			b[6] = (byte) 0x22;
			b[7] = (byte) 0x66;
			b[8] = (byte) 0xdd;
			b[9] = (byte) 0x88;
			b[10] = (byte) 0x94;
			b[11] = (byte) 0x31;
			b[12] = (byte) 0xec;
			b[13] = (byte) 0x1f;
			b[14] = (byte) 0x74;
			b[15] = (byte) 0x31;
			b[16] = (byte) 0xe5;
			b[17] = (byte) 0xcc;
			b[18] = (byte) 0xc9;
			b[19] = (byte) 0x48;
			b[20] = (byte) 0x46;
			b[21] = (byte) 0xff;
			b[22] = (byte) 0x0b;
			b[23] = (byte) 0x3c;
			b[24] = (byte) 0x8e;
			b[25] = (byte) 0x1d;
			b[26] = (byte) 0x2b;
			b[27] = (byte) 0xd8;
			b[28] = (byte) 0xd1;
			b[29] = (byte) 0x00;
			b[30] = (byte) 0x17;
			b[31] = (byte) 0xce;
			b[32] = (byte) 0x0b;
			b[33] = (byte) 0xe1;
			b[34] = (byte) 0xdb;
			b[35] = (byte) 0xa2;
			b[36] = (byte) 0x20;
			b[37] = (byte) 0xbd;
			b[38] = (byte) 0x41;
			b[39] = (byte) 0xb4;
			b[40] = (byte) 0xf9;
			b[41] = (byte) 0x2f;
			b[42] = (byte) 0xd2;
			b[43] = (byte) 0xd1;
			b[44] = (byte) 0xf8;
			b[45] = (byte) 0xfe;
			b[46] = (byte) 0x43;
			b[47] = (byte) 0x62;
			ADMOB_ALT_UNIT_ID = EncryptionProvider.decrypt(b);
		}
		{
			byte[] b = new byte[48];
			b[0] = (byte) 0x26;
			b[1] = (byte) 0xe5;
			b[2] = (byte) 0x4b;
			b[3] = (byte) 0x0b;
			b[4] = (byte) 0x72;
			b[5] = (byte) 0xda;
			b[6] = (byte) 0x22;
			b[7] = (byte) 0x66;
			b[8] = (byte) 0xdd;
			b[9] = (byte) 0x88;
			b[10] = (byte) 0x94;
			b[11] = (byte) 0x31;
			b[12] = (byte) 0xec;
			b[13] = (byte) 0x1f;
			b[14] = (byte) 0x74;
			b[15] = (byte) 0x31;
			b[16] = (byte) 0x28;
			b[17] = (byte) 0x7e;
			b[18] = (byte) 0xfa;
			b[19] = (byte) 0x52;
			b[20] = (byte) 0x7a;
			b[21] = (byte) 0x61;
			b[22] = (byte) 0xf7;
			b[23] = (byte) 0x6b;
			b[24] = (byte) 0x94;
			b[25] = (byte) 0x4e;
			b[26] = (byte) 0x4d;
			b[27] = (byte) 0xe2;
			b[28] = (byte) 0x67;
			b[29] = (byte) 0x50;
			b[30] = (byte) 0xba;
			b[31] = (byte) 0x58;
			b[32] = (byte) 0x6c;
			b[33] = (byte) 0xba;
			b[34] = (byte) 0xcb;
			b[35] = (byte) 0xb8;
			b[36] = (byte) 0xbf;
			b[37] = (byte) 0x28;
			b[38] = (byte) 0x85;
			b[39] = (byte) 0x40;
			b[40] = (byte) 0x14;
			b[41] = (byte) 0xfa;
			b[42] = (byte) 0x96;
			b[43] = (byte) 0x64;
			b[44] = (byte) 0xc1;
			b[45] = (byte) 0x73;
			b[46] = (byte) 0x7d;
			b[47] = (byte) 0x5b;
			ADMOB_INTERSTITIAL_UNIT_ID = EncryptionProvider.decrypt(b);
		}
	}

	private void initProUpgradeKey() {
		byte[] b = new byte[16];
		b[0] = (byte) 0x79;
		b[1] = (byte) 0x83;
		b[2] = (byte) 0x56;
		b[3] = (byte) 0x61;
		b[4] = (byte) 0x46;
		b[5] = (byte) 0x6f;
		b[6] = (byte) 0x0b;
		b[7] = (byte) 0x1f;
		b[8] = (byte) 0xc0;
		b[9] = (byte) 0x82;
		b[10] = (byte) 0x57;
		b[11] = (byte) 0x27;
		b[12] = (byte) 0xe0;
		b[13] = (byte) 0x15;
		b[14] = (byte) 0x3d;
		b[15] = (byte) 0x16;
		kPRO_UPGRADE = EncryptionProvider.decrypt(b);
	}

	public static String getPublicKey() {
		byte[] b = new byte[400];
		b[0] = (byte) 0xef;
		b[1] = (byte) 0x4a;
		b[2] = (byte) 0xa8;
		b[3] = (byte) 0xad;
		b[4] = (byte) 0xdf;
		b[5] = (byte) 0xa8;
		b[6] = (byte) 0xed;
		b[7] = (byte) 0xe2;
		b[8] = (byte) 0x5d;
		b[9] = (byte) 0x8b;
		b[10] = (byte) 0x29;
		b[11] = (byte) 0xe2;
		b[12] = (byte) 0xfe;
		b[13] = (byte) 0x0d;
		b[14] = (byte) 0xac;
		b[15] = (byte) 0xbc;
		b[16] = (byte) 0x99;
		b[17] = (byte) 0x43;
		b[18] = (byte) 0x9d;
		b[19] = (byte) 0x76;
		b[20] = (byte) 0x4e;
		b[21] = (byte) 0x02;
		b[22] = (byte) 0x07;
		b[23] = (byte) 0x7c;
		b[24] = (byte) 0xc5;
		b[25] = (byte) 0xe8;
		b[26] = (byte) 0x76;
		b[27] = (byte) 0xe4;
		b[28] = (byte) 0x7d;
		b[29] = (byte) 0x09;
		b[30] = (byte) 0x3c;
		b[31] = (byte) 0x49;
		b[32] = (byte) 0xf8;
		b[33] = (byte) 0xda;
		b[34] = (byte) 0x66;
		b[35] = (byte) 0x4b;
		b[36] = (byte) 0x5f;
		b[37] = (byte) 0x50;
		b[38] = (byte) 0xab;
		b[39] = (byte) 0x6b;
		b[40] = (byte) 0x58;
		b[41] = (byte) 0xab;
		b[42] = (byte) 0x86;
		b[43] = (byte) 0x3a;
		b[44] = (byte) 0x6b;
		b[45] = (byte) 0x72;
		b[46] = (byte) 0x47;
		b[47] = (byte) 0x0a;
		b[48] = (byte) 0xd1;
		b[49] = (byte) 0x56;
		b[50] = (byte) 0xdf;
		b[51] = (byte) 0x26;
		b[52] = (byte) 0xfa;
		b[53] = (byte) 0xbd;
		b[54] = (byte) 0x6d;
		b[55] = (byte) 0x6d;
		b[56] = (byte) 0xf2;
		b[57] = (byte) 0x72;
		b[58] = (byte) 0xbb;
		b[59] = (byte) 0xfe;
		b[60] = (byte) 0xf8;
		b[61] = (byte) 0x9d;
		b[62] = (byte) 0xb2;
		b[63] = (byte) 0x1b;
		b[64] = (byte) 0xd7;
		b[65] = (byte) 0xd1;
		b[66] = (byte) 0x26;
		b[67] = (byte) 0x64;
		b[68] = (byte) 0x7a;
		b[69] = (byte) 0x81;
		b[70] = (byte) 0x5a;
		b[71] = (byte) 0x0b;
		b[72] = (byte) 0x2c;
		b[73] = (byte) 0x3e;
		b[74] = (byte) 0x86;
		b[75] = (byte) 0x99;
		b[76] = (byte) 0xbd;
		b[77] = (byte) 0x5c;
		b[78] = (byte) 0xb2;
		b[79] = (byte) 0x09;
		b[80] = (byte) 0x09;
		b[81] = (byte) 0x49;
		b[82] = (byte) 0x4a;
		b[83] = (byte) 0x85;
		b[84] = (byte) 0xb0;
		b[85] = (byte) 0x60;
		b[86] = (byte) 0x65;
		b[87] = (byte) 0xe5;
		b[88] = (byte) 0x0b;
		b[89] = (byte) 0x7f;
		b[90] = (byte) 0x8e;
		b[91] = (byte) 0x9f;
		b[92] = (byte) 0x99;
		b[93] = (byte) 0xe2;
		b[94] = (byte) 0x06;
		b[95] = (byte) 0x35;
		b[96] = (byte) 0x37;
		b[97] = (byte) 0xb8;
		b[98] = (byte) 0x57;
		b[99] = (byte) 0x2a;
		b[100] = (byte) 0x75;
		b[101] = (byte) 0x71;
		b[102] = (byte) 0x82;
		b[103] = (byte) 0x6f;
		b[104] = (byte) 0xc3;
		b[105] = (byte) 0x1c;
		b[106] = (byte) 0x52;
		b[107] = (byte) 0xf5;
		b[108] = (byte) 0x39;
		b[109] = (byte) 0x8b;
		b[110] = (byte) 0x31;
		b[111] = (byte) 0xa8;
		b[112] = (byte) 0x95;
		b[113] = (byte) 0x2a;
		b[114] = (byte) 0x9c;
		b[115] = (byte) 0x27;
		b[116] = (byte) 0xcb;
		b[117] = (byte) 0xde;
		b[118] = (byte) 0x19;
		b[119] = (byte) 0x47;
		b[120] = (byte) 0x8a;
		b[121] = (byte) 0x59;
		b[122] = (byte) 0x21;
		b[123] = (byte) 0x23;
		b[124] = (byte) 0x4f;
		b[125] = (byte) 0xae;
		b[126] = (byte) 0xdc;
		b[127] = (byte) 0x23;
		b[128] = (byte) 0xf8;
		b[129] = (byte) 0x90;
		b[130] = (byte) 0x04;
		b[131] = (byte) 0x7d;
		b[132] = (byte) 0x50;
		b[133] = (byte) 0x2e;
		b[134] = (byte) 0x8a;
		b[135] = (byte) 0x99;
		b[136] = (byte) 0x27;
		b[137] = (byte) 0x4b;
		b[138] = (byte) 0x24;
		b[139] = (byte) 0xd7;
		b[140] = (byte) 0x94;
		b[141] = (byte) 0xfc;
		b[142] = (byte) 0x6e;
		b[143] = (byte) 0xaa;
		b[144] = (byte) 0xbc;
		b[145] = (byte) 0xa0;
		b[146] = (byte) 0xcb;
		b[147] = (byte) 0xa7;
		b[148] = (byte) 0x4a;
		b[149] = (byte) 0x3c;
		b[150] = (byte) 0xfe;
		b[151] = (byte) 0x6e;
		b[152] = (byte) 0x33;
		b[153] = (byte) 0xe4;
		b[154] = (byte) 0x50;
		b[155] = (byte) 0xe7;
		b[156] = (byte) 0x2d;
		b[157] = (byte) 0x12;
		b[158] = (byte) 0x24;
		b[159] = (byte) 0xb2;
		b[160] = (byte) 0x07;
		b[161] = (byte) 0x36;
		b[162] = (byte) 0xbe;
		b[163] = (byte) 0x67;
		b[164] = (byte) 0xde;
		b[165] = (byte) 0x4b;
		b[166] = (byte) 0x51;
		b[167] = (byte) 0x61;
		b[168] = (byte) 0x07;
		b[169] = (byte) 0x77;
		b[170] = (byte) 0xbe;
		b[171] = (byte) 0xba;
		b[172] = (byte) 0xe3;
		b[173] = (byte) 0xc1;
		b[174] = (byte) 0x4c;
		b[175] = (byte) 0x2a;
		b[176] = (byte) 0x30;
		b[177] = (byte) 0x50;
		b[178] = (byte) 0xac;
		b[179] = (byte) 0x68;
		b[180] = (byte) 0x8f;
		b[181] = (byte) 0xe4;
		b[182] = (byte) 0xe1;
		b[183] = (byte) 0x7d;
		b[184] = (byte) 0x66;
		b[185] = (byte) 0x3d;
		b[186] = (byte) 0xf0;
		b[187] = (byte) 0xda;
		b[188] = (byte) 0x1f;
		b[189] = (byte) 0x41;
		b[190] = (byte) 0x5c;
		b[191] = (byte) 0x20;
		b[192] = (byte) 0x57;
		b[193] = (byte) 0xa8;
		b[194] = (byte) 0x92;
		b[195] = (byte) 0x18;
		b[196] = (byte) 0xfb;
		b[197] = (byte) 0x7b;
		b[198] = (byte) 0x32;
		b[199] = (byte) 0xbb;
		b[200] = (byte) 0x98;
		b[201] = (byte) 0x67;
		b[202] = (byte) 0x34;
		b[203] = (byte) 0x70;
		b[204] = (byte) 0xdb;
		b[205] = (byte) 0x42;
		b[206] = (byte) 0xd9;
		b[207] = (byte) 0x56;
		b[208] = (byte) 0x11;
		b[209] = (byte) 0x21;
		b[210] = (byte) 0xcd;
		b[211] = (byte) 0xa8;
		b[212] = (byte) 0xd4;
		b[213] = (byte) 0x57;
		b[214] = (byte) 0xfa;
		b[215] = (byte) 0x7d;
		b[216] = (byte) 0xcd;
		b[217] = (byte) 0x12;
		b[218] = (byte) 0x5a;
		b[219] = (byte) 0xb5;
		b[220] = (byte) 0xd7;
		b[221] = (byte) 0x5a;
		b[222] = (byte) 0x5c;
		b[223] = (byte) 0x58;
		b[224] = (byte) 0x09;
		b[225] = (byte) 0x5b;
		b[226] = (byte) 0x74;
		b[227] = (byte) 0x9e;
		b[228] = (byte) 0xfe;
		b[229] = (byte) 0x9e;
		b[230] = (byte) 0xac;
		b[231] = (byte) 0x32;
		b[232] = (byte) 0xb1;
		b[233] = (byte) 0x15;
		b[234] = (byte) 0xd1;
		b[235] = (byte) 0xbf;
		b[236] = (byte) 0xe4;
		b[237] = (byte) 0xa2;
		b[238] = (byte) 0x2d;
		b[239] = (byte) 0xe0;
		b[240] = (byte) 0x1e;
		b[241] = (byte) 0x48;
		b[242] = (byte) 0xc5;
		b[243] = (byte) 0xef;
		b[244] = (byte) 0x25;
		b[245] = (byte) 0xd5;
		b[246] = (byte) 0x8a;
		b[247] = (byte) 0x14;
		b[248] = (byte) 0xca;
		b[249] = (byte) 0x27;
		b[250] = (byte) 0x0e;
		b[251] = (byte) 0x09;
		b[252] = (byte) 0xe0;
		b[253] = (byte) 0x5b;
		b[254] = (byte) 0x4c;
		b[255] = (byte) 0x8a;
		b[256] = (byte) 0x19;
		b[257] = (byte) 0x43;
		b[258] = (byte) 0xdb;
		b[259] = (byte) 0x03;
		b[260] = (byte) 0xfa;
		b[261] = (byte) 0x4d;
		b[262] = (byte) 0x32;
		b[263] = (byte) 0x87;
		b[264] = (byte) 0xa3;
		b[265] = (byte) 0xf4;
		b[266] = (byte) 0x0f;
		b[267] = (byte) 0x4d;
		b[268] = (byte) 0xb3;
		b[269] = (byte) 0x29;
		b[270] = (byte) 0xc9;
		b[271] = (byte) 0xa9;
		b[272] = (byte) 0x71;
		b[273] = (byte) 0x50;
		b[274] = (byte) 0xbf;
		b[275] = (byte) 0x0d;
		b[276] = (byte) 0xfb;
		b[277] = (byte) 0xb0;
		b[278] = (byte) 0x19;
		b[279] = (byte) 0xdc;
		b[280] = (byte) 0xf5;
		b[281] = (byte) 0xd2;
		b[282] = (byte) 0x8e;
		b[283] = (byte) 0xd4;
		b[284] = (byte) 0x2d;
		b[285] = (byte) 0xa8;
		b[286] = (byte) 0x6c;
		b[287] = (byte) 0xce;
		b[288] = (byte) 0xb2;
		b[289] = (byte) 0x1b;
		b[290] = (byte) 0x47;
		b[291] = (byte) 0x6c;
		b[292] = (byte) 0x6d;
		b[293] = (byte) 0x78;
		b[294] = (byte) 0x04;
		b[295] = (byte) 0xdd;
		b[296] = (byte) 0x52;
		b[297] = (byte) 0x30;
		b[298] = (byte) 0xb5;
		b[299] = (byte) 0x83;
		b[300] = (byte) 0x74;
		b[301] = (byte) 0xf2;
		b[302] = (byte) 0xf0;
		b[303] = (byte) 0x96;
		b[304] = (byte) 0x19;
		b[305] = (byte) 0x05;
		b[306] = (byte) 0x6e;
		b[307] = (byte) 0x92;
		b[308] = (byte) 0x39;
		b[309] = (byte) 0xc7;
		b[310] = (byte) 0xf2;
		b[311] = (byte) 0xd3;
		b[312] = (byte) 0x7a;
		b[313] = (byte) 0x5c;
		b[314] = (byte) 0x27;
		b[315] = (byte) 0x18;
		b[316] = (byte) 0xc3;
		b[317] = (byte) 0xd7;
		b[318] = (byte) 0xa3;
		b[319] = (byte) 0xc3;
		b[320] = (byte) 0x60;
		b[321] = (byte) 0x2b;
		b[322] = (byte) 0xb2;
		b[323] = (byte) 0x2d;
		b[324] = (byte) 0x80;
		b[325] = (byte) 0xb5;
		b[326] = (byte) 0x7d;
		b[327] = (byte) 0x2d;
		b[328] = (byte) 0x15;
		b[329] = (byte) 0xe4;
		b[330] = (byte) 0x4e;
		b[331] = (byte) 0xa5;
		b[332] = (byte) 0x83;
		b[333] = (byte) 0x49;
		b[334] = (byte) 0xb8;
		b[335] = (byte) 0xe0;
		b[336] = (byte) 0x58;
		b[337] = (byte) 0x1c;
		b[338] = (byte) 0x78;
		b[339] = (byte) 0x01;
		b[340] = (byte) 0xf7;
		b[341] = (byte) 0x15;
		b[342] = (byte) 0x80;
		b[343] = (byte) 0x9c;
		b[344] = (byte) 0x8f;
		b[345] = (byte) 0xfb;
		b[346] = (byte) 0xea;
		b[347] = (byte) 0x99;
		b[348] = (byte) 0xca;
		b[349] = (byte) 0xc5;
		b[350] = (byte) 0x0b;
		b[351] = (byte) 0x71;
		b[352] = (byte) 0x6a;
		b[353] = (byte) 0xc7;
		b[354] = (byte) 0x3c;
		b[355] = (byte) 0x4a;
		b[356] = (byte) 0xe5;
		b[357] = (byte) 0xe7;
		b[358] = (byte) 0xf9;
		b[359] = (byte) 0x7b;
		b[360] = (byte) 0xab;
		b[361] = (byte) 0x79;
		b[362] = (byte) 0x48;
		b[363] = (byte) 0x06;
		b[364] = (byte) 0x4e;
		b[365] = (byte) 0x08;
		b[366] = (byte) 0x19;
		b[367] = (byte) 0x38;
		b[368] = (byte) 0x91;
		b[369] = (byte) 0xa6;
		b[370] = (byte) 0xaa;
		b[371] = (byte) 0xc3;
		b[372] = (byte) 0x05;
		b[373] = (byte) 0x8f;
		b[374] = (byte) 0xd2;
		b[375] = (byte) 0x2b;
		b[376] = (byte) 0xad;
		b[377] = (byte) 0xe9;
		b[378] = (byte) 0x23;
		b[379] = (byte) 0xa4;
		b[380] = (byte) 0xe2;
		b[381] = (byte) 0x73;
		b[382] = (byte) 0xa6;
		b[383] = (byte) 0x5c;
		b[384] = (byte) 0x10;
		b[385] = (byte) 0xad;
		b[386] = (byte) 0x6f;
		b[387] = (byte) 0x17;
		b[388] = (byte) 0xf4;
		b[389] = (byte) 0x66;
		b[390] = (byte) 0xd9;
		b[391] = (byte) 0x61;
		b[392] = (byte) 0x85;
		b[393] = (byte) 0x9d;
		b[394] = (byte) 0x47;
		b[395] = (byte) 0x52;
		b[396] = (byte) 0x18;
		b[397] = (byte) 0x86;
		b[398] = (byte) 0x46;
		b[399] = (byte) 0x38;
		return EncryptionProvider.decrypt(b);
	}

	public static String getSkuProUpgrade() {
		byte[] b = new byte[16];
		b[0] = (byte) 0xae;
		b[1] = (byte) 0xa1;
		b[2] = (byte) 0xb6;
		b[3] = (byte) 0xd4;
		b[4] = (byte) 0x89;
		b[5] = (byte) 0x9f;
		b[6] = (byte) 0x51;
		b[7] = (byte) 0x56;
		b[8] = (byte) 0x1e;
		b[9] = (byte) 0x12;
		b[10] = (byte) 0x9d;
		b[11] = (byte) 0xef;
		b[12] = (byte) 0xc8;
		b[13] = (byte) 0xce;
		b[14] = (byte) 0x3f;
		b[15] = (byte) 0x22;
		return EncryptionProvider.decrypt(b);
	}

	private static String kCURRENT_LENS_ID = "currentLensID";

	public String getCurrentLensID() {
		return mPreference.getString(kCURRENT_LENS_ID, null);
	}

	public void setCurrentLensID(final String value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putString(kCURRENT_LENS_ID, value);
		editor.commit();
	}

	private static String kLENS_INTENSITY = "lensLookupInitensity";

	public float getLensIntensity() {
		return mPreference.getFloat(kLENS_INTENSITY, 1.f);
	}

	public void setLensIntensity(final float value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putFloat(kLENS_INTENSITY, value);
		editor.commit();
	}

	private static String prefFavorites = "prefFavorites";

//	public boolean saveFavories(ArrayList<DicecamLens> arrFavorites) {
//		
//		try {
//			SharedPreferences.Editor prefsEditor = mPreference.edit();
//			Gson gson = new Gson();
//			String json = gson.toJson(arrFavorites);
//			prefsEditor.putString(prefFavorites, json);
//			prefsEditor.commit();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}

//	public ArrayList<DicecamLens> loadFavories() {
//		Gson gson = new Gson();
//		String json = mPreference.getString(prefFavorites, "");
//		ArrayList<DicecamLens> arrObj = gson.fromJson(json, ArrayList.class);
//		return arrObj;
//	}

	private static String kUSE_VIGNETTE = "useVignette";

	public boolean getUseVignette() {
		return mPreference.getBoolean(kUSE_VIGNETTE, false);
	}

	public void setUseVignette(final boolean value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putBoolean(kUSE_VIGNETTE, value);
		editor.commit();
	}

	private static String kUSE_BLUR = "useBlur";

	public boolean getUseBlur() {
		return mPreference.getBoolean(kUSE_BLUR, false);
	}

	public void setUseBlur(final boolean value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putBoolean(kUSE_BLUR, value);
		editor.commit();
	}

	public boolean getShouldFramePhoto() {
		return (mPreference.getInt(kFRAME_WIDTH, FRAME_WIDTH_NONE) != FRAME_WIDTH_NONE);
	}

	public static final int FRAME_WIDTH_NONE = 0;
	public static final int FRAME_WIDTH_LIGHT = 1;
	public static final int FRAME_WIDTH_BOLD = 2;

	private static String kFRAME_WIDTH = "frameWidth";

	public int getFrameWidth() {
		return mPreference.getInt(kFRAME_WIDTH, FRAME_WIDTH_NONE);
	}

	public void setFrameWidth(final int value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putInt(kFRAME_WIDTH, value);
		editor.commit();
	}

	public static final int FRAME_COLOR_BLACK = 0;
	public static final int FRAME_COLOR_WHITE = 1;

	private static String kFRAME_COLOR = "frameColor";

	public int getFrameColor() {
		return mPreference.getInt(kFRAME_COLOR, FRAME_COLOR_BLACK);
	}

	public void setFrameColor(final int value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putInt(kFRAME_COLOR, value);
		editor.commit();
	}

	private static String kCAMERA_SELECTOIN = "cameraSelection";

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public int getCameraSelection() {
		return mPreference.getInt(kCAMERA_SELECTOIN, Camera.CameraInfo.CAMERA_FACING_BACK);
	}

	public void setCameraSelection(final int value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putInt(kCAMERA_SELECTOIN, value);
		editor.commit();
	}

	private static String mFirstTimeExecution = "firstTimeExecution";

	public boolean getFirstTimeExecution() {
		return mPreference.getBoolean(mFirstTimeExecution, true);
	}

	public void setFirstTimeExecution(final boolean value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putBoolean(mFirstTimeExecution, value);
		editor.commit();
	}

	private static String kFRAME_SELECTION = "frameSelection";

	public int getFrameSelection() {
		return mPreference.getInt(kFRAME_SELECTION, CollageProvider.DICECAM_COLLAGE_STATUS_RECTANGLE_SINGLE);
	}

	public void setFrameSelection(final int value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putInt(kFRAME_SELECTION, value);
		editor.commit();
	}

	private static String kGEO_TAG = "geoTag";

	public boolean getGeoTag() {
		return mPreference.getBoolean(kGEO_TAG, true);
	}

	public void setGeoTag(final boolean value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putBoolean(kGEO_TAG, value);
		editor.commit();
	}

	private static String kSAVE_TO_REMOVABLE_STORAGE = "removable_storage";

	public boolean getSaveToRemovableStorage() {
		// 2.1.3 return mPreference.getBoolean(kSAVE_TO_REMOVABLE_STORAGE,
		// true);
		return false;
	}

	public void setSaveToRemovableStorage(final boolean value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putBoolean(kSAVE_TO_REMOVABLE_STORAGE, value);
		editor.commit();
	}

	private static String kWATERMARK = "watermark_integer";

	public int getWatermark() {
		return mPreference.getInt(kWATERMARK, WatermarkProvider.DICECAM_WATERMARK_SIMPLE);
	}

	public void setWatermark(final int value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putInt(kWATERMARK, value);
		editor.commit();
	}

	private static String kWATERMARK_ENABLED = "watermark_enabled";

	public boolean isWatermarkEnabled() {
		return mPreference.getBoolean(kWATERMARK_ENABLED, true);
	}

	public void setWatermarkEnabled(final boolean value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putBoolean(kWATERMARK_ENABLED, value);
		editor.commit();
	}

	private static String kSAVE_AS_PREVIEW = "saveAsPreview";

	public boolean getSaveAsPreview() {
		return mPreference.getBoolean(kSAVE_AS_PREVIEW, true);
	}

	public void setSaveAsPreview(final boolean value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putBoolean(kSAVE_AS_PREVIEW, value);
		editor.commit();
	}

	private static String kTIMER = "timer";

	public int getTimer() {
		return mPreference.getInt(kTIMER, 0); // in milliseconds
	}

	public void setTimer(final int value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putInt(kTIMER, value);
		editor.commit();
	}

	private static String kINTERVAL = "interval";

	public int getInterval() {
		return mPreference.getInt(kINTERVAL, 660); // in milliseconds
	}

	public void setInterval(final int value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putInt(kINTERVAL, value);
		editor.commit();
	}

	private static String kPRO_UPGRADE = null;

	public boolean getProUpgrade() {
		// return mPreference.getBoolean(kPRO_UPGRADE, false);
		return true;
	}

	public void setProUpgrade(final boolean value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putBoolean(kPRO_UPGRADE, value);
		editor.commit();
	}

	private static String kTOOLBAR_BODY_HEIGHT = "toolbar_body_height";

	public int getToolbarBodyHeight() {
		return mPreference.getInt(kTOOLBAR_BODY_HEIGHT, 0);
	}

	public void setToolbarBodyHeight(final int value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putInt(kTOOLBAR_BODY_HEIGHT, value);
		editor.commit();
	}

	private static String kLIVE_PREVIEW_DISABLED = "livePreviewDisabled";

	public void setLivePreviewDisabled(final boolean value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putBoolean(kLIVE_PREVIEW_DISABLED, value);
		editor.commit();
	}

	private static String kFIRST_LAUNCH_TIME = "first_launch_time";

	public long getFirstLaunchTime() {
		return mPreference.getLong(kFIRST_LAUNCH_TIME, 0);
	}

	public void setFirstLaunchTime(final long value) {
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putLong(kFIRST_LAUNCH_TIME, value);
		editor.commit();
	}
}

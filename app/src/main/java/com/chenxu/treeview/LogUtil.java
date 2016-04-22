package com.chenxu.treeview;

import android.util.Log;

public class LogUtil {

	public static boolean isDebug = true;
	public static void i(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		}
	}
    public static void ii(String msg){
        if (isDebug){
            Log.i("chenxu", msg);
        }
    }
}

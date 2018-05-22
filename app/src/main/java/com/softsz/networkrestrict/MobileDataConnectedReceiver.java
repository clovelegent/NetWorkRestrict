package com.softsz.networkrestrict;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

public class MobileDataConnectedReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(action == "action_mobil_data_reconnected"){
			
			List<String> list = CommandUtil.execute("ping -c 1 114.114.114.114 ");
			boolean shouldRemoveApn = false;
			if(list != null){
				Log.d("xph_MobileDataConnectedReceiver", list.toString());
				shouldRemoveApn = list.get(0).equals("status:0");
			}
			if(shouldRemoveApn){
				ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo ni = conManager.getActiveNetworkInfo(); 
				String apn = ni.getExtraInfo();
				int id = removeCurrentAPN(context.getContentResolver(),apn);
				Log.d("xph_MobileDataConnectedReceiver","apn = "+apn+",id="+id);
			}
		}
	}

	public int removeCurrentAPN(ContentResolver resolver, String newAPN) { 
        Cursor cursor = null;
        Uri uri = Uri.parse("content://telephony/carriers");
        try { 
            cursor = resolver.query(uri, null, "apn = ?", new String[]{newAPN.toLowerCase()}, null); 
            String apnId = null; 
            if (cursor != null && cursor.moveToFirst()) { 
                apnId = cursor.getString(cursor.getColumnIndex("_id")); 
            } 
            cursor.close(); 
            if (apnId != null) { 
                return resolver.delete(uri, "_id = ?", new String[]{apnId});
            } 
        } catch (SQLException e) { 
        	
        } finally { 
            if (cursor != null) { 
                cursor.close(); 
            } 
        } 
        return -1; 
} 
}

package com.example.deathblade.callcut;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhonecallReceiver extends BroadcastReceiver {

    public static String TAG="PhoneStateReceiver";
    public boolean hangup=true;
    public Handler handler;
    public Runnable runnable;
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.d(TAG,"PhoneStateReceiver**Call State=" + state);
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Log.d(TAG,"PhoneStateReceiver**Idle");
                if (hangup){
                    Log.e("Look over ","here");
                    File sdcard = Environment.getExternalStorageDirectory();
                    try {
                        File cut_call=new File(sdcard.getAbsolutePath()+"/check");
                        if (!cut_call.exists()){
                        boolean work = cut_call.mkdirs();
                        Log.e("direcrory created=",Boolean.toString(work));
                        }
                        File file = new File(cut_call,"hangup.txt");
                        FileWriter writer = new FileWriter(file);
                        writer.append("Cancelled");
                        writer.flush();
                        writer.close();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Log.e("Look","here");
                    }
                }
                else{
                    Log.e("Nigga u","dumb");
                }
            } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                // Incoming call
                String incomingNumber =
                        intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.d(TAG,"PhoneStateReceiver**Incoming call " + incomingNumber);

//                if (!killCall(context)) { // Using the method defined earlier
//                    Log.d(TAG,"PhoneStateReceiver **Unable to kill incoming call");
//                }

            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                Log.d(TAG,"PhoneStateReceiver **Offhook");
                hangup=true;
                handler = new Handler();
                final int delay = 3000; //milliseconds
                runnable = new Runnable(){
                    @Override
                    public void run() {
                        File sdcard = Environment.getExternalStorageDirectory();
                        File file = new File(sdcard.getAbsolutePath(), "call.txt");
                        if (file.exists()) {
                            File file1 = new File(sdcard.getAbsolutePath()+"/check","hangup.txt");
                            file1.delete();
                            killCall(context);
                            file.delete();
                            hangup = false;
                            handler.removeMessages(0);
                        }
                        Log.e("Still ","Working");
                        handler.postDelayed(this,delay);
                }
                };
                handler.postDelayed(runnable,delay);
            }
        } else if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            // Outgoing call
            String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d(TAG,"PhoneStateReceiver **Outgoing call " + outgoingNumber);

            setResultData(null); // Kills the outgoing call

        } else {
            Log.d(TAG,"PhoneStateReceiver **unexpected intent.action=" + intent.getAction());
        }
    }



    public boolean killCall(Context context) {
        try {
            handler.removeCallbacks(runnable);
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);
            Log.e("Working here","hhhehehehehhh");

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            Log.d(TAG,"PhoneStateReceiver **" + ex.toString());
            return false;
        }
        return true;
    }

}
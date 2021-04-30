package com.ainuo.douyin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

public abstract class DouyinReceiver extends BroadcastReceiver {
    private static final String ACTION_DOUYIN_RESP = DouyinReceiver.class.getPackage() + ".action.DOUYIN_RESP";

    private static final String KEY_DOUYIN_RESP = "douyin_resp";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TextUtils.equals(ACTION_DOUYIN_RESP, intent.getAction())) {
            Intent resp = intent.getParcelableExtra(KEY_DOUYIN_RESP);
            handleIntent(resp);
        }
    }

    public abstract void handleIntent(Intent intent);

    public static void registerReceiver(Context context, DouyinReceiver receiver) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DOUYIN_RESP);
        context.registerReceiver(receiver, intentFilter);
    }

    public static void unregisterReceiver(Context context, DouyinReceiver receiver) {
        context.unregisterReceiver(receiver);
    }

    public static void sendWechatResp(Context context, Intent resp) {
        Intent intent = new Intent();
        intent.setAction(ACTION_DOUYIN_RESP);
        intent.putExtra(KEY_DOUYIN_RESP, resp);
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }
}
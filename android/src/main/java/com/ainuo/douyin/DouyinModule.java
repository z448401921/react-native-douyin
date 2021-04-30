package com.ainuo.douyin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
//import androidx.core.content.FileProvider;

import com.bytedance.sdk.open.aweme.CommonConstants;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.base.ImageObject;
import com.bytedance.sdk.open.aweme.base.MediaContent;
import com.bytedance.sdk.open.aweme.base.VideoObject;
import com.bytedance.sdk.open.aweme.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory;
import com.bytedance.sdk.open.douyin.DouYinOpenConfig;
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi;
import com.bytedance.sdk.open.douyin.model.OpenRecord;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;

public class DouyinModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    public ReactApplicationContext mContext;
    public static DouYinOpenApi douyinOpenApi;
    private String callerLocalEntry="com.ainuo.douyin.DouyinCallbackActivity";
    public static Intent callbackInit;
    public Promise promise;



    public DouyinModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;

        reactContext.addLifecycleEventListener(this);
    }


    @Override
    public String getName() {
        return "DouYinModule";
    }

    public String getFileUri(String fileName) {
        String filePath = mContext.getFilesDir()  +"/"+fileName;

        Log.d("douyin",filePath);
        File file = new File(filePath);
        String authority = mContext.getPackageName() + ".douyinFileProvider";
        Uri gpxContentUri;
        try {
            gpxContentUri = FileProviderAdapter.getUriForFile(mContext, authority, file);
        } catch (IllegalArgumentException e) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            gpxContentUri = Uri.fromFile(file);
        }
        // 授权给抖音访问路径,这里填抖音包名
        mContext.grantUriPermission("com.ss.android.ugc.aweme",
                gpxContentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return gpxContentUri.toString();   // contentUri.toString() 即是以"content://"开头的用于共享的路径
    }



    @ReactMethod
    public  void init(String appId){
        DouYinOpenApiFactory.init(new DouYinOpenConfig(appId));
        douyinOpenApi = DouYinOpenApiFactory.create(getCurrentActivity());

        DouyinReceiver.registerReceiver(mContext, douyinReceiver);
       // douyinOpenApi.handleIntent(getCurrentActivity().getIntent(),this);
    }

    private DouyinReceiver douyinReceiver = new DouyinReceiver() {
        @Override
        public void handleIntent(Intent intent) {
            if (douyinOpenApi != null) {
                douyinOpenApi.handleIntent(intent, iApiEventHandler);
            }
        }
    };

    private IApiEventHandler iApiEventHandler = new IApiEventHandler() {
        @Override
        public void onReq(BaseReq req) {

        }

        @Override
        public void onResp(BaseResp resp) {
            WritableMap map=Arguments.createMap();
            Log.d("douyin__",String.valueOf(resp.errorCode));
            Log.d("douyin__",String.valueOf(resp.errorMsg));
            Log.d("douyin__",String.valueOf(resp.getType()));
            if (resp.getType() == CommonConstants.ModeType.SHARE_CONTENT_TO_TT_RESP) {
                if (resp.isSuccess()) {
                    promise.resolve(true);
                } else {
                    promise.reject(String.valueOf(resp.errorCode),resp.errorMsg);
                }
            }

            if (resp.getType() == CommonConstants.ModeType.SEND_AUTH_RESPONSE) {
                Authorization.Response response = (Authorization.Response) resp;
                if (resp.isSuccess()) {
                    map.putString("authCode",response.authCode);
                    map.putString("state",response.state);
                    promise.resolve(map);
                }else{
                    promise.reject(response.authCode,response.errorMsg);
                }
            }
        }

        @Override
        public void onErrorIntent(Intent intent) {

        }
    };


        @ReactMethod
    public void shareVideo(String fileName,Boolean isPublish,Promise promise) {
        if (douyinOpenApi.isShareSupportFileProvider() &&
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            this.promise=promise;
            String path=getFileUri(fileName);

            ArrayList<String> videos = new ArrayList<>();
            videos.add(path);

            Share.Request request = new Share.Request();
            VideoObject videoObject = new VideoObject();
            videoObject.mVideoPaths = videos; // 该地方路径可以传FileProvider格式的路径
            MediaContent content = new MediaContent();
            content.mMediaObject = videoObject;
            request.mMediaContent = content;
            //指定当前类名
            request.callerLocalEntry=callerLocalEntry;
            douyinOpenApi.share(request);
            if(douyinOpenApi.isAppSupportShareToPublish() && isPublish) {
                request.shareToPublish = true;
            }

            douyinOpenApi.share(request);
        } else {
            Toast.makeText(getCurrentActivity(), "当前版本不支持", Toast.LENGTH_LONG).show();
        }
    }

    @ReactMethod
    public void record(Promise promise) {
        OpenRecord.Request request = new OpenRecord.Request();
        if (douyinOpenApi != null && douyinOpenApi.isSupportOpenRecordPage()) {  // 判断抖音版本是否支持打开抖音拍摄页
            douyinOpenApi.openRecordPage(request);
        }
    }
   //授权登录
    @ReactMethod
    public void auth(String scope, String state,Promise promise) {
        Authorization.Request request = new Authorization.Request();
        this.promise=promise;
        request.callerLocalEntry=callerLocalEntry;
        request.scope = scope;
        request.state = state;                                 // 用于保持请求和回调的状态，授权请求后原样带回给第三方。
        douyinOpenApi.authorize(request);                    // 优先使用抖音app进行授权，如果抖音app因版本或者其他原因无法授权，则使用wap页授权
    }


    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {

    }
}

/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.baidu.mobads.demo.main;

import android.os.Bundle;
import android.util.Log;

import com.baidu.mobads.BaiduManager;

import org.apache.cordova.*;

public class MainActivity extends CordovaActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 百度广告展现前先调用sdk初始化方法，可以有效缩短广告第一次展现所需时间
        BaiduManager.init(this);
        // enable Cordova apps to be started in the background
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }

        // Set by <content src="index.html" /> in config.xml
        loadUrl(launchUrl);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");

        // 通过BaiduXAdSDKContext.exit()来告知AdSDK，以便AdSDK能够释放资源.
        com.baidu.mobads.production.BaiduXAdSDKContext.exit();

        super.onDestroy();
    }
}

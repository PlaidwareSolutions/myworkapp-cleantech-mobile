package com.example.rfidapp.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebViewUtil {
    @SuppressLint("SetJavaScriptEnabled")
    public static void openWebView(
            WebView webView,
            Activity activity,
            String urlWebView,
            Runnable onPageFinishedCallback,
            Runnable onPageErrorCallback) {

        final boolean[] isPageFullyLoaded = {false};

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                view.clearHistory();
                view.getSettings().setJavaScriptEnabled(true);
                view.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                view.getSettings().setSupportMultipleWindows(true);
                view.getSettings().setDomStorageEnabled(true);
                view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                view.getSettings().setLoadWithOverviewMode(true);
                view.getSettings().setAllowFileAccess(false);
                view.getSettings().setMediaPlaybackRequiresUserGesture(false);
                view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                view.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isPageFullyLoaded[0] = false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("HomeScreenActivity", "shouldOverrideUrlLoading: " + url);
                if (url.startsWith("upi:")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        activity.startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        Toast.makeText(activity, "UPI app is not available", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e("HomeScreenActivity", "onPageFinished: " + url);
                if (isPageFullyLoaded[0]) {
                    if (onPageFinishedCallback != null) {
                        onPageFinishedCallback.run();
                    }
                } else {
                    isPageFullyLoaded[0] = true;
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.e("HomeScreenActivity", "onReceivedError: " + view.getUrl());
                super.onReceivedError(view, request, error);
                if (onPageErrorCallback != null) {
                    onPageErrorCallback.run();
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }

        });

        webView.addJavascriptInterface(new MyJavaScriptInterface(activity,webView),"Android");

        Map<String, String> map = new HashMap<>();
        webView.loadUrl(urlWebView, map);

        /*List<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(4);
        numbers.add(5);

        // Convert the ArrayList to a string (e.g., "numbers=1,2,3,4,5")
        String postData = "";
        StringBuilder postDataBuilder = new StringBuilder();
        postDataBuilder.append("numbers=");
        for (int i = 0; i < numbers.size(); i++) {
            postDataBuilder.append(numbers.get(i));
            if (i < numbers.size() - 1) {
                postDataBuilder.append(",");
            }
            postData = postDataBuilder.toString();
        }
        webView.postUrl(urlWebView,postData.getBytes(StandardCharsets.UTF_8));*/

        webView.setOnLongClickListener(v -> true);
        webView.setLongClickable(false);
    }


    public static class MyJavaScriptInterface {

        Activity activity;
        WebView webView;

        public MyJavaScriptInterface(Activity activity, WebView webView) {
            this.activity = activity;
            this.webView = webView;
        }

        @JavascriptInterface
        public void showToast(String str) {
            activity.runOnUiThread(() -> {
                Util.showToast(activity, str);
               /* String str1 = "Android data transfer";
                webView.loadUrl("javascript:xxx('" + str1 + "')");*/
            });
        }
    }
}
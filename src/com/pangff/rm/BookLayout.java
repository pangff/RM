package com.pangff.rm;

import java.io.ByteArrayInputStream;

import android.R.color;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

/**
 * Created with IntelliJ IDEA. User: marshal Date: 13-4-3 Time: 下午3:59 To change
 * this template use File | Settings | File Templates.
 */
@SuppressLint("NewApi")
public class BookLayout extends FrameLayout  {
	
	private WebView webView;
	private LoadingView loadingView;
	private ImageViewPager viewPager;
	
	private int dpWidth, dpHeight;
	private float scale;
	protected Context ctx;
	private Handler handler = new Handler();
	private int currentPage = 0;
	private int pageCount = 0;
	public BookLayout(Context context) {
		super(context);
		this.init();
	}

	public BookLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}

	public BookLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.init();
	}

	private void init() {
		this.ctx = getContext();
		this.scale = getContext().getResources().getDisplayMetrics().density;

		webView = new WebView(getContext());

		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setJavaScriptEnabled(true);

		webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

		webView.addJavascriptInterface(this, "Android");

		webView.setWebChromeClient(new WebChromeClient() {
			public boolean onConsoleMessage(ConsoleMessage message) {
				Log.d("rm",
						message.message() + " -- From line "
								+ message.lineNumber() + " of "
								+ message.sourceId());
				return true;
			}
		});
		this.addView(webView);
		
		
		viewPager = new ImageViewPager(ctx);
		this.addView(viewPager);
		
		loadingView = new LoadingView(ctx);
		
	}
	
	public void removeLoading(){
		Intent intent = new Intent(Constants.ACTION_REMOVE_LOADING);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        this.getContext().sendBroadcast(intent);
	}
	
	public void onJustScrollFinish() {
	}
	
	/**
	 * 页面加载完成
	 * @param totalWidth
	 * @param pageWidth
	 */
	public void onPageLoaded(final float totalWidth, final float pageWidth) {
		currentPage = 0;
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (totalWidth % pageWidth == 0) {
					pageCount = (int) (totalWidth / pageWidth)-1;
				} else {
					pageCount = (int) (totalWidth / pageWidth);
				}
				if(!ArtBookUtils.hasLoading("rm")){
					BookLayout.this.addView(loadingView);
					webView.loadUrl("javascript:pageScroll(" + currentPage + ")");
				}else{
					viewPager.initViewPager(ctx, Constants.IMAGE_PATH);
				}
			}
		});
	}
	
	/**
	 * 初始化webview截屏
	 */
	public synchronized void screenshot() {

		handler.post(new Runnable() {
			@Override
			public void run() {
				// loadDialog.dismiss();
				Log.e("加载页数:", "loadings" + currentPage);
				Bitmap bitmap = ArtBookUtils.loadBitmapFromView(webView, webView.getWidth(),webView.getHeight());
				ArtBookUtils.saveBitmap(bitmap,"loadings_"+ currentPage );
				currentPage++;
				if (currentPage <= pageCount) {
					webView.loadUrl("javascript:pageScroll(" + currentPage + ")");
				}else{//截图完成，加载显示页面
					removeLoading();
					viewPager.initViewPager(ctx, Constants.IMAGE_PATH);
				}
			}
		});
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		dpWidth = (int) (w / scale);
		dpHeight = (int) (h / scale);
		
		

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view,
					String url) {
				if (url.equals("file:///android_asset/book.css")) {
					StringBuilder builder = new StringBuilder();
					builder.append("" + "        body{"
							+ "           margin:0px;" + "        }"
							+ "        #content{" + "           margin:0px;"
							+ "            width: "
							+ (dpWidth)
							+ "px;"
							+"color:#282828;"
							+ "            height:"
							+ (dpHeight - 20)
							+ "px;"
						    + "            overflow: hidden;"
							+ "        }"
							+ "        header{\n"
							+"font-family:adobeFont;"
							+ "           font-size: 40px;\n"
							+ "        }"
							+ "        article header{"
							+ "            padding:0px;"
							+ "            margin:0px 50px 0px 50px;"
							+ "        }"
							+ "        img{\n"
							+ "            max-width: "
							+ (dpWidth - 110)
							+ "px;\n"
							+"box-shadow: 3px 3px 3px #787878;"
							+ "        }"
							+ "        article {"
							+ "            "
							+ "        }"
							+ "        article{"
							+"line-height:200%;"
							+ "            -webkit-column-width:"
							+ dpWidth
							+ "px;"
		
							+ "            -webkit-column-gap: 10px;"
							+ "            height:"
							+ (dpHeight - 20 - 40)
							+ "px;"
							+"font-family:dqFont;"
							+"             font-size:25px;"
							+ "            padding: 0px;"
							+"margin-top:40px;"
							+ "        }"
							+ "        article *{"
							+ "            padding:5px;"
							+ "            margin:40px 50px 40px 50px;"
							+ "        }"
							+ "        article p{"
							+ "            padding:0px;"
							+ "            margin:15px 50px 15px 50px;"
							+ "        }"
							+"@font-face {font-family: adobeFont;src:url(\"file:///android_asset/fonts/adobe_black.otf\")}@font-face {font-family: dqFont;src:url(\"file:///android_asset/fonts/dq_black.otf\")"
							);
					
					
					ByteArrayInputStream inputStream = new ByteArrayInputStream(
							builder.toString().getBytes());
					return new WebResourceResponse("text/css", "UTF-8",
							inputStream);
				}

				return null;
			}
		});

		webView.loadUrl("file:///android_asset/book.html");
	}
}

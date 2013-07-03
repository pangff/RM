package com.pangff.rm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

/**
 * Created with IntelliJ IDEA. User: marshal Date: 13-3-13 Time: 上午11:48 To
 * change this template use File | Settings | File Templates.
 */
public class ArtBookUtils {
	
	public static String APP_ID = "artbook_v3";

	/**
	 * 获得本地图片
	 * 
	 * @param native_image
	 * @return
	 */
	public static InputStream getNativeFile(String native_image, int height,
			int width) {
		if (native_image == null || native_image.equals("")) {
			return null;
		}
		File file = new File(native_image);
		if (file.exists()) {

			try {
				String strCss = "";
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				FileInputStream is = new FileInputStream(file);
				int count = -1;
				byte buffer[] = new byte[2048];
				if (is != null) {
					Log.v("ArtBook", "－－文件读取成功－－");
					while ((count = is.read(buffer, 0, 2048)) != -1) {
						outStream.write(buffer, 0, count);
					}
					buffer = null;
					String str = new String(outStream.toByteArray());
					Log.v("ArtBook", str);
					String str1 = str.replaceAll("temp_height", height + "px");
					Log.v("ArtBook", "str:" + str1);
					strCss = str1.replaceAll("temp_width", width + "px");
					Log.v("ArtBook", "str:" + strCss);
				}
				return new ByteArrayInputStream(strCss.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	public static InputStream changeWidthAndHeight(Context contex,
			String cssfile, int width, int height) {
		try {
			String strCss = "";
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			InputStream is = contex.getAssets().open(cssfile);
			int count = -1;
			byte buffer[] = new byte[2048];
			if (is != null) {
				Log.v("ArtBook", "－－文件读取成功－－");
				while ((count = is.read(buffer, 0, 2048)) != -1) {
					outStream.write(buffer, 0, count);
				}
				buffer = null;
				String str = new String(outStream.toByteArray());
				Log.v("ArtBook", str);
				String str1 = str.replaceAll("temp_height", height + "px");
				String str2 = str1.replaceAll("temp_image_width", width - 80
						+ "px");
				Log.v("ArtBook", "str:" + str1);
				strCss = str2.replaceAll("temp_width", width + "px");
				Log.v("ArtBook", "str:" + strCss);
			}
			return new ByteArrayInputStream(strCss.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	
	/**
	 * 屏幕宽度
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		final float scale = context.getResources().getDisplayMetrics().widthPixels;
		return (int) (scale);
	}
	
	/**
	 * 屏幕高度
	 */
	public static int getScreenHeight(Context context) {
		final float scale = context.getResources().getDisplayMetrics().heightPixels;
		return (int) (scale);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static Bitmap loadBitmapFromView(View v, int width, int height) {
		v.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		long time = System.currentTimeMillis();
		Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);

		v.measure(View.MeasureSpec.makeMeasureSpec(width,
				View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
				height, View.MeasureSpec.EXACTLY));
		v.layout(0, 0, width, height);
		v.draw(c);

		Log.d("artbook", "load bitmap time: "
				+ (System.currentTimeMillis() - time));
		return b;
	}

	public static Bitmap loadBitmapFromViewByCapture(Context context,WebView v) {
		long time = System.currentTimeMillis();

		Picture picture = v.capturePicture();
		int width = picture.getWidth();
		int height = picture.getHeight();
		Bitmap bitmap = null;
		Bitmap bt = null;
		if (width > 0 && height > 0) {
			// 创建指定高宽的Bitmap对象
			bitmap = Bitmap
					.createBitmap( width, height, Bitmap.Config.ARGB_8888);
			
						// 创建Canvas,并以bitmap为绘制目标
			
			Canvas canvas = new Canvas(bitmap);
			// 将WebView影像绘制在Canvas上
			picture.draw(canvas);
			bt = Bitmap.createScaledBitmap(bitmap, ArtBookUtils.dip2px(context, width), ArtBookUtils.dip2px(context, height), true);

			if(bitmap!=null){
				bitmap.recycle();
				bitmap = null;
			}

			Log.d("artbook",
					"load bitmap by capture time: "
							+ (System.currentTimeMillis() - time));
		}
		return bt;
	}
	
	
	private static Bitmap comp(Bitmap image) {
		 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();		
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		newOpts.inSampleSize = 2;//设置缩放比例
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return bitmap;//压缩好比例大小后再进行质量压缩
	}

	public static void saveBitmap(Bitmap bitmap,String fileName) {
		long time = System.currentTimeMillis();
		String dirPath = Environment.getExternalStorageDirectory() + "/rm";
		File file = new File(dirPath);
		if(!file.exists()){
			file.mkdir();
		}
		String filePath = dirPath+"/"+ fileName;
		try {
			OutputStream stream = new FileOutputStream(filePath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
			stream.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Log.d("artbook", "save bitmap time: "
				+ (System.currentTimeMillis() - time));
	}
	
	public static boolean hasLoading(String dir) {
		String dirPath = Environment.getExternalStorageDirectory() +dir;
		File file = new File(dirPath);
		if(file.exists() && file.list().length>0){
			return true;
		}
		return false;
	}
	
	 
	public static Bitmap loadBitmap(String dir,int index) {
		Bitmap bt =  BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/artbook/"+dir+"/loadings_"+index);
		return  bt;
	}
}

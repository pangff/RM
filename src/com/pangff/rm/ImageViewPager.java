package com.pangff.rm;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created with IntelliJ IDEA. User: marshal Date: 12-10-25 Time: 下午3:31 To
 * change this template use File | Settings | File Templates.
 */
public class ImageViewPager extends ViewPager {

	public ImageViewPager(Context context) {
		super(context);
	}

	public ImageViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean ret = super.onInterceptTouchEvent(ev);
		if (ret) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
		return ret;
	}
	
	int state = 0;
	int currentPage = 0;
	public void initViewPager(final Context context,String path) {
		
		File file = new File(path);
		ComparatorByName comparator = new ComparatorByName(true);  
        final File files[]=file.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.toLowerCase().startsWith(".");
            }
        });
        if (files.length>0) {  
            synchronized (files) {  
                Arrays.sort(files, comparator);
            }  
        }  
        
		this.setAdapter(new PagerAdapter() {

			@Override
			public int getCount() {
				return files!=null?files.length:0;
			}

			@Override
			public boolean isViewFromObject(View view, Object o) {
				return view == o;
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				Log.d("demo", "instantiate item:" + position);
				RelativeLayout view = (RelativeLayout) ((Activity) context)
						.getLayoutInflater().inflate(R.layout.viewpager_item, null);
				ImageView imageView = (ImageView) view.findViewById(R.id.image);
				imageView.setImageBitmap(BitmapFactory.decodeFile(files[position].getAbsolutePath()));
				container.addView(view);
				
				Log.v("PPTREAD", files[position].getAbsolutePath());
				final int location = position;
				imageView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(location<files.length-1){
							ImageViewPager.this.setCurrentItem(location+1, true);
						}
					}
				});
				return view;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				Log.d("demo", "destroy item:" + position);
				container.removeView((View) object);
			}
		});
	}
}

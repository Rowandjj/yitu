package cn.edu.chd.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher.ViewFactory;
import cn.edu.chd.utils.BitmapLruCacheHelper;
import cn.edu.chd.utils.BitmapUtils;
import cn.edu.chd.yitu.R;

/**
 * @author Rowand jj
 * 
 *         应用第一次安装时会有一个引导界面，相关介绍图片可以切换展示， 这个类对此动作进行了封装
 */
public class YiGuideView extends FrameLayout implements OnTouchListener,
		ViewFactory
{
	private OnGuideFinishListener mListener = null;
	/**
	 * 当前浏览的位置
	 */
	private int current_pos = 0;
	/**
	 * 手指按下时的x坐标
	 */
	private float touchDownX = 0;
	/**
	 * 手指放开时的x坐标
	 */
	private float touchUpX = 0;
	private static final int SIZE_NEED_CHANGE = 120;
	private static final String TAG = "UserGuideView";
	private int[] imageData;
	private Context context;
	private static int reqWidth;
	private static int reqHeight;
	
	private ImageSwitcher mImageSwitcher = null;
	private LinearLayout mViewGroup = null;
	
	private ImageView[] mTips = null;

	public YiGuideView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
		
		
		LayoutInflater.from(getContext()).inflate(R.layout.yi_guide_view,this);
		mImageSwitcher =  (ImageSwitcher) findViewById(R.id.yi_guide_vs);
		mViewGroup = (LinearLayout) findViewById(R.id.yi_guide_viewgroup);
		
		
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		reqWidth = manager.getDefaultDisplay().getWidth();
		reqHeight = manager.getDefaultDisplay().getHeight();
		Log.i(TAG, "reqWidth = " + reqWidth + ",reqHeight =" + reqHeight);
		mImageSwitcher.setOnTouchListener(this);
		// 设置视图工厂
		mImageSwitcher.setFactory(this);

	}

	/**
	 * 设置待显示的图片资源
	 * 
	 * @param data
	 */
	public void setImageData(int[] data)
	{
		if (data != null && data.length > 0)
		{
			this.imageData = data;
			// 初始化时显示第一张图片
			mImageSwitcher.setImageDrawable(new BitmapDrawable(this.loadBitmap(imageData[0] + "", reqWidth, reqHeight)));
			
			mTips = new ImageView[data.length];
			for(int i  = 0; i < mTips.length; i++)
			{
				ImageView iv = new ImageView(context);
				mTips[i] = iv;
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));         
	            layoutParams.rightMargin = 7;  
	            layoutParams.leftMargin = 7;  
	            iv.setBackgroundResource(R.drawable.page_indicator_unfocused);  
				mViewGroup.addView(iv,layoutParams);
			}
			setImageBackground(current_pos);
		}
	}
	private void setImageBackground(int selectItems)
	{    
        for(int i = 0; i < mTips.length; i++)
        {    
            if(i == selectItems)
            {    
                mTips[i].setBackgroundResource(R.drawable.page_indicator_focused);    
            }else
            {    
            	mTips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);    
            }    
        }    
	}
	/**
	 * @param listener
	 *            设置引导界面执行完成后的回调函数
	 */
	public void setOnGuideFinishListener(OnGuideFinishListener listener)
	{
		this.mListener = listener;
	}

	public interface OnGuideFinishListener
	{
		public void onGuideFinish();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			touchDownX = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			touchUpX = event.getX();
			if (touchDownX - touchUpX > SIZE_NEED_CHANGE)// 手指从右向左滑动,显示下一张图片
			{
				if(current_pos == imageData.length - 1 )
				{
					if(mListener!=null)
					{
						mListener.onGuideFinish();//执行回调
					}
				}else
				{
					current_pos = current_pos + 1;
					// 设置图片进入和滑出的动画
					Animation in_right = AnimationUtils.loadAnimation(context,R.anim.in_right);
					Animation out_left = AnimationUtils.loadAnimation(context,R.anim.out_left);
					mImageSwitcher.setInAnimation(in_right);
					mImageSwitcher.setOutAnimation(out_left);
					mImageSwitcher.setImageDrawable(new BitmapDrawable(getResources(),loadBitmap(imageData[current_pos] + "", reqWidth,reqHeight)));
					setImageBackground(current_pos);				
				}
				
			} else if (touchUpX - touchDownX > SIZE_NEED_CHANGE)// 手指从左向右滑动，显示上一张图片
			{
				if(current_pos == 0)
				{
					break;
				}else
				{
					current_pos--;
					setImageBackground(current_pos);				
					Animation in_left = AnimationUtils.loadAnimation(context,R.anim.in_left);
					Animation out_right = AnimationUtils.loadAnimation(context,R.anim.out_right);
					mImageSwitcher.setInAnimation(in_left);
					mImageSwitcher.setOutAnimation(out_right);
					mImageSwitcher.setImageDrawable(new BitmapDrawable(getResources(),loadBitmap(imageData[current_pos] + "", reqWidth,reqHeight)));
				}
			}
			break;
		}
		return true;
	}

	@Override
	public View makeView()
	{
		ImageView iv = new ImageView(context);
		iv.setLayoutParams(new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
		iv.setScaleType(ScaleType.FIT_XY);
		return iv;
	}

	/**
	 * 加载一张图片 先从缓存中查找，如果没有在从指定资源路径下寻找
	 * 
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private Bitmap loadBitmap(String resId, int reqWidth, int reqHeight)
	{
		Bitmap bitmap = BitmapLruCacheHelper.getInstance().getBitmapFromMemCache(resId);
		if (bitmap == null)
		{
			bitmap = BitmapUtils.decodeSampledBitmapFromResource(getResources(), Integer.parseInt(resId), reqWidth,reqHeight);
			BitmapLruCacheHelper.getInstance().addBitmapToMemCache(resId,bitmap);
		}
		return bitmap;
	}
}






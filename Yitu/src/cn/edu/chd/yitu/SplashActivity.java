package cn.edu.chd.yitu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import cn.edu.chd.utils.BitmapUtils;

/**
 * @author Rowand jj 
 * 欢迎界面
 */
public class SplashActivity extends Activity
{
	public static final String IS_FIRST_IN = "is_first_in";
	public static final String SP_SPLASH = "sp_splash";

	private static final int GO_HOME = 1;
	private static final int GO_GUIDE = 2;
	private static final int DELAY_MILLIS = 200;//延迟时间
	
	private ImageView splash_iv = null;

	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			case GO_HOME:
				goHome();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_splash);
		init();

		if (isFirstIn())
		{
			//TODO load data here
			handler.sendEmptyMessageDelayed(GO_GUIDE, DELAY_MILLIS);
		} else
		{
			handler.sendEmptyMessageDelayed(GO_HOME, DELAY_MILLIS);
		}
	}

	public void init()
	{
		WindowManager manager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		int reqWidth = manager.getDefaultDisplay().getWidth();
		int reqHeight = manager.getDefaultDisplay().getHeight();
		splash_iv = (ImageView) findViewById(R.id.splash_iv);
		splash_iv.setImageBitmap(BitmapUtils.decodeSampledBitmapFromResource(
				getResources(), R.raw.welcome, reqWidth, reqHeight));
	}

	/**
	 * 判断当前是否是第一次进入应用
	 */
	public boolean isFirstIn()
	{
		SharedPreferences sp = this.getSharedPreferences(SP_SPLASH,
				Context.MODE_PRIVATE);
		return sp.getBoolean(IS_FIRST_IN, true);
	}
	/**
	 * 回应用主界面
	 */
	private void goHome()
	{
		Intent intent = new Intent(this,ChooseModel.class);
		this.startActivity(intent);
		overridePendingTransition(R.anim.slide_remain, R.anim.alpha_1_to_0);
		this.finish();
	}
	/**
	 * 进入引导界面
	 */
	public void goGuide()
	{
		Intent intent = new Intent(this,UserGuide.class);
		this.startActivity(intent);
		overridePendingTransition(R.anim.slide_remain, R.anim.alpha_1_to_0);
		this.finish();
	}
	
}








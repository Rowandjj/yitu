package cn.edu.chd.yitu;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cn.edu.chd.service.ShakeService;
import cn.edu.chd.values.ApplicationValues;
import cn.edu.chd.view.YiSettingButton;
import cn.edu.chd.view.YiSettingButton.OnCheckChangedListener;
import cn.edu.chd.view.YiTitleBar;
import cn.edu.chd.view.YiTitleBar.LeftButtonClickListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.sina.weibo.SinaWeibo.ShareParams;
import cn.sharesdk.system.email.Email;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qzone.QZone;

/**
 * @author Rowand jj
 *
 *设置界面
 */
public class Settings extends Activity implements OnCheckChangedListener,OnSeekBarChangeListener,OnClickListener,OnGestureListener,PlatformActionListener,Callback
{
	/**
	 * 标题栏
	 */
	private YiTitleBar title_bar = null;
	/**
	 * 画图时屏幕是否常亮的开关
	 */
	private YiSettingButton ysb_screen_switch = null;
	/**
	 * "摇一摇"启动应用的开关
	 */
	private YiSettingButton ysb_shake_start = null;
	/**
	 * 显示画布宽度的TextView
	 */
	private TextView tv_canvas_width = null;
	/**
	 * 显示画布高度的TextView
	 */
	private TextView tv_canvas_height = null;
	/**
	 * 设置画布高度的SeekBar
	 */
	private SeekBar seekbar_canvas_size_height = null;
	/**
	 * 设置画布宽度的SeekBar
	 */
	private SeekBar seekbar_canvas_size_width = null;
	/**
	 * 关于
	 */
	private Button but_about = null;
	/**
	 * 用户指引
	 */
	private Button but_user_guide = null;
	/**
	 * 分享
	 */
	private Button but_share = null;
	
	/**
	 * 检测用户手势
	 */
	private GestureDetector mGestureDetector;
	
	/**
	 * 触发滑动的阀值
	 */
	private static final int VALUE = 100;
	private static final String TAG = "Settings";
	
	private int defaultWidth;
	private int defaultHeight;
	
	private Dialog dialog;
	
	private static final String SHARE_TEXT = "易涂是一款基于android的2d趣味涂鸦软件，支持各种图元创建、填色、变换、浏览等操作.";
	
	//----------sns分享平台--------------
	
	private Platform sinaWeibo,qqZone,renren,email,message;
	
	private LinearLayout but_sina,but_qzone,but_renren,but_email,but_message;
	
	private View view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_settings);
		
		//初始化sharesdk
		ShareSDK.initSDK(this);
		ShareSDK.setConnTimeout(5000);
		ShareSDK.setReadTimeout(10000);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		defaultWidth = dm.widthPixels;
		defaultHeight = dm.heightPixels;
		
		mGestureDetector = new GestureDetector(this,this);//注册手势事件
		initTitleBar();//初始化标题栏
		initComponent();//初始化控件
		readConfiguration();//读取配置,必须在初始化控件后执行
		
		initSharePlatform();
		initShareWindow();
	}
	
	//TODO
	private void initSharePlatform()
	{
		sinaWeibo = ShareSDK.getPlatform(SinaWeibo.NAME);
		qqZone = ShareSDK.getPlatform(QZone.NAME);
		renren = ShareSDK.getPlatform(Renren.NAME);
		email = ShareSDK.getPlatform(Email.NAME);
		message = ShareSDK.getPlatform(ShortMessage.NAME);
		
		sinaWeibo.setPlatformActionListener(this);
		qqZone.setPlatformActionListener(this);
		renren.setPlatformActionListener(this);
		email.setPlatformActionListener(this);
		message.setPlatformActionListener(this);
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ShareSDK.stopSDK(this);
	}
	/**
	 * 读取配置信息
	 */
	public void readConfiguration()
	{
		Log.i(TAG,"WI:"+defaultWidth+",HEI"+defaultHeight);
		
		SharedPreferences sp = this.getSharedPreferences(ApplicationValues.Settings.SETTING_PREF,MODE_PRIVATE);
		
		String canvas_width = sp.getString(ApplicationValues.Settings.CANVAS_WIDTH,defaultWidth+"");
		String canvas_height = sp.getString(ApplicationValues.Settings.CANVAS_HEIGHT,defaultHeight+"");
		boolean is_screen_on = sp.getBoolean(ApplicationValues.Settings.SCREEN_STATE,false);
		boolean is_shake_on = sp.getBoolean(ApplicationValues.Settings.SHAKE_MODEL, false);
		//将画布宽高显示到ui
		seekbar_canvas_size_width.setProgress(Integer.parseInt(canvas_width)-defaultWidth);
		seekbar_canvas_size_height.setProgress(Integer.parseInt(canvas_height)-defaultHeight);
		tv_canvas_width.setText(canvas_width);
		tv_canvas_height.setText(canvas_height);
		
		//将开关状态显示到UI
		ysb_screen_switch.setChecked(is_screen_on);
		ysb_shake_start.setChecked(is_shake_on);
	}
	/**
	 * 在activity销毁之前，保存好配置
	 * 
	 * */
	@Override
	protected void onStop()
	{
		super.onStop();
		SharedPreferences sp = this.getSharedPreferences(ApplicationValues.Settings.SETTING_PREF,MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(ApplicationValues.Settings.CANVAS_WIDTH,tv_canvas_width.getText().toString());
		editor.putString(ApplicationValues.Settings.CANVAS_HEIGHT,tv_canvas_height.getText().toString());
		editor.putBoolean(ApplicationValues.Settings.SCREEN_STATE, ysb_screen_switch.isChecked());
		editor.putBoolean(ApplicationValues.Settings.SHAKE_MODEL, ysb_shake_start.isChecked());
		editor.commit();
	}
	/**
	 * 初始化标题栏
	 */
	private void initTitleBar()
	{
		title_bar = (YiTitleBar) findViewById(R.id.ytb_settings);
		title_bar.setTitleName("设置");
		title_bar.setLeftButtonBGResource(R.drawable.setting_title_bar_selector);
		title_bar.setOnLeftButtonClickListener(new LeftButtonClickListener()
		{
			@Override
			public void leftButtonClick()
			{
				Settings.this.finish();
				overridePendingTransition(R.anim.slide_remain, R.anim.out_left);
			}
		});
	}
	
	/**
	 * 初始化控件
	 */
	private void initComponent()
	{
		view = getLayoutInflater().inflate(R.layout.popupwindow_share,null);
		
		ysb_screen_switch = (YiSettingButton) findViewById(R.id.ysb_screen_always_on);
		ysb_shake_start = (YiSettingButton) findViewById(R.id.ysb_shake_start);
		
		tv_canvas_height = (TextView) findViewById(R.id.tv_canvas_size_height);
		tv_canvas_width = (TextView) findViewById(R.id.tv_canvas_size_width);
		
		seekbar_canvas_size_height = (SeekBar) findViewById(R.id.seekbar_canvas_size_height);
		seekbar_canvas_size_width = (SeekBar) findViewById(R.id.seekbar_canvas_size_width);
	
		ysb_screen_switch.setOnCheckChangedListener(this);
		ysb_shake_start.setOnCheckChangedListener(this);
	
		ysb_screen_switch.setContentTitle(R.string.str_screen_on);
		ysb_shake_start.setContentTitle(R.string.str_shake_start);
		
		seekbar_canvas_size_height.setOnSeekBarChangeListener(this);
		seekbar_canvas_size_width.setOnSeekBarChangeListener(this);
		
		//设置最大值
		seekbar_canvas_size_width.setMax(2048-defaultWidth);
		seekbar_canvas_size_height.setMax(2048-defaultHeight);
		
		but_about = (Button) findViewById(R.id.but_about);
		but_user_guide = (Button) findViewById(R.id.but_user_guide);
		but_share = (Button) findViewById(R.id.but_share);

		but_about.setOnClickListener(this);
		but_share.setOnClickListener(this);
		but_user_guide.setOnClickListener(this);
		
		//-----------------------------------
		but_email = (LinearLayout) view.findViewById(R.id.id_share_email);
		but_sina = (LinearLayout) view.findViewById(R.id.id_share_sinaweibo);
		but_qzone = (LinearLayout) view.findViewById(R.id.id_share_qzone);
		but_renren = (LinearLayout) view.findViewById(R.id.id_share_renren);
		but_message = (LinearLayout) view.findViewById(R.id.id_share_message);
		
		Log.i(TAG,but_email.toString());
		
		but_email.setOnClickListener(this);
		but_sina.setOnClickListener(this);
		but_qzone.setOnClickListener(this);
		but_renren.setOnClickListener(this);
		but_message.setOnClickListener(this);
		
	}
	/*
	 * 【重要】重新定义touch事件分发优先级
	 * */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		this.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}
	@Override
	public void onCheckChanged(View view,boolean isChecked)
	{
		switch (view.getId())
		{
		case R.id.ysb_screen_always_on:
			//onstop方法中保存配置即可,此处不需要操作
			break;
		case R.id.ysb_shake_start:
			if(isChecked)
			{
				Intent intent = new Intent(this,ShakeService.class);
				this.startService(intent);
			}else
			{
				Intent intent =new Intent(this,ShakeService.class);
				this.stopService(intent);
			}
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser)
	{
		switch (seekBar.getId())
		{
		case R.id.seekbar_canvas_size_width:
			progress += defaultWidth;
			tv_canvas_width.setText(progress+"");
			break;
		case R.id.seekbar_canvas_size_height:
			progress += defaultHeight;
			tv_canvas_height.setText(progress+"");
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.but_about:
			Intent intent = new Intent(this,CopyRight.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_left, R.anim.slide_remain);
			break;
		case R.id.but_user_guide:
			Intent intent1 = new Intent(this,UserGuide.class);
			this.startActivity(intent1);
			overridePendingTransition(R.anim.in_left, R.anim.slide_remain);
			break;
		case R.id.but_share:
//			initShareWindow();
			dialog.show();
			break;
		//---------------------------------
		case R.id.id_share_email:
			share_to_email();
			break;
		case R.id.id_share_message:
			share_to_message();
			break;
		case R.id.id_share_qzone:
			share_to_qzone();
			break;
		case R.id.id_share_renren:
			share_to_renren();
			break;
		case R.id.id_share_sinaweibo:
			share_to_sina();
			break;
		}
	}
	
	private void initShareWindow()
	{
		dialog = new AlertDialog.Builder(this).create();
		int width = getWindowManager().getDefaultDisplay().getWidth()*4/5;
		dialog.show();
		LayoutParams lp = new LayoutParams(width, LayoutParams.MATCH_PARENT);
		dialog.getWindow().setContentView(view,lp);
		dialog.dismiss();
	}
	
	private void share_to_sina()
	{
		ShareParams sp = new ShareParams();
		sp.setText(SHARE_TEXT);
		sinaWeibo = ShareSDK.getPlatform(SinaWeibo.NAME);
		sinaWeibo.setPlatformActionListener(this);
		sinaWeibo.share(sp);
	}
	
	private void share_to_renren()
	{
		cn.sharesdk.renren.Renren.ShareParams sp = new cn.sharesdk.renren.Renren.ShareParams();
		sp.setText(SHARE_TEXT);
		sp.setTitleUrl("http://www.cnsoftbei.com/");
		sp.setTitle("软件分享");
		sp.setComment("赞");
		renren = ShareSDK.getPlatform(Renren.NAME);
		renren.setPlatformActionListener(this);
		renren.share(sp);
	}
	private void share_to_qzone()
	{
		cn.sharesdk.tencent.qzone.QZone.ShareParams sp = new cn.sharesdk.tencent.qzone.QZone.ShareParams();
		sp.setText(SHARE_TEXT);
		sp.setTitleUrl("http://www.cnsoftbei.com/");
		sp.setTitle("软件分享");
		sp.setSite("易涂");
		sp.setSiteUrl("http://www.cnsoftbei.com/");
		qqZone = ShareSDK.getPlatform(QZone.NAME);
		qqZone.setPlatformActionListener(this);
		qqZone.share(sp);
	}
	
	private void share_to_email()
	{
		cn.sharesdk.system.email.Email.ShareParams sp = new cn.sharesdk.system.email.Email.ShareParams();
		sp.setText(SHARE_TEXT);
		sp.setAddress("http://www.cnsoftbei.com/");
		sp.setTitle("软件分享");
		email = ShareSDK.getPlatform(Email.NAME);
		email.setPlatformActionListener(this);
		email.share(sp);
	}
	private void share_to_message()
	{
		cn.sharesdk.system.text.ShortMessage.ShareParams sp = new cn.sharesdk.system.text.ShortMessage.ShareParams();
		sp.setText(SHARE_TEXT);
		message = ShareSDK.getPlatform(ShortMessage.NAME);
		message.setPlatformActionListener(this);
		message.share(sp);
	}
	
	
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_remain, R.anim.out_left);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return mGestureDetector.onTouchEvent(event);
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY)
	{
		Log.i(TAG,"e1x = "+e1.getX()+",e2x="+e2.getX());
		Log.i(TAG,"vx="+velocityX+",vy="+velocityY);
		if(Math.abs(velocityX) > Math.abs(velocityY) && (e1.getX() - e2.getX() > VALUE))
		{
			this.finish();
			overridePendingTransition(R.anim.slide_remain, R.anim.out_left);
			return true;
		}
		return false;
	}
	@Override
	public boolean onDown(MotionEvent e)
	{
		return false;
	}
	@Override
	public void onShowPress(MotionEvent e)
	{
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		return false;
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY)
	{
		return false;
	}
	@Override
	public void onLongPress(MotionEvent e)
	{
	}

//-----------------share sdk 部分----------------------------------
	@Override
	public void onCancel(Platform arg0, int arg1)
	{
		Message msg = Message.obtain();
		msg.what = 0;
		UIHandler.sendMessage(msg,this);
	}


	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2)
	{
		Message msg = Message.obtain();
		msg.what = 1;
		UIHandler.sendMessage(msg,this);
	}


	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2)
	{
		Message msg = Message.obtain();
		msg.what = -1;
		UIHandler.sendMessage(msg,this);
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		switch (msg.what)
		{
		case 1:
			dialog.dismiss();
			break;
		case 0:
			dialog.dismiss();
			break;
		case -1:
			dialog.dismiss();
			break;
		}
		return true;
	}
	
	
}





















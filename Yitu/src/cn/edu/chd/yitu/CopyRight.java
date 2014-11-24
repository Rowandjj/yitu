package cn.edu.chd.yitu;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import cn.edu.chd.view.SlidingFinishLayout;
import cn.edu.chd.view.SlidingFinishLayout.OnSlidingFinishListener;
import cn.edu.chd.view.YiTitleBar;
import cn.edu.chd.view.YiTitleBar.LeftButtonClickListener;

/**
 * @author Rowand jj
 *
 *关于界面，布局是采用webview形式
 */
public class CopyRight extends Activity
{
	/**
	 * 标题栏
	 */
	private YiTitleBar ytb_about = null;
	private WebView mWebView = null;
	/**
	 * 带滑动删除功能的布局
	 */
	private SlidingFinishLayout msFinishLayout = null;
	/**
	 * html位置
	 */
	private static final String path = "file:///android_asset/about.html";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_about);
		initTitleBar();
		
		mWebView = (WebView) findViewById(R.id.webview_about);
		mWebView.loadUrl(path);
		msFinishLayout = (SlidingFinishLayout) findViewById(R.id.slide_finish_cr);
		msFinishLayout.setTouchView(mWebView);
		//设置滑动事件
		msFinishLayout.setOnSlidingFinishListener(new OnSlidingFinishListener()
		{
			@Override
			public void onSlidingFinish()
			{
				CopyRight.this.finish();//销毁当前activity
			}
		});
	}
	private void initTitleBar()
	{
		ytb_about = (YiTitleBar) findViewById(R.id.ytb_about);
		ytb_about.setTitleName(R.string.str_about);
		ytb_about.setLeftButtonBGResource(R.drawable.setting_title_bar_selector);
		ytb_about.setOnLeftButtonClickListener(new LeftButtonClickListener()
		{
			@Override
			public void leftButtonClick()
			{
				CopyRight.this.finish();
				overridePendingTransition(R.anim.slide_remain, R.anim.out_left);
			}
		});
	}
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_remain, R.anim.out_right);
	}
	
}























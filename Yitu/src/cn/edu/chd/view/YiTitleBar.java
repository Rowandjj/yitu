package cn.edu.chd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.edu.chd.yitu.R;

/**
 * @author Rowand jj
 *自定义的标题栏
 *	<带一个标题和一个按钮>
 */
public class YiTitleBar extends FrameLayout implements OnClickListener
{
	private ImageButton but = null;
	private TextView tv = null;
	private LeftButtonClickListener mListener = null;
	
	public YiTitleBar(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}
	public YiTitleBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		LayoutInflater.from(getContext()).inflate(R.layout.view_yi_title_bar,this);
		but = (ImageButton) findViewById(R.id.title_bar_but_left);
		tv = (TextView) findViewById(R.id.title_bar_tv_center);
		but.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.title_bar_but_left)
		{
			if(mListener != null)
			{
				mListener.leftButtonClick();
			}
		}
	}
	/**
	 * 设置左侧按钮被点击时触发的回调事件
	 * @param listener
	 */
	public void setOnLeftButtonClickListener(LeftButtonClickListener listener)
	{
		this.mListener = listener;
	}
	
	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitleName(String title)
	{
		if(title!=null)
		{
			tv.setText(title);
		}
	}
	public void setTitleName(int resId)
	{
		tv.setText(resId);
	}
	/**
	 * 设置左侧按钮背景
	 * @param resId 资源id
	 */
	public void setLeftButtonBGResource(int resId)
	{
		but.setBackgroundResource(resId);
	}
	public interface LeftButtonClickListener
	{
		public void leftButtonClick();
	}
}












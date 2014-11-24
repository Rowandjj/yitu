package cn.edu.chd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ToggleButton;
import cn.edu.chd.yitu.R;

/**
 * @author Rowand jj
 *	自定义的带toggleButton的按钮（设置界面专用）
 */
public class YiSettingButton extends FrameLayout implements OnClickListener
{
	private Button but = null;
	private ToggleButton tb = null;
	private OnCheckChangedListener mListener = null;//自定义回调接口
	public YiSettingButton(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}
	public YiSettingButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		LayoutInflater.from(getContext()).inflate(R.layout.view_yi_setting_button,this);
		but = (Button) findViewById(R.id.but_setting_content);
		tb = (ToggleButton) findViewById(R.id.toggle_but_setting_onff);
		
		but.setOnClickListener(this);
		tb.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if(mListener != null)
				{
					mListener.onCheckChanged(YiSettingButton.this,isChecked);
				}
			}
		});
	}
	
	/**
	 * 设置选择状态
	 * @param checkState
	 */
	public void setChecked(boolean checkState)
	{
		this.tb.setChecked(checkState);
	}
	/**
	 * 是否被选中
	 * @return
	 */
	public boolean isChecked()
	{
		return this.tb.isChecked();
	}
	@Override
	public void onClick(View v)
	{
		if(R.id.but_setting_content == v.getId())
		{
			tb.toggle();
		}
	}
	/**
	 * 设置标题
	 */
	public void setContentTitle(String title)
	{
		if(title!=null)
		{
			but.setText(title);
		}
	}
	public void setContentTitle(int resId)
	{
		but.setText(resId);
	}
	/**
	 * 设置回调接口
	 * @param mListener
	 */
	public void setOnCheckChangedListener(OnCheckChangedListener mListener)
	{
		this.mListener = mListener;
	}
	public interface OnCheckChangedListener
	{
		public void onCheckChanged(View view,boolean isChecked);
	}
}























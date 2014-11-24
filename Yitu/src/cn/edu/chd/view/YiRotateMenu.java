package cn.edu.chd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import cn.edu.chd.utils.YiAnimation;
import cn.edu.chd.yitu.R;

/**
 * @author Rowand jj
 *
 *具有翻转效果的3级菜单
 */
public class YiRotateMenu extends FrameLayout implements OnClickListener
{
	private ImageButton home = null;
	private ImageButton second = null;

	private ImageButton second_left = null;
	private ImageButton second_right = null;
	
	private ImageButton third_1 = null;
	private ImageButton third_2 = null;
	private ImageButton third_3 = null;
	private ImageButton third_4 = null;
	private ImageButton third_5 = null;
	private ImageButton third_6 = null;
	private ImageButton third_7 = null;
	
	
	private boolean isLevel2Show = true;
	private boolean isLevel3Show = true;
	
	private RelativeLayout level2;
	private RelativeLayout level3;
	
	private boolean isEraserChecked = false;
	private boolean isBarrelChecked = false;
	/**
	 * 回调接口
	 */
	private OnMenuItemClickListener mListener = null;
	
	public YiRotateMenu(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}
	public YiRotateMenu(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.layout_rotate_menu, this);
		initComponent();
	}
	
	/**
	 * 初始化控件
	 */
	private void initComponent()
	{
		home = (ImageButton) findViewById(R.id.ib_home);
		
		second = (ImageButton) findViewById(R.id.second);
		second_left = (ImageButton) findViewById(R.id.second_left);
		second_right = (ImageButton) findViewById(R.id.second_right);
		
		third_1 = (ImageButton) findViewById(R.id.third_1);
		third_2 = (ImageButton) findViewById(R.id.third_2);
		third_3 = (ImageButton) findViewById(R.id.third_3);
		third_4 = (ImageButton) findViewById(R.id.third_4);
		third_5 = (ImageButton) findViewById(R.id.third_5);
		third_6 = (ImageButton) findViewById(R.id.third_6);
		third_7 = (ImageButton) findViewById(R.id.third_7);
		
		level2 = (RelativeLayout) findViewById(R.id.level2);
		level3 = (RelativeLayout) findViewById(R.id.level3);
		
		second_left.setOnClickListener(this);
		second_right.setOnClickListener(this);
		third_1.setOnClickListener(this);
		third_2.setOnClickListener(this);
		third_3.setOnClickListener(this);
		third_4.setOnClickListener(this);
		third_5.setOnClickListener(this);
		third_6.setOnClickListener(this);
		third_7.setOnClickListener(this);
		
		second.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (isLevel3Show)
				{
					// 隐藏3级导航菜单
					YiAnimation.startAnimationOUT(level3, 500, 0);
				} else
				{
					// 显示3级导航菜单
					YiAnimation.startAnimationIN(level3, 500);
				}
				isLevel3Show = !isLevel3Show;
			}
		});

		home.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!isLevel2Show)
				{
					// 显示2级导航菜单
					YiAnimation.startAnimationIN(level2, 500);
				} else
				{
					if (isLevel3Show)
					{
						// 隐藏3级导航菜单
						YiAnimation.startAnimationOUT(level3, 500, 0);
						// 隐藏2级导航菜单
						YiAnimation.startAnimationOUT(level2, 500, 500);
						isLevel3Show = !isLevel3Show;
					} else
					{
						// 隐藏2级导航菜单
						YiAnimation.startAnimationOUT(level2, 500, 0);
					}
				}
				isLevel2Show = !isLevel2Show;
			}
		});

	}
	@Override
	public void onClick(View v)
	{
		if(mListener != null)
		{
			mListener.OnItemClick(v);
		}
		switch (v.getId())
		{
		case R.id.third_4://填充
			if(!isBarrelChecked)
			{
				third_4.setBackgroundResource(R.drawable.barrel_checked);
				if(isEraserChecked)
				{
					third_5.setBackgroundResource(R.drawable.eraser);
					isEraserChecked = false;
				}
			}
			else
			{
				third_4.setBackgroundResource(R.drawable.barrel);
			}
			isBarrelChecked = !isBarrelChecked;
			break;
		case R.id.third_5://橡皮
			if(!isEraserChecked)
			{
				third_5.setBackgroundResource(R.drawable.eraser_checked);
				if(isBarrelChecked)
				{
					third_4.setBackgroundResource(R.drawable.barrel);
					isBarrelChecked = false;
				}
			}
			else
			{
				third_5.setBackgroundResource(R.drawable.eraser);
			}
			isEraserChecked = !isEraserChecked;
			break;
		default:
			break;
		}
	}
	
	/**
	 * 设置回调接口
	 */
	public void setOnMenuItemClickListener(OnMenuItemClickListener mListener)
	{
		this.mListener = mListener;
	}
	/**
	 *
	 *菜单项被点击所触发的回调接口
	 */
	public interface OnMenuItemClickListener
	{
		public void OnItemClick(View view);
	}
}




















package cn.edu.chd.yitu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.chd.utils.BitmapEffectUtils;
import cn.edu.chd.utils.BitmapUtils;

/**
 * @author Rowand jj
 *
 *显示特效
 */
public class ImageEffect extends Activity implements OnClickListener
{ 
	protected static final int OK = 1;
	/**
	 * 取消
	 */
	private Button but_neg = null;
	/**
	 * 确定
	 */
	private Button but_pos = null;
	
	private ImageView iv_show = null;
	
	/**
	 * 特效名称
	 */
	private TextView tv_title = null;
	
	private Bitmap srcBitmap = null;
	
	private Bitmap tempBitmap = null;
	
	private String imagePath = null;
	
	private String[] titles = {
			"原图","灰度","浮雕","黑白","底片","旧时光"
	};
	
	private int width;
	private int height;
	
	private ImageView iv_effect1 = null;
	private ImageView iv_effect2 = null;
	private ImageView iv_effect3 = null;
	private ImageView iv_effect4 = null;
	private ImageView iv_effect5 = null;
	private ImageView iv_effect6 = null;
	
	private ProgressBar pb_op = null;
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if(msg.what == OK)
			{
				iv_show.setImageBitmap(tempBitmap);
				pb_op.setVisibility(View.GONE);
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_image_effect);
		initComponent();
		imagePath = getIntent().getStringExtra("IMAGE");
		width = getWindowManager().getDefaultDisplay().getWidth();
		height = getWindowManager().getDefaultDisplay().getHeight();
		srcBitmap = BitmapUtils.decodeSampledBitmapFromFile(imagePath, width, height);
		iv_show.setImageBitmap(srcBitmap);
		tv_title.setText(titles[0]);
	}
	
	private void initComponent()
	{
		but_neg = (Button) findViewById(R.id.but_ie_neg);
		but_pos = (Button) findViewById(R.id.but_ie_pos);
		
		iv_show = (ImageView) findViewById(R.id.iv_image_effect);
		tv_title = (TextView) findViewById(R.id.tv_ie_content);
		
		pb_op = (ProgressBar) findViewById(R.id.progressbar_effect);
		
		iv_effect1 = (ImageView) findViewById(R.id.iv_effect1);
		iv_effect2 = (ImageView) findViewById(R.id.iv_effect2);
		iv_effect3 = (ImageView) findViewById(R.id.iv_effect3);
		iv_effect4 = (ImageView) findViewById(R.id.iv_effect4);
		iv_effect5 = (ImageView) findViewById(R.id.iv_effect5);
		iv_effect6 = (ImageView) findViewById(R.id.iv_effect6);
		
		but_neg.setOnClickListener(this);
		but_pos.setOnClickListener(this);
		
		iv_effect1.setOnClickListener(this);
		iv_effect2.setOnClickListener(this);
		iv_effect3.setOnClickListener(this);
		iv_effect4.setOnClickListener(this);
		iv_effect5.setOnClickListener(this);
		iv_effect6.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.but_ie_pos:
			try
			{
				File file = new File(imagePath);
				if(file.exists())
					file.delete();
				if(tempBitmap == null)
				{
					tempBitmap = srcBitmap;
				}
				tempBitmap.compress(CompressFormat.JPEG,100,new FileOutputStream(file));
				Toast.makeText(this,"已保存",Toast.LENGTH_SHORT).show();
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			this.finish();
			break;
		case R.id.but_ie_neg:
			this.finish();
			break;
		case R.id.iv_effect1:
			tv_title.setText(titles[0]);
			tempBitmap = srcBitmap;
			iv_show.setImageBitmap(srcBitmap);
			break;
		case R.id.iv_effect2:
			tv_title.setText(titles[1]);
			pb_op.setVisibility(View.VISIBLE);
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					tempBitmap = BitmapEffectUtils.huiduEffect(srcBitmap);
					Message msg = Message.obtain(mHandler,OK);
					msg.sendToTarget();
				}
			}).start();
			
			iv_show.setImageBitmap(tempBitmap);
			break;
		case R.id.iv_effect3:
			pb_op.setVisibility(View.VISIBLE);
			tv_title.setText(titles[2]);
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					tempBitmap = BitmapEffectUtils.fudiaoEffect(srcBitmap);
					Message msg = Message.obtain(mHandler,OK);
					msg.sendToTarget();
				}
			}).start();
			
			iv_show.setImageBitmap(tempBitmap);
			break;
		case R.id.iv_effect4:
			pb_op.setVisibility(View.VISIBLE);
			tv_title.setText(titles[3]);
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					tempBitmap = BitmapEffectUtils.blackAndWhiteEffect(srcBitmap);
					Message msg = Message.obtain(mHandler,OK);
					msg.sendToTarget();
				}
			}).start();
			
			iv_show.setImageBitmap(tempBitmap);
			break;
		case R.id.iv_effect5:
			pb_op.setVisibility(View.VISIBLE);
			tv_title.setText(titles[4]);
			
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					tempBitmap = BitmapEffectUtils.dipianEffect(srcBitmap);
					Message msg = Message.obtain(mHandler,OK);
					msg.sendToTarget();
				}
			}).start();
			
			iv_show.setImageBitmap(tempBitmap);
			break;
		case R.id.iv_effect6:
			pb_op.setVisibility(View.VISIBLE);
			tv_title.setText(titles[5]);
			
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					tempBitmap = BitmapEffectUtils.huaijiuEffect(srcBitmap);
					Message msg = Message.obtain(mHandler,OK);
					msg.sendToTarget();
				}
			}).start();
			break;
		}
	}
	
}















































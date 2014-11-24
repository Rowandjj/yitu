package cn.edu.chd.yitu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import cn.edu.chd.utils.BitmapUtils;
import cn.edu.chd.values.ApplicationValues;

/**
 * @author Rowand jj
 *画布的预览界面
 */
public class CanvasPreview extends Activity implements OnClickListener
{
	/**
	 * 使提示消息消失的消息类型
	 */
	private static final int MSG_DISAPPER_TV = 1;
	/**
	 * 消息延迟时间
	 */
	private static final int DELAY_TIME = 1500;
	
	/**
	 * 图片路径
	 */
	private String imagePath = "";
	
	private TextView tv = null;
	private ImageView iv = null;
	
	private Bitmap bitmap = null;
	
	private Intent to_intent = null;

	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == MSG_DISAPPER_TV)
			{
				tv.startAnimation(AnimationUtils.loadAnimation(CanvasPreview.this,R.anim.alpha_1_to_0_slower));
				tv.setVisibility(View.GONE);
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_canvas_preview);
		Intent intent = getIntent();
		
		int reqWidth = getWindowManager().getDefaultDisplay().getWidth();
		int reqHeight = getWindowManager().getDefaultDisplay().getHeight();
		
		tv = (TextView) findViewById(R.id.tv_c_preview);
		iv = (ImageView) findViewById(R.id.iv_c_preview);
		
		to_intent = new Intent(this,DrawActivity.class);
		
		Message msg = Message.obtain();
		msg.what = MSG_DISAPPER_TV;
		handler.sendMessageDelayed(msg, DELAY_TIME);
		//先要获取类型
		String type = intent.getStringExtra(ApplicationValues.Base.PREVIEW_TYPE);
		if(ApplicationValues.Base.TYPE_CAMERA.equals(type))//画布来自相机
		{
			cameraOperation(intent,reqWidth,reqHeight);
			to_intent.putExtra(ApplicationValues.Base.PREVIEW_TYPE, ApplicationValues.Base.TYPE_CAMERA);
		}else if(ApplicationValues.Base.TYPE_GALLERY.equals(type))//画布来自图库
		{
			galleryOperation(intent,reqWidth,reqHeight);
			to_intent.putExtra(ApplicationValues.Base.PREVIEW_TYPE, ApplicationValues.Base.TYPE_GALLERY);
		}else if(ApplicationValues.Base.TYPE_MY_WORKS.equals(type))//来自我的作品
		{
			myWorkOperation(intent, reqWidth, reqHeight);
			to_intent.putExtra(ApplicationValues.Base.PREVIEW_TYPE, ApplicationValues.Base.TYPE_MY_WORKS);
		}else if(ApplicationValues.Base.TYPE_NORMAL_MODEL.equals(type))//来自模板
		{
			normalModelOperation(intent, reqWidth, reqHeight);
			to_intent.putExtra(ApplicationValues.Base.PREVIEW_TYPE, ApplicationValues.Base.TYPE_NORMAL_MODEL);
			to_intent.putExtra(TabNormalModel.COLOR, intent.getIntExtra(TabNormalModel.COLOR, 0));
		}
		//设置图片点击事件
		iv.setOnClickListener(this);
	}
	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.iv_c_preview)
		{
			to_intent.putExtra(ApplicationValues.Base.IMAGE_STORE_PATH,imagePath);
			startActivity(to_intent);
			CanvasPreview.this.finish();
			overridePendingTransition(R.anim.slide_remain, R.anim.out_left);
		}
	}
	private void cameraOperation(Intent intent,int reqWidth,int reqHeight)
	{
		String image_path = intent.getStringExtra(TabDIY.IMAGE_DATA);
		imagePath = image_path;
		if(image_path == null)
			return;
		bitmap = BitmapUtils.decodeSampledBitmapFromFile(image_path, reqWidth, reqHeight);
		iv.setImageBitmap(bitmap);
	}
	
	private void galleryOperation(Intent intent,int reqWidth,int reqHeight)
	{
		this.cameraOperation(intent, reqWidth, reqHeight);
	}
	
	private void myWorkOperation(Intent intent,int reqWidth,int reqHeight)
	{
		this.cameraOperation(intent, reqWidth, reqHeight);
	}
	
	private void normalModelOperation(Intent intent,int reqWidth,int reqHeight)
	{
		String image_path = intent.getStringExtra(TabDIY.IMAGE_DATA);
		imagePath = image_path;
		if(image_path == null)
			return;
		bitmap = BitmapUtils.decodeSampledBitmapFromResource(getResources(), Integer.parseInt(image_path), reqWidth, reqHeight);
		iv.setImageBitmap(bitmap);
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(bitmap != null)
		{
			bitmap.recycle();
			bitmap = null;
		}
	}
}






























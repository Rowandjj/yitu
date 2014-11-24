package cn.edu.chd.yitu;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ProgressBar;
import cn.edu.chd.adapter.GroupAdapter;
import cn.edu.chd.domain.ImageBean;
import cn.edu.chd.view.YiTitleBar;
import cn.edu.chd.view.YiTitleBar.LeftButtonClickListener;

/**
 * @author Rowand jj
 *
 *图库,方便从其中选择照片，使应用具有一致的风格
 */
public class YiGallery extends Activity
{
	private YiTitleBar ytb_gallery = null;
	private GridView gv = null;
	private List<ImageBean> list = new ArrayList<ImageBean>();
	private static final int SCAN_OK = 1;
	public static final String DATA = "data";
	GroupAdapter adapter = null;
	private ProgressBar pb = null;
	/**
	 * key为文件夹名，value为文件夹下所有图片的路径
	 */
	private Map<String, List<String>> mGroupMap = new HashMap<String, List<String>>();
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == SCAN_OK)
			{
				pb.setVisibility(View.GONE);
				int x = 90;
				int y = 90;
				adapter = new GroupAdapter(list = subGroupOfImage(mGroupMap), gv, YiGallery.this,new Point(x, y));
				gv.setAdapter(adapter);
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_yi_gallery);
		initComponent();
		refreshSdCard();
		getImages();
	}
	
	/**
	 * 初始化控件
	 */
	private void initComponent()
	{
		pb = createProgressBar(this);
		ytb_gallery = (YiTitleBar) findViewById(R.id.ytb_gallery);
		ytb_gallery.setTitleName("选择照片");
		ytb_gallery.setLeftButtonBGResource(R.drawable.setting_title_bar_selector);
		ytb_gallery.setOnLeftButtonClickListener(new LeftButtonClickListener()
		{
			@Override
			public void leftButtonClick()
			{
				YiGallery.this.finish();
				overridePendingTransition(R.anim.slide_remain, R.anim.out_left);
			}
		});
		gv = (GridView) findViewById(R.id.gv_gallery);
		gv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				String folderName = list.get(position).getFolderName();
				List<String> list = mGroupMap.get(folderName);//文件夹下所有图片的路径
				/*点击跳转*/
				Intent intent = new Intent(YiGallery.this,ShowImage.class);
				intent.putStringArrayListExtra(DATA,(ArrayList<String>) list);
				startActivityForResult(intent,1);
				overridePendingTransition(R.anim.in_right,R.anim.slide_remain);
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK)
		{
			if(requestCode == 1)
			{
				setResult(RESULT_OK, data);
				YiGallery.this.finish();
			}
		}
	}
	private void refreshSdCard()
	{
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
		intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
		this.sendBroadcast(intent);//发送广播通知系统重新扫描sd卡
	}
	/**
	 * 异步扫描本地图片
	 */
	public void getImages()
	{
		pb.setVisibility(View.VISIBLE);
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver resolver = YiGallery.this.getContentResolver();
				
				Cursor cursor = resolver.query(uri,null, MediaStore.Images.Media.MIME_TYPE+"=? or "+MediaStore.Images.Media.MIME_TYPE+"=?", new String[]{"image/jpeg","image/png"}, null);
				if(cursor == null)
				{
					return;
				}
				while(cursor.moveToNext())
				{
//					获取图片路径
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//					获取父路径
					File file = new File(path).getParentFile();
					String parentName = file.getName();
					if(parentName.contains("yitu"))//忽略yitu文件夹
					{
						continue;
					}
					List<String> childList = mGroupMap.get(parentName);
					if(childList!=null)
					{
						childList.add(path);
					}else
					{
						childList = new ArrayList<String>();
						childList.add(path);
						mGroupMap.put(parentName,childList);
					}
					
				}
				handler.sendEmptyMessage(SCAN_OK);
			}
		}).start();
	}
	 /**
	  * 将Map转化为合适的list，方便适配器填充数据
	 * @param mGroupMap
	 * @return
	 */
	private List<ImageBean> subGroupOfImage(Map<String, List<String>> mGroupMap)
	{
		 if(mGroupMap == null)
			 return null;
		 
		 List<ImageBean> list = new ArrayList<ImageBean>();
		 for(Map.Entry<String,List<String>> me : mGroupMap.entrySet())
		 {
			 List<String> value = me.getValue();
			 ImageBean bean = new ImageBean();
			 bean.setFolderName(me.getKey());
			 bean.setTopImagePath(value.get(0));
			 
			 list.add(bean);
		 }
		 return list;
	}
	/**
     * 动态创建ProgressBar
     * @param a
     * @return
     */
    public ProgressBar createProgressBar(Activity a)
    {
        FrameLayout rootContainer = (FrameLayout) a.findViewById(android.R.id.content);//固定写法，返回根视图
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        ProgressBar pb = new ProgressBar(a);
        pb.setLayoutParams(lp);
        pb.setVisibility(View.GONE);//默认不显示
        rootContainer.addView(pb);
        return pb;
    }
}

















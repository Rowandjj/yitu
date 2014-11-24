package cn.edu.chd.yitu;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import cn.edu.chd.utils.YiUtils;
import cn.edu.chd.values.ApplicationValues;

/**
 * @author Rowand jj 自定义画布背景 ---拍照作为背景 ---图库中选择
 */
public class TabDIY extends Fragment
{
	private GridView mGridView = null;
	private static final String IMAGE_NAME = "image_name";
	private static final String TEXT_NAME = "text_name";

	private static final String from_gallery = "图库";
	private static final String from_camera = "拍照";
	private static final int CODE_CAMERA = 1;
	private static final int CODE_GALLERY = 2;

	public static final String IMAGE_DATA = "image_data";
	
	private File saveFile = null;
	private static final String TAG = "TabDIY";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.layout_tab_diy, null);
		mGridView = (GridView) view.findViewById(R.id.grid_view_tab_diy);
		
		return view;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> item1 = new HashMap<String, String>();
		item1.put(IMAGE_NAME, R.drawable.from_camera + "");
		item1.put(TEXT_NAME, from_camera);
		Map<String, String> item2 = new HashMap<String, String>();
		item2.put(IMAGE_NAME, R.drawable.from_gallery + "");
		item2.put(TEXT_NAME, from_gallery);
		list.add(item1);
		list.add(item2);
		mGridView.setAdapter(new SimpleAdapter(getActivity(), list,
				R.layout.gridview_item_diy, new String[]
				{ IMAGE_NAME, TEXT_NAME }, new int[]
				{ R.id.diy_item_image, R.id.diy_item_text }));
		mGridView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				if (position == 0)
				{
					toCamera();
				} else if (position == 1)
				{
					toGalley();
				}
			}
		});
	}

	/**
	 * 拍照
	 * @param path
	 */
	private void toCamera()
	{
		String name = YiUtils.getCurrentDate() + ".jpg";
		saveFile = new File(YiUtils.getTempPath(), name);
		/* 打开系统相机拍照的intent */
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(saveFile));
		this.startActivityForResult(intent, CODE_CAMERA);
	}

	/**
	 * 从图库中选择图片
	 */
	private void toGalley()
	{
		Intent intent = new Intent(getActivity(),YiGallery.class);
		this.startActivityForResult(intent, CODE_GALLERY);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK)
		{
			if(requestCode == CODE_CAMERA)
			{
				Intent intent = new Intent(getActivity(),CanvasPreview.class);
				intent.putExtra(IMAGE_DATA,saveFile.getAbsolutePath());
				Log.i(TAG,"path = "+saveFile.getAbsolutePath());
				intent.putExtra(ApplicationValues.Base.PREVIEW_TYPE, ApplicationValues.Base.TYPE_CAMERA);
				this.startActivity(intent);
			}else if(requestCode == CODE_GALLERY)
			{
				String image_path = data.getStringExtra(IMAGE_DATA);
				Intent intent = new Intent(getActivity(),CanvasPreview.class);
				intent.putExtra(IMAGE_DATA,image_path);
				intent.putExtra(ApplicationValues.Base.PREVIEW_TYPE, ApplicationValues.Base.TYPE_GALLERY);
				this.startActivity(intent);
			}
		}
	}
}





















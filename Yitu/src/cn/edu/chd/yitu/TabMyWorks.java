package cn.edu.chd.yitu;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import cn.edu.chd.adapter.YiImageAdapter;
import cn.edu.chd.utils.YiUtils;
import cn.edu.chd.values.ApplicationValues;

/**
 * @author Rowand jj
 * 	 "我的作品"选项卡 生命周期：onCreate->onCreateView
 */
public class TabMyWorks extends Fragment
{
	/**
	 * 是否有作品
	 */
	public boolean has_works;
	private GridView mGridView = null;
	private static final int DEFAULT_WIDTH = 140;//默认宽度
	private static final int DEFAULT_HEIGHT = 170;//默认高度
	private static final String TAG = "TabMyWorks";
	public static final String POSITION = "position";
	/**
	 * 作品文件夹下的图片名
	 */
	private List<String> imageNames;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		has_works = getImageNames().size()==0 ? false : true;//判断指定文件夹中是否有文件存在
		if (has_works)
		{
			imageNames = getImageNames();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = null;
		if (has_works)
		{
			view = inflater.inflate(R.layout.layout_tab_mine, null);
			mGridView = (GridView) view.findViewById(R.id.grid_view_tab_mine_works);
		} else
		{
			view = inflater.inflate(R.layout.layout_tab_mine_noworks, null);
		}
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		if (has_works)
		{
			loadImage();
			mGridView.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id)
				{
					//TODO 浏览
					Intent intent = new Intent(TabMyWorks.this.getActivity(),BrowseWorks.class);
					intent.putExtra(POSITION,position+"");
					TabMyWorks.this.startActivity(intent);
					
				}
			});
			registerForContextMenu(mGridView);//注册上下文菜单
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		//创建上下文菜单
		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.menu_works_item_selected, menu);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();//需要向下转型
		switch (item.getItemId())
		{
		case R.id.id_menu_works_delete:
			delete(info.position);
			return true;
		case R.id.id_menu_works_edit:
			edit(info.position);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
		
	}
	
	/**
	 * 删除作品
	 */
	private void delete(int position)
	{
		String fileName = imageNames.get(position);
		new File(fileName).delete();
		
		ChooseModel activity = (ChooseModel) getActivity();
		activity.getAdapter().reLoad();
	}

	/**
	 * 编辑作品
	 * @param position
	 */
	private void edit(int position)
	{
		Intent intent = new Intent(getActivity(),CanvasPreview.class);
		intent.putExtra(ApplicationValues.Base.PREVIEW_TYPE, ApplicationValues.Base.TYPE_MY_WORKS);
		intent.putExtra(TabDIY.IMAGE_DATA,imageNames.get(position));
		startActivity(intent);
	}
	/**
	 * 获取指定文件夹下的作品图片名称集合
	 * 
	 * @return
	 */
	private List<String> getImageNames()
	{
		String dir = null;
		List<String> imageNames = new ArrayList<String>();//防止null指针
		if (YiUtils.isSDCardAvailable())
		{
			dir = Environment.getExternalStorageDirectory().getAbsolutePath()+ ApplicationValues.Base.SAVE_PATH;
			String[] temp1 = YiUtils.traverseImages(dir);
			if (temp1 != null)
				imageNames = new ArrayList<String>(Arrays.asList(temp1));
		}
		dir = Environment.getDataDirectory().getAbsolutePath()+ ApplicationValues.Base.SAVE_PATH;
		String[] temp2 = YiUtils.traverseImages(dir);
		if (temp2 != null)
		{
			for (String str : temp2)
			{
				imageNames.add(str);
			}
		}
		Log.i(TAG,"---------->GET_IMAGE_NAMES RUN,NUM = "+imageNames.size());
		return imageNames;
	}

	/**
	 * 加载图片并显示到GridView上
	 */
	private void loadImage()
	{
		Point point = new Point(DEFAULT_WIDTH,DEFAULT_HEIGHT);
		mGridView.setAdapter(new YiImageAdapter(getActivity(), imageNames, mGridView, point));
	}
}







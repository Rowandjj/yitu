package cn.edu.chd.yitu;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import cn.edu.chd.adapter.ChildAdapter;
import cn.edu.chd.view.YiTitleBar;
import cn.edu.chd.view.YiTitleBar.LeftButtonClickListener;

/**
 * @author Rowand jj
 *
 *显示一个文件夹中所有的图片,选中即返回画布预览界面
 */
public class ShowImage extends Activity
{
	private GridView gv = null;
	private YiTitleBar ytb_show_image = null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_show_image);
		
		Intent intent = getIntent();
		final ArrayList<String> images = intent.getStringArrayListExtra(YiGallery.DATA);
		File file = new File(images.get(0)).getParentFile();
		String parentName = file.getName();
		gv = (GridView) findViewById(R.id.show_image_grid);
		gv.setAdapter(new ChildAdapter(images, gv, this,new Point(90,90)));
		ytb_show_image = (YiTitleBar) findViewById(R.id.ytb_show_image);
		ytb_show_image.setTitleName(parentName);
		ytb_show_image.setLeftButtonBGResource(R.drawable.setting_title_bar_selector);
		ytb_show_image.setOnLeftButtonClickListener(new LeftButtonClickListener()
		{
			@Override
			public void leftButtonClick()
			{
				ShowImage.this.finish();	
				overridePendingTransition(R.anim.slide_remain, R.anim.out_left);
			}
		});
		gv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Intent ret_intent = new Intent();
				ret_intent.putExtra(TabDIY.IMAGE_DATA,images.get(position));
				setResult(RESULT_OK, ret_intent);
				ShowImage.this.finish();
			}
		});
	}
}






















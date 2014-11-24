package cn.edu.chd.yitu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import cn.edu.chd.values.ApplicationValues;

/**
 * @author Rowand jj
 *系统定义的画布背景
 *	
 */
public class TabNormalModel extends Fragment implements OnItemClickListener
{
	private GridView mGridView = null;
	
	private static final String IMAGE = "image";
	private static final String TEXT = "text";
	public static final String COLOR = "color";

	private static final int[] models = {
		R.drawable.model1_bg,R.drawable.model2_bg,
		R.drawable.model3_bg,R.drawable.model4_bg,
		R.drawable.model5_bg,R.drawable.model6_bg
		};//模板
	private static final int[] colors = {
		0xFFFFFFFF,0xFFDE5510,
		0xFF21AAE7,0xFFA51410,
		0xFFFFBA00,0xFF9C71EF,
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.layout_tab_normal_model,null);
		mGridView = (GridView) view.findViewById(R.id.grid_view_tab_normal_model);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		List<Map<String,String>> data = initData();
		mGridView.setAdapter(new SimpleAdapter(getActivity(), data, R.layout.gridview_item_diy_normal_model, new String[]{IMAGE,TEXT}, new int[]{R.id.model_item_image,R.id.model_item_text}));
		mGridView.setOnItemClickListener(this);
	}
	public List<Map<String,String>> initData()
	{
		List<Map<String,String>> data = new ArrayList<Map<String,String>>();
		
		Map<String,String> map1 = new HashMap<String, String>();
		map1.put(IMAGE, R.drawable.model1_bg+"");//140*120
		map1.put(TEXT,"");
		
		Map<String,String> map2 = new HashMap<String, String>();
		map2.put(IMAGE, R.drawable.model2_bg+"");//140*120
		map2.put(TEXT,"");
		
		Map<String,String> map3 = new HashMap<String, String>();
		map3.put(IMAGE, R.drawable.model3_bg+"");//140*120
		map3.put(TEXT,"");
		
		Map<String,String> map4 = new HashMap<String, String>();
		map4.put(IMAGE, R.drawable.model4_bg+"");//140*120
		map4.put(TEXT,"");
		
		Map<String,String> map5 = new HashMap<String, String>();
		map5.put(IMAGE, R.drawable.model5_bg+"");//140*120
		map5.put(TEXT,"");
		
		Map<String,String> map6 = new HashMap<String, String>();
		map6.put(IMAGE, R.drawable.model6_bg+"");//140*120
		map6.put(TEXT,"");
		
		data.add(map1);
		data.add(map2);
		data.add(map3);
		data.add(map4);
		data.add(map5);
		data.add(map6);
		return data;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		Intent intent = new Intent(this.getActivity(),CanvasPreview.class);
		intent.putExtra(ApplicationValues.Base.PREVIEW_TYPE, ApplicationValues.Base.TYPE_NORMAL_MODEL);
		intent.putExtra(TabDIY.IMAGE_DATA,models[position]+"");
		intent.putExtra(COLOR,colors[position]);
		startActivity(intent);
	}
}











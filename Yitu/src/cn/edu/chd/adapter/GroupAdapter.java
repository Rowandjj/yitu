package cn.edu.chd.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import cn.edu.chd.domain.ImageBean;
import cn.edu.chd.utils.NativeImageLoader;
import cn.edu.chd.utils.NativeImageLoader.NativeImageLoaderCallback;
import cn.edu.chd.yitu.R;

public class GroupAdapter extends BaseAdapter
{
	private List<ImageBean> list;
	private LayoutInflater inflater;
	private GridView mGridView;
	private Point mPoint;
	
	public GroupAdapter(List<ImageBean> list,GridView mGridView,Context context,Point point)
	{
		this.list = list;
		this.inflater = LayoutInflater.from(context);
		this.mGridView = mGridView;
		this.mPoint = point;
	}
	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		return list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder vHolder = null;
		ImageBean bean = list.get(position);
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.grid_group_item,null);
			vHolder = new ViewHolder();
			vHolder.iv = (ImageView) convertView.findViewById(R.id.group_image);
			vHolder.tv = (TextView) convertView.findViewById(R.id.group_title);

			convertView.setTag(vHolder);
		}else
		{
			vHolder = (ViewHolder) convertView.getTag();
		}
		vHolder.iv.setImageResource(R.drawable.pictures_no);
		vHolder.tv.setText(bean.getFolderName());
		vHolder.iv.setTag(bean.getTopImagePath());
		
		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(bean.getTopImagePath(), mPoint, new NativeImageLoaderCallback()
		{
			@Override
			public void onImageLoad(Bitmap bitmap, String path)
			{
				ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
				if(mImageView!=null && bitmap!=null)
				{
					mImageView.setImageBitmap(bitmap);
				}
			}
		});
		if(bitmap!=null)
		{
			vHolder.iv.setImageBitmap(bitmap);
		}
		else
		{
			vHolder.iv.setImageResource(R.drawable.pictures_no);
		}
		return convertView;
	}
	private static class ViewHolder
	{
		ImageView iv;
		TextView tv;
	}
}























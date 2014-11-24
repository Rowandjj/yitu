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
import cn.edu.chd.utils.NativeImageLoader;
import cn.edu.chd.utils.NativeImageLoader.NativeImageLoaderCallback;
import cn.edu.chd.yitu.R;

public class ChildAdapter extends BaseAdapter
{
	private List<String> data;
	private GridView mGridView;
	private LayoutInflater inflater;
	private Point point;
	
	public ChildAdapter(List<String> data,GridView mGridView,Context context,Point point)
	{
		this.inflater = LayoutInflater.from(context);
		this.data = data;
		this.mGridView = mGridView;
		this.point = point;
	
	
	}
	
	@Override
	public int getCount()
	{
		return data.size();
	}

	@Override
	public Object getItem(int position)
	{
		return data.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		String path = data.get(position);
		if(convertView == null)
		{
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.child_group_item,null);
			holder.mImageView = (ImageView) convertView.findViewById(R.id.child_image);
			convertView.setTag(holder);
		}else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mImageView.setTag(path);
		holder.mImageView.setImageResource(R.drawable.pictures_no);
		
		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, point, new NativeImageLoaderCallback()
		{
			@Override
			public void onImageLoad(Bitmap bitmap, String path)
			{
				ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
				if(mImageView!=null && bitmap!=null)
					mImageView.setImageBitmap(bitmap);
			}
		});
		
		if(bitmap!=null)
		{
			holder.mImageView.setImageBitmap(bitmap);
		}
		else
		{
			holder.mImageView.setImageResource(R.drawable.pictures_no);
		}
		
		return convertView;
	}
	
	private static class ViewHolder
	{
		ImageView mImageView;
	}
}












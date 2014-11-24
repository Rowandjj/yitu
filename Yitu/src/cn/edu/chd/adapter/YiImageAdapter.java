package cn.edu.chd.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import cn.edu.chd.utils.BitmapLruCacheHelper;
import cn.edu.chd.utils.NativeImageLoader;
import cn.edu.chd.utils.NativeImageLoader.NativeImageLoaderCallback;
import cn.edu.chd.yitu.R;

/**
 * @author Rowand jj
 *
 *图片适配器
 *	功能：1.优先从缓存中获取图片，如果获取不到再从路径中加载
 *		2.滑动时停止加载，滑动结束开始加载	
 */		
public class YiImageAdapter extends BaseAdapter implements OnScrollListener
{
	private static final String TAG = "YiImageAdapter";
	/**
	 * 绑定的GridView对象
	 */
	private GridView g;
	/**
	 * 上下文
	 */
	private Context context;
	/**
	 * 数据
	 */
	private List<String> data;
	/**
	 * 图片加载器
	 */
	private NativeImageLoader imageLoader;
	/**
	 * 是否第一次进入
	 */
	private boolean isFirstEnter = true;
	
	private Point mPoint;
	
	private int mFirstVisibleItem;
	
	private int mVisibleItemCount;

	public YiImageAdapter(Context context, List<String> data, GridView g,
			Point point)
	{
		this.context = context;
		this.data = data;
		this.g = g;
		this.mPoint = point;
		//初始化图片加载器
		this.imageLoader = NativeImageLoader.getInstance();
		
		//绑定滚动事件监听器
		g.setOnScrollListener(this);
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
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final ViewHolder holder;
		final ImageView imageView;
		String path = data.get(position);
		if (convertView == null)
		{
			holder = new ViewHolder();
			//从布局文件中加载布局
			convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item_my_works,null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.my_works_item_image);
			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		imageView = holder.imageView;
		imageView.setTag(path);
		Bitmap bitmap = BitmapLruCacheHelper.getInstance().getBitmapFromMemCache(path);
		if (bitmap != null)
		{
			imageView.setImageBitmap(bitmap);
		} else
		{
			imageView.setImageResource(R.drawable.default_bg);
			
		}
		return convertView;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
		Log.i(TAG,"SCROLL STATE CHANGE..");
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)// 滑动停止时启动下载图片
		{
			showImage(mFirstVisibleItem, mVisibleItemCount);
		} else
		{
			cancellTask();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount)
	{
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;

		if (isFirstEnter && visibleItemCount > 0)
		{
			showImage(firstVisibleItem, visibleItemCount);
			isFirstEnter = false;
		}
	}

	/**
	 * 显示图片，先从缓存中找，如果没找到就开启线程下载
	 * 
	 * @param firstVisibleItem
	 *            第一个可见项的id
	 * @param visibleItemCount
	 *            可见项的总数
	 */
	private void showImage(int firstVisibleItem, int visibleItemCount)
	{
		for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++)
		{
			String mImageUrl = data.get(i);
			final ImageView mImageView = (ImageView) g.findViewWithTag(mImageUrl);
			imageLoader.loadNativeImage(mImageUrl, mPoint,new NativeImageLoaderCallback()
			{
				@Override
				public void onImageLoad(Bitmap bitmap, String path)
				{
					if(mImageView != null && bitmap!=null)
                    {
                        mImageView.setImageBitmap(bitmap);//下载后直接设置到view对象上
                    }
				}
			});
		}
	}

	/**
	 * 取消任务
	 */
	public void cancellTask()
	{
		imageLoader.cancellTask();
	}
	private static class ViewHolder
	{
		private ImageView imageView;
	}
}

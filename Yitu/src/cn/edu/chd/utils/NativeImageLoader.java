package cn.edu.chd.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;

/**
 * @author Rowand jj
 *
 *图片加载器
 *	用于加载图片，先从缓存中加载图片，如果没有找到则根据路径加载图片
 */
public class NativeImageLoader
{
	private static NativeImageLoader imageLoader = new NativeImageLoader();
	private static final int THREAD_NUM = 2;
	private static final int DEFAULT_WIDTH = 140;//默认宽度
	private static final int DEFAULT_HEIGHT = 170;//默认高度
	protected static final int LOAD_OK = 1;
	private ExecutorService threadPool = null;//线程池
	
	private NativeImageLoader()
	{
	}

	public static NativeImageLoader getInstance()
	{
		return imageLoader;
	}

	/**
	 * 加载图片
	 * @param path
	 * @param mPoint
	 * @param mCallBack
	 * @return
	 */
	public Bitmap loadNativeImage(final String path,Point mPoint, final NativeImageLoaderCallback mCallBack)
	{
		if(path == null)
		{
			return null;
		}
		if(mPoint == null)
		{
			mPoint = new Point(DEFAULT_WIDTH,DEFAULT_HEIGHT);
		}
		final int x = mPoint.x;
		final int y = mPoint.y;
		//从缓存获取图片
		Bitmap bitmap = BitmapLruCacheHelper.getInstance().getBitmapFromMemCache(path);
		if(bitmap != null)
		{
			return bitmap;
		}else
		{
			final Handler handler = new Handler()
			{
				@Override
				public void handleMessage(Message msg)
				{
					if(msg.what == LOAD_OK)
					{
						mCallBack.onImageLoad((Bitmap)msg.obj, path);
					}
				}
			};
			//启动线程加载bitmap，并将加载好的bitmap放到消息队列中
			getThreadPool().execute(new Runnable()
			{
				@Override
				public void run()
				{
					//加载的是经过裁剪的bitmap
					Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFile(path, x, y);
					Message msg = Message.obtain(handler, LOAD_OK, bitmap);
					msg.sendToTarget();
					BitmapLruCacheHelper.getInstance().addBitmapToMemCache(path, bitmap);
				}
			});
		}
		return null;
	}
	/**
	 * 刪除
	 */
	public synchronized void cancellTask()
	{
		if(threadPool != null)
        {
		  threadPool.shutdownNow();
		  threadPool = null;
        }
	}
	
	/**
     * 获取线程池实例
     */
    public ExecutorService getThreadPool()
    {
        if (threadPool == null)
        {
            synchronized (ExecutorService.class)
            {
                if (threadPool == null)
                {
                	threadPool = Executors.newFixedThreadPool(THREAD_NUM);
                }
            }
        }
        return threadPool;
    }

	/**
	 * @author Rowand jj
	 *
	 *回调接口
	 */
	public interface NativeImageLoaderCallback
	{
		public void onImageLoad(Bitmap bitmap,String path);
	}
}












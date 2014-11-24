package cn.edu.chd.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * @author Rowand jj
 *压缩图片的工具类
 */
public final class BitmapUtils
{
	/**
	 * 默认采样比
	 */
	private static final int DEFAULT_SAMPLE_SIZE = 1;
	private static final String TAG = "BitmapUtils";
	
	/**
	 * 根据采样比压缩图片
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res,int resId,int reqWidth,int reqHeight)
	{
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId,opts);
		opts.inSampleSize = caclulateInSampleSize(opts, reqWidth, reqHeight);
		opts.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, opts);
	}
	/**
	 * 跟decodeSampledBitmapFromResource功能相同，获取bitmap的方式不同
	 */
	public static Bitmap decodeSampledBitmapFromFile(String pathName,int reqWidth,int reqHeight)
	{
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, opts);
		opts.inSampleSize = caclulateInSampleSize(opts, reqWidth, reqHeight);
		opts.inJustDecodeBounds = false;
		Log.i(TAG,"OPTS = "+opts.inSampleSize);
		return BitmapFactory.decodeFile(pathName, opts);
	}
	/**
	 * 跟decodeSampledBitmapFromResource功能相同，获取bitmap的方式不同
	 */
	public static Bitmap decodeSampledBitmapFromByteArray(byte[] data,int reqWidth,int reqHeight)
	{
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		opts.inSampleSize = caclulateInSampleSize(opts, reqWidth, reqHeight);
		opts.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
	}
	/**
	 * 不可以两次调用decodeStream,第二次调用InptutStream时，指针已经指向末尾了.
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromStream(InputStream in,int reqWidth,int reqHeight)
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] data = null;
		try
		{
			int len = 0;
			byte[] buf = new byte[1024];
			while((len = in.read(buf)) != -1)
			{
				bout.write(buf, 0, len);
			}
			data = bout.toByteArray();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return decodeSampledBitmapFromByteArray(data, reqWidth, reqHeight);
	}
	
	
	/**
	 * 计算采样比
	 */
	private static int caclulateInSampleSize(BitmapFactory.Options opts,int reqWidth,int reqHeight)
	{
		if(opts == null)
			return DEFAULT_SAMPLE_SIZE;
		int width = opts.outWidth;
		int height = opts.outHeight;
		int sampleSize = DEFAULT_SAMPLE_SIZE;
		if(width > reqWidth || height > reqHeight)
		{
			int widthRatio = (int) (width/(float)reqWidth);
			int heightRatio = (int) (height/(float)reqHeight);
			
			sampleSize = (widthRatio > heightRatio) ? heightRatio : widthRatio;
		}
		return sampleSize;
	}
}
























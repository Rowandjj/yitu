package cn.edu.chd.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;

import cn.edu.chd.values.ApplicationValues;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

public class YiUtils
{
	private static final String TAG = "YiUtils";

	/**
	 * 判断sd卡是否可用
	 * @return
	 */
	public static boolean isSDCardAvailable()
	{
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getCurrentDate()
	{
		return DateFormat.format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)).toString();
	}
	
	/**
	 * 遍历指定文件夹下的所有图片,返回图片名集合
	 *	<不考虑子文件夹>
	 */
	public static String[] traverseImages(String dir)
	{
		/*参数判断*/
		if(dir == null)
			return null;
		File path = new File(dir);
		if(!path.exists() || !path.isDirectory())
		{
			return null;
		}
		
		String[] imageNames = path.list(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String filename)
			{
				return filename.endsWith("jpg") || filename.endsWith("jpeg")||filename.endsWith("bmp")||filename.endsWith("png");
			}
		});
		if(imageNames == null)
		{
			return null;
		}
		String[] data = new String[imageNames.length];
		for(int i = 0; i < imageNames.length; i++)
		{
			data[i] = dir+"/"+imageNames[i];
			Log.i(TAG,"FILENAME:"+data[i]);
		}
		return data;
	}
	/**
	 * 获取存储路径
	 * @return
	 */
	public static String getPath()
	{
		String path = null;
		if(YiUtils.isSDCardAvailable())
		{
			path = Environment.getExternalStorageDirectory().getAbsolutePath()+ApplicationValues.Base.SAVE_PATH;
		}else
		{
			path = Environment.getDataDirectory().getAbsolutePath()+ApplicationValues.Base.SAVE_PATH;
		}
		File dir = new File(path);
		if(!dir.exists())
		{
			dir.mkdirs();
		}
		return path;
	}
	/**
	 * @return
	 */
	public static String getTempPath()
	{
		String path = null;
		if(YiUtils.isSDCardAvailable())
		{
			path = Environment.getExternalStorageDirectory().getAbsolutePath()+ApplicationValues.Base.TEMP_PATH;
		}else
		{
			path = Environment.getDataDirectory().getAbsolutePath()+ApplicationValues.Base.TEMP_PATH;
		}
		File dir = new File(path);
		if(!dir.exists())
		{
			dir.mkdirs();
		}
		return path;
	}
	/**
	 * 将流中的图片拷贝到指定位置
	 * @param in
	 * @param file
	 */
	public static void copyBitmapFromStream(InputStream in,File file)
	{
		FileOutputStream fout = null;
		try
		{
			fout = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len = in.read(buffer)) != -1)
			{
				fout.write(buffer,0,len);
			}
			fout.close();
			in.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}







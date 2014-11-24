package cn.edu.chd.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * @author Rowand jj
 *
 *图片的效果
 */
public class BitmapEffectUtils
{
	private BitmapEffectUtils(){}
	
	/**
	 * 黑白
	 * @return
	 */
	public static Bitmap blackAndWhiteEffect(Bitmap bitmap)
	{
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap base = Bitmap.createBitmap(width, height,bitmap.getConfig());
        
        Canvas canvas = new Canvas(base);//以base为模板创建canvas对象
        canvas.drawBitmap(bitmap, new Matrix(),new Paint());
        
        for(int i = 0; i < width; i++)//遍历像素点
        {
            for(int j = 0; j < height; j++)
            {
                int color = bitmap.getPixel(i, j);
                
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                int a = Color.alpha(color);
                
                int avg = (r+g+b)/3;//RGB均值
                
                if(avg >= 100)//100可以理解为经验值
                {
                    base.setPixel(i, j,Color.argb(a, 255, 255, 255));//设为白色     
                }
                else
                {
                    base.setPixel(i, j,Color.argb(a, 0, 0, 0));//设为黑色 
                }
            }
        }
        return base;
	}
	/**
	 * 底片
	 * @return
	 */
	public static Bitmap dipianEffect(Bitmap bitmap)
	{
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //创建可写的bitmap
        Bitmap base = Bitmap.createBitmap(width,height,bitmap.getConfig());
        Paint paint = new Paint();//新建画笔
//      新建画布
        Canvas canvas = new Canvas(base);
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                int color = bitmap.getPixel(i,j);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                int a = Color.alpha(color);
                //修改图片每个点的像素的rgb值
                base.setPixel(i,j, Color.argb(a, 255-r, 255-g, 255-b));
            }
        }
        return base;
    }
	/**
	 * 灰度
	 * @param bitmap
	 * @return
	 */
	public static Bitmap huiduEffect(Bitmap bitmap)
	{
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap base = Bitmap.createBitmap(width, height,bitmap.getConfig());
        
        Canvas canvas = new Canvas(base);//以base为模板创建canvas对象
        canvas.drawBitmap(bitmap, new Matrix(),new Paint());
        
        for(int i = 0; i < width; i++)//遍历像素点
        {
            for(int j = 0; j < height; j++)
            {
                int color = bitmap.getPixel(i, j);
                
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                int a = Color.alpha(color);
            
                int value = (int) (r*0.3+g*0.59+b*0.11);//经验公式
                int newColor = Color.argb(a, value, value, value);
                base.setPixel(i, j,newColor);
            }
        }
        return base;
	}
	
	/**
	 * 怀旧
	 * @param bitmap
	 * @return
	 */
	public static Bitmap huaijiuEffect(Bitmap bitmap)
	{
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap base = Bitmap.createBitmap(width, height,bitmap.getConfig());
        
        Canvas canvas = new Canvas(base);//以base为模板创建canvas对象
        canvas.drawBitmap(bitmap, new Matrix(),new Paint());
        
        int newR,newG,newB;
        for(int i = 0; i < width; i++)//遍历像素点
        {
            for(int j = 0; j < height; j++)
            {
                int current_color = bitmap.getPixel(i, j);
                int r = Color.red(current_color);
                int g = Color.green(current_color);
                int b = Color.blue(current_color);
                int a = Color.alpha(current_color);
                
                /*经验公式*/
                newR = (int) (0.393 * r + 0.769 * g + 0.189 * b);  
                newG = (int) (0.349 * r + 0.686 * g + 0.168 * b);  
                newB = (int) (0.272 * r + 0.534 * g + 0.131 * b);  
                
                int newColor = Color.argb(a, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255 : newB); 
                base.setPixel(i, j, newColor);
            }
        }
	   return base;      
	}
	
	/**
	 * 浮雕
	 * @param bitmap
	 * @return
	 */
	public static Bitmap fudiaoEffect(Bitmap bitmap)
	{
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap base = Bitmap.createBitmap(width, height,bitmap.getConfig());
        
        Canvas canvas = new Canvas(base);//以base为模板创建canvas对象
        canvas.drawBitmap(bitmap, new Matrix(),new Paint());
        
        int pre_color = 0;//记录上一个点的rgb值
        for(int i = 0; i < width; i++)//遍历像素点
        {
            for(int j = 0; j < height; j++)
            {
                int current_color = bitmap.getPixel(i, j);
                
                int r = Color.red(current_color) - Color.red(pre_color)+128;
                int g = Color.green(current_color) - Color.green(pre_color)+128;
                int b = Color.blue(current_color) - Color.blue(pre_color)+128;
                int a = Color.alpha(current_color);
                
                int newcolor = (int)(r * 0.3 + g * 0.59 + b * 0.11);//灰度处理
                
                base.setPixel(i, j, Color.argb(a, newcolor, newcolor, newcolor));
                pre_color = current_color;
            }
        }
        return base;
	}
}


























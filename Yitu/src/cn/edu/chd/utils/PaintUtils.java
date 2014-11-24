package cn.edu.chd.utils;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Paint.Style;

/**
 * @author Rowand jj
 * 
 *         绘图工具类
 * 
 */
public class PaintUtils
{
	private PaintUtils(){}
	private static InnerStack mStack = new InnerStack();
	
	/**
	 * 能够绘制虚线的paint
	 */
	public static Paint getDashedPaint()
	{
		Paint p  = new Paint();
		p.setStyle(Style.STROKE);
		p.setAntiAlias(true);
		p.setDither(true);
		p.setStrokeJoin(Paint.Join.ROUND);
		p.setStrokeCap(Paint.Cap.ROUND);
		p.setStrokeWidth(3);
		PathEffect effects = new DashPathEffect(new float[]{5,10,5,10},2);
		p.setPathEffect(effects);
		
		return p;
	}
	
	/**
	 * 填色操作
	 */
	@Deprecated
	public static void fill(Bitmap bitmap,int x,int y,int width,int height,int new_color)
	{
		if(isBlank(bitmap, x, y))//空白区域
		{
			flood_fill(bitmap, x, y, width, height, new_color, Color.TRANSPARENT);
		}
		else//非空白区域
		{
			int oldColor = getColor(bitmap, x, y);
			flood_fill(bitmap, x, y, width, height, new_color, oldColor);
		}
	}
	/**
	 * 判断是否已填色
	 */
	@Deprecated
	private static boolean isBlank(Bitmap bitmap,int x,int y)
	{
		return bitmap.getPixel(x, y)==Color.TRANSPARENT;
	}
	/**
	 * 基于扫描线的像素填充方法
	 * ------------------------
	 * @param bitmap 待填充的bitmap
	 * @param x 起始点横坐标
	 * @param y 起始点纵坐标
	 * @param width 宽度
	 * @param height 高度
	 * @param new_color 新颜色
	 * @param old_color 旧颜色
	 */
	@Deprecated
	private static void flood_fill(Bitmap bitmap,int x,int y,int width,int height,int new_color,int oldColor)
	{
		if(oldColor == new_color) 
		{  
	        return;  
	    }  
        int y1;   
        boolean spanLeft, spanRight;  
        mStack.push(x, y);  
          
        while(true)  
        {      
            x = mStack.popX();  
            if(x == -1) 
            	return;  
            y = mStack.popY();  
            y1 = y;  
            while(y1 >= 0 && getColor(bitmap,x, y1) == oldColor) y1--; // go to line top/bottom  
            y1++; // start from line starting point pixel  
            spanLeft = spanRight = false;  
            while(y1 < height && getColor(bitmap,x, y1) == oldColor)  
            {  
                setColor(bitmap,x, y1, new_color);  
                if(!spanLeft && x > 0 && getColor(bitmap,x - 1, y1) == oldColor)// just keep left line once in the stack  
                {  
                	mStack.push(x - 1, y1);  
                    spanLeft = true;  
                }  
                else if(spanLeft && x > 0 && getColor(bitmap,x - 1, y1) != oldColor)  
                {  
                    spanLeft = false;  
                }  
                if(!spanRight && x < width - 1 && getColor(bitmap,x + 1, y1) == oldColor) // just keep right line once in the stack  
                {  
                	mStack.push(x + 1, y1);  
                    spanRight = true;  
                }  
                else if(spanRight && x < width - 1 && getColor(bitmap,x + 1, y1) != oldColor)  
                {  
                    spanRight = false;  
                }   
                y1++;  
            }  
        }  
	}
	private static int getColor(Bitmap bitmap,int x,int y)
	{
		return bitmap.getPixel(x, y);
	}
	private static void setColor(Bitmap bitmap,int x,int y,int color)
	{
		bitmap.setPixel(x, y, color);
	}
	
	/**
	 *内部栈
	 */
	private static class InnerStack
	{
		private List<Integer> listX = new ArrayList<Integer>();
		private List<Integer> listY = new ArrayList<Integer>();
	
		public void push(int x,int y)
		{
			listX.add(x);
			listY.add(y);
		}
		public int popX()
		{
			if(!listX.isEmpty())
				return listX.remove(listX.size()-1);
			else
				return -1;
		}
		public int popY()
		{
			if(!listY.isEmpty())
				return listY.remove(listY.size()-1);
			else
				return -1;
		}
	}
}





















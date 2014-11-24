package cn.edu.chd.domain;

import android.graphics.Canvas;

/**
 * @author Rowand jj
 * 
 * 所有图元(直线，贝塞尔...)需要继承此接口
 */
public interface Tuyuan
{
	/**
	 * 只有等移动距离超过这个值才会绘图
	 */
	public static final float TOUCH_TOLERANCE = 4.0f;
	
	/**
	 * 绘制图元
	 */
	public void draw(Canvas canvas);

	/**
	 * 手指按下
	 */
	public void touchDown(float x, float y);

	/**
	 * 手指移动
	 */
	public void touchMove(float x, float y);

	/**
	 * 手指松开
	 */
	public void touchUp(float x, float y);

	/**
	 * 是否已经绘制了图元
	 */
	public boolean hasDraw();
	
	/**
	 * 是否包含点(x,y)
	 */
	public boolean contains(float x,float y);
	
	/**
	 * 高亮显示
	 */
	public void setHighLight(Canvas canvas);
	
	/**
	 * 选择图元
	 */
	public void checked(Canvas canvas);
	
	/**
	 * 缩放
	 */
	public void scale(float offsetX,float offsetY);
	
	/**
	 * 平移
	 */
	public void translate(float offsetX,float offsetY);
	
	/**
	 * 旋转
	 */
	public void rotate(float degrees);
	/**
	 * 填充当前图元
	 */
	public void fill(int color);
	
	/**
	 * 当前图元是否被填充
	 */
	public boolean isFilled();
	
	/**
	 * 拷贝当前图元，拷贝的图元形状与原图元相同，位置为圆图元的右下方(+10,+10)
	 */
	public Tuyuan copy();
}




















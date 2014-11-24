package cn.edu.chd.graphics;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import cn.edu.chd.domain.Tuyuan;
import cn.edu.chd.domain.PaintStyle;
import cn.edu.chd.utils.PaintUtils;
import cn.edu.chd.values.ApplicationValues;

/**
 * @author Rowand jj
 *
 *多边形
 *
 *
 *
 */
public class Polygn implements Tuyuan
{
	/**
	 * 划线起始点横坐标
	 */
	private float mCurrentX = 0.0f;
	/**
	 * 划线起始点纵坐标
	 */
	private float mCurrentY = 0.0f;
	/**
	 * 画笔
	 */
	private Paint mPenPaint = null;
	/**
	 * 路径
	 */
	private Path mPath = new Path();
	
	/**
	 * 转折的回调接口
	 */
	private OnTurnListener mListener = null;
	
	private boolean mHasDraw = false;
	
	private Path oldPath = new Path();
	
	/**
	 * 图元是否被填充
	 */
	private boolean isFilled = false;
	
	/**
	 * 代表从起点到终点的一条直线
	 */
	private Path endPath = new Path();
	
	private float startX = 0.0f;
	private float startY = 0.0f;
	
	private float tempX = 0.0f;
	private float tempY = 0.0f;
	
	private boolean isTouchUp = false;
	
	/**
	 * 最终路径
	 */
	private Path finalPath = new Path();
	
	/**
	 * 组成最终路径的点集合
	 */
	private List<PointF> points = new ArrayList<PointF>();
	
	private int penSize;
	private int penColor;
	private PaintStyle paintStyle;
	private int alpha;
	
	
	public Polygn(int penSize, int penColor,int alpha,PaintStyle paintStyle)
	{
		mPenPaint = paintStyle.getPaintStyle();
		
		mPenPaint.setStrokeWidth(penSize);
		mPenPaint.setColor(penColor);
		mPenPaint.setAlpha(255-alpha);
		
		this.penSize = penSize;
		this.penColor = penColor;
		this.alpha = alpha;
		this.paintStyle = paintStyle;
	}
	@Override
	public void draw(Canvas canvas)
	{
		if(canvas != null)
		{
			if(isTouchUp)//如果多边形已经生成，那么直接用finalpath，之前的oldpath和mpath都作废
			{
				canvas.drawPath(finalPath, mPenPaint);
				return;
			}
			//绘制过去的路径
			canvas.drawPath(oldPath, mPenPaint);
			if(mHasDraw && !isTouchUp)//从起始点到终点画一条线
			{
				canvas.drawPath(mPath, mPenPaint);
				endPath.reset();
				endPath.moveTo(startX, startY);
				endPath.lineTo(tempX, tempY);
				canvas.drawPath(endPath, mPenPaint);
			}
		}
	}

	@Override
	public void touchDown(float x, float y)
	{
		mPath.reset();
		mPath.moveTo(x, y);
		savePoint(x, y);
		startX = x;
		startY = y;
		
		points.add(new PointF(x, y));
	}

	@Override
	public void touchMove(float x, float y)
	{
		tempX = x;
		tempY = y;
		if(isMoved(x, y))
		{
			if(mListener!=null && mListener.onTurn())//如果当前点是转折点就记录当前的位置，并作为新起始点
			{
				oldPath.addPath(mPath);
				savePoint(x, y);
				mPath = new Path();
				points.add(new PointF(x, y));
			}
			
			drawPolygnLine(x, y);
			mHasDraw = true;
		}
	}
	
	@Override
	public void touchUp(float x, float y)
	{
		drawPolygnLine(x, y);
		oldPath.addPath(endPath);
		oldPath.addPath(mPath);
		isTouchUp = true;
		points.add(new PointF(x, y));
		
		//当touchup事件触发后，代表多边形绘制完毕，此时根据之前的点集生成一条代表此多边形的path，以后对多边形的
		//平移填色等操作均在此path上进行
		
		//生成最终路径
		for(int i = 0; i < points.size(); i++)
		{
			if(i == 0)//第一个点
			{
				finalPath.moveTo(points.get(i).x,points.get(i).y);
			}
			else
			{
				finalPath.lineTo(points.get(i).x, points.get(i).y);
			}
		}
		finalPath.close();//闭合最终路径（即从起点到终点绘制一条线）
	}
	
	/**
	 * 保存当前点
	 */
	private void savePoint(float x, float y)
	{
		mCurrentX = x;
		mCurrentY = y;
	}

	private boolean isMoved(float x, float y)
	{
		float dx = Math.abs(x - mCurrentX);
		float dy = Math.abs(y - mCurrentY);
		boolean isMoved = dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE;
		return isMoved;
	}
	@Override
	public boolean hasDraw()
	{
		return mHasDraw;
	}
	
	private void drawPolygnLine(float x,float y)
	{
		mPath.reset();
		mPath.moveTo(mCurrentX, mCurrentY);
		mPath.lineTo(x, y);
	}
	
	/**
	 * @param listener
	 * 必须注册回调事件
	 */
	public void setOnTurnListener(OnTurnListener listener)
	{
		this.mListener = listener;
	}
	/**
	 * @author Rowand jj
	 *转折回调事件
	 */
	public interface OnTurnListener
	{
		public boolean onTurn();
	}
	@Override
	public boolean contains(float x, float y)
	{
		RectF bounds = new RectF();
		finalPath.computeBounds(bounds, true);
		return bounds.contains(x, y);
	}
	@Override
	public void setHighLight(Canvas canvas)
	{
		checked(canvas, Color.YELLOW);		
	}
	@Override
	public void checked(Canvas canvas)
	{
		checked(canvas,Color.BLACK);
	}
	private void checked(Canvas canvas,int color)
	{
		Paint p = PaintUtils.getDashedPaint();
		p.setColor(color);
		RectF bounds = new RectF();
		
		//计算边界
		finalPath.computeBounds(bounds, true);
		canvas.drawRect(bounds, p);
		//绘制关键点
		Paint pTemp = new Paint();
		pTemp.setStyle(Style.FILL);
		pTemp.setColor(color);
		canvas.drawCircle(bounds.left, bounds.top,ApplicationValues.Base.RADIUS,pTemp);
		canvas.drawCircle(bounds.left, bounds.bottom,ApplicationValues.Base.RADIUS,pTemp);
		canvas.drawCircle(bounds.right, bounds.top,ApplicationValues.Base.RADIUS,pTemp);
		canvas.drawCircle(bounds.right, bounds.bottom,ApplicationValues.Base.RADIUS,pTemp);
	}
	@Override
	public void translate(float offsetX, float offsetY)
	{
		Matrix matrix = new Matrix();
		matrix.setTranslate(offsetX, offsetY);
		finalPath.transform(matrix);
	}
	@Override
	public void fill(int color)
	{
		mPenPaint.setColor(color);
		mPenPaint.setStyle(Style.FILL);
		isFilled = true;
	}
	@Override
	public boolean isFilled()
	{
		return isFilled;
	}
	private void setPath(Path mPath)
	{
		this.finalPath = mPath;
	}
	private void setTouchUp(boolean istouchup)
	{
		this.isTouchUp = istouchup;
	}
	@Override
	public void scale(float offsetX, float offsetY)
	{
		Matrix matrix = new Matrix();
		RectF bounds = new RectF();
		finalPath.computeBounds(bounds, true);
		matrix.setScale(offsetX, offsetY, (bounds.right+bounds.left)/2.0f,(bounds.top+bounds.bottom)/2.0f);
		finalPath.transform(matrix);
	}
	@Override
	public void rotate(float degrees)
	{
		Matrix matrix = new Matrix();
		RectF bounds = new RectF();
		finalPath.computeBounds(bounds, true);
		matrix.setRotate(degrees,(bounds.right+bounds.left)/2.0f,(bounds.top+bounds.bottom)/2.0f);
		finalPath.transform(matrix);		
	}
	@Override
	public Tuyuan copy()
	{
		Polygn copedTuyuan = new Polygn(penSize, penColor, alpha, paintStyle.newInstance());
		if(isFilled)
		{
			copedTuyuan.fill(mPenPaint.getColor());
		}
		copedTuyuan.setPath(new Path(finalPath));
		copedTuyuan.translate(40,40);
		copedTuyuan.setTouchUp(true);
		return copedTuyuan;
	}
}















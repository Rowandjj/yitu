package cn.edu.chd.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import cn.edu.chd.domain.Tuyuan;
import cn.edu.chd.domain.PaintStyle;
import cn.edu.chd.utils.PaintUtils;
import cn.edu.chd.values.ApplicationValues;

/**
 *用于绘制直线
 */
public class Line implements Tuyuan
{
	/**
	 * 划线起始点横坐标
	 */
	private float mSrcX = 0.0f;
	/**
	 * 划线起始点纵坐标
	 */
	private float mSrcY = 0.0f;
	
	/**
	 * 画笔
	 */
	private Paint mPenPaint = null;
	/**
	 * 路径
	 */
	private Path mPath = new Path();
	
	private boolean mHasDraw = false;
	
	private int penSize;
	private int penColor;
	private PaintStyle paintStyle;
	private int alpha;
	
	/**
	 * 图元是否被填充
	 */
	private boolean isFilled = false;
	
	public Line(int penSize, int penColor,int alpha,PaintStyle paintStyle)
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
			canvas.drawPath(mPath, mPenPaint);
		}
	}

	@Override
	public void touchDown(float x, float y)
	{
		mPath.reset();
		mPath.moveTo(x, y);
		savePoint(x, y);
	}

	@Override
	public void touchMove(float x, float y)
	{
		if(isMoved(x, y))
		{
			drawLine(x, y);
			mHasDraw = true;
		}
	}

	@Override
	public void touchUp(float x, float y)
	{
		drawLine(x, y);
	}

	@Override
	public boolean hasDraw()
	{
		return mHasDraw;
	}
	
	private void savePoint(float x, float y)
	{
		mSrcX = x;
		mSrcY = y;
	}

	private boolean isMoved(float x, float y)
	{
		float dx = Math.abs(x - mSrcX);
		float dy = Math.abs(y - mSrcY);
		boolean isMoved = dx >= (TOUCH_TOLERANCE+3) || dy >= (TOUCH_TOLERANCE+3);
		return isMoved;
	}
	/**
	 * 划线
	 */
	private void drawLine(float x,float y)
	{
		mPath.reset();
		mPath.moveTo(mSrcX, mSrcY);
		mPath.lineTo(x, y);
	}
	@Override
	public boolean contains(float x, float y)
	{
		RectF bounds = new RectF();
		mPath.computeBounds(bounds, true);//todo 检查坐标
		return bounds.contains(x, y);
	}
	private void setPath(Path mPath)
	{
		this.mPath = mPath;
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
		//绘制虚线的画笔
		Paint p = PaintUtils.getDashedPaint();
		p.setColor(color);
		RectF bounds = new RectF();
		
		//计算边界
		mPath.computeBounds(bounds, true);
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
		mPath.transform(matrix);
	}
	@Override
	public void fill(int color)
	{
	}
	@Override
	public boolean isFilled()
	{
		return false;
	}
	@Override
	public void scale(float offsetX, float offsetY)
	{
		Matrix matrix = new Matrix();
		RectF bounds = new RectF();
		mPath.computeBounds(bounds, true);
		float x0 = (bounds.right+bounds.left)/2.0f;
		float y0 = (bounds.top+bounds.bottom)/2.0f;
		
		matrix.setScale(offsetX, offsetY,x0,y0);
		
		mPath.transform(matrix);
	}
	
	@Override
	public void rotate(float degrees)
	{
		Matrix matrix = new Matrix();
		RectF bounds = new RectF();
		mPath.computeBounds(bounds, true);
		//中心点坐标
		float x0 = (bounds.right+bounds.left)/2.0f;
		float y0 = (bounds.top+bounds.bottom)/2.0f;
		
		matrix.setRotate(degrees,x0,y0);
		
		mPath.transform(matrix);
	}
	
	@Override
	public Tuyuan copy()
	{
		Line copedTuyuan = new Line(penSize, penColor, alpha, paintStyle.newInstance());
		if(isFilled)
		{
			copedTuyuan.fill(mPenPaint.getColor());
		}
		copedTuyuan.setPath(new Path(mPath));
		copedTuyuan.translate(40,40);
		return copedTuyuan;
	}
}









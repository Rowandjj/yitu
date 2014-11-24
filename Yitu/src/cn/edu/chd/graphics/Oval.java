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
 * @author Rowand jj
 *
 *绘制椭圆/圆
 */
public class Oval implements Tuyuan
{
	private float mSrcX = 0.0f;
	private float mSrcY = 0.0f;
	/**
	 * 画笔
	 */
	private Paint mPenPaint = null;
	/**
	 * 路径
	 */
	private Path mPath = new Path();
	
	private RectF mRect = null;
	
	private boolean isFilled = false;
	
	private boolean mHasDraw = false;
	
	private int penSize;
	private int penColor;
	private PaintStyle paintStyle;
	private int alpha;
	
	public Oval(int penSize, int penColor,int alpha,PaintStyle paintStyle)
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
			drawOval(x, y);
			mHasDraw = true;
		}
	}

	@Override
	public void touchUp(float x, float y)
	{
		drawOval(x, y);
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
		boolean isMoved = dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE;
		return isMoved;
	}
	
	/**
	 * 绘制椭圆
	 */
	private void drawOval(float x,float y)
	{
		mPath.reset();
		if(mRect == null)
		{
			mRect = new RectF();
		}
		mRect.set(mSrcX, mSrcY, x, y);
		mPath.addOval(mRect,Path.Direction.CW);
	}

	@Override
	public boolean contains(float x, float y)
	{
		//得到外切矩形
		RectF bounds = new RectF();
		mPath.computeBounds(bounds, true);
		
		//根据椭圆方程计算
		float a = Math.abs((bounds.left - bounds.right)/2.0f);
		float b = Math.abs((bounds.top - bounds.bottom)/2.0f);
		
		float centerX = (bounds.left + bounds.right)/2.0f;
		float centerY = (bounds.top + bounds.bottom)/2.0f;
		return (x-centerX)*(x-centerX)/(float)(a*a)+(y-centerY)*(y-centerY)/(float)(b*b) <= 1;
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
		
		canvas.drawCircle(bounds.left, bounds.top + (bounds.bottom-bounds.top)/2.0f,ApplicationValues.Base.RADIUS,pTemp);
		canvas.drawCircle(bounds.right, bounds.top + (bounds.bottom-bounds.top)/2.0f,ApplicationValues.Base.RADIUS,pTemp);
		canvas.drawCircle(bounds.left+(bounds.right-bounds.left)/2.0f, bounds.top,ApplicationValues.Base.RADIUS,pTemp);
		canvas.drawCircle(bounds.left+(bounds.right-bounds.left)/2.0f, bounds.bottom,ApplicationValues.Base.RADIUS,pTemp);
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
		mPenPaint.setStyle(Style.FILL);
		mPenPaint.setColor(color);
		isFilled = true;
	}

	@Override
	public boolean isFilled()
	{
		return isFilled;
	}

	@Override
	public void scale(float offsetX, float offsetY)
	{
		Matrix matrix = new Matrix();
		RectF bounds = new RectF();
		mPath.computeBounds(bounds, true);
		matrix.setScale(offsetX, offsetY, (bounds.right+bounds.left)/2.0f,(bounds.top+bounds.bottom)/2.0f);
		mPath.transform(matrix);
	}
	@Override
	public void rotate(float degrees)
	{
		Matrix matrix = new Matrix();
		RectF bounds = new RectF();
		mPath.computeBounds(bounds, true);
		matrix.setRotate(degrees,(bounds.right+bounds.left)/2.0f,(bounds.top+bounds.bottom)/2.0f);
		mPath.transform(matrix);		
	}
	private void setPath(Path mPath)
	{
		this.mPath = mPath;
	}
	@Override
	public Tuyuan copy()
	{
		Oval copedTuyuan = new Oval(penSize, penColor, alpha, paintStyle.newInstance());
		if(isFilled)
		{
			copedTuyuan.fill(mPenPaint.getColor());
		}
		copedTuyuan.setPath(new Path(mPath));
		copedTuyuan.translate(40,40);
		return copedTuyuan;
	}
}




















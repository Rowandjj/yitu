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
 *三次贝塞尔曲线
 */
public class Bezier implements Tuyuan
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
	
	private boolean mHasDraw = false;
	
	/**
	 * 图元是否被填充
	 */
	private boolean isFilled = false;
	
	private int penSize;
	private int penColor;
	private PaintStyle paintStyle;
	private int alpha;
	
	public Bezier(int penSize, int penColor,int alpha,PaintStyle paintStyle)
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
			drawBazier(x, y);
			mHasDraw = true;
		}
	}
	
	@Override
	public void touchUp(float x, float y)
	{
		drawBazier(x, y);
	}
	
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
	
	/**
	 * 绘制三次贝塞尔曲线
	 */
	private void drawBazier(float x,float y)
	{
		mPath.reset();
		mPath.moveTo(mCurrentX, mCurrentY);
		
		float x1,y1,x2,y2;
		
		if(x>mCurrentX && y<mCurrentY)//第一象限
		{
			x1 = mCurrentX;
			y1 = y+(mCurrentY-y)/2.0f;
			x2 = x;
			y2 = y+(mCurrentY-y)/2.0f;		
		}else if(x<mCurrentX && y<mCurrentY)//第二象限
		{
			x1 = mCurrentX;
			y1 = y+(mCurrentY-y)/2.0f;
			x2 = x;
			y2 = y+(mCurrentY-y)/2.0f;
		}else if(x<mCurrentX && y>mCurrentY)//第三象限
		{
			x2 = x+(mCurrentX-x)/2.0f;
			y2 = y;
			x1 = x+(mCurrentX-x)/2.0f;
			y1 = mCurrentY;
		}else//if(x>mCurrentX && y>mCurrentY)//第四象限
		{
			x1 = mCurrentX+(x-mCurrentX)/2.0f;
			y1 = mCurrentY;
			
			x2 = mCurrentX+(x-mCurrentX)/2.0f;
			y2 = y;
		}
		mPath.cubicTo(x1, y1, x2, y2, x, y);
	}
	@Override
	public boolean contains(float x, float y)
	{
		RectF bounds = new RectF();
		mPath.computeBounds(bounds, true);//todo 检查坐标
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
		return isFilled;
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
	private void setPath(Path mPath)
	{
		this.mPath = mPath;
	}
	@Override
	public Tuyuan copy()
	{
		Bezier copedTuyuan = new Bezier(penSize, penColor, alpha, paintStyle.newInstance());
		if(isFilled)
		{
			copedTuyuan.fill(mPenPaint.getColor());
		}
		copedTuyuan.setPath(new Path(mPath));
		copedTuyuan.translate(40,40);
		return copedTuyuan;
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
}















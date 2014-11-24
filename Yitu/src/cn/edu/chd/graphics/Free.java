package cn.edu.chd.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import cn.edu.chd.domain.PaintStyle;
import cn.edu.chd.domain.Tuyuan;
import cn.edu.chd.utils.PaintUtils;
import cn.edu.chd.values.ApplicationValues;

/**
 * @author Rowand jj
 *
 *自由手绘
 */
public class Free implements Tuyuan
{
	private float mCurrentX = 0.0f;
	private float mCurrentY = 0.0f;
	private Path mPath = new Path();
	private Paint mPenPaint = null;
	private boolean mHasDraw = false;
	
	private int penSize;
	private int penColor;
	private PaintStyle paintStyle;
	private int alpha;

	private boolean isFilled = false;
	public Free(int penSize, int penColor,int alpha,PaintStyle paintStyle)
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

	public void setPenSize(int width)
	{
		mPenPaint.setStrokeWidth(width);
	}

	public void setPenColor(int color)
	{
		mPenPaint.setColor(color);
	}

	@Override
	public void draw(Canvas canvas)
	{
		if (canvas != null)
		{
			canvas.drawPath(mPath, mPenPaint);
		}
	}

	@Override
	public void touchDown(float x, float y)
	{
		// 每次down的时候都要将path清空，并且重新设置起点
		mPath.reset();
		// 重新设置起点
		mPath.moveTo(x, y);
		savePoint(x, y);
	}

	@Override
	public void touchMove(float x, float y)
	{
		if (isMoved(x, y))
		{
			drawBeziercurve(x, y);
			savePoint(x, y);
			mHasDraw = true;
		}
	}

	@Override
	public void touchUp(float x, float y)
	{
		mPath.lineTo(x, y);
	}

	@Override
	public boolean hasDraw()
	{
		return mHasDraw;
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

	private void drawBeziercurve(float x, float y)
	{
		mPath.quadTo(mCurrentX, mCurrentY, (x + mCurrentX) / 2,(y + mCurrentY) / 2);
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
		Free copedTuyuan = new Free(penSize, penColor, alpha, paintStyle.newInstance());
		if(isFilled)
		{
			copedTuyuan.fill(mPenPaint.getColor());
		}
		copedTuyuan.setPath(new Path(mPath));
		copedTuyuan.translate(40,40);
		return copedTuyuan;
	}
}

















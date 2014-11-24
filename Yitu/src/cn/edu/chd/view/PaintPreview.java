package cn.edu.chd.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;
import cn.edu.chd.domain.PaintStyle;
import cn.edu.chd.values.ApplicationValues;

/**
 * @author Rowand jj
 *
 *预览用户所选择的图元及画笔样式
 */
public class PaintPreview extends ImageView
{
	private Paint mPaint = null;
	private int mColor = ApplicationValues.PaintSettings.PENCOLOR_DEFAULT;
	private int mAlpha = ApplicationValues.PaintSettings.PEN_ALPHA_DEFAULT;
	private Path mPath = new Path();
	private int paintType = ApplicationValues.PaintStyle.MODE_PLAIN_PEN;
	private int tuyuanType = ApplicationValues.TuyuanStyle.STYLE_FREE;
	private int mSize = ApplicationValues.PaintSettings.PENSIZE_DEFAULT;
	
	private float startX = 0;
	private float startY = 0;
	
	/* 构造器 */
	public PaintPreview(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		drawOperation(canvas);
		
	}
	
	/**
	 * 绘制预览效果
	 */
	private void drawOperation(Canvas canvas)
	{
		if(canvas == null)
			return;
		mPath.reset();
		init();
		canvas.drawPath(mPath, mPaint);
	}
	
	private void init()
	{
		//初始化画笔风格
		mPaint = new PaintStyle(paintType).getPaintStyle();
		//设置颜色
		mPaint.setColor(mColor);
		//设置透明度
		mPaint.setAlpha(255-mAlpha);
		//设置大小
		mPaint.setStrokeWidth(mSize);
		//设置路径
		mPath = initPath(tuyuanType);
	}
	
	private Path initPath(int tuyuanType)
	{
		switch (tuyuanType)
		{
		case ApplicationValues.TuyuanStyle.STYLE_FREE://手绘
			drawFree();
			break;
		case ApplicationValues.TuyuanStyle.STYLE_LINE://直线
			drawLine();
			break;
		case ApplicationValues.TuyuanStyle.STYLE_RECT://矩形
			drawRect();
			break;
		case ApplicationValues.TuyuanStyle.STYLE_OVAL://椭圆
			drawOval();
			break;
		case ApplicationValues.TuyuanStyle.STYLE_BEZIER://三次贝塞尔曲线
			drawBazier();
			break;
		case ApplicationValues.TuyuanStyle.STYLE_BROKEN_LINE://折线
			drawBrokenLine();
			break;
		case ApplicationValues.TuyuanStyle.STYLE_POLYGN://多边形
			drawPolygn();
			break;
		default:
			break;
		}
		return mPath;
	}
	
	private void drawLine()
	{
		startX = getWidth() / 6.0f;
		startY = getHeight() / 2.0f;
		
		mPath.moveTo(startX,startY);
		mPath.lineTo(getWidth()*5/6.0f,startY);
	}
	private void drawFree()
	{
		startX = getWidth() / 6.0f;
		startY = getHeight() / 2.0f;
		
		mPath.moveTo(startX, startY);
		mPath.quadTo(getWidth()/2.0f,getHeight()*3/4.0f,getWidth()*5/6.0f , startY);
	}
	private void drawBazier()
	{
		startX = getWidth() / 6.0f;
		startY = getHeight() / 2.0f;
		mPath.moveTo(startX, startY);
		mPath.cubicTo(getWidth()/3.0f,getHeight()/8.0f,getWidth()*2/3,getHeight()*7/8.0f, getWidth()*5/6.0f,startY);
	}
	private void drawOval()
	{
		startX = getWidth() / 6.0f;
		startY = getHeight() / 4.0f;
		mPath.addOval(new RectF(startX, startY, getWidth()*5/6.0f, getHeight()*3/4.0f),Path.Direction.CW);
	}
	private void drawRect()
	{
		startX = getWidth() / 6.0f;
		startY = getHeight() / 4.0f;
		mPath.addRect(new RectF(startX, startY, getWidth()*5/6.0f, getHeight()*3/4.0f),Path.Direction.CW);
	}
	private void drawBrokenLine()
	{
		startX = getWidth() / 6.0f;
		startY = getHeight() / 2.0f;
		
		float temp1x = getWidth()/3.0f;
		float temp1y = getHeight()/4.0f;
		float temp2x = getWidth()*2/3;
		float temp2y = getHeight()*3/4.0f;
		float temp3x = getWidth()*5/6.0f;
		float temp3y = startY;
		
		mPath.moveTo(startX,startY);
		mPath.lineTo(temp1x, temp1y);
		mPath.moveTo(temp1x, temp1y);
		mPath.lineTo(temp2x, temp2y);
		mPath.moveTo(temp2x, temp2y);
		mPath.lineTo(temp3x, temp3y);
	}
	private void drawPolygn()
	{
		startX = getWidth() / 6.0f;
		startY = getHeight() / 2.0f;
		float temp1x = getWidth()/2.0f;
		float temp1y = getHeight()/4.0f;
		
		float temp2x = getWidth()*5/6.0f;
		float temp2y = startY;
		
		float temp3x = getWidth()*2/3.0f;
		float temp3y = getHeight()*3/4.0f;
		
		float temp4x = getWidth()/3.0f;
		float temp4y = getHeight()*3/4.0f;
		mPath.moveTo(startX,startY);
		mPath.lineTo(temp1x, temp1y);
		mPath.moveTo(temp1x, temp1y);
		mPath.lineTo(temp2x, temp2y);
		mPath.moveTo(temp2x, temp2y);
		mPath.lineTo(temp3x, temp3y);
		mPath.moveTo(temp3x, temp3y);
		mPath.lineTo(temp4x, temp4y);
		mPath.moveTo(temp4x, temp4y);
		mPath.lineTo(startX, startY);
	}
	/**
	 * 设置属性
	 * @param color 颜色
	 * @param alpha 透明度
	 * @param tuyuanType 图元类型
	 * @param paintType 画笔样式
	 */
	public void setAttrs(int color,int alpha,int size,int tuyuanType,int paintType)
	{
		this.mColor = color;
		this.mAlpha = alpha;
		this.mSize = size;
		this.tuyuanType = tuyuanType;
		this.paintType = paintType;
		
		invalidate();
	}
}


















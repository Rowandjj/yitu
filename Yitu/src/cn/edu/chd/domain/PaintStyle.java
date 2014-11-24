package cn.edu.chd.domain;

import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.util.Log;
import cn.edu.chd.values.ApplicationValues;

/**
 *生成画笔的样式
 */
public class PaintStyle
{
	private static final String TAG = "PaintStyle";
	private Paint mPenPaint = null;
	private int type = ApplicationValues.PaintStyle.MODE_PLAIN_PEN;
	
	public PaintStyle(int type)
	{
		this.type = type;
		mPenPaint = new Paint();
	}
	/**
	 * 拷贝当前的画笔样式
	 */
	public PaintStyle newInstance()
	{
		PaintStyle instance = new PaintStyle(type);
		return instance;
	}
	public void setPaintStyle(int type)
	{
		this.type = type;
	}
	public Paint getPaintStyle()
	{
		switch (type)
		{
		case ApplicationValues.PaintStyle.MODE_PLAIN_PEN:
			Log.i(TAG,"MODE_PLAIN_PEN");
			plainPen();//普通
			break;
		case ApplicationValues.PaintStyle.MODE_EMBOSS_PEN:
			Log.i(TAG,"MODE_EMBOSS_PEN");
			embossPen();
			break;
		case ApplicationValues.PaintStyle.MODE_BLUR_PEN:
			Log.i(TAG,"MODE_BLUR_PEN");
			blurPen();
			break;
		case ApplicationValues.PaintStyle.MODE_SHADER_PEN:
			Log.i(TAG,"MODE_SHADER_PEN");
			shaderPen();
			break;

		default:
			throw new RuntimeException("YITU:no such pen style found...");
		}
		
		return mPenPaint;
	}
	private void embossPen()
	{
		MaskFilter mEmboss = new EmbossMaskFilter(new float[]{ 1, 1, 1 }, 0.4f, 6, 3.5f);
		mPenPaint.setDither(true);
		mPenPaint.setAntiAlias(true);
		mPenPaint.setStyle(Paint.Style.STROKE);
		mPenPaint.setStrokeJoin(Paint.Join.ROUND);
		mPenPaint.setStrokeCap(Paint.Cap.ROUND);
		mPenPaint.setMaskFilter(mEmboss);
	}
	private void blurPen()
	{
		MaskFilter mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
		mPenPaint.setDither(true);
		mPenPaint.setAntiAlias(true);
		mPenPaint.setStyle(Paint.Style.STROKE);
		mPenPaint.setStrokeJoin(Paint.Join.ROUND);
		mPenPaint.setStrokeCap(Paint.Cap.ROUND);
		mPenPaint.setMaskFilter(mBlur);
	}
	
	private void plainPen()
	{
		mPenPaint.setDither(true);
		mPenPaint.setAntiAlias(true);
		mPenPaint.setStyle(Paint.Style.STROKE);
		mPenPaint.setStrokeJoin(Paint.Join.ROUND);
		mPenPaint.setStrokeCap(Paint.Cap.ROUND);
	}
	
	private void shaderPen()
	{
		mPenPaint.setDither(true);
		mPenPaint.setAntiAlias(true);
		mPenPaint.setStyle(Paint.Style.STROKE);
		mPenPaint.setStrokeJoin(Paint.Join.ROUND);
		mPenPaint.setStrokeCap(Paint.Cap.ROUND);
		mPenPaint.setShadowLayer(15,2,2,Color.WHITE);
	}
}











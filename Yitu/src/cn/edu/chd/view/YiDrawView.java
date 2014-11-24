package cn.edu.chd.view;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import cn.edu.chd.domain.Tuyuan;
import cn.edu.chd.domain.PaintStyle;
import cn.edu.chd.graphics.Bezier;
import cn.edu.chd.graphics.BrokenLine;
import cn.edu.chd.graphics.BrokenLine.OnTurnListener;
import cn.edu.chd.graphics.Free;
import cn.edu.chd.graphics.Line;
import cn.edu.chd.graphics.Oval;
import cn.edu.chd.graphics.Polygn;
import cn.edu.chd.graphics.Rectangle;
import cn.edu.chd.values.ApplicationValues;

/**
 * @author Rowand jj
 *
 *提供绘图方法的view
 *
 *此类为绘图的核心类
 *
 */
public class YiDrawView extends ImageView implements OnGestureListener
{
	private static final String TAG = "YiDrawView";

	/**
	 * 画布
	 */
	private Canvas mCanvas = null;
	
	/**
	 * 当前图元
	 */
	private Tuyuan mCurrentPaint = null;
	
	/**
	 * 画笔颜色
	 */
	private int mPenColor = ApplicationValues.PaintSettings.PENCOLOR_DEFAULT;;
	/**
	 * 画笔大小
	 */
	private int mPenSize = ApplicationValues.PaintSettings.PENSIZE_DEFAULT;
	
	/**
	 * 画笔透明度
	 */
	private int mPenAlpha = ApplicationValues.PaintSettings.PEN_ALPHA_DEFAULT;
	
	/**
	 * 绘图的背景，由于是资源所以此bitmap是不可变的,如果要操作这个bitmap必须创建其副本，即tempBitmap
	 */
	private Bitmap mBitmap = null;
	
	/**
	 * mBitmap的副本，可以直接操作
	 */
	private Bitmap tempBitmap = null;
	
	/**
	 * 用在ondraw方法中绘制bitmap
	 */
	private Paint mPaint = null;
	
	/**
	 * 画笔模式(直线，贝塞尔...)
	 */
	private int mPaintMode = -1;
	
	/**
	 * 画布宽
	 */
	private int mBitmapWidth = 0;
	/**
	 * 画布高
	 */
	private int mBitmapHeight = 0;
	
	/**
	 * 提供redo,undo等操作
	 */
	private PaintStack mPaintStack = null;
	
	/**
	 *视图是否第一次被创建 
	 */
	private boolean isFirstCreated = true;
	
	private boolean isTouchUp = false;
	
	/**
	 * 画笔样式
	 */
	private int paintStyleValue = -1;
	
	/**
	 * 是否震动
	 * 	绘制折线时，震动代表遇到折点
	 */
	private boolean isShaked = false;
	
	/**
	 * 手势识别器
	 */
	private GestureDetector mGestureDetector = null;
	
	/**
	 * 当前被选中的图元
	 */
	private Tuyuan checkedElement = null;
	
	/**
	 * 是否处于拖动图元状态
	 */
	private boolean shouldDrag = false;
	
	/**
	 * 绘制图元的起点
	 */
	private float srcX,srcY;
	
	/**
	 * 橡皮模式
	 */
	private boolean eraserMode = false;
	
	/**
	 * 填充模式
	 */
	private boolean fillMode = false;
	
	private Tuyuan copedTuyuan = null;
	
	private boolean pasteFlag = false;
	
	/**
	 * 缩放模式
	 */
	private boolean zoomMode = false;
	
	/**
	 * 旋转模式
	 */
	private boolean rotateMode = false;
	
	/* 构造器 */
	public YiDrawView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mCanvas = new Canvas();
		mPaint = new Paint(Paint.DITHER_FLAG);
	
		//设置默认画笔样式
		paintStyleValue = ApplicationValues.PaintStyle.MODE_PLAIN_PEN;
		//默认为自由手绘图元
		setCurrentTuyuanType(ApplicationValues.TuyuanStyle.STYLE_FREE);
		mPaintStack = new PaintStack(this);
		
		mGestureDetector = new GestureDetector(context,this);
	}
	 
	/**
	 * 外部设置画布接口，务必调用
	 */
	public void setImageBitmap(Bitmap image)
	{
		this.mBitmap = image;
		postInvalidate();
	}
	@Override
	public void setLayoutParams(LayoutParams params)
	{
		super.setLayoutParams(params);
		postInvalidate();
	}
	/**
	 * 在dispatchTouchEvent方法中改变touch事件的分发顺序，让其先识别手势操作，
	 * 如果手势操作成功了（使得一个图元处于拖动装态），那么阻止绘图操作，并监听move
	 * 事件对图元进行平移等变换,否则继续执行绘图操作.
	 * */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		mGestureDetector.onTouchEvent(event);//先识别手势
		
		if(eraserMode)//如果是橡皮模式,则拦截绘图操作
		{
			return false;
		}
		if(shouldDrag)//如果处于拖动状态，则禁止绘图操作
		{
			switch (event.getAction())
			{
			case MotionEvent.ACTION_MOVE:
				deleteTuyuan(checkedElement);//删除原有图元
				checkedElement.translate(event.getX()-srcX,event.getY()-srcY);//平移
				srcX = event.getX();
				srcY = event.getY();
				invalidate();
				Log.i(TAG,"=================MOVE");
				break;
			case MotionEvent.ACTION_UP:
				if(shouldDrag)
				{
					shouldDrag = false;//每次up都清除拖动状态
					checkedElement.draw(mCanvas);//重新在新位置上绘制图元
					mPaintStack.push(checkedElement);//加入栈中
					invalidate();
				}
			}
			return false;
		}
		return super.dispatchTouchEvent(event);
	}
	
	/**
	 * 删除被选中图元
	 */
	private void deleteTuyuan(Tuyuan c)
	{
		mPaintStack.deleteCommand(c);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return handleTouchForDraw(event);
	}
	
	/**
	 * 处理绘图过程的touch事件
	 */
	private boolean handleTouchForDraw(MotionEvent event)
	{
		Log.i(TAG, "handleTouchForDraw");
		float x = event.getX();
		float y = event.getY();
		isTouchUp = false;
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			srcX = event.getX();
			srcY = event.getY();
			mCurrentPaint = createNewTuyuan(mPaintMode);
			mCurrentPaint.touchDown(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			mCurrentPaint.touchMove(x, y);
			isShaked = false;
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			mCurrentPaint.touchUp(x, y);
			mCurrentPaint.draw(mCanvas);//绘制到bitmap上
			if(mCurrentPaint.hasDraw())
			{
				mPaintStack.push(mCurrentPaint);//放入栈中
			}
			invalidate();
			isTouchUp = true;
			if(Math.abs(srcX-x)>=2 && Math.abs(srcY-y)>=2)
			{
				checkedElement = mCurrentPaint;//当前正在绘制的图元默认被选中
			}
			break;
		}
		return true;
	}
	
	@Override
	public boolean onDown(MotionEvent e)
	{
		Log.i(TAG,"onDown");
		/**
		 * 手势按下事件
		 * */
  		//获取手指当前按下位置
		float x = e.getX();
		float y = e.getY();
		//检查当前位置是否有图元能够被选中
		Tuyuan c = mPaintStack.check(x, y);
		
		if(c != null)//选中了图元
		{
			checkedElement = c;
			//如果当前是橡皮模式，则删除被选中图元
			if(eraserMode)
			{
				deleteTuyuan(c);
				checkedElement = null;
			}
			//如果当前是填充模式，则填充选中图元
			if(fillMode)
			{
				checkedElement.fill(mPenColor);
			}
		}else
		{
			checkedElement = null;
		}
		invalidate();//重绘
		
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e)
	{
	}


	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		return false;
	}


	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY)
	{
		return false;
	}

	
	
	@Override
	public void onLongPress(MotionEvent e)
	{
		shouldDrag(e);//判断是否需要切换为拖动状态
	}


	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY)
	{
		return false;
	}
	
	/**
	 * 判断是否需要切换为拖动状态
	 */
	private void shouldDrag(MotionEvent e)
	{
		//获取手指当前按下位置
		float x = e.getX();
		float y = e.getY();
		//检查当前位置是否有图元能够被选中
		Tuyuan c = mPaintStack.check(x, y);
		//如果当前点击的图元正好是之前选中的图元那么该图元变为拖动状态
		if(c != null && checkedElement != null && c.equals(checkedElement))
		{
			shouldDrag = true;
		}else
		{
			shouldDrag = false;
		}
		invalidate();
	}
	/**
	 * 视图重绘方法
	 * */
	@Override
	protected void onDraw(Canvas canvas)
	{
		//TODO
		if(tempBitmap != null)
		{
			super.onDraw(canvas);
			canvas.drawBitmap(tempBitmap, 0, 0, mPaint);//绘制之前的路径
			if(!isTouchUp)
			{
				mCurrentPaint.draw(canvas);//在view上绘制当前路径
			}
		}
		
		//选择的图元不为空
		if(checkedElement != null)
		{
			if(shouldDrag)//拖动状态下需要高亮显示
			{	
				checkedElement.setHighLight(canvas);
				checkedElement.draw(mCanvas);
//				checkedElement.draw(canvas);
			}else if(fillMode)//填充模式
			{
				checkedElement.draw(mCanvas);
	//			checkedElement.draw(canvas);
			}else if(zoomMode)//图元缩放模式
			{
				checkedElement.checked(canvas);
				checkedElement.draw(mCanvas);
//				checkedElement.draw(canvas);
				zoomMode = false;
			}else if(rotateMode)//图元旋转模式
			{
				checkedElement.checked(canvas);
				checkedElement.draw(mCanvas);
//				checkedElement.draw(canvas);
				rotateMode = false;
			}
			else//否则只是选中状态
			{
				checkedElement.checked(canvas);
			}
		}
		//剪贴板是否有内容，如果有内容则绘制
		if(pasteFlag && copedTuyuan!=null)
		{
			Log.i(TAG,"=======ONDRAW_PASTE=======");
			copedTuyuan.draw(mCanvas);
			copedTuyuan = null;
			pasteFlag = false;
		}
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		mBitmapWidth = w;
		mBitmapHeight = h;
		if(isFirstCreated)
		{
			createCanvasBitmap(mBitmapWidth, mBitmapHeight);
			isFirstCreated = false;
		}
	}
	/**
	 * @param type 图元类型
	 * @return 图元
	 * 
	 * 创建新的图元模式
	 */
	public Tuyuan createNewTuyuan(int type)
	{
		Tuyuan tool = null;
		switch (type)
		{
		case ApplicationValues.TuyuanStyle.STYLE_FREE://自由手绘
			tool = new Free(mPenSize, mPenColor,mPenAlpha,new PaintStyle(paintStyleValue));
			break;
		case ApplicationValues.TuyuanStyle.STYLE_LINE://直线
			tool = new Line(mPenSize, mPenColor,mPenAlpha,new PaintStyle(paintStyleValue));
			break;
		case ApplicationValues.TuyuanStyle.STYLE_RECT://矩形
			tool = new Rectangle(mPenSize, mPenColor, mPenAlpha,new PaintStyle(paintStyleValue));
			break;
		case ApplicationValues.TuyuanStyle.STYLE_OVAL://椭圆
			tool = new Oval(mPenSize, mPenColor,mPenAlpha, new PaintStyle(paintStyleValue));
			break;
		case ApplicationValues.TuyuanStyle.STYLE_BEZIER://三次贝塞尔曲线
			tool = new Bezier(mPenSize, mPenColor, mPenAlpha, new PaintStyle(paintStyleValue));
			break;
		case ApplicationValues.TuyuanStyle.STYLE_BROKEN_LINE://折线
			tool = new BrokenLine(mPenSize,mPenColor,mPenAlpha, new PaintStyle(paintStyleValue));
			BrokenLine temp = (BrokenLine) tool;
			//注册“遇到转折点”事件，当传感器监测到晃动时代表遇到折点
			temp.setOnTurnListener(new OnTurnListener()
			{
				@Override
				public boolean onTurn()
				{
					return isShaked;
				}
			});
			break;
		case ApplicationValues.TuyuanStyle.STYLE_POLYGN://多边形
			tool = new Polygn(mPenSize, mPenColor, mPenAlpha, new PaintStyle(paintStyleValue));
			((Polygn)tool).setOnTurnListener(new Polygn.OnTurnListener()
			{
				@Override
				public boolean onTurn()
				{
					return isShaked;
				}
			});
			break;
			//TODO 添加其他模式
		}
		mPaintMode = type;
		return tool;
	}
	
	public boolean isShaked()
	{
		return isShaked;
	}

	public void setShaked(boolean isShaked)
	{
		this.isShaked = isShaked;
	}

	/**
	 * 设置是否为橡皮模式
	 */
	public void setEraserMode(boolean eraserMode)
	{
		this.eraserMode = eraserMode;
		if(fillMode)
		{
			fillMode = false;
		}
	}
	public boolean getEraserMode()
	{
		return this.eraserMode;
	}
	
	public boolean isFillMode()
	{
		return fillMode;
	}

	/**
	 * 设置是否为填充模式
	 */
	public void setFillMode(boolean fillMode)
	{
		this.fillMode = fillMode;
		if(eraserMode)
		{
			eraserMode = false;
		}
	}
	
	/**
	 *复制图元 
	 */
	public boolean copyTuyuan()
	{
		if(checkedElement != null)
		{
			Log.i(TAG,"copedTuyuan");
			copedTuyuan = checkedElement.copy();
			return true;
		}else
		{
			return false;
		}
	}
	
	/**
	 * 粘贴图元
	 */
	public boolean pasteTuyuan()
	{
		if(mPaintStack != null && copedTuyuan != null)
		{
			Log.i(TAG,"PASTE Tuyuan......");
			mPaintStack.push(copedTuyuan);
			invalidate();
			pasteFlag = true;
			return true;
		}
		return false;
	}
	
	/**
	 * 图元放大
	 */
	public void enlarge()
	{
		if(checkedElement != null)
		{
			zoomMode = true;
			mPaintStack.deleteCommand(checkedElement);
			checkedElement.scale(1.1f, 1.1f);
			mPaintStack.push(checkedElement);
			invalidate();
		}
	}
	
	/**
	 * 图元缩小
	 */
	public void shrink()
	{
		if(checkedElement != null)
		{
			zoomMode = true;
			mPaintStack.deleteCommand(checkedElement);
			checkedElement.scale(0.9f, 0.9f);
			mPaintStack.push(checkedElement);
			invalidate();
		}
	}
	
	/**
	 * 向左旋转
	 */
	public void rotateLeft()
	{
		if(checkedElement != null)
		{
			rotateMode = true;
			mPaintStack.deleteCommand(checkedElement);
			checkedElement.rotate(30);
			mPaintStack.push(checkedElement);
			invalidate();
		}
	}
	
	/**
	 * 向右旋转
	 */
	public void rotateRight()
	{
		if(checkedElement != null)
		{
			rotateMode = true;
			mPaintStack.deleteCommand(checkedElement);
			checkedElement.rotate(-30);
			mPaintStack.push(checkedElement);
			invalidate();
		}
	}
	/**
	 * 设置画笔样式
	 */
	public void setPaintStyle(int paintType)
	{
		this.paintStyleValue = paintType;
	}
	/**
	 * 设置画笔颜色
	 */
	public void setPenColor(int penColor)
	{
		this.mPenColor = penColor;
	}
	/**
	 * 设置画笔大小
	 */
	public void setPenSize(int penSize)
	{
		this.mPenSize = penSize;
	}
	
	/**
	 * 设置画笔透明度
	 */
	public void setPenAlpha(int penAlpha)
	{
		this.mPenAlpha = penAlpha;
	}
	/**
	 * 设置当前图元样式
	 * @param type
	 */
	public void setCurrentTuyuanType(int type)
	{
		mCurrentPaint = createNewTuyuan(type);
		mPaintMode = type;
	}

	public int getPenAlpha()
	{
		return this.mPenAlpha;
	}
	
	public int getPenSize()
	{
		return this.mPenSize;
	}
	
	public int getPenColor()
	{
		return this.mPenColor;
	}
	
	public int getPaintStyle()
	{
		return this.paintStyleValue;
	}
	
	public int getTuyuanStyle()
	{
		return this.mPaintMode;
	}
	
	public int getCurrentTuyuanMode()
	{
		return mPaintMode;
	}
	/**
	 * 为画布设置bitmap
	 */
	private void createCanvasBitmap(int w, int h)
	{
		tempBitmap = mBitmap.copy(Config.ARGB_8888,true);
		mCanvas.setBitmap(tempBitmap);
	}
	
	/**
	 * undo
	 */
	public void undo()
	{
		checkedElement = null;
		if(mPaintStack != null)
		{
			mPaintStack.undo();
		}
	}
	
	/**
	 * redo
	 */
	public void redo()
	{
		if(mPaintStack != null)
		{
			mPaintStack.redo();
		}
	}
	
	/**
	 * 清空画布
	 */
	public void clearAll()
	{
		checkedElement = null;
		shouldDrag = false;
		
		if(mPaintStack != null)
		{
			mPaintStack.clear();
		}
		
		if(tempBitmap != null && !tempBitmap.isRecycled())
		{
			tempBitmap.recycle();
			tempBitmap = null;
		}
		//重设画布
		createCanvasBitmap(mBitmapWidth, mBitmapHeight);
		
		invalidate();
	}
	
	/**
	 * 是否绘制了数据
	 */
	public boolean hasData()
	{
		return !mPaintStack.undoStack.isEmpty();
	}
	
	/**
	 * 获取用户已经绘制的数据
	 * @return
	 */
	public Bitmap getDrawData()
	{
		return tempBitmap;
	}
	
	//---------------------------------------------------------------------------
	/**
	 * @author Rowand jj
	 *
	 *提供redo undo delete check功能的栈结构
	 */
	public class PaintStack
	{
		private YiDrawView view = null;
		
		/**
		 * undo栈
		 */
		private List<Tuyuan> undoStack = new LinkedList<Tuyuan>();
		
		/**
		 * redo栈
		 */
		private List<Tuyuan> redoStack = new LinkedList<Tuyuan>();
		
		/**
		 * 构造器
		 */
		public PaintStack(YiDrawView view)
		{
			this.view = view;
		}
		
		/**
		 * 重做
		 */
		public void redo()
		{
			Canvas c = view.mCanvas;
			if(redoStack != null && redoStack.size() > 0)
			{
				Tuyuan tool = redoStack.remove(redoStack.size() - 1);
				undoStack.add(tool);
				tool.draw(c);
			}
			view.invalidate();
		}
		
		/**
		 * 撤销
		 */
		public void undo()
		{
			if(view.tempBitmap != null && !view.tempBitmap.isRecycled())
			{
				view.tempBitmap.recycle();
				view.tempBitmap = null;
			}
			view.createCanvasBitmap(view.mBitmapWidth,view.mBitmapHeight);
			Canvas c = view.mCanvas;
			if(undoStack != null && undoStack.size() > 0)
			{
				Tuyuan tool = undoStack.remove(undoStack.size() - 1);
				redoStack.add(tool);
				
				for(Tuyuan t : undoStack)
				{
					t.draw(c);
				}
			}
			view.invalidate();
		}
		
		/**
		 * 检查当前位置是否能选中图元
		 */
		public Tuyuan check(float x,float y)
		{
			for(Tuyuan c : undoStack)
			{
				if(c.contains(x, y))
				{
					return c;
				}
			}
			return null;
		}
		/**
		 * 删除一个图元
		 */
		public void deleteCommand(Tuyuan command)
		{
			if(view.tempBitmap != null && !view.tempBitmap.isRecycled())
			{
				view.tempBitmap.recycle();
				view.tempBitmap = null;
			}
			view.createCanvasBitmap(view.mBitmapWidth,view.mBitmapHeight);
			Canvas c = view.mCanvas;
			if(undoStack != null && undoStack.size() > 0)
			{
				if(undoStack.remove(command))
				{
					redoStack.add(command);
				}
				for(Tuyuan t : undoStack)
				{
					t.draw(c);
				}
			}
			view.invalidate();
		}
		
		/**
		 * 将用户操作"压栈"
		 */
		public void push(Tuyuan tool)
		{
			if(undoStack != null)
			{
				undoStack.add(tool);
			}
		}
		
		/**
		 * 清空栈
		 */
		public void clear()
		{
			if(undoStack != null && redoStack != null)
			{
				undoStack.clear();
				redoStack.clear();
			}
		}
	}
}












































package cn.edu.chd.values;

import android.graphics.Color;

/**
 * @author Rowand jj
 *
 *存储全局变量
 */
public interface ApplicationValues
{
	/**
	 *基本配置信息
	 */
	public interface Base
	{
		/**
		 * sharepref中存储的xml名
		 */
		public static final String BASE_PREF = "base_pref";
		/**
		 * 作品保存的路径
		 */
		public static final String SAVE_PATH = "/yitu";
		/**
		 * 临时文件夹
		 */
		public static final String TEMP_PATH = "/yituTemp";
		
		/**
		 * 当前图片的存储路径
		 */
		public static final String IMAGE_STORE_PATH = "image_store_path";
		/*intent类型*/
		public static final String PREVIEW_TYPE = "preview_type";//预览类型
		public static final String TYPE_CAMERA = "type_camera";
		public static final String TYPE_GALLERY = "type_gallery";
		public static final String TYPE_MY_WORKS = "type_my_works";
		public static final String TYPE_NORMAL_MODEL = "type_normal_model";
		
		public static final int RADIUS = 8;
	}
	/**
	 *设置信息
	 */
	public interface Settings 
	{
		/**
		 * sharepref中存储的xml名
		 */
		public static final String SETTING_PREF = "setting_pref";
		
		/**
		 * 屏幕状态（是否画图时屏幕常亮）
		 */
		public static final String SCREEN_STATE = "screen_state";
		
		/**
		 * 夜间模式
		 */
		public static final String NIGHT_MODEL = "night_model";
		
		/**
		 * "摇一摇"启动app
		 */
		public static final String SHAKE_MODEL = "shake_model";
		
		/**
		 * 画布宽
		 */
		public static final String CANVAS_WIDTH = "canvas_width";
		
		/**
		 * 画布高
		 */
		public static final String CANVAS_HEIGHT = "canvas_height";
	}
	
	/**
	 * @author Rowand jj
	 *画笔样式
	 */
	public interface PaintStyle
	{
		/**
		 * 铅笔
		 */
		public static final int MODE_PLAIN_PEN = 1;
		
		public static final int MODE_EMBOSS_PEN = 2;
		
		public static final int MODE_BLUR_PEN = 3;
		
		/**
		 * 画刷
		 */
		public static final int MODE_BRUSH = 4;
		
		public static final int MODE_SHADER_PEN = 5;
	}
	
	public interface TuyuanStyle
	{
		/**
		 * 自由手绘
		 */
		public static final int STYLE_FREE = 1;
		
		/**
		 * 三次贝塞尔曲线
		 */
		public static final int STYLE_BEZIER = 2;

		/**
		 * 圆/椭圆
		 */
		public static final int STYLE_OVAL = 3;
		
		/**
		 * 矩形
		 */
		public static final int STYLE_RECT = 4;
		
		/**
		 * 直线
		 */
		public static final int STYLE_LINE = 6;
		
		/**
		 * 折线
		 */
		public static final int STYLE_BROKEN_LINE = 7;
		
		/**
		 * 多边形
		 */
		public static final int STYLE_POLYGN = 8;
	}
	
	public interface PaintSettings
	{
		/**
		 * 画笔默认颜色
		 */
		public static final int PENCOLOR_DEFAULT = Color.BLACK;
		
		/**
		 * 画笔默认大小
		 */
		public static final int PENSIZE_DEFAULT = 4;
		
		/**
		 * 橡皮宽度
		 */
		public static final int ERASER_WIDER_DEFAULT = 3;
		
		/**
		 * 默认透明度
		 */
		public static final int PEN_ALPHA_DEFAULT = 0;
	}
}














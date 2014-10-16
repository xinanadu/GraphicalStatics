package info.zhegui.graphicalstatics;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class GraphicalView extends SurfaceView implements SurfaceHolder.Callback {

	class LunarThread extends Thread {

		/** Handle to the surface manager object we interact with */
		private SurfaceHolder mSurfaceHolder;
		/** Message handler used by thread to interact with TextView */
		private Handler mHandler;
		/** Handle to the application context, used to e.g. fetch Drawables. */
		private Context mContext;
		private String mUnit;

		private int SCREEN_WIDTH = 600;
		private int SCREEN_HEIGHT = 400;
		private final int MARGIN_X = 50;
		private final int MARGIN_Y = 50;

		private final int INTERVAL_VERTICAL = 50;
		private final int INTERVAL_HORIZONTAL = 80;
		// 竖直单位字体宽度
		private final int UNIT_VERTICAL_FONT_WIDTH = 20;
		// 水平单位字体宽度
		private final int UNIT_HORIONTAL_FONT_WIDTH = 50;
		// 单位字体高度
		private final int UNIT_FONT_HEIGHT = 20;

		public LunarThread(SurfaceHolder surfaceHolder, Context context,
				Handler handler) {
			log("LunarThread()");
			mSurfaceHolder = surfaceHolder;
			mHandler = handler;
			mContext = context;

			// DisplayMetrics dm = new DisplayMetrics();
			// ((Activity) context).getWindowManager().getDefaultDisplay()
			// .getMetrics(dm);
			// SCREEN_HEIGHT = dm.heightPixels;
			// SCREEN_WIDTH = dm.widthPixels;
		}

		@Override
		public void run() {

			SCREEN_HEIGHT = mSurfaceHolder.getSurfaceFrame().height();
			SCREEN_WIDTH = mSurfaceHolder.getSurfaceFrame().width();

			Canvas c = null;
			try {
				c = mSurfaceHolder.lockCanvas(null);
				synchronized (mSurfaceHolder) {
					doDrawGrid(c);
					doDrawBar(c);
				}
			} finally {
				// do this in a finally so that if an exception is thrown
				// during the above, we don't leave the Surface in an
				// inconsistent state
				if (c != null) {
					mSurfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}

		private void doDrawGrid(Canvas canvas) {
			log("doDrawGrid(" + canvas + ")");
			canvas.drawColor(Color.parseColor("#ff69736D"));

			Paint paintText = new Paint();
			paintText.setLinearText(true);
			paintText.setColor(Color.parseColor("#cccccccc"));
			paintText.setAntiAlias(true);
			paintText.setStrokeWidth(3);
			paintText.setTextSize(16);

			// 水平线
			Paint paintlineHorizontal = new Paint();
			paintlineHorizontal.setColor(Color.parseColor("#66cccccc"));
			for (int count = 0; count < 5; count++) {
				if (count == 0) {
					canvas.drawText(mUnit, SCREEN_WIDTH - MARGIN_X
							- UNIT_HORIONTAL_FONT_WIDTH, SCREEN_HEIGHT
							- MARGIN_Y - UNIT_FONT_HEIGHT, paintText);
				}

				int startX = MARGIN_X + UNIT_VERTICAL_FONT_WIDTH;
				int stopX = SCREEN_WIDTH - MARGIN_X - UNIT_HORIONTAL_FONT_WIDTH;
				int startY = SCREEN_HEIGHT - MARGIN_Y - UNIT_FONT_HEIGHT
						- INTERVAL_VERTICAL * count;
				int stopY = SCREEN_HEIGHT - MARGIN_Y - UNIT_FONT_HEIGHT
						- INTERVAL_VERTICAL * count;
				canvas.drawLine(startX, startY, stopX, stopY,
						paintlineHorizontal);

				canvas.drawText(count + "", MARGIN_X, SCREEN_HEIGHT - MARGIN_Y
						- INTERVAL_VERTICAL * count - UNIT_FONT_HEIGHT,
						paintText);
			}

			// 竖直线
			Paint paintlineVertical = new Paint();
			paintlineVertical.setStyle(Style.STROKE);
			paintlineVertical.setColor(Color.parseColor("#66cccccc"));
			for (int count = 0; count < 5; count++) {
				if (count > 0) {
					paintlineVertical.setPathEffect(new DashPathEffect(
							new float[] { 3, 5 }, 0));
				} else {
					canvas.drawText("（元）", MARGIN_X, MARGIN_Y
							- UNIT_VERTICAL_FONT_WIDTH + UNIT_FONT_HEIGHT,
							paintText);
				}

				int startX = MARGIN_X + UNIT_VERTICAL_FONT_WIDTH
						+ INTERVAL_HORIZONTAL * count;
				int stopX = MARGIN_X + UNIT_VERTICAL_FONT_WIDTH
						+ INTERVAL_HORIZONTAL * count;
				int startY = MARGIN_Y + UNIT_FONT_HEIGHT;
				int stopY = SCREEN_HEIGHT - MARGIN_Y - UNIT_FONT_HEIGHT;
				canvas.drawLine(startX, startY, stopX, stopY, paintlineVertical);
			}
		}

		private void doDrawBar(Canvas canvas) {
			Paint paintBar = new Paint();
			paintBar.setColor(Color.parseColor("#53BA2C"));
			int barWidth = INTERVAL_HORIZONTAL / 2;

			Paint paintText = new Paint();
			paintText.setLinearText(true);
			paintText.setColor(Color.parseColor("#cccccccc"));
			paintText.setAntiAlias(true);
			paintText.setStrokeWidth(3);
			paintText.setTextSize(16);

			for (int count = 0; count < 5 - 1; count++) {
				int startX = MARGIN_X + UNIT_VERTICAL_FONT_WIDTH
						+ INTERVAL_HORIZONTAL / 4 + INTERVAL_HORIZONTAL * count;
				int stopX = MARGIN_X + UNIT_VERTICAL_FONT_WIDTH
						+ INTERVAL_HORIZONTAL / 4 + INTERVAL_HORIZONTAL * count
						+ barWidth;
				int startY = 300;
				int stopY = SCREEN_HEIGHT - MARGIN_Y - UNIT_FONT_HEIGHT;
				canvas.drawRect(startX, startY, stopX, stopY, paintBar);

				canvas.drawText("300", startX, stopY + UNIT_FONT_HEIGHT,
						paintText);
			}
		}

		public void setUnit(String unit) {
			synchronized (mSurfaceHolder) {
				mUnit = unit;
			}
		}

	}

	/** The thread that actually draws the animation */
	private LunarThread thread;

	public GraphicalView(Context context) {
		super(context);
	}

	public GraphicalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// create thread only; it's started in surfaceCreated()
		thread = new LunarThread(holder, context, new Handler() {
			@Override
			public void handleMessage(Message m) {

			}
		});
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub

		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	public void setUnit(String unit) {
		thread.setUnit(unit);
	}

	private void log(String msg) {
		Log.e(this.getClass().getSimpleName(), msg);
	}

}

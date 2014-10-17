package info.zhegui.graphicalstatics;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.os.Bundle;
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
		LinkedList<StatisticsMessage> listData = new LinkedList<StatisticsMessage>();

		private int SCREEN_WIDTH = 600;
		private int SCREEN_HEIGHT = 400;
		private final int MARGIN_X = 50;
		private final int MARGIN_Y = 50;

		private final int HORIZONTAL_LINE_COUNT = 6;

		private int INTERVAL_VERTICAL = 50;
		private int INTERVAL_HORIZONTAL = 80;
		// 竖直单位字体宽度
		private final int UNIT_VERTICAL_FONT_WIDTH = 50;
		// 水平单位字体宽度
		private final int UNIT_HORIONTAL_FONT_WIDTH = 50;
		// 单位字体高度
		private final int UNIT_FONT_HEIGHT = 20;
		// 校正条上面的数字的位置y
		private final int UNIT_FONT_HEIGHT_FIX = 5;
		// 竖直最大刻度
		private int mIntervalVerticalScale;
		// 数值*mRate即为该图条的绘制高度
		private double mRate;

		Paint mPaintText = new Paint();

		public LunarThread(SurfaceHolder surfaceHolder, Context context,
				String unit, LinkedList<StatisticsMessage> listData,
				Handler handler) {
			// log("LunarThread()");
			mSurfaceHolder = surfaceHolder;
			mHandler = handler;
			mContext = context;
			mUnit = unit;
			this.listData = listData;

			mPaintText.setLinearText(true);
			mPaintText.setColor(Color.parseColor("#cccccccc"));
			mPaintText.setAntiAlias(true);
			mPaintText.setStrokeWidth(3);
			mPaintText.setTextSize(16);
		}

		@Override
		public void run() {

			SCREEN_HEIGHT = mSurfaceHolder.getSurfaceFrame().height();
			SCREEN_WIDTH = mSurfaceHolder.getSurfaceFrame().width();

			INTERVAL_HORIZONTAL = (SCREEN_WIDTH - MARGIN_X * 2
					- UNIT_HORIONTAL_FONT_WIDTH - UNIT_VERTICAL_FONT_WIDTH)
					/ listData.size();

			// 获取rate-------------------------------------------
			int maxNumber = 0;
			for (StatisticsMessage msg : listData) {
				if (maxNumber < msg.number) {
					maxNumber = msg.number;
				}
			}

			// 获取竖直刻度间隔
			mIntervalVerticalScale = getIntervaleVerticalScale(maxNumber);
			mRate = (SCREEN_HEIGHT - UNIT_FONT_HEIGHT * 2 - MARGIN_Y * 2)
					* (HORIZONTAL_LINE_COUNT - 1)
					/ HORIZONTAL_LINE_COUNT
					/ (mIntervalVerticalScale * (HORIZONTAL_LINE_COUNT - 1) * 1.00);

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

			// 水平线
			Paint paintlineHorizontal = new Paint();
			paintlineHorizontal.setColor(Color.parseColor("#66cccccc"));
			mPaintText.setTextAlign(Align.RIGHT);
			for (int count = 0; count < HORIZONTAL_LINE_COUNT; count++) {
				if (count == 0) {
					canvas.drawText(mUnit, SCREEN_WIDTH - MARGIN_X,
							SCREEN_HEIGHT - MARGIN_Y - UNIT_FONT_HEIGHT,
							mPaintText);
				}

				int startX = MARGIN_X + UNIT_VERTICAL_FONT_WIDTH;
				int stopX = SCREEN_WIDTH - MARGIN_X - UNIT_HORIONTAL_FONT_WIDTH;
				int startY = SCREEN_HEIGHT - MARGIN_Y - UNIT_FONT_HEIGHT
						- INTERVAL_VERTICAL * count;
				int stopY = SCREEN_HEIGHT - MARGIN_Y - UNIT_FONT_HEIGHT
						- INTERVAL_VERTICAL * count;
				canvas.drawLine(startX, startY, stopX, stopY,
						paintlineHorizontal);

				int scale = mIntervalVerticalScale * count;
				canvas.drawText(scale + "", startX - 3, SCREEN_HEIGHT
						- MARGIN_Y - INTERVAL_VERTICAL * count
						- UNIT_FONT_HEIGHT, mPaintText);
			}

			// 竖直线
			Paint paintlineVertical = new Paint();
			paintlineVertical.setStyle(Style.STROKE);
			paintlineVertical.setColor(Color.parseColor("#66cccccc"));
			mPaintText.setTextAlign(Align.CENTER);
			for (int count = 0; count < listData.size() + 1; count++) {
				if (count > 0) {
					paintlineVertical.setPathEffect(new DashPathEffect(
							new float[] { 3, 5 }, 0));
				} else {
					canvas.drawText("（元）", MARGIN_X + UNIT_VERTICAL_FONT_WIDTH,
							MARGIN_Y + UNIT_FONT_HEIGHT - UNIT_FONT_HEIGHT_FIX,
							mPaintText);
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

			mPaintText.setTextAlign(Align.CENTER);
			for (int count = 0; count < listData.size(); count++) {
				int startX = MARGIN_X + UNIT_VERTICAL_FONT_WIDTH
						+ INTERVAL_HORIZONTAL / 4 + INTERVAL_HORIZONTAL * count;
				int stopX = MARGIN_X + UNIT_VERTICAL_FONT_WIDTH
						+ INTERVAL_HORIZONTAL / 4 + INTERVAL_HORIZONTAL * count
						+ barWidth;
				int stopY = SCREEN_HEIGHT - MARGIN_Y - UNIT_FONT_HEIGHT;
				int startY = stopY - (int) (listData.get(count).number * mRate);
				canvas.drawRect(startX, startY, stopX, stopY, paintBar);

				canvas.drawText(listData.get(count).number + "", startX
						+ barWidth / 2, startY - UNIT_FONT_HEIGHT_FIX,
						mPaintText);

				canvas.drawText(listData.get(count).type,
						startX + barWidth / 2, stopY + UNIT_FONT_HEIGHT,
						mPaintText);
			}
		}

		private int getIntervaleVerticalScale(int maxValue) {
			int num1 = maxValue / (HORIZONTAL_LINE_COUNT - 1);
			int num2 = 0;
			String maxPos = (num1 + "").substring(0, 1);
			String newNumStr = "";
			boolean shouldPlus = false;
			for (int i = 0; i < (num1 + "").length() - 1; i++) {
				newNumStr += "0";
				if (i > 0 && (num1 + "").substring(i, i + 1).compareTo("0") > 0) {
					shouldPlus = true;
				}
			}
			if (shouldPlus) {
				maxPos = (Integer.parseInt(maxPos) + 1) + "";
			}
			num2 = Integer.parseInt(maxPos + newNumStr);

			return num2;
		}

	}

	/** The thread that actually draws the animation */
	private LunarThread thread;

	public GraphicalView(Context context) {
		super(context);
	}

	public GraphicalView(Context context, AttributeSet attrs, String unit,
			LinkedList<StatisticsMessage> listData) {
		super(context, attrs);
		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// create thread only; it's started in surfaceCreated()
		thread = new LunarThread(holder, context, unit, listData,
				new Handler() {
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

	private void log(String msg) {
		Log.e(this.getClass().getSimpleName(), msg);
	}

}

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

		private int SCREEN_WIDTH = 600;
		private int SCREEN_HEIGHT = 400;
		private final int MARGIN_X = 50;
		private final int MARGIN_Y = 50;

		public LunarThread(SurfaceHolder surfaceHolder, Context context,
				Handler handler) {
			mSurfaceHolder = surfaceHolder;
			mHandler = handler;
			mContext = context;

			DisplayMetrics dm = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
			SCREEN_HEIGHT = dm.heightPixels;
			SCREEN_WIDTH = dm.widthPixels;
		}

		@Override
		public void run() {
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
			// log("doDrawGrid("+canvas+")");
			canvas.drawColor(Color.parseColor("#ff69736D"));

			Paint paintlineVertical = new Paint();
			paintlineVertical.setStyle(Style.STROKE);
			paintlineVertical.setPathEffect(new DashPathEffect(new float[] { 3,
					5 }, 0));
			paintlineVertical.setColor(Color.parseColor("#66cccccc"));
			Paint paintlineHorizontal = new Paint();
			paintlineHorizontal.setColor(Color.parseColor("#66cccccc"));
			final int INTERVAL_VERTICAL = 50;
			final int INTERVAL_HORIZONTAL = 80;
			canvas.drawLine(MARGIN_X, MARGIN_Y + INTERVAL_VERTICAL * 0,
					SCREEN_WIDTH - MARGIN_X, MARGIN_Y + INTERVAL_VERTICAL * 0,
					paintlineHorizontal);
			canvas.drawLine(MARGIN_X, MARGIN_Y + INTERVAL_VERTICAL * 1,
					SCREEN_WIDTH - MARGIN_X, MARGIN_Y + INTERVAL_VERTICAL * 1,
					paintlineHorizontal);
			canvas.drawLine(MARGIN_X, MARGIN_Y + INTERVAL_VERTICAL * 2,
					SCREEN_WIDTH - MARGIN_X, MARGIN_Y + INTERVAL_VERTICAL * 2,
					paintlineHorizontal);
			canvas.drawLine(MARGIN_X, MARGIN_Y + INTERVAL_VERTICAL * 3,
					SCREEN_WIDTH - MARGIN_X, MARGIN_Y + INTERVAL_VERTICAL * 3,
					paintlineHorizontal);
			canvas.drawLine(MARGIN_X, MARGIN_Y + INTERVAL_VERTICAL * 4,
					SCREEN_WIDTH - MARGIN_X, MARGIN_Y + INTERVAL_VERTICAL * 4,
					paintlineHorizontal);
			canvas.drawLine(MARGIN_X, MARGIN_Y + INTERVAL_VERTICAL * 5,
					SCREEN_WIDTH - MARGIN_X, MARGIN_Y + INTERVAL_VERTICAL * 5,
					paintlineHorizontal);

			canvas.drawLine(MARGIN_X + INTERVAL_HORIZONTAL * 0, MARGIN_Y,
					MARGIN_X + INTERVAL_HORIZONTAL * 0, SCREEN_HEIGHT
							- MARGIN_Y, paintlineVertical);
			canvas.drawLine(MARGIN_X + INTERVAL_HORIZONTAL * 1, MARGIN_Y,
					MARGIN_X + INTERVAL_HORIZONTAL * 1, SCREEN_HEIGHT
							- MARGIN_Y, paintlineVertical);
			canvas.drawLine(MARGIN_X + INTERVAL_HORIZONTAL * 2, MARGIN_Y,
					MARGIN_X + INTERVAL_HORIZONTAL * 2, SCREEN_HEIGHT
							- MARGIN_Y, paintlineVertical);
			canvas.drawLine(MARGIN_X + INTERVAL_HORIZONTAL * 3, MARGIN_Y,
					MARGIN_X + INTERVAL_HORIZONTAL * 3, SCREEN_HEIGHT
							- MARGIN_Y, paintlineVertical);
			canvas.drawLine(MARGIN_X + INTERVAL_HORIZONTAL * 4, MARGIN_Y,
					MARGIN_X + INTERVAL_HORIZONTAL * 4, SCREEN_HEIGHT
							- MARGIN_Y, paintlineVertical);
			canvas.drawLine(MARGIN_X + INTERVAL_HORIZONTAL * 5, MARGIN_Y,
					MARGIN_X + INTERVAL_HORIZONTAL * 5, SCREEN_HEIGHT
							- MARGIN_Y, paintlineVertical);
		}

		private void doDrawBar(Canvas canvas) {
			Paint paintBar = new Paint();
			paintBar.setColor(Color.parseColor("#53BA2C"));
			canvas.drawRect(MARGIN_X + 10, MARGIN_Y, MARGIN_X + 30,
					MARGIN_Y + 300, paintBar);
		}

	}

	/** The thread that actually draws the animation */
	private LunarThread thread;
	
	public GraphicalView(Context context){
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

	private void log(String msg) {
		Log.e(this.getClass().getSimpleName(), msg);
	}
}

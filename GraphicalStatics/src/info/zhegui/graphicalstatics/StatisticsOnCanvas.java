package info.zhegui.graphicalstatics;

import android.app.Activity;
import android.os.Bundle;
import android.util.AttributeSet;

public class StatisticsOnCanvas extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		GraphicalView view = new GraphicalView(this, null);
		view.setUnit("£®∑÷÷”£©");
		setContentView(view);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}

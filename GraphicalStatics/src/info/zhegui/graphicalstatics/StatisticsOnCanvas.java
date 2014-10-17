package info.zhegui.graphicalstatics;

import java.util.LinkedList;

import android.app.Activity;
import android.os.Bundle;

public class StatisticsOnCanvas extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		LinkedList<StatisticsMessage> listData = new LinkedList<StatisticsMessage>();
		listData.add(new StatisticsMessage("1", 0));
		listData.add(new StatisticsMessage("2", 50));
		listData.add(new StatisticsMessage("3", 1000));
		listData.add(new StatisticsMessage("4", 2000));
		listData.add(new StatisticsMessage("5", 25000));
		listData.add(new StatisticsMessage("6", 3000));
		listData.add(new StatisticsMessage("7", 10000));
		listData.add(new StatisticsMessage("8", 300));
		listData.add(new StatisticsMessage("9", 20));
		listData.add(new StatisticsMessage("10", 3000));
		listData.add(new StatisticsMessage("11", 9000));
		listData.add(new StatisticsMessage("12", 0));

		GraphicalView view = new GraphicalView(this, null, "£¨ÔÂ£©", listData);
		setContentView(view);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}

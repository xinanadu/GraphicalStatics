package info.zhegui.graphicalstatics;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ListView mListView;

	private ArrayList<Message> listMsg = new ArrayList<Message>();

	private MyAdapter adapter;

	private DisplayMetrics mDisplayMetrics;

	private static final int WHAT_ADAPTER = 101;

	private double rate;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_ADAPTER:

				adapter = new MyAdapter();
				mListView.setAdapter(adapter);

				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mListView = (ListView) findViewById(R.id.listView1);

		listMsg.add(new Message("1月份",10, ""));
		listMsg.add(new Message("2月份",20, ""));
		listMsg.add(new Message("3月份",300, ""));
		listMsg.add(new Message("4月份",40, ""));
		listMsg.add(new Message("5月份",500, ""));
		listMsg.add(new Message("6月份",60, ""));
		listMsg.add(new Message("7月份",70000, ""));
		listMsg.add(new Message("8月份",9000000, ""));

		mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);

		calc();
	}

	private void calc() {
		new Thread() {
			public void run() {

				int maxNumber = 0;
				for (Message msg : listMsg) {
					if (maxNumber < msg.number) {
						maxNumber = msg.number;
					}
				}

				double newMaxLength = mDisplayMetrics.heightPixels * 0.8;
				rate = newMaxLength / maxNumber;
				android.os.Message msg = mHandler.obtainMessage(WHAT_ADAPTER);
				msg.sendToTarget();
			};
		}.start();

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listMsg.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return listMsg.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(MainActivity.this).inflate(
						R.layout.item, parent, false);

				holder = new Holder();
				holder.tvType= (TextView) convertView
						.findViewById(R.id.tv_type);
				holder.tvNumber = (TextView) convertView
						.findViewById(R.id.tv_number);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_name);

				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			Message msg = listMsg.get(position);
			holder.tvType.setText(msg.type);
			holder.tvName.setText("￥" + msg.number);

			int newWidth = (int) (msg.number * rate);
			LayoutParams params = new LayoutParams(newWidth < 2 ? 2 : newWidth, 50);
			holder.tvNumber.setLayoutParams(params);

			return convertView;
		}
	}

	static class Holder {
		public TextView tvType;
		public TextView tvNumber;
		public TextView tvName;
	}

	class Message {
		public String type;
		public int number;
		public String name;

		public Message(String type, int number, String name) {
			this.type=type;
			this.number = number;
			this.name = name;
		}
	}
}

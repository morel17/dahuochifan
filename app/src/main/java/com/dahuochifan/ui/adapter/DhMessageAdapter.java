package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.BuildConfig;
import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.model.message.MessageDetail;
import com.dahuochifan.utils.MainTools;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.utils.L;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DhMessageAdapter extends RootAdapter<MessageDetail> {
	private SweetAlertDialog pd;
	private RequestParams params;
	private Context context;

	public DhMessageAdapter(Context context) {
		super(context);
		this.context = context;
		params = new RequestParams();
	}
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MyConstant.MYHANDLER_CODE6 :
					pd.dismiss();
					break;
				case MyConstant.MYHANDLER_CODE4 :
					pd.dismiss();
					break;
				default :
					break;
			}
		};
	};

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.item_message2, parent, false);
		ViewHolder vh = new ViewHolder(view);
		vh.dateTv = (TextView) view.findViewById(R.id.date_tv);
		vh.time_Tv = (TextView) view.findViewById(R.id.time_tv);
		vh.contentTv = (TextView) view.findViewById(R.id.content_tvme);
		// vh.swipe=(SwipeLayout)view.findViewById(R.id.swipe);
		// vh.delete=(Button)view.findViewById(R.id.delete);
		vh.card_rl = (RelativeLayout) view.findViewById(R.id.card_rl);
		view.setTag(vh);
		return view;
	}

	@Override
	protected void bindView(View view, final int position, final MessageDetail object) {
		final ViewHolder vh = (ViewHolder) view.getTag();
		// you have to generate label ID manual
//		LabelView label = vh.getView(12345);
//		if (label == null) {
//			label = new LabelView(context);
//			label.setId(12345);
//			label.setBackgroundResource(R.drawable.bar);
//			label.setTargetViewInBaseAdapter(vh.card_rl, 138, 25, Gravity.LEFT_TOP);
////			label.setTargetView(vh.card_rl, 25,  Gravity.LEFT_TOP);
//		}
		vh.time_Tv.setText(MainTools.dayForTime(object.getCreatetime()));
		if(BuildConfig.LEO_DEBUG)
			L.e(MainTools.currentdaydistance(object.getCreatetime()) + "");
		if (MainTools.currentdaydistance(object.getCreatetime()) == 0) {
			vh.dateTv.setText("今天");
		} else if (MainTools.currentdaydistance(object.getCreatetime()) == 1) {
			vh.dateTv.setText("昨天");
		} else {
			vh.dateTv.setText(MainTools.dayForDay(object.getCreatetime()));
		}
		// vh.swipe.addSwipeListener(new SimpleSwipeListener() {
		// @Override
		// public void onOpen(SwipeLayout layout) {
		// YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
		// }
		// });

		vh.contentTv.setText(Html.fromHtml(object.getContent()));
	}
	class ViewHolder {
		public TextView dateTv;
		public TextView time_Tv;
		public TextView contentTv;
		// private SwipeLayout swipe;
		// private Button delete;
		private RelativeLayout card_rl;
		private SparseArray<View> views = new SparseArray<View>();
		private View convertView;

		public ViewHolder(View convertView) {
			this.convertView = convertView;
		}
		@SuppressWarnings("unchecked")
		public <T extends View> T getView(int resId) {
			View v = views.get(resId);
			if (null == v) {
				v = convertView.findViewById(resId);
				views.put(resId, v);
			}
			return (T) v;
		}
	}
}

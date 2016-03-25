package com.dahuochifan.plugin.main;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.List;

public class PluginActivity extends ActivityGroup {
	private Button btnFindPlugins;
	private CheckBox chbAttachMain;
	
	private LocalActivityManager m_ActivityManager;
	
	private List<PluginBean> plugins;
	private ListView lv_plugins;


	private PluginsAdapter adapter=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_main);
        lv_plugins=(ListView)findViewById(R.id.lv_plugins);
        m_ActivityManager = getLocalActivityManager();
		adapter=new PluginsAdapter(this);
		lv_plugins.setAdapter(adapter);
		plugins=findPlugins();
		handler.sendEmptyMessage(0);
	}

	public static void getInstanceActivity(Context context){
		context.startActivity(new Intent(context, PluginActivity.class));
	}


	public class PluginsAdapter extends RootAdapter<PluginBean>{

		public Context mContext;
		/**
		 * Instantiates a new root adapter.
		 *
		 * @param context the context
		 */
		public PluginsAdapter(Context context) {
			super(context);
			mContext=context;
		}

		@Override
		protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
			View v=inflater.inflate(R.layout.plugin_item,parent,false);
			ViewHolder holder=new ViewHolder();
			holder.tv_item=(TextView)v.findViewById(R.id.tv_item);
			v.setTag(holder);
			return v;
		}

		@Override
		protected void bindView(View view, int position, final PluginBean object) {
			ViewHolder holder=(ViewHolder)view.getTag();
			holder.tv_item.setText(object.getLabel());

			holder.tv_item.setOnClickListener(new OnClickListener() {


				@Override
				public void onClick(View v) {
					try {
//						Toast.makeText(PluginActivity.this,"=="+ object.getPakageName(),Toast.LENGTH_LONG).show();
						if(object.getPakageName().equals("com.dahuochifan")){
							finish();
						} else {
						Intent it = new Intent();
						it.setAction(object.getPakageName());
						it.putExtra("userids", MyConstant.user.getUserids());
						it.putExtra("token", MyConstant.user.getToken());
						it.putExtra("baseurl",MyConstant.URL_MAIN);
						//这里，不会把插件的窗体附加到主程序中，纯粹无用的演示
						startActivity(it);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}

		public class ViewHolder{
			public TextView tv_item;
		}
	}



	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			adapter.clear();
			adapter.addAll(plugins);
			adapter.notifyDataSetChanged();
		}
	};
	
	/**
	 * 查找插件
	 * @return
	 */
	private List<PluginBean> findPlugins(){
		
		List<PluginBean> plugins=new ArrayList<PluginBean>();
		
		
		//遍历包名，来获取插件
		PackageManager pm=getPackageManager();
		
		
		List<PackageInfo> pkgs=pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for(PackageInfo pkg	:pkgs){
			//包名
			String packageName=pkg.packageName;
			String sharedUserId= pkg.sharedUserId;
			
			//sharedUserId是开发时约定好的，这样判断是否为自己人
			if(!"com.dahuochifan.plugin".equals(sharedUserId)||"com.dahuochifan.plugin.main".equals(packageName))
				continue;
			
			//进程名
			String prcessName=pkg.applicationInfo.processName;
			
			//label，也就是appName了
			String label=pm.getApplicationLabel(pkg.applicationInfo).toString();
			
			PluginBean plug=new PluginBean();
			plug.setLabel(label);
			plug.setPakageName(packageName);
			
			plugins.add(plug);
		}
//		Toast.makeText(PluginActivity.this,"=="+ plugins.get(0).getPakageName(),Toast.LENGTH_LONG).show();
		
		return plugins;
        
	}


}
package com.dahuochifan.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dahuochifan.R;
import com.dahuochifan.api.MyConstant;
import com.dahuochifan.model.CookBookLevel;
import com.dahuochifan.model.cookbookself.ChefCBAll;
import com.dahuochifan.param.ParamData;
import com.dahuochifan.ui.activity.CaipuEditActivity;
import com.dahuochifan.utils.MainTools;
import com.dahuochifan.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.zcw.togglebutton.ToggleButton;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import cz.msebera.android.httpclient.Header;

/**
 *
 */
public class MyCookBookAdapter extends RootAdapter<ChefCBAll> {
    private Context context;
    private RequestParams params;
    private Gson gson;
    private boolean cantoogle;
    private SharedPreferences spf;

    public MyCookBookAdapter(Context context) {
        super(context);
        this.context = context;
        params = new RequestParams();
        gson = new Gson();
        cantoogle = true;
        spf = SharedPreferenceUtil.initSharedPerence().init(context, MyConstant.APP_SPF_NAME);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_my_cookbook, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.mytogglebtn = (ToggleButton) v.findViewById(R.id.mytogglebtn);
        viewHolder.edittv = (TextView) v.findViewById(R.id.edittv);
        viewHolder.level_tv = (TextView) v.findViewById(R.id.level_tv);
        viewHolder.comments_tv = (TextView) v.findViewById(R.id.comments_tv);
        viewHolder.myratingbar = (RatingBar) v.findViewById(R.id.myratingbar);
        viewHolder.tags_tv[0] = (TextView) v.findViewById(R.id.tag_one_tv);
        viewHolder.tags_tv[1] = (TextView) v.findViewById(R.id.tag_two_tv);
        viewHolder.tags_tv[2] = (TextView) v.findViewById(R.id.tag_three_tv);
        viewHolder.tags_tv[3] = (TextView) v.findViewById(R.id.tag_four_tv);
        viewHolder.tags_tv[4] = (TextView) v.findViewById(R.id.tag_five_tv);

        viewHolder.price_tv = (TextView) v.findViewById(R.id.price_tv);
        viewHolder.person_tv = (TextView) v.findViewById(R.id.person_tv);
        viewHolder.swip_main_rl = (RelativeLayout) v.findViewById(R.id.swip_main_rl);
        v.setTag(viewHolder);
        return v;
    }

    @Override
    protected void bindView(View view, int position, final ChefCBAll object) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.person_tv.setText(object.getMap().getCookbook().get(position).getMaxnum() + "");
        viewHolder.level_tv.setText(object.getMap().getCookbook().get(position).getName());
        viewHolder.comments_tv.setText(object.getMap().getCookbook().get(position).getCommentnum() + "评论");
        viewHolder.price_tv.setText(object.getMap().getCookbook().get(position).getPrice()+"");
        setToggle(viewHolder.mytogglebtn, object.getMap().getChef().isopen());//主厨的isopen代表店铺是否被关闭，菜谱的isopen代表菜谱的关闭与否
        if (object.getMap().getCookbook().get(position).getCommentnum() == 0) {
            viewHolder.myratingbar.setRating(0 * 0.5f);
        } else {
            viewHolder.myratingbar.setRating(object.getMap().getCookbook().get(position).getTotalscore() / object.getMap().getCookbook().get(position).getCommentnum() * 0.5f);
        }
        String tags_str = object.getMap().getCookbook().get(position).getTags();
        if (!TextUtils.isEmpty(tags_str)) {
            viewHolder.tags_tv[0].setVisibility(View.GONE);
            viewHolder.tags_tv[1].setVisibility(View.GONE);
            viewHolder.tags_tv[2].setVisibility(View.GONE);
            viewHolder.tags_tv[3].setVisibility(View.GONE);
            viewHolder.tags_tv[4].setVisibility(View.GONE);
            String[] tag = tags_str.split(",");
            for (int i = 0; i < tag.length; i++) {
                viewHolder.tags_tv[i].setText(tag[i]);
                viewHolder.tags_tv[i].setVisibility(View.VISIBLE);
            }
        }
        viewHolder.mytogglebtn.setOnToggleChanged(new OnToggleChanged() {

            @Override
            public void onToggle(boolean on) {
                if (cantoogle) {
                    postToggle2(on, object, viewHolder.mytogglebtn);
                }
            }
        });

        viewHolder.edittv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CaipuEditActivity.class);
                intent.putExtra("obj", (Serializable) object);
                context.startActivity(intent);
            }
        });
        viewHolder.swip_main_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, CommentsChefDetailActivity.class);
//                intent.putExtra("obj", (Serializable) object);
//                context.startActivity(intent);
            }
        });
    }

    private void setToggle(final ToggleButton mytogglebtn, boolean flag) {
        if (flag) {
            mytogglebtn.setToggleOn();
        } else {
            mytogglebtn.setToggleOff();
        }
    }

    /**
     * @param on          打开关闭状态
     * @param object      菜谱对象
     * @param mytogglebtn button
     *                    关闭菜单
     */
    private void postToggle(final boolean on, CookBookLevel object, final ToggleButton mytogglebtn) {
        params = ParamData.getInstance().toggleObj(object.getCbids(), on);
        client.post(MyConstant.URL_COOKBOOK_OPEN, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                cantoogle = false;
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                try {
                    JSONObject jobj = new JSONObject(arg2);
                    String tag = jobj.getString("tag");
                    int resultcode = jobj.getInt("resultcode");
                    if (resultcode == 1) {
                        setToggle(mytogglebtn, on);
                    } else {
                        setToggle(mytogglebtn, !on);
                    }
                    MainTools.ShowToast(context, tag);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                MainTools.ShowToast(context, R.string.interneterror);
                setToggle(mytogglebtn, !on);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                cantoogle = true;
            }
        });
    }

    /**
     * @param on          打开关闭状态
     * @param object      Chef对象
     * @param mytogglebtn button
     */
    private void postToggle2(final boolean on, ChefCBAll object, final ToggleButton mytogglebtn) {
        params = ParamData.getInstance().toggleObj2(object.getMap().getPic().get(0).getChefids(), on);
        client.post(MyConstant.URL_CHEF_OPEN, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                cantoogle = false;
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                try {
                    JSONObject jobj = new JSONObject(arg2);
                    String tag = jobj.getString("tag");
                    int resultcode = jobj.getInt("resultcode");
                    if (resultcode == 1) {
                        setToggle(mytogglebtn, on);
                    } else {
                        setToggle(mytogglebtn, !on);
                    }
                    MainTools.ShowToast(context, tag);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                MainTools.ShowToast(context, R.string.interneterror);
                setToggle(mytogglebtn, !on);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                cantoogle = true;
            }
        });
    }

    public class ViewHolder {
        private ToggleButton mytogglebtn;
        private TextView edittv;
        private TextView level_tv;
        private TextView comments_tv;
        private RatingBar myratingbar;
        private TextView[] tags_tv = new TextView[5];
        private TextView price_tv;
        private TextView person_tv;
        private RelativeLayout swip_main_rl;

    }

}

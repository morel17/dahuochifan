package com.dahuochifan.dao;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.dahuochifan.R;
import com.dahuochifan.dao.DaoMaster.DevOpenHelper;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DaoTestActivity extends ListActivity {
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private GwcDao gwcDao;
	private NoteDao noteDao;
	private Cursor cursor;
	private Calendar ca;
	private String date;

	@Bind(R.id.add)
	Button add;
	@Bind(R.id.delete)
	Button delete;
	@Bind(R.id.update)
	Button update;
	@Bind(R.id.check)
	Button check;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dao);
		ButterKnife.bind(this);
		init();
	}

	private void init() {
		DevOpenHelper helper = new DevOpenHelper(this, "dhcf-db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = (DaoSession) daoMaster.newSession();
		gwcDao = daoSession.getGwcDao();
		noteDao = daoSession.getNoteDao();
		
		String textColumn = GwcDao.Properties.Cbids.columnName;
		String orderBy = textColumn + " COLLATE LOCALIZED ASC";
		cursor = db.query(gwcDao.getTablename(), gwcDao.getAllColumns(), null, null, null, null, orderBy);
		String[] from = {textColumn, GwcDao.Properties.Mid.columnName};
		int[] to = {android.R.id.text1, android.R.id.text2};
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to);
		setListAdapter(adapter);
		ca = Calendar.getInstance();
		date = ca.YEAR + "-" + ca.MONTH + "-" + ca.DATE;
		
	/*	List<Gwc>tempList=gwcDao.queryRaw("where mydate=? and mid =? order by cbids", new String[]{date,MyConstant.user.getUserids()});
		for(int i=0;i<tempList.size();i++){
			Log.e("hehe", tempList.get(i).getCbids()+"");
		}
		 */
	/*	String textColumn = NoteDao.Properties.Text.columnName;
		String orderBy = textColumn + " COLLATE LOCALIZED ASC";
		cursor = db.query(noteDao.getTablename(), noteDao.getAllColumns(), null, null, null, null, orderBy);
		String[] from = {textColumn, NoteDao.Properties.Comment.columnName};
		int[] to = {android.R.id.text1, android.R.id.text2};

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to);
		setListAdapter(adapter);*/
	}
	private static int index = 0;
	private void addUiListeners() {
		// TODO Auto-generated method stub

	}
	@OnClick(R.id.add)
	public void add_function() {
		/*
		 * Gwc gwc = new Gwc(null, "1", index + "", date, "status", "address", "distance", "nickname", "name", "avatar", 20.19, 111, 0); gwcDao.insert(gwc);
		 * index++; cursor.requery();
		 */
		final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
		String comment = "Added on " + df.format(new Date());
		/*Note note = new Note(null, index + "", comment, new Date());
		noteDao.insert(note);
		Log.d("DaoExample", "Inserted new note, ID: " + note.getId());
		index++;
		cursor.requery();*/
		//Gwc gwc=new Gwc(null,"1",index+"");
		Gwc gwc = new Gwc(null, "1", index + "", date, "status", "address", "distance", "nickname", "name", "avatar", 20.19, 111, 0,"index"+index); 
		gwcDao.insert(gwc);
		index++;
		cursor.requery();
	}
	@OnClick(R.id.delete)
	public void delete_function() {
		List<Gwc> list=gwcDao.queryRaw("where mid=?", "2");
		for(int i=0;i<list.size();i++){
			Gwc gwc=list.get(i);
			gwcDao.delete(gwc);
		}
		cursor.requery();
	}
	@OnClick(R.id.update)
	public void update_function() {
		Gwc gwc = new Gwc(null, "1", index + "", date, "status", "address", "distance", "nickname", "name", "avatar", 20.19, 111, 0,"index"+1); 
		gwcDao.insert(gwc);
		index++;
		cursor.requery();
	}
	@OnClick(R.id.check)
	public void check_function() {
		List<Gwc>tempList=gwcDao.queryRaw("where cbids =? order by chefid", new String[]{"1"});
		if(tempList!=null&&tempList.size()>0){
//			Toast.makeText(DaoTestActivity.this, tempList.size()+"", 1000).show();
			for(int i=0;i<tempList.size();i++){
			}
		}
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		/*
		 * gwcDao.deleteByKey(id); Toast.makeText(DaoTestActivity.this, id+"", 1000).show(); Log.d("DaoExample", "Deleted note, ID: " + id); cursor.requery();
		 */
		//noteDao.deleteByKey(id);
		gwcDao.deleteByKey(id);
		Log.d("DaoExample", "Deleted note, ID: " + id);
		cursor.requery();
	}
}

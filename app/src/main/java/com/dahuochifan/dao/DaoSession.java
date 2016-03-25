package com.dahuochifan.dao;

import java.util.Map;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;
public class DaoSession extends AbstractDaoSession {
	private final DaoConfig gwcDaoConfig;
	private final DaoConfig noteDaoConfig;
	private final DaoConfig chefDaoConfig;

	private final GwcDao gwcDao;
	private final NoteDao noteDao;
	private final ChefListDao chefDao;

	public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap) {
		super(db);

		gwcDaoConfig = daoConfigMap.get(GwcDao.class).clone();
		gwcDaoConfig.initIdentityScope(type);

		noteDaoConfig = daoConfigMap.get(NoteDao.class).clone();
		noteDaoConfig.initIdentityScope(type);
		
		chefDaoConfig=daoConfigMap.get(ChefListDao.class).clone();
		chefDaoConfig.initIdentityScope(type);

		gwcDao = new GwcDao(gwcDaoConfig, this);
		noteDao = new NoteDao(noteDaoConfig, this);
		chefDao=new ChefListDao(chefDaoConfig,this);

		registerDao(Gwc.class, gwcDao);
		registerDao(Note.class, noteDao);
		registerDao(ChefListStr.class,chefDao);
	}
	public void clear() {
		gwcDaoConfig.getIdentityScope().clear();
		noteDaoConfig.getIdentityScope().clear();
		chefDaoConfig.getIdentityScope().clear();
	}

	public GwcDao getGwcDao() {
		return gwcDao;
	}
	public NoteDao getNoteDao() {
		return noteDao;
	}
	public ChefListDao getChefListDao(){
		return chefDao;
	}

}

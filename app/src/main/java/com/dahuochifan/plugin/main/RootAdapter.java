package com.dahuochifan.plugin.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

/**
 * The Root adapter.
 *
 * @param <T>
 *            the generic type
 */
public abstract class RootAdapter<T> extends BaseAdapter implements Filterable {
	/** The objects. */
	private List<T> mObjects;
	private List<T> mUnfilteredObjects;

	/** The context. */
	protected Context mContext;

	private Filter mFilter = new Filter() {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			if (TextUtils.isEmpty(constraint)) {
				results.values = mUnfilteredObjects;
				results.count = mUnfilteredObjects.size();
			} else {
				final ArrayList<T> filtered = new ArrayList<T>();
				for (T object : mUnfilteredObjects) {
					if (isFiltered(constraint, object)) {
						filtered.add(object);
					}
				}
				results.values = filtered;
				results.count = filtered.size();
			}
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			mObjects = (List<T>) results.values;
			notifyDataSetChanged();
		}
	};

	/**
	 * Instantiates a new root adapter.
	 *
	 * @param context
	 *            the context
	 */
	public RootAdapter(Context context) {
		mObjects = new ArrayList<T>();
		mUnfilteredObjects = new ArrayList<T>();
		mContext = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return mObjects.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public T getItem(int position) {
		return mObjects.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = createView(LayoutInflater.from(mContext), parent, getItemViewType(position));
		}
		bindView(convertView,position, getItem(position));

		return convertView;
	}

	/**
	 * Adds the object.
	 *
	 * @param object
	 *            the object
	 */
	public void add(T object) {
		mUnfilteredObjects.add(object);
		mObjects.add(object);
	}

	public void addAll(Collection<T> objects) {
		if (objects != null) {
			mUnfilteredObjects.addAll(objects);
			mObjects.addAll(objects);
		}
	}

	public void remove(T object) {
		mUnfilteredObjects.remove(object);
		mObjects.remove(object);
	}

	public void clear() {
		mUnfilteredObjects.clear();
		mObjects.clear();
	}

	protected boolean isFiltered(CharSequence filter, T object) {
		return true;
	}

	@Override
	public Filter getFilter() {
		return mFilter;
	}
	

	public List<T> getmObjects() {
		return mObjects;
	}

	public void setmObjects(List<T> mObjects) {
		this.mObjects = mObjects;
	}

	public List<T> getmUnfilteredObjects() {
		return mUnfilteredObjects;
	}

	public void setmUnfilteredObjects(List<T> mUnfilteredObjects) {
		this.mUnfilteredObjects = mUnfilteredObjects;
	}

	protected abstract View createView(LayoutInflater inflater, ViewGroup parent, int viewType);

	protected abstract void bindView(View view,int position, T object);
}
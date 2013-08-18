package cz.fit.lentaruand.data.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import cz.fit.lentaruand.data.DatabaseObject;
import cz.fit.lentaruand.data.db.SQLiteType;
import cz.fit.lentaruand.utils.LentaConstants;

abstract class AbstractDao<T extends DatabaseObject> implements Dao<T> {
	private final static String textKeyWhere;
	private final static String intKeyWhere;
	
	private static String getWhereFromSQLiteType(SQLiteType type) {
		switch (type) {
		case TEXT:
			return textKeyWhere;
		case INTEGER:
			return intKeyWhere;
		default:
			throw new IllegalArgumentException("Only TEXT and INTEGER are supported as key types in database.");
		}
	}
	
	static {
		textKeyWhere = "%1s LIKE ?";
		intKeyWhere = "%1s = ?";
	}
	
	private final ContentResolver cr;
	
	protected AbstractDao(ContentResolver cr) {
		if (cr == null) {
			throw new IllegalArgumentException("contentResolver is null.");
		}
		
		this.cr = cr;
	}

	@Override
	public synchronized Collection<T> read() {
		Cursor cur = cr.query(getContentProviderUri(),
				getProjectionAll(), null, null, null);

		try {
			List<T> result = new ArrayList<T>();

			if (cur.moveToFirst()) {
				do {
					result.add(createDataObject(cur));
				} while (cur.moveToNext());
			}
			
			return result;
		} finally {
			cur.close();
		}		
	}

	@Override
	public synchronized T read(long id) {
		Uri uri = ContentUris.withAppendedId(getContentProviderUri(), id);
		
		Cursor cur = cr.query(
				uri,
				getProjectionAll(),
				null,
				null, 
				null
				);
		
		try {
			if (cur.getCount() > 1) {
				Log.w(LentaConstants.LoggerMainAppTag, "There are more than one data object by using uri '" + uri + "'. Will use the first one from the list.");
			}
			
			if (cur.moveToFirst())
				return createDataObject(cur);
		} finally {
			cur.close();
		}
		
		return null;
	}

	@Override
	public synchronized Collection<T> read(Collection<Long> ids) {
		Collection<T> result = new ArrayList<T>(ids.size());
		
		for (long id : ids) {
			T dataObject = read(id);
			
			if (dataObject != null) {
				result.add(dataObject);
			}
		}
		
		return result;
	}

	@Override
	public synchronized T read(String key) {
		return read(getKeyColumnType(), getKeyColumnName(), key);
	}
	
	@Override
	public synchronized T read(SQLiteType keyType, String keyColumnName, String keyValue) {
		String[] whereArgs = { keyValue };
		String where = String.format(getWhereFromSQLiteType(keyType), keyColumnName);

		Cursor cur = cr.query(
				getContentProviderUri(),
				getProjectionAll(),
				where,
				whereArgs, 
				null
				);
		
		try {
			if (cur.getCount() > 1) {
				Log.w(LentaConstants.LoggerMainAppTag,
						"There are more than one data object by using uri '"
								+ getContentProviderUri()
								+ "' with keyType = '" + keyType.name()
								+ "', keyColumnName = '" + keyColumnName
								+ "', keyValue = '" + keyValue
								+ "'. Will use the first one from the list.");
			}
			
			if (cur.moveToFirst())
				return createDataObject(cur);
		} finally {
			cur.close();
		}
		
		return null;
	}
	
	@Override
	public synchronized long create(T daoObject) {
		Uri uri = cr.insert(getContentProviderUri(), prepareContentValues(daoObject));
		long id = ContentUris.parseId(uri);
		daoObject.setId(id);

		cr.notifyChange(ContentUris.withAppendedId(getContentProviderUri(), id), null);
		
		return id;
	}
	
	@Override
	public synchronized Collection<Long> create(Collection<T> dataObjects) {
		Collection<Long> result = null;
		
		for (T dataObject : dataObjects) {
			Uri uri = cr.insert(getContentProviderUri(), prepareContentValues(dataObject));
			Long id = ContentUris.parseId(uri);
			
			dataObject.setId(id);
			
			if (result == null) {
				result = new ArrayList<Long>();
			}
			
			result.add(id);
		}
		

		cr.notifyChange(getContentProviderUri(), null);
		
		if (result == null) {
			return Collections.emptyList();
		}
		
		return result;
	}

	@Override
	public synchronized int delete(long id) {
		Uri uri = ContentUris.withAppendedId(getContentProviderUri(), id);
		return cr.delete(uri, null, null);
	}

	@Override
	public synchronized int delete(String keyValue) {
		return delete(getKeyColumnType(), getKeyColumnName(), keyValue);
	}
	
	@Override
	public synchronized int delete(SQLiteType keyType,
			String keyColumnName, String keyValue) {
		String[] whereArgs = { keyValue };
		String where = String.format(getWhereFromSQLiteType(keyType), keyColumnName);
		
		return cr.delete(getContentProviderUri(), where, whereArgs);
	}

	public synchronized void update(T daoObject) {
		long id = daoObject.getId();
		
		String[] whereArgs;
		String where;
		
		if (id != DatabaseObject.ID_NONE) {
			cr.update(ContentUris.withAppendedId(getContentProviderUri(), id), prepareContentValues(daoObject), null, null);
		} else {
			whereArgs = new String[]{ daoObject.getKeyValue() };
			where = String.format(getWhereFromSQLiteType(getKeyColumnType()), getKeyColumnName());
			
			cr.update(getContentProviderUri(), prepareContentValues(daoObject), where, whereArgs);
		}
	}
	
	public synchronized Collection<String> readAllKeys() {
		String[] projectionKeyOnly = {	getKeyColumnName() };
			
		Cursor cur = cr.query(
				getContentProviderUri(), 
				projectionKeyOnly, 
				null,
				null,
				null
				);
		
		try {
			Collection<String> result = new ArrayList<String>();
			
			while (cur.moveToNext()) {
				result.add(cur.getString(cur.getColumnIndexOrThrow(getKeyColumnName())));
			}

			return result;
		} finally {
			cur.close();
		}
	}
	
	protected ContentResolver getContentResolver() {
		return cr;
	}
	
	@Override
	public void registerContentObserver(final Dao.Observer<T> observer) {
		DaoObserver<T> daoObserver;
		
		if (observer instanceof DaoObserver) {
			daoObserver = (DaoObserver<T>)observer;
		} else {
//			daoObserver = new DaoObserver<T>() {
//				@Override
//				public void onDataChanged(boolean selfChange, Collection<T> dataObjects) {
//					observer.onDataChanged(selfChange, dataObjects);
//				}
//				
//				@Override
//				public void onDataChanged(boolean selfChange, T dataObject) {
//					observer.onDataChanged(selfChange, dataObject);
//				}
//			};
			
			throw new IllegalArgumentException("observer is not derived from DaoObserver which is not supported now. You should create observer by extending DaoObserver abstract class.");
		}
		
		daoObserver.setDao(this);
		cr.registerContentObserver(getContentProviderUri(), true, daoObserver.getContentObserver());
	}
	
	@Override
	public void unregisterContentObserver(Dao.Observer<T> observer) {
		DaoObserver<T> daoObserver;
		
		if (observer instanceof DaoObserver) {
			daoObserver = (DaoObserver<T>)observer;
		} else {
			throw new IllegalArgumentException("observer is not derived from DaoObserver which is not supported now. You should create observer by extending DaoObserver abstract class.");
		}
		
		cr.unregisterContentObserver(daoObserver.getContentObserver());
	}
	
	protected abstract ContentValues prepareContentValues(T newsObject);
	protected abstract T createDataObject(Cursor cur);
	protected abstract String getKeyColumnName();
	protected abstract SQLiteType getKeyColumnType();
	protected abstract String getIdColumnName();
	protected abstract String[] getProjectionAll();
	protected abstract Uri getContentProviderUri();
}
package com.example.alcoholchecker.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.alcoholchecker.data.model.AlcoholCheckRecord;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AlcoholCheckDao_Impl implements AlcoholCheckDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AlcoholCheckRecord> __insertionAdapterOfAlcoholCheckRecord;

  public AlcoholCheckDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAlcoholCheckRecord = new EntityInsertionAdapter<AlcoholCheckRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `alcohol_check_records` (`id`,`userId`,`userName`,`checkType`,`alcoholLevel`,`result`,`photoPath`,`latitude`,`longitude`,`note`,`checkedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AlcoholCheckRecord entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getUserName());
        statement.bindString(4, entity.getCheckType());
        statement.bindDouble(5, entity.getAlcoholLevel());
        statement.bindString(6, entity.getResult());
        if (entity.getPhotoPath() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPhotoPath());
        }
        if (entity.getLatitude() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getLongitude());
        }
        if (entity.getNote() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getNote());
        }
        statement.bindLong(11, entity.getCheckedAt());
      }
    };
  }

  @Override
  public Object insertRecord(final AlcoholCheckRecord record,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAlcoholCheckRecord.insertAndReturnId(record);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AlcoholCheckRecord>> getAllRecords() {
    final String _sql = "SELECT * FROM alcohol_check_records ORDER BY checkedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"alcohol_check_records"}, new Callable<List<AlcoholCheckRecord>>() {
      @Override
      @NonNull
      public List<AlcoholCheckRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfUserName = CursorUtil.getColumnIndexOrThrow(_cursor, "userName");
          final int _cursorIndexOfCheckType = CursorUtil.getColumnIndexOrThrow(_cursor, "checkType");
          final int _cursorIndexOfAlcoholLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "alcoholLevel");
          final int _cursorIndexOfResult = CursorUtil.getColumnIndexOrThrow(_cursor, "result");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCheckedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "checkedAt");
          final List<AlcoholCheckRecord> _result = new ArrayList<AlcoholCheckRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlcoholCheckRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpUserName;
            _tmpUserName = _cursor.getString(_cursorIndexOfUserName);
            final String _tmpCheckType;
            _tmpCheckType = _cursor.getString(_cursorIndexOfCheckType);
            final float _tmpAlcoholLevel;
            _tmpAlcoholLevel = _cursor.getFloat(_cursorIndexOfAlcoholLevel);
            final String _tmpResult;
            _tmpResult = _cursor.getString(_cursorIndexOfResult);
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final long _tmpCheckedAt;
            _tmpCheckedAt = _cursor.getLong(_cursorIndexOfCheckedAt);
            _item = new AlcoholCheckRecord(_tmpId,_tmpUserId,_tmpUserName,_tmpCheckType,_tmpAlcoholLevel,_tmpResult,_tmpPhotoPath,_tmpLatitude,_tmpLongitude,_tmpNote,_tmpCheckedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<AlcoholCheckRecord>> getRecordsByUser(final String userId) {
    final String _sql = "SELECT * FROM alcohol_check_records WHERE userId = ? ORDER BY checkedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"alcohol_check_records"}, new Callable<List<AlcoholCheckRecord>>() {
      @Override
      @NonNull
      public List<AlcoholCheckRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfUserName = CursorUtil.getColumnIndexOrThrow(_cursor, "userName");
          final int _cursorIndexOfCheckType = CursorUtil.getColumnIndexOrThrow(_cursor, "checkType");
          final int _cursorIndexOfAlcoholLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "alcoholLevel");
          final int _cursorIndexOfResult = CursorUtil.getColumnIndexOrThrow(_cursor, "result");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCheckedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "checkedAt");
          final List<AlcoholCheckRecord> _result = new ArrayList<AlcoholCheckRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlcoholCheckRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpUserName;
            _tmpUserName = _cursor.getString(_cursorIndexOfUserName);
            final String _tmpCheckType;
            _tmpCheckType = _cursor.getString(_cursorIndexOfCheckType);
            final float _tmpAlcoholLevel;
            _tmpAlcoholLevel = _cursor.getFloat(_cursorIndexOfAlcoholLevel);
            final String _tmpResult;
            _tmpResult = _cursor.getString(_cursorIndexOfResult);
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final long _tmpCheckedAt;
            _tmpCheckedAt = _cursor.getLong(_cursorIndexOfCheckedAt);
            _item = new AlcoholCheckRecord(_tmpId,_tmpUserId,_tmpUserName,_tmpCheckType,_tmpAlcoholLevel,_tmpResult,_tmpPhotoPath,_tmpLatitude,_tmpLongitude,_tmpNote,_tmpCheckedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<AlcoholCheckRecord>> getRecordsByDateRange(final long startTime,
      final long endTime) {
    final String _sql = "SELECT * FROM alcohol_check_records WHERE checkedAt BETWEEN ? AND ? ORDER BY checkedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"alcohol_check_records"}, new Callable<List<AlcoholCheckRecord>>() {
      @Override
      @NonNull
      public List<AlcoholCheckRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfUserName = CursorUtil.getColumnIndexOrThrow(_cursor, "userName");
          final int _cursorIndexOfCheckType = CursorUtil.getColumnIndexOrThrow(_cursor, "checkType");
          final int _cursorIndexOfAlcoholLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "alcoholLevel");
          final int _cursorIndexOfResult = CursorUtil.getColumnIndexOrThrow(_cursor, "result");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCheckedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "checkedAt");
          final List<AlcoholCheckRecord> _result = new ArrayList<AlcoholCheckRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlcoholCheckRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpUserName;
            _tmpUserName = _cursor.getString(_cursorIndexOfUserName);
            final String _tmpCheckType;
            _tmpCheckType = _cursor.getString(_cursorIndexOfCheckType);
            final float _tmpAlcoholLevel;
            _tmpAlcoholLevel = _cursor.getFloat(_cursorIndexOfAlcoholLevel);
            final String _tmpResult;
            _tmpResult = _cursor.getString(_cursorIndexOfResult);
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final long _tmpCheckedAt;
            _tmpCheckedAt = _cursor.getLong(_cursorIndexOfCheckedAt);
            _item = new AlcoholCheckRecord(_tmpId,_tmpUserId,_tmpUserName,_tmpCheckType,_tmpAlcoholLevel,_tmpResult,_tmpPhotoPath,_tmpLatitude,_tmpLongitude,_tmpNote,_tmpCheckedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<AlcoholCheckRecord>> getDetectedRecords() {
    final String _sql = "SELECT * FROM alcohol_check_records WHERE result = '検出' ORDER BY checkedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"alcohol_check_records"}, new Callable<List<AlcoholCheckRecord>>() {
      @Override
      @NonNull
      public List<AlcoholCheckRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfUserName = CursorUtil.getColumnIndexOrThrow(_cursor, "userName");
          final int _cursorIndexOfCheckType = CursorUtil.getColumnIndexOrThrow(_cursor, "checkType");
          final int _cursorIndexOfAlcoholLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "alcoholLevel");
          final int _cursorIndexOfResult = CursorUtil.getColumnIndexOrThrow(_cursor, "result");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCheckedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "checkedAt");
          final List<AlcoholCheckRecord> _result = new ArrayList<AlcoholCheckRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlcoholCheckRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpUserName;
            _tmpUserName = _cursor.getString(_cursorIndexOfUserName);
            final String _tmpCheckType;
            _tmpCheckType = _cursor.getString(_cursorIndexOfCheckType);
            final float _tmpAlcoholLevel;
            _tmpAlcoholLevel = _cursor.getFloat(_cursorIndexOfAlcoholLevel);
            final String _tmpResult;
            _tmpResult = _cursor.getString(_cursorIndexOfResult);
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final long _tmpCheckedAt;
            _tmpCheckedAt = _cursor.getLong(_cursorIndexOfCheckedAt);
            _item = new AlcoholCheckRecord(_tmpId,_tmpUserId,_tmpUserName,_tmpCheckType,_tmpAlcoholLevel,_tmpResult,_tmpPhotoPath,_tmpLatitude,_tmpLongitude,_tmpNote,_tmpCheckedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

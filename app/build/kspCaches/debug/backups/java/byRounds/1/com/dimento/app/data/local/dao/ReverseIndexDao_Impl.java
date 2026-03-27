package com.dimento.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.dimento.app.data.local.entity.ReverseIndexEntity;
import com.dimento.app.data.local.model.EventWithGroupNameRow;
import java.lang.Class;
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
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ReverseIndexDao_Impl implements ReverseIndexDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReverseIndexEntity> __insertionAdapterOfReverseIndexEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteForEvent;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public ReverseIndexDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReverseIndexEntity = new EntityInsertionAdapter<ReverseIndexEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reverse_index` (`keyword`,`eventId`,`groupId`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReverseIndexEntity entity) {
        statement.bindString(1, entity.getKeyword());
        statement.bindLong(2, entity.getEventId());
        statement.bindLong(3, entity.getGroupId());
      }
    };
    this.__preparedStmtOfDeleteForEvent = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM reverse_index WHERE eventId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM reverse_index";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<ReverseIndexEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReverseIndexEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteForEvent(final long eventId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteForEvent.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, eventId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteForEvent.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object searchEvents(final String query, final Long groupId,
      final Continuation<? super List<EventWithGroupNameRow>> $completion) {
    final String _sql = "\n"
            + "        SELECT DISTINCT e.id, e.groupId, e.text, e.eventDateMillis, e.recordedDateMillis, e.completedDateMillis, e.voicePath, g.name AS groupName\n"
            + "        FROM reverse_index idx\n"
            + "        INNER JOIN memory_events e ON e.id = idx.eventId\n"
            + "        INNER JOIN memory_groups g ON g.id = e.groupId\n"
            + "        WHERE idx.keyword LIKE ? || '%'\n"
            + "          AND (? IS NULL OR idx.groupId = ?)\n"
            + "        ORDER BY e.eventDateMillis DESC, e.id DESC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    if (groupId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, groupId);
    }
    _argIndex = 3;
    if (groupId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, groupId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EventWithGroupNameRow>>() {
      @Override
      @NonNull
      public List<EventWithGroupNameRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfGroupId = 1;
          final int _cursorIndexOfText = 2;
          final int _cursorIndexOfEventDateMillis = 3;
          final int _cursorIndexOfRecordedDateMillis = 4;
          final int _cursorIndexOfCompletedDateMillis = 5;
          final int _cursorIndexOfVoicePath = 6;
          final int _cursorIndexOfGroupName = 7;
          final List<EventWithGroupNameRow> _result = new ArrayList<EventWithGroupNameRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EventWithGroupNameRow _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGroupId;
            _tmpGroupId = _cursor.getLong(_cursorIndexOfGroupId);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final long _tmpEventDateMillis;
            _tmpEventDateMillis = _cursor.getLong(_cursorIndexOfEventDateMillis);
            final long _tmpRecordedDateMillis;
            _tmpRecordedDateMillis = _cursor.getLong(_cursorIndexOfRecordedDateMillis);
            final Long _tmpCompletedDateMillis;
            if (_cursor.isNull(_cursorIndexOfCompletedDateMillis)) {
              _tmpCompletedDateMillis = null;
            } else {
              _tmpCompletedDateMillis = _cursor.getLong(_cursorIndexOfCompletedDateMillis);
            }
            final String _tmpVoicePath;
            if (_cursor.isNull(_cursorIndexOfVoicePath)) {
              _tmpVoicePath = null;
            } else {
              _tmpVoicePath = _cursor.getString(_cursorIndexOfVoicePath);
            }
            final String _tmpGroupName;
            _tmpGroupName = _cursor.getString(_cursorIndexOfGroupName);
            _item = new EventWithGroupNameRow(_tmpId,_tmpGroupId,_tmpText,_tmpEventDateMillis,_tmpRecordedDateMillis,_tmpCompletedDateMillis,_tmpVoicePath,_tmpGroupName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

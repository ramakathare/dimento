package com.dimento.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.dimento.app.data.local.entity.MemoryEventEntity;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MemoryEventDao_Impl implements MemoryEventDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MemoryEventEntity> __insertionAdapterOfMemoryEventEntity;

  private final EntityDeletionOrUpdateAdapter<MemoryEventEntity> __updateAdapterOfMemoryEventEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public MemoryEventDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMemoryEventEntity = new EntityInsertionAdapter<MemoryEventEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `memory_events` (`id`,`groupId`,`text`,`eventDateMillis`,`recordedDateMillis`,`completedDateMillis`,`voicePath`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MemoryEventEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getGroupId());
        statement.bindString(3, entity.getText());
        statement.bindLong(4, entity.getEventDateMillis());
        statement.bindLong(5, entity.getRecordedDateMillis());
        if (entity.getCompletedDateMillis() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getCompletedDateMillis());
        }
        if (entity.getVoicePath() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getVoicePath());
        }
      }
    };
    this.__updateAdapterOfMemoryEventEntity = new EntityDeletionOrUpdateAdapter<MemoryEventEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `memory_events` SET `id` = ?,`groupId` = ?,`text` = ?,`eventDateMillis` = ?,`recordedDateMillis` = ?,`completedDateMillis` = ?,`voicePath` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MemoryEventEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getGroupId());
        statement.bindString(3, entity.getText());
        statement.bindLong(4, entity.getEventDateMillis());
        statement.bindLong(5, entity.getRecordedDateMillis());
        if (entity.getCompletedDateMillis() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getCompletedDateMillis());
        }
        if (entity.getVoicePath() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getVoicePath());
        }
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM memory_events WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final MemoryEventEntity event,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMemoryEventEntity.insertAndReturnId(event);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MemoryEventEntity event,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMemoryEventEntity.handle(event);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long eventId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
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
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final long eventId,
      final Continuation<? super MemoryEventEntity> $completion) {
    final String _sql = "SELECT * FROM memory_events WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, eventId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MemoryEventEntity>() {
      @Override
      @Nullable
      public MemoryEventEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGroupId = CursorUtil.getColumnIndexOrThrow(_cursor, "groupId");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfEventDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "eventDateMillis");
          final int _cursorIndexOfRecordedDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "recordedDateMillis");
          final int _cursorIndexOfCompletedDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "completedDateMillis");
          final int _cursorIndexOfVoicePath = CursorUtil.getColumnIndexOrThrow(_cursor, "voicePath");
          final MemoryEventEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new MemoryEventEntity(_tmpId,_tmpGroupId,_tmpText,_tmpEventDateMillis,_tmpRecordedDateMillis,_tmpCompletedDateMillis,_tmpVoicePath);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MemoryEventEntity>> observeByGroup(final long groupId) {
    final String _sql = "\n"
            + "        SELECT * FROM memory_events\n"
            + "        WHERE groupId = ?\n"
            + "        ORDER BY eventDateMillis ASC, id ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, groupId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"memory_events"}, new Callable<List<MemoryEventEntity>>() {
      @Override
      @NonNull
      public List<MemoryEventEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGroupId = CursorUtil.getColumnIndexOrThrow(_cursor, "groupId");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfEventDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "eventDateMillis");
          final int _cursorIndexOfRecordedDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "recordedDateMillis");
          final int _cursorIndexOfCompletedDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "completedDateMillis");
          final int _cursorIndexOfVoicePath = CursorUtil.getColumnIndexOrThrow(_cursor, "voicePath");
          final List<MemoryEventEntity> _result = new ArrayList<MemoryEventEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemoryEventEntity _item;
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
            _item = new MemoryEventEntity(_tmpId,_tmpGroupId,_tmpText,_tmpEventDateMillis,_tmpRecordedDateMillis,_tmpCompletedDateMillis,_tmpVoicePath);
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
  public Object getAllWithGroupNames(
      final Continuation<? super List<EventWithGroupNameRow>> $completion) {
    final String _sql = "\n"
            + "        SELECT e.*, g.name AS groupName\n"
            + "        FROM memory_events e\n"
            + "        INNER JOIN memory_groups g ON g.id = e.groupId\n"
            + "        ORDER BY e.eventDateMillis ASC, e.id ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EventWithGroupNameRow>>() {
      @Override
      @NonNull
      public List<EventWithGroupNameRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGroupId = CursorUtil.getColumnIndexOrThrow(_cursor, "groupId");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfEventDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "eventDateMillis");
          final int _cursorIndexOfRecordedDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "recordedDateMillis");
          final int _cursorIndexOfCompletedDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "completedDateMillis");
          final int _cursorIndexOfVoicePath = CursorUtil.getColumnIndexOrThrow(_cursor, "voicePath");
          final int _cursorIndexOfGroupName = CursorUtil.getColumnIndexOrThrow(_cursor, "groupName");
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

  @Override
  public Object getDueToday(final long startOfDayMillis, final long endOfDayMillis,
      final Continuation<? super List<MemoryEventEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM memory_events\n"
            + "        WHERE eventDateMillis BETWEEN ? AND ?\n"
            + "          AND completedDateMillis IS NULL\n"
            + "        ORDER BY eventDateMillis ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDayMillis);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDayMillis);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MemoryEventEntity>>() {
      @Override
      @NonNull
      public List<MemoryEventEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGroupId = CursorUtil.getColumnIndexOrThrow(_cursor, "groupId");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfEventDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "eventDateMillis");
          final int _cursorIndexOfRecordedDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "recordedDateMillis");
          final int _cursorIndexOfCompletedDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "completedDateMillis");
          final int _cursorIndexOfVoicePath = CursorUtil.getColumnIndexOrThrow(_cursor, "voicePath");
          final List<MemoryEventEntity> _result = new ArrayList<MemoryEventEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemoryEventEntity _item;
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
            _item = new MemoryEventEntity(_tmpId,_tmpGroupId,_tmpText,_tmpEventDateMillis,_tmpRecordedDateMillis,_tmpCompletedDateMillis,_tmpVoicePath);
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

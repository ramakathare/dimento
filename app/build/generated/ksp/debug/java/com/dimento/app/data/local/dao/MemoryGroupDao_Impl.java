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
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.dimento.app.data.local.entity.MemoryGroupEntity;
import com.dimento.app.data.local.model.GroupSummaryRow;
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
public final class MemoryGroupDao_Impl implements MemoryGroupDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MemoryGroupEntity> __insertionAdapterOfMemoryGroupEntity;

  private final EntityDeletionOrUpdateAdapter<MemoryGroupEntity> __deletionAdapterOfMemoryGroupEntity;

  private final EntityDeletionOrUpdateAdapter<MemoryGroupEntity> __updateAdapterOfMemoryGroupEntity;

  public MemoryGroupDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMemoryGroupEntity = new EntityInsertionAdapter<MemoryGroupEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `memory_groups` (`id`,`name`,`createdAtMillis`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MemoryGroupEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getCreatedAtMillis());
      }
    };
    this.__deletionAdapterOfMemoryGroupEntity = new EntityDeletionOrUpdateAdapter<MemoryGroupEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `memory_groups` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MemoryGroupEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfMemoryGroupEntity = new EntityDeletionOrUpdateAdapter<MemoryGroupEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `memory_groups` SET `id` = ?,`name` = ?,`createdAtMillis` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MemoryGroupEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getCreatedAtMillis());
        statement.bindLong(4, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final MemoryGroupEntity group,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMemoryGroupEntity.insertAndReturnId(group);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final MemoryGroupEntity group,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMemoryGroupEntity.handle(group);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MemoryGroupEntity group,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMemoryGroupEntity.handle(group);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final long groupId,
      final Continuation<? super MemoryGroupEntity> $completion) {
    final String _sql = "SELECT * FROM memory_groups WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, groupId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MemoryGroupEntity>() {
      @Override
      @Nullable
      public MemoryGroupEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final MemoryGroupEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            _result = new MemoryGroupEntity(_tmpId,_tmpName,_tmpCreatedAtMillis);
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
  public Flow<List<MemoryGroupEntity>> observeAll() {
    final String _sql = "SELECT * FROM memory_groups ORDER BY name COLLATE NOCASE ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"memory_groups"}, new Callable<List<MemoryGroupEntity>>() {
      @Override
      @NonNull
      public List<MemoryGroupEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final List<MemoryGroupEntity> _result = new ArrayList<MemoryGroupEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemoryGroupEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            _item = new MemoryGroupEntity(_tmpId,_tmpName,_tmpCreatedAtMillis);
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
  public Object getAll(final Continuation<? super List<MemoryGroupEntity>> $completion) {
    final String _sql = "SELECT * FROM memory_groups ORDER BY name COLLATE NOCASE ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MemoryGroupEntity>>() {
      @Override
      @NonNull
      public List<MemoryGroupEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final List<MemoryGroupEntity> _result = new ArrayList<MemoryGroupEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemoryGroupEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            _item = new MemoryGroupEntity(_tmpId,_tmpName,_tmpCreatedAtMillis);
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
  public Object findByName(final String name,
      final Continuation<? super MemoryGroupEntity> $completion) {
    final String _sql = "SELECT * FROM memory_groups WHERE name = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, name);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MemoryGroupEntity>() {
      @Override
      @Nullable
      public MemoryGroupEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final MemoryGroupEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            _result = new MemoryGroupEntity(_tmpId,_tmpName,_tmpCreatedAtMillis);
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
  public Flow<List<GroupSummaryRow>> observeGroupSummaries(final long nowMillis) {
    final String _sql = "\n"
            + "        SELECT\n"
            + "            g.id AS groupId,\n"
            + "            g.name AS name,\n"
            + "            (\n"
            + "                SELECT e.text FROM memory_events e\n"
            + "                WHERE e.groupId = g.id\n"
            + "                ORDER BY e.eventDateMillis DESC, e.id DESC\n"
            + "                LIMIT 1\n"
            + "            ) AS lastMessage,\n"
            + "            (\n"
            + "                SELECT e.eventDateMillis FROM memory_events e\n"
            + "                WHERE e.groupId = g.id\n"
            + "                ORDER BY e.eventDateMillis DESC, e.id DESC\n"
            + "                LIMIT 1\n"
            + "            ) AS lastEventDateMillis,\n"
            + "            (\n"
            + "                SELECT COUNT(*) FROM memory_events e\n"
            + "                WHERE e.groupId = g.id AND e.eventDateMillis > ?\n"
            + "            ) AS futureEventCount\n"
            + "        FROM memory_groups g\n"
            + "        ORDER BY COALESCE(lastEventDateMillis, 0) DESC, g.name COLLATE NOCASE ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, nowMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"memory_events",
        "memory_groups"}, new Callable<List<GroupSummaryRow>>() {
      @Override
      @NonNull
      public List<GroupSummaryRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfGroupId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfLastMessage = 2;
          final int _cursorIndexOfLastEventDateMillis = 3;
          final int _cursorIndexOfFutureEventCount = 4;
          final List<GroupSummaryRow> _result = new ArrayList<GroupSummaryRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GroupSummaryRow _item;
            final long _tmpGroupId;
            _tmpGroupId = _cursor.getLong(_cursorIndexOfGroupId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpLastMessage;
            if (_cursor.isNull(_cursorIndexOfLastMessage)) {
              _tmpLastMessage = null;
            } else {
              _tmpLastMessage = _cursor.getString(_cursorIndexOfLastMessage);
            }
            final Long _tmpLastEventDateMillis;
            if (_cursor.isNull(_cursorIndexOfLastEventDateMillis)) {
              _tmpLastEventDateMillis = null;
            } else {
              _tmpLastEventDateMillis = _cursor.getLong(_cursorIndexOfLastEventDateMillis);
            }
            final int _tmpFutureEventCount;
            _tmpFutureEventCount = _cursor.getInt(_cursorIndexOfFutureEventCount);
            _item = new GroupSummaryRow(_tmpGroupId,_tmpName,_tmpLastMessage,_tmpLastEventDateMillis,_tmpFutureEventCount);
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

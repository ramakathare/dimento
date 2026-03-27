package com.dimento.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.dimento.app.data.local.dao.MemoryEventDao;
import com.dimento.app.data.local.dao.MemoryEventDao_Impl;
import com.dimento.app.data.local.dao.MemoryGroupDao;
import com.dimento.app.data.local.dao.MemoryGroupDao_Impl;
import com.dimento.app.data.local.dao.ReverseIndexDao;
import com.dimento.app.data.local.dao.ReverseIndexDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DiMentoDatabase_Impl extends DiMentoDatabase {
  private volatile MemoryGroupDao _memoryGroupDao;

  private volatile MemoryEventDao _memoryEventDao;

  private volatile ReverseIndexDao _reverseIndexDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `memory_groups` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `createdAtMillis` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `memory_events` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `groupId` INTEGER NOT NULL, `text` TEXT NOT NULL, `eventDateMillis` INTEGER NOT NULL, `recordedDateMillis` INTEGER NOT NULL, `completedDateMillis` INTEGER, `voicePath` TEXT, FOREIGN KEY(`groupId`) REFERENCES `memory_groups`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_memory_events_groupId` ON `memory_events` (`groupId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_memory_events_eventDateMillis` ON `memory_events` (`eventDateMillis`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `reverse_index` (`keyword` TEXT NOT NULL, `eventId` INTEGER NOT NULL, `groupId` INTEGER NOT NULL, PRIMARY KEY(`keyword`, `eventId`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_reverse_index_eventId` ON `reverse_index` (`eventId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_reverse_index_groupId` ON `reverse_index` (`groupId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '86207cb976ecc3ffda3edbd44e7cdea2')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `memory_groups`");
        db.execSQL("DROP TABLE IF EXISTS `memory_events`");
        db.execSQL("DROP TABLE IF EXISTS `reverse_index`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsMemoryGroups = new HashMap<String, TableInfo.Column>(3);
        _columnsMemoryGroups.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryGroups.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryGroups.put("createdAtMillis", new TableInfo.Column("createdAtMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMemoryGroups = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMemoryGroups = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMemoryGroups = new TableInfo("memory_groups", _columnsMemoryGroups, _foreignKeysMemoryGroups, _indicesMemoryGroups);
        final TableInfo _existingMemoryGroups = TableInfo.read(db, "memory_groups");
        if (!_infoMemoryGroups.equals(_existingMemoryGroups)) {
          return new RoomOpenHelper.ValidationResult(false, "memory_groups(com.dimento.app.data.local.entity.MemoryGroupEntity).\n"
                  + " Expected:\n" + _infoMemoryGroups + "\n"
                  + " Found:\n" + _existingMemoryGroups);
        }
        final HashMap<String, TableInfo.Column> _columnsMemoryEvents = new HashMap<String, TableInfo.Column>(7);
        _columnsMemoryEvents.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryEvents.put("groupId", new TableInfo.Column("groupId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryEvents.put("text", new TableInfo.Column("text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryEvents.put("eventDateMillis", new TableInfo.Column("eventDateMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryEvents.put("recordedDateMillis", new TableInfo.Column("recordedDateMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryEvents.put("completedDateMillis", new TableInfo.Column("completedDateMillis", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemoryEvents.put("voicePath", new TableInfo.Column("voicePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMemoryEvents = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysMemoryEvents.add(new TableInfo.ForeignKey("memory_groups", "CASCADE", "NO ACTION", Arrays.asList("groupId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMemoryEvents = new HashSet<TableInfo.Index>(2);
        _indicesMemoryEvents.add(new TableInfo.Index("index_memory_events_groupId", false, Arrays.asList("groupId"), Arrays.asList("ASC")));
        _indicesMemoryEvents.add(new TableInfo.Index("index_memory_events_eventDateMillis", false, Arrays.asList("eventDateMillis"), Arrays.asList("ASC")));
        final TableInfo _infoMemoryEvents = new TableInfo("memory_events", _columnsMemoryEvents, _foreignKeysMemoryEvents, _indicesMemoryEvents);
        final TableInfo _existingMemoryEvents = TableInfo.read(db, "memory_events");
        if (!_infoMemoryEvents.equals(_existingMemoryEvents)) {
          return new RoomOpenHelper.ValidationResult(false, "memory_events(com.dimento.app.data.local.entity.MemoryEventEntity).\n"
                  + " Expected:\n" + _infoMemoryEvents + "\n"
                  + " Found:\n" + _existingMemoryEvents);
        }
        final HashMap<String, TableInfo.Column> _columnsReverseIndex = new HashMap<String, TableInfo.Column>(3);
        _columnsReverseIndex.put("keyword", new TableInfo.Column("keyword", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReverseIndex.put("eventId", new TableInfo.Column("eventId", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReverseIndex.put("groupId", new TableInfo.Column("groupId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReverseIndex = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReverseIndex = new HashSet<TableInfo.Index>(2);
        _indicesReverseIndex.add(new TableInfo.Index("index_reverse_index_eventId", false, Arrays.asList("eventId"), Arrays.asList("ASC")));
        _indicesReverseIndex.add(new TableInfo.Index("index_reverse_index_groupId", false, Arrays.asList("groupId"), Arrays.asList("ASC")));
        final TableInfo _infoReverseIndex = new TableInfo("reverse_index", _columnsReverseIndex, _foreignKeysReverseIndex, _indicesReverseIndex);
        final TableInfo _existingReverseIndex = TableInfo.read(db, "reverse_index");
        if (!_infoReverseIndex.equals(_existingReverseIndex)) {
          return new RoomOpenHelper.ValidationResult(false, "reverse_index(com.dimento.app.data.local.entity.ReverseIndexEntity).\n"
                  + " Expected:\n" + _infoReverseIndex + "\n"
                  + " Found:\n" + _existingReverseIndex);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "86207cb976ecc3ffda3edbd44e7cdea2", "a3e8bb341c0d400804a9e38b976eaf5f");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "memory_groups","memory_events","reverse_index");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `memory_groups`");
      _db.execSQL("DELETE FROM `memory_events`");
      _db.execSQL("DELETE FROM `reverse_index`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(MemoryGroupDao.class, MemoryGroupDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MemoryEventDao.class, MemoryEventDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ReverseIndexDao.class, ReverseIndexDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public MemoryGroupDao memoryGroupDao() {
    if (_memoryGroupDao != null) {
      return _memoryGroupDao;
    } else {
      synchronized(this) {
        if(_memoryGroupDao == null) {
          _memoryGroupDao = new MemoryGroupDao_Impl(this);
        }
        return _memoryGroupDao;
      }
    }
  }

  @Override
  public MemoryEventDao memoryEventDao() {
    if (_memoryEventDao != null) {
      return _memoryEventDao;
    } else {
      synchronized(this) {
        if(_memoryEventDao == null) {
          _memoryEventDao = new MemoryEventDao_Impl(this);
        }
        return _memoryEventDao;
      }
    }
  }

  @Override
  public ReverseIndexDao reverseIndexDao() {
    if (_reverseIndexDao != null) {
      return _reverseIndexDao;
    } else {
      synchronized(this) {
        if(_reverseIndexDao == null) {
          _reverseIndexDao = new ReverseIndexDao_Impl(this);
        }
        return _reverseIndexDao;
      }
    }
  }
}

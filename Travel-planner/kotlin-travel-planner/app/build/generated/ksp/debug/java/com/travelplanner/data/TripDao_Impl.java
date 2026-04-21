package com.travelplanner.data;

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
import java.lang.Class;
import java.lang.Exception;
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
public final class TripDao_Impl implements TripDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Trip> __insertionAdapterOfTrip;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<Trip> __deletionAdapterOfTrip;

  private final EntityDeletionOrUpdateAdapter<Trip> __updateAdapterOfTrip;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTripById;

  public TripDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTrip = new EntityInsertionAdapter<Trip>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `trips` (`id`,`title`,`destination`,`startDate`,`endDate`,`budget`,`currency`,`status`,`description`,`daysJson`,`expensesJson`,`bookingsJson`,`mood`,`rating`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Trip entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDestination());
        statement.bindString(4, entity.getStartDate());
        statement.bindString(5, entity.getEndDate());
        statement.bindDouble(6, entity.getBudget());
        statement.bindString(7, entity.getCurrency());
        final String _tmp = __converters.fromTripStatus(entity.getStatus());
        statement.bindString(8, _tmp);
        statement.bindString(9, entity.getDescription());
        statement.bindString(10, entity.getDaysJson());
        statement.bindString(11, entity.getExpensesJson());
        statement.bindString(12, entity.getBookingsJson());
        statement.bindString(13, entity.getMood());
        statement.bindLong(14, entity.getRating());
        statement.bindLong(15, entity.getCreatedAt());
        statement.bindLong(16, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfTrip = new EntityDeletionOrUpdateAdapter<Trip>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `trips` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Trip entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfTrip = new EntityDeletionOrUpdateAdapter<Trip>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `trips` SET `id` = ?,`title` = ?,`destination` = ?,`startDate` = ?,`endDate` = ?,`budget` = ?,`currency` = ?,`status` = ?,`description` = ?,`daysJson` = ?,`expensesJson` = ?,`bookingsJson` = ?,`mood` = ?,`rating` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Trip entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDestination());
        statement.bindString(4, entity.getStartDate());
        statement.bindString(5, entity.getEndDate());
        statement.bindDouble(6, entity.getBudget());
        statement.bindString(7, entity.getCurrency());
        final String _tmp = __converters.fromTripStatus(entity.getStatus());
        statement.bindString(8, _tmp);
        statement.bindString(9, entity.getDescription());
        statement.bindString(10, entity.getDaysJson());
        statement.bindString(11, entity.getExpensesJson());
        statement.bindString(12, entity.getBookingsJson());
        statement.bindString(13, entity.getMood());
        statement.bindLong(14, entity.getRating());
        statement.bindLong(15, entity.getCreatedAt());
        statement.bindLong(16, entity.getUpdatedAt());
        statement.bindString(17, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteTripById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM trips WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertTrip(final Trip trip, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTrip.insert(trip);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTrip(final Trip trip, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTrip.handle(trip);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTrip(final Trip trip, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTrip.handle(trip);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTripById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTripById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
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
          __preparedStmtOfDeleteTripById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Trip>> getAllTrips() {
    final String _sql = "SELECT * FROM trips ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"trips"}, new Callable<List<Trip>>() {
      @Override
      @NonNull
      public List<Trip> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDestination = CursorUtil.getColumnIndexOrThrow(_cursor, "destination");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "budget");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfDaysJson = CursorUtil.getColumnIndexOrThrow(_cursor, "daysJson");
          final int _cursorIndexOfExpensesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "expensesJson");
          final int _cursorIndexOfBookingsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "bookingsJson");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<Trip> _result = new ArrayList<Trip>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Trip _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDestination;
            _tmpDestination = _cursor.getString(_cursorIndexOfDestination);
            final String _tmpStartDate;
            _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate);
            final String _tmpEndDate;
            _tmpEndDate = _cursor.getString(_cursorIndexOfEndDate);
            final double _tmpBudget;
            _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final TripStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTripStatus(_tmp);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpDaysJson;
            _tmpDaysJson = _cursor.getString(_cursorIndexOfDaysJson);
            final String _tmpExpensesJson;
            _tmpExpensesJson = _cursor.getString(_cursorIndexOfExpensesJson);
            final String _tmpBookingsJson;
            _tmpBookingsJson = _cursor.getString(_cursorIndexOfBookingsJson);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new Trip(_tmpId,_tmpTitle,_tmpDestination,_tmpStartDate,_tmpEndDate,_tmpBudget,_tmpCurrency,_tmpStatus,_tmpDescription,_tmpDaysJson,_tmpExpensesJson,_tmpBookingsJson,_tmpMood,_tmpRating,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getTripById(final String id, final Continuation<? super Trip> $completion) {
    final String _sql = "SELECT * FROM trips WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Trip>() {
      @Override
      @Nullable
      public Trip call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDestination = CursorUtil.getColumnIndexOrThrow(_cursor, "destination");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "budget");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfDaysJson = CursorUtil.getColumnIndexOrThrow(_cursor, "daysJson");
          final int _cursorIndexOfExpensesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "expensesJson");
          final int _cursorIndexOfBookingsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "bookingsJson");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final Trip _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDestination;
            _tmpDestination = _cursor.getString(_cursorIndexOfDestination);
            final String _tmpStartDate;
            _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate);
            final String _tmpEndDate;
            _tmpEndDate = _cursor.getString(_cursorIndexOfEndDate);
            final double _tmpBudget;
            _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final TripStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTripStatus(_tmp);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpDaysJson;
            _tmpDaysJson = _cursor.getString(_cursorIndexOfDaysJson);
            final String _tmpExpensesJson;
            _tmpExpensesJson = _cursor.getString(_cursorIndexOfExpensesJson);
            final String _tmpBookingsJson;
            _tmpBookingsJson = _cursor.getString(_cursorIndexOfBookingsJson);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new Trip(_tmpId,_tmpTitle,_tmpDestination,_tmpStartDate,_tmpEndDate,_tmpBudget,_tmpCurrency,_tmpStatus,_tmpDescription,_tmpDaysJson,_tmpExpensesJson,_tmpBookingsJson,_tmpMood,_tmpRating,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<Trip>> getTripsByStatus(final TripStatus status) {
    final String _sql = "SELECT * FROM trips WHERE status = ? ORDER BY startDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromTripStatus(status);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"trips"}, new Callable<List<Trip>>() {
      @Override
      @NonNull
      public List<Trip> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDestination = CursorUtil.getColumnIndexOrThrow(_cursor, "destination");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfBudget = CursorUtil.getColumnIndexOrThrow(_cursor, "budget");
          final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfDaysJson = CursorUtil.getColumnIndexOrThrow(_cursor, "daysJson");
          final int _cursorIndexOfExpensesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "expensesJson");
          final int _cursorIndexOfBookingsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "bookingsJson");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<Trip> _result = new ArrayList<Trip>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Trip _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDestination;
            _tmpDestination = _cursor.getString(_cursorIndexOfDestination);
            final String _tmpStartDate;
            _tmpStartDate = _cursor.getString(_cursorIndexOfStartDate);
            final String _tmpEndDate;
            _tmpEndDate = _cursor.getString(_cursorIndexOfEndDate);
            final double _tmpBudget;
            _tmpBudget = _cursor.getDouble(_cursorIndexOfBudget);
            final String _tmpCurrency;
            _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
            final TripStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTripStatus(_tmp_1);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpDaysJson;
            _tmpDaysJson = _cursor.getString(_cursorIndexOfDaysJson);
            final String _tmpExpensesJson;
            _tmpExpensesJson = _cursor.getString(_cursorIndexOfExpensesJson);
            final String _tmpBookingsJson;
            _tmpBookingsJson = _cursor.getString(_cursorIndexOfBookingsJson);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final int _tmpRating;
            _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new Trip(_tmpId,_tmpTitle,_tmpDestination,_tmpStartDate,_tmpEndDate,_tmpBudget,_tmpCurrency,_tmpStatus,_tmpDescription,_tmpDaysJson,_tmpExpensesJson,_tmpBookingsJson,_tmpMood,_tmpRating,_tmpCreatedAt,_tmpUpdatedAt);
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

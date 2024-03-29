package ru.slon_ds.rmpdclient.remotecontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ru.slon_ds.rmpdclient.AndroidApplication;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.common.Logger;

public class MessageQueue extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "message_queue.db";

    public static MessageQueue instance() {
        if (_instance == null) {
            _instance = new MessageQueue(AndroidApplication.context());
        }
        return _instance;
    }

    private static MessageQueue _instance = null;

    private MessageQueue(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS message_queue ([id] INTEGER PRIMARY KEY NOT NULL, [data] TEXT NOT NULL, [created_at] TIMESTAMP NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS message_queue");
        onCreate(db);
    }

    public boolean enqueue(EnqueuedData data) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("data", data.toJson());
            contentValues.put("created_at", current_time_utc());
            db.insert("message_queue", null, contentValues);
            return true;
        } catch (Exception e) {
            Logger.exception(this, "enqueue error", e);
            return false;
        }
    }

    public DequeueResult dequeue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT [id], [data] FROM message_queue ORDER BY [created_at] LIMIT 1", null);
        DequeueResult result = null;
        try {
            if (cursor.moveToFirst()) {
                result = new DequeueResult();
                JsonDict json_data = new JsonDict(cursor.getString(cursor.getColumnIndex("data")));
                result.data = json_data.fetch_object("msg");
                result.sequence_number = json_data.fetch("seq", Integer.class);
                result.id = cursor.getInt(cursor.getColumnIndex("id"));
            }
        } catch (SQLException e) {
            Logger.exception(this, "dequeue error", e);
        } catch (JSONException e) {
            Logger.exception(this, "dequeue error (parser)", e);
        } finally {
            cursor.close();
        }
        return result;
    }

    public void remove(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM message_queue WHERE [id]=" + id.toString());
        } catch (SQLException e) {
            Logger.exception(this, "remove error", e);
        }
    }

    private String current_time_utc() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    public class DequeueResult {
        public JsonDict data = null;
        public Integer sequence_number = null;
        public Integer id = null;
    }

    public class EnqueuedData {
        public JsonDict data = null;
        public Integer sequence_number = null;

        public EnqueuedData(JsonDict data, Integer sequence_number) {
            this.data = data;
            this.sequence_number = sequence_number;
        }

        public EnqueuedData() {}

        public String toJson() {
            try {
                JSONObject json = new JSONObject();
                json.put("msg", this.data);
                json.put("seq", this.sequence_number);
                return json.toString();
            } catch (org.json.JSONException e) {
                Logger.exception(this, "error dumping json", e);
                return "";
            }
        }
    }
}

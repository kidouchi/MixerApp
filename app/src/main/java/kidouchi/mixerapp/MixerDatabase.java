package kidouchi.mixerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by iuy407 on 8/23/15.
 */
public class MixerDatabase extends SQLiteOpenHelper {

    private static MixerDatabase mInstance = null;

    private static final String DB_NAME = "mixerDB.db";
    private static final int DB_VERSION = 2;

    public static final String TABLE_SOUND = "table_sound";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SOUND_FILE = "sound_filepath";
//    public static final String COLUMN_COLOR = "color";

    private SQLiteDatabase mixerDB = null;

    private MixerDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static MixerDatabase getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MixerDatabase(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String createTable = "CREATE TABLE " + TABLE_SOUND + " (" +
//                COLUMN_ID + " INTEGER PRIMARY KEY, " +
//                COLUMN_SOUND_FILE + " TEXT, " +
//                COLUMN_COLOR + " TEXT)";
        String createTable = "CREATE TABLE " + TABLE_SOUND + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_SOUND_FILE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS " + TABLE_SOUND;
        db.execSQL(dropTable);
    }

    public void open() throws SQLException {
        mixerDB = this.getWritableDatabase();
    }

    public void close() {
        if (mixerDB != null) {
            mixerDB.close();
        }
    }

    public int createSound(String soundFile) {
        mixerDB.beginTransaction();
        int rowId;
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SOUND_FILE, soundFile);
//            values.put(COLUMN_COLOR, color);
            rowId = (int) mixerDB.insert(TABLE_SOUND, null, values);

            mixerDB.setTransactionSuccessful();
        } finally {
            mixerDB.endTransaction();
        }
        return rowId;
    }

    public Sound getSound(int id) {
        Cursor cursor = mixerDB.query(
                TABLE_SOUND,
                null,
                COLUMN_ID + " = ?",
                new String[] { id+"" },
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            int colId = cursor.getColumnIndex(COLUMN_ID);
            int soundId = cursor.getInt(colId);

            int colFile = cursor.getColumnIndex(COLUMN_SOUND_FILE);
            String soundFile = cursor.getString(colFile);

//            int colColor = cursor.getColumnIndex(COLUMN_COLOR);
//            String soundColor = cursor.getString(colColor);

//            return new Sound(soundId, soundFile, soundColor);
            return new Sound(soundId, soundFile);
        }

        return null;
    }

    public void updateSoundFile(int id, String soundFile) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SOUND_FILE, soundFile);
        mixerDB.update(
                TABLE_SOUND,
                values,
                COLUMN_ID + " = ?",
                new String[] { id+"" });
    }

//    public void updateSoundColor(int id, String color) {
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_COLOR, color);
//        mixerDB.update(
//                TABLE_SOUND,
//                values,
//                COLUMN_ID + "= ?",
//                new String[] { id+"" });
//    }

    public void deleteSound(int id) {
        mixerDB.delete(TABLE_SOUND,
                COLUMN_ID + " = ?",
                new String[] { id+"" });
    }

    public ArrayList<Sound> getAllSounds() {
        ArrayList<Sound> sounds = new ArrayList<Sound>();

        Cursor cursor = mixerDB.query(TABLE_SOUND, null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int colId = cursor.getColumnIndex(COLUMN_ID);
            int id = cursor.getInt(colId);

            int colSound = cursor.getColumnIndex(COLUMN_SOUND_FILE);
            String soundFile = cursor.getString(colSound);

//            int colColor = cursor.getColumnIndex(COLUMN_COLOR);
//            String color = cursor.getString(colColor);

//            sounds.add(new Sound(id, soundFile, color));
            sounds.add(new Sound(id, soundFile));
            cursor.moveToNext();
        }
        return sounds;
    }
}

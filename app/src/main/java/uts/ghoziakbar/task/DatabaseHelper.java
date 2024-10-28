package uts.ghoziakbar.task;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AgendaDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_AGENDA = "agenda";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_STATUS = "status";

    // Instance Singleton
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_AGENDA + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_STATUS + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AGENDA);
        onCreate(db);
    }

    public boolean insertAgenda(String title, String description, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_DESCRIPTION, description);
        contentValues.put(COLUMN_STATUS, status);

        long result = db.insert(TABLE_AGENDA, null, contentValues);
        db.close(); // Tutup database setelah penyimpanan
        return result != -1; // Jika hasilnya -1, berarti penyimpanan gagal
    }

    public List<Agenda> getAllAgendas() {
        List<Agenda> agendaList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_AGENDA, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                // Dapatkan indeks kolom
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
                int statusIndex = cursor.getColumnIndex(COLUMN_STATUS);

                // Periksa apakah kolom ditemukan
                if (idIndex != -1 && titleIndex != -1 && descriptionIndex != -1 && statusIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String title = cursor.getString(titleIndex);
                    String description = cursor.getString(descriptionIndex);
                    String status = cursor.getString(statusIndex);
                    agendaList.add(new Agenda(id, title, description, status));
                } else {
                    Log.e("DatabaseHelper", "Kolom tidak ditemukan");
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return agendaList;
    }


    public void deleteAgenda(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_AGENDA, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close(); // Tutup database setelah penghapusan
    }

    // Metode untuk memperbarui agenda
    public boolean updateAgenda(int id, String title, String description, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_DESCRIPTION, description);
        contentValues.put(COLUMN_STATUS, status);

        int result = db.update(TABLE_AGENDA, contentValues, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close(); // Tutup database setelah pembaruan
        return result > 0; // Kembalikan true jika ada yang diperbarui
    }
}

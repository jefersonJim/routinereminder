package br.com.jry.routinereminder.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jry.routinereminder.entity.Alarme;

/**
 * Created by Jeferson on 08/04/2016.
 */
public class AlarmeDao extends SQLiteOpenHelper{
    private static final String TABELA = "alarme";
    private static final int VERSAO = 1;
    private static final String DATABASE = "ALARME.DB";

    public AlarmeDao(Context context) {
        super(context, DATABASE, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE  "+TABELA+" ( " +
                     "   id INTEGER PRIMARY KEY,   " +
                     "   descricao TEXT,   " +
                     "   mensagem TEXT,   " +
                     "   ativo INTEGER, " + //-- 0(false), 1(true)
                     "   latitude NUMERIC,   " +
                     "   longitude NUMERIC,   " +
                     "   repetir INTEGER," + // -- 0(false), 1(true)
                     "   distancia INTEGER,   " +
                     "   medida TEXT,   " +
                     "   dias TEXT," +
                     "   endereco TEXT," +
                     "   criacao DATETIME" + //-- 1, 2,3,4,5,6,7 => D, S, T, Q, Q, S, S
                     ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABELA;
        db.execSQL(sql);
        onCreate(db);
    }

    public List<Alarme> getListAlarmes(){
        List<Alarme> alarmes = new ArrayList<>();
        String sql = "select * from " + TABELA + " order by id";
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);

        try {
            while (cursor.moveToNext()) {
                Alarme alarme = new Alarme();
                alarme.setId(cursor.getInt(0));
                alarme.setDescricao(cursor.getString(1));
                alarme.setMensagem(cursor.getString(2));
                alarme.setAtivo(cursor.getInt(3));
                alarme.setLatitude(cursor.getDouble(4));
                alarme.setLongitude(cursor.getDouble(5));
                alarme.setDistancia(cursor.getInt(7));
                alarme.setEndereco(cursor.getString(10));

                alarmes.add(alarme);
            }
        } catch (Exception sqle) {
            Log.e("Erro", sqle.getMessage());
        } finally {
            cursor.close();
        }
        return alarmes;
    }

    public long insertOrUpdate(Alarme alarme){
        ContentValues contentValues = new ContentValues();
        contentValues.put("descricao", alarme.getDescricao());
        contentValues.put("mensagem", alarme.getMensagem());
        contentValues.put("latitude",alarme.getLatitude());
        contentValues.put("longitude",alarme.getLongitude());
        contentValues.put("distancia", alarme.getDistancia());
        contentValues.put("endereco",alarme.getEndereco());
        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        contentValues.put("criacao", currentDateandTime);

        if(alarme.getId() != null){
            return getWritableDatabase().update(TABELA, contentValues, "id = ?", new String[]{String.valueOf(alarme.getId())});
        }
        return  getWritableDatabase().insert(TABELA, null, contentValues);
    }

    public int delete(int id){
        return getReadableDatabase().delete(TABELA, "id = ?", new String[]{String.valueOf(id)});
    }

}

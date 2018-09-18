package es.rafaco.devtools.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.database.Cursor;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.filesystem.DevToolsFiles;
import es.rafaco.devtools.filesystem.MediaScannerUtils;
import es.rafaco.devtools.view.activities.PermissionActivity;


/**
 * Can export an sqlite database into a csv file.
 *
 * The file has on the top dbVersion and on top of each table data the name of the table
 *
 * Inspired by
 * https://stackoverflow.com/questions/31367270/exporting-sqlite-database-to-csv-file-in-android
 * and some other SO threads as well.
 *
 */
public class SqliteExporter {

    private static final String TAG = SqliteExporter.class.getSimpleName();
    public static final String DB_BACKUP_DB_VERSION_KEY = "dbVersion";
    public static final String DB_BACKUP_DB_NAME_KEY = "dbName";
    public static final String DB_BACKUP_TABLE_NAME = "table";
    private static final String SEPARATOR = "\n";

    public static String[] getAllDatabases(){
        return DevTools.getAppContext().databaseList();
    }

    public static String export(String dbName, SupportSQLiteDatabase db) throws IOException{

        if( !PermissionActivity.check(PermissionActivity.IntentAction.STORAGE)){
            throw new IOException("Cannot write to external storage");
        }

        File backupFile = DevToolsFiles.prepareDatabase(dbName, new Date().getTime());

        List<String> tables = getTablesOnDataBase(db);
        Log.d(TAG, "Started to fill the backup file in " + backupFile.getAbsolutePath());
        long starTime = System.currentTimeMillis();
        writeCsv(backupFile, dbName, db, tables);
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "Creating backup took " + (endTime - starTime) + "ms.");

        MediaScannerUtils.scan(backupFile);

        return backupFile.getAbsolutePath();
    }

    /**
     * Get all the table names we have in db
     *
     * @param db
     * @return
     */
    public static List<String> getTablesOnDataBase(SupportSQLiteDatabase db){
        Cursor c = null;
        List<String> tables = new ArrayList<>();
        try{
            c = db.query("SELECT name FROM sqlite_master WHERE type='table'");
            if (c.moveToFirst()) {
                while ( !c.isAfterLast() ) {
                    tables.add(c.getString(0));
                    c.moveToNext();
                }
            }
        }
        catch(Exception throwable){
            Log.e(TAG, "Could not get the table names from db", throwable);
        }
        finally{
            if(c!=null)
                c.close();
        }
        return tables;
    }

    private static void writeCsv(File backupFile, String dbName, SupportSQLiteDatabase db, List<String> tables){
        CSVWriter csvWrite = null;
        Cursor curCSV = null;
        try {
            csvWrite = new CSVWriter(new FileWriter(backupFile));
            writeSingleValue(csvWrite, DB_BACKUP_DB_VERSION_KEY + "=" + db.getVersion());
            writeSingleValue(csvWrite, DB_BACKUP_DB_NAME_KEY + "=" + dbName);
            for(String table: tables){
                writeSingleValue(csvWrite, SEPARATOR);
                writeSingleValue(csvWrite, DB_BACKUP_TABLE_NAME + "=" + table);
                curCSV = db.query("SELECT * FROM " + table);
                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext()) {
                    int columns = curCSV.getColumnCount();
                    String[] columnArr = new String[columns];
                    for( int i = 0; i < columns; i++){
                        columnArr[i] = curCSV.getString(i);
                    }
                    csvWrite.writeNext(columnArr);
                }
            }
        }
        catch(Exception sqlEx) {
            Log.e(TAG, sqlEx.getMessage(), sqlEx);
        }finally {
            if(csvWrite != null){
                try {
                    csvWrite.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if( curCSV != null ){
                curCSV.close();
            }
        }
    }

    private static void writeSingleValue(CSVWriter writer, String value){
        writer.writeNext(new String[]{value});
    }
}

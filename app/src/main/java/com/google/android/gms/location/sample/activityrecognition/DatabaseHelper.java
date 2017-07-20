package com.google.android.gms.location.sample.activityrecognition;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;



/**
 * Created by magnust on 07.06.2017.
 */


    /**
     * Inspired by: ProgrammingKnowledge.
     * Source: http://www.codebind.com/android-tutorials-and-examples/android-sqlite-tutorial-example/
     */


    public class DatabaseHelper extends SQLiteOpenHelper {

        // Keeping track of date
        private String date;
        private boolean checkDate = true;
        private int checkDateCounter = 0;
        private final static int CHECK_DATE_INTERVAL = 5; // The number of values entered in the DB before the date will be checked
        private float walkingDistance = 0;
        private float drivingDistance = 0;
        private String oldDate;
        private Cursor lastDateInRow;

        private boolean firstRun = true;

        private static final String LOGG = "DatabaseHelper";
        public static final String DATABASE_NAME = "ActivityLog.db";

        // Table names
        public static final String TABLE_NAME = "USERINPUT";
        public static final String TABLE_DISTANCE = "DISTANCETRACKER";

        // COL's for TABLE_DISTANCE
        public static final String D_COL_1 = "DATE";
        public static final String D_COL_2 = "WALKING";
        public static final String D_COL_3 = "DRIVING";




        // COL's for TABLE_NAME
        // public static final String UI_COL_1 = "ID";
        public static final String UI_COL_2 = "NAME";
        public static final String UI_COL_3 = "SURNAME";
        public static final String UI_COL_4 = "WEIGHT";
        public static final String UI_COL_5 = "CAR_MAKE";
        public static final String UI_COL_6 = "YEARLY_ELECTRICITY_CONSUMPTION";


        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE_NAME + " (" + UI_COL_2 + " TEXT PRIMARY KEY," + UI_COL_3 + " TEXT," + UI_COL_4 + " TEXT," + UI_COL_5 + " TEXT, " + UI_COL_6 + " TEXT)");
            db.execSQL("create table " + TABLE_DISTANCE + " (" + D_COL_1 + " TEXT PRIMARY KEY," + D_COL_2 + " FLOAT," + D_COL_3 + " FLOAT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        public boolean insertData(String name, String surname, String weight, String carMake, String elCons) {
            SQLiteDatabase db = this.getWritableDatabase();
            if (checkIfEmpty(db, TABLE_NAME)) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(UI_COL_2, name);
                contentValues.put(UI_COL_3, surname);
                contentValues.put(UI_COL_4, weight);
                contentValues.put(UI_COL_5, carMake);
                contentValues.put(UI_COL_6, elCons);
                Log.i(LOGG, "Insert data ok, put values");
                long result = db.insert(TABLE_NAME, null, contentValues);
                if (result == -1)
                    return false;
                else
                    Log.i(LOGG, "True returned");
                return true;
            } else {
                // the table is not empty and, it is not possible to insert a new row!
                return false;
            }
        }

        public boolean insertDistance(String activity, Float distance) {
            // Update the checkDateCounter
            checkDateCounter += 1;
            if (checkDateCounter > CHECK_DATE_INTERVAL){
                checkDate = true;
                checkDateCounter = 0;
            }

            SQLiteDatabase db = this.getWritableDatabase();

            // Check if it is necessary to check time
            if(checkDate){
                // Check date
                date = checkTheDate();
                checkDate = false;
                Log.i(LOGG, "Current date: " + date);

                // Check

                // Check if the date row exists
                lastDateInRow = db.rawQuery("select * from " + TABLE_DISTANCE + " WHERE DATE"  +
                        "='" + date + "'", null);
                int cursorHasResults = lastDateInRow.getCount();
                Log.i(LOGG, "cursorHasResult = " + String.format("%.1f", (double)cursorHasResults));

                // Note: if the cursor doesn't have results, the method makes first entry and return
                if(cursorHasResults > 0){
                    lastDateInRow.moveToLast();
                    oldDate = lastDateInRow.getString(lastDateInRow.getColumnIndex("DATE"));

                }else {
                    // This is the exception, only valid first time
                    // Next time check date again to get correct oldDate and cursor object
                    checkDate = true;

                    // Create first row
                    Log.i(LOGG, "First time -> new insert");

                    walkingDistance = 0;
                    drivingDistance = 0;

                    // Have to check activity
                    ContentValues contentValues = new ContentValues();


                    if (activity.equals("WALKING")){
                        // The activity is walking

                        walkingDistance += distance;
                        contentValues.put(D_COL_1, date);
                        contentValues.put(D_COL_2, walkingDistance);
                        contentValues.put(D_COL_3, drivingDistance);
                        long result = db.insert(TABLE_DISTANCE, null, contentValues);
                        Log.i(LOGG, "First ever. Date: "+ date + ", Walking dist.: " + String.format("%.1f", walkingDistance) + ", Driving dist.: " + String.format("%.1f", drivingDistance));
                        if (result == -1) {

                            return false;
                        }
                        else {
                            Log.i(LOGG, "First WALKING detected");

                            return true;
                        }


                    } else if (activity.equals("DRIVING")){
                        // The activity is driving

                        drivingDistance += distance;
                        contentValues.put(D_COL_1, date);
                        contentValues.put(D_COL_2, walkingDistance);
                        contentValues.put(D_COL_3, drivingDistance);

                        long result = db.insert(TABLE_DISTANCE, null, contentValues);
                        Log.i(LOGG, "First ever. Date: "+ date + ", Walking dist.: " + String.format("%.1f", walkingDistance) + ", Driving dist.: " + String.format("%.1f", drivingDistance));
                        if (result == -1) {

                            return false;
                        }
                        else {
                            Log.i(LOGG, "First DRIVING detected");

                            return true;
                        }


                    }

                    return false;
                }
            }

            if(!oldDate.equals(date)){

                // New day
                Log.i(LOGG, "(oldDate != date) -> new insert");
                walkingDistance = 0;
                drivingDistance = 0;
                ContentValues contentValues = new ContentValues();

                // Create row of the day
                Log.i(LOGG, "First time today -> new insert");

                // Have to check activity

                if (activity.equals("WALKING")){
                    // The activity is walking
                    walkingDistance += distance;
                    contentValues.put(D_COL_1, date);
                    contentValues.put(D_COL_2, walkingDistance);
                    contentValues.put(D_COL_3, drivingDistance);
                    long result = db.insert(TABLE_DISTANCE, null, contentValues);
                    // Log to monitor input
                    Log.i(LOGG, "New day. Date: "+ date + ", Walking dist.: " + String.format("%.1f", walkingDistance) + ", Driving dist.: " + String.format("%.1f", drivingDistance));

                    if (result == -1) {

                        return false;
                    }
                    else {
                        Log.i(LOGG, "New day first WALKING detected");

                        return true;
                    }


                } else if (activity.equals("DRIVING")){
                    // The activity is driving
                    drivingDistance += distance;
                    contentValues.put(D_COL_1, date);
                    contentValues.put(D_COL_2, walkingDistance);
                    contentValues.put(D_COL_3, drivingDistance);
                    // Log to monitor input
                    Log.i(LOGG, "New day. Date: "+ date + ", Walking dist.: " + String.format("%.1f", walkingDistance) + ", Driving dist.: " + String.format("%.1f", drivingDistance));

                    long result = db.insert(TABLE_DISTANCE, null, contentValues);

                    if (result == -1) {

                        return false;
                    }
                    else {
                        Log.i(LOGG, "New day first DRIVING detected");

                        return true;
                    }


                }

                return false;

            }else{
                if(firstRun){
                    Log.i(LOGG,"Inside firstRun");
                    // Updating the values
                    walkingDistance = lastDateInRow.getFloat(1);
                    drivingDistance = lastDateInRow.getFloat(2);
                    firstRun = false;

                }
                // Date has not changed update the row
                Log.i(LOGG, "oldDate == date -> update");

                // Check which activity is active
                if (activity.equals("WALKING")){
                    // The activity is walking
                    walkingDistance += distance;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(D_COL_2, walkingDistance);

                    db.update(TABLE_DISTANCE, contentValues,"DATE = ?" , new String[]{date});
                    Log.i(LOGG, "Update. Date: "+ date + ", Walking dist.: " + String.format("%.1f", walkingDistance) + ", Driving dist.: " + String.format("%.1f", drivingDistance));
                    return true;
                } else if (activity.equals("DRIVING")){
                    // The activity is driving
                    drivingDistance += distance;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(D_COL_3, drivingDistance);

                    db.update(TABLE_DISTANCE, contentValues,"DATE = ?" , new String[]{date});
                    Log.i(LOGG, "Update. Date: "+ date + ", Walking dist.: " + String.format("%.1f", walkingDistance) + ", Driving dist.: " + String.format("%.1f", drivingDistance));
                    return true;
                }

                return false;
            }

        }

        public Cursor getAllData() {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
            return res;
        }

        public Cursor getDistance() {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor distRes = db.rawQuery("select * from " + TABLE_DISTANCE, null);
            Log.i(LOGG, "Querry in distance table");
            return distRes;
        }

        public boolean updateData(String name, String surname, String weight, String carMake, String elCons) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(UI_COL_2, name);
            contentValues.put(UI_COL_3, surname);
            contentValues.put(UI_COL_4, weight);
            contentValues.put(UI_COL_5, carMake);
            contentValues.put(UI_COL_6, elCons);
            db.update(TABLE_NAME, contentValues, "NAME = ?", new String[]{name});
            return true;
        }

        public Integer deleteData(String name) {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_NAME, "NAME = ?", new String[]{name});
        }

        private boolean checkIfEmpty(SQLiteDatabase db, String tableName){
            String count = "SELECT count(*) FROM " + tableName;
            Cursor mcursor = db.rawQuery(count, null);
            mcursor.moveToFirst();
            int icount = mcursor.getInt(0);

            // Table is not empty
            if(icount>0){
                return false;
            }

            // Table is empty
            else{
                return true;
            }


        }

        private String checkTheDate(){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c.getTime());

            return formattedDate;
        }




    }


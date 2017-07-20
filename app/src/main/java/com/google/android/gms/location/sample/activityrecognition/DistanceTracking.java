package com.google.android.gms.location.sample.activityrecognition;

        import android.*;
        import android.Manifest;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.support.v4.app.ActivityCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.location.LocationListener;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;




public class DistanceTracking extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String LOG_TAG="DistanceTracking";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final int REQUEST_LOCATION = 2;
    private static final int DELAY = 4000;
    private Location mLastLocation;
    private double totalDistance = 0;
   public TextView mDistanceTextView;
    public TextView mAccuracyTextView;


    // db

    DatabaseHelper myDb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Distance is initiated");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_tracking);

        mDistanceTextView = (TextView) findViewById(R.id.textViewDistance);
        mAccuracyTextView = (TextView) findViewById(R.id.textViewSpeed);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.i(LOG_TAG, "OnCreate connected");

        myDb = new DatabaseHelper(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(DELAY);
        mLocationRequest.setFastestInterval(DELAY);
        Log.i(LOG_TAG, "Interval is set");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(LOG_TAG, "Request location");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Log.i(LOG_TAG, "Textview should be changed");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //String strLongitude = Location.convert(location.getLongitude(), Location.FORMAT_SECONDS);
            //String strLatitude = Location.convert(location.getLatitude(), Location.FORMAT_SECONDS);
            // mDistanceTextView.setText(Double.toString(totalDistance));
            mDistanceTextView.setText(String.format("%.1f", totalDistance));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double distance = mLastLocation.distanceTo(location);

        Log.i(LOG_TAG, "Inside the onLocationChanged");
        mLastLocation = location;
        totalDistance += distance;

        // mDistanceTextView.setText(Double.toString(totalDistance));
        mDistanceTextView.setText( "Not correct: " + String.format("%.1f", totalDistance));




        // The code is tricked to think that it knows activity
        boolean isInserted = myDb.insertDistance("WALKING", (float)distance);

        if(isInserted == true){

        }
        else{
            Toast.makeText(DistanceTracking.this,"Data not Inserted",Toast.LENGTH_LONG).show();
        }


        //Speed
        if(location != null){
            mAccuracyTextView.setText(String.format("%.1f", mLastLocation.getSpeed()));
        }else{
            mAccuracyTextView.setText("0");

        }


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "GoogleApiClient connection has been suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "GoogleApiClient connection has failed");
        Toast.makeText(this, "ConnectionFailed", Toast.LENGTH_LONG).show();
    }


}

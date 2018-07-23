package com.globalpaysolutions.rcpopper.ui.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.globalpaysolutions.rcpopper.R;
import com.globalpaysolutions.rcpopper.models.DialogContent;
import com.globalpaysolutions.rcpopper.models.MarkerData;
import com.globalpaysolutions.rcpopper.presenters.PopPresenterImpl;
import com.globalpaysolutions.rcpopper.utils.Constants;
import com.globalpaysolutions.rcpopper.views.PopView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class Pop extends AppCompatActivity implements OnMapReadyCallback, PopView
{
    private static final String TAG = Pop.class.getSimpleName();

    //MVP
    private PopPresenterImpl mPresenter;

    //Adapters y Layouts
    private Circle mCircle;
    private ImageView bgGold;
    private ImageView bgSilver;
    private ImageView bgBronze;
    private ImageView bgWildcard;
    private GoogleMap mGoogleMap;
    private ProgressDialog mProgressDialog;

    //Global variables
    final private int REQUEST_ACCESS_FINE_LOCATION = 3;
    private Map<String, Marker> mGoldPointsMarkers;
    private Map<String, Marker> mSilverPointsMarkers;
    private Map<String, Marker> mBronzePointsMarkers;
    private Map<String, Marker> mWildcardPointsMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        bgGold = (ImageView) findViewById(R.id.bgGold);
        bgSilver = (ImageView) findViewById(R.id.bgSilver);
        bgBronze = (ImageView) findViewById(R.id.bgBronze);
        bgWildcard = (ImageView) findViewById(R.id.bgWildcard);

        mPresenter = new PopPresenterImpl(this, this, this);

        mGoldPointsMarkers = new HashMap<>();
        mSilverPointsMarkers = new HashMap<>();
        mBronzePointsMarkers = new HashMap<>();
        mWildcardPointsMarkers = new HashMap<>();


        mPresenter.checkSignin();
        mPresenter.setInitialViewsState();
        mPresenter.chekcLocationServiceEnabled();

    }

    @Override
    public void renderMap()
    {
        try
        {
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void addClickListeners()
    {
        bgGold.setOnClickListener(goldListener);
        bgSilver.setOnClickListener(silverListener);
        bgBronze.setOnClickListener(bronzeListener);
        bgWildcard.setOnClickListener(wildcardListener);
    }

    @Override
    public void displayActivateLocationDialog()
    {

    }

    @Override
    public void checkPermissions()
    {
        try
        {
            int checkFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int checkCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);


            if (checkFineLocationPermission != PackageManager.PERMISSION_GRANTED && checkCoarseLocationPermission != PackageManager.PERMISSION_GRANTED)
            {
                if(Build.VERSION.SDK_INT >= 23)
                {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) &&
                            !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION))
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Pop.this);
                        alertDialog.setTitle(getString(R.string.dialog_permissions_title));
                        alertDialog.setMessage(getString(R.string.dialog_permissions_location_content));
                        alertDialog.setPositiveButton(getString(R.string.button_accept), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                ActivityCompat.requestPermissions(Pop.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                            }
                        });
                        alertDialog.show();
                    }
                }
                else
                {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                }
            }
            else
            {
                mPresenter.connnectToLocationService();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void setInitialUserLocation(Location pLocation)
    {
        try
        {
            mPresenter.intializeGeolocation();

            LatLng currentLocation = new LatLng(pLocation.getLatitude(), pLocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(19).build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            mPresenter.chestsQuery(currentLocation);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateUserLocationOnMap(Location pLocation)
    {
        try
        {
            LatLng currentLocation = new LatLng(pLocation.getLatitude(), pLocation.getLongitude());
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));

            mPresenter.updateChestsCriteria(currentLocation);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void showLoadingDialog(String pLabel)
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(pLabel);
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void hideLoadingDialog()
    {
        try
        {
            if (mProgressDialog != null && mProgressDialog.isShowing())
            {
                mProgressDialog.dismiss();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void addGoldPoint(MarkerData markerData, LatLng pLocation)
    {
        try
        {
            Marker marker = mGoldPointsMarkers.get(markerData.getKey());

            if(marker == null)
            {
                marker = mGoogleMap.addMarker(new MarkerOptions().position(pLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gold_marker)));

                marker.setTitle(getString(R.string.title_delete_chest));
                marker.setSnippet(getString(R.string.label_delete_chest));
                marker.setTag(markerData);

                mGoldPointsMarkers.put(markerData.getKey(), marker);
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Gold point couldn't be added: " + ex.getMessage());
        }
    }

    @Override
    public void removeGoldPoint(String pKey)
    {
        try
        {
            Marker marker = mGoldPointsMarkers.get(pKey);
            marker.remove();
            mGoldPointsMarkers.remove(pKey);
        }
        catch (NullPointerException npe)
        {
            Log.i(TAG, "Handled: NullPointerException when trying to remove marker from map");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void addSilverPoint(MarkerData markerData, LatLng pLocation)
    {
        try
        {
            Marker marker = mSilverPointsMarkers.get(markerData.getKey());

            if(marker == null)
            {
                marker = mGoogleMap.addMarker(new MarkerOptions().position(pLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_silver_marker)));

                marker.setTitle(getString(R.string.title_delete_chest));
                marker.setSnippet(getString(R.string.label_delete_chest));
                marker.setTag(markerData);

                mSilverPointsMarkers.put(markerData.getKey(), marker);
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Silver point couldn't be added: " + ex.getMessage());
        }
    }

    @Override
    public void removeSilverPoint(String pKey)
    {
        try
        {
            Marker marker = mSilverPointsMarkers.get(pKey);
            marker.remove();
            mSilverPointsMarkers.remove(pKey);
        }
        catch (NullPointerException npe)
        {
            Log.i(TAG, "Handled: NullPointerException when trying to remove marker from map");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void addBronzePoint(MarkerData markerData, LatLng pLocation)
    {
        try
        {
            Marker marker = mBronzePointsMarkers.get(markerData.getKey());

            if(marker == null)
            {
                marker = mGoogleMap.addMarker(new MarkerOptions().position(pLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bronze_marker)));

                marker.setTitle(getString(R.string.title_delete_chest));
                marker.setSnippet(getString(R.string.label_delete_chest));
                marker.setTag(markerData);

                mBronzePointsMarkers.put(markerData.getKey(), marker);
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Bronze point couldn't be added: " + ex.getMessage());
        }
    }

    @Override
    public void removeBronzePoint(String pKey)
    {
        try
        {
            Marker marker = mBronzePointsMarkers.get(pKey);
            marker.remove();
            mBronzePointsMarkers.remove(pKey);
        }
        catch (NullPointerException npe)
        {
            Log.i(TAG, "Handled: NullPointerException when trying to remove marker from map");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void showToast(String text)
    {
        if(!TextUtils.isEmpty(text))
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showGenericDialog(DialogContent content, DialogInterface.OnClickListener listener)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(content.getTitle());
        alertDialog.setMessage(content.getContent());

        if(listener == null)
        {
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, content.getButton1(), listener);
        }
        else
        {
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, content.getButton1(),
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
        }

        alertDialog.show();
    }

    @Override
    public void addWildcardPoint(MarkerData markerData, LatLng location)
    {
        try
        {
            Marker marker = mWildcardPointsMarkers.get(markerData.getKey());

            if(marker == null)
            {
                marker = mGoogleMap.addMarker(new MarkerOptions().position(location)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_wildcard_marker)));

                marker.setTitle(getString(R.string.title_delete_chest));
                marker.setSnippet(getString(R.string.label_delete_chest));
                marker.setTag(markerData);

                mWildcardPointsMarkers.put(markerData.getKey(), marker);
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Bronze point couldn't be added: " + ex.getMessage());
        }
    }

    @Override
    public void removeWildcardPoint(String pKey)
    {
        try
        {
            Marker marker = mWildcardPointsMarkers.get(pKey);
            marker.remove();
            mWildcardPointsMarkers.remove(pKey);
        }
        catch (NullPointerException npe)
        {
            Log.i(TAG, "Handled: NullPointerException when trying to remove marker from map");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        //mPresenter.checkPermissions();

        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        mGoogleMap.setTrafficEnabled(false);
        mGoogleMap.setIndoorEnabled(true);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);

        mGoogleMap.setOnCameraMoveListener(() ->
        {
            LatLng location = mGoogleMap.getCameraPosition().target;
            mPresenter.updateChestsCriteria(location);
            mPresenter.popLocation(location);

            if (mCircle == null)
                drawMarkerWithCircle(location);
            else
                updateMarkerWithCircle(location);
        });

        mGoogleMap.setOnInfoWindowClickListener(marker ->
        {
            if(marker != null)
            {
                MarkerData markerData = (MarkerData) marker.getTag();
                mPresenter.deleteChest(markerData.getKey(), markerData.getType());
            }
        });

        try
        {
            mGoogleMap.setMyLocationEnabled(true);
        }
        catch (SecurityException ex)
        {
            ex.printStackTrace();
        }

        mPresenter.checkPermissions();
        mPresenter.onMapReady();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    try
                    {
                        Log.i(TAG, "ACCESS FINE LOCATION GRANTED");
                        mGoogleMap.setMyLocationEnabled(true);
                        mPresenter.connnectToLocationService();

                    }
                    catch (SecurityException ex)
                    {
                        ex.printStackTrace();
                    }
                }
                else
                {
                    if(Build.VERSION.SDK_INT >= 23)
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Pop.this);
                        alertDialog.setTitle(getString(R.string.dialog_permissions_title));
                        alertDialog.setMessage(getString(R.string.dialog_permissions_location_content));
                        alertDialog.setPositiveButton(getString(R.string.button_retry), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                ActivityCompat.requestPermissions(Pop.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                            }
                        });
                        alertDialog.show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private View.OnClickListener goldListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            mPresenter.writeChestLocation(Constants.CHEST_TYPE_GOLD);
        }
    };

    private View.OnClickListener silverListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            mPresenter.writeChestLocation(Constants.CHEST_TYPE_SILVER);
        }
    };

    private View.OnClickListener bronzeListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            mPresenter.writeChestLocation(Constants.CHEST_TYPE_BRONZE);
        }
    };

    private View.OnClickListener wildcardListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            mPresenter.writeChestLocation(Constants.CHEST_TYPE_WILDCARD);
        }
    };

    private void drawMarkerWithCircle(LatLng location)
    {
        //Adds circle on map
        mCircle = mGoogleMap.addCircle(new CircleOptions()
                .center(location)
                .radius(17)
                .strokeWidth(3)
                .strokeColor(ContextCompat.getColor(this, R.color.color_semitransparent_yellow))
                .fillColor(ContextCompat.getColor(this, R.color.color_semitransparent_dim_yellow)));
    }

    private void updateMarkerWithCircle(LatLng location)
    {
        mCircle.setCenter(location);
    }

}

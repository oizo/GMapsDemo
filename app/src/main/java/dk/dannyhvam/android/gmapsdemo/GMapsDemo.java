package dk.dannyhvam.android.gmapsdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

public class GMapsDemo extends AppCompatActivity implements OnMapReadyCallback, SeekBar.OnSeekBarChangeListener {

    public static final String TAG = GMapsDemo.class.getSimpleName();

    // Settings
    private boolean mAnimateCamera = false;

    // UI
    private GoogleMap mMap;
    private SeekBar mSeekBar;
    private Circle mCircle;

    // values
    private LatLng mLatLng = new LatLng(56.407244,8.9165593);
    private int mCirclePadding;
    private int mColorFill = Color.argb(100, 255, 0, 0);
    private int mColorStroke = Color.BLACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCirclePadding = (int) (15 * getResources().getDisplayMetrics().density);
        setContentView(R.layout.activity_gmaps_demo);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(mLatLng).title("Herrup Church"));
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mSeekBar.setProgress(25);
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int radius = 100 + (progress * 100);
        LatLngBounds bounds = getBounds(mLatLng, radius);
        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(bounds, mCirclePadding);
        if (mAnimateCamera) {
            mMap.stopAnimation();
            mMap.animateCamera(camUpdate, 50, null);
        } else {
            mMap.moveCamera(camUpdate);
        }
        if (mCircle != null) {
            mCircle.remove();
        }
        mCircle = mMap.addCircle(createCircleOption(mLatLng, radius));
    }

    public static LatLngBounds getBounds(LatLng center, double radius) {
        return new LatLngBounds.Builder()
                .include(SphericalUtil.computeOffset(center, radius, 0))
                .include(SphericalUtil.computeOffset(center, radius, 90))
                .include(SphericalUtil.computeOffset(center, radius, 180))
                .include(SphericalUtil.computeOffset(center, radius, 270))
                .build();
    }

    private CircleOptions createCircleOption(LatLng latLng, double radius) {
        return new CircleOptions()
                .center(latLng)
                .radius(radius)
                .fillColor(mColorFill)
                .strokeColor(mColorStroke)
                .strokeWidth(10);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, Menu.NONE, "Toggle map animation");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            mAnimateCamera = !mAnimateCamera;
            String msg = mAnimateCamera ? "Animating map movement" : "No animation of map movement";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}

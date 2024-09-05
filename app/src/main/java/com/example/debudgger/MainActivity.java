package com.example.debudgger;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

//<a target="_blank" href="https://icons8.com/icon/88561/done">Check</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
// <a target="_blank" href="https://icons8.com/icon/39942/expand-arrow">Expand Arrow</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a> <- no more legal shites
// <a target="_blank" href="https://icons8.com/icon/Z171W6tuzsMp/pencil">Pencil</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a> <- no mroe legal shites
// <a target="_blank" href="https://icons8.com/icon/3220/plus">Plus</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
//<a target="_blank" href="https://icons8.com/icon/100913/sorting-arrowheads">Sort</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
//<a target="_blank" href="https://icons8.com/icon/1941/trash">Trash</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
//<a target="_blank" href="https://icons8.com/icon/123372/update-left-rotation">Reset</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
public class MainActivity extends AppCompatActivity implements DatePicker.DatePickerListener {
    ImageView start, add, sort;
    TextView resetSort, stats, total;
    boolean isDrawerOpen = true;
    Uri img;
    ActivityResultLauncher<Intent> startAct;
    AppDatabase db;
    SavingsListener sl;
    RecyclerView rv;
    RvSavings adapter;
    String givenDate;
    String locationStr;
    String[] locationPos = new String[4];
    FusedLocationProviderClient locator;
    final static int REQ_CODE = 100;
    private final String key = "2dd42133a106e4224b12bb39a4cb245d";


    @Override
    /**
     * TODO
     * - Add all the sorting tomoro
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        db = AppDatabase.getInstance(this);
        sl = db.savings();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        start = findViewById(R.id.iv_start);
        add = findViewById(R.id.iv_add);
        sort = findViewById(R.id.iv_sort);
        resetSort = findViewById(R.id.textView5);
        stats = findViewById(R.id.stats);
        total = findViewById(R.id.total);

        start();
        start.setOnClickListener(v -> {
            if (isDrawerOpen) {
                openIVDrawer();
            } else {
                closeIVDrawer();
            }
            isDrawerOpen = !isDrawerOpen;
        });
            sort.setOnClickListener(l->{
                Dialog dg = new Dialog(this);
                dg.setContentView(R.layout.diag_sortdata);
                closeIVDrawer();
                isDrawerOpen = !isDrawerOpen;
                dg.setCancelable(false);

                dg.show();
                dg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dg.getWindow().getAttributes().windowAnimations = R.style.bottomsheetanim;
                dg.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dg.getWindow().setGravity(Gravity.CENTER);

                ImageView returnsigma = dg.findViewById(R.id.returnsigma);
                Button during = dg.findViewById(R.id.during);
                Button before = dg.findViewById(R.id.before);
                Button byname = dg.findViewById(R.id.byname);
                EditText edname = dg.findViewById(R.id.ed_name);

                during.setOnClickListener(li -> {
                    dg.cancel();
                    DatePicker datePicker = new DatePicker(date -> {
                        givenDate = date;
                        adapter = new RvSavings(sl.sortedDataWhenBought(givenDate), this, sl);
                        rv.setAdapter(adapter);
                        givenDate = "";
                    });
                    datePicker.show(getSupportFragmentManager(), "datePicker");
                    resetSort.setVisibility(View.VISIBLE);
                    total.setText("Total: " + adapter.returnAllSpent());
                });

                before.setOnClickListener(li->{
                    dg.cancel();
                    DatePicker datePicker = new DatePicker(date -> {
                        givenDate = date;
                        adapter = new RvSavings(sl.sortedDataBeforeBought(givenDate), this, sl);
                        rv.setAdapter(adapter);
                        givenDate = "";
                    });
                    datePicker.show(getSupportFragmentManager(), "datePicker");
                    resetSort.setVisibility(View.VISIBLE);
                    total.setText("Total: " + adapter.returnAllSpent());
                });
                returnsigma.setOnClickListener(li-> dg.cancel());
            });

        add.setOnClickListener(v->{
            Dialog dg = new Dialog(this);
            dg.setContentView(R.layout.diag_additems);
            dg.show();
            dg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dg.getWindow().getAttributes().windowAnimations = R.style.bottomsheetanim;
            dg.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dg.getWindow().setGravity(Gravity.CENTER);
            closeIVDrawer();
            dg.setCancelable(false);
            isDrawerOpen = !isDrawerOpen;

            EditText name = dg.findViewById(R.id.ed_name);
            EditText price = dg.findViewById(R.id.ed_price);
            Button addImage = dg.findViewById(R.id.button);
            ImageView save = dg.findViewById(R.id.add);
            addImage.setOnClickListener(v1->{
                Intent i = new Intent(MediaStore.ACTION_PICK_IMAGES);
                i.setType("image/*");
                startAct.launch(i);
            });
            ImageView returnsigma = dg.findViewById(R.id.returnsigma);
            returnsigma.setOnClickListener(li-> dg.cancel());
            save.setOnClickListener(v1->{
                String sname = name.getText().toString();
                double dprice = Double.parseDouble(price.getText().toString());

                if(img == null || sname.isEmpty() || price.getText().toString().isEmpty()){
                    Toast.makeText(this, "Missing input fields. Please fill all of them.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar c = Calendar.getInstance();
                String date = c.get(c.YEAR) + "-" + (c.get(c.MONTH) + 1) + "-" + c.get(c.DAY_OF_MONTH);
                sl.insertItem(new SavingsClass(sname, date, dprice, convertUriToByteArray(img)));
                dg.cancel();
                img = null;
                Toast.makeText(this, date, Toast.LENGTH_SHORT).show();;
            });
        });
        adapter = new RvSavings(new ArrayList<>(), this, sl);
        sl.getAllData().observe(this, changes->{
            adapter.updateNewData(changes);
            total.setText("Total: " + adapter.returnAllSpent());
        });
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        resetSort.setText(Html.fromHtml("<u>RESET SORT</u>"));
        resetSort.setOnClickListener(v->{
            adapter = new RvSavings(sl.getAllDataAsList(), this, sl);
            rv.setAdapter(adapter);
            resetSort.setVisibility(View.GONE);
            total.setText("Total: " + adapter.returnAllSpent());
        });
        locator = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
    }

    public void openIVDrawer() {
        add.setVisibility(ImageView.VISIBLE);
        sort.setVisibility(ImageView.VISIBLE);

        AnimatorSet animset = new AnimatorSet();
        animset.playTogether(
                createObjectShow(add, 100, -200f),
                createObjectShow(sort, 150, -400f)
        );

        Animation rotationin = AnimationUtils.loadAnimation(this, R.anim.expand_in);
        start.startAnimation(rotationin);
        animset.start();
    }

    public void closeIVDrawer() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                createObjectClose(add, 100, -200f),
                createObjectClose(sort, 150, -400f)
        );
        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                add.setVisibility(View.INVISIBLE);
                sort.setVisibility(View.INVISIBLE);
            }
        });
        Animation rotationout = AnimationUtils.loadAnimation(this, R.anim.expand_out);
        start.startAnimation(rotationout);
        animatorSet.start();
    }

    public ObjectAnimator createObjectShow(View item, int delay, float y) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(item, "translationY", 0f, y);
        animator.setStartDelay(delay);
        animator.setDuration(400);

        return animator;
    }

    public ObjectAnimator createObjectClose(View item, int delay, float y) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(item, "translationY", y, 0f);
        animator.setStartDelay(delay);
        animator.setDuration(400);

        return animator;
    }

    private byte[] convertUriToByteArray(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            return stream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void start(){
        startAct = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                img = o.getData().getData();
            }
        });
    }

    @Override
    public void onDateSet(String date) {

    }

    public void getWeather(){
        String finalUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + locationPos[1] + "," + locationPos[0] + "&appid=" + key;
        StringRequest request = new StringRequest(Request.Method.POST, finalUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String output = "";
                try {
                    JSONObject json_out = new JSONObject(response);
                    JSONObject jsonWeather = json_out.getJSONObject("main");
                    JSONArray jsonArray = json_out.getJSONArray("weather");
                    JSONObject desc = jsonArray.getJSONObject(0);

                    DecimalFormat df = new DecimalFormat("#.##");

                    String currTemp = df.format(jsonWeather.getDouble("temp") - 273.15);
                    String season = desc.getString("description");

                    locationPos[2] = currTemp;
                    locationPos[3] = season;
                    stats.setText("City: " + locationPos[0] + "\nTemp: " + locationPos[2] +"C" + "\nWeather: " + locationPos[3]);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
    public void getLocation(){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locator.getLastLocation()
                    .addOnSuccessListener(success->{
                        if(success != null){
                            Geocoder geo = new Geocoder(this, Locale.getDefault());
                            try {
                                List<Address> result = geo.getFromLocation(success.getLatitude(), success.getLongitude(), 1);
                                locationPos[0] = result.get(0).getCountryCode().toLowerCase();
                                locationPos[1] = result.get(0).getLocality();
                                locationStr = result.get(0).getCountryCode() + ", " +result.get(0).getLocality() + ", "+ result.get(0).getAddressLine(0) ;
                                Log.d("location", locationStr);
                                getWeather();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }


                        }
                    });
        } else {
            askPermissions();
        }
    }
    public void askPermissions(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQ_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation();
            } else {
                Toast.makeText(this, "no perms", Toast.LENGTH_SHORT).show();
                Log.d("NO PERMS?", "no perms bru");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

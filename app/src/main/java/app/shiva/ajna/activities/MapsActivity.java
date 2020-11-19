package app.shiva.ajna.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.shiva.ajna.fragments.CallViewFragment;
import app.shiva.ajna.DirectionsParser;
import app.shiva.ajna.adapter.AllUsersRecycleViewAdaptor;
import app.shiva.ajna.adapter.ChatBoxAdaptor;
import app.shiva.ajna.adapter.ChatRoomUsersListViewAdaptor;
import app.shiva.ajna.adapter.FriendRequestAdaptor;
import app.shiva.ajna.adapter.ChatRoomsListViewAdaptor;
import app.shiva.ajna.R;

import static android.Manifest.permission.CAMERA;
import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;

import app.shiva.ajna.adapter.FooterTabsAdaptor;
import app.shiva.ajna.adapter.MyContactsAdaptor;
import app.shiva.ajna.model.Call;
import app.shiva.ajna.model.ChatRoom;
import app.shiva.ajna.model.ChatRoomUser;
import app.shiva.ajna.model.Issue;
import app.shiva.ajna.model.LocationTracking;
import app.shiva.ajna.model.MessageSeen;
import app.shiva.ajna.model.TabModel;
import app.shiva.ajna.model.User;
import app.shiva.ajna.model.Message;
import app.shiva.ajna.model.Contact;
import app.shiva.ajna.model.UserActive;
import app.shiva.ajna.model.UserLatLng;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


@SuppressWarnings("ALL")
public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, ZXingScannerView.ResultHandler, View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 101;

    //Map Variables :
    private GoogleMap mMap;
    private Location mLastLocation;
    CameraPosition cameraPosition;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;
    private Polyline polyline;

    private LocationCallback mLocationCallback;
    //Firebase classes
    FirebaseAuth mAuth;
    String userId;
    DatabaseReference mFirebaseDatabaseReference;
    StorageReference storageRef;


    // Ui Components
    public static ConstraintLayout overLappingCons;
    public static ConstraintLayout mainCons;
    private ConstraintLayout topCons;
    private ConstraintLayout footerCons;
    private ImageView userImageView;
    ConstraintLayout searchCons;
    TextView locationConsTextView;
    private CardView userImageViewCons;
    CardView newFriendRequestNotifier;
    ConstraintLayout contactpendingCounterLayout;
    ConstraintLayout showFriendProfile;
    ConstraintLayout middleCons;
    ConstraintLayout markMyLocationCons;
    ImageButton setting;

    // Extra Variables
    private FriendRequestAdaptor friendRequestAdaptor;
    private ProgressBar progressBar;
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;

    LatLng myLatlng;
    User myDetails;
    String activeStatusShareSettingValue = "false";

    // Firebase ValueEvent Listeners

    ValueEventListener getMyDataListener;
    DatabaseReference myDataReference;

    SupportMapFragment mapFragment;
    private static final String TAG = "MapsActivity";
    FooterTabsAdaptor footerTabsAdaptor;
    ArrayList<TabModel> footerTabModels =new ArrayList<TabModel>();

    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.d("oncreateMapsactivity", "onCreate");

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }else{

            handlerThread = new HandlerThread("My background Thread");
            handlerThread.start();

            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            storageRef = FirebaseStorage.getInstance().getReference();
            myDataReference = mFirebaseDatabaseReference.child("Users").child(userId);

            getMyData();
            getAllUsersData();
            getMyContacts();
            getMyChatRooms();
            checkForMyPhoneCalls();

            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.blopsound);
            mediaPlayer.setVolume(0.07f,0.07f);

            //get Permission and Datas
            checkLocationPermission();
            setContentView(R.layout.activity_consumer_maps);
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);



            FragmentManager fm = getSupportFragmentManager();
            SupportMapFragment mapFragment =  SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mapFragment).commit();
            mapFragment.getMapAsync(this);

        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(Intent.ACTION_PROVIDER_CHANGED);
        getApplicationContext().registerReceiver(gpsSwitchStateReceiver, filter);


        // My Main Dailog Box. (Single Dialog Object for all dialogbox)
        myMainDialog = new Dialog(this);
        if (myMainDialog.getWindow() != null) {

            myMainDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            // Dialog box animation for myMainDialog.show() and myMainDialog.dismiss();
            myMainDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;

        }


        // Constraint Layout On top of Map View (Use case: add xml layout directly as view to constraint layout )
        middleCons = findViewById(R.id.middleCons);
        topCons=findViewById(R.id.topCons);
        footerCons=findViewById(R.id.footerCons);

        // New message notification counter
            newFriendRequestNotifier = findViewById(R.id.newFriendRequestNotifier);


        startBackgroundThread();
        setting=findViewById(R.id.setting);
        setting.setOnClickListener(this);
        searchCons= findViewById(R.id.searchCons);
        searchCons.setOnClickListener(this);
        findViewById(R.id.friendRequestButton).setOnClickListener(this);

        mainCons=findViewById(R.id.mainCons);
        overLappingCons=findViewById(R.id.overLappingCons);
        findViewById(R.id.singleTextView).setOnClickListener(this);

        }

        showFooterTabs();
    }

    public int dpToPx(int dp) {
        float density = getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }


    boolean userImageViewSetStatus=false;
    ArrayList<User> liveLocationSharingFriends=new ArrayList<User>();
    Handler mainHandler;
    public void  startBackgroundThread(){
        mainHandler = new Handler();
        mainHandler.postDelayed( new Runnable() {
            public void run() {

                if(myDetails!=null && !userImageViewSetStatus){

                    userImageViewSetStatus=true;

                }

                if(myLatlng !=null){
                    if(usermarker==null && myDetails!=null){
                       createMyMarker();
                    }

                    getfriendLocations();



                }
                mainHandler.postDelayed(this, 100);
            }
        },200);

    }



    private void showQuickAddDialog() {
        ArrayList<User> quickAddUsersList=new ArrayList<>();
        for(User user:allUsersList){
            if(!myAllContacts.contains(user.getUserID())){
                quickAddUsersList.add(user);
            }
        }

        myMainDialog.setContentView(R.layout.searchview);
        myMainDialog.show();
        RecyclerView alluserRecycleView=myMainDialog.findViewById(R.id.alluserRecycleView);

        TextView emptyInfo=myMainDialog.findViewById(R.id.emptyInfo);
        if(quickAddUsersList.size()==0){
            emptyInfo.setVisibility(View.VISIBLE);
            alluserRecycleView.setVisibility(View.GONE);
        }else{
            emptyInfo.setVisibility(View.GONE);
            alluserRecycleView.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        alluserRecycleView.setLayoutManager(horizontalLayoutManager);
        allUsersRecycleViewAdaptor = new AllUsersRecycleViewAdaptor(quickAddUsersList, new AllUsersRecycleViewAdaptor.OnItemClickListener() {
            @Override
            public void onViewClick(User user) {
                showaddcontact(user);
            }

            @Override
            public void onAddClick(User user) {


                    Toast.makeText(getApplicationContext(), "Request Sent to: " + user.getUserName().toString(),
                            Toast.LENGTH_SHORT).show();

                    String contactID = mFirebaseDatabaseReference.child("Contacts").push().getKey();
                    Contact contact = new Contact(contactID, userId, user.getUserID(), "Pending");

                    if (contactID != null) {
                        mFirebaseDatabaseReference.child("Contacts").child(contactID).setValue(contact);

                    }


            }
        });
        alluserRecycleView.setAdapter(allUsersRecycleViewAdaptor);



    }

    private int lastContactsPosition=0;
    private int scrollMyContactsTo;
    MyContactsAdaptor myContactsAdaptor;
    private  ArrayList<User> myContactsUsersList=new ArrayList<User>();
    public void showMyContacts(User scrollToUser) {


        View myContactsView = getLayoutInflater().inflate(R.layout.mycontactsview, null,false);

        RecyclerView mycontacts = myContactsView.findViewById(R.id.myContacts);

        LinearLayoutManager myContactsLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mycontacts.setLayoutManager(myContactsLayoutManager);

        SnapHelper myContactsSnapHelper = new LinearSnapHelper();


        myContactsAdaptor = new MyContactsAdaptor(myContactsUsersList, myDetails, new MyContactsAdaptor.OnItemClickListener() {
            @Override
            public void onViewClick(User user) {
                Toast toast = Toast.makeText(getApplicationContext(), "View Click",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 250);
                toast.show();
            }

            @Override
            public void onSendMessage(User user) {
                for (ChatRoom chatRoom : chatRoomList) {
                    HashMap<String, ChatRoomUser> chatRoomUserHashMap =(HashMap<String, ChatRoomUser>) chatRoom.getChatRoomUsers();

                    if (chatRoomUserHashMap.containsKey(user.getUserID()) && chatRoomUserHashMap.containsKey(userId)) {

                        showeachChatView(chatRoom);

                    }

                }
            }

            @Override
            public void onVideoCall(User user) {
                startCallingFriend(user,"video call");
            }

            @Override
            public void onGetDirections(User user) {
                Toast toast = Toast.makeText(getApplicationContext(), "Get Directions",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 250);
                toast.show();

                if (friendMarkerHashMap.get(user.getUserID()) != null && friendMarkerHashMap != null) {

                    String url= getRequestUrl(usermarker.getPosition(),friendMarkerHashMap.get(user.getUserID()).getPosition());
                    TaskRequestDirections taskRequestDirections=new TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }

            }
        });

        mycontacts.setAdapter(myContactsAdaptor);
        myContactsSnapHelper.attachToRecyclerView(mycontacts);

        mycontacts.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View view = myContactsSnapHelper.findSnapView(myContactsLayoutManager);
                int position=myContactsLayoutManager.getPosition(view);
                Log.e("position",""+position);

                User user= myContactsUsersList.get(position);

                if(newState==SCROLL_STATE_IDLE && lastContactsPosition==position){

                    if (friendMarkerHashMap.get(user.getUserID()) != null && friendMarkerHashMap != null) {
                        changeMapCamera(friendMarkerHashMap.get(user.getUserID()).getPosition(),13);
                        friendMarkerHashMap.get(user.getUserID()).showInfoWindow();
                    }


                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View snapView= myContactsSnapHelper.findSnapView(myContactsLayoutManager);
                int position=myContactsLayoutManager.getPosition(snapView);

                User user= myContactsUsersList.get(position);

                    if (friendMarkerHashMap.get(user.getUserID()) != null && friendMarkerHashMap != null) {
                        changeMapCamera(friendMarkerHashMap.get(user.getUserID()).getPosition(),13);
                        friendMarkerHashMap.get(user.getUserID()).showInfoWindow();
                    }


                if(position!= lastContactsPosition){

                    lastContactsPosition =position;

                }

                for(User user1: myContactsUsersList){

                    int tabsIndex= myContactsUsersList.indexOf(user1);
                    View view = myContactsLayoutManager.getChildAt(tabsIndex);


                    if(snapView!=null && view!=null){
                        if(snapView==view){

                                setRecyclerViewItemDecoration(view,R.drawable.black_round_transparent_background,position, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);


                        }
                        else if(tabsIndex==position-1 || tabsIndex==position+1){

                            setRecyclerViewItemDecoration(view,R.drawable.black_round_transparent_background,position, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,0.5f);


                        }

                        else{

                            setRecyclerViewItemDecoration(view,R.drawable.black_round_transparent_background,position, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,0.2f);



                        }
                    }

                }



            }
        });

        if(scrollToUser!=null){

            mycontacts.scrollToPosition(myContactsUsersList.indexOf(scrollToUser));

        }else{

            myContactsLayoutManager.smoothScrollToPosition(mycontacts, null, lastContactsPosition);

        }
        middleCons.removeAllViews();
        middleCons.addView(myContactsView, ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT);


    }

    private String getRequestUrl(LatLng origin, LatLng dest) {

        String str_org= "origin=" + origin.latitude +","+origin.longitude;
        String str_dest= "destination=" + dest.latitude +","+dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=walking";
        String param=str_org+"&"+str_dest+"&"+sensor+"&"+mode;
        String output="json";
        String url= "https://maps.googleapis.com/maps/api/directions/"+output+"?"+param+"&"+"key="+"AIzaSyDyF6cf1FvOZ7aqQhucZFvQJKYh0B2oRmQ";

        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String resposeString="";
        InputStream inputStream=null;
        HttpURLConnection httpURLConnection=null;
        try {
            URL url= new URL(reqUrl);
            httpURLConnection=(HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            // get directions response
            inputStream=httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer=new StringBuffer();

            String line=null;
            while((line=bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }

            resposeString=stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(inputStream!=null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }


        return resposeString;
    }



    public  class TaskRequestDirections extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String responseString="";

            try {
               responseString=requestDirection(strings[0]);


            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TaskParser taskParser=new TaskParser();
            taskParser.execute(s);
        }
    }


    public class  TaskParser extends AsyncTask<String,Void, List<List<HashMap<String,String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject=null;

            List<List<HashMap<String,String>>> routes=null;

            try {
                jsonObject=new JSONObject(strings[0]);
                DirectionsParser directionsParser=new DirectionsParser();
                routes=directionsParser.parse(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return routes;
        }


        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {

            ArrayList<LatLng> points=null;
            PolylineOptions polylineOptions=null;

            for(List<HashMap<String, String>> path:lists){

                points=new ArrayList<LatLng>();
                polylineOptions=new PolylineOptions();

                for(HashMap<String, String> point:path){
                   double lat= Double.parseDouble(point.get("lat"));
                    double lon= Double.parseDouble(point.get("lng"));

                    points.add(new LatLng(lat,lon));

                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if(polylineOptions!=null){
                polyline = mMap.addPolyline(polylineOptions);

                mainCons.setVisibility(View.GONE);
                findViewById(R.id.navigationCons).setVisibility(View.VISIBLE);

                changeMapCamera(usermarker.getPosition(),10);

            }
            else{
                Toast.makeText(getApplicationContext(),"No direction Found",Toast.LENGTH_LONG).show();
            }
        }
    }

   private void showCameraScanner() {

        mScannerView = new ZXingScannerView(getApplicationContext());
        myMainDialog.setContentView(mScannerView);
        myMainDialog.show();
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if (mScannerView == null) {
                    mScannerView = new ZXingScannerView(getApplicationContext());
                    myMainDialog.setContentView(mScannerView);
                    myMainDialog.show();

                }
                mScannerView.setResultHandler(MapsActivity.this);
                mScannerView.startCamera();

            } else {
                requestPermission();
            }
        }
    }

    ArrayList<User> allUsersList = new ArrayList<User>();
    HashMap<User, String> hashMap = new HashMap<User, String>();
    AllUsersRecycleViewAdaptor allUsersRecycleViewAdaptor;


    ValueEventListener getAllUserListener;
    DatabaseReference allUsersReference;

    private void getAllUsersData() {
        allUsersReference = mFirebaseDatabaseReference.child("Users");
        getAllUserListener = allUsersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    allUsersList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String id = dataSnapshot1.child("userID").getValue(String.class);
                        String name = dataSnapshot1.child("userName").getValue(String.class);
                        String email = dataSnapshot1.child("userEmail").getValue(String.class);
                        String photoUri = dataSnapshot1.child("photoUri").getValue(String.class);
                        String accountMode = (dataSnapshot1.child("accountMode").getValue(String.class));

                        String activeStatus = dataSnapshot1.child("userActive").child("activeStatus").getValue(String.class);
                        String activeStatusShareSetting = dataSnapshot1.child("userActive").child("activeStatusShareSetting").getValue(String.class);
                        String timestamp = dataSnapshot1.child("userActive").child("timestamp").getValue(String.class);

                        UserActive userActive = new UserActive(activeStatus, activeStatusShareSetting, timestamp);

                        String locationTrackingStatus = dataSnapshot1.child("locationTracking").child("locationTrackingStatus").getValue(String.class);

                        String latitude = dataSnapshot1.child("locationTracking").child("userLatLng").child("latitude").getValue(String.class);
                        String longitude = dataSnapshot1.child("locationTracking").child("userLatLng").child("longitude").getValue(String.class);

                        UserLatLng friendUserLatLng = new UserLatLng(latitude, longitude);
                        LocationTracking locationTracking = new LocationTracking(locationTrackingStatus, friendUserLatLng);

                        User newUser = new User(id, name, email, photoUri, accountMode, userActive, locationTracking);


                        // && !id.equals(userId) missing
                        if (id != null  && accountMode != null && accountMode.equals("public") && !id.equals(userId)) {
                            if(!allUsersList.contains(newUser)){
                                allUsersList.add(newUser);
                            }

                        }




                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean mylocationTracking = false;
    Button businessRegisterDoneButton;
    Button businessRegisterCancelButton;

    private void showSettingDialog() {

        myMainDialog.setContentView(R.layout.setting);
        myMainDialog.show();
        Switch accountModeSwitch = myMainDialog.findViewById(R.id.accountModeSwitch);
        Switch locationSwitch = myMainDialog.findViewById(R.id.locationSwitch);
        Switch activeStatusShareSetting = myMainDialog.findViewById(R.id.activeStatusShareSetting);
        if (mylocationTracking) {
            locationSwitch.setChecked(true);
        } else {
            locationSwitch.setChecked(false);

        }
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Location Sharing Started",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 250);
                    toast.show();
                    mylocationTracking = true;
                    locationSwitch.setChecked(true);
                    mFirebaseDatabaseReference.child("Users").child(userId).child("locationTracking").child("locationTrackingStatus").setValue("true");
                } else {
                    // The toggle is disabled
                    Toast toast = Toast.makeText(getApplicationContext(), "Location Sharing ended",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 250);
                    toast.show();
                    if (usermarker != null) {
                        usermarker.setVisible(false);
                    }

                    mFirebaseDatabaseReference.child("Users").child(userId).child("locationTracking").child("locationTrackingStatus").setValue("false");
                    locationSwitch.setChecked(false);
                    mylocationTracking = false;
                }
            }
        });


        if (activeStatusShareSettingValue.equals("true")) {
            activeStatusShareSetting.setChecked(true);
        } else {
            activeStatusShareSetting.setChecked(false);

        }
        activeStatusShareSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast toast = Toast.makeText(getApplicationContext(), "You are activeStatusShareSetting now.",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 250);
                    toast.show();
                    activeStatusShareSetting.setChecked(true);
                    userActiveStatus("true");
                } else {
                    // The toggle is disabled
                    Toast toast = Toast.makeText(getApplicationContext(), "You are inactive now",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 250);
                    toast.show();

                    userActiveStatus("false");
                    activeStatusShareSetting.setChecked(false);

                }
            }
        });

        if (myDetails != null) {
            if (myDetails.getAccountMode().equals("public")) {
                accountModeSwitch.setChecked(true);
            } else {
                accountModeSwitch.setChecked(false);
            }
        }
        accountModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Your Account is Visible now.",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 250);
                    toast.show();
                    accountModeSwitch.setChecked(true);
                    userAccountPublic("public");
                } else {
                    // The toggle is disabled
                    Toast toast = Toast.makeText(getApplicationContext(), "Your Account is Hidden now.",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 250);
                    toast.show();

                    userAccountPublic("hidden");
                    accountModeSwitch.setChecked(false);

                }
            }
        });


    }


    Dialog businessFormDialog;
    int PICK_BUSINESS_PROFILE_IMAGE_REQUEST = 222;
    String newBusinessID;
    ConstraintLayout businessImageViewCons;
    ConstraintLayout extraInfoCons;
    ProgressBar progressBar2;
    ImageView businessImage;
    String newBusinessImageUri = "";
    String businessCategory = "bar";


    HashMap<String, Marker> friendMarkerHashMap = new HashMap<String, Marker>();
    Handler friendLocationHandler = new Handler();

    private void getfriendLocations() {

        for (Contact contact : connectedContacts) {

            String friendId = "";

            if (contact.getSenderID().equals(userId)) {
                friendId = contact.getReceiverID();
            } else if (contact.getReceiverID().equals(userId)) {
                friendId = contact.getSenderID();
            }

            try {
                for (User newUser : myContactsUsersList) {

                    if (newUser.getUserID().equals(friendId)) {

                        if (newUser.getLocationTracking().getLocationTrackingStatus().equals("true")) {
                            View userMarkerView = getLayoutInflater().inflate(R.layout.imageviewmarker, null);
                            ImageView userImage = userMarkerView.findViewById(R.id.image);
                            ConstraintLayout markerbg = userMarkerView.findViewById(R.id.markerbg);
                            //markerbg.setBackground(getResources().getDrawable(R.drawable.friendmarker));



                                double lattitude=Double.valueOf(newUser.getLocationTracking().getUserLatLng().getLatitude());
                                double longitude=Double.valueOf(newUser.getLocationTracking().getUserLatLng().getLongitude());
                                LatLng friendLatLng = new LatLng(lattitude,longitude);


                                Picasso.get()
                                        .load(newUser.getPhotoUri())
                                        .into(userImage, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {


                                                Bitmap bmp = createBitmapFromView(userMarkerView);

                                                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bmp, 180, 180, false));
                                                Marker friendMarker;

                                                if(mMap!=null){
                                                    if (friendMarkerHashMap.containsKey(newUser.getUserID())) {
                                                        friendMarker = friendMarkerHashMap.get(newUser.getUserID());
                                                        friendMarker.setPosition(friendLatLng);
                                                        friendMarker.setVisible(true);
                                                    } else {
                                                        friendMarker = mMap.addMarker(new MarkerOptions()
                                                                .position(friendLatLng).title(newUser.getUserName())
                                                                .visible(true).icon(icon));
                                                        friendMarker.setTag(newUser.getUserID());

                                                        friendMarkerHashMap.put(newUser.getUserID(), friendMarker);
                                                    }


                                                    if (usermarker != null) {
                                                        Location friendLocation = new Location("");
                                                        friendLocation.setLatitude(friendMarker.getPosition().latitude);
                                                        friendLocation.setLongitude(friendMarker.getPosition().longitude);

                                                        Location myLocation = new Location("");
                                                        myLocation.setLatitude(usermarker.getPosition().latitude);
                                                        myLocation.setLongitude(usermarker.getPosition().longitude);


                                                        double distanceDifference = myLocation.distanceTo(friendLocation);
                                                        double distance = (int) (Math.round(distanceDifference));
                                                        if (distance > 1000) {
                                                            distance = distance / 1000;
                                                            friendMarker.setSnippet(distance + " Km away");
                                                        } else {
                                                            friendMarker.setSnippet(distance + " meters away");
                                                        }
                                                    }
                                                }

                                                bmp.recycle();

                                            }

                                            @Override
                                            public void onError(Exception e) {

                                            }
                                        });




                        } else {

                            if (friendMarkerHashMap.get(newUser.getUserID()) != null) {
                                friendMarkerHashMap.get(newUser.getUserID()).setVisible(false);
                            }
                        }

                    }

                }
            } catch (Exception ex) {
                // Here we are logging the exception to see why it happened.
                Log.e(TAG, ex.toString());
            }


        }


    }


    private Bitmap createBitmapFromView(View view) {

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            view.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
            view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
            Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);

        return bitmap;

    }


    ChatRoomsListViewAdaptor chatRoomsListviewAdaptor;
    FriendRequestAdaptor messageFriendRequestAdaptor;
    Boolean createGroupStatusAvailable;
    ConstraintLayout eachMessageChatView;

    DatabaseReference messageRoomsReference;
    Dialog myMainDialog;
    ConstraintLayout messageView;
    LinearLayout messageviewLayout;

    public static boolean messageDialogActive = false;

    private void showMyMessages() {
        createGroupStatusAvailable = false;
        messageDialogActive = true;

        View myMessagesView = getLayoutInflater().inflate(R.layout.mymessages,null,true);



        messageView = myMessagesView.findViewById(R.id.messageView);

        messageviewLayout=myMessagesView.findViewById(R.id.messageviewLayout);
        messageviewLayout.setOnClickListener(this);
        RecyclerView myChatRoomRecycleView = myMessagesView.findViewById(R.id.myMessages);


        ConstraintLayout composingLayout = myMessagesView.findViewById(R.id.composingLayout);
        ImageButton composeMessage = myMessagesView.findViewById(R.id.composeMessage);
        TextView composeHeader = myMessagesView.findViewById(R.id.composeHeader);

        LinearLayoutManager horizontalLayoutManager2
                = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        myChatRoomRecycleView.setLayoutManager(horizontalLayoutManager2);

        chatRoomsListviewAdaptor = new ChatRoomsListViewAdaptor(chatRoomList, allUsersList, new ChatRoomsListViewAdaptor.OnItemClickListener() {
            @Override
            public void onViewClick(ChatRoom chatRoom) {
            Log.e("click","click");
                showeachChatView(chatRoom);
            }

            @Override
            public void onViewLongClick(ChatRoom chatRoom) {

            }

            @Override
            public void onDeleteChatRoom(ChatRoom chatRoom,int position) {
                chatRoomList.remove(chatRoom);
                Log.e("Chat Room Deleted","True");
                chatRoomsListviewAdaptor.notifyItemRemoved(position);
            }
        });
        myChatRoomRecycleView.setAdapter(chatRoomsListviewAdaptor);


        ArrayList<Contact> groupMembers = new ArrayList<Contact>();
        RecyclerView mycontacts = myMessagesView.findViewById(R.id.myContacts);
        LinearLayoutManager horizontalLayoutManager1
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mycontacts.setLayoutManager(horizontalLayoutManager1);
        messageFriendRequestAdaptor = new FriendRequestAdaptor(connectedContacts, allUsersList, new FriendRequestAdaptor.OnItemClickListener() {
            @Override
            public void onViewClick(Contact contact) {


                composingLayout.setVisibility(View.GONE);
                myChatRoomRecycleView.setVisibility(View.VISIBLE);
                composeMessage.setBackgroundResource(R.drawable.composemessage);
                String friendID = "";
                if (contact.getSenderID().equals(userId)) {
                    friendID = contact.getReceiverID();
                } else if (contact.getReceiverID().equals(userId)) {
                    friendID = contact.getSenderID();
                }


                for (ChatRoom chatRoom : chatRoomList) {
                    HashMap<String, ChatRoomUser> chatRoomUserHashMap =(HashMap<String, ChatRoomUser>) chatRoom.getChatRoomUsers();

                    if (chatRoomUserHashMap.containsKey(friendID)) {

                        showeachChatView(chatRoom);

                    }


                }

            }

            @Override
            public void onDelete(Contact contact) {

            }

            @Override
            public void onAccept(Contact contact) {

            }
        });
        mycontacts.setAdapter(messageFriendRequestAdaptor);


        composeMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Value", "tt");

                if (composingLayout.getVisibility() == View.GONE) {
                    composingLayout.setVisibility(View.VISIBLE);
                    myChatRoomRecycleView.setVisibility(View.GONE);
                    composeMessage.setBackgroundResource(R.drawable.delete);
                    groupMembers.clear();
                    //groupMembersAdaptor.notifyDataSetChanged();

                } else {
                    composingLayout.setVisibility(View.GONE);
                    myChatRoomRecycleView.setVisibility(View.VISIBLE);
                    composeMessage.setBackgroundResource(R.drawable.composemessage);
                    composeHeader.setText("Send Message To : ");
                }


            }
        });

        middleCons.removeAllViews();
       middleCons.addView(myMessagesView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);



    }

    ArrayList<ChatRoom> chatRoomList = new ArrayList<ChatRoom>();


    ValueEventListener getMyChatRoomListener;
    DatabaseReference myChatRoomReference;

    private void getMyChatRooms() {
        myChatRoomReference = mFirebaseDatabaseReference.child("Messages Rooms");
        getMyChatRoomListener = myChatRoomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    chatRoomList.clear();
                    unseenmessageCounter = 0;


                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        boolean myChatRoom = false;
                        String chatRoomActiveTime = dataSnapshot1.child("activeTime").getValue(String.class);
                        String chatRoomID = dataSnapshot1.child("chatRoomID").getValue(String.class);


                        HashMap<String, ChatRoomUser> chatRoomUsers = new HashMap<String, ChatRoomUser>();
                        HashMap<String, Message> messagesHashMap = new HashMap<String, Message>();

                        for (DataSnapshot chatRoomUserDatasnapshot : dataSnapshot1.child("chatRoomUsers").getChildren()) {
                            String chatRoomUserId = chatRoomUserDatasnapshot.child("id").getValue(String.class);
                            String chatRoomUserTypingStatus = chatRoomUserDatasnapshot.child("typingStatus").getValue(String.class);
                            ChatRoomUser chatRoomUser = new ChatRoomUser(chatRoomUserId, chatRoomUserTypingStatus);

                            chatRoomUsers.put(chatRoomUser.getId(), chatRoomUser);

                            if (chatRoomUser.getId().equals(userId)) {

                                myChatRoom = true;

                            }

                        }
                        if (myChatRoom) {
                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("messages").getChildren()) {

                                String date = dataSnapshot2.child("date").getValue(String.class);
                                String messageText = dataSnapshot2.child("message").getValue(String.class);
                                String messageId2 = dataSnapshot2.child("messageId").getValue(String.class);
                                String messageType = dataSnapshot2.child("messageType").getValue(String.class);
                                String senderID = dataSnapshot2.child("senderID").getValue(String.class);

                                if (!dataSnapshot2.child("messageSeens").exists()) {


                                    if (senderID != null) {

                                        Message message = new Message(messageText, messageId2, date, senderID, messageType);

                                        if (senderID.equals(userId) || senderID.equals("auto")) {
                                            messagesHashMap.put(message.getMessageId(), message);
                                        }
                                        else{
                                            long currentTime=System.currentTimeMillis();
                                            String deliveredTimeStamp = String.valueOf(currentTime);
                                            String seenTimeStamp = "0";

                                            ArrayList<MessageSeen> messageSeenArrayList = new ArrayList<MessageSeen>();
                                            MessageSeen messageSeen = new MessageSeen(userId, deliveredTimeStamp, seenTimeStamp);
                                            messageSeenArrayList.add(messageSeen);
                                            mFirebaseDatabaseReference.child("Messages Rooms").child(chatRoomID).child("messages").child(messageId2).child("messageSeens").child("0").setValue(messageSeen);

                                            messagesHashMap.put(message.getMessageId(), message);
                                        }
                                    }

                                } else {

                                    String messageTo = dataSnapshot2.child("messageSeens").child("0").child("messageTo").getValue(String.class);
                                    String deliveredTimeStamp = dataSnapshot2.child("messageSeens").child("0").child("deliveredTimeStamp").getValue(String.class);
                                    String seenTimeStamp = dataSnapshot2.child("messageSeens").child("0").child("seenTimeStamp").getValue(String.class);

                                    ArrayList<MessageSeen> messageSeenArrayList = new ArrayList<MessageSeen>();
                                    MessageSeen messageSeen = new MessageSeen(messageTo, deliveredTimeStamp, seenTimeStamp);
                                    messageSeenArrayList.add(messageSeen);

                                    Message message = new Message(messageText, messageId2, date, senderID, messageType, messageSeenArrayList);

                                    messagesHashMap.put(message.getMessageId(), message);

                                    if (messageSeen != null && senderID != null) {
                                        if (messageSeen.getSeenTimeStamp().equals("0") && !senderID.equals(userId)) {

                                            unseenmessageCounter = 1;

                                        }
                                    }

                                }

                            }

                            ChatRoom currentChatRoom = new ChatRoom(chatRoomID, chatRoomActiveTime, chatRoomUsers, messagesHashMap);


                            if (!chatRoomList.contains(currentChatRoom)) {
                                chatRoomList.add(currentChatRoom);
                                Collections.sort(chatRoomList);

                            }
                            if (chatRoomsListviewAdaptor != null) {

                                chatRoomsListviewAdaptor.notifyDataSetChanged();

                            }
                        }



                        if(footerTabModels !=null && footerTabsAdaptor!=null){
                            if (unseenmessageCounter == 0) {
                                footerTabModels.get(1).setNotificationCounter(0);
                                footerTabsAdaptor.notifyDataSetChanged();
                            } else {
                                footerTabModels.get(1).setNotificationCounter(1);
                                footerTabsAdaptor.notifyDataSetChanged();
                            }
                        }



                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    ArrayList<Message> activeChatroomMessages = new ArrayList<Message>();
    ValueEventListener myMessagesListener;
    DatabaseReference myMessagesReference;


   private ArrayList<Message> getMyChatRoomMessages(ChatRoom chatRoom) {
        myMessagesReference = mFirebaseDatabaseReference.child("Messages");
        myMessagesListener = mFirebaseDatabaseReference.child("Messages Rooms").child(chatRoom.getChatRoomID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    activeChatroomMessages.clear();

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child("messages").getChildren()) {

                        String date = dataSnapshot1.child("date").getValue(String.class);
                        String messageText = dataSnapshot1.child("message").getValue(String.class);
                        String messageId2 = dataSnapshot1.child("messageId").getValue(String.class);
                        String messageType = dataSnapshot1.child("messageType").getValue(String.class);
                        String senderID = dataSnapshot1.child("senderID").getValue(String.class);

                        if (!dataSnapshot1.child("messageSeens").exists()) {


                            Message message = new Message(messageText, messageId2, date, senderID, messageType);

                            if (senderID != null && (senderID.equals(userId) || senderID.equals("auto"))) {

                                activeChatroomMessages.add(message);

                            }


                        } else {

                            String messageTo = dataSnapshot1.child("messageSeens").child("0").child("messageTo").getValue(String.class);
                            String deliveredTimeStamp = dataSnapshot1.child("messageSeens").child("0").child("deliveredTimeStamp").getValue(String.class);
                            String seenTimeStamp = dataSnapshot1.child("messageSeens").child("0").child("seenTimeStamp").getValue(String.class);

                            ArrayList<MessageSeen> messageSeenArrayList = new ArrayList<MessageSeen>();
                            MessageSeen messageSeen = new MessageSeen(messageTo, deliveredTimeStamp, seenTimeStamp);
                            messageSeenArrayList.add(messageSeen);

                            if (messageId2 != null && senderID != null && !senderID.equals(userId) && !senderID.equals("auto") && eachChatViewActive) {
                                if (messageSeen != null && messageSeen.getSeenTimeStamp().equals("0") && !messageSeen.getDeliveredTimeStamp().equals("0")) {
                                    long currentDateTime1 = System.currentTimeMillis();
                                    MessageSeen messageSeen1 = new MessageSeen(messageSeen.getMessageTo(), messageSeen.getDeliveredTimeStamp(), String.valueOf(currentDateTime1));

                                    mFirebaseDatabaseReference.child("Messages Rooms").child(chatRoom.getChatRoomID()).child("messages").child(messageId2).child("messageSeens").child("0").setValue(messageSeen1);

                                }

                            }

                            Message message = new Message(messageText, messageId2, date, senderID, messageType, messageSeenArrayList);

                            activeChatroomMessages.add(message);


                        }


                    }

                    if (chatBoxAdaptor != null && activeChatroomMessages!=null) {
                        Collections.sort(activeChatroomMessages);
                        chatBoxAdaptor.notifyDataSetChanged();
                    }

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child("chatRoomUsers").getChildren()) {
                        String chatRoomUserId = dataSnapshot1.child("id").getValue(String.class);
                        String chatRoomUserTypingStatus = dataSnapshot1.child("typingStatus").getValue(String.class);
                        ChatRoomUser chatRoomUser = new ChatRoomUser(chatRoomUserId, chatRoomUserTypingStatus);


                        if (chatRoomUser != null && !chatRoomUser.getId().equals(userId) && chatRoomUser.getTypingStatus().equals("true")) {
                            typingCons.setVisibility(View.VISIBLE);
                            typingCons.post(new Runnable() {
                                @Override
                                public void run() {
                                    mediaPlayer.start();
                                }
                            });

                        } else {
                            typingCons.setVisibility(View.GONE);
                            mediaPlayer.pause();
                        }
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return activeChatroomMessages;
    }


    private FriendRequestAdaptor requestPendingFriendRequestAdaptor;
    private FriendRequestAdaptor requestFriendRequestAdaptor;
    private ArrayList<Contact> friendRequestList = new ArrayList<Contact>();
    private ArrayList<Contact> friendRequestPendingList = new ArrayList<Contact>();

    private void showFriendRequestsDialog() {

        myMainDialog.setContentView(R.layout.friendrequests);
        myMainDialog.show();

        ConstraintLayout allUsersLayout = myMainDialog.findViewById(R.id.allUsersLayout);
        final ConstraintLayout requestslayout = myMainDialog.findViewById(R.id.requestslayout);
        final ConstraintLayout requestspendinglayout = myMainDialog.findViewById(R.id.requestspendinglayout);
        TextView emptystatus = myMainDialog.findViewById(R.id.emptystatus);
        ConstraintLayout friendRequestCounter = myMainDialog.findViewById(R.id.friendRequestCounter);


        if (friendRequestList.size() > 0) {

            friendRequestCounter.setVisibility(View.VISIBLE);

        } else {

            friendRequestCounter.setVisibility(View.GONE);

        }


        final RecyclerView friendRequestRecycleView = myMainDialog.findViewById(R.id.friendRequestRecycleView);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        friendRequestRecycleView.setLayoutManager(horizontalLayoutManager);
        SnapHelper snapHelper = new LinearSnapHelper(); // Or PagerSnapHelper
        snapHelper.attachToRecyclerView(friendRequestRecycleView);



        requestFriendRequestAdaptor = new FriendRequestAdaptor(friendRequestList, allUsersList, new FriendRequestAdaptor.OnItemClickListener() {
            @Override
            public void onViewClick(Contact contact) {

            }

            @Override
            public void onDelete(Contact contact) {
                Toast.makeText(getApplicationContext(), "Contact Deleted :" + contact.getContactID(), Toast.LENGTH_LONG).show();

                mFirebaseDatabaseReference.child("Contacts").child(contact.getContactID()).setValue(null);
                friendRequestList.remove(contact);
                requestFriendRequestAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onAccept(Contact contact) {
                Toast.makeText(getApplicationContext(), "Accepted", Toast.LENGTH_LONG).show();
                friendRequestList.remove(contact);
                String friendID = "";
                if (contact.getSenderID().equals(userId)) {
                    friendID = contact.getReceiverID();
                } else if (contact.getReceiverID().equals(userId)) {
                    friendID = contact.getSenderID();
                }

                mFirebaseDatabaseReference.child("Contacts").child(contact.getContactID()).child("status").setValue("Connected");
                requestFriendRequestAdaptor.notifyDataSetChanged();
                messageRoomsReference = mFirebaseDatabaseReference.child("Messages Rooms").push();
                dd = mFirebaseDatabaseReference.push();

                String roomID = messageRoomsReference.getKey();


                HashMap<String, ChatRoomUser> chatroomUsers = new HashMap<String, ChatRoomUser>();
                ChatRoomUser chatRoomUser1 = new ChatRoomUser(friendID, "false");
                ChatRoomUser chatRoomUser2 = new ChatRoomUser(userId, "false");

                chatroomUsers.put(chatRoomUser1.getId(), chatRoomUser1);
                chatroomUsers.put(chatRoomUser2.getId(), chatRoomUser2);


                long currentDateTime = System.currentTimeMillis();

                ArrayList<MessageSeen> messageSeenArrayList = new ArrayList<MessageSeen>();


                Message mes1 = new Message("Connection Created. Start by Chatting with Friends", dd.getKey(), String.valueOf(currentDateTime), "auto", "auto", messageSeenArrayList);

                HashMap<String, Message> messages = new HashMap<String, Message>();
                messages.put(mes1.getMessageId(), mes1);

                ChatRoom chatRoom = new ChatRoom(roomID, String.valueOf(currentDateTime), chatroomUsers, messages);

                messageRoomsReference.setValue(chatRoom);


            }
        });

        requestPendingFriendRequestAdaptor = new FriendRequestAdaptor(friendRequestPendingList, allUsersList, new FriendRequestAdaptor.OnItemClickListener() {
            @Override
            public void onViewClick(Contact contact) {

            }

            @Override
            public void onDelete(final Contact contact) {

                Toast.makeText(getApplicationContext(), "Contact Deleted :" + contact.getContactID(), Toast.LENGTH_LONG).show();

                mFirebaseDatabaseReference.child("Contacts").child(contact.getContactID()).setValue(null);
                friendRequestPendingList.remove(contact);
                requestPendingFriendRequestAdaptor.notifyDataSetChanged();

            }

            @Override
            public void onAccept(Contact contact) {
                Toast.makeText(getApplicationContext(), "ACCEPT", Toast.LENGTH_LONG).show();
            }
        });



        requestslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendRequestRecycleView.setAdapter(requestFriendRequestAdaptor);
                requestspendinglayout.setBackgroundResource(0);
                requestslayout.setBackgroundResource(R.drawable.greenround);
            }
        });

        requestspendinglayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendRequestRecycleView.setAdapter(requestPendingFriendRequestAdaptor);
                requestslayout.setBackgroundResource(0);
                requestspendinglayout.setBackgroundResource(R.drawable.greenround);

            }
        });


        friendRequestRecycleView.setAdapter(requestFriendRequestAdaptor);
        requestspendinglayout.setBackgroundResource(0);

    }


    String messageId = "";
    private View eachchatView;
    private RecyclerView messageRecycleView;
    DatabaseReference newMessageReference;

    ArrayList<Message> messageslist = new ArrayList<Message>();
    ChatRoomUsersListViewAdaptor chatRoomUserAdaptor;
    public static boolean eachChatViewActive = false;
    public static ChatRoom activeChatRoom;
    Message typingMessage;
    ChatBoxAdaptor chatBoxAdaptor;
    EditText writemessage;
    String messageTo;
    ConstraintLayout typingCons;
    ImageView typerImage;
    ArrayList<User> friendsData;
    User chattingFriend=new User();
    InputMethodManager inputMethodManager;

    private void showeachChatView(ChatRoom chatRoom) {

        eachChatViewActive = true;
        activeChatRoom = chatRoom;
        if(chatBoxAdaptor!=null){
            activeChatroomMessages.clear();
            chatBoxAdaptor.notifyDataSetChanged();
        }

        getMyChatRoomMessages(chatRoom);
        footerCons.setVisibility(View.GONE);
        setting.setVisibility(View.INVISIBLE);



        View eachChatView= getLayoutInflater().inflate(R.layout.chatwindow,null);
        middleCons.removeAllViews();
        middleCons.addView(eachChatView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final ImageView contactImage = eachChatView.findViewById(R.id.contactImage);
        final TextView roomNameTextView = eachChatView.findViewById(R.id.roomName);
        RecyclerView chatRoomUsersListView = eachChatView.findViewById(R.id.chatRoomUsersListView);
        typingCons = eachChatView.findViewById(R.id.typingCons);
        typerImage = eachChatView.findViewById(R.id.typerImage);

        eachChatView.findViewById(R.id.videoCall).setOnClickListener(this);


        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        chatRoomUsersListView.setLayoutManager(horizontalLayoutManager);


        friendsData = new ArrayList<User>();

        for (Map.Entry<String, ChatRoomUser> entry : chatRoom.getChatRoomUsers().entrySet()) {
            String key = entry.getKey();
            ChatRoomUser chatRoomUser = entry.getValue();
            if (!chatRoomUser.getId().equals(userId)) {
                for (User user : myContactsUsersList) {
                    if (chatRoomUser.getId().equals(user.getUserID())) {
                        friendsData.add(user);

                    }
                }
            }

        }

        chattingFriend=friendsData.get(0);
        Picasso.get().load(friendsData.get(0).getPhotoUri()).fit().into(typerImage);


        chatRoomUserAdaptor = new ChatRoomUsersListViewAdaptor(friendsData);
        chatRoomUsersListView.setAdapter(chatRoomUserAdaptor);

        ImageButton backbutton = eachChatView.findViewById(R.id.backButton);


        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeChatRoom=null;
                eachChatViewActive=false;


                footerCons.setVisibility(View.VISIBLE);
                setting.setVisibility(View.VISIBLE);
                if(lastFooterPosition==1){
                    showMyMessages();
                }
                else if(lastFooterPosition==0){
                    showMyContacts(null);
                }
                else{
                    middleCons.removeAllViews();
                }

            }
        });

        messageRecycleView = eachChatView.findViewById(R.id.messageRecycleView);
        messageRecycleView.setOnClickListener(this);
        writemessage = eachChatView.findViewById(R.id.writemessage);


        ImageButton send = eachChatView.findViewById(R.id.send);

        LinearLayoutManager messageRecycleViewhorizontalLayoutManager
                = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, true);
        messageRecycleView.setLayoutManager(messageRecycleViewhorizontalLayoutManager);


        chatBoxAdaptor = new ChatBoxAdaptor(activeChatroomMessages, new ChatBoxAdaptor.OnItemClickListener() {
            @Override
            public void deleteMessage(Message message,int position) {

                Toast.makeText(getApplicationContext(), "Message Deleted", Toast.LENGTH_LONG).show();
                activeChatroomMessages.remove(message);
                chatBoxAdaptor.notifyItemChanged(position);
                mFirebaseDatabaseReference.child("Messages Rooms").child(chatRoom.getChatRoomID()).child("messages").child(message.getMessageId()).removeValue();

            }

        });

        messageRecycleView.setAdapter(chatBoxAdaptor);

        writemessage.setFocusable(true);


        writemessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("edittext focus", "" + hasFocus);
                if (hasFocus) {

                    newMessageReference = mFirebaseDatabaseReference.child("Messages Rooms").child(chatRoom.getChatRoomID()).child("messages").push();
                    messageId = newMessageReference.getKey();

                } else {

                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);


                }
            }
        });


        writemessage.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                Log.d("text", "" + s);
                ArrayList<ChatRoomUser> chatRoomUsers = new ArrayList<ChatRoomUser>();

                if (s.length() > 0) {

                    mFirebaseDatabaseReference.child("Messages Rooms").child(chatRoom.getChatRoomID()).child("chatRoomUsers").child(userId).child("typingStatus").setValue("true");

                }
                if (s.length() == 0) {

                    mFirebaseDatabaseReference.child("Messages Rooms").child(chatRoom.getChatRoomID()).child("chatRoomUsers").child(userId).child("typingStatus").setValue("false");

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writemessage.getText() != null && writemessage.getText().length() > 0) {

                    ArrayList<MessageSeen> messageSeenArrayList = new ArrayList<MessageSeen>();

                    long currentDateTime1 = System.currentTimeMillis();
                    Message newMessage = new Message(writemessage.getText().toString(), messageId, String.valueOf(currentDateTime1), userId, "textmessage", messageSeenArrayList);

                    chatRoom.setActiveTime(String.valueOf(currentDateTime1));
                    chatRoom.getMessages().put(newMessage.getMessageId(), newMessage);
                    mFirebaseDatabaseReference.child("Messages Rooms").child(chatRoom.getChatRoomID()).child("activeTime").setValue(String.valueOf(currentDateTime1));
                    mFirebaseDatabaseReference.child("Messages Rooms").child(chatRoom.getChatRoomID()).child("messages").child(newMessage.getMessageId()).setValue(newMessage);

                    writemessage.setText("");

                    newMessageReference = mFirebaseDatabaseReference.child("Messages Rooms").child(chatRoom.getChatRoomID()).child("messages").push();
                    messageId = newMessageReference.getKey();

                }

            }
        });

    }


    private void startCallingFriend(User user,String callType){

        String chatRoomId="nn";
        for(ChatRoom chatRoom:chatRoomList){
            if(chatRoom.getChatRoomUsers().containsKey(user.getUserID()) && chatRoom.getChatRoomUsers().containsKey(userId)){
                chatRoomId=chatRoom.getChatRoomID();
            }
        }
        getIntent().putExtra("chatRoomId", chatRoomId);
        getIntent().putExtra("callType", callType);
        getIntent().putExtra("callMode", "outgoing");
        mainCons.setVisibility(View.GONE);
        overLappingCons.removeAllViews();
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();

        Fragment myCallFragment = new CallViewFragment();
        ((CallViewFragment) myCallFragment).setUser(user);
        fragTransaction.add(overLappingCons.getId(), myCallFragment , "callViewFragment");
        fragTransaction.commit();



    }


    String callMode="offline"; // incoming or outgoing
    private void showIncomingCall(User user,String callType){

        overLappingCons.removeAllViews();

        getIntent().putExtra("callType", callType);
        getIntent().putExtra("callMode", "incoming");

        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();

        Fragment myCallFragment = new CallViewFragment();
        ((CallViewFragment) myCallFragment).setUser(user);
        fragTransaction.add(overLappingCons.getId(), myCallFragment , "callViewFragment");
        fragTransaction.commit();




    }


    ValueEventListener myIncomingCallValueEventListener;
    private void checkForMyPhoneCalls(){

        myIncomingCallValueEventListener=mFirebaseDatabaseReference.child("Calls").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    Call call =dataSnapshot.getValue(Call.class);

                        if(call.getCallStatus().equals("calling") && !call.getSessionId().equals("empty")){
                            mainCons.setVisibility(View.GONE);
                            for(User user:allUsersList){
                                if(user.getUserID().equals(call.getCallerId())){
                                    chattingFriend=user;
                                    showIncomingCall(chattingFriend,call.getCallType());
                                }
                            }

                        }



                }else{
                    overLappingCons.removeAllViews();
                    mainCons.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }





    int unseenmessageCounter;


    private ArrayList<Contact> connectedContacts = new ArrayList<Contact>();

    ValueEventListener myContactsListener;
    DatabaseReference contactsReference;
    ArrayList<String> myAllContacts=new ArrayList<>();

    private void getMyContacts() {
        contactsReference = mFirebaseDatabaseReference.child("Contacts");

        myContactsListener = contactsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myAllContacts.clear();
                connectedContacts.clear();
                friendRequestList.clear();
                friendRequestPendingList.clear();
                myContactsUsersList.clear();
                if (dataSnapshot.exists()) {

                    if (requestFriendRequestAdaptor != null && requestPendingFriendRequestAdaptor != null) {
                        requestFriendRequestAdaptor.notifyDataSetChanged();
                        requestPendingFriendRequestAdaptor.notifyDataSetChanged();
                    }

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String contactID = dataSnapshot1.child("contactID").getValue(String.class);
                        String senderID = dataSnapshot1.child("senderID").getValue(String.class);
                        String receiverID = dataSnapshot1.child("receiverID").getValue(String.class);
                        String status = dataSnapshot1.child("status").getValue(String.class);
                        Contact contact = new Contact(contactID, senderID, receiverID, status);

                        if ((status != null && senderID != null && receiverID != null) && (senderID.equals(userId) || receiverID.equals(userId))) {

                            if (status.equals("Pending") && senderID.equals(userId)) {
                                friendRequestPendingList.add(contact);
                                myAllContacts.add(receiverID);
                                if (requestPendingFriendRequestAdaptor != null) {
                                    requestPendingFriendRequestAdaptor.notifyDataSetChanged();
                                }
                            } else if (status.equals("Connected")) {
                                connectedContacts.add(contact);

                                    String friendID="";
                                    if (contact.getSenderID().equals(userId)) {
                                        friendID = contact.getReceiverID();
                                    } else if (contact.getReceiverID().equals(userId)) {
                                        friendID = contact.getSenderID();
                                    }
                                    myAllContacts.add(friendID);
                                    User friend = new User();
                                    for (User searchUser : allUsersList) {
                                        if (searchUser.getUserID().equals(friendID)) {
                                            friend = searchUser;
                                            myContactsUsersList.add(friend);
                                            if (myContactsAdaptor != null) {
                                                myContactsAdaptor.notifyDataSetChanged();
                                            }
                                        }
                                    }


                                if (friendRequestAdaptor != null) {
                                    friendRequestAdaptor.notifyDataSetChanged();
                                }
                            }
                            if (status.equals("Pending") && receiverID.equals(userId)) {
                                friendRequestList.add(contact);
                                myAllContacts.add(senderID);
                                if (requestFriendRequestAdaptor != null) {
                                    requestFriendRequestAdaptor.notifyDataSetChanged();
                                }
                            }


                        }


                    }


                } else {
                    if (requestPendingFriendRequestAdaptor != null) {
                        requestPendingFriendRequestAdaptor.notifyDataSetChanged();
                    }
                    if (requestFriendRequestAdaptor != null) {
                        requestFriendRequestAdaptor.notifyDataSetChanged();
                    }
                }
                if (friendRequestList.size() > 0) {

                    newFriendRequestNotifier.setVisibility(View.VISIBLE);

                }else{
                    newFriendRequestNotifier.setVisibility(View.GONE);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    int PICK_PROFILE_IMAGE_REQUEST = 111;

    ImageView profileImage;


    boolean createNewMeetPoint=false;
    private void showIssueTrackerView(){
        View issueTrackerView=getLayoutInflater().inflate(R.layout.issuetracker,null);
        TextView header=issueTrackerView.findViewById(R.id.header);
        EditText issueTitle=issueTrackerView.findViewById(R.id.issueTitle);
        EditText issueDescription=issueTrackerView.findViewById(R.id.issueDescription);
        TextView sendIssueReport=issueTrackerView.findViewById(R.id.sendReport);

        sendIssueReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String issueTitleString=issueTitle.getText().toString();
                String issueDescriptionString=issueDescription.getText().toString();
                String buttonString=sendIssueReport.getText().toString();
                if(issueTitleString.length()>0 && issueDescriptionString.length()>0 && !buttonString.equals("Done")){
                    Issue issue=new Issue(issueTitle.getText().toString(),issueDescription.getText().toString());
                    mFirebaseDatabaseReference.child("Issues").push().setValue(issue);
                    header.setText("Thank You for your Report.");
                    sendIssueReport.setVisibility(View.GONE);
                    issueTitle.setVisibility(View.GONE);
                    issueDescription.setVisibility(View.GONE);
                   // middleCons.removeAllViews();
                }

                if(buttonString.equals("Done")){
                    showIssueTrackerView();

                }

            }
        });
        middleCons.removeAllViews();
        middleCons.addView(issueTrackerView, ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT);

        createNewMeetPoint=true;
    }

    private void showProfileViewDialog() {

        myMainDialog.setContentView(R.layout.profileview);
        myMainDialog.show();


        profileImage = myMainDialog.findViewById(R.id.userImageView);
        ImageView userBarCode = myMainDialog.findViewById(R.id.userBarCode);
        final TextView userNameTextView = myMainDialog.findViewById(R.id.username);


        String text = "User:" + userId;

        userNameTextView.setText(myDetails.getUserName());
        if (myDetails.getPhotoUri() != null) {
            Picasso.get().load(myDetails.getPhotoUri()).fit().centerCrop().into(profileImage);

        }

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            userBarCode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        ImageButton addUserImage = myMainDialog.findViewById(R.id.addUserImage);
        userImageViewCons = myMainDialog.findViewById(R.id.businessImageViewCons);
        progressBar = myMainDialog.findViewById(R.id.progressBar);
        addUserImage.setOnClickListener(this);


        Button signOut = myMainDialog.findViewById(R.id.signOut);
        signOut.setOnClickListener(this);


    }

    private void getMyData() {
        getMyDataListener = myDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    String name = dataSnapshot.child("userName").getValue(String.class);
                    String email = dataSnapshot.child("userEmail").getValue(String.class);
                    String photoUri = dataSnapshot.child("photoUri").getValue(String.class);
                    String accountMode = (dataSnapshot.child("accountMode").getValue(String.class));


                    String activeStatus = dataSnapshot.child("userActive").child("activeStatus").getValue(String.class);
                    String activeStatusShareSetting = dataSnapshot.child("userActive").child("activeStatusShareSetting").getValue(String.class);
                    String timestamp = dataSnapshot.child("userActive").child("timestamp").getValue(String.class);

                    UserActive userActive = new UserActive(activeStatus, activeStatusShareSetting, timestamp);

                    String locationTrackingStatus = dataSnapshot.child("locationTracking").child("locationTrackingStatus").getValue(String.class);

                    String latitude = dataSnapshot.child("locationTracking").child("userLatLng").child("latitude").getValue(String.class);
                    String longitude = dataSnapshot.child("locationTracking").child("userLatLng").child("longitude").getValue(String.class);

                    UserLatLng mylocation = new UserLatLng(latitude, longitude);
                    LocationTracking locationTracking = new LocationTracking(locationTrackingStatus, mylocation);


                    // location tracking SETTING  true / false
                    if (locationTracking.getLocationTrackingStatus().equals("true")) {
                        mylocationTracking = true;
                    }

                    // activeStatusShareSetting time tracking SETTING  String value true / false
                    activeStatusShareSettingValue = userActive.getActiveStatusShareSetting();

                        myDetails = new User(userId, name, email, photoUri, accountMode, userActive, locationTracking);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void requestForImageUpload() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_PROFILE_IMAGE_REQUEST);
    }

    private void signOutUser() {
        myMainDialog.dismiss();
        Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        mAuth.signOut();

    }


    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(getApplicationContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private Location location;
    Marker usermarker;
    ConstraintLayout messageCall;
    Marker newbusinessMarker;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            mMap = googleMap;
            mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom - 0.5f));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(false);

            if (locationManager != null) {
                isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (isGpsEnabled) {


                } else {
                    showTurnOnGpsDialog();
                }
            }

        }


        mMap.setOnMarkerClickListener(marker -> {

            if (userId.equals(marker.getTag())) {
                showProfileViewDialog();
            }
            if (!userId.equals(marker.getTag())) {


                for (User searchUser : myContactsUsersList) {
                    if (searchUser.getUserID().equals(marker.getTag())) {

                        //showMyContacts(searchUser);
                        footerTabsLayoutManager.smoothScrollToPosition(footerTabRecyclerView, null, 0);


                    }
                }

            }

            return false;
        });

        mMap.setOnMapLongClickListener(latLng -> mMap.setOnMapLongClickListener(null));


        mMap.setOnMapClickListener(latLng -> {


            if (usermarker != null) {
                usermarker.showInfoWindow();
            }


        });

    }


    private void changeMapCamera(LatLng latLng, int zoomLevel) {
        if (latLng != null) {
            cameraPosition = new CameraPosition.Builder()
                    .target(latLng)      // Sets the center of the map to Mountain View
                    .zoom(zoomLevel)                   // Sets the zoom
                    .bearing(mCurrentLocation.getBearing())                // Sets the orientation of the camera to east
                    .tilt(55)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(@NonNull int requestCode, @NonNull
            String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                        if (locationManager != null) {
                            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                            if (isGpsEnabled) {
                                mMap.setMyLocationEnabled(true);

                            } else {
                                showTurnOnGpsDialog();
                            }
                        }


                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "permission denied", Toast.LENGTH_LONG).show();
                }

            }
            break;
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        mScannerView.setResultHandler(MapsActivity.this);
                        mScannerView.startCamera();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (shouldShowRequestPermissionRationale(CAMERA)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    (dialog, which) -> requestPermissions(new String[]{CAMERA},
                                            REQUEST_CAMERA));
                            return;
                        }
                    }
                }
                break;



        }


    }



    private static boolean firstConnect = false;
    boolean isGpsEnabled = false;
    public BroadcastReceiver gpsSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                // Make an action or refresh an already managed state.

                if (locationManager != null) {

                    isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    //firstConnect=isGpsEnabled;

                    if (isGpsEnabled) {

                        if (firstConnect) {

                            myMainDialog.dismiss();
                            myMainDialog.setCancelable(true);
                            myMainDialog.setCanceledOnTouchOutside(true);
                            Toast.makeText(getApplicationContext(), "Gps is ON",
                                    Toast.LENGTH_SHORT).show();
                            firstConnect = false;


                        }

                    } else {
                        // Handle Location turned OFF
                        //disable
                        if (!firstConnect) {
                            showTurnOnGpsDialog();
                            Toast.makeText(getApplicationContext(), "Gps is OFF",
                                    Toast.LENGTH_SHORT).show();
                            firstConnect = true;

                        }


                    }
                }


            }


        }

    };

    View turnongps;

    public void showTurnOnGpsDialog() {

        turnongps = getLayoutInflater().inflate(R.layout.turnongps, null);
        Button accept = turnongps.findViewById(R.id.accept);

        accept.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });

        myMainDialog.setCancelable(false);
        myMainDialog.setCanceledOnTouchOutside(false);
        myMainDialog.setContentView(turnongps);
        myMainDialog.show();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        getIntentData();
    }


    private class MyRunnable implements Runnable {
        final WeakReference<TextView> tvText;
        final String activeTime;
        MyRunnable(TextView tvText, String activeTime) {
            this.tvText = new WeakReference<>(tvText);
            this.activeTime = activeTime;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            //Save the TextView to a local variable because the weak referenced object could become empty at any time
            TextView mText = tvText.get();
            if (mText != null) {

            }
        }
    }


    // previousSnapView;
    private int lastFooterPosition =2;
    private View scrollabletabs;
    private RecyclerView footerTabRecyclerView;
    private LinearLayoutManager footerTabsLayoutManager;
    private void showFooterTabs() {

        //RecyclerView for tabs
       scrollabletabs = getLayoutInflater().inflate(R.layout.scrollabletabs,null);
        footerTabRecyclerView=scrollabletabs.findViewById(R.id.tabsRecyclerView);
        TextView tabTitle=scrollabletabs.findViewById(R.id.myMessageViewHeader);
        footerCons.addView(scrollabletabs);
        markMyLocationCons=scrollabletabs.findViewById(R.id.markMyLocationCons);
        markMyLocationCons.setOnClickListener(this);
        Resources res = getResources();

        Drawable contactsDrawable = ResourcesCompat.getDrawable(res, R.drawable.contacts, null);
        TabModel tabModel1=new TabModel("My Contacts",contactsDrawable,0);

        Drawable myMessagesDrawable = ResourcesCompat.getDrawable(res, R.drawable.message, null);
        TabModel tabModel2=new TabModel("Recent Messages",myMessagesDrawable,unseenmessageCounter);

        Drawable profileDrawable = ResourcesCompat.getDrawable(res, R.drawable.profileavatar, null);
        TabModel tabModel3=new TabModel("My Profile",profileDrawable,0);



        Drawable barcodeDrawable = ResourcesCompat.getDrawable(res, R.drawable.qr, null);
        TabModel tabModel4=new TabModel("Scan QR Code",barcodeDrawable,0);

        Drawable issueTrackerDrawable = ResourcesCompat.getDrawable(res, R.drawable.issue, null);
        TabModel tabModel5=new TabModel("Issue Tracker",issueTrackerDrawable,0);

        footerTabModels.add(tabModel1);
        footerTabModels.add(tabModel2);
        footerTabModels.add(tabModel3);
        footerTabModels.add(tabModel4);
        footerTabModels.add(tabModel5);

        footerTabsLayoutManager
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        footerTabRecyclerView.setLayoutManager(footerTabsLayoutManager);
        footerTabsAdaptor =new FooterTabsAdaptor(footerTabModels, new FooterTabsAdaptor.OnItemClickListener() {
            @Override
            public void onViewClick(TabModel tabModel) {
                if(tabModel.getTitle().equals("My Profile")){

                    showProfileViewDialog();

                }

                footerTabsLayoutManager.smoothScrollToPosition(footerTabRecyclerView, null, footerTabModels.indexOf(tabModel));
            }
        });
        footerTabRecyclerView.setAdapter(footerTabsAdaptor);
        SnapHelper snapHelper=new LinearSnapHelper();
        snapHelper.attachToRecyclerView(footerTabRecyclerView);


        footerTabRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {



            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View view = snapHelper.findSnapView(footerTabsLayoutManager);
                int position=footerTabsLayoutManager.getPosition(view);

                if(newState==SCROLL_STATE_IDLE && lastFooterPosition==position){


                    switch (position){
                        case 0:
                            showMyContacts(null);
                            searchCons.setVisibility(View.GONE);
                            break;

                        case 1:
                            showMyMessages();
                            topCons.setVisibility(View.INVISIBLE);
                            break;

                        case 2:
                            topCons.setVisibility(View.VISIBLE);
                            middleCons.removeAllViews();
                            if(usermarker!=null){
                                changeMapCamera(usermarker.getPosition(),13);
                            }
                            searchCons.setVisibility(View.VISIBLE);

                            break;

                        case 3:
                            topCons.setVisibility(View.INVISIBLE);
                            middleCons.removeAllViews();
                            showCameraScanner();
                            break;

                        case 4:
                            topCons.setVisibility(View.INVISIBLE);
                            showIssueTrackerView();
                            searchCons.setVisibility(View.GONE);
                            break;



                    }

                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View snapView= snapHelper.findSnapView(footerTabsLayoutManager);
                int position=footerTabsLayoutManager.getPosition(snapView);
                tabTitle.post(new Runnable() {
                    @Override
                    public void run() {
                        tabTitle.setText(footerTabModels.get(position).getTitle());
                    }
                });



                if(position!= lastFooterPosition){

                    if(!mediaPlayer.isPlaying()){
                        mediaPlayer.start();
                    }
                    lastFooterPosition =position;

                }

                for(TabModel tabModel: footerTabModels){

                    int tabsIndex= footerTabModels.indexOf(tabModel);
                    View view = footerTabsLayoutManager.getChildAt(tabsIndex);


                    if(snapView!=null && view!=null){
                        if(snapView==view){

                            setRecyclerViewItemDecoration(view,R.drawable.whiteround,position,70,70,1.0f);

                        }
                        else if(tabsIndex==position-1 || tabsIndex==position+1){
                            setRecyclerViewItemDecoration(view,R.drawable.greenround,position,45,45,0.5f);

                        }
                        else{

                            setRecyclerViewItemDecoration(view,R.drawable.greenround,position,30,30,0.2f);

                        }
                        if(tabModel.getNotificationCounter()>0 && snapView!=view){
                            setRecyclerViewItemDecoration(view,R.drawable.greenround,position,50,50,1.0f);
                        }




                    }

                }



            }
        });



        footerTabRecyclerView.scrollToPosition(lastFooterPosition);

    }


    private void setRecyclerViewItemDecoration(View itemView,int drawable,int position,int width,int height,float alpha){
        itemView.post(new Runnable() {
            @Override public void run() {
                itemView.requestLayout();
                Resources res = getResources();
                Drawable drawable1 = ResourcesCompat.getDrawable(res, drawable, null);
                itemView.setBackground(drawable1);
                ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
                layoutParams.width = dpToPx(width);
                layoutParams.height = dpToPx(height);
                itemView.setAlpha(alpha);
                itemView.setLayoutParams(layoutParams);
            }});
    }



    public static boolean mapActivityOn = false;

    public void userAccountPublic(String mode) {
        mFirebaseDatabaseReference.child("Users").child(userId).child("accountMode").setValue(mode);

    }

    public void userActiveStatus(String activeStatusShare) {

        long currentDateTime = System.currentTimeMillis();
        if (activeStatusShare.equals("true")) {
            UserActive userActive = new UserActive("true", "true", String.valueOf(currentDateTime));
            mFirebaseDatabaseReference.child("Users").child(userId).child("userActive").setValue(userActive);
        } else {
            UserActive userActive = new UserActive("true", "false", String.valueOf(currentDateTime));
            mFirebaseDatabaseReference.child("Users").child(userId).child("userActive").setValue(userActive);
        }
    }



    Location mCurrentLocation;
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                if (mCurrentLocation != null && actvityRunning) {

                    myLatlng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    MapsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           if(usermarker!=null){
                               usermarker.setPosition(myLatlng);
                           }
                        }
                    });

                    if(mylocationTracking){

                        UserLatLng userLatLng=new UserLatLng(String.valueOf(mCurrentLocation.getLatitude()),String.valueOf(mCurrentLocation.getLongitude()));
                        mFirebaseDatabaseReference.child("Users").child(userId).child("locationTracking").child("userLatLng").setValue(userLatLng);
                    }
                }
            }
        };
    }

    int i = 0;
    boolean startLocationUpdateStatus = true;
    LocationRequest locationRequest;
    public void startLocationUpdates() {

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        fusedLocationClient.requestLocationUpdates(locationRequest,mLocationCallback, handlerThread.getLooper()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("startLocationUpdates start", "" + i);
            }
        });


        startLocationUpdateStatus = false;

    }



    // QR code scanner Handling function
    @Override
    public void handleResult(Result result) {
        final String data = result.getText();

        if (data.contains("User:")) {
            myMainDialog.dismiss();
            String consumerID = data.substring(5);
            Toast.makeText(getApplicationContext(), "User ID: " + consumerID,
                    Toast.LENGTH_SHORT).show();
            if (consumerID.equals(userId)) {
                Toast.makeText(getApplicationContext(), "Cannot add Yourself.",
                        Toast.LENGTH_SHORT).show();
            } else {
                for (User user : allUsersList) {

                    if (user.getUserID().equals(consumerID)) {
                        showaddcontact(user);
                    }
                }
            }


        } else {
            myMainDialog.dismiss();
            Toast.makeText(getApplicationContext(), "QR code not in this application.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void showaddcontact(User user) {


        myMainDialog.setContentView(R.layout.addcontactprofile);
        myMainDialog.show();

        final Button add = myMainDialog.findViewById(R.id.addFriend);
        final ImageView contactImage = myMainDialog.findViewById(R.id.contactImage);
        final TextView userNameTextView = myMainDialog.findViewById(R.id.userName);
        ImageButton chatMessage = myMainDialog.findViewById(R.id.chatMessage);


        if (user.getPhotoUri() != null) {
            Picasso.get().load(user.getPhotoUri()).fit().centerCrop().into(contactImage);
        }
        userNameTextView.setText(user.getUserName());

        for (Contact contact : friendRequestList) {
            if (contact.getReceiverID().equals(userId) && contact.getSenderID().equals(user.getUserID()) || contact.getReceiverID().equals(user.getUserID()) && contact.getSenderID().equals(userId)) {
                add.setText(contact.getStatus());
            }
        }
        for (Contact contact : friendRequestPendingList) {
            if (contact.getReceiverID().equals(userId) && contact.getSenderID().equals(user.getUserID()) || contact.getReceiverID().equals(user.getUserID()) && contact.getSenderID().equals(userId)) {
                add.setText(contact.getStatus());
            }
        }
        for (Contact contact : connectedContacts) {
            if (contact.getReceiverID().equals(userId) && contact.getSenderID().equals(user.getUserID()) || contact.getReceiverID().equals(user.getUserID()) && contact.getSenderID().equals(userId)) {
                add.setText(contact.getStatus());
                chatMessage.setVisibility(View.VISIBLE);
            }
        }

        add.setOnClickListener(v -> {

            if (add.getText().equals("Add Contact")) {
                myMainDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Request Sent to: " + userNameTextView.getText().toString(),
                        Toast.LENGTH_SHORT).show();

                String contactID = mFirebaseDatabaseReference.child("Contacts").push().getKey();
                Contact contact = new Contact(contactID, userId, user.getUserID(), "Pending");

                if (contactID != null) {
                    mFirebaseDatabaseReference.child("Contacts").child(contactID).setValue(contact);

                }

            }

        });


        chatMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (ChatRoom chatRoom : chatRoomList) {

                    if (chatRoom.getChatRoomUsers().containsKey(user.getUserID()) && chatRoom.getChatRoomUsers().containsKey(userId)) {
                        showMyMessages();
                        showeachChatView(chatRoom);
                    }
                }

            }
        });


    }


    public void getIntentData() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            String chatroomID = getIntent().getStringExtra("chatroomID");


            for (ChatRoom chatRoom : chatRoomList) {
                if (chatRoom.getChatRoomID().equals(chatroomID)) {

                    if (myMainDialog != null) {
                        myMainDialog.dismiss();

                    }
                    showeachChatView(chatRoom);

                }
            }
            if(chatRoomList.size()==0){

                Log.e("chatroom empty","true");
                showMyMessages();
                getMyChatRooms();
            }

            getIntent().replaceExtras(new Bundle());
        }
    }


    DatabaseReference dd;
    StorageReference filePath;

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PROFILE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri localStorageuri = data.getData();

            userImageViewCons.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            filePath = storageRef.child("Users").child(userId).child("image.jpg");

            profileImage.setImageURI(localStorageuri);

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, bao); // bmp is bitmap from user image file
            bitmap.recycle();
            byte[] byteArray = bao.toByteArray();


            UploadTask uploadTask = filePath.putBytes(byteArray);
            uploadTask.addOnFailureListener(exception -> {
                // Handle unsuccessful uploads
            }).addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                userImageViewCons.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                Picasso.get().load(uri).fit().centerCrop().into(profileImage);
                mFirebaseDatabaseReference.child("Users").child(userId).child("photoUri").setValue(uri.toString());

            })).addOnProgressListener(taskSnapshot -> {
                double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
            });

        }



    }


    public void createMyMarker() {

       View userMarkerView = getLayoutInflater().inflate(R.layout.imageviewmarker, null);
        ImageView image = userMarkerView.findViewById(R.id.image);
        ConstraintLayout markerbg = userMarkerView.findViewById(R.id.markerbg);
       //markerbg.setBackground(getResources().getDrawable(R.drawable.usermarker));


        if (mMap != null) {

            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(myDetails.getPhotoUri())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            image.setImageBitmap(resource);
                            Bitmap bmp = createBitmapFromView(userMarkerView);

                            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bmp, 180, 180, false));

                            if (usermarker == null && myLatlng != null && mMap!=null) {

                                usermarker = mMap.addMarker(new MarkerOptions()
                                        .position(myLatlng).title("You")
                                        .visible(true).icon(icon).draggable(true));
                                usermarker.setTag(userId);

                               changeMapCamera(usermarker.getPosition(),13);


                            }
                            bmp.recycle();
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });



        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userImageView:
                showProfileViewDialog();
                break;


            case R.id.signOut:
                signOutUser();
                break;


            case R.id.addUserImage:
                requestForImageUpload();
                break;


            case R.id.setting:
                showSettingDialog();
                break;

            case R.id.searchCons:
                showQuickAddDialog();
                break;

            case R.id.friendRequestButton:
                showFriendRequestsDialog();
                break;


            case R.id.singleTextView:
                polyline.remove();
                mainCons.setVisibility(View.VISIBLE);
                findViewById(R.id.navigationCons).setVisibility(View.GONE);
                break;

            case R.id.markMyLocationCons:
                if(usermarker!=null){
                    changeMapCamera(usermarker.getPosition(),13);
                }
                break;

            case R.id.messageviewLayout:
                //middleCons.removeAllViews();
                break;

            case R.id.videoCall:
                startCallingFriend(chattingFriend,"video call");
                break;

                case R.id.messageRecycleView:
                    Log.e("Chat Room Deleted","True");
                    chatBoxAdaptor.notifyDataSetChanged();
                break;


        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("saveInstances", outState.toString());
    }


    HandlerThread handlerThread;
    Handler myLocationHandler;

    @Override
    public void onResume() {

        Log.d("activitymethod", "onResume");



        mapActivityOn = true;
        myLocationHandler=new Handler(handlerThread.getLooper());

        if (startLocationUpdateStatus) {

            myLocationHandler.post(new Runnable() {
                @Override
                public void run() {
                  createLocationCallback();
                  startLocationUpdates();
                }
            });


        }
        actvityRunning=true;
        super.onResume();

    }



    @Override
    public void onStart() {
        Log.d("activitymethod", "onStart");

        super.onStart();


    }




    boolean actvityRunning=false;
    @Override
    public void onPause() {


        myLocationHandler.removeCallbacks(null);
        mapActivityOn = false;
        eachChatViewActive = false;
        messageDialogActive = false;
        Log.d("activitymethod", "onPause");
        mainHandler.removeCallbacksAndMessages(null);

        if (fusedLocationClient != null && mLocationCallback != null) {
            final Task<Void> voidTask = fusedLocationClient.removeLocationUpdates(mLocationCallback);

            voidTask.addOnCompleteListener(task ->
                    Log.e("onPause", "addOnCompleteListener: " + task.isComplete())
            ).addOnSuccessListener(aVoid -> {
                Log.e("onPause", "addOnSuccessListener: ");

                if (fusedLocationClient != null) {
                    locationRequest=null;
                    fusedLocationClient = null;
                }

                if (mLocationCallback != null) {
                    mLocationCallback=null;
                }


            }).addOnFailureListener(e -> Log.e("onPause", "addOnFailureListener: " + e.getMessage()));

        }
        startLocationUpdateStatus = true;

        long currentDateTime = System.currentTimeMillis();
        if (myDetails != null && myDetails.getUserActive() != null) {
            activeStatusShareSettingValue = myDetails.getUserActive().getActiveStatusShareSetting();
            UserActive userActive = new UserActive("false", activeStatusShareSettingValue, String.valueOf(currentDateTime));
            mFirebaseDatabaseReference.child("Users").child(userId).child("userActive").setValue(userActive);

        }


        super.onPause();

    }

    @Override
    public void onStop() {

        Log.d("activitymethod", "onStop");

        actvityRunning=false;

        if (mScannerView != null) {
            mScannerView.setResultHandler(null);
        }

        super.onStop();

    }


    @Override
    public void onDestroy() {


        handlerThread.quit();

        if(myIncomingCallValueEventListener!=null){
            mFirebaseDatabaseReference.child("Calls").child(userId).removeEventListener(myIncomingCallValueEventListener);
        }
        if (myContactsListener != null) {
            contactsReference.removeEventListener(myContactsListener);
        }
        if (getMyDataListener != null) {
            myDataReference.removeEventListener(getMyDataListener);
        }
        if (getAllUserListener != null) {
            allUsersReference.removeEventListener(getAllUserListener);
        }
        if (getMyChatRoomListener != null) {
            myChatRoomReference.removeEventListener(getMyChatRoomListener);
        }
        if (myMessagesListener != null) {
            myMessagesReference.removeEventListener(myMessagesListener);
        }

        if (gpsSwitchStateReceiver != null) {

            getApplicationContext().unregisterReceiver(gpsSwitchStateReceiver);
            locationManager=null;
        }

        if (inputMethodManager != null) {
            inputMethodManager = null;
        }


        if (mMap != null) {
            usermarker=null;
            friendMarkerHashMap.clear();
            mMap.clear();
            allUsersList.clear();
            chatRoomList.clear();
            activeChatroomMessages.clear();
            mMap.setMyLocationEnabled(false);
            mMap=null;
            Log.d("fragments", ""+getSupportFragmentManager().getFragments().toString());

        }


        super.onDestroy();

    }

    @Override
    public void onLowMemory() {
        mapFragment.onLowMemory();
        Log.e("Low Memory", "app is using lots of memory" );

        super.onLowMemory();
    }

    @Override
    public void onBackPressed() {

        //finishAffinity();
        Log.d("Activity Destroyed", "" + isFinishing());

    }

}



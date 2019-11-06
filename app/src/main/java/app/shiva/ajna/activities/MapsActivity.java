package app.shiva.ajna.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import app.shiva.ajna.adapter.ChatBoxListViewAdapter;
import app.shiva.ajna.adapter.ChatRoomUsersListViewAdaptor;
import app.shiva.ajna.adapter.ContactAdapter;
import app.shiva.ajna.adapter.MessagesListviewAdaptor;
import app.shiva.ajna.R;

import static android.Manifest.permission.CAMERA;

import app.shiva.ajna.model.Call;
import app.shiva.ajna.model.ChatRoom;
import app.shiva.ajna.model.Message;
import app.shiva.ajna.model.Contact;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        LocationListener, ZXingScannerView.ResultHandler {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 101;
    private static final int REQUEST_CHECK_SETTINGS=1001;
    private static final String TAG = "tt";
    private FirebaseAuth mAuth;
    //Map Variables :
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private CameraPosition cameraPosition;
    private ContactAdapter contactAdapter;

    private ImageView profileImage;
    private ConstraintLayout userImageViewCons;
    private ProgressBar progressBar;
    private ConstraintLayout contactsLayout;


    private static final int REQUEST_CAMERA = 1;
    Dialog scanqr;
    private ZXingScannerView mScannerView;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    //Firebase classes

    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    ConstraintLayout overlappingConstraint;
    LatLng UserLatlng=new LatLng(0,0);
    ArrayList<Marker> friendMarkerList=new ArrayList<>();
    boolean locationSetting=false;
    LocationManager locationManager;
    TextView contactscounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_maps);
        mAuth = FirebaseAuth.getInstance();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        getIntentData();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Obtain the SupportMapFragment and get nified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        messageCall=findViewById(R.id.messageCall);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {

                        mLastLocation = location;
                        UserLatlng=new LatLng(location.getLatitude(),location.getLongitude());

                        mFirebaseDatabaseReference.child("Users").child("Consumers").child(userId).child("photoUri").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    View userMarkerView=getLayoutInflater().inflate(R.layout.imageviewmarker,null);
                                    ImageView userImage=userMarkerView.findViewById(R.id.friendImage);
                                    ConstraintLayout markerbg=userMarkerView.findViewById(R.id.markerbg);
                                    String photoUri=dataSnapshot.getValue(String.class);
                                    markerbg.setBackground(getDrawable(R.drawable.usermarker));

                                    Picasso.get()
                                            .load(photoUri)
                                            .into(userImage, new com.squareup.picasso.Callback() {
                                                @Override
                                                public void onSuccess() {

                                                    Bitmap bmp =  createBitmapFromView(MapsActivity.this,userMarkerView);

                                                    BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bmp, 180, 180, false));
                                                    if(usermarker!=null){
                                                        usermarker.setPosition(UserLatlng);
                                                    }
                                                    else{
                                                        usermarker = mMap.addMarker(new MarkerOptions()
                                                                .position(UserLatlng).title("You")
                                                                .visible(true).icon(icon));
                                                        usermarker.setTag(userId);
                                                    }

                                                }

                                                @Override
                                                public void onError(Exception e) {

                                                }
                                            });

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                }
                mFirebaseDatabaseReference.child("Location Updates").child(userId).setValue(UserLatlng);

            }

            ;
        };


        contactsLayout = findViewById(R.id.contactsLayout);
        contactscounter=findViewById(R.id.contactscounter);
        ConstraintLayout contacts = findViewById(R.id.contacts);
        ConstraintLayout messages = findViewById(R.id.messages);
        ConstraintLayout scan = findViewById(R.id.scan);
        TextView no_contact_status = findViewById(R.id.no_contact_status);
        ImageButton setting = findViewById(R.id.setting);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingDialog();
            }
        });

        overlappingConstraint = findViewById(R.id.overlappingConstraint);


        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contactsLayout.setVisibility(View.VISIBLE);
                messageCall.setVisibility(View.GONE);
                ConstraintLayout contactpendingCounterLayout=findViewById(R.id.contactpendingCounterLayout);

                if(friendRequestList.size()>0){
                    contactpendingCounterLayout.setVisibility(View.VISIBLE);

                }
                else{
                    contactpendingCounterLayout.setVisibility(View.GONE);
                }


            }
        });
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showMessageDialog();

            }
        });


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanqr = new Dialog(MapsActivity.this);
                mScannerView = new ZXingScannerView(getApplicationContext());
                scanqr.setContentView(mScannerView);
                scanqr.show();
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
                    if (checkPermission()) {
                        if (mScannerView == null) {
                            mScannerView = new ZXingScannerView(MapsActivity.this);
                            scanqr.setContentView(mScannerView);
                            scanqr.show();

                        }
                        mScannerView.setResultHandler(MapsActivity.this);
                        mScannerView.startCamera();

                    } else {
                        requestPermission();
                    }
                }
            }
        });
        EditText search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // writemessage.setText(s.toString());

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //s is the current character in the eddittext after it is changed
                search.clearFocus();
                if (s.length() > 0) {

                }

                if (s.length() == 0) {

                }
            }
        });


        RecyclerView mycontacts = findViewById(R.id.mycontacts);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(MapsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mycontacts.setLayoutManager(horizontalLayoutManager);
        contactAdapter = new ContactAdapter(this, getMyContacts(), new ContactAdapter.OnItemClickListener() {
            @Override
            public void onViewClick(Contact contact) {



                friendID = "";
                if (contact.getSenderID().equals(userId)) {
                    friendID = contact.getReceiverID();
                } else if (contact.getReceiverID().equals(userId)) {
                    friendID = contact.getSenderID();
                }

                for(Marker marker:friendMarkerList){
                    if(friendID.equals(marker.getTag())){
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));
                        marker.showInfoWindow();
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
        mycontacts.setAdapter(contactAdapter);
        if (connectedContacts.size() == 0) {
            no_contact_status.setVisibility(View.VISIBLE);
        } else {
            no_contact_status.setVisibility(View.INVISIBLE);
        }

        ImageView friendrequestbutton = findViewById(R.id.friendrequestbutton);

        friendrequestbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFriendRequests();
            }
        });





    }

    private void showSettingDialog() {

        View settingView=getLayoutInflater().inflate(R.layout.setting,null);
        Dialog settingDialog=new Dialog(MapsActivity.this);
        settingDialog.setContentView(settingView);
        settingDialog.show();
    }

    ArrayList<LatLng> friendLocations=new ArrayList<>();

    String friendId="";
    String friendPhotoUri="";
    LatLng friendLatLng=new LatLng(0,0);
    View friendMarkerView;
    private void getfriendLocations() {

        for(Contact contact:connectedContacts){


            if(contact.getSenderID().equals(userId)){
                friendId=contact.getReceiverID();
            }
            else if(contact.getReceiverID().equals(userId)){
                friendId=contact.getSenderID();
            }

            mFirebaseDatabaseReference.child("Location Updates").child(friendId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Log.d("Location",dataSnapshot.child("latitude").getValue(double.class).toString());
                        double latitude=dataSnapshot.child("latitude").getValue(double.class);
                        double longitude=dataSnapshot.child("longitude").getValue(double.class);
                        friendLatLng = new LatLng(latitude,longitude);
                        mFirebaseDatabaseReference.child("Users").child("Consumers").child(friendId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    friendPhotoUri = dataSnapshot.child("photoUri").getValue(String.class);
                                    String friendName = dataSnapshot.child("consumerName").getValue(String.class);
                                    friendMarkerView=getLayoutInflater().inflate(R.layout.imageviewmarker,null);
                                    ImageView friendImage=friendMarkerView.findViewById(R.id.friendImage);


                                    Picasso.get()
                                            .load(friendPhotoUri)
                                            .into(friendImage, new com.squareup.picasso.Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    Bitmap bmp =  createBitmapFromView(MapsActivity.this,friendMarkerView);

                                                    BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bmp, 180, 180, false));


                                                    Marker friendMarker = mMap.addMarker(new MarkerOptions()
                                                            .position(friendLatLng).title(friendName)
                                                            .visible(true).icon(icon));
                                                    friendMarker.setTag(friendId);
                                                    for(Marker marker:friendMarkerList){
                                                        if(friendId.equals(marker.getTag())){
                                                            marker.remove();
                                                        }
                                                    }
                                                    friendMarkerList.add(friendMarker);
                                                    friendMarker.showInfoWindow();
                                                }

                                                @Override
                                                public void onError(Exception e) {

                                                }
                                            });


                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




        }

    }


    private Bitmap createBitmapFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));

        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }


    MessagesListviewAdaptor messagesListviewAdaptor;
    ContactAdapter groupMembersAdaptor;
    Boolean createGroupStatusAvailable;
    ConstraintLayout eachMessageChatView;
    RecyclerView myMessages;
    DatabaseReference messageRoomsReference;
    Dialog myMessagesDialog;

    private void showMessageDialog() {
        createGroupStatusAvailable = false;
        myMessagesDialog = new Dialog(MapsActivity.this);
        final View myMessagesView = getLayoutInflater().inflate(R.layout.mymessages, null);

        myMessagesDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        myMessagesDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                eachChatViewActive = false;
                eachMessageChatView.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Messages View Dismiss",
                        Toast.LENGTH_SHORT).show();
            }
        });
        ConstraintLayout searchingLayout = myMessagesView.findViewById(R.id.composingLayout);
        ConstraintLayout createGroup = myMessagesView.findViewById(R.id.createGrouplayout);
        myMessages = myMessagesView.findViewById(R.id.myMessages);
        eachMessageChatView = myMessagesView.findViewById(R.id.eachMessageChatView);

        ImageButton composeMessage = myMessagesView.findViewById(R.id.composeMessage);
        TextView composeHeader = myMessagesView.findViewById(R.id.composeHeader);
        TextView composeHeader2 = myMessagesView.findViewById(R.id.composeHeader2);
        TextView myMessageViewHeader = myMessagesView.findViewById(R.id.myMessageViewHeader);
        ImageButton groupLayoutButton = myMessagesView.findViewById(R.id.groupLayoutButton);
        TextView groupLayoutTextView = myMessagesView.findViewById(R.id.groupLayoutTextView);


        LinearLayoutManager horizontalLayoutManager2
                = new LinearLayoutManager(MapsActivity.this, RecyclerView.VERTICAL, false);
        myMessages.setLayoutManager(horizontalLayoutManager2);

        messagesListviewAdaptor = new MessagesListviewAdaptor(this, getMyChatRooms(), chatRoomHashmap, new MessagesListviewAdaptor.OnItemClickListener() {
            @Override
            public void onViewClick(ChatRoom chatRoom) {
                myMessages.setVisibility(View.INVISIBLE);
                showeachChatView(chatRoom, eachMessageChatView);
            }
        });
        myMessages.setAdapter(messagesListviewAdaptor);

        ArrayList<Contact> groupMembers = new ArrayList<>();
        RecyclerView mycontacts = myMessagesView.findViewById(R.id.mycontacts);
        LinearLayoutManager horizontalLayoutManager1
                = new LinearLayoutManager(MapsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mycontacts.setLayoutManager(horizontalLayoutManager1);
        contactAdapter = new ContactAdapter(this, getMyContacts(), new ContactAdapter.OnItemClickListener() {
            @Override
            public void onViewClick(Contact contact) {

                if (createGroupStatusAvailable) {

                    if (!groupMembers.contains(contact)) {
                        groupMembers.add(contact);
                        groupMembersAdaptor.notifyDataSetChanged();
                        composeHeader2.setText("Selected Members (" + groupMembers.size() + ")");
                    }

                } else if (!createGroupStatusAvailable) {
                    searchingLayout.setVisibility(View.GONE);
                    myMessages.setVisibility(View.VISIBLE);
                    composeMessage.setBackgroundResource(R.drawable.composemessage);
                    friendID = "";
                    if (contact.getSenderID().equals(userId)) {
                        friendID = contact.getReceiverID();
                    } else if (contact.getReceiverID().equals(userId)) {
                        friendID = contact.getSenderID();
                    }


                    for (HashMap.Entry<ChatRoom, ArrayList<String>> entry : chatRoomHashmap.entrySet()) {
                        ChatRoom key = entry.getKey();
                        ArrayList value = entry.getValue();

                        if (value.contains(friendID) && value.contains(userId) && value.size() == 2) {
                            showeachChatView(key, eachMessageChatView);

                        }

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
        mycontacts.setAdapter(contactAdapter);


        composeMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Value", "tt");

                if (searchingLayout.getVisibility() == View.GONE) {
                    searchingLayout.setVisibility(View.VISIBLE);
                    myMessages.setVisibility(View.INVISIBLE);
                    composeMessage.setBackgroundResource(R.drawable.delete);
                    myMessageViewHeader.setText("New Message");
                    groupMembers.clear();
                    //groupMembersAdaptor.notifyDataSetChanged();

                } else {
                    searchingLayout.setVisibility(View.GONE);
                    myMessages.setVisibility(View.VISIBLE);
                    composeMessage.setBackgroundResource(R.drawable.composemessage);
                    myMessageViewHeader.setText("My Messages");
                    composeHeader.setText("Send Message To : ");
                    composeHeader2.setText("OR");
                    groupLayoutButton.setBackgroundResource(R.drawable.contacts);
                    groupLayoutTextView.setText("Create Group");
                    createGroupStatusAvailable = false;
                }


            }
        });


        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (groupMembers.size() > 0) {
                    final Dialog roomNameDialog = new Dialog(MapsActivity.this);
                    final View edittextandbuttonView = getLayoutInflater().inflate(R.layout.edittextandbutton, null);

                    ImageButton setRoomButtom = edittextandbuttonView.findViewById(R.id.setRoomButtom);
                    EditText roomName = edittextandbuttonView.findViewById(R.id.roomName);

                    setRoomButtom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("Size", ":" + groupMembers.size());
                            if (roomName.getText().toString().length() > 0 && groupMembers.size() >= 2) {
                                roomNameDialog.dismiss();
                                searchingLayout.setVisibility(View.GONE);
                                myMessages.setVisibility(View.VISIBLE);
                                composeMessage.setBackgroundResource(R.drawable.composemessage);
                                myMessageViewHeader.setText("My Messages");
                                composeHeader.setText("Send Message To : ");
                                composeHeader2.setText("OR");
                                groupLayoutButton.setBackgroundResource(R.drawable.contacts);
                                groupLayoutTextView.setText("Create Group");
                                createGroupStatusAvailable = false;

                                messageRoomsReference = mFirebaseDatabaseReference.child("Messages Rooms").push();
                                String roomID = messageRoomsReference.getKey();
                                mFirebaseDatabaseReference.child("Messages Room Names").child(roomID).setValue(roomName.getText().toString());
                                ArrayList<String> chatroomUsers = new ArrayList<>();
                                for (Contact contact : groupMembers) {
                                    friendID = "";
                                    if (contact.getSenderID().equals(userId)) {
                                        friendID = contact.getReceiverID();
                                    } else if (contact.getReceiverID().equals(userId)) {
                                        friendID = contact.getSenderID();
                                    }
                                    chatroomUsers.add(friendID);
                                }

                                chatroomUsers.add(userId);
                                messageRoomsReference.setValue(chatroomUsers);
                                dd = mFirebaseDatabaseReference.child("Messages").child(roomID).push();
                                long currentDateTime = System.currentTimeMillis();
                                Message mes1 = new Message(roomName.getText().toString() + " room created.Get active in Groups", dd.getKey(), String.valueOf(currentDateTime), "auto", "sent");
                                dd.setValue(mes1);


                                groupMembers.clear();
                                groupMembersAdaptor.notifyDataSetChanged();

                            } else {
                                roomName.setTextColor(Color.RED);
                                roomName.setHint("Room Name Empty");

                            }
                        }
                    });
                    roomNameDialog.setContentView(edittextandbuttonView);
                    roomNameDialog.show();

                } else {
                    myMessageViewHeader.setText("Creating Group");
                    composeHeader.setText("Choose At least two Members for Group : ");
                    composeHeader2.setText("Selected Members (" + groupMembers.size() + ")");
                    createGroupStatusAvailable = true;

                    groupLayoutButton.setBackgroundResource(R.drawable.accept);
                    groupLayoutTextView.setText("DONE");

                    RecyclerView selectedGroupMembersListView = myMessagesView.findViewById(R.id.selectedGroupMembersListView);
                    LinearLayoutManager horizontalLayoutManager3
                            = new LinearLayoutManager(MapsActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    selectedGroupMembersListView.setLayoutManager(horizontalLayoutManager3);
                    groupMembersAdaptor = new ContactAdapter(getApplicationContext(), groupMembers, new ContactAdapter.OnItemClickListener() {
                        @Override
                        public void onViewClick(Contact contact) {
                            groupMembers.remove(contact);
                            groupMembersAdaptor.notifyDataSetChanged();
                            composeHeader2.setText("Selected Members (" + groupMembers.size() + ")");

                        }

                        @Override
                        public void onDelete(Contact contact) {

                        }

                        @Override
                        public void onAccept(Contact contact) {

                        }
                    });
                    selectedGroupMembersListView.setAdapter(groupMembersAdaptor);

                }
            }
        });


        myMessagesDialog.setContentView(myMessagesView);
        myMessagesDialog.show();


    }

    ArrayList<ChatRoom> chatRoomList = new ArrayList<>();
    HashMap<ChatRoom, ArrayList<String>> chatRoomHashmap = new HashMap<>();

    private ArrayList<ChatRoom> getMyChatRooms() {
        mFirebaseDatabaseReference.child("Messages Rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatRoomList.clear();
                    chatRoomHashmap.clear();
                    ArrayList<ChatRoom> temchatRoomList=new ArrayList<>();


                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        String chatRoomActiveTime = dataSnapshot1.child("activeTime").getValue(String.class);
                        String lastMessageID = dataSnapshot1.child("lastMessageID").getValue(String.class);


                        ArrayList<String> chatRoomUsersID = new ArrayList<>();
                        chatRoomUsersID = (ArrayList<String>) dataSnapshot1.child("chatRoomUsers").getValue();

                        if (chatRoomUsersID.contains(userId)) {

                            long time=Long.valueOf(chatRoomActiveTime);
                            ChatRoom chatRoom = new ChatRoom(dataSnapshot1.getKey(), chatRoomActiveTime, chatRoomUsersID, lastMessageID);

                            chatRoomList.add(chatRoom);
                            if(chatRoomList.size()>0){
                                Log.d("list before",""+chatRoomList);
                                for(ChatRoom chtr:chatRoomList){
                                    if(time>Long.valueOf(chtr.getActiveTime())){

                                        Log.d("before chtr",""+chatRoomList.indexOf(chtr));
                                        Log.d("before chatroom",""+chatRoomList.indexOf(chatRoom));
                                        Collections.swap(chatRoomList, chatRoomList.indexOf(chatRoom), chatRoomList.indexOf(chtr));
                                        Log.d("after chrt",""+chatRoomList.indexOf(chtr));
                                        Log.d("after chatroom",""+chatRoomList.indexOf(chatRoom));
                                        Log.d("list",""+chatRoomList);

                                    }

                                }

                            }

                            if (messagesListviewAdaptor != null) {
                                messagesListviewAdaptor.notifyDataSetChanged();
                            }
                            chatRoomHashmap.put(chatRoom, chatRoomUsersID);


                        }


                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return chatRoomList;
    }


    private ContactAdapter requestPendingContactAdapter;
    private ContactAdapter requestContactAdapter;
    private ArrayList<Contact> friendRequestList = new ArrayList<>();
    private ArrayList<Contact> friendRequestPendingList = new ArrayList<>();

    private void showFriendRequests() {
        Dialog friendrequestsViewDialog = new Dialog(MapsActivity.this);
        View friendrequestsView = getLayoutInflater().inflate(R.layout.friendrequests, null);
        final ConstraintLayout requestslayout = friendrequestsView.findViewById(R.id.requestslayout);
        final ConstraintLayout requestspendinglayout = friendrequestsView.findViewById(R.id.requestspendinglayout);
        TextView emptystatus = friendrequestsView.findViewById(R.id.emptystatus);


        final RecyclerView friendRequestRecycleView = friendrequestsView.findViewById(R.id.friendRequestRecycleView);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(MapsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        friendRequestRecycleView.setLayoutManager(horizontalLayoutManager);
        requestContactAdapter = new ContactAdapter(this, friendRequestList, new ContactAdapter.OnItemClickListener() {
            @Override
            public void onViewClick(Contact contact) {

            }

            @Override
            public void onDelete(Contact contact) {
                Toast.makeText(MapsActivity.this, "Contact Deleted :" + contact.getContactID(), Toast.LENGTH_LONG).show();

                mFirebaseDatabaseReference.child("Contacts").child(contact.getContactID()).setValue(null);
                friendRequestList.remove(contact);
                requestContactAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAccept(Contact contact) {
                Toast.makeText(MapsActivity.this, "Accepted", Toast.LENGTH_LONG).show();
                friendRequestList.remove(contact);
                friendID = "";
                if (contact.getSenderID().equals(userId)) {
                    friendID = contact.getReceiverID();
                } else if (contact.getReceiverID().equals(userId)) {
                    friendID = contact.getSenderID();
                }

                String chatRoomName = "";
                mFirebaseDatabaseReference.child("Users").child("Consumers").child(friendID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String consumerName = dataSnapshot.child("consumerName").getValue(String.class);
                            String consumerEmail = dataSnapshot.child("consumerEmail").getValue(String.class);
                            String photoUri = dataSnapshot.child("photoUri").getValue(String.class);

                            mFirebaseDatabaseReference.child("Contacts").child(contact.getContactID()).child("status").setValue("Connected");
                            requestContactAdapter.notifyDataSetChanged();
                            messageRoomsReference = mFirebaseDatabaseReference.child("Messages Rooms").push();

                            String roomID = messageRoomsReference.getKey();
                            ArrayList<String> chatroomUsers = new ArrayList<>();
                            chatroomUsers.add(userId);
                            chatroomUsers.add(friendID);

                            long activeTime = System.currentTimeMillis();
                            dd = mFirebaseDatabaseReference.child("Messages").child(roomID).push();

                            ChatRoom chatRoom = new ChatRoom(roomID, String.valueOf(activeTime), chatroomUsers, dd.getKey());

                            messageRoomsReference.setValue(chatRoom);

                            long currentDateTime = System.currentTimeMillis();
                            Message mes1 = new Message("Connection Created. Start by Chatting with Friends", dd.getKey(), String.valueOf(currentDateTime), "auto", "sent");
                            dd.setValue(mes1);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        requestPendingContactAdapter = new ContactAdapter(this, friendRequestPendingList, new ContactAdapter.OnItemClickListener() {
            @Override
            public void onViewClick(Contact contact) {

            }

            @Override
            public void onDelete(final Contact contact) {

                Toast.makeText(MapsActivity.this, "Contact Deleted :" + contact.getContactID(), Toast.LENGTH_LONG).show();

                mFirebaseDatabaseReference.child("Contacts").child(contact.getContactID()).setValue(null);
                friendRequestPendingList.remove(contact);
                requestPendingContactAdapter.notifyDataSetChanged();

            }

            @Override
            public void onAccept(Contact contact) {
                Toast.makeText(MapsActivity.this, "ACCEPT", Toast.LENGTH_LONG).show();
            }
        });

        friendRequestRecycleView.setAdapter(requestContactAdapter);

        requestslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendRequestRecycleView.setAdapter(requestContactAdapter);
                requestspendinglayout.setBackgroundResource(0);
                requestslayout.setBackgroundResource(R.drawable.white_roundedbox);
            }
        });

        requestspendinglayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendRequestRecycleView.setAdapter(requestPendingContactAdapter);
                requestslayout.setBackgroundResource(0);
                requestspendinglayout.setBackgroundResource(R.drawable.white_roundedbox);
                // friendRequestRecycleView.setBackgroundResource(R.drawable.mychatbox);
            }
        });

        friendrequestsViewDialog.setContentView(friendrequestsView);
        friendrequestsViewDialog.show();
        requestspendinglayout.setBackgroundResource(0);

    }

    String friendID = "";
    String messageId = "";
    private View eachchatView;
    private ListView messagelistview;
    ChatBoxListViewAdapter chatBoxListViewAdapter;
    DatabaseReference newMessageReference;

    ArrayList<Message> messageslist = new ArrayList<>();
    ChatRoomUsersListViewAdaptor chatRoomUserAdaptor;
    boolean eachChatViewActive = false;

    private void showeachChatView(ChatRoom chatRoom, ConstraintLayout eachMessageChatView) {
        eachMessageChatView.setVisibility(View.VISIBLE);
        eachChatViewActive = true;
        eachchatView = getLayoutInflater().inflate(R.layout.contactchatview, null);
        final ImageView contactImage = eachchatView.findViewById(R.id.contactImage);
        final TextView roomNameTextView = eachchatView.findViewById(R.id.roomName);
        RecyclerView chatRoomUsersListView = eachchatView.findViewById(R.id.chatRoomUsersListView);
        ImageButton videoCall = eachchatView.findViewById(R.id.videoCall);
        ImageButton voiceCall = eachchatView.findViewById(R.id.voiceCall);


        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(MapsActivity.this, RecyclerView.HORIZONTAL, false);
        chatRoomUsersListView.setLayoutManager(horizontalLayoutManager);


        chatRoomUserAdaptor = new ChatRoomUsersListViewAdaptor(getApplicationContext(), chatRoomHashmap.get(chatRoom), new ChatRoomUsersListViewAdaptor.OnItemClickListener() {

            @Override
            public void onViewClick(String FriendID) {

            }

        });
        chatRoomUserAdaptor.notifyDataSetChanged();
        chatRoomUsersListView.setAdapter(chatRoomUserAdaptor);

        if (chatRoomHashmap.get(chatRoom).size() > 2) {

            mFirebaseDatabaseReference.child("Messages Room Names").child(chatRoom.getChatRoomID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String roomName = dataSnapshot.getValue(String.class);
                        roomNameTextView.setText(roomName);
                        roomNameTextView.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            ArrayList<String> chatRoomUsers = chatRoomHashmap.get(chatRoom);
            for (String id : chatRoomUsers) {
                if (id.equals(userId)) {

                } else {
                    friendID = id;
                }
            }
        }

        voiceCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call call = new Call(userId, friendID, "Voice Call");
                mFirebaseDatabaseReference.child("Calls").child(friendID).setValue(call);

            }
        });

        videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call call = new Call(userId, friendID, "Video Call");
                mFirebaseDatabaseReference.child("Calls").child(friendID).setValue(call);
                startConnection();
            }
        });

        ImageButton backbutton = eachchatView.findViewById(R.id.backButton);

        eachMessageChatView.removeAllViews();
        eachMessageChatView.addView(eachchatView);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eachMessageChatView.removeAllViews();
                myMessages.setVisibility(View.VISIBLE);
                eachChatViewActive = false;
            }
        });

        messagelistview = eachchatView.findViewById(R.id.messagelistview);
        ImageButton addfiles = eachchatView.findViewById(R.id.addfiles);
        addfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Add Files Now",
                        Toast.LENGTH_SHORT).show();
            }
        });
        final EditText writemessage = eachchatView.findViewById(R.id.writemessage);
        final MediaPlayer sendsound = MediaPlayer.create(this, R.raw.bubble);
        sendsound.setVolume(0.1f, 0.1f);
        final MediaPlayer friendtypingSound = MediaPlayer.create(this, R.raw.friendtyping);
        friendtypingSound.setVolume(0.05f, 0.05f);
        final ImageButton send = eachchatView.findViewById(R.id.send);

        chatBoxListViewAdapter = new ChatBoxListViewAdapter(MapsActivity.this, R.layout.bubble, getFriendMessageslist(chatRoom.getChatRoomID()));
        messagelistview.setAdapter(chatBoxListViewAdapter);


        writemessage.setFocusable(true);

        writemessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageId.isEmpty()) {


                }

            }
        });
        long currentDateTime = System.currentTimeMillis();
        newMessageReference = mFirebaseDatabaseReference.child("Messages").child(chatRoom.getChatRoomID()).push();
        messageId = newMessageReference.getKey();

        writemessage.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //s is the current character in the eddittext after it is changed
                writemessage.clearFocus();
                if (s.length() == 1) {
                    Message typingMessage = new Message("...", messageId, String.valueOf(currentDateTime), userId, "typing");
                    newMessageReference.setValue(typingMessage);
                }
                if (s.length() == 0 && messageId == newMessageReference.getKey()) {

                    newMessageReference.removeValue();
                }


            }

        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writemessage.getText() != null && writemessage.getText().length() > 0) {
                    sendsound.start();
                    long currentDateTime1 = System.currentTimeMillis();
                    Message newMessage = new Message(writemessage.getText().toString(), messageId, String.valueOf(currentDateTime1), userId, "sent");
                    newMessageReference.setValue(newMessage);
                    newMessageReference = mFirebaseDatabaseReference.child("Messages").child(chatRoom.getChatRoomID()).push();
                    mFirebaseDatabaseReference.child("Messages Rooms").child(chatRoom.getChatRoomID()).child("activeTime").setValue(String.valueOf(currentDateTime1));
                    messageId = newMessageReference.getKey();
                    writemessage.setText("");

                }

            }
        });
    }

    private ArrayList<Message> getFriendMessageslist(String chatRoomID) {
        mFirebaseDatabaseReference.child("Messages").child(chatRoomID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    messageslist.clear();
                    chatBoxListViewAdapter.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        String message1 = data.child("message").getValue(String.class);
                        String messageid = data.child("messageId").getValue(String.class);
                        String date = data.child("date").getValue(String.class);
                        String senderID = data.child("senderID").getValue(String.class);
                        String status = data.child("status").getValue(String.class);
                        if (!senderID.equals(userId) && eachChatViewActive && !message1.equals("...")) {
                            mFirebaseDatabaseReference.child("Messages").child(chatRoomID).child(messageid).child("status").setValue("seen");
                            status = "seen";
                        }
                        if (!senderID.equals(userId) && status.equals("sent")) {
                            mFirebaseDatabaseReference.child("Messages").child(chatRoomID).child(messageid).child("status").setValue("Delivered");
                            status = "Delivered";
                        }
                        Message chatMessage = new Message(message1, messageid, date, senderID, status);
                        messageslist.add(chatMessage);
                        chatBoxListViewAdapter.notifyDataSetChanged();
                        messagelistview.setSelection(messagelistview.getAdapter().getCount() - 1);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return messageslist;
    }

    private ArrayList<Contact> connectedContacts = new ArrayList<>();

    private ArrayList<Contact> getMyContacts() {
        mFirebaseDatabaseReference.child("Contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    connectedContacts.clear();
                    friendRequestList.clear();
                    friendRequestPendingList.clear();
                    if (requestContactAdapter != null && requestPendingContactAdapter != null) {
                        requestContactAdapter.notifyDataSetChanged();
                        requestPendingContactAdapter.notifyDataSetChanged();
                    }

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String contactID = dataSnapshot1.child("contactID").getValue(String.class);
                        String senderID = dataSnapshot1.child("senderID").getValue(String.class);
                        String receiverID = dataSnapshot1.child("receiverID").getValue(String.class);
                        String status = dataSnapshot1.child("status").getValue(String.class);
                        Contact contact = new Contact(contactID, senderID, receiverID, status);

                        if (senderID.equals(userId) || receiverID.equals(userId)) {
                            if (status.equals("Pending") && senderID.equals(userId)) {
                                friendRequestPendingList.add(contact);
                                if (requestPendingContactAdapter != null) {
                                    requestPendingContactAdapter.notifyDataSetChanged();
                                }
                            } else if (status.equals("Connected")) {
                                connectedContacts.add(contact);
                                contactAdapter.notifyDataSetChanged();
                                getfriendLocations();

                            }
                            if (status.equals("Pending") && receiverID.equals(userId)) {
                                friendRequestList.add(contact);
                                if (requestContactAdapter != null) {
                                    requestContactAdapter.notifyDataSetChanged();
                                }

                            }


                        }


                    }
                    if (friendRequestList.size()>0) {

                        contactscounter.setVisibility(View.VISIBLE);
                        contactscounter.setText(String.valueOf(friendRequestList.size()));
                    }
                    else{
                        contactscounter.setVisibility(View.GONE);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return connectedContacts;
    }

    int PICK_IMAGE_REQUEST = 111;
    String userName = "";

    private void showProfileView() {
        Dialog profileDialog = new Dialog(MapsActivity.this);
        View profileView = getLayoutInflater().inflate(R.layout.profileview, null);
        profileImage = profileView.findViewById(R.id.profileImage);
        ImageView userBarCode = profileView.findViewById(R.id.userBarCode);
        final TextView userNameTextView = profileView.findViewById(R.id.username);
        profileDialog.setContentView(profileView);
        profileDialog.show();

        String text = "User:" + userId;
        mFirebaseDatabaseReference.child("Users").child("Consumers").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String consumerName = dataSnapshot.child("consumerName").getValue(String.class);
                    String consumerEmail = dataSnapshot.child("consumerEmail").getValue(String.class);
                    String photoUri = dataSnapshot.child("photoUri").getValue(String.class);
                    userName = consumerName;
                    userNameTextView.setText(userName);
                    if (photoUri != null) {
                        Picasso.get().load(photoUri).fit().centerCrop().into(profileImage);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Toast.makeText(MapsActivity.this, consumer.getconsumerEmail(), Toast.LENGTH_LONG).show();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            userBarCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        ImageButton uploadImage = profileView.findViewById(R.id.uploadImage);
        userImageViewCons = profileView.findViewById(R.id.userImageViewCons);
        progressBar = profileView.findViewById(R.id.progressBar);
        uploadImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

        });


        Button signOut = profileView.findViewById(R.id.signOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                profileDialog.dismiss();
            }
        });


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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private Location location;
    Marker usermarker;
    ConstraintLayout messageCall;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);


            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            mLastLocation = location;

            startLocationUpdates();




        }
        changeMapCamera();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (userId.equals(marker.getTag())) {
                    showProfileView();
                }else{

                    messageCall.setVisibility(View.VISIBLE);
                    contactsLayout.setVisibility(View.GONE);
                }
                return false;
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {


            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                changeMapCamera();
                //overlappingConstraint.removeAllViews();
                contactsLayout.setVisibility(View.GONE);
                messageCall.setVisibility(View.GONE);

                if(usermarker!=null){
                    usermarker.showInfoWindow();
                }


            }
        });

    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
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
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mMap.setMyLocationEnabled(true);

                        Criteria criteria = new Criteria();
                        String provider = locationManager.getBestProvider(criteria, true);
                        Location location = locationManager.getLastKnownLocation(provider);
                        mLastLocation = location;
                        changeMapCamera();
                        startLocationUpdates();
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
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
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{CAMERA},
                                                    REQUEST_CAMERA);
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;


        }

        // other 'case' lines to check for other permissions this app might request.
        //You can add here other case statements according to your requirement.


    }

    private void changeMapCamera() {
        if (mLastLocation != null) {
            cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))      // Sets the center of the map to Mountain View
                    .zoom(15)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(85)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }
    }

    private void getIntentData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.d("Data", getIntent().getExtras().toString());
            Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
            String chatroomID = getIntent().getStringExtra("chatroomID");
            Log.d("notification in click ", chatroomID);
            showMessageDialog();
            getIntent().getExtras().clear();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        getIntentData();
        if (myMessagesDialog != null && eachMessageChatView.getVisibility() == View.VISIBLE) {
            eachChatViewActive = true;
        }



    }

    private  BroadcastReceiver gpsSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {

                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (isGpsEnabled || isNetworkEnabled) {
                    // Handle Location turned ON
                } else {
                    // Handle Location turned OFF
                }
            }
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        eachChatViewActive = false;
        mFirebaseDatabaseReference.child("User-Active-time").child(userId).removeValue();

    }

    @Override
    public void onPause() {
        super.onPause();
        eachChatViewActive = false;

        getMyChatRooms();
        getIntentData();
        mFirebaseDatabaseReference.child("User-Active-time").child(userId).removeValue();

        getApplicationContext().unregisterReceiver(gpsSwitchStateReceiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(Intent.ACTION_PROVIDER_CHANGED);
        getApplicationContext().registerReceiver(gpsSwitchStateReceiver, filter);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        getMyChatRooms();
        getIntentData();

        mFirebaseDatabaseReference.child("User-Active-time").child(userId).setValue("online");

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        eachChatViewActive = false;
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }


    @Override
    public void handleResult(Result result) {
        final String data = result.getText();

        if(data.contains("User:")) {
            scanqr.dismiss();
            String consumerID = data.substring(5);
            Toast.makeText(getApplicationContext(), "User ID: " + consumerID,
                    Toast.LENGTH_SHORT).show();
            if(consumerID.equals(userId)){
                Toast.makeText(getApplicationContext(), "Cannot add yourself !!!" + consumerID,
                        Toast.LENGTH_SHORT).show();
            }
            else{
                showaddcontact(consumerID);
            }


        }
        else {
            scanqr.dismiss();
            Toast.makeText(getApplicationContext(), "QR code not in this application.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showaddcontact(final String consumerID) {
        final Dialog addcontactDialog = new Dialog(MapsActivity.this);
        View addcontact = getLayoutInflater().inflate(R.layout.addcontactprofile, null);

        final Button add=addcontact.findViewById(R.id.addfriend);
        final ImageView contactImage=addcontact.findViewById(R.id.contactImage);
        final TextView userName=addcontact.findViewById(R.id.userName);


        addcontactDialog.setContentView(addcontact);
        addcontactDialog.show();

        mFirebaseDatabaseReference.child("Users").child("Consumers").child(consumerID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String consumerName=dataSnapshot.child("consumerName").getValue(String.class);
                    String consumerEmail=dataSnapshot.child("consumerEmail").getValue(String.class);
                    String photoUri=dataSnapshot.child("photoUri").getValue(String.class);
                    if(photoUri!=null){
                       Picasso.get().load(photoUri).fit().centerCrop().into(contactImage);
                    }
                    userName.setText(consumerName);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

             for(Contact contact:friendRequestList){
                 if(contact.getReceiverID().equals(userId) && contact.getSenderID().equals(consumerID) || contact.getReceiverID().equals(consumerID) && contact.getSenderID().equals(userId)){
                     add.setText(contact.getStatus());
                 }
             }
            for(Contact contact:friendRequestPendingList){
                if(contact.getReceiverID().equals(userId) && contact.getSenderID().equals(consumerID) || contact.getReceiverID().equals(consumerID) && contact.getSenderID().equals(userId)){
                    add.setText(contact.getStatus());
                }
            }
            for(Contact contact:connectedContacts){
                if(contact.getReceiverID().equals(userId) && contact.getSenderID().equals(consumerID) || contact.getReceiverID().equals(consumerID) && contact.getSenderID().equals(userId)){
                    add.setText(contact.getStatus());
                }
            }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(add.getText().equals("Add Contact")){
                    addcontactDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Request Sent to: "+ userName.getText().toString(),
                            Toast.LENGTH_SHORT).show();

                    String contactID=mFirebaseDatabaseReference.child("Contacts").push().getKey();
                    Contact contact=new Contact(contactID,userId,consumerID,"Pending");

                    mFirebaseDatabaseReference.child("Contacts").child(contactID).setValue(contact);


                }

            }
        });


    }



    DatabaseReference dd;
    StorageReference filePath;

    @Override
    public void onActivityResult(int requestCode,int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri localStorageuri = data.getData();
            userImageViewCons.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            filePath = storageRef.child("Users").child(userId).child("image.jpg");


            filePath.putFile(localStorageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                   filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           userImageViewCons.setVisibility(View.VISIBLE);
                           progressBar.setVisibility(View.INVISIBLE);
                           Picasso.get().load(uri).fit().centerCrop().into(profileImage);
                           mFirebaseDatabaseReference.child("Users").child("Consumers").child(userId).child("photoUri").setValue(uri.toString());

                       }
                   });

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = 100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                    progressBar.setProgress((int)progress);
                }
            });

        }


    }

    EglBase rootEglBase;
    MediaPlayer callingsound;
    SurfaceViewRenderer friendVideo;
    SurfaceViewRenderer myvideo;
    PeerConnectionFactory peerConnectionFactory;
    MediaConstraints sdpConstraints;
    VideoTrack localVideoTrack;
    VideoTrack remoteVideoTrack;
    AudioTrack localAudioTrack;
    MediaStream stream;

    PeerConnection localPeer, remotePeer;
    Button start, call, hangup;
    private void startConnection(){

        rootEglBase=EglBase.create();
        //initialize PCF
        PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(this)
                        .setFieldTrials("WebRTC-IntelVP8/Enabled")
                        .createInitializationOptions()
        );
        PeerConnectionFactory.Options options= new PeerConnectionFactory.Options();

        peerConnectionFactory=PeerConnectionFactory.builder().setOptions(options)
                .createPeerConnectionFactory();

        VideoCapturer videoCapturer=createVideoCapturer();
        MediaConstraints constraints=new MediaConstraints();
        VideoSource videoSource=peerConnectionFactory.createVideoSource(false);
        //VideoTrack localVideoTrack= createVideoTrack(videoCapturer,videoSource);
        SurfaceTextureHelper textureHelper=SurfaceTextureHelper.create(Thread.currentThread().getName(),rootEglBase.getEglBaseContext());
        videoCapturer.initialize(textureHelper,this,videoSource.getCapturerObserver());
        videoCapturer.startCapture(1024,720,30);//capture in HD

        localVideoTrack=peerConnectionFactory.createVideoTrack("VIDEO1",videoSource);
        localVideoTrack.setEnabled(true);
        AudioSource audioSource=peerConnectionFactory.createAudioSource(constraints);
        localAudioTrack=peerConnectionFactory.createAudioTrack("AUDIO1",audioSource);
        localAudioTrack.setEnabled(true);



        final View videocallview = getLayoutInflater().inflate(R.layout.videocallview, null);
        ImageButton endCall=videocallview.findViewById(R.id.endCall);
        overlappingConstraint.addView(videocallview);
        myMessagesDialog.dismiss();


        friendVideo =videocallview.findViewById(R.id.friendVideo);
        myvideo =videocallview.findViewById(R.id.myvideo);



        friendVideo.init(rootEglBase.getEglBaseContext(),null);
        friendVideo.setVisibility(View.VISIBLE);

        friendVideo.setMirror(true);
        myvideo.init(rootEglBase.getEglBaseContext(),null);
        myvideo.setVisibility(View.VISIBLE);

        myvideo.setMirror(true);

        localVideoTrack.addSink(myvideo);
        localVideoTrack.addSink(friendVideo);





        callingsound=MediaPlayer.create(getApplicationContext(),R.raw.phoneringing);
        callingsound.setVolume(00.1f,0.01f);
         if(callingsound.isPlaying()){
             callingsound.stop();
             callingsound.start();
         }
         else{
             callingsound.start();
         }

        callingsound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                callingsound.start();
            }
        });

        endCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlappingConstraint.removeAllViews();
                callingsound.stop();
                callingsound.reset();
                hangup();
            }
        });

        call();



    }


    private VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer;
        videoCapturer = createCameraCapturer(new Camera1Enumerator(true));
        return videoCapturer;
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // Trying to find a front facing camera!
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // We were not able to find a front cam. Look for other cameras
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this);
    }

    private void call() {
        //we already have video and audio tracks. Now create peerconnections
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        PeerConnection.IceServer.Builder iceServerBuilder = PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302");
        iceServerBuilder.setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_INSECURE_NO_CHECK); //this does the magic.

        PeerConnection.IceServer iceServer =  iceServerBuilder.createIceServer();

        iceServers.add(iceServer);

         if(iceServers.isEmpty()){
             Log.d("Servers","null");
            }
            else{
                Log.d("Servers",iceServers.toString());
            }

       //create sdpConstraints
        sdpConstraints = new MediaConstraints();
        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));



        //creating localPeer
        localPeer = peerConnectionFactory.createPeerConnection(iceServers,sdpConstraints, new PeerConnectionObserver("localPeerCreation") {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                onIceCandidateReceived(localPeer, iceCandidate);

            }
        });

        //creating remotePeer
        remotePeer = peerConnectionFactory.createPeerConnection(iceServers, sdpConstraints, new PeerConnectionObserver("remotePeerCreation") {

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                onIceCandidateReceived(remotePeer, iceCandidate);

            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
                if(mediaStream.audioTracks.size() > 0) {
                    remoteVideoTrack=mediaStream.videoTracks.get(0);
                    remotePeer.addStream(stream);
                    remoteVideoTrack.addSink(friendVideo);
                }


            }
        });

        MediaStream mediaStream=peerConnectionFactory.createLocalMediaStream("ARDAMS");
        mediaStream.addTrack(localVideoTrack);
        mediaStream.addTrack(localAudioTrack);
       localPeer.addStream(mediaStream);


        Log.d("Local Peer",localPeer.toString());


    }
    private void hangup() {
        //localPeer.close();
        //remotePeer.close();
        localPeer = null;
        remotePeer = null;
    }


    public void onIceCandidateReceived(PeerConnection peer, IceCandidate iceCandidate) {
        //we have received ice candidate. We can set it to the other peer.
        if (peer == localPeer) {
            remotePeer.addIceCandidate(iceCandidate);
        } else {
            localPeer.addIceCandidate(iceCandidate);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}



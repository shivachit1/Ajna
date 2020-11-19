package app.shiva.ajna.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import app.shiva.ajna.R;
import app.shiva.ajna.activities.MapsActivity;
import app.shiva.ajna.model.Call;
import app.shiva.ajna.model.User;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.CAMERA;

public class CallViewFragment extends Fragment implements View.OnClickListener,Session.SessionListener, PublisherKit.PublisherListener {

    private User friend;
    private ConstraintLayout friendVideoCons;
    private ConstraintLayout myVideoCons;
    private DatabaseReference databaseReference;
    private ValueEventListener myCallValueEventListener;


    private ConstraintLayout acceptCallCons;
    private String userId;


    private static final String LOG_TAG=MapsActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM=124;
    private Session openToksession;

    private Publisher mPublisher;
    private Subscriber mSubscriber;
    private String callMode;
    private Call activeCall;
    private boolean firstCall=true;
    private View callView;
    private Handler timeHandler=new Handler();
    private long timerCountDown=0;
    private MediaPlayer phoneCallingTonePlayer;
    private MediaPlayer phoneRingingTonePlayer;
    TextView callStatusTextView;
    private String openTok_SESSION_ID;
    private String openTok_TOKEN;

    public CallViewFragment() {
        // Required empty public constructor
    }

    public void setUser(User friend){
        this.friend=friend;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       callView= inflater.inflate(R.layout.callview,container,false);
        Intent i = getActivity().getIntent();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
       String callType=i.getStringExtra("callType");
       callMode=i.getStringExtra("callMode");

        phoneCallingTonePlayer = MediaPlayer.create(getContext(), R.raw.phonecallingtone);
        phoneCallingTonePlayer.setVolume(0.07f,0.07f);

        phoneRingingTonePlayer = MediaPlayer.create(getContext(), R.raw.phoneringtone);
        phoneRingingTonePlayer.setVolume(0.07f,0.07f);
      Log.e("fragmentCount" ,""+getActivity().getSupportFragmentManager().getFragments().toString());

        Log.e("fragmentCount" ,""+getChildFragmentManager().getFragments().toString());

        databaseReference = FirebaseDatabase.getInstance().getReference();


        acceptCallCons=callView.findViewById(R.id.acceptCallCons);
        callStatusTextView=callView.findViewById(R.id.callStatusTextView);
        ImageView friendImage=callView.findViewById(R.id.friendImage);
        TextView friendName=callView.findViewById(R.id.friendName);
        friendName.setText(friend.getUserName());
        Picasso.get().load(friend.getPhotoUri()).fit().into(friendImage);


        callView.findViewById(R.id.endcallCons).setOnClickListener(this);

        long currentTime=System.currentTimeMillis();
        if(callMode!=null){
            if(callMode.equals("outgoing")){

                activeCall=new Call(userId,friend.getUserID(),callType,"calling","empty","empty","0","0");
            }else if (callMode.equals("incoming")){

                activeCall=new Call(friend.getUserID(),userId,callType,"calling","empty","empty","0","0");

                acceptCallCons.setOnClickListener(this);
            }

            myCallValueEventListener=databaseReference.child("Calls").child(activeCall.getRecieverId()).addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){

                        Call call =dataSnapshot.getValue(Call.class);
                        if(call!=null && (call.getCallerId().equals(userId)||call.getRecieverId().equals(userId))){

                            friendName.setText(friend.getUserName());

                            if(call.getCallStatus().equals("calling")){
                                callStatusTextView.setText("Calling");
                                if(call.getCallerId().equals(userId)){
                                    phoneCallingTonePlayer.start();
                                }

                                if(call.getRecieverId().equals(userId)){
                                    databaseReference.child("Calls").child(activeCall.getRecieverId()).child("callStatus").setValue("Ringing");
                                    Log.d("Reciever","Myself");


                                }else{
                                    Log.d("Reciever",friend.getUserName());

                                }

                            }
                            else if(call.getCallStatus().equals("Ringing")){
                                // Play Ringing Sound
                                Log.d("Phone Ringing",friend.getUserName());
                                callStatusTextView.setText("Ringing");
                                if(call.getRecieverId().equals(userId)){
                                    acceptCallCons.setVisibility(View.VISIBLE);
                                    phoneRingingTonePlayer.start();
                                }else{
                                    acceptCallCons.setVisibility(View.GONE);
                                }

                            }

                            if(call.getCallStatus().equals("picked up")){


                                phoneCallingTonePlayer.stop();
                                phoneRingingTonePlayer.stop();
                                openTok_SESSION_ID=call.getSessionId();
                                openTok_TOKEN=call.getTokenId();

                                requestPermisions();
                                Log.d("Phonepicked up",friend.getUserName());
                                callStatusTextView.setText("picked up");

                                timeHandler.postDelayed(myRunnable,1000);



                            }
                            else if(call.getCallStatus().equals("ended")){
                                callStatusTextView.setText("Call Ended");
                                Log.d("Call ended",friend.getUserName());

                                if(openToksession!=null){
                                    openToksession.disconnect();
                                }

                                if(getFragmentManager().findFragmentByTag("callViewFragment") != null) {
                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction().
                                            remove(CallViewFragment.this).commit();
                                }


                                MapsActivity.overLappingCons.removeAllViews();
                                MapsActivity.mainCons.setVisibility(View.VISIBLE);
                                databaseReference.child("Calls").child(activeCall.getRecieverId()).removeValue();
                            }

                        }
                        else{
                            friendName.setText(friend.getUserName()+" is busy other call");
                        }

                    }else{

                        if(callMode.equals("outgoing")){
                            Log.d("User call status","User Not busy");
                            friendName.setText(friend.getUserName());
                            databaseReference.child("Calls").child(friend.getUserID()).setValue(activeCall);
                        }



                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        return callView;
    }


    int seconds=0;
    int minutes=0;
    int hours=0;
    Runnable myRunnable = new Runnable() {
        public void run() {
            timerCountDown++;
            DateUtils.formatElapsedTime(timerCountDown);
            callStatusTextView.setText(""+DateUtils.formatElapsedTime(timerCountDown));
            timeHandler.postDelayed(this, 1000);
            Log.d("TimerCount:",""+timerCountDown);
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {

            case RC_VIDEO_APP_PERM:
                EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults);
                break;


        }


    }



    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermisions(){
        String [] perms={Manifest.permission.INTERNET, CAMERA,Manifest.permission.RECORD_AUDIO};
        if(EasyPermissions.hasPermissions(getContext(),perms)){
            friendVideoCons=callView.findViewById(R.id.friendVideoCons);
            myVideoCons=callView.findViewById(R.id.myVideoCons);
            //opentok Variables for api call
            String openTok_API_KEY = "46714972";

            //String openTok_SESSION_ID = "1_MX40NjcxNDk3Mn5-MTU4ODQ2MzQ0Mzk0MH5zd2xzQm0yZjdEZlZOMEtOQzVUeHUrOUR-fg";
           // String openTok_TOKEN = "T1==cGFydG5lcl9pZD00NjcxNDk3MiZzaWc9YzZhNWEwMjNhYmE4YmM1MzYyM2ZkYzJkNWZkZTQ0MzkzZWQ5MzM3NDpzZXNzaW9uX2lkPTFfTVg0ME5qY3hORGszTW41LU1UVTRPRFEyTXpRME16azBNSDV6ZDJ4elFtMHlaamRFWmxaT01FdE9RelZVZUhVck9VUi1mZyZjcmVhdGVfdGltZT0xNTg4NDYzNTE5Jm5vbmNlPTAuODA3NDA2NjU0MTc1NzYwNSZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTkxMDU1NTE5JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";

            openToksession = new Session.Builder(getContext(), openTok_API_KEY, openTok_SESSION_ID).build();
            openToksession.setSessionListener(CallViewFragment.this);
            openToksession.connect(openTok_TOKEN);

        }else{
            EasyPermissions.requestPermissions(this,"This app needs access to your camera and mic to make video calls ",RC_VIDEO_APP_PERM,perms);
        }

    }




    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {



    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }


    // 2 publishing a stream to the session
    @Override
    public void onConnected(Session session) {

        Log.i(LOG_TAG,"Opentok Session Connected");
        mPublisher=new Publisher.Builder(getContext()).build();
        mPublisher.setPublisherListener(CallViewFragment.this);

        if(mPublisher!=null){
            myVideoCons.addView(mPublisher.getView());

            if(mPublisher.getView() instanceof GLSurfaceView){

                ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
            }
            openToksession.publish(mPublisher);
        }

    }

    @Override
    public void onDisconnected(Session session) {

        Log.i(LOG_TAG,"Opentok Session Disconnected");

        if (mPublisher != null) {
            mPublisher.destroy();
        }
        if (mSubscriber != null) {
            mSubscriber.destroy();
        }

    }


    // 3 Subscribing to the stream
    @Override
    public void onStreamReceived(Session session, Stream stream) {

        Log.d(LOG_TAG,"Stream Received");

        if(mSubscriber==null){
            mSubscriber=new Subscriber.Builder(getContext(),stream).build();
            openToksession.subscribe(mSubscriber);
            friendVideoCons.addView(mSubscriber.getView(), LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        }

    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {

        Log.d(LOG_TAG,"Stream Dropped");

        if(mSubscriber!=null){
            mSubscriber=null;
            friendVideoCons.removeAllViews();
        }


    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

        Log.d(LOG_TAG,"Stream Error");

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.acceptCallCons:
                //accept Call
                acceptCallCons.setVisibility(View.GONE);
                databaseReference.child("Calls").child(activeCall.getRecieverId()).child("callStatus").setValue("picked up");

                break;

            case R.id.endcallCons:
                databaseReference.child("Calls").child(activeCall.getRecieverId()).child("callStatus").setValue("ended");

                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(myCallValueEventListener!=null){
            databaseReference.child("Calls").child(activeCall.getRecieverId()).removeEventListener(myCallValueEventListener);
        }
        phoneCallingTonePlayer.stop();
        phoneRingingTonePlayer.stop();
        timeHandler.removeCallbacks(myRunnable);

    }
}

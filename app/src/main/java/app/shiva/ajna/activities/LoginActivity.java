package app.shiva.ajna.activities;

import android.content.Intent;
import androidx.annotation.NonNull;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import app.shiva.ajna.R;
import app.shiva.ajna.model.UserLatLng;
import app.shiva.ajna.model.LocationTracking;
import app.shiva.ajna.model.User;
import app.shiva.ajna.model.UserActive;

public class LoginActivity extends FragmentActivity {
    private FirebaseAuth mAuth;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference mDatabaseReference = database.getReference().child("Users");
    private static final String TAG = "MyActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loginactivity);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        ConstraintLayout signInCons=findViewById(R.id.signInCons);


        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            signInCons.setVisibility(View.GONE);
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            startActivity(intent);
            FirebaseMessaging.getInstance().subscribeToTopic(currentUser.getUid());

        }else{
            signInCons.setVisibility(View.VISIBLE);
            GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.google_client_id))
                    .requestEmail()
                    .build();


            mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), options);


            Button googleSignin = findViewById(R.id.googleSignIn);
            googleSignin.setOnClickListener(v -> signIn());
        }




    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                if(account!=null){
                    // Signed in successfully, show authenticated UI.
                    authWithGoogle(account);
                }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }



    private ValueEventListener valueEventListener;
    private DatabaseReference dataSaveReference;


    private void authWithGoogle(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
         mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
             if(task.isSuccessful()){

                 final FirebaseUser user = mAuth.getCurrentUser();
                 assert user != null;

                 dataSaveReference =mDatabaseReference.child(user.getUid());
                 FirebaseMessaging.getInstance().subscribeToTopic(user.getUid());

                 valueEventListener= dataSaveReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            long currentDateTime = System.currentTimeMillis();
                            UserLatLng userLatLng = new UserLatLng("0","0");
                            UserActive userActive = new UserActive("true","true",String.valueOf(currentDateTime));
                            LocationTracking locationTracking=new LocationTracking("true", userLatLng);
                            User newUser = new User(user.getUid(),user.getDisplayName(),user.getEmail(),"none","public",userActive,locationTracking);
                            dataSaveReference.setValue(newUser);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                 Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                 overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                 startActivity(intent);

                 Toast toast= Toast.makeText(getApplicationContext(),
                         "Welcome "+ user.getDisplayName(), Toast.LENGTH_LONG);
                 toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 200);
                 toast.show();

             }
             else{
                 Toast.makeText(getApplicationContext(),"Auth Error",Toast.LENGTH_SHORT).show();
             }
         });
    }




    @Override
    public void onStart() {

        Log.d("LoginActivity :" , "OnStart");
        super.onStart();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("LoginActivity :" , "OnDestroy");

        if(valueEventListener!=null){
            dataSaveReference.removeEventListener(valueEventListener);
        }


    }
    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}





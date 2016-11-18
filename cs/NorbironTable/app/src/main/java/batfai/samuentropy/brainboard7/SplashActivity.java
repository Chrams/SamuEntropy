/*
 * SplashActivity.java
 *
 * Norbiron Game
 * This is a case study for creating sprites for SamuEntropy/Brainboard.
 *
 * Copyright (C) 2016, Dr. Bátfai Norbert
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Ez a program szabad szoftver; terjeszthető illetve módosítható a
 * Free Software Foundation által kiadott GNU General Public License
 * dokumentumában leírtak; akár a licenc 3-as, akár (tetszőleges) későbbi
 * változata szerint.
 *
 * Ez a program abban a reményben kerül közreadásra, hogy hasznos lesz,
 * de minden egyéb GARANCIA NÉLKÜL, az ELADHATÓSÁGRA vagy VALAMELY CÉLRA
 * VALÓ ALKALMAZHATÓSÁGRA való származtatott garanciát is beleértve.
 * További részleteket a GNU General Public License tartalmaz.
 *
 * A felhasználónak a programmal együtt meg kell kapnia a GNU General
 * Public License egy példányát; ha mégsem kapta meg, akkor
 * tekintse meg a <http://www.gnu.org/licenses/> oldalon.
 *
 * Version history:
 *
 * 0.0.1, 2013.szept.29.
 */
package batfai.samuentropy.brainboard7;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.FileNotFoundException;

class Splash extends Thread {

    SplashActivity act;
    android.graphics.drawable.AnimationDrawable anim;

    public Splash(android.graphics.drawable.AnimationDrawable anim, SplashActivity act) {
        this.anim = anim;
        this.act = act;
    }

    public void st() {
        android.content.Intent intent = new android.content.Intent();
        intent.setClass(act, NeuronGameActivity.class);
        act.startActivity(intent);
        anim.stop();
    }

    @Override
    public void run() {
        anim.start();
    }
}

/**
 *
 * @author nbatfai
 */
public class SplashActivity extends /*android.app.Activity*/ FragmentActivity
implements GoogleApiClient.OnConnectionFailedListener{


    private static final int RC_SIGN_IN = 9001;

    private Splash splash;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private android.widget.ImageButton playBtn;

    public FirebaseUser mUser;

    private GoogleApiClient mGoogleApiClient;


    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        android.widget.ImageView iv = (android.widget.ImageView) findViewById(R.id.neuronanimation);

        iv.setBackgroundResource(R.drawable.neuron_animation);

        final android.graphics.drawable.AnimationDrawable anim = (android.graphics.drawable.AnimationDrawable) iv.getBackground();

        splash = new Splash(anim, this);
        runOnUiThread(splash);

        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        final android.widget.Button authBtn = (Button)findViewById(R.id.button_auth);
        playBtn = (android.widget.ImageButton)findViewById(R.id.button_play);

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    //user signed in
                    mUser = user;
                    playBtn.setEnabled(true);
                    //splash.st();
                    //finish();
                    authBtn.setText("Sign Out");
                    /*try{
                        //java.io.InputStream is = getContentResolver().openInputStream(user.getPhotoUrl());
                        //Drawable d = Drawable.createFromStream(is,user.getPhotoUrl().toString());
                        Drawable da = Drawable.createFromPath(user.getPhotoUrl().getPath());
                        authBtn.setCompoundDrawables(da,null,null,null);
                    } catch (Exception e)
                    {
                        //Drawable d = getDrawable(R.drawable.btn_google_signin);
                        //authBtn.setCompoundDrawables(d,null,null,null);
                        Toast.makeText(SplashActivity.this, "Cannot resolve Google Photo Url!", Toast.LENGTH_SHORT).show();
                    }*/
                    //authBtn.setCompoundDrawables(user.);
                    authBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAuth.signOut();
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                            playBtn.setEnabled(false);
                            Toast.makeText(SplashActivity.this, "Bye Bye!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // user signed out
                    mUser = null;
                    authBtn.setText("Sign In");
                    authBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_google_signin,0,0,0);
                    authBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            signIn();
                        }
                    });
                    //signIn();
                }
            }
        };

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUser != null){
                    splash.st();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        splash.run();
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(SplashActivity.this, "Google Sign-in Failed!",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(SplashActivity.this, "Authentication Failed!",Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(SplashActivity.this, "Welcome back "+"!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //@Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent evt) {
        if (evt.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            //splash.st();
            if(mUser != null){
                //splash.st();
            } else {
                //signIn();
            }
        }
        return true;
    }
}

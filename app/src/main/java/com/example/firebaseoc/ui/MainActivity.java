package com.example.firebaseoc.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.firebaseoc.R;
import com.example.firebaseoc.manager.UserManager;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity{
    private static final int RC_SIGN_IN = 123;
    @BindView(R.id.loginButton) Button loginButton;
    @BindView(R.id.chatButton) Button chatButton;
    private UserManager mUserManager = UserManager.getInstance();
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleResponseAfterSignIn(requestCode,resultCode,data);
    }

    /**
     * this methode will handle the response of startSignInActivity
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the data returned by after finishing the result activity
     */
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                mUserManager.createUser();
                showSnackBar(R.string.connection_succeed);
            }else {
                if(response == null){
                    showSnackBar(R.string.error_authentication_canceled);
                }else if(response.getError() != null){
                    if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                        showSnackBar(R.string.error_no_internet);
                    }else if(response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR){
                        showSnackBar(R.string.error_unknown_error);
                    }
                }
            }
        }
    }

    /**
     * this methode is responsible of showing the snack-bar a the button of our app
     * @param stringId the id of the string that contain result String
     */
    private void showSnackBar(int stringId) {
        // TODO : fix the snack bar so that it can work with activity .
        Snackbar.make(findViewById(R.id.loginButton),getString(stringId),Snackbar.LENGTH_SHORT).show();
    }

    /**
     * using the OnClick annotation from butter knife we set onClick listener when we click on login
     * @param view the main activity
     */
    @OnClick(R.id.loginButton)
    public void logIn(View view){
        if(mUserManager.isCurrentUserLogged()){
            startProfileActivity();
        }else {
            startSignInActivity();
        }
    }
    @OnClick(R.id.chatButton)
    public void onClickChat(View view){
        if(mUserManager.isCurrentUserLogged()){
            startMentorChatActivity();
        }else {
            showSnackBar(R.string.error_not_connected);
        }
    }

    private void startMentorChatActivity() {
        Intent myIntent = new Intent(this,MentorChatActivity.class);
        startActivity(myIntent);
    }

    /**
     * this method create a new intent to start the profile Activity if the use is not logged in.
     */
    private void startProfileActivity() {
        Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
        startActivity(intent);
    }

    /**
     * this method start AuthUI of firebase that will display all the necessary authentication
     * logic and UI
     */
    private void startSignInActivity(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers =
                Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                        new AuthUI.IdpConfig.EmailBuilder().build()
                );

        // Launch the activity
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo_auth)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLoginButton();
    }

    /**
     * this update the login button text by need
     */
    private void updateLoginButton() {
        loginButton.setText(
                mUserManager.isCurrentUserLogged() ?
                        getString(R.string.button_login_text_logged):
                        getString(R.string.button_login_text_not_logged)
        );
    }
}
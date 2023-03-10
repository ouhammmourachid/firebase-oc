package com.example.firebaseoc.ui;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.firebaseoc.R;
import com.example.firebaseoc.manager.UserManager;
import com.example.firebaseoc.model.User;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class ProfileActivity extends BaseActivity{
    private UserManager mUserManager = UserManager.getInstance();
    @BindView(R.id.updateButton) Button updateButton;
    @BindView(R.id.deleteButton) Button deleteButton;
    @BindView(R.id.signOutButton) Button signOutButton;
    @BindView(R.id.usernameEditText) EditText usernameEditText;
    @BindView(R.id.emailTextView) TextView emailTextView;
    @BindView(R.id.isMentorCheckBox) CheckBox isMentorCheckbox;
    @BindView(R.id.profileImageView) ImageView profileImageView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @Override
    int getLayoutResourceId() {
        return R.layout.activity_profile;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUIWithUserData();
    }

    @OnClick(R.id.updateButton)
    public void updateProfile(){
        mProgressBar.setVisibility(View.VISIBLE);
        mUserManager.updateUsername(usernameEditText.getText().toString())
                .addOnSuccessListener(aVoid->{
                    mProgressBar.setVisibility(View.GONE);
                });
    }

    @OnCheckedChanged(R.id.isMentorCheckBox)
    public void checkboxIsMentor(boolean isChecked){
        isMentorCheckbox.setChecked(isChecked);
        mUserManager.updateIsMentor(isChecked);
    }
    @OnClick(R.id.signOutButton)
    public void signOut(){
        mUserManager.signOut(ProfileActivity.this).addOnSuccessListener(aVoid->{
            finish();
        });
    }
    @OnClick(R.id.deleteButton)
    public void deleteProfile(){
        new AlertDialog.Builder(this)
                .setMessage(R.string.popup_message_confirmation_delete_account)
                .setPositiveButton(R.string.popup_message_choice_yes,((dialogInterface, i) -> {
                    mUserManager.deleteUser(ProfileActivity.this)
                            .addOnSuccessListener(aVoid->{
                                finish();
                            });
                }))
                .setNegativeButton(R.string.popup_message_choice_no,null)
                .show();
    }

    /**
     * method allow us to display the profile information like email ,username and profile picture
     * that we will call On create methode that we render every time we create our profile  activity .
     */
    private void updateUIWithUserData(){
        if(mUserManager.isCurrentUserLogged()){
            FirebaseUser user = mUserManager.getCurrentUser();
            if(user.getPhotoUrl() != null){

                setProfilePicture(user.getPhotoUrl());
            }
            getUserData();
            setTextUserData(user);
        }
    }
    private void getUserData(){
        mUserManager.getUserData()
                .addOnSuccessListener(user -> {
                    String username = user.getUsername().isEmpty() ?
                            getString(R.string.info_no_username_found) : user.getUsername();
                    isMentorCheckbox.setChecked(user.getIsMentor());
                    usernameEditText.setText(username);
                });
    }

    /**
     * update the username and the email info in the profile layout if they are exist
     * other wise display some not found messages instead
     * @param user a Firebase use the current one
     */
    private void setTextUserData(FirebaseUser user) {
        String email = user.getEmail().isEmpty() ? getString(R.string.info_no_email_found): user.getEmail();
        emailTextView.setText(email);

        mUserManager.getUserData()
                .addOnCompleteListener(task->{
                    if(task.getResult() != null){
                        usernameEditText.setText(task.getResult().getUsername());
                    }
                })
                .addOnFailureListener(aVoid->{
                    String username = user.getDisplayName().isEmpty() ? getString(R.string.info_no_username_found) : user.getDisplayName();
                    usernameEditText.setText(username);
                });

    }

    /**
     * this methode allow us to load image from using a url and then apply circleCorpTransform
     * @param photoUrl the url of the profile image .
     */
    private void setProfilePicture(Uri photoUrl) {
        Glide.with(this)
                .load(photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(profileImageView);
    }
}

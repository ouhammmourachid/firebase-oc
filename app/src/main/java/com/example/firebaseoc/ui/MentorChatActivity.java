package com.example.firebaseoc.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.firebaseoc.R;
import com.example.firebaseoc.manager.ChatManager;
import com.example.firebaseoc.manager.UserManager;
import com.example.firebaseoc.model.Message;
import com.example.firebaseoc.ui.chat.MentorChatAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MentorChatActivity extends BaseActivity implements MentorChatAdapter.Listener {
    private MentorChatAdapter mentorChatAdapter;
    private String currentChatName;
    private Uri uriImageSelected;
    private static final String CHAT_NAME_ANDROID = "android";
    private static final String CHAT_NAME_BUG = "bug";
    private static final String CHAT_NAME_FIREBASE = "firebase";
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;
    private UserManager userManager = UserManager.getInstance();
    private ChatManager chatManager = ChatManager.getInstance();
    @BindView(R.id.androidChatButton) ImageButton  androidChatButton;
    @BindView(R.id.bugChatButton) ImageButton bugChatButton;
    @BindView(R.id.firebaseChatButton) ImageButton firebaseChatButton;
    @BindView(R.id.chatRecyclerView) RecyclerView chatRecyclerView;
    @BindView(R.id.emptyRecyclerView) TextView emptyRecyclerView;
    @BindView(R.id.sendButton) Button sendButton;
    @BindView(R.id.chatEditText) EditText chatEditText;
    @BindView(R.id.addFileButton) ImageButton addFileButton;
    @BindView(R.id.imagePreview) ImageView imagePreview;
    @Override
    int getLayoutResourceId() {
        return R.layout.activity_mentor_chat;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureRecyclerView(CHAT_NAME_ANDROID);
    }
    @OnClick(R.id.androidChatButton)
    public void onClickAndroid(){
        configureRecyclerView(CHAT_NAME_ANDROID);
    }
    @OnClick(R.id.bugChatButton)
    public void onClickBug(View view){
        configureRecyclerView(CHAT_NAME_BUG);
    }
    @OnClick(R.id.firebaseChatButton)
    public void onClickFirebase(View view){
        configureRecyclerView(CHAT_NAME_FIREBASE);
    }
    @OnClick(R.id.sendButton)
    public void sendMessage(){
        boolean canSendMessage = !chatEditText.getText().toString().isEmpty() && userManager.isCurrentUserLogged();
        if(canSendMessage){
            String messageText = chatEditText.getText().toString();
            if(imagePreview.getDrawable() == null){
                chatManager.createMessageForChat(chatEditText.getText().toString(),currentChatName);
            }else {
                chatManager.createMessageWithImageForChat(
                        messageText,
                        uriImageSelected,
                        currentChatName
                );
                imagePreview.setImageDrawable(null);
            }
            chatEditText.setText("");
        }
    }
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    @OnClick(R.id.addFileButton)
    public void addFileBtn(View view){
        if(!EasyPermissions.hasPermissions(this,PERMS)){
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.popup_title_permission_files_access),
                    RC_CHOOSE_PHOTO,
                    PERMS
            );
            return;
        }
        Intent myIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(myIntent,RC_CHOOSE_PHOTO);
    }
    private void configureRecyclerView(String chatName) {
        currentChatName = chatName;
        mentorChatAdapter = new MentorChatAdapter(
                generateOptionsForAdapter(chatManager.getAllMessageForChat(currentChatName)),
                Glide.with(this),
                this
        );
        mentorChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                chatRecyclerView.smoothScrollToPosition(mentorChatAdapter.getItemCount());
            }
        });
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(mentorChatAdapter);
    }

    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query,Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onDataChange() {
        emptyRecyclerView.setVisibility(mentorChatAdapter.getItemCount() == 0 ? View.VISIBLE:View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleResponse(requestCode,resultCode,data);
    }

    private void handleResponse(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_CHOOSE_PHOTO){
            if(resultCode == RESULT_OK){
                uriImageSelected = data.getData();
                Glide.with(this)
                        .load(uriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imagePreview);
            }else {
                Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show();
            }
        }

    }
}

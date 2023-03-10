package com.example.firebaseoc.manager;

import android.net.Uri;

import com.example.firebaseoc.repository.ChatRepository;
import com.google.firebase.firestore.Query;

public class ChatManager {
    private static volatile ChatManager instance;
    private ChatRepository mChatRepository;

    public ChatManager() {
        mChatRepository = new ChatRepository();
    }
    public static ChatManager getInstance() {
        if(instance != null){
            return instance;
        }
        synchronized (ChatManager.class){
            if(instance == null){
                instance = new ChatManager();
            }
            return instance;
        }
    }
    public Query getAllMessageForChat(String chat){
        return mChatRepository.getAllMessageForChat(chat);
    }
    public void createMessageForChat(String message,String chat){
        mChatRepository.createMessageForChat(message,chat);
    }
    public void createMessageWithImageForChat(String message, Uri imageUri,String chat){
        mChatRepository.uploadImage(imageUri,chat)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                mChatRepository.createMessageWithImageForChat(
                                        uri.toString(),
                                        message,
                                        chat
                                );
                            });
                });
    }
}

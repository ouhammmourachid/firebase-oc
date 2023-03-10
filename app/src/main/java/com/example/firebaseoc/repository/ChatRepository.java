package com.example.firebaseoc.repository;

import android.net.Uri;

import com.example.firebaseoc.manager.UserManager;
import com.example.firebaseoc.model.Message;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public final class ChatRepository {
    private final static String CHAT_COLLECTION = "chats";
    private final static String MESSAGE_COLLECTION = "messages";
    private static volatile ChatRepository instance;
    private UserManager userManager;

    public ChatRepository() {
        userManager = UserManager.getInstance();
    }
    public static ChatRepository getInstance(){
        ChatRepository result = instance;
        if(result != null){
            return result;
        }
        synchronized (ChatRepository.class){
            if (result == null){
                instance = new ChatRepository();
            }
            return instance;
        }
    }
    public CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(CHAT_COLLECTION);
    }
    public Query getAllMessageForChat(String chat){
        return getChatCollection()
                .document(chat)
                .collection(MESSAGE_COLLECTION)
                .orderBy("dateCreated")
                .limit(50);
    }
    public void createMessageForChat(String textMessage,String chatName){
        userManager.getUserData()
                .addOnSuccessListener(user -> {
                    Message message = new Message(textMessage,user);
                    getChatCollection()
                            .document(chatName)
                            .collection(MESSAGE_COLLECTION)
                            .add(message);
                });
    }

    public void createMessageWithImageForChat(String urlImage,String textMessage,String chat){
        userManager.getUserData()
                .addOnSuccessListener(user -> {
                    Message message = new Message(textMessage,user,urlImage);

                    getChatCollection()
                            .document(chat)
                            .collection(MESSAGE_COLLECTION)
                            .add(message);
                });
    }

    public UploadTask uploadImage(Uri imageUri,String chat){
        String uuid = UUID.randomUUID().toString();
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference(chat+"/"+uuid);
        return mImageRef.putFile(imageUri);
    }
}

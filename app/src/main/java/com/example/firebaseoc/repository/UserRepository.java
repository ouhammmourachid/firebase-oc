package com.example.firebaseoc.repository;


import android.content.Context;

import androidx.annotation.Nullable;

import com.example.firebaseoc.model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A class of type Singleton that will give us a new instance unique and only one
 * when we execute getInstance() methode
 */
public final class UserRepository {
    private static volatile UserRepository instance;
    private static final String COLLECTION_NAME = "users";
    private static final String USERNAME_FIELD = "username";
    private static final String IS_MENTOR_FIELD = "isMentor";

    /**
     * the definition of the constructor that will create the unique instance .
     */
    private UserRepository(){
        // TODO : logic of the Repository.
    }

    /**
     * this static function is responsible of creating single instance of the User repository singleton.
     * @return an instance of user Repository.
     */
    public static UserRepository getInstance(){
        UserRepository repository = instance;
        if(repository != null){
            return repository;
        }
        synchronized (UserRepository.class){
            if(instance == null){
                instance = new UserRepository();
            }
            return instance;
        }
    }

    /**
     * that method will give us the current user if login
     * @return Current FirebaseUser
     */
    @Nullable
    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * this method use AuthUI singleton to signOut
     * @param context
     * @return a Task object of type void.
     */
    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);
    }

    /**
     * this method use AuthUI singleton to delete user .
     * @param context
     * @return a Task object of type void.
     */
    public Task<Void> deleteUser(Context context){
        return AuthUI.getInstance().delete(context);
    }

    private CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    /**
     * this function a user after getting the information of the login user either by using google account
     * or email and then fetch it from the firebase store if exist and create a new one or update
     */
    public void createUser(){
        FirebaseUser user = getCurrentUser();
        if(user != null){
            String urlPicture = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String uid = user.getUid();

            User userCreated = new User(uid,username,urlPicture);

            Task<DocumentSnapshot> userData = getUserData();

            userData.addOnSuccessListener(documentSnapshot -> {
                userCreated.setIsMentor(false);
                if(documentSnapshot.contains(IS_MENTOR_FIELD)){
                    userCreated.setIsMentor((Boolean) documentSnapshot.getBoolean(IS_MENTOR_FIELD));
                }
                if(documentSnapshot.contains(USERNAME_FIELD)){
                    userCreated.setUsername((String) documentSnapshot.getString(USERNAME_FIELD));
                }
                getUsersCollection().document(uid).set(userCreated);
            });
        }
    }

    /**
     * this function is responsible of fetching the user information from the firebase store under type of document snapshot.
     * @return snapshot document that contain user information.
     */
    public Task<DocumentSnapshot> getUserData() {
        String uid = getCurrentUserUid();
        return uid != null ? getUsersCollection().document(uid).get() : null;
    }

    /**
     * to get the UID of the user that currently login use this function.
     * @return UID of the current user.
     */
    public String getCurrentUserUid() {
        FirebaseUser currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

    /**
     * we use this function to update the name of the user
     * @param username the new user name.
     * @return an object Task of type Void .
     */
    public Task<Void> updateUsername(String username){
        String uid = getCurrentUserUid();
        return uid != null ? getUsersCollection().document(uid).update(USERNAME_FIELD,username) : null;
    }

    /**
     * to update the boolean field of our object user that represent whether the user is mentor or not .
     * @param isMentor whether the user is mentor or not .
     */
    public void updateIsMentor(Boolean isMentor){
        String uid = getCurrentUserUid();
        if(uid != null){
            getUsersCollection().document(uid).update(IS_MENTOR_FIELD,isMentor);
        }
    }

    /**
     * this function is responsible of deleting the user in the firebase store .
     * @return task of type void if the user exist.
     */
    public Task<Void> deleteUserFromFireStore(){
        String uid = getCurrentUserUid();
        return (uid != null)? getUsersCollection().document(uid).delete():null;
    }
}

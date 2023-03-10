package com.example.firebaseoc.manager;

import android.content.Context;

import com.example.firebaseoc.model.User;
import com.example.firebaseoc.repository.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

/**
 * this package is responsible of managing the FirebaseUser an play the role of middle man
 * between UserRepository and the Activities
 */
public class UserManager {
    private static volatile UserManager instance;
    private UserRepository mUserRepository;

    public UserManager(){
        mUserRepository = UserRepository.getInstance();
    }

    /**
     *  this methode will allows to a unique instance every time we call it .
     * @return a unique instance of UserManager
     */
    public static UserManager getInstance(){
        UserManager manager = instance;
        if(manager != null){
            return instance;
        }
        synchronized (UserManager.class){
            if(manager == null){
                instance = new UserManager();
            }
            return instance;
        }
    }

    /**
     * get the current user if login
     * @return FirebaseUser the current one
     */
    public FirebaseUser getCurrentUser(){
        return mUserRepository.getCurrentUser();
    }

    /**
     * this method check if the user is logged in
     * @return true if user logged in other wise false
     */
    public boolean isCurrentUserLogged(){
        return (getCurrentUser() != null);
    }

    /**
     * this method use user Repository to signOut
     * @param context
     * @return
     */
    public Task<Void> signOut(Context context){
        return mUserRepository.signOut(context);
    }

    /**
     * this method use user Repository to delete user .
     * @param context
     * @return
     */
    public Task<Void> deleteUser(Context context){
        return mUserRepository.deleteUserFromFireStore()
                .addOnSuccessListener(task ->{
                    mUserRepository.deleteUser(context);
                });
    }

    /**
     * this function use the user repository to fetch the  user from the task returned by user repository.
     * @return task of type user.
     */
    public Task<User> getUserData(){
        return mUserRepository.getUserData().continueWith(task ->
            task.getResult().toObject(User.class)
        );
    }

    /**
     * update the username by using the user repository.
     * @param username the new username.
     * @return task of type viod.
     */
    public Task<Void> updateUsername(String username){
        return mUserRepository.updateUsername(username);
    }

    /**
     * update the boolean field is-mentor.
     * @param isMentor whether if user is mentor or not.
     */
    public void updateIsMentor(Boolean isMentor){
         mUserRepository.updateIsMentor(isMentor);
    }

    /**
     * this method is responsible of creating a use in the firebase store.
     */
    public void createUser(){
        mUserRepository.createUser();
    }
}

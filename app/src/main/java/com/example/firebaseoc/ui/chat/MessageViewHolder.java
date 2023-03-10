package com.example.firebaseoc.ui.chat;

import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.firebaseoc.R;
import com.example.firebaseoc.databinding.ItemChatBinding;
import com.example.firebaseoc.model.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessageViewHolder extends RecyclerView.ViewHolder{
    private ItemChatBinding binding;
    private boolean isSender;
    private final int colorCurrentUser ;
    private final int colorRemoteUser ;

    public MessageViewHolder(@NonNull View itemView,boolean isSender) {
        super(itemView);
        this.isSender = isSender;
        binding = ItemChatBinding.bind(itemView);

        //set default colors:
        colorCurrentUser = ContextCompat.getColor(itemView.getContext(), R.color.colorAccent);
        colorRemoteUser = ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary);
    }
    public void updateWithMessage(Message message, RequestManager glide){
        // update message.
        binding.messageTextView.setText(message.getMessage());
        binding.messageTextView.setTextAlignment(isSender? View.TEXT_ALIGNMENT_TEXT_END:View.TEXT_ALIGNMENT_TEXT_START);

        // update date.
        if(message.getDateCreated() != null){
            binding.dateTextView.setText(convertDateToHour(message.getDateCreated()));
        }

        // update isMentor
        if(message.getUserSender().getIsMentor() != null){
            Log.e("TAG",message.getUserSender().getIsMentor()? "is Mentor":"Not a mentor");
            binding.profileIsMentor.setVisibility(message.getUserSender().getIsMentor()? View.VISIBLE:View.GONE);
        }

        //update profile picture.
        if(message.getUserSender().getUrlPicture() != null){
            glide.load(message.getUserSender().getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.profileImage);
        }

        // update image sent.
        if(message.getUrlImage() != null){
            glide.load(message.getUrlImage())
                    .into(binding.senderImageView);
            binding.senderImageView.setVisibility(View.VISIBLE);
        }else {
            binding.senderImageView.setVisibility(View.GONE);
        }
        updateLayoutFromSenderType();
    }

    private void updateLayoutFromSenderType() {
        //update message bubble color background.
        ((GradientDrawable)binding.messageTextContainer.getBackground()).setColor(isSender? colorCurrentUser:colorRemoteUser);
        binding.messageTextContainer.requestLayout();

        if(!isSender){
            updateProfileContainer();
            updateMessageContainer();
        }else {
            binding.profileContainer.setVisibility(View.GONE);
        }
    }

    private void updateMessageContainer() {
        ConstraintLayout.LayoutParams messageContainerLayoutParams =
                (ConstraintLayout.LayoutParams) binding.messageContainer.getLayoutParams();
        messageContainerLayoutParams.startToStart =ConstraintLayout.LayoutParams.UNSET;
        messageContainerLayoutParams.endToStart =ConstraintLayout.LayoutParams.UNSET;
        messageContainerLayoutParams.startToEnd =binding.profileContainer.getId();
        messageContainerLayoutParams.endToEnd =ConstraintLayout.LayoutParams.PARENT_ID;
        messageContainerLayoutParams.horizontalBias = 0.0f;
        binding.messageContainer.requestLayout();

        LinearLayout.LayoutParams messageTextLayoutParams =
                (LinearLayout.LayoutParams) binding.messageTextContainer.getLayoutParams();
        messageTextLayoutParams.gravity = Gravity.START;
        binding.messageTextContainer.requestLayout();

        LinearLayout.LayoutParams dateLayoutParams =
                (LinearLayout.LayoutParams) binding.dateTextView.getLayoutParams();
        dateLayoutParams.gravity = Gravity.BOTTOM | Gravity.START;
        binding.dateTextView.requestLayout();
    }

    private void updateProfileContainer() {
        ConstraintLayout.LayoutParams profileContainerLayoutParams =
                (ConstraintLayout.LayoutParams) binding.profileContainer.getLayoutParams();
        profileContainerLayoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET;
        profileContainerLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        binding.profileContainer.requestLayout();
    }

    private String convertDateToHour(Date date){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(date);
    }
}

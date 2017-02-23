package com.smart.rchat.smart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.smart.rchat.smart.util.AppUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by nishant on 14.02.17.
 */

public class ForgotPasswordActivity extends BaseActivity {

    @BindView(R.id.edEmail)
    public EditText edEmail;

    @BindView(R.id.toolBar)
    public Toolbar toolBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.btReqPwd)
    public void onPassWordRequest(View view){
        String email = edEmail.getText().toString();
        if(!AppUtil.isValidEmail(email)){
            makeToast("email is invalid");
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    makeToast("please check your mail");
                    finish();
                }else{
                    Exception ex = task.getException();
                    String errorMessage = "Something went wrong";
                    if(ex != null && ex instanceof FirebaseException){
                        FirebaseException firebaseException = (FirebaseException) ex;
                        errorMessage = firebaseException.getMessage();
                    }
                    makeToast(errorMessage);
                }
            }
        });
    }

}

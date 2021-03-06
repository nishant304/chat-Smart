package com.smart.rchat.smart;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.smart.rchat.smart.interfaces.ResponseListener;
import com.smart.rchat.smart.models.User;
import com.smart.rchat.smart.util.AppUtil;
import com.smart.rchat.smart.util.Preferenceutil;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by nishant on 1/25/2017.
 */

public class PhoneVerifyActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.edPhone)
    public EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_verify_screen);
    }

    @OnClick(R.id.btPhoneSubmit)
    public void onClick(View v) {
        if (phone.getText().toString().length() < 8) {
            makeToast("phone number length is less than 8");
            return;
        }

        final String phoneNo = phone.getText().toString().trim().replace(" ", "");
        getNetworkClient().updatePhoneNo(phoneNo, new User(AppUtil.getUserId(), "", phoneNo, ""),
                new UpdateUserResponseListener(this));
    }

    private static class UpdateUserResponseListener implements ResponseListener {

        private WeakReference<PhoneVerifyActivity> weakReference;

        private UpdateUserResponseListener(PhoneVerifyActivity profileActivity) {
            weakReference = new WeakReference<PhoneVerifyActivity>(profileActivity);
        }

        @Override
        public void onError(Exception error) {
            final PhoneVerifyActivity profileActivity = (PhoneVerifyActivity) weakReference.get();
            if (profileActivity == null) {
                return;
            }
            profileActivity.makeToast(error.getMessage());
        }

        @Override
        public void onSuccess(JSONObject jsonObject) {
            final PhoneVerifyActivity profileActivity = (PhoneVerifyActivity) weakReference.get();
            if (profileActivity == null) {
                return;
            }
            Preferenceutil.saveUserID(profileActivity);
            profileActivity.makeToast("phone verification complete");
            profileActivity.finish();
        }
    }

}

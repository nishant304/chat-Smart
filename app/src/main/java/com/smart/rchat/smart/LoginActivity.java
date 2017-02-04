package com.smart.rchat.smart;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.rchat.smart.services.ContactsListenerService;
import com.smart.rchat.smart.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created by nishant on 1/22/2017.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_READ_CONTACTS = 1;
    @BindView(R.id.edLoginEmail)
    public AutoCompleteTextView email;

    @BindView(R.id.edPassword)
    public EditText password;

    @BindView(R.id.btLogin)
    public Button login;

    @BindView(R.id.btRegister)
    public Button register;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        populateAutoComplete();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    showDialog();
                    sendHome();
                }
            }
        };

    }

    private void sendHome() {
        //startService();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/Users");
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeEventListener(this);
                hideDialog();
                Intent intent = new Intent(LoginActivity.this, dataSnapshot.exists() ?
                        HomeActivity.class : PhoneVerifyActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideDialog();
                makeToast("Please login");
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,ContactsContract.Data.CONTENT_URI,ProfileQuery.PROJECTION,
                ContactsContract.Data.MIMETYPE + "=? ",new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(cursor.getColumnIndex(Email.ADDRESS)));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        email.setAdapter(adapter);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }


    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                Email.ADDRESS,
                Email.IS_PRIMARY,
        };
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btRegister) {
            createAccount(email.getText().toString(), password.getText().toString());
        } else {
            signIn(email.getText().toString(), password.getText().toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }
        showDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            String code = "something went wrong";
                            if (task.getException() instanceof FirebaseAuthException) {
                                FirebaseAuthException firebaseAuthException = (FirebaseAuthException) task.getException();
                                code = firebaseAuthException.getErrorCode();
                            }
                            hideDialog();
                            Toast.makeText(LoginActivity.this, code,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }
        showDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            hideDialog();
                            String code = "something went wrong";
                            if (task.getException() instanceof FirebaseAuthException) {
                                FirebaseAuthException firebaseAuthException = (FirebaseAuthException) task.getException();
                                code = firebaseAuthException.getErrorCode();
                            }
                            makeToast(code);
                        }
                    }
                });

    }

    private boolean validateForm() {
        boolean valid = true;

        if (!AppUtil.isValidEmail(this.email.getText().toString())) {
            makeToast("Please enter proper email address");
            this.email.setError("Required.");
            valid = false;
        } else {
            this.email.setError(null);
        }

        String password = this.password.getText().toString();
        if (TextUtils.isEmpty(password) || password.length() < 8) {
            if (TextUtils.isEmpty(password)) {
                this.password.setError("Required.");
            } else {
                makeToast("password length must be at least 8 chars ");
                this.password.getText().clear();
            }
            valid = false;
        } else {
            this.password.setError(null);
        }

        return valid;
    }

}

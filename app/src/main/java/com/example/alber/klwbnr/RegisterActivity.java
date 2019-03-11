package com.example.alber.klwbnr;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout DisplayName;
    private TextInputLayout Email;
    private TextInputLayout Password;
    private Button CreateBtn;

    private Toolbar Toolbar;
    private DatabaseReference Database;
    private ProgressDialog RegProgress;        //ProgressDialog
    private FirebaseAuth Auth;                   //Firebase Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Toolbar Set
        Toolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(Toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // back arrow

        RegProgress = new ProgressDialog(this);

        Auth = FirebaseAuth.getInstance();


        // Android Fields

        DisplayName = (TextInputLayout) findViewById(R.id.reg_name);
        Email = (TextInputLayout) findViewById(R.id.register_email);
        Password = (TextInputLayout) findViewById(R.id.register_password);
        CreateBtn = (Button) findViewById(R.id.register_create_btn);

        CreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_name = DisplayName.getEditText().getText().toString();
                String email = Email.getEditText().getText().toString();
                String password = Password.getEditText().getText().toString();

                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    RegProgress.setTitle("Registering User");
                    RegProgress.setMessage("Please wait while we create your account!");
                    RegProgress.setCanceledOnTouchOutside(false);
                    RegProgress.show();

                    register_user(display_name, email, password);

                }
            }
        });


    }

    private void register_user(final String display_name, String email, String password) {

        Auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    Database = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", display_name);
                    userMap.put("device_token", device_token);

                    Database.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                RegProgress.dismiss();

                                Intent mainIntent = new Intent(RegisterActivity.this, UsersActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();

                            }

                        }
                    });

                } else {
                    String error = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        error = "Weak Password!     ಠ~ಠ  ";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        error = "Invalid Email    (ง ͠° ͟ل͜ ͡°)ง   ";
                    } catch (FirebaseAuthUserCollisionException e) {
                        error = "Existing account";
                    } catch (Exception e) {
                        error = "Unknown error!";
                        e.printStackTrace();
                    }

                    RegProgress.hide();
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();

                }

            }
        });

    }
}
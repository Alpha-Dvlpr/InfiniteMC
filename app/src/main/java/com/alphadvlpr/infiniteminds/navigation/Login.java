package com.alphadvlpr.infiniteminds.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private EditText etUser, etPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private ActionMenuItemView itemTrending, itemInfo;
    private BottomAppBar loginBar;
    private FloatingActionButton fabSearch;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.login);

        final Button login = findViewById(R.id.loginButton),
                changePassword = findViewById(R.id.loginChangePassword);
        Switch switchShowPassword = findViewById(R.id.loginShowPassword);
        etUser = findViewById(R.id.loginEmail);
        etPassword = findViewById(R.id.loginPassword);
        progressBar = findViewById(R.id.progressBar);
        fabSearch = findViewById(R.id.loginSearchFAB);
        itemInfo = findViewById(R.id.menuAbout);
        itemTrending = findViewById(R.id.menuTrend);
        loginBar = findViewById(R.id.loginBottomAppBar);

        setActions();

        progressBar.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();

        switchShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){ etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); }
                else{ etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                boolean errUser = false, errPass = false;
                if (etUser.getText().toString().isEmpty()) { errUser = true; }

                if (etPassword.getText().toString().isEmpty()) { errPass = true; }

                if (!errPass && !errUser) {
                    progressBar.setVisibility(View.VISIBLE);
                    login.setVisibility(View.GONE);

                    auth.signInWithEmailAndPassword(etUser.getText().toString(), etPassword.getText().toString())
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        makeToast("WELCOME!");
                                        passData(etUser.getText().toString(), new Intent(Login.this, MainInterface.class));
                                    } else { makeToast("ERROR WHILE LOGGING IN!!"); }

                                    etUser.setText("");
                                    etPassword.setText("");
                                    login.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            });

                } else if (errPass && errUser) { makeToast("BOTH FIELD ARE REQUIRED"); }
            }
        });


        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String selectedEmail;

                if(etUser.getText().toString().isEmpty()){ makeToast("YOU MUST TYPE AN EMAIL"); }
                else{
                    selectedEmail = etUser.getText().toString();
                    auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(selectedEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){ makeToast("EMAIL SENT TO '" + selectedEmail + "'"); }
                                    else{ makeToast("COULD NOT SEND EMAIL"); }
                                }
                            });
                }
            }
        });
    }

    private void passData(String mail, final Intent i){
        mDatabase.collection("users")
                .document(mail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User aux = documentSnapshot.toObject(User.class);

                        i.putExtra("email", Objects.requireNonNull(aux).getEmail());
                        i.putExtra("nickname", aux.getNickname());
                        i.putExtra("published", aux.getPublished());
                        i.putExtra("admin", aux.getAdmin());

                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                });
    }

    private void setActions(){
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Search.class));
                finish();
            }
        });

        loginBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Home.class));
                finish();
            }
        });

        itemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Info.class));
                finish();
            }
        });

        itemTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Trending.class));
                finish();
            }
        });
    }

    private void makeToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

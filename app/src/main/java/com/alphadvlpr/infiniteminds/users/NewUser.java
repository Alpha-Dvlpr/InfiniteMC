package com.alphadvlpr.infiniteminds.users;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class manages the option to create a new user.
 *
 * @author AlphaDvlp.
 */
public class NewUser extends AppCompatActivity {

    private EditText editMail;
    private EditText editPassword;
    private EditText editNickname;
    private EditText editPasswordConfirm;
    private TextView textMail;
    private TextView textNick;
    private TextView textPass;
    private String mail;
    private String nick;
    private String pass;
    private String passConfirm;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private Switch showPasswords;
    private Button newUser;

    /**
     * This method initializes all the views on this Activity.
     *
     * @param savedInstanceState The previous state of the activity if saved.
     * @author AlphaDvlpr.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user);

        newUser = findViewById(R.id.newUserCreate);
        editMail = findViewById(R.id.newUserEmail);
        editNickname = findViewById(R.id.newUserNickname);
        editPassword = findViewById(R.id.newUserPassword);
        textMail = findViewById(R.id.num);
        textPass = findViewById(R.id.nup);
        textNick = findViewById(R.id.nun);
        editPasswordConfirm = findViewById(R.id.newUserPasswordConfirm);
        showPasswords = findViewById(R.id.newUserShowPasswords);
        firebaseAuth = FirebaseAuth.getInstance();

        setActions();
    }

    /**
     * This method sets the actions for all the buttons of the view.
     *
     * @author AlphaDvlpr.
     */
    protected void setActions() {
        showPasswords.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    editPasswordConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editPasswordConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail = editMail.getText().toString();
                nick = editNickname.getText().toString();
                pass = editPassword.getText().toString();
                passConfirm = editPasswordConfirm.getText().toString();

                boolean passError = false;

                if (mail.isEmpty()) {
                    textMail.setTextColor(Color.RED);
                } else {
                    textMail.setTextColor(Color.BLACK);
                }

                if (nick.isEmpty()) {
                    textNick.setTextColor(Color.RED);
                } else {
                    textNick.setTextColor(Color.BLACK);
                }

                if (pass.isEmpty() || passConfirm.isEmpty()) {
                    textPass.setTextColor(Color.RED);
                } else {
                    if (!pass.equals(passConfirm)) {
                        textPass.setTextColor(Color.RED);
                        passError = true;
                    } else {
                        textPass.setTextColor(Color.BLACK);
                        passError = false;
                    }
                }

                if (pass.isEmpty() || mail.isEmpty() || nick.isEmpty() || passConfirm.isEmpty()) {
                    makeToast("ALL FIELDS ARE REQUIRED");
                } else if (passError) {
                    makeToast("PASSWORDS DO NOT MATCH");
                } else {
                    final User user = new User(mail, nick, 0L, false);

                    firebaseAuth
                            .createUserWithEmailAndPassword(mail, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mDatabase.collection("users").document(mail)
                                                .set(user)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        makeToast("USER CREATED SUCCESSFULLY");
                                                        FirebaseAuth.getInstance().signOut();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        makeToast("ERROR WHILE SAVING USER DATA");
                                                    }
                                                });
                                    } else {
                                        makeToast("ERROR CREATING THE USER, MAYBE IT ALREADY EXISTS");
                                    }
                                }
                            });
                }
            }
        });
    }

    /**
     * Method to show a Toast notification on the current view.
     *
     * @param msg The message to be displayed.
     * @author AlphaDvlpr.
     */
    protected void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

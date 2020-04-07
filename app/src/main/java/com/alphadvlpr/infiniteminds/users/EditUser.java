package com.alphadvlpr.infiniteminds.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alphadvlpr.infiniteminds.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * This class manages the option to edit any user information.
 *
 * @author AlphaDvlpr.
 */
public class EditUser extends AppCompatActivity {

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private Button changeAdmin;
    private Button changeNickname;
    private EditText newNickname;
    private Switch admin;
    private TextView newEmail;
    private Intent prev;

    /**
     * This method initializes all the views on this Activity.
     *
     * @param savedInstanceState The previous state of the activity if saved.
     * @author AlphaDvlpr.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user);

        changeAdmin = findViewById(R.id.editUserChangeAdmin);
        changeNickname = findViewById(R.id.editUserChangeNickname);
        newNickname = findViewById(R.id.editUserNickname);
        admin = findViewById(R.id.editUserIsAdmin);
        newEmail = findViewById(R.id.editUserEmail);
        prev = getIntent();

        setInformationToView();
        setActions();
    }

    /**
     * This method gets the information from the intent and displays it on the correct view.
     *
     * @author AlphaDvlpr.
     */
    protected void setInformationToView() {
        newEmail.setText(prev.getStringExtra("email"));
        newNickname.setText(prev.getStringExtra("nickname"));
        admin.setChecked(prev.getBooleanExtra("admin", false));
    }

    /**
     * This method sets the actions for all the buttons of the view.
     *
     * @author AlphaDvlpr.
     */
    protected void setActions() {
        changeAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.collection("users").document(Objects.requireNonNull(prev.getStringExtra("email")))
                        .update("admin", admin.isChecked())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                makeToast("ADMIN STATUS UPDATED SUCCESSFULLY");
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeToast("ERROR WHILE UPDATING ADMIN STATUS");
                            }
                        });
            }
        });

        changeNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nick = newNickname.getText().toString();

                if (nick.isEmpty()) {
                    makeToast("NICKNAME CAN'T BE EMPTY");
                } else {
                    mDatabase.collection("users").document(Objects.requireNonNull(prev.getStringExtra("email")))
                            .update("nickname", nick)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    makeToast("NICKNAME UPDATED SUCCESSFULLY");
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    makeToast("ERROR WHILE UPDATING NICKNAME");
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

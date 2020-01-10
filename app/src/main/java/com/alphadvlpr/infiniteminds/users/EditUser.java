package com.alphadvlpr.infiniteminds.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alphadvlpr.infiniteminds.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class EditUser extends AppCompatActivity {

    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user);

        Button changeAdmin = findViewById(R.id.editUserChangeAdmin),
                changeNickname = findViewById(R.id.editUserChangeNickname);
        final EditText newNickname = findViewById(R.id.editUserNickname);
        final Switch admin = findViewById(R.id.editUserIsAdmin);
        TextView newEmail = findViewById(R.id.editUserEmail);

        final Intent prev = getIntent();

        newEmail.setText(prev.getStringExtra("email"));
        newNickname.setText(prev.getStringExtra("nickname"));
        admin.setChecked(prev.getBooleanExtra("admin", false));

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
                            public void onFailure(@NonNull Exception e) { makeToast("ERROR WHILE UPDATING ADMIN STATUS"); }
                        });
            }
        });

        changeNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nick = newNickname.getText().toString();

                if (nick.isEmpty()){ makeToast("NICKNAME CAN'T BE EMPTY"); }
                else{
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
                                public void onFailure(@NonNull Exception e) { makeToast("ERROR WHILE UPDATING NICKNAME"); }
                            });
                }
            }
        });
    }

    private void makeToast(String msg){ Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }
}

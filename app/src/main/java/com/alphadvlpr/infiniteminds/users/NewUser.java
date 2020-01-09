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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewUser extends AppCompatActivity {

    private EditText editMail, editPassword, editNickname, editPasswordConfirm;
    private TextView textMail, textNick, textPass;
    private String mail, nick, pass, passConfirm;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user);

        Button newUser = findViewById(R.id.newUserCreate);
        editMail = findViewById(R.id.newUserEmail);
        editNickname = findViewById(R.id.newUserNickname);
        editPassword = findViewById(R.id.newUserPassword);
        textMail = findViewById(R.id.num);
        textPass = findViewById(R.id.nup);
        textNick = findViewById(R.id.nun);
        editPasswordConfirm = findViewById(R.id.newUserPasswordConfirm);
        Switch showPasswords = findViewById(R.id.newUserShowPasswords);

        firebaseAuth = FirebaseAuth.getInstance();

        showPasswords.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    editPasswordConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
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

                if(mail.isEmpty()){ textMail.setTextColor(Color.RED); }
                else{ textMail.setTextColor(Color.BLACK); }

                if(nick.isEmpty()){ textNick.setTextColor(Color.RED); }
                else{ textNick.setTextColor(Color.BLACK); }

                if(pass.isEmpty() || passConfirm.isEmpty()){ textPass.setTextColor(Color.RED); }
                else{
                    if(!pass.equals(passConfirm)){
                        textPass.setTextColor(Color.RED);
                        passError = true;
                    }else{
                        textPass.setTextColor(Color.BLACK);
                        passError = false;
                    }
                }

                if(pass.isEmpty() || mail.isEmpty() || nick.isEmpty() || passConfirm.isEmpty()){ makeToast("Todos los campos son abligatorios"); }
                else if(passError){ makeToast("Las contraseñas no coinciden"); }
                else{
                    final User user = new User(mail, nick, 0L, false);

                    firebaseAuth
                            .createUserWithEmailAndPassword(mail, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        mDatabase.collection("users").document(mail)
                                                .set(user)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        makeToast("Usuario creado correctamente");
                                                        FirebaseAuth.getInstance().signOut();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) { makeToast("Error al guardar la informacion del usuario"); }
                                                });
                                    }else{ makeToast("Error al crear el usuario, puede que ya exista"); }
                                }
                            });
                }
            }
        });
    }

    private void makeToast(String msg){ Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }
}

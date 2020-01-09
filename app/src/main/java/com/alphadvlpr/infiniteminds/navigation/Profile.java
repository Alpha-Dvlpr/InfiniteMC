package com.alphadvlpr.infiniteminds.navigation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alphadvlpr.infiniteminds.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Profile extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        final EditText editNewNick = findViewById(R.id.profileNewNickname),
                editNewMail = findViewById(R.id.profileNewEmail),
                editConfirmNewMail = findViewById(R.id.profileNewEmailConfirm);
        Button buttonChangePassword = findViewById(R.id.profileChangePassword),
                buttonChangeNickname = findViewById(R.id.profileChangeNickname),
                buttonChangeEmail = findViewById(R.id.profileChangeEmail),
                buttonDeleteAccount = findViewById(R.id.profileDeleteAccount);
        TextView published = findViewById(R.id.profilePublished),
                current = findViewById(R.id.pcu);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        final Intent prev = getIntent();

        published.setText("Artículos publicados: " + prev.getLongExtra("published", -1L));
        current.setText("USUARIO ACTUAL\n" + prev.getStringExtra("email"));

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(Objects.requireNonNull(user.getEmail()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    makeToast("Email enviado a '" + user.getEmail() + "'");
                                    auth.signOut();
                                    finish();
                                }
                                else{ makeToast("No se ha podido enviar el email"); }
                            }
                        });
            }
        });

        buttonChangeNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNickname = editNewNick.getText().toString();

                if(newNickname.isEmpty()){ makeToast("Debes introducir un nickname"); }
                else{
                    mDatabase
                            .collection("users")
                            .document(Objects.requireNonNull(prev.getStringExtra("email")))
                            .update("nickname", newNickname)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    makeToast("Nickname actualizado con éxito");
                                    editNewNick.setText("");
                                    FirebaseAuth.getInstance().signOut();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) { makeToast("Error al actualizar el nickname"); }
                            });
                }
            }
        });

        buttonChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newMail = editNewMail.getText().toString();
                final String confirmEmail = editConfirmNewMail.getText().toString();

                if(newMail.isEmpty() || confirmEmail.isEmpty()){ makeToast("Debes introducir un email."); }
                else if(!newMail.equals(confirmEmail)){ makeToast("Los emails no coinciden"); }
                else{
                    user.updateEmail(newMail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        makeToast("Email cambiado a '" + newMail + "'");
                                        editNewMail.setText("");
                                        editConfirmNewMail.setText("");
                                        FirebaseAuth.getInstance().signOut();
                                        finish();
                                    } else{ makeToast("No se ha podido cambiar el email"); }
                                }
                            });
                }
            }
        });

        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layoutInside = new LinearLayout(v.getContext());
                layoutInside.setOrientation(LinearLayout.VERTICAL);
                layoutInside.setPadding(10, 10, 10, 10);

                final EditText email = new EditText(v.getContext());
                final EditText password = new EditText(v.getContext());
                final Switch showPassword = new Switch(v.getContext());

                email.setHint("Email");
                password.setHint("Contraseña");
                showPassword.setText(R.string.show_password);

                password.setTransformationMethod(PasswordTransformationMethod.getInstance());

                showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) { password.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); }
                        else { password.setTransformationMethod(PasswordTransformationMethod.getInstance()); }
                    }
                });

                layoutInside.addView(email);
                layoutInside.addView(password);
                layoutInside.addView(showPassword);

                final AlertDialog dialogInside = new AlertDialog.Builder(Profile.this)
                        .setTitle("CONFIRMA TUS CREDENCIALES")
                        .setView(layoutInside)
                        .setCancelable(false)
                        .setPositiveButton("BORRAR", null)
                        .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
                        })
                        .create();

                dialogInside.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button buttonPos = (dialogInside).getButton(AlertDialog.BUTTON_POSITIVE);

                        buttonPos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String mail = email.getText().toString();
                                String pass = password.getText().toString();

                                if (mail.isEmpty() || pass.isEmpty()) { makeToast("Debes introducir tu email y tu contraseña"); }
                                else {
                                    final FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
                                    AuthCredential credential = EmailAuthProvider.getCredential(mail, pass);

                                    Objects.requireNonNull(current)
                                            .reauthenticate(credential)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        mDatabase.collection("users").document(mail)
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        current.delete()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            makeToast("Todos los datos borrados con éxito");
                                                                                            dialogInside.cancel();

                                                                                            Intent toHome = new Intent(Profile.this, Home.class);
                                                                                            toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                            toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                            toHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                            startActivity(toHome);
                                                                                            finish();
                                                                                        } else { makeToast("Error al eliminar la cuenta"); }
                                                                                    }
                                                                                });
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) { makeToast("No se han podido eliminar los datos del servidor"); }
                                                                });
                                                    } else { makeToast("Error al iniciar sesión"); }
                                                }
                                            });
                                }
                            }
                        });
                    }
                });
                dialogInside.show();
            }
        });
    }

    private void makeToast(String msg){ Toast.makeText(this, msg, Toast.LENGTH_LONG).show(); }
}

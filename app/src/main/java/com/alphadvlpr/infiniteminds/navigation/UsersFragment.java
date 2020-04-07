package com.alphadvlpr.infiniteminds.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * This class manages the Users Fragment.
 *
 * @author AlphaDvlpr.
 */
public class UsersFragment extends Fragment {

    private EditText etUser;
    private EditText etPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
    private Button login;
    private Button changePassword;
    private Switch switchShowPassword;
    private Context context;
    private Activity activity;

    /**
     * This method loads a custom view into a container to show it to the user
     *
     * @param inflater           The tool to place the view inside the container.
     * @param container          The container where the view will be displayed.
     * @param savedInstanceState The previous state of the activity if it was saved.
     * @return Returns the view to be loaded.
     * @author AlphaDvlpr.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        login = view.findViewById(R.id.loginButton);
        changePassword = view.findViewById(R.id.loginChangePassword);
        switchShowPassword = view.findViewById(R.id.loginShowPassword);
        etUser = view.findViewById(R.id.loginEmail);
        etPassword = view.findViewById(R.id.loginPassword);
        progressBar = view.findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();

        context = getContext();
        activity = getActivity();

        progressBar.setVisibility(View.GONE);
        setActions();

        return view;
    }

    /**
     * This method gets the information of the logged user and starts the new
     * activity giving the Intent that information.
     *
     * @param mail The email of the logged user.
     * @param i    The intent to add the data.
     * @author AlphaDvlpr.
     */
    private void passData(String mail, final Intent i) {
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
                        activity.finish();
                    }
                });
    }

    /**
     * This method sets the actions for the buttons of the view.
     *
     * @author AlphaDvlpr.
     */
    protected void setActions() {
        switchShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                boolean errUser = false, errPass = false;
                if (etUser.getText().toString().isEmpty()) {
                    errUser = true;
                }

                if (etPassword.getText().toString().isEmpty()) {
                    errPass = true;
                }

                if (!errPass && !errUser) {
                    progressBar.setVisibility(View.VISIBLE);
                    login.setVisibility(View.GONE);

                    auth.signInWithEmailAndPassword(etUser.getText().toString(), etPassword.getText().toString())
                            .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        makeToast("WELCOME!");
                                        passData(etUser.getText().toString(), new Intent(context, MainInterface.class));
                                    } else {
                                        makeToast("ERROR WHILE LOGGING IN!!");
                                    }

                                    etUser.setText("");
                                    etPassword.setText("");
                                    login.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            });

                } else if (errPass && errUser) {
                    makeToast("BOTH FIELD ARE REQUIRED");
                }
            }
        });


        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String selectedEmail;

                if (etUser.getText().toString().isEmpty()) {
                    makeToast("YOU MUST TYPE AN EMAIL");
                } else {
                    selectedEmail = etUser.getText().toString();
                    auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(selectedEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        makeToast("EMAIL SENT TO '" + selectedEmail + "'");
                                    } else {
                                        makeToast("COULD NOT SEND EMAIL");
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
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}

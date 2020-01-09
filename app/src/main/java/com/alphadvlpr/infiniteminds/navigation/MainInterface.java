package com.alphadvlpr.infiniteminds.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alphadvlpr.infiniteminds.R;
import com.alphadvlpr.infiniteminds.articles.ArticlesList;
import com.alphadvlpr.infiniteminds.articles.NewArticle;
import com.alphadvlpr.infiniteminds.categories.CategoriesList;
import com.alphadvlpr.infiniteminds.users.UsersList;
import com.google.firebase.auth.FirebaseAuth;

public class MainInterface extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.main_interface);

        final Button buttonUsers = findViewById(R.id.mainInterfaceUsers);
        Button buttonEdit = findViewById(R.id.mainInterfaceEdit),
                buttonPublish = findViewById(R.id.mainInterfacePublish),
                buttonSignOut = findViewById(R.id.mainInterfaceSignOut),
                buttonProfile = findViewById(R.id.mainInterfaceProfile),
                buttonCategories = findViewById(R.id.mainInterfaceCategories);
        final TextView currentUserEmail = findViewById(R.id.mainInterfaceCurrentUser);

        Intent prev = getIntent();

        final String email = prev.getStringExtra("email"),
                nickname = prev.getStringExtra("nickname");
        final Long published = prev.getLongExtra("published", -1L);
        final boolean admin = prev.getBooleanExtra("admin", false);

        currentUserEmail.setText("USUARIO ACTUAL\n" + email);

        if(!admin){
            buttonUsers.setVisibility(View.GONE);
            buttonCategories.setVisibility(View.GONE);
        }

        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                makeToast("Esperamos volver a verte!");

                Intent intent = new Intent(MainInterface.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        buttonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toNew = new Intent(MainInterface.this, NewArticle.class);
                toNew.putExtra("email", email);
                startActivity(toNew);
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(MainInterface.this, ArticlesList.class)); }
        });

        buttonUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEditUsers = new Intent(MainInterface.this, UsersList.class);
                toEditUsers.putExtra("email", email);
                startActivity(toEditUsers);
            }
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toProfile = new Intent(MainInterface.this, Profile.class);
                toProfile.putExtra("published", published);
                toProfile.putExtra("email", email);
                toProfile.putExtra("nickname", nickname);
                startActivity(toProfile);
            }
        });

        buttonCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(MainInterface.this, CategoriesList.class)); }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MainInterface.this, Home.class));
        finish();
    }

    private void makeToast(String msg){ Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }
}

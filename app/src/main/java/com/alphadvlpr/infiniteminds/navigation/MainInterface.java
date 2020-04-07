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

/**
 * This class manages the main interface of a logged user.
 *
 * @author AlphaDvlpr.
 */
public class MainInterface extends AppCompatActivity {

    private String email;
    private String nickname;
    private Long published;
    private boolean admin;
    private Intent prev;
    private Button buttonUsers;
    private Button buttonEdit;
    private Button buttonPublish;
    private Button buttonSignOut;
    private Button buttonProfile;
    private Button buttonCategories;
    private TextView currentUserEmail;

    /**
     * This method initializes all the views on this Activity.
     *
     * @param savedInstanceState The previous saved state of the activity.
     * @author AlphaDvlpr.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.main_interface);

        buttonUsers = findViewById(R.id.mainInterfaceUsers);
        buttonEdit = findViewById(R.id.mainInterfaceEdit);
        buttonPublish = findViewById(R.id.mainInterfacePublish);
        buttonSignOut = findViewById(R.id.mainInterfaceSignOut);
        buttonProfile = findViewById(R.id.mainInterfaceProfile);
        buttonCategories = findViewById(R.id.mainInterfaceCategories);
        currentUserEmail = findViewById(R.id.mainInterfaceCurrentUser);
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
        email = prev.getStringExtra("email");
        nickname = prev.getStringExtra("nickname");
        published = prev.getLongExtra("published", -1L);
        admin = prev.getBooleanExtra("admin", false);

        currentUserEmail.setText("CURRENT USER\n" + email);

        if (!admin) {
            buttonUsers.setVisibility(View.GONE);
            buttonCategories.setVisibility(View.GONE);
        }
    }

    /**
     * This method sets the actions for the buttons of the view.
     *
     * @author AlphaDvlpr.
     */
    protected void setActions() {
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                makeToast("WISH TO SEE YOU SOON!");

                Intent intent = new Intent(MainInterface.this, UsersFragment.class);
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
            public void onClick(View v) {
                startActivity(new Intent(MainInterface.this, ArticlesList.class));
            }
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
            public void onClick(View v) {
                startActivity(new Intent(MainInterface.this, CategoriesList.class));
            }
        });
    }

    /**
     * This method starts the Home view and finish the current one preventing the user to return here
     * when he/she wants to exit the app pressing the back button there.
     *
     * @author AlphaDvlpr.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MainInterface.this, MainActivity.class));
        finish();
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

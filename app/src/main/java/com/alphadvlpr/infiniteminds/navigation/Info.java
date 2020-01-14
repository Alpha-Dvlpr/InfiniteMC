package com.alphadvlpr.infiniteminds.navigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;

import com.alphadvlpr.infiniteminds.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Info extends AppCompatActivity {

    private Button report, contact, share, privacy;
    private BottomAppBar mainBar;
    private FloatingActionButton fabSearch;
    private ActionMenuItemView itemUsers, itemTrending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        privacy = findViewById(R.id.infoPrivacy);
        report = findViewById(R.id.infoReport);
        contact = findViewById(R.id.infoContact);
        share = findViewById(R.id.infoShare);
        fabSearch = findViewById(R.id.infoSearchFAB);
        itemTrending = findViewById(R.id.menuTrend);
        itemUsers = findViewById(R.id.menuUsers);
        mainBar = findViewById(R.id.infoBottomAppBar);

        setActions();
    }

    private void setActions(){
        itemTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Info.this, Trending.class));
                finish();
            }
        });

        mainBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Info.this, Home.class));
                finish();
            }
        });

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Info.this, Search.class));
                finish();
            }
        });

        itemUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Info.this, Login.class));
                finish();
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] TO = {"alpha.dvlpr@gmail.com"};

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "ADDON-MODS FOR MCPE BUGS");
                emailIntent.putExtra(Intent.EXTRA_TEXT,
                        "*** DESCRIBE THE ERROR THE BEST WAY POSSIBLE ***\n" +
                                "*** IF YOU CAN ADD IMAGES OF THE ERROR ***\n\n" +
                                "THE ERROR IS: \n" +
                                "HAPPENED DOING: \n" +
                                "CAN IT BE EASILY FORCED TO HAPPEN?: ");

                try { startActivity(Intent.createChooser(emailIntent, "REPORT BUGS")); }
                catch (android.content.ActivityNotFoundException ex) { makeToast("ERROR WHILE DOING THE ACTION"); }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, "DOWNLOAD APP");
                share.putExtra(Intent.EXTRA_TEXT, "Download Addon-Mods for MCPE.\nhttps://play.google.com/store/apps/details?id=com.alphadvlpr.infiniteminds");

                startActivity(Intent.createChooser(share, "SHARE APP"));
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://teaminfiniteminds.wixsite.com/website/blog/organiza-tu-equipo-de-la-manera-más-óptima"))); }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] TO = {"teaminfiniteminds@gmail.com"};

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "INFORMATION ABOUT ADDON-MODS FOR MCPE");
                emailIntent.putExtra(Intent.EXTRA_TEXT,"*** TYPE HERE YOUR MESSAGE ***");

                try { startActivity(Intent.createChooser(emailIntent, "SEND EMAIL")); }
                catch (android.content.ActivityNotFoundException ex) { makeToast("ERROR WHILE DOING THE ACTION"); }
            }
        });
    }

    private void makeToast(String msg){ Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }
}

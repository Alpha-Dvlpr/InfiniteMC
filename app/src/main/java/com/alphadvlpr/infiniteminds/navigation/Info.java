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
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Error en InfiniteMC");
                emailIntent.putExtra(Intent.EXTRA_TEXT,
                        "*** DESCRIBE EL ERROR DE LA MANERA MÁS PRECISA POSIBLE ***\n" +
                                "*** SI PUEDES ADJUNTA CAPTURAS DEL ERROR ***\n\n" +
                                "EL ERROR ES: \n" +
                                "OCURRIÓ HACIENDO: \n" +
                                "¿SE PUEDE REPRODUCIR DE MANERA SENCILLA?: ");

                try { startActivity(Intent.createChooser(emailIntent, "REPORTAR ERRORES")); }
                catch (android.content.ActivityNotFoundException ex) { makeToast("Error al procesar la acción."); }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { makeToast("PROXIMAMENTE"); }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeToast("PROXIMAMENTE");
                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://alphadvlpr.wixsite.com/home/privacy-policy")));
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] TO = {"teaminfiniteminds@gmail.com"};

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Información sobre InfiniteMC");
                emailIntent.putExtra(Intent.EXTRA_TEXT,"*** ESCRIBE AQUÍ EL MENSAJE ***");

                try { startActivity(Intent.createChooser(emailIntent, "ENVIAR EMAIL")); }
                catch (android.content.ActivityNotFoundException ex) { makeToast("Error al procesar la acción."); }
            }
        });
    }

    private void makeToast(String msg){ Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }
}

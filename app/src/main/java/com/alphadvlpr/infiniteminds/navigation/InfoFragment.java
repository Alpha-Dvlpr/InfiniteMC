package com.alphadvlpr.infiniteminds.navigation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.alphadvlpr.infiniteminds.R;

/**
 * This class manages the Info Fragment.
 *
 * @author AlphaDvlpr.
 */
public class InfoFragment extends Fragment {

    private Button report;
    private Button contact;
    private Button share;
    private Button privacy;
    private Context context;

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
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        privacy = view.findViewById(R.id.infoPrivacy);
        report = view.findViewById(R.id.infoReport);
        contact = view.findViewById(R.id.infoContact);
        share = view.findViewById(R.id.infoShare);

        context = getContext();

        setActions();

        return view;
    }

    /**
     * This method sets the actions for the buttons of the view.
     *
     * @author AlphaDvlpr.
     */
    private void setActions() {
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

                try {
                    startActivity(Intent.createChooser(emailIntent, "REPORT BUGS"));
                } catch (android.content.ActivityNotFoundException ex) {
                    makeToast("ERROR WHILE DOING THE ACTION");
                }
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
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://teaminfiniteminds.wixsite.com/website/post/organiza-tu-equipo-de-la-manera-más-óptima")));
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
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "INFORMATION ABOUT ADDON-MODS FOR MCPE");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "*** TYPE HERE YOUR MESSAGE ***");

                try {
                    startActivity(Intent.createChooser(emailIntent, "SEND EMAIL"));
                } catch (android.content.ActivityNotFoundException ex) {
                    makeToast("ERROR WHILE DOING THE ACTION");
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

package com.rockyniu.stickers.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.rockyniu.stickers.R;
import com.rockyniu.stickers.fragment.NewsFragment;
import com.rockyniu.stickers.listener.MainTabListener;
import com.rockyniu.stickers.listener.OnFragmentInteractionListener;


public class MainActivity extends BaseActivity implements OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    String userId = "";
    ActionBar.Tab newsTab;
    Fragment newsFragment = NewsFragment.newInstance(userId);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
//        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
//        }

        // Asking for the default ActionBar element that our platform supports.
        final ActionBar actionBar = getSupportActionBar();

        // Screen handling while hiding ActionBar icon.
//        actionBar.setDisplayShowHomeEnabled(false);

        // Screen handling while hiding Actionbar title.
//        actionBar.setDisplayShowTitleEnabled(false);

        // Creating ActionBar tabs.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Setting custom tab icons.
        newsTab = actionBar.newTab().setText(R.string.news);

        // Setting tab listeners.
        newsTab.setTabListener(new MainTabListener(newsFragment));

        // Adding tabs to the ActionBar.
        actionBar.addTab(newsTab);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the com.rockyniu.mpgcalculator.model.User/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit " + getResources().getString(R.string.app_name) + "?")
                .setMessage(null)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();
    }
}

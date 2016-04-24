package tk.melnichuk.kommunalchik;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import tk.melnichuk.kommunalchik.CustomViews.UnitTypesKeyboard;
import tk.melnichuk.kommunalchik.DataManagers.DbManager;
import tk.melnichuk.kommunalchik.DataManagers.ExcelManager;
import tk.melnichuk.kommunalchik.DataManagers.Tables.BaseTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.BillTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.CommunalTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.ElectricityRowTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.ElectricityTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.GasTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.HeatingTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.PhoneRowTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.PhoneTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.SegmentBillTypeTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.SegmentRowTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.SegmentTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.WaterRowTable;
import tk.melnichuk.kommunalchik.DataManagers.Tables.WaterTable;

public class MainActivity extends FragmentActivity {

    public UnitTypesKeyboard mKeyboard = null;
    public boolean mShowSavedBillExitMessage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        BaseTable[] tables = {
                new SegmentTable(),
                new SegmentBillTypeTable(),
                new SegmentRowTable(),
                new BillTable(),
                new CommunalTable(),
                new GasTable(),
                new WaterTable(),
                new WaterRowTable(),
                new HeatingTable(),
                new ElectricityTable(),
                new ElectricityRowTable(),
                new PhoneTable(),
                new PhoneRowTable()
        };
        DbManager dbManager = new DbManager(this);
        for(BaseTable t: tables){
            dbManager.registerTable(t);
        }

        dbManager.onCreate(dbManager.getWritableDatabase());

        ExcelManager em = new ExcelManager();
        em.init();

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }


            MenuFragment menuFragment = new MenuFragment();
            menuFragment.setArguments(getIntent().getExtras());
            // Create a new Fragment to be placed in the activity layout
            //HeadlinesFragment firstFragment = new HeadlinesFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
           // firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, menuFragment).commit();
        }

      //  mKeyboard = new UnitTypesKeyboard(this,isLandscapeOrientation() ? R.xml.keyboard_landscape : R.xml.keyboard_portrait );

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
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onBackPressed() {
        if(mKeyboard != null && mKeyboard.isCustomKeyboardVisible()) {
            mKeyboard.hideCustomKeyboard();
            return;
        }
        //int count = getSupportFragmentManager().getBackStackEntryCount();
        //Log.d("_LDB", count + " <-n");
        if(mShowSavedBillExitMessage && getSupportFragmentManager().getBackStackEntryCount() == 2){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.alert_dialog_bills_saved_bill_exit_message)
                    .setPositiveButton(R.string.alert_dialog_yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mShowSavedBillExitMessage = false;
                                    MainActivity.super.onBackPressed();
                                }
                            })
            .setNegativeButton(R.string.alert_dialog_no, null).show();
            return;
        }

        super.onBackPressed();
    }

    public boolean isLandscapeOrientation() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}

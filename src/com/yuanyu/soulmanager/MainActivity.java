package com.yuanyu.soulmanager;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.yuanyu.soulmanager.ui.utils.FragmentSwitcher;
import com.yuanyu.soulmanager.ui.utils.FragmentSwitcher.Item;
import com.yuanyu.soulmanager.ui.BackupFragment;
import com.yuanyu.soulmanager.ui.EventsFragment;
import com.yuanyu.soulmanager.ui.StatisticsFragment;
import com.yuanyu.soulmanager.ui.StatusesFragment;
import com.yuanyu.soulmanager.ui.ProjectsFragment;
import com.yuanyu.soulmanager.ui.TasksFragment;

public class MainActivity extends FragmentActivity implements View.OnClickListener, 
FragmentSwitcher.OnFragmentSwitchedListener {

	protected static final String TAG = "MainActivity";

	private static final FragmentSwitcher.Item[] ITEMS = {
		new FragmentSwitcher.Item(R.id.item_statuses, TAG + "-StatusesFragment") {
            @Override
            public Fragment createFragment() {
                return new StatusesFragment();
            }
        }.setTag(R.string.side_menu_statuses),
        
        new FragmentSwitcher.Item(R.id.item_projects, TAG + "-ProjectsFragment") {
            @Override
            public Fragment createFragment() {
                return new ProjectsFragment();
            }
        }.setTag(R.string.side_menu_projects),
        
        new FragmentSwitcher.Item(R.id.item_tasks, TAG + "-TasksFragment") {
            @Override
            public Fragment createFragment() {
                return new TasksFragment();
            }
        }.setTag(R.string.side_menu_tasks),
        
        new FragmentSwitcher.Item(R.id.item_events, TAG + "-EventsFragment") {
            @Override
            public Fragment createFragment() {
                return new EventsFragment();
            }
        }.setTag(R.string.side_menu_events),
        
        new FragmentSwitcher.Item(R.id.item_backup, TAG + "-BackupFragment") {
            @Override
            public Fragment createFragment() {
                return new BackupFragment();
            }
        }.setTag(R.string.side_menu_backup),
        
        /*new FragmentSwitcher.Item(R.id.item_experience, TAG + "-ExperienceFragment") {
            @Override
            public Fragment createFragment() {
                return new ExperienceFragment();
            }
        }.setTag(R.string.side_menu_experience),
        
        new FragmentSwitcher.Item(R.id.item_finance, TAG + "-FinanceFragment") {
            @Override
            public Fragment createFragment() {
                return new FinanceFragment();
            }
        }.setTag(R.string.side_menu_finance),
        
        new FragmentSwitcher.Item(R.id.item_dreams, TAG + "-DreamsFragment") {
            @Override
            public Fragment createFragment() {
                return new DreamsFragment();
            }
        }.setTag(R.string.side_menu_dreams),*/
        
        new FragmentSwitcher.Item(R.id.item_statistics, TAG + "-StatisticsFragment") {
            @Override
            public Fragment createFragment() {
                return new StatisticsFragment();
            }
        }.setTag(R.string.side_menu_statistics),
	};

	private DrawerLayout mDrawerLayout;
	private View mDrawer;
	private ActionBarDrawerToggle mDrawerToggle;
	private FragmentSwitcher mFragmentSwitcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		View contentView = findViewById(android.R.id.content);
		mFragmentSwitcher = new FragmentSwitcher(getSupportFragmentManager(), contentView,
				R.id.panel_main_view, R.id.item_statuses, TAG + "-FragmentSwitcher", ITEMS);

		// get notified about fragment changes so we can change our title
		mFragmentSwitcher.setOnFragmentSwitchedListener(this);
		// initial update of the title
		updateTitleFromFragment();
		
		mFragmentSwitcher.setOnClickListener(this);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer = findViewById(R.id.menu_container);

        if (mDrawerLayout!=null) {

            // set a custom shadow that overlays the main content when the drawer opens
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    R.drawable.ic_drawer, R.string.app_name, R.string.app_name) { //TODO proper strings & icon

                /** Called when a drawer has settled in a completely closed state. */
                @Override
                public void onDrawerClosed(View view) {
                    getActionBar().setDisplayHomeAsUpEnabled(true);
                }

                /** Called when a drawer has settled in a completely open state. */
                @Override
                public void onDrawerOpened(View drawerView) {
                    getActionBar().setDisplayHomeAsUpEnabled(false);
                }
            };

            // Set the drawer toggle as the DrawerListener
            mDrawerLayout.setDrawerListener(mDrawerToggle);

            // Set up the action bar in slidingMenu mode
            final ActionBar actionBar = getActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }
	
	@Override
    public void onBackPressed() {
        if (mFragmentSwitcher.isOverlayFragmentDisplayed()) {
            mFragmentSwitcher.hideOverlayFragment();
        }
        else if (getSupportFragmentManager().getBackStackEntryCount() == 0
                && mFragmentSwitcher.getCurrentFragment() != R.id.item_statuses) {
            mFragmentSwitcher.setFragment(R.id.item_statuses);
        } else {
            super.onBackPressed();
        }
    }
	
	private void updateTitleFromFragment() {
        Item item = mFragmentSwitcher.getCurrentFragmentItem();
        if (item != null) {
            Integer titleRes = (Integer) item.getTag();
            getActionBar().setTitle(titleRes.intValue());
        }
    }

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected "+item);
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

	// TODO, Verify if needed
	@Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                // layout / drawer are null in case of wide tablet layout
                if (mDrawerLayout != null && mDrawer != null) {
                    mDrawerLayout.closeDrawer(mDrawer);
                }
                break;
        }
    }

	@Override
	public void onFragmentSwitched(FragmentSwitcher source, int viewId) {
		updateTitleFromFragment();
	}
}

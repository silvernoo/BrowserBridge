package com.github.silvernoo.browserbridge;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.github.silvernoo.browserbridge.dao.ShareContentDao;
import com.github.silvernoo.browserbridge.ext.ItemTouchHelperViewHolder;
import com.github.silvernoo.browserbridge.ext.MyListCursorAdapter;
import com.github.silvernoo.browserbridge.widget.SimpleDividerItemDecoration;

/**
 * Created by saikou on 2016/8/19 0019.
 * Email uedeck@gmail.com .
 */
public class MainActivity extends BaseActivity {

    private ShareContentDao shareContentDao;
    private MyListCursorAdapter mAdapter;
    public static final float ALPHA_FULL = 1.0f;
    public Cursor cursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        shareContentDao = new ShareContentDao(this);
        final ImageView space = (ImageView) findViewById(R.id.space);
        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this, true));

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyListCursorAdapter(this, cursor = shareContentDao.preLoad());
        mRecyclerView.setAdapter(mAdapter);


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                MyListCursorAdapter.ViewHolder viewHolder1 = (MyListCursorAdapter.ViewHolder) viewHolder;
                shareContentDao.flow();
                shareContentDao.excludeId = viewHolder1._id;
                Snackbar.make(((MyListCursorAdapter.ViewHolder) viewHolder).mTextView1, R.string.deleted_record, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                shareContentDao.excludeId = -1;
                                mAdapter.changeCursor(cursor = shareContentDao.preLoad(), 0);
                            }
                        }).show();
                mAdapter.changeCursor(cursor = shareContentDao.preLoad(), 0);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                // We only want the active item to change
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    if (viewHolder instanceof ItemTouchHelperViewHolder) {
                        // Let the view holder know that this item is being moved or dragged
                        ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
                        itemViewHolder.onItemSelected();
                    }
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                viewHolder.itemView.setAlpha(ALPHA_FULL);

                if (viewHolder instanceof ItemTouchHelperViewHolder) {
                    // Tell the view holder it's time to restore the idle state
                    ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
                    itemViewHolder.onItemClear();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


//        Intent intent = new Intent();
//        intent.setClassName("com.android.settings", "com.android.settings.Settings$UsageAccessSettingsActivity");
//        intent.setAction("android.intent.action.MAIN");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (mPreferences.getBoolean(KEY_CLIPBOARD, true)) {
            menu.findItem(R.id.action_enable_clipboard).setVisible(false);
            menu.findItem(R.id.action_disable_clipboard).setVisible(true);
        } else {
            menu.findItem(R.id.action_enable_clipboard).setVisible(true);
            menu.findItem(R.id.action_disable_clipboard).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_enable_clipboard) {
            mPreferences.edit().putBoolean(KEY_CLIPBOARD, true).apply();
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.action_disable_clipboard) {
            mPreferences.edit().putBoolean(KEY_CLIPBOARD, false).apply();
            invalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        shareContentDao.flow();
    }

    @Override
    public void invalidateOptionsMenu() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.super.invalidateOptionsMenu();
            }
        }, 500);
    }
}

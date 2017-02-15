package com.smart.rchat.smart;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.smart.rchat.smart.adapter.ContactsAdapter;
import com.smart.rchat.smart.database.RChatContract;

import butterknife.BindView;

/**
 * Created by nishant on 08.02.17.
 */

public abstract class ContactActivity extends BaseActivity {

    @BindView(R.id.toolbar2)
    public Toolbar toolbar;

    @BindView(R.id.lvContactcreen)
    public ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        setSupportActionBar(toolbar);
        getLoaderManager().initLoader(0,null,new CursorLoadListener());
    }

    private class CursorLoadListener implements LoaderManager.LoaderCallbacks<Cursor>{

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return new CursorLoader(ContactActivity.this, RChatContract.USER_TABLE.CONTENT_URI,null,null,null,null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if(cursor == null) return;
            onCursorLoaded(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

    }

    protected  ListView getListView(){
        return  listView;
    }

    protected  abstract void onCursorLoaded(Cursor cursor);

}

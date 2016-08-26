package com.github.silvernoo.browserbridge.ext;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.silvernoo.browserbridge.R;
import com.github.silvernoo.browserbridge.dao.ShareContentDao;
import com.github.silvernoo.browserbridge.dao.ShareData;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by skyfishjy on 10/31/14.
 */
public class MyListCursorAdapter extends CursorRecyclerViewAdapter<MyListCursorAdapter.ViewHolder> {
    public ShareContentDao shareContentDao;

    public MyListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        shareContentDao = new ShareContentDao(context);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public TextView mTextView1;
        public TextView mTextView2;
        public CircleImageView count;
        public int position;
        public int _id;


        public ViewHolder(View view) {
            super(view);
            mTextView1 = (TextView) view.findViewById(R.id.text1);
            mTextView2 = (TextView) view.findViewById(R.id.text2);
            count = (CircleImageView) view.findViewById(R.id.profile_image);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {
        ShareData shareData = ShareData.fromCursor(cursor, shareContentDao);
        viewHolder.mTextView1.setText(shareData.content);
        if (shareData.urls != null) {
            if (shareData.urls.size() >= 1 && shareData.urls.get(0) != null)
                viewHolder.mTextView2.setText(shareData.urls.get(0));
            else
                viewHolder.mTextView2.setVisibility(View.GONE);
        }
        if (shareData.packageName != null)
            try {
                viewHolder.count.setImageDrawable(viewHolder.count.getContext().getPackageManager().getApplicationIcon(shareData.packageName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        viewHolder._id = shareData._id;
        viewHolder.position = position;
        shareContentDao.writableDatabase.close();
    }
}
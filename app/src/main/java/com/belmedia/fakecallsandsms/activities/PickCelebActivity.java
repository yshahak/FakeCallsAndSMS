package com.belmedia.fakecallsandsms.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.belmedia.fakecallsandsms.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PickCelebActivity extends AppCompatActivity {

    @Bind(R.id.my_recycler_view) RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    final static String KEY_CELEB_RESULT = "keyCeleb";
    Activity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caleb_gallery);
        ButterKnife.bind(this);
        ctx =this;
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        int[] myImageList = new int[]{R.drawable.celeb_obama , R.drawable.celeb_golda, R.drawable.celeb_hawking};

        mAdapter = new MyAdapter(myImageList, getIntent());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nemu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements View.OnClickListener {
        private int[] mDataSet;
        private Intent callerActivityIntent;


        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public  class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView celebImage;
            public int drawable_id;

            // each data item is just a string in this case
            public ViewHolder(ImageView v) {
                super(v);
                celebImage = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(int[] myDataSet, Intent intent) {
            mDataSet = myDataSet;
            callerActivityIntent = intent;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,  int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.celeb_slot, parent, false);
            v.setOnClickListener(this);
            ViewHolder viewHolder = new ViewHolder((ImageView) v);
            v.setTag(viewHolder);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.drawable_id = mDataSet[position];
            holder.celebImage.setImageResource(holder.drawable_id);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataSet.length;
        }

        @Override
        public void onClick(View v) {
            callerActivityIntent.putExtra(KEY_CELEB_RESULT, ((ViewHolder)v.getTag()).drawable_id);
            ctx.setResult(RESULT_OK, callerActivityIntent);
            ctx.finish();
        }
    }
}

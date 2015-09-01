package com.belmedia.fakecallsandsms.sms;

/**
 * Created by B.E.L on 22/06/2015.
 */

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.belmedia.fakecallsandsms.R;
import com.belmedia.fakecallsandsms.Utils;

import java.util.List;


/**
 * {@link SmsAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.ViewHolder> implements View.OnClickListener {
    private Context ctx;

    public SmsAdapter(Context ctx, List<ChatMessage> chatMessages) {
        this.ctx = ctx;
        this.chatMessages = chatMessages;
    }

    private List<ChatMessage> chatMessages;

    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_NOT_ME = 0;



    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView avatar;
        public final TextView hourView;
        public final TextView dateView;

        public final TextView txtMessage;

        // each data item is just a string in this case
        public ViewHolder(ViewGroup view) {
            super(view);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            dateView = (TextView) view.findViewById(R.id.txtInfo);
            hourView = (TextView) view.findViewById(R.id.txtInfo_hour);
            txtMessage = (TextView) view.findViewById(R.id.txtMessage);

        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public SmsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_ME: {
                layoutId = R.layout.list_item_chat_message_right;
                break;
            }
            case VIEW_TYPE_NOT_ME: {
                layoutId = R.layout.list_item_chat_message_left;
                break;
            }
        }
        // create a new view
        View v = LayoutInflater.from(ctx).inflate(layoutId, parent, false);
        v.setOnClickListener(this);
        ViewHolder viewHolder = new ViewHolder((ViewGroup) v);
        v.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessages.get(position).getIsme() ? VIEW_TYPE_ME: VIEW_TYPE_NOT_ME;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChatMessage chatMessage = chatMessages.get(position);
        holder.txtMessage.setText(chatMessage.getMessage());
        String date = chatMessage.getDate();
        if (date == null)
            holder.dateView.setVisibility(View.GONE);
        else{
            holder.dateView.setVisibility(View.VISIBLE);
            holder.dateView.setText(date);
        }
        holder.hourView.setText(chatMessage.getHourTime());
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_NOT_ME){
            holder.avatar.setVisibility(View.VISIBLE);
            if (holder.avatar.getWidth() != 0){
                Utils.loadImageFromUri(ctx, Uri.parse(chatMessage.getThumbnail()), holder.avatar);
            } else {
                holder.avatar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Utils.loadImageFromUri(ctx, Uri.parse(chatMessage.getThumbnail()), holder.avatar);
                        holder.avatar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });
            }
        } else
            holder.avatar.setVisibility(View.GONE);
    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return chatMessages.size();

    }



    @Override
    public void onClick(View v) {

    }
}


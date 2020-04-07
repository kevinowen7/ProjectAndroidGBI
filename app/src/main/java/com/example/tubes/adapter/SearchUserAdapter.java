package com.example.tubes.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tubes.Model.UserData;
import com.example.tubes.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private OnDetailListener mOnDetailListener;

    public ArrayList<UserData> mItemList;

    private Context mContext;
    private Activity mActiviy;


    public void addContext(Context context){
        mContext = context;
    }


    public void addActivity(Activity videoActivity) {
        this.mActiviy = videoActivity;
    }

    public SearchUserAdapter(ArrayList<UserData> itemList, OnDetailListener onDetailListener) {

        mItemList = itemList;
        this.mOnDetailListener = onDetailListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_jemaat, parent, false);
            return new ItemViewHolder(view ,mOnDetailListener);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof ItemViewHolder) {

            populateItemRows((ItemViewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }

    }



    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView usernameItem;
        TextView nameItem;
        ImageView profile_pic;
        ProgressBar progressList;

        OnDetailListener onDetailListener;

        @SuppressLint("SetJavaScriptEnabled")
        public ItemViewHolder(@NonNull View itemView , OnDetailListener onDetailListener) {
            super(itemView);

            profile_pic = itemView.findViewById(R.id.profile_pic);
            usernameItem = itemView.findViewById(R.id.list_username);
            nameItem = itemView.findViewById(R.id.list_nama);
            progressList = itemView.findViewById(R.id.progressList);

            this.onDetailListener = onDetailListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onDetailListener.onDetailClick(getAdapterPosition());
        }
    }

    public interface OnDetailListener{
        void onDetailClick(int position);
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressLoad;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressLoad = itemView.findViewById(R.id.progressLoad);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

    private void populateItemRows(final ItemViewHolder viewHolder, final int position) {
        viewHolder.nameItem.setText(mItemList.get(position).getmName());
        viewHolder.usernameItem.setText(mItemList.get(position).getmUsername());
        //Change Picture Profile if exist
        if (!mItemList.get(position).getmUrl().equals("null")){
            Picasso.get().load(viewHolder.itemView.getContext().getString(R.string.WEB_SERVER)+mItemList.get(position).getmUrl()).memoryPolicy(MemoryPolicy.NO_CACHE).into(viewHolder.profile_pic);
        }

    }

}

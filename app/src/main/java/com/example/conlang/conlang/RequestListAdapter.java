package com.example.conlang.conlang;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.RequestViewHolder>  {
    private List<Map<String, Object>> mRequest ;
    private ListItemClickListener mOnClickListener;
    private  Map<String, Object> request;

    // we must create this interface to initiate the adapter click feature

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex,ListItemClickListener listener );
    }

    public void SetOnItemClickListener(ListItemClickListener onClickListener){
     mOnClickListener=onClickListener;
    }

/* Contractor */
public RequestListAdapter(List<Map<String, Object>> mRequest ,ListItemClickListener onClickListener) {
  this.mRequest=mRequest;
    mOnClickListener=onClickListener;

}
        /*
        * the follwoing method connect the spesfic view holder with th list
        */
    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layoutIdForListItem = R.layout.request_item;
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        RequestViewHolder viewHolder = new RequestViewHolder(view,mOnClickListener);
        return viewHolder;
    }
/*
* take the value and assind it to the view
* */
    @Override
    public void onBindViewHolder(@NonNull RequestListAdapter.RequestViewHolder RequestViewHolder, int position) {
        request = mRequest.get(position);
        setData(request,RequestViewHolder);
    }

    private void setData(Map<String, Object> request, RequestViewHolder RequestViewHolder) {
        if (request.size() != 0 ) {
            RequestViewHolder.translatorName.setText(request.get("Name").toString());
            RequestViewHolder.translatorEmail.setText(request.get("Email").toString());
        }


    }

    @Override
    public int getItemCount() {

        return mRequest.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView translatorName;
        public TextView translatorEmail;
        public TextView translatorType;
        private Map<String ,Object> request;
        private ListItemClickListener mListener;
        public RequestViewHolder(@NonNull View itemView , final ListItemClickListener listener ) {
            super(itemView);
            translatorName=itemView.findViewById(R.id.translatorName);
            translatorEmail=itemView.findViewById(R.id.translatorEmail);
            setListener(listener);
            itemView.setOnClickListener(this);
                   /* new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  if (listener!=null){
                      int position= getAdapterPosition();
                      if(position!= RecyclerView.NO_POSITION){
                          mListener=listener;
                          listener.onListItemClick(position ,listener );
                      }
                  }
                }
            });*/
        }

        private void setListener(ListItemClickListener listener) {
            mListener=listener;
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
           int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition,mListener);
        }

    }
}

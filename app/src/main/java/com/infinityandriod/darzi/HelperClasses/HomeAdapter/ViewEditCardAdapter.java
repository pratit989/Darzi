package com.infinityandriod.darzi.HelperClasses.HomeAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infinityandriod.darzi.R;

import java.util.ArrayList;

public class ViewEditCardAdapter extends RecyclerView.Adapter<ViewEditCardAdapter.ViewEditCustomerViewHolder>{

    ArrayList<ViewEditCardHelperClass> viewEditCustomers;


    public ViewEditCardAdapter(ArrayList<ViewEditCardHelperClass> viewEditCustomers) {
        this.viewEditCustomers = viewEditCustomers;
    }

    @NonNull
    @Override
    public ViewEditCustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_edit_card_design,parent,false);
        ViewEditCustomerViewHolder ViewEditCustomerViewHolder = new ViewEditCustomerViewHolder(view);


        return ViewEditCustomerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewEditCustomerViewHolder holder, int position) {


        ViewEditCardHelperClass viewEditCardHelperClass = viewEditCustomers.get(position);

        holder.image.setImageResource(viewEditCardHelperClass.getImage());

    }

    @Override
    public int getItemCount() {
        return viewEditCustomers.size();
    }

    public static class ViewEditCustomerViewHolder extends RecyclerView.ViewHolder{

        ImageView image;


        public ViewEditCustomerViewHolder(@NonNull View itemView) {
            super(itemView);

            //hooks

            image = itemView.findViewById(R.id.view_edit_cust_image);

        }
    }

}

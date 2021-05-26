package com.infinityandriod.darzi.HelperClasses.HomeAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infinityandriod.darzi.R;
import com.infinityandriod.darzi.User.ExpenseManagement;
import com.infinityandriod.darzi.User.IncomeManagement;
import com.infinityandriod.darzi.User.StaffManagement;

import java.util.ArrayList;

public class ManagementAdapter extends RecyclerView.Adapter<ManagementAdapter.ManagementViewHolder> {

    ArrayList<ManagementHelperClass> managementCategories;

    public ManagementAdapter(ArrayList<ManagementHelperClass> managementCategories) {
        this.managementCategories = managementCategories;
    }

    @NonNull
    @Override
    public ManagementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.management_card_design,parent,false);
        return new ManagementViewHolder(view);
    }

    Class[] classes = new Class[]{StaffManagement.class, IncomeManagement.class, ExpenseManagement.class};


    @Override
    public void onBindViewHolder(@NonNull ManagementViewHolder holder, int position) {

        ManagementHelperClass managementHelperClass = managementCategories.get(position);

        holder.image.setImageResource(managementHelperClass.getImage());
        holder.title.setText(managementHelperClass.getTitle());
        holder.relativeLayout.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, classes[position]);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {

        return managementCategories.size();
    }

    public static class ManagementViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title,desc;
        RelativeLayout relativeLayout;

        public ManagementViewHolder(@NonNull View itemView) {
            super(itemView);

            //hooks
            image = itemView.findViewById(R.id.management_image);
            title = itemView.findViewById(R.id.management_title);
            relativeLayout = itemView.findViewById(R.id.management_layout);
        }
    }

}

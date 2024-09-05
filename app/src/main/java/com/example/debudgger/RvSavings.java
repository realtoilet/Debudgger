package com.example.debudgger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
/**
 * TODO:
 * - Add 2 buttons, edit and delete
 */
public class RvSavings extends RecyclerView.Adapter<RvSavings.ViewHolder>{
    List<SavingsClass> list;
    Context c;
    SavingsListener db;

    public RvSavings(List<SavingsClass> list, Context c, SavingsListener db) {
        this.list = list;
        this.c = c;
        this.db = db;
    }

    @NonNull
    @Override
    public RvSavings.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.container_rv_cell, parent, false);
        return new RvSavings.ViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RvSavings.ViewHolder h, int p) {
        SavingsClass saved = list.get(p);
        h.name.setText(saved.itemName);
        h.price.setText("Price: " + saved.price);
        h.date.setText("Bought on: " + saved.date);
        h.img.setImageBitmap(BitmapFactory.decodeByteArray(saved.image, 0, saved.image.length));
        Log.d("uid of them bitches", "walter sigma " + saved.uid);
        h.check.setVisibility(View.GONE);
        h.cancel.setVisibility(View.GONE);

        h.delete.setOnClickListener(l -> {
            //TODO POTANGINA AYUSIN TONG HAYOP NA SHIT NA TO FRFR
        });


        h.edit.setOnClickListener(l -> {
            h.check.setVisibility(View.VISIBLE);
            h.cancel.setVisibility(View.VISIBLE);
            h.edit.setVisibility(View.GONE);
            h.delete.setVisibility(View.GONE);
            EditText nameEdit = new EditText(h.itemView.getContext());
            nameEdit.setText(saved.itemName);
            nameEdit.setBackground(ContextCompat.getDrawable(h.itemView.getContext(), R.drawable.dg_ed_bg));
            nameEdit.setTypeface(ResourcesCompat.getFont(h.itemView.getContext(), R.font.archivo_light));
            nameEdit.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.bg_blue));
            nameEdit.setTextSize(15f);
            nameEdit.setPadding(20, 0, 0, 0); // Set padding as needed

            ConstraintLayout.LayoutParams paramsName = new ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    100
            );
            paramsName.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            paramsName.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            paramsName.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            paramsName.setMargins(360, 20, 0, 250);
            nameEdit.setLayoutParams(paramsName);

            h.name.setVisibility(View.GONE);
            ((ViewGroup) h.name.getParent()).addView(nameEdit);

            EditText priceEdit = new EditText(h.itemView.getContext());
            priceEdit.setText(String.valueOf(saved.price));
            priceEdit.setBackground(ContextCompat.getDrawable(h.itemView.getContext(), R.drawable.dg_ed_bg));
            priceEdit.setTypeface(ResourcesCompat.getFont(h.itemView.getContext(), R.font.archivo_light));
            priceEdit.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.bg_blue));
            priceEdit.setTextSize(15f);
            priceEdit.setPadding(20, 0, 10, 0); // Set padding as needed

            ConstraintLayout.LayoutParams paramsPrice = new ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    100
            );
            paramsPrice.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            paramsPrice.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            paramsPrice.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            paramsPrice.setMargins(360, 80, 0, 0);
            priceEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            priceEdit.setLayoutParams(paramsPrice);
            ((ViewGroup) h.price.getParent()).addView(priceEdit);
            h.price.setVisibility(View.GONE);
            nameEdit.requestFocus();
            priceEdit.requestFocus();

            h.check.setOnClickListener(li-> {
                saved.itemName = nameEdit.getText().toString();
                saved.price = Double.parseDouble(priceEdit.getText().toString());

                db.updateThings(saved.itemName, saved.price, saved.uid);

                h.name.setText(saved.itemName);
                h.price.setText("Price: " + saved.price);
                h.name.setVisibility(View.VISIBLE);
                h.price.setVisibility(View.VISIBLE);

                ((ViewGroup) nameEdit.getParent()).removeView(nameEdit);
                ((ViewGroup) priceEdit.getParent()).removeView(priceEdit);
                h.edit.setVisibility(View.VISIBLE);
                h.delete.setVisibility(View.VISIBLE);
                h.check.setVisibility(View.GONE);
                h.cancel.setVisibility(View.GONE);
            });

            h.cancel.setOnClickListener(li->{
                h.name.setVisibility(View.VISIBLE);
                h.price.setVisibility(View.VISIBLE);
                ((ViewGroup) nameEdit.getParent()).removeView(nameEdit);
                ((ViewGroup) priceEdit.getParent()).removeView(priceEdit);
                h.edit.setVisibility(View.VISIBLE);
                h.delete.setVisibility(View.VISIBLE);
                h.check.setVisibility(View.GONE);
                h.cancel.setVisibility(View.GONE);
            });
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, price;
        ImageView img, edit, delete, check, cancel;

        public ViewHolder(@NonNull View i) {
            super(i);

            name = i.findViewById(R.id.tv_name);
            date = i.findViewById(R.id.tv_date);
            price = i.findViewById(R.id.tv_price);
            img = i.findViewById(R.id.iv_image);
            edit = i.findViewById(R.id.edit);
            delete = i.findViewById(R.id.delete);
            check = i.findViewById(R.id.check);
            cancel = i.findViewById(R.id.cancel);
        }
    }
    public void updateNewData(List<SavingsClass> newData) {
        this.list = newData;
        notifyItemInserted(Math.max(0, list.size() - 1));
    }
    public void refreshData(List<SavingsClass> newData) {
        this.list = newData;
    }
    public double returnAllSpent(){
        double total = 0;
        for (SavingsClass s : list){
            total += s.price;
        }
        return total;
    }
}

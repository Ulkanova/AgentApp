package com.ulkanova.agentapp.model;

import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ulkanova.agentapp.R;

import java.util.List;

public class PropiedadAdapter extends RecyclerView.Adapter<PropiedadAdapter.PropiedadViewHolder> {

    @NonNull
    private List<Propiedad> mDataset;
    private OnPropiedadListener mOnPropiedadListener;

    public PropiedadAdapter(@NonNull List<Propiedad> mDataset, OnPropiedadListener mOnPropiedadListener) {
        this.mDataset = mDataset;
        this.mOnPropiedadListener = mOnPropiedadListener;

    }

    @Override
    public PropiedadAdapter.PropiedadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fila_propiedad, parent, false);
        PropiedadViewHolder  vh = new PropiedadViewHolder(v, mOnPropiedadListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PropiedadAdapter.PropiedadViewHolder holder, int position) {
        holder.lblPrecio.setTag(position);
        holder.lblAlquilerVenta.setTag(position);
        holder.imagenPropiedad.setTag(position);
        holder.lblDormitorios.setTag(position);
        holder.lblTipo.setTag(position);

        Propiedad propiedades = mDataset.get(position);

        String precio = String.format("%,d", propiedades.getPrecio()).replace(",",".");
        String precioVisual ="";
        if (propiedades.isPesos()) {
            precioVisual+="$ "+precio;
        }
        else {
            precioVisual+="USD "+precio;
        }

        holder.lblPrecio.setText(precioVisual);

        if (propiedades.isVenta()) {
            holder.lblAlquilerVenta.setText("VENTA");
        }
        else holder.lblAlquilerVenta.setText("ALQUILER");

        holder.lblDormitorios.setText(String.valueOf(propiedades.getDormitorios()).replace("0","Monoambiente"));
        if (propiedades.getDormitorios()==0){holder.lblDormitorios.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);}

        holder.lblTipo.setText(propiedades.getTipo());

//        holder.imagenPropiedad.setImageURI(Uri.parse(propiedades.get));
        Log.d("PROPIEDAD", "ADAPTERonBindViewHolder - IdPropiedad "+propiedades.getIdPropiedad());
//        if (!(propiedades.getImagen()==null)) {
//            cargarImagen(holder);
//        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class PropiedadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imagenPropiedad;
        TextView lblAlquilerVenta, lblPrecio, lblDormitorios,lblTipo;
        OnPropiedadListener onPropiedadListener;

        public PropiedadViewHolder(@NonNull View v, OnPropiedadListener OnPropiedadListener) {
            super(v);
            imagenPropiedad = v.findViewById(R.id.imagenPropiedad);
            lblAlquilerVenta = v.findViewById(R.id.lblAlquilerVenta);
            lblPrecio = v.findViewById(R.id.lblPrecio);
            lblDormitorios = v.findViewById(R.id.lblDormitorios);
            lblTipo = v.findViewById(R.id.lblTipo);
            this.onPropiedadListener = OnPropiedadListener;
            imagenPropiedad.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {onPropiedadListener.onPropiedadClick(getAdapterPosition());

        }
    }
    public interface OnPropiedadListener{
        void onPropiedadClick (int posicion);
    }
}



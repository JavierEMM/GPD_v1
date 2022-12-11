package pe.edu.pucp.gdp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import pe.edu.pucp.gdp.Entity.Carrito;
import pe.edu.pucp.gdp.Entity.Productos;
import pe.edu.pucp.gdp.R;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>{

    private ArrayList<Carrito> listaCarrito = new ArrayList<>();
    private ArrayList<Carrito> listaOriginal;
    private Context context;
    private OnItemClickListener borrar;

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }
    public void filtrado(String txtBuscar){
        int size = txtBuscar.length();
        if(size == 0){
            listaCarrito.clear();
            listaCarrito.addAll(listaOriginal);
        }else{
            listaCarrito.clear();
            for(Carrito d : listaOriginal){
                if(d.getProductos().getNombre().toLowerCase().contains(txtBuscar)){
                    listaCarrito.add(d);
                }
            }
        }
        notifyDataSetChanged();
    }
    public void setBorrar(OnItemClickListener borrar) {
        this.borrar = borrar;
    }
    public class CarritoViewHolder extends RecyclerView.ViewHolder{
        Carrito carrito;
        public CarritoViewHolder(@NonNull View itemView, OnItemClickListener borrar){
            super(itemView);
            Button btnEliminar = itemView.findViewById(R.id.btnBorrar);
            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    borrar.OnItemClick(getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public CarritoAdapter.CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_rv_carrito,parent,false);
        return new CarritoViewHolder(itemView,borrar);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        Carrito carrito = listaCarrito.get(position);
        holder.carrito = carrito;

        ImageView imageView = holder.itemView.findViewById(R.id.imageProducto);
        TextView nombreProducto = holder.itemView.findViewById(R.id.textNombreProducto);
        TextView cantidad = holder.itemView.findViewById(R.id.textCantidad);
        TextView precioProducto = holder.itemView.findViewById(R.id.textPrecioTotal);

        nombreProducto.setText(carrito.getProductos().getNombre());
        cantidad.setText("Cantidad: "+carrito.getCantidad());
        double cantidadDbl = Double.parseDouble(carrito.getCantidad());
        double precioDbl = Double.parseDouble(carrito.getProductos().getPrecio());
        precioProducto.setText("Precio: "+(cantidadDbl*precioDbl));

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("productos/"+carrito.getProductos().getId()+"/photo.jpg");
        Glide.with(context).load(storageReference).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);

    }

    @Override
    public int getItemCount() {
        return listaCarrito.size();
    }

    public ArrayList<Carrito> getListaCarrito() {
        return listaCarrito;
    }

    public void setListaCarrito(ArrayList<Carrito> listaCarrito) {
        this.listaCarrito = listaCarrito;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaCarrito);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}

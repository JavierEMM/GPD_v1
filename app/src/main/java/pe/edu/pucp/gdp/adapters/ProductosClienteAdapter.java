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

import pe.edu.pucp.gdp.Entity.Productos;
import pe.edu.pucp.gdp.R;

public class ProductosClienteAdapter extends RecyclerView.Adapter<ProductosClienteAdapter.ProductosClienteViewHolder>{

    private ArrayList<Productos> listaProductos = new ArrayList<>();
    private ArrayList<Productos> listaOriginal;
    private Context context;
    private OnItemClickListener carrito;

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    public void filtrado(String txtBuscar){
        int size = txtBuscar.length();
        if(size == 0){
            listaProductos.clear();
            listaProductos.addAll(listaOriginal);
        }else{
            listaProductos.clear();
            for(Productos d : listaOriginal){
                if(d.getNombre().toLowerCase().contains(txtBuscar)){
                    listaProductos.add(d);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setCarrito(OnItemClickListener carrito) {
        this.carrito = carrito;
    }

    public class ProductosClienteViewHolder extends RecyclerView.ViewHolder{
        Productos productos;
        public ProductosClienteViewHolder(@NonNull View itemView, OnItemClickListener carrito){
            super(itemView);

            Button btnCarrito = itemView.findViewById(R.id.btnCarrito);
            btnCarrito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    carrito.OnItemClick(getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public ProductosClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_rv_cliente_productos,parent,false);
        return new ProductosClienteViewHolder(itemView,carrito);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductosClienteViewHolder holder, int position) {
        Productos productos = listaProductos.get(position);
        holder.productos = productos;

        ImageView imageView = holder.itemView.findViewById(R.id.imageProducto);
        TextView nombreProducto = holder.itemView.findViewById(R.id.textNombreProducto);
        TextView descripcionProducto = holder.itemView.findViewById(R.id.textDescripcionProducto);
        TextView precioProducto = holder.itemView.findViewById(R.id.textPrecio);
        TextView stockProducto = holder.itemView.findViewById(R.id.textStock);

        nombreProducto.setText(productos.getNombre());
        descripcionProducto.setText(productos.getDescripcion());
        precioProducto.setText("Precio: S/"+productos.getPrecio());
        stockProducto.setText("Stock: "+productos.getStock());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("productos/"+productos.getId()+"/photo.jpg");
        Glide.with(context).load(storageReference).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);

    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public ArrayList<Productos> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(ArrayList<Productos> listaProductos) {
        this.listaProductos = listaProductos;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaProductos);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}

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
import pe.edu.pucp.gdp.Entity.Repartidor;
import pe.edu.pucp.gdp.R;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductosViewHolder>{

    private ArrayList<Productos> listaProductos = new ArrayList<>();
    private ArrayList<Productos> listaOriginal;
    private Context context;
    private OnItemClickListener editar;
    private OnItemClickListener borrar;

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

    public void setBorrar(OnItemClickListener borrar) {
        this.borrar = borrar;
    }
    public void setEditar(OnItemClickListener editar) {
        this.editar = editar;
    }

    public class ProductosViewHolder extends RecyclerView.ViewHolder{
        Productos productos;
        public ProductosViewHolder(@NonNull View itemView, OnItemClickListener editar, OnItemClickListener borrar){
            super(itemView);
            Button btnEliminar = itemView.findViewById(R.id.btnBorrar);
            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    borrar.OnItemClick(getAdapterPosition());
                }
            });
            Button btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editar.OnItemClick(getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public ProductosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_rv_productos,parent,false);
        return new ProductosViewHolder(itemView,editar,borrar);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductosViewHolder holder, int position) {
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

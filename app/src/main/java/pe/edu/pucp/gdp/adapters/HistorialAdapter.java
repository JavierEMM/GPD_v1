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
import pe.edu.pucp.gdp.Entity.Pedido;
import pe.edu.pucp.gdp.R;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>{

    private ArrayList<Pedido> listaPedidos = new ArrayList<>();
    private ArrayList<Pedido> listaOriginal;
    private Context context;

    public void filtrado(String txtBuscar){
        int size = txtBuscar.length();
        if(size == 0){
            listaPedidos.clear();
            listaPedidos.addAll(listaOriginal);
        }else{
            listaPedidos.clear();
            for(Pedido d : listaOriginal){
                if(d.getNumeroPedido().toLowerCase().contains(txtBuscar)){
                    listaPedidos.add(d);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class HistorialViewHolder extends RecyclerView.ViewHolder{
        Pedido pedido;
        public HistorialViewHolder(@NonNull View itemView){
            super(itemView);
        }
    }
    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_rv_historial,parent,false);
        return new HistorialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        Pedido pedido = listaPedidos.get(position);
        holder.pedido = pedido;

        TextView numeroPedido = holder.itemView.findViewById(R.id.textPedido);
        TextView textDireccionPedido = holder.itemView.findViewById(R.id.textDireccionPedido);
        TextView textTotalCancelarPedido = holder.itemView.findViewById(R.id.textTotalCancelarPedido);
        TextView textEstadoPedido = holder.itemView.findViewById(R.id.textEstadoPedido);

        numeroPedido.setText("PEDIDO #"+pedido.getNumeroPedido());
        textDireccionPedido.setText("Direccion: "+pedido.getUser().getDireccion());
        textTotalCancelarPedido.setText("Precio: "+pedido.getPrecio());
        textEstadoPedido.setText("Estado: "+pedido.getEstado());

    }
    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public ArrayList<Pedido> getListaPedidos() {
        return listaPedidos;
    }

    public void setListaPedidos(ArrayList<Pedido> listaPedidos) {
        this.listaPedidos = listaPedidos;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaPedidos);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}

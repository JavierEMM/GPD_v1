package pe.edu.pucp.gdp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import pe.edu.pucp.gdp.Entity.Repartidor;
import pe.edu.pucp.gdp.R;

public class RepartidoresAdapter extends RecyclerView.Adapter<RepartidoresAdapter.RepartidoresViewHolder>{

    private ArrayList<Repartidor> listaRepartidores = new ArrayList<>();
    private ArrayList<Repartidor> listaOriginal;
    private Context context;
    private OnItemClickListener editar;
    private OnItemClickListener borrar;

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    public void filtrado(String txtBuscar){
        int size = txtBuscar.length();
        if(size == 0){
            listaRepartidores.clear();
            listaRepartidores.addAll(listaOriginal);
        }else{
            listaRepartidores.clear();
            for(Repartidor d : listaOriginal){
                if(d.getNombres().toLowerCase().contains(txtBuscar)){
                    listaRepartidores.add(d);
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

    public class RepartidoresViewHolder extends RecyclerView.ViewHolder{
        Repartidor repartidor;
        public RepartidoresViewHolder(@NonNull View itemView,OnItemClickListener editar,OnItemClickListener borrar){
            super(itemView);
            Button btnEliminar = itemView.findViewById(R.id.btnBorrarRepartidor);
            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    borrar.OnItemClick(getAdapterPosition());
                }
            });
            Button btnEditar = itemView.findViewById(R.id.btnEditarRepartidor);
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
    public RepartidoresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_repartidores,parent,false);
        return new RepartidoresViewHolder(itemView,editar,borrar);
    }

    @Override
    public void onBindViewHolder(@NonNull RepartidoresViewHolder holder, int position) {
        Repartidor repartidor = listaRepartidores.get(position);
        holder.repartidor=repartidor;
        ImageView imageView = holder.itemView.findViewById(R.id.imageRepartidor);
        TextView nombreRepartidor = holder.itemView.findViewById(R.id.textNombreRepartidor);
        TextView dniRepartidor = holder.itemView.findViewById(R.id.textDniRepartidor);
        TextView telefonoRepartidor = holder.itemView.findViewById(R.id.textTelefonoRepartidor);
        nombreRepartidor.setText(repartidor.getNombres());
        dniRepartidor.setText("DNI: "+repartidor.getDni());
        telefonoRepartidor.setText("Telefono: "+repartidor.getNumero());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("repartidores/"+repartidor.getDni()+"/photo.jpg");
        Glide.with(context).load(storageReference).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);
    }

    @Override
    public int getItemCount() {
        return listaRepartidores.size();
    }

    public ArrayList<Repartidor> getListaRepartidores() {
        return listaRepartidores;
    }

    public void setListaRepartidores(ArrayList<Repartidor> listaRepartidores) {
        this.listaRepartidores = listaRepartidores;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaRepartidores);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}

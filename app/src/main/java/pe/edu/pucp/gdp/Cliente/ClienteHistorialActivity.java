package pe.edu.pucp.gdp.Cliente;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import pe.edu.pucp.gdp.Entity.Carrito;
import pe.edu.pucp.gdp.Entity.Pedido;
import pe.edu.pucp.gdp.R;
import pe.edu.pucp.gdp.adapters.CarritoAdapter;
import pe.edu.pucp.gdp.adapters.HistorialAdapter;

public class ClienteHistorialActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    HistorialAdapter historialAdapter = new HistorialAdapter();
    ArrayList<Pedido> pedidos = new ArrayList<>();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore =  FirebaseFirestore.getInstance();


    @Override
    protected void onResume() {
        super.onResume();
        firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("pedidos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                pedidos.clear();
                for (DocumentSnapshot querySnapshot : queryDocumentSnapshots.getDocuments()){
                    Pedido pedido = querySnapshot.toObject(Pedido.class);
                    pedidos.add(pedido);
                }
                historialAdapter.setListaPedidos(pedidos);
                historialAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_historial);
        BottomNavigationView bottomNavigationView =  findViewById(R.id.bottonnav);
        navbar(bottomNavigationView, ClienteHistorialActivity.this);

        historialAdapter.setContext(ClienteHistorialActivity.this);
        RecyclerView recyclerView = findViewById(R.id.recycleViewHistorial);
        recyclerView.setAdapter(historialAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ClienteHistorialActivity.this));


        SearchView searchView = findViewById(R.id.searchTextProductos);
        searchView.setOnQueryTextListener(ClienteHistorialActivity.this);
    }

    public void navbar(BottomNavigationView bottomNavigationView, Context context){
        bottomNavigationView.setSelectedItemId(R.id.perfil);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.productos:
                    intent =  new Intent(context, ClienteListaProductosActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.perfil:
                    intent =  new Intent(context, ClientePerfilActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.carrito:
                    break;
            }
            return true;
        });
    }
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        historialAdapter.filtrado(s);
        return false;
    }
}
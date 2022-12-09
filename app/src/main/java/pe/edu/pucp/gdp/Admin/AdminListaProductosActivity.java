package pe.edu.pucp.gdp.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import pe.edu.pucp.gdp.Entity.Productos;
import pe.edu.pucp.gdp.R;
import pe.edu.pucp.gdp.adapters.ProductosAdapter;

public class AdminListaProductosActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ProductosAdapter productosAdapter = new ProductosAdapter();
    ArrayList<Productos> productos = new ArrayList<>();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore =  FirebaseFirestore.getInstance();


    @Override
    protected void onResume() {
        super.onResume();
        firebaseFirestore.collection("productos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                productos.clear();
                for (DocumentSnapshot querySnapshot : queryDocumentSnapshots.getDocuments()){
                    Productos producto = querySnapshot.toObject(Productos.class);
                    productos.add(producto);
                }
                productosAdapter.setListaProductos(productos);
                productosAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_lista_productos);
        BottomNavigationView bottomNavigationView =  findViewById(R.id.bottonnav);
        navbar(bottomNavigationView, AdminListaProductosActivity.this);

        productosAdapter.setContext(AdminListaProductosActivity.this);
        RecyclerView recyclerView = findViewById(R.id.recycleViewProductos);
        recyclerView.setAdapter(productosAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminListaProductosActivity.this));

        productosAdapter.setEditar(new ProductosAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {

            }
        });
        productosAdapter.setBorrar(new ProductosAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {

            }
        });

        SearchView searchView = findViewById(R.id.searchTextProductos);
        searchView.setOnQueryTextListener(AdminListaProductosActivity.this);
    }

    public void navbar(BottomNavigationView bottomNavigationView, Context context){
        bottomNavigationView.setSelectedItemId(R.id.productos);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.productos:
                    break;
                case R.id.repartidores:
                    intent =  new Intent(context,AdminListaRepartidoresActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.reportes:
                    intent =  new Intent(context,AdminReportesActivity.class);
                    startActivity(intent);
                    finish();
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
        productosAdapter.filtrado(s);
        return false;
    }

}
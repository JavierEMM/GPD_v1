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
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import pe.edu.pucp.gdp.Entity.Productos;
import pe.edu.pucp.gdp.Entity.Repartidor;
import pe.edu.pucp.gdp.R;
import pe.edu.pucp.gdp.adapters.RepartidoresAdapter;

public class AdminListaRepartidoresActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    RepartidoresAdapter repartidoresAdapter = new RepartidoresAdapter();
    ArrayList<Repartidor> repartidors = new ArrayList<>();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore =  FirebaseFirestore.getInstance();


    @Override
    protected void onResume() {
        super.onResume();
        firebaseFirestore.collection("repartidores").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                repartidors.clear();
                for (DocumentSnapshot querySnapshot : queryDocumentSnapshots.getDocuments()){
                    Repartidor repartidor = querySnapshot.toObject(Repartidor.class);
                    repartidors.add(repartidor);
                }
                repartidoresAdapter.setListaRepartidores(repartidors);
                repartidoresAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_lista_repartidores);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonnav);
        navbar(bottomNavigationView,AdminListaRepartidoresActivity.this);

        repartidoresAdapter.setContext(AdminListaRepartidoresActivity.this);
        RecyclerView recyclerView = findViewById(R.id.recycleViewRepartidor);
        recyclerView.setAdapter(repartidoresAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminListaRepartidoresActivity.this));

        SearchView searchView = findViewById(R.id.searchTextUsuarios);
        searchView.setOnQueryTextListener(AdminListaRepartidoresActivity.this);
    }

    public void navbar(BottomNavigationView bottomNavigationView, Context context){
        bottomNavigationView.setSelectedItemId(R.id.repartidores);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent;
            item.setChecked(true);
            switch (item.getItemId()) {
                case R.id.productos:
                    intent =  new Intent(context,AdminListaProductosActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.repartidores:
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
        repartidoresAdapter.filtrado(s);
        return false;
    }
}
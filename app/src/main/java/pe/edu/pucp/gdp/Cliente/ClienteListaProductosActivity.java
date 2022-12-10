package pe.edu.pucp.gdp.Cliente;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;

import pe.edu.pucp.gdp.Admin.AdminListaProductosActivity;
import pe.edu.pucp.gdp.Admin.AdminListaRepartidoresActivity;
import pe.edu.pucp.gdp.Admin.AdminReportesActivity;
import pe.edu.pucp.gdp.Entity.Carrito;
import pe.edu.pucp.gdp.Entity.Productos;
import pe.edu.pucp.gdp.R;
import pe.edu.pucp.gdp.adapters.ProductosAdapter;
import pe.edu.pucp.gdp.adapters.ProductosClienteAdapter;

public class ClienteListaProductosActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    ProductosClienteAdapter productosAdapter = new ProductosClienteAdapter();
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
        setContentView(R.layout.activity_cliente_lista_productos);
        BottomNavigationView bottomNavigationView =  findViewById(R.id.bottonnav);
        navbar(bottomNavigationView, ClienteListaProductosActivity.this);


        productosAdapter.setContext(ClienteListaProductosActivity.this);
        RecyclerView recyclerView = findViewById(R.id.recycleViewProductos);
        recyclerView.setAdapter(productosAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ClienteListaProductosActivity.this));

        productosAdapter.setCarrito(new ProductosClienteAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                AlertDialog.Builder cantidad = new AlertDialog.Builder(ClienteListaProductosActivity.this);
                cantidad.setTitle("Cantidad de productos");
                final EditText cantidadInput = new EditText(ClienteListaProductosActivity.this);
                cantidadInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                cantidad.setView(cantidadInput);
                cantidad.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String cantidad = cantidadInput.getText().toString();
                        boolean bool=true;
                        try {
                            Integer.parseInt(cantidad);
                        }catch (NumberFormatException e){
                            Toast.makeText(ClienteListaProductosActivity.this, "Debe ser un numero entero", Toast.LENGTH_SHORT).show();
                            bool=false;
                        }
                        if(bool){
                            Productos productos = productosAdapter.getListaProductos().get(position);
                            int stock = Integer.parseInt(productos.getStock());
                            int cantidadInt = Integer.parseInt(cantidad);
                            if(stock >= cantidadInt && cantidadInt > 0){
                                Carrito carrito = new Carrito();
                                carrito.setCantidad(String.valueOf(cantidadInt));
                                carrito.setId(firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("carrito").document().getId());
                                carrito.setProductos(productos);
                                firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("carrito").document(carrito.getId()).set(carrito).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        productos.setStock(String.valueOf(stock-cantidadInt));
                                        firebaseFirestore.collection("productos").document(productos.getId()).set(productos).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                productosAdapter.notifyItemChanged(position);
                                            }
                                        });
                                    }
                                });
                            }else{
                                Toast.makeText(ClienteListaProductosActivity.this, "Debe ser un numero menor al stock y mayor a 0", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
                cantidad.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                cantidad.show();
            }
        });

        SearchView searchView = findViewById(R.id.searchTextProductos);
        searchView.setOnQueryTextListener(ClienteListaProductosActivity.this);
    }

    public void navbar(BottomNavigationView bottomNavigationView, Context context){
        bottomNavigationView.setSelectedItemId(R.id.productos);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.productos:
                    break;
                case R.id.perfil:
                    intent =  new Intent(context, ClientePerfilActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.carrito:
                    intent =  new Intent(context, ClienteCarritoActivity.class);
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
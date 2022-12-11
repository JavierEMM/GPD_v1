package pe.edu.pucp.gdp.Cliente;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

import pe.edu.pucp.gdp.Entity.Carrito;
import pe.edu.pucp.gdp.Entity.Pedido;
import pe.edu.pucp.gdp.Entity.Productos;
import pe.edu.pucp.gdp.Entity.User;
import pe.edu.pucp.gdp.R;
import pe.edu.pucp.gdp.adapters.CarritoAdapter;
import pe.edu.pucp.gdp.adapters.ProductosClienteAdapter;

public class ClienteCarritoActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener {

    CarritoAdapter carritoAdapter = new CarritoAdapter();
    ArrayList<Carrito> carritos = new ArrayList<>();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore =  FirebaseFirestore.getInstance();
    double precioTotal=0;
    @Override
    protected void onResume() {
        super.onResume();
        firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("carrito").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                carritos.clear();
                for (DocumentSnapshot querySnapshot : queryDocumentSnapshots.getDocuments()){
                    Carrito carrito = querySnapshot.toObject(Carrito.class);
                    double cantidad = Double.parseDouble(carrito.getCantidad());
                    precioTotal += Double.parseDouble(carrito.getProductos().getPrecio())*cantidad;
                    carritos.add(carrito);
                }
                TextView sumaTotal = findViewById(R.id.textSumaTotal);
                sumaTotal.setText("SUMA TOTAL: S/"+precioTotal);
                carritoAdapter.setListaCarrito(carritos);
                carritoAdapter.notifyDataSetChanged();
            }
        });
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_carrito);
        BottomNavigationView bottomNavigationView =  findViewById(R.id.bottonnav);
        navbar(bottomNavigationView, ClienteCarritoActivity.this);

        carritoAdapter.setContext(ClienteCarritoActivity.this);
        RecyclerView recyclerView = findViewById(R.id.recycleViewCarrito);
        recyclerView.setAdapter(carritoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ClienteCarritoActivity.this));
        carritoAdapter.setBorrar(new CarritoAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Carrito carrito = carritoAdapter.getListaCarrito().get(position);
                firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("carrito").document(carrito.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseFirestore.collection("productos").document(carrito.getProductos().getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                int cantidadProducto = Integer.parseInt(carrito.getCantidad());
                                Productos productos = documentSnapshot.toObject(Productos.class);
                                int cantidad = Integer.parseInt(productos.getStock());
                                cantidad = cantidadProducto+cantidad;
                                productos.setStock(String.valueOf(cantidad));
                                firebaseFirestore.collection("productos").document(carrito.getProductos().getId()).set(productos);
                                Toast.makeText(ClienteCarritoActivity.this, "Borrado Correctamente del carrito", Toast.LENGTH_SHORT).show();
                            }
                        });
                        precioTotal = precioTotal-Double.parseDouble(carrito.getCantidad())*Double.parseDouble(carrito.getProductos().getPrecio());
                        TextView sumaTotal = findViewById(R.id.textSumaTotal);
                        sumaTotal.setText("SUMA TOTAL: S/"+precioTotal);
                        carritoAdapter.getListaCarrito().remove(position);
                        carritoAdapter.notifyItemRemoved(position);
                    }
                });
            }
        });
        ((Button) findViewById(R.id.btnAgregarPedido)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carritoAdapter.getListaCarrito().size()>0){
                    Pedido pedido = new Pedido();

                    pedido.setEstado("Pendiente");
                    pedido.setNumeroPedido(String.valueOf(Math.floor(Math.random()*10000)));
                    pedido.setPrecio(String.valueOf(precioTotal));

                    firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user =  documentSnapshot.toObject(User.class);
                            pedido.setUser(user);
                        }
                    });

                    firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("carrito").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            ArrayList<Carrito> carritos =  new ArrayList<>();
                            for(DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                                Carrito carrito = documentSnapshot.toObject(Carrito.class);
                                carritos.add(carrito);
                                firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("carrito").document(carrito.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                    }
                                });
                            }
                            pedido.setCarritoArrayList(carritos);
                            pedido.setId(firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("pedidos").document().getId());
                            firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("pedidos").document(pedido.getId()).set(pedido).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ClienteCarritoActivity.this, "Pedido añadido correctamente", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ClienteCarritoActivity.this,ClienteListaProductosActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(ClienteCarritoActivity.this, "Carrito Vacio", Toast.LENGTH_SHORT).show();
                }
            }
        });
        SearchView searchView = findViewById(R.id.searchTextProductos);
        searchView.setOnQueryTextListener(ClienteCarritoActivity.this);
    }


    public void navbar(BottomNavigationView bottomNavigationView, Context context){
        bottomNavigationView.setSelectedItemId(R.id.carrito);
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
        carritoAdapter.filtrado(s);
        return false;
    }
}
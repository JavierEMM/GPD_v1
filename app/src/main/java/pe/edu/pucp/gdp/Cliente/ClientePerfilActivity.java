package pe.edu.pucp.gdp.Cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import pe.edu.pucp.gdp.Entity.User;
import pe.edu.pucp.gdp.Login.LoginActivity;
import pe.edu.pucp.gdp.R;

public class ClientePerfilActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore =  FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    User user;
    @Override
    protected void onResume() {
        super.onResume();
        TextView textNombres =  findViewById(R.id.textNombrePerfil);
        TextView textApellidos =  findViewById(R.id.textApellidosPerfil);
        TextView textTelefono =  findViewById(R.id.textTelefonoPerfil);
        TextView textDireccion =  findViewById(R.id.textDireccionPerfil);
        firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ImageView imageView = findViewById(R.id.imagePerfil);
                user = documentSnapshot.toObject(User.class);
                textNombres.setText(user.getNombres());
                textApellidos.setText(user.getApellidos());
                textTelefono.setText(user.getNumero());
                textDireccion.setText(user.getDireccion());
                Glide.with(ClientePerfilActivity.this).load(storageReference.child("users").child(firebaseAuth.getCurrentUser().getUid()+"/photo.jpg")).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_perfil);

        BottomNavigationView bottomNavigationView =  findViewById(R.id.bottonnav);
        navbar(bottomNavigationView, ClientePerfilActivity.this);

        ((Button) findViewById(R.id.btnCerrarSesion)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(ClientePerfilActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ((Button) findViewById(R.id.btnHistorialPerfil)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientePerfilActivity.this,ClienteHistorialActivity.class);
                startActivity(intent);
            }
        });
        ((Button) findViewById(R.id.btnEditarPerfil)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientePerfilActivity.this,ClienteEditarPerfilActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
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
}
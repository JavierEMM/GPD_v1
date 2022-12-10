package pe.edu.pucp.gdp.Cliente;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import pe.edu.pucp.gdp.Admin.AdminEditarProductoActivity;
import pe.edu.pucp.gdp.Entity.Productos;
import pe.edu.pucp.gdp.Entity.User;
import pe.edu.pucp.gdp.Login.MapaActivity;
import pe.edu.pucp.gdp.Login.RegisterActivity;
import pe.edu.pucp.gdp.R;

public class ClienteEditarPerfilActivity extends AppCompatActivity {

    Uri uri;
    User user;
    FirebaseFirestore firebaseFirestore =  FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    String id;
    ActivityResultLauncher<Intent> tomarFoto = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    ImageView imageView = findViewById(R.id.imageEditPerfil);
                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
                    String path =  MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),bitmap,"val",null);
                    uri = Uri.parse(path);
                    Glide.with(ClienteEditarPerfilActivity.this).load(bitmap).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);
                }
            }
    );

    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        user = intent.getSerializableExtra("user") == null ? null : (User) intent.getSerializableExtra("user");
        if(user != null){
            ImageView imageView = findViewById(R.id.imageEditPerfil);
            EditText editNombres = findViewById(R.id.editNombresPerfil);
            EditText editApellidos = findViewById(R.id.editApellidosPerfil);
            EditText editDireccion = findViewById(R.id.editDireccionPerfil);
            EditText editTelefono = findViewById(R.id.editTelefonoPerfil);

            if(uri == null){
                Glide.with(ClienteEditarPerfilActivity.this).load(storageReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid() +"/photo.jpg")).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);
            }
            editNombres.setText(user.getNombres());
            editNombres.setEnabled(false);
            editApellidos.setText(user.getApellidos());
            editApellidos.setEnabled(false);
            editDireccion.setText(user.getDireccion());
            editDireccion.setEnabled(false);
            editTelefono.setText(user.getNumero());
        }else{
            Toast.makeText(ClienteEditarPerfilActivity.this, "Error al cargar el perfil", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_editar_perfil);
        BottomNavigationView bottomNavigationView =  findViewById(R.id.bottonnav);
        navbar(bottomNavigationView, ClienteEditarPerfilActivity.this);

        ((Button) findViewById(R.id.btnTomarFoto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                tomarFoto.launch(intent);
            }
        });

        ((Button) findViewById(R.id.btnMapa)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClienteEditarPerfilActivity.this, ClienteEditarPerfilMapaActivity.class);
                EditText numero = findViewById(R.id.editTelefonoPerfil);
                if(!numero.getText().toString().isEmpty()){
                    user.setNumero(numero.getText().toString());
                }
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });

        ((Button) findViewById(R.id.btnAceptarPerfil)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText direccion = findViewById(R.id.editDireccionPerfil);
                EditText numero = findViewById(R.id.editTelefonoPerfil);
                boolean bool = true;
                if(direccion.getText().toString().isEmpty()){
                    direccion.setError("ERROR");
                    bool = false;
                }
                if(numero.getText().toString().trim().isEmpty()){
                    numero.setError("ERROR");
                    bool = false;
                }
                if(uri!=null){
                    if(bool){
                        user.setDireccion(direccion.getText().toString());
                        user.setNumero(numero.getText().toString());
                        firebaseStorage.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/photo.jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Intent intent = new Intent(ClienteEditarPerfilActivity.this,ClientePerfilActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        firebaseFirestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                }else{
                    if(bool){
                        user.setDireccion(direccion.getText().toString());
                        user.setNumero(numero.getText().toString());
                        firebaseFirestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Intent intent = new Intent(ClienteEditarPerfilActivity.this,ClientePerfilActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            }
        });
    }

    public void navbar(BottomNavigationView bottomNavigationView, Context context){
        bottomNavigationView.setSelectedItemId(R.id.productos);
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
                    intent =  new Intent(context, ClienteCarritoActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
            return true;
        });
}
}
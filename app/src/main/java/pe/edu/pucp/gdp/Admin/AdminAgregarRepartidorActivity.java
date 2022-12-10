package pe.edu.pucp.gdp.Admin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

import pe.edu.pucp.gdp.Entity.Repartidor;
import pe.edu.pucp.gdp.R;

public class AdminAgregarRepartidorActivity extends AppCompatActivity {

    Uri uri;
    FirebaseFirestore firebaseFirestore =  FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    String id;

    ActivityResultLauncher<Intent> tomarFoto = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    ImageView imageView = findViewById(R.id.imageAgregarRepartidor);
                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
                    String path =  MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),bitmap,"val",null);
                    uri = Uri.parse(path);
                    Glide.with(AdminAgregarRepartidorActivity.this).load(bitmap).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_agregar_repartidor);

        //Navbar
        BottomNavigationView bottomNavigationView =  findViewById(R.id.bottonnav);
        navbar(bottomNavigationView, AdminAgregarRepartidorActivity.this);
        //Tomar foto

        ((Button) findViewById(R.id.btnTomarFoto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                tomarFoto.launch(intent);
            }
        });

        ((Button) findViewById(R.id.btnAgregarRepartidor)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editNombre = findViewById(R.id.editNombreRepartidor);
                EditText editApellidos = findViewById(R.id.editApellidoRepartidor);
                EditText editDni = findViewById(R.id.editDniRepartidor);
                EditText editTelefono = findViewById(R.id.editTelefonoRepartidor);
                EditText editCorreo = findViewById(R.id.editCorreoRepartidor);
                boolean bool = true;
                if(editNombre.getText().toString().trim().isEmpty()){
                    editNombre.setError("No deje el nombre vacio");
                    bool = false;
                }
                if(editApellidos.getText().toString().trim().isEmpty()){
                    editApellidos.setError("No deje el apellido vacio");
                    bool = false;
                }
                if(editDni.getText().toString().trim().isEmpty()){
                    editDni.setError("No deje el dni vacio");
                    bool = false;
                }
                if(editTelefono.getText().toString().trim().isEmpty()){
                    editTelefono.setError("No deje el telefono vacio");
                    bool = false;
                }
                if(editCorreo.getText().toString().trim().isEmpty()){
                    editCorreo.setError("No deje el correo vacio");
                    bool = false;
                }
                try{
                    Integer.parseInt(editDni.getText().toString());
                }catch (NumberFormatException numberFormatException){
                    bool = false;
                    editDni.setError("Debe ser numero");
                }
                try{
                    Double.parseDouble(editTelefono.getText().toString());
                }catch (NumberFormatException numberFormatException){
                    bool = false;
                    editTelefono.setError("Debe ser numero");
                }
                if(uri == null){
                    bool = false;
                    Toast.makeText(AdminAgregarRepartidorActivity.this, "Tome una foto", Toast.LENGTH_SHORT).show();
                }

                if(bool) {
                    Repartidor repartidor = new Repartidor();
                    repartidor.setNombres(editNombre.getText().toString());
                    repartidor.setApellidos(editApellidos.getText().toString());
                    repartidor.setNumero(editTelefono.getText().toString());
                    repartidor.setCorreo(editCorreo.getText().toString());
                    repartidor.setDni(editDni.getText().toString());
                    storageReference.child("repartidores").child(repartidor.getDni()+"/photo.jpg").putFile(uri);
                    firebaseFirestore.collection("repartidores").document(repartidor.getDni()).set(repartidor).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent intent = new Intent(AdminAgregarRepartidorActivity.this,AdminListaRepartidoresActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
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
                    intent =  new Intent(context,AdminListaProductosActivity.class);
                    startActivity(intent);
                    finish();
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
}
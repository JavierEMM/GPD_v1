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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

import pe.edu.pucp.gdp.Entity.Productos;
import pe.edu.pucp.gdp.R;

public class AdminEditarProductoActivity extends AppCompatActivity {

    Uri uri;
    FirebaseFirestore firebaseFirestore =  FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    String id;
    ActivityResultLauncher<Intent> tomarFoto = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    ImageView imageView = findViewById(R.id.imageAgregarProducto);
                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
                    String path =  MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),bitmap,"val",null);
                    uri = Uri.parse(path);
                    Glide.with(AdminEditarProductoActivity.this).load(bitmap).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);
                }
            }
    );

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Productos productos = intent.getSerializableExtra("producto") == null ? null : (Productos) intent.getSerializableExtra("producto");
        if(productos != null){
            ImageView imageView = findViewById(R.id.imageAgregarProducto);
            EditText editNombrePro = findViewById(R.id.editNombreProducto);
            EditText editStockPro = findViewById(R.id.editStockProducto);
            EditText editPrecioPro = findViewById(R.id.editPrecioProducto);
            EditText saborProducto = findViewById(R.id.editSaborProducto);
            EditText descripcionProducto = findViewById(R.id.editDescripcionProducto);
            id = productos.getId();
            if(uri == null){
                Glide.with(AdminEditarProductoActivity.this).load(storageReference.child("productos").child(productos.getId()+"/photo.jpg")).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);
            }

            editNombrePro.setText(productos.getNombre());
            editStockPro.setText(productos.getStock());
            editPrecioPro.setText(productos.getPrecio());
            saborProducto.setText(productos.getSabor());
            descripcionProducto.setText(productos.getDescripcion());
        }else{
            Toast.makeText(AdminEditarProductoActivity.this, "Error al cargar el producto", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_editar_producto);

        BottomNavigationView bottomNavigationView =  findViewById(R.id.bottonnav);
        navbar(bottomNavigationView, AdminEditarProductoActivity.this);

        ((Button) findViewById(R.id.btnTomarFoto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                tomarFoto.launch(intent);
            }
        });

        ((Button) findViewById(R.id.btnEditarProducto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editNombrePro = findViewById(R.id.editNombreProducto);
                EditText editStockPro = findViewById(R.id.editStockProducto);
                EditText editPrecioPro = findViewById(R.id.editPrecioProducto);
                EditText saborProducto = findViewById(R.id.editSaborProducto);
                EditText descripcionProducto = findViewById(R.id.editDescripcionProducto);
                boolean bool = true;
                if(editNombrePro.getText().toString().trim().isEmpty()){
                    editNombrePro.setError("No deje el nombre vacio");
                    bool = false;
                }
                if(editStockPro.getText().toString().trim().isEmpty()){
                    editStockPro.setError("No deje el stock vacio");
                    bool = false;
                }
                if(editPrecioPro.getText().toString().trim().isEmpty()){
                    editPrecioPro.setError("No deje el precio vacio");
                    bool = false;
                }
                if(saborProducto.getText().toString().trim().isEmpty()){
                    saborProducto.setError("No deje el sabor vacio");
                    bool = false;
                }
                if(descripcionProducto.getText().toString().trim().isEmpty()){
                    descripcionProducto.setError("No deje la descripcion vacio");
                    bool = false;
                }
                try{
                    Integer.parseInt(editStockPro.getText().toString());
                }catch (NumberFormatException numberFormatException){
                    bool = false;
                    editStockPro.setError("Debe ser numero");
                }
                try{
                    Double.parseDouble(editPrecioPro.getText().toString());
                }catch (NumberFormatException numberFormatException){
                    bool = false;
                    editPrecioPro.setError("Debe ser numero");
                }
                if(uri == null){
                    if(bool){
                        Productos productos = new Productos();
                        productos.setNombre(editNombrePro.getText().toString());
                        productos.setStock(editStockPro.getText().toString());
                        productos.setPrecio(editPrecioPro.getText().toString());
                        productos.setSabor(saborProducto.getText().toString());
                        productos.setDescripcion(descripcionProducto.getText().toString());
                        productos.setId(id);
                        firebaseFirestore.collection("productos").document(productos.getId()).set(productos).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Intent intent = new Intent(AdminEditarProductoActivity.this,AdminListaProductosActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }else{
                    if(bool){
                        Productos productos = new Productos();
                        productos.setNombre(editNombrePro.getText().toString());
                        productos.setStock(editStockPro.getText().toString());
                        productos.setPrecio(editPrecioPro.getText().toString());
                        productos.setSabor(saborProducto.getText().toString());
                        productos.setDescripcion(descripcionProducto.getText().toString());
                        productos.setId(id);
                        storageReference.child("productos").child(productos.getId()+"/photo.jpg").putFile(uri);
                        firebaseFirestore.collection("productos").document(productos.getId()).set(productos).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Intent intent = new Intent(AdminEditarProductoActivity.this,AdminListaProductosActivity.class);
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
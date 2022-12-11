package pe.edu.pucp.gdp.Admin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import pe.edu.pucp.gdp.Entity.Pedido;
import pe.edu.pucp.gdp.Login.LoginActivity;
import pe.edu.pucp.gdp.R;

public class AdminReportesActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore =  FirebaseFirestore.getInstance();
    ActivityResultLauncher<Intent> activityForResultLauncher = registerForActivityResult(
       new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {

                    firebaseFirestore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            ArrayList<String> listaIds =  new ArrayList<>();
                            for(DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                                listaIds.add(documentSnapshot.getId());
                                Log.d("MENSAJE",documentSnapshot.getId());
                            }

                            for (String id : listaIds){
                                firebaseFirestore.collection("users").document(id).collection("pedidos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot querySnapshot) {
                                        String escribir = "";
                                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                                            Pedido pedido= documentSnapshot.toObject(Pedido.class);
                                            if(pedido != null){
                                                escribir +="NUMERO: "+pedido.getNumeroPedido();
                                                escribir +="\nUSUARIO: "+pedido.getUser().getNombres();
                                                escribir +="\nDIRECCION: "+pedido.getUser().getDireccion();
                                                escribir +="\nPRECIO: "+pedido.getPrecio()+"\n";
                                            }
                                        }
                                        try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(data.getData(), "w");
                                             FileWriter fileWriter = new FileWriter(pfd.getFileDescriptor());) {
                                            fileWriter.write(escribir);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    });
            }
        }
    }
);


    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reportes);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonnav);
        navbar(bottomNavigationView,AdminReportesActivity.this);

        ((Button) findViewById(R.id.btnCerrarSesion)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent =  new Intent(AdminReportesActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ((Button) findViewById(R.id.btnDescargaReporte)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TITLE,"archivo.txt");
                    activityForResultLauncher.launch(intent);
            }
        });
    }

    public void navbar(BottomNavigationView bottomNavigationView, Context context){
        bottomNavigationView.setSelectedItemId(R.id.reportes);
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
                    break;
            }
            return true;
        });
    }
}
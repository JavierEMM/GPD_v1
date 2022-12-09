package pe.edu.pucp.gdp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import pe.edu.pucp.gdp.Admin.AdminListaProductosActivity;
import pe.edu.pucp.gdp.Cliente.ClienteListaProductosActivity;
import pe.edu.pucp.gdp.R;
import pe.edu.pucp.gdp.Repartidor.RepartidorListaPedidosActivity;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Initialize firebase instances

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Intent intent =  null;
                    Log.d("rol",documentSnapshot.get("rol",String.class));
                    if(documentSnapshot.get("rol",String.class).equals("ROL_ADMIN")){
                        intent = new Intent(LoginActivity.this, AdminListaProductosActivity.class);
                    }
                    if(documentSnapshot.get("rol",String.class).equals("ROL_CLIENTE")){
                        intent = new Intent(LoginActivity.this, ClienteListaProductosActivity.class);
                    }
                    if(documentSnapshot.get("rol",String.class).equals("ROL_REPARTIDOR")){
                        intent = new Intent(LoginActivity.this, RepartidorListaPedidosActivity.class);
                    }
                    if(intent != null){
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this,"El usuario no tiene un rol establecido",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        //Save user

        ((Button) findViewById(R.id.btnLogin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = findViewById(R.id.edtEmail);
                EditText password = findViewById(R.id.edtPassword);
                String user = email.getText().toString();
                String strPassword = password.getText().toString();
                boolean bandera = true;
                if(email.getText().toString().isEmpty()){
                    email.setError("Ingrese su email");
                    bandera = false;
                }
                if(password.getText().toString().isEmpty()) {
                    password.setError("Ingrese su contrasena");
                    bandera = false;
                }
                if(bandera){
                    firebaseAuth.signInWithEmailAndPassword(user,strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                if(task.getResult().getUser().isEmailVerified()){
                                    firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Intent intent =  null;
                                            Log.d("rol",documentSnapshot.get("rol",String.class));
                                            if(documentSnapshot.get("rol",String.class).equals("ROL_ADMIN")){
                                                intent = new Intent(LoginActivity.this, AdminListaProductosActivity.class);
                                            }
                                            if(documentSnapshot.get("rol",String.class).equals("ROL_CLIENTE")){
                                                intent = new Intent(LoginActivity.this, ClienteListaProductosActivity.class);
                                            }
                                            if(documentSnapshot.get("rol",String.class).equals("ROL_REPARTIDOR")){
                                                intent = new Intent(LoginActivity.this, RepartidorListaPedidosActivity.class);
                                            }
                                            if(intent != null){
                                                startActivity(intent);
                                                finish();
                                            }else{
                                                Toast.makeText(LoginActivity.this,"El usuario no tiene un rol establecido",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    firebaseAuth.signOut();
                                    Toast.makeText(LoginActivity.this, "Verifique su correo", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(LoginActivity.this, "Verifica tus credenciales", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        TextView txtRegistrarse = findViewById(R.id.txtRegistrarse);
        TextView txtOlvidar = findViewById(R.id.txtOlvidar);
        txtRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        txtOlvidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(LoginActivity.this,RecuperarActivity.class);
                startActivity(intent);
            }
        });

    }
}
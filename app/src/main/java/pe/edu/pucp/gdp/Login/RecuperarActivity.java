package pe.edu.pucp.gdp.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import pe.edu.pucp.gdp.R;

public class RecuperarActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        TextView txtRegistrarse = findViewById(R.id.txtRegistrarse);
        TextView txtRegresarLogin = findViewById(R.id.txtRegresarLogin);

        txtRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(RecuperarActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        txtRegresarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(RecuperarActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ((Button) findViewById(R.id.btnRecuperar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.edtEmail);
                if(editText.getText().toString().isEmpty()){
                    Toast.makeText(RecuperarActivity.this, "", Toast.LENGTH_SHORT).show();
                }else{
                    firebaseFirestore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            String key = "";
                            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                                if(documentSnapshot.get("correo", String.class).equals(editText.getText().toString())){
                                   key = documentSnapshot.getId();
                                }
                            }
                            if(key.equals("")){
                                Toast.makeText(RecuperarActivity.this,"No se encontro el correo",Toast.LENGTH_SHORT).show();
                            }else{
                                firebaseAuth.sendPasswordResetEmail(editText.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(RecuperarActivity.this,"Se ha enviado un correo de recupercaion", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RecuperarActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

}
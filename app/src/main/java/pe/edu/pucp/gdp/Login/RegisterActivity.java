package pe.edu.pucp.gdp.Login;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContentInfo;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pe.edu.pucp.gdp.Entity.RespuestaAPI;
import pe.edu.pucp.gdp.Entity.User;
import pe.edu.pucp.gdp.R;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String latitude, longitude;
    RespuestaAPI respuestaAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Intent intent = getIntent();
        EditText direccion = findViewById(R.id.edtDireccion);
        EditText correo_listener = findViewById(R.id.edtCorreo);
        final boolean[] correo_correcto = {false};
        correo_listener.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    comprobarEmail(correo_listener.getText().toString(),correo_listener);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(respuestaAPI.getSuccess().equals("false")){
                            correo_listener.setError("Correo incorrecto");
                            correo_correcto[0] =false;
                        }
                        if(respuestaAPI.getSuccess().equals("true")){
                            correo_correcto[0] =true;
                        }
                    }
                }, 5000);
            }
        });


        direccion.setEnabled(false);
        if(intent != null){
            EditText nombres = findViewById(R.id.edtNombres);
            EditText apellidos = findViewById(R.id.edtApellidos);
            EditText numero = findViewById(R.id.edtNumeroTelefonico);
            EditText correo = findViewById(R.id.edtCorreo);
            direccion = findViewById(R.id.edtDireccion);
            User user = intent.getSerializableExtra("user") == null ? null : (User) intent.getSerializableExtra("user");
            if(user != null){
                latitude = user.getLatitude();
                longitude = user.getLongitude();
                Log.d("MENSAJE","ENTRA AQUÍ");
                nombres.setText(user.getNombres());
                apellidos.setText(user.getApellidos());
                correo.setText(user.getCorreo());
                numero.setText(user.getNumero());
                direccion.setText(user.getDireccion());
            }
        }
        ((Button) findViewById(R.id.btnMapa)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,MapaActivity.class);
                User user = new User();
                EditText nombres = findViewById(R.id.edtNombres);
                EditText apellidos = findViewById(R.id.edtApellidos);
                EditText correo = findViewById(R.id.edtCorreo);
                EditText numero = findViewById(R.id.edtNumeroTelefonico);
                if(!nombres.getText().toString().isEmpty()){
                    user.setNombres(nombres.getText().toString());
                }
                if(!apellidos.getText().toString().isEmpty()){
                    user.setApellidos(apellidos.getText().toString());
                }
                if(!correo.getText().toString().isEmpty()){
                    user.setCorreo(correo.getText().toString());
                }
                if(!numero.getText().toString().isEmpty()){
                    user.setNumero(numero.getText().toString());
                }

                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
        //FIREBASEAUTH

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        ((Button) findViewById(R.id.btnRegister)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nombres = findViewById(R.id.edtNombres);
                EditText apellidos = findViewById(R.id.edtApellidos);
                EditText correo = findViewById(R.id.edtCorreo);
                EditText contrasena = findViewById(R.id.edtContrasena);
                EditText repitecontrasena = findViewById(R.id.edtRepetirContrasena);
                EditText numero = findViewById(R.id.edtNumeroTelefonico);
                EditText direccion = findViewById(R.id.edtDireccion);
                boolean bool = true;
                if(nombres.getText().toString().isEmpty()){
                    nombres.setError("Insertar nombres");
                    bool = false;
                }
                if(apellidos.getText().toString().isEmpty()){
                    apellidos.setError("Insertar apellidos");
                    bool = false;
                }
                if(correo.getText().toString().isEmpty()){
                    correo.setError("Insertar correo");
                    bool = false;
                }else{

                }
                if(contrasena.getText().toString().isEmpty()){
                    contrasena.setError("Insertar contraseña");
                    bool = false;
                }
                if(repitecontrasena.getText().toString().isEmpty()){
                    repitecontrasena.setError("Repite la contraseña");
                    bool = false;
                }
                if(numero.getText().toString().isEmpty()){
                    numero.setError("Insertar numero");
                    bool = false;
                }
                if(direccion.getText().toString().isEmpty()){
                    direccion.setError("Insertar direccion");
                    bool = false;
                }
                if(!contrasena.getText().toString().equals(repitecontrasena.getText().toString())){
                    repitecontrasena.setError("Las contraseñas no coinciden");
                    bool = false;
                }
                if(contrasena.getText().toString().length() < 6){
                    contrasena.setError("La contraseña de be tener mas de 6 caracteres");
                    bool =false;
                }
                if(bool && correo_correcto[0]){
                    firebaseAuth.createUserWithEmailAndPassword(correo.getText().toString(),contrasena.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Log.d("MENSAJE","ENTRA AQUÍ");
                            firebaseAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("MENSAJE","ENTRA AQUÍ 2");
                                    Toast.makeText(RegisterActivity.this, "Se ha enviado un correo de confirmación a su correo", Toast.LENGTH_SHORT).show();
                                    User user = new User(nombres.getText().toString().trim(),apellidos.getText().toString().trim(),correo.getText().toString().trim(),numero.getText().toString().trim(),direccion.getText().toString().trim(),"ROL_CLIENTE");
                                    user.setLatitude(latitude);
                                    user.setLongitude(longitude);
                                    firebaseFirestore.collection("users").document(authResult.getUser().getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                firebaseAuth.signOut();
                                                Toast.makeText(RegisterActivity.this, "Se ha creado su usuario", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Log.d("MENSAJE","ERROR FIREBASEFIRESTORE");
                                            }

                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });

        TextView txtRegresarLogin = findViewById(R.id.txtRegresarLogin);
        txtRegresarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void comprobarEmail(String email, EditText editText) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        String url = "https://gepd.azurewebsites.net/api/FunctionGPD";
        Map<String,String> params = new HashMap<>();
        params.put("email",email);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(params),new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                respuestaAPI = new RespuestaAPI();
                try {
                    respuestaAPI.setSuccess(response.getString("success"));
                    respuestaAPI.setData(response.getString("data"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("change Pass response -->> " + error.toString());
            }
        });
        queue.add(jsonRequest);
    }

}
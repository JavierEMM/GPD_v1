package pe.edu.pucp.gdp.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pe.edu.pucp.gdp.R;

public class AdminReportesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reportes);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonnav);
        navbar(bottomNavigationView,AdminReportesActivity.this);
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
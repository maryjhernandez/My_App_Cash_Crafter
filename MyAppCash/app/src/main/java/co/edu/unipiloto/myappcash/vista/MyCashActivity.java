package co.edu.unipiloto.myappcash.vista;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import co.edu.unipiloto.myappcash.R;

public class MyCashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cash);

        // Configura el botón para ir a VistaIngresosActivity
        findViewById(R.id.btn_ingreso).setOnClickListener(v -> {
            Intent intent = new Intent(MyCashActivity.this, VistaIngresosActivity.class);
            startActivity(intent);
        });

        // Configura el botón para ir a GastosActivity
        findViewById(R.id.btn_gastos).setOnClickListener(v -> {
            Intent intent = new Intent(MyCashActivity.this, VistaGastosActivity.class);
            startActivity(intent);

        });

        // Configura el botón para ir a AhorrosActivity
        findViewById(R.id.btn_ahorros).setOnClickListener(v -> {
            Intent intent = new Intent(MyCashActivity.this, VistaAhorrosActivity.class);
            startActivity(intent);
        });
    }
}

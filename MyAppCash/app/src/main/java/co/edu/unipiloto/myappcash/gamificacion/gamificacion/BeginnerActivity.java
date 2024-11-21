package co.edu.unipiloto.myappcash.gamificacion.gamificacion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.myappcash.R;
import co.edu.unipiloto.myappcash.gamificacion.gamificacion.educacion.infoActivity;

public class BeginnerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginner);

        // Asignar el EditText de búsqueda a su ID
        EditText searchEditText = findViewById(R.id.searchEditText);

        // Botón de Ahorro
        Button ahorroButton = findViewById(R.id.ahorroButton);
        ahorroButton.setOnClickListener(v -> {
            Intent intent = new Intent(BeginnerActivity.this, infoActivity.class);
            startActivity(intent);
        });

        // Botón de Presupuestación
        Button presupuestacionButton = findViewById(R.id.presupuestacionButton);
        presupuestacionButton.setOnClickListener(v -> {
            Intent intent = new Intent(BeginnerActivity.this, infoActivity.class);
            startActivity(intent);
        });

        // Botón de Hábitos Financieros
        Button habitosFinancierosButton = findViewById(R.id.habitosFinancierosButton);
        habitosFinancierosButton.setOnClickListener(v -> {
            Intent intent = new Intent(BeginnerActivity.this, infoActivity.class);
            startActivity(intent);
        });

        // Botón de Conocimientos Básicos
        Button conocimientosBasicosButton = findViewById(R.id.conocimientosBasicosButton);
        conocimientosBasicosButton.setOnClickListener(v -> {
            Intent intent = new Intent(BeginnerActivity.this, infoActivity.class);
            startActivity(intent);
        });
    }
}

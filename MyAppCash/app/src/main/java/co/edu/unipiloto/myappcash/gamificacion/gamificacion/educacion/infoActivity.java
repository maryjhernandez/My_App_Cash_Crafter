package co.edu.unipiloto.myappcash.gamificacion.gamificacion.educacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.myappcash.R;
import co.edu.unipiloto.myappcash.gamificacion.gamificacion.educacion.NivelUno.NUAhorroDosActivity;
import co.edu.unipiloto.myappcash.gamificacion.gamificacion.educacion.NivelUno.NUAhorroTresActivity;
import co.edu.unipiloto.myappcash.gamificacion.gamificacion.educacion.NivelUno.NUAhorroUnoActivity;

public class infoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);  // Cambiado a activity_info.xml

        // Configuración de la barra de búsqueda
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Manejar la búsqueda según la consulta 'query'
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Manejar cambios en el texto de la barra de búsqueda
                return false;
            }
        });

        // Botón Nivel Uno
        Button nivelUnoButton = findViewById(R.id.buttonNivelUno);
        nivelUnoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(infoActivity.this, NUAhorroUnoActivity.class);
                startActivity(intent);
            }
        });

        // Botón Nivel Dos
        Button nivelDosButton = findViewById(R.id.buttonNivelDos);
        nivelDosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(infoActivity.this, NUAhorroDosActivity.class);
                startActivity(intent);
            }
        });

        // Botón Nivel Tres
        // Botón Nivel Tres
        Button nivelTresButton = findViewById(R.id.buttonNivelTres);
        nivelTresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(infoActivity.this, NUAhorroTresActivity.class);
                startActivity(intent);
            }
        });

    }
}

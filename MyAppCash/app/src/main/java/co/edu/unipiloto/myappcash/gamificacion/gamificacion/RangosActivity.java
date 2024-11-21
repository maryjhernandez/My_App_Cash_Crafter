package co.edu.unipiloto.myappcash.gamificacion.gamificacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import co.edu.unipiloto.myappcash.R;

public class RangosActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardBeginner, cardExplorer, cardStrategist, cardMaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rangos);

        // Asignar los CardViews a sus respectivos IDs
        cardBeginner = findViewById(R.id.cardBeginner);
        cardExplorer = findViewById(R.id.cardExplorer);
        cardStrategist = findViewById(R.id.cardStrategist);
        cardMaster = findViewById(R.id.cardMaster);

        // Asignar click listeners a cada uno de los CardViews
        cardBeginner.setOnClickListener(this);
        cardExplorer.setOnClickListener(this);
        cardStrategist.setOnClickListener(this);
        cardMaster.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        // Determinar qué CardView fue seleccionado y lanzar la actividad correspondiente
        if (v.getId() == R.id.cardBeginner) {
            intent = new Intent(this, BeginnerActivity.class);
        } else if (v.getId() == R.id.cardExplorer) {
            intent = new Intent(this, ExplorerActivity.class);
        } else if (v.getId() == R.id.cardStrategist) {
            intent = new Intent(this, StrategistActivity.class);
        } else if (v.getId() == R.id.cardMaster) {
            intent = new Intent(this, MasterActivity.class);
        }

        // Si se ha seleccionado una actividad, lanzarla
        if (intent != null) {
            startActivity(intent);
        }
    }
}

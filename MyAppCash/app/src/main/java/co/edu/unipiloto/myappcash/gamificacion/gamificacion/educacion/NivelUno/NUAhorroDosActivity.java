

package co.edu.unipiloto.myappcash.gamificacion.gamificacion.educacion.NivelUno;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.myappcash.R;

public class NUAhorroDosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        EdgeToEdge.enable(this);


        setContentView(R.layout.activity_nuahorro_dos);
    }

    @Override
    public void onBackPressed() {

    }
}

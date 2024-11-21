package co.edu.unipiloto.myappcash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);

        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.barraDeCarga);
        progressBar.setVisibility(View.GONE);
        if (auth.getCurrentUser()!=null){
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Por favor espere, se esta iniciando", Toast.LENGTH_SHORT).show();
        }

    }

    public void login(View view) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }


    public void regristraseU(View view) {
        Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
        startActivity(intent);
    }
}
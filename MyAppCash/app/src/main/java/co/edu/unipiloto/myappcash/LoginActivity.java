package co.edu.unipiloto.myappcash;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Button ingresa;
    EditText correo, contrasena;
    TextView registarse;
    FirebaseAuth auth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.barraDeCarga);
        progressBar.setVisibility(View.GONE);
        ingresa = findViewById(R.id.btningresar);
        correo = findViewById(R.id.txtemail);
        contrasena = findViewById(R.id.txtclave);
        registarse = findViewById(R.id.lnlregistrar);

        // Navegación al registro
        registarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
            }
        });

        // Inicio de sesión
        ingresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String usuarioCorreo = correo.getText().toString().trim();
        String usuarioClave = contrasena.getText().toString().trim();

        // Validación de campos
        if (TextUtils.isEmpty(usuarioCorreo)) {
            Toast.makeText(this, "Por favor ingrese su correo", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(usuarioClave)) {
            Toast.makeText(this, "Por favor ingrese su contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE); // Mostrar barra de progreso

        // Inicio de sesión con Firebase
        auth.signInWithEmailAndPassword(usuarioCorreo, usuarioClave)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE); // Ocultar barra de progreso
                        if (task.isSuccessful()) {
                            // Verificar si el correo está verificado
                            if (auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Por favor, verifica tu correo antes de iniciar sesión.", Toast.LENGTH_SHORT).show();
                                auth.signOut(); // Desconectar al usuario no verificado
                            }
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                            Toast.makeText(LoginActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}

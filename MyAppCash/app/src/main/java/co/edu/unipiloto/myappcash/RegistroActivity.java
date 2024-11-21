package co.edu.unipiloto.myappcash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
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
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

import co.edu.unipiloto.myappcash.models.UserModel;

public class RegistroActivity extends AppCompatActivity {

    Button registro;
    EditText nombre, correo, usuario, contrasena;
    TextView inicioS, passwordStrengthTextView;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressBar = findViewById(R.id.barraDeCarga);
        progressBar.setVisibility(View.GONE);

        registro = findViewById(R.id.btnregistrar);
        nombre = findViewById(R.id.txtnomapellidos);
        correo = findViewById(R.id.txtemail);
        usuario = findViewById(R.id.txtusuario);
        contrasena = findViewById(R.id.txtclave);
        inicioS = findViewById(R.id.lbliniciarsesion);
        passwordStrengthTextView = findViewById(R.id.password_strength);

        // Evaluar la fuerza de la contraseña
        contrasena.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordStrengthTextView.setText(checkPasswordStrength(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        inicioS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistroActivity.this, LoginActivity.class));
            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                crearUsuario();
            }
        });
    }

    private void crearUsuario() {
        String usuarioNombre = nombre.getText().toString().trim();
        String usuarioCorreo = correo.getText().toString().trim();
        String usuarioId = usuario.getText().toString().trim();
        String usuarioClave = contrasena.getText().toString().trim();

        // Validar campos
        if (TextUtils.isEmpty(usuarioNombre)) {
            Toast.makeText(this, "Nombre vacío", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(usuarioCorreo)) {
            Toast.makeText(this, "Correo vacío", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(usuarioCorreo).matches()) {
            Toast.makeText(this, "Por favor, ingrese un email válido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidEmailDomain(usuarioCorreo)) {
            Toast.makeText(this, "Solo se permiten correos de Gmail o Hotmail", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(usuarioId)) {
            Toast.makeText(this, "Usuario vacío", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(usuarioClave)) {
            Toast.makeText(this, "Contraseña vacía", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidPassword(usuarioClave)) {
            Toast.makeText(this, "La contraseña no cumple con los requisitos de seguridad", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creación del usuario
        auth.createUserWithEmailAndPassword(usuarioCorreo, usuarioClave)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserModel userModel = new UserModel(usuarioNombre, usuarioCorreo, usuarioId, usuarioClave);
                            String id = task.getResult().getUser().getUid();
                            database.getReference().child("Usuario").child(id).setValue(userModel);

                            // Enviar correo de verificación
                            auth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressBar.setVisibility(View.GONE);
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegistroActivity.this,
                                                        "Registro Exitoso. Verifica tu correo para entrar en Cash Crafter.",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(RegistroActivity.this,
                                                        "Error al enviar el correo de verificación: " + task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegistroActivity.this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$");
        return pattern.matcher(password).matches();
    }

    private String checkPasswordStrength(String password) {
        if (password.length() < 8) {
            return "Débil";
        } else if (password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).*$")) {
            return "Muy Fuerte";
        } else if (password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$")) {
            return "Fuerte";
        } else if (password.matches("^(?=.*[a-z])(?=.*[A-Z]).*$") ||
                password.matches("^(?=.*[a-z])(?=.*\\d).*$") ||
                password.matches("^(?=.*[A-Z])(?=.*\\d).*$")) {
            return "Media";
        } else {
            return "Débil";
        }
    }

    // Método para validar el dominio del correo electrónico
    private boolean isValidEmailDomain(String email) {
        return email.endsWith("@gmail.com") || email.endsWith("@hotmail.com");
    }
}

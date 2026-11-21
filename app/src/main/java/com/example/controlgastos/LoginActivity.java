package com.example.controlgastos;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etCorreo, etPass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            irAMain();
            return;
        }

        etCorreo = findViewById(R.id.etCorreoLogin);
        etPass = findViewById(R.id.etPassLogin);
        Button btnIngresar = findViewById(R.id.btnIniciarSesion);
        Button btnIrRegistro = findViewById(R.id.btnIrRegistro);

        btnIngresar.setOnClickListener(v -> iniciarSesion());
        btnIrRegistro.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void iniciarSesion() {
        String correo = etCorreo.getText().toString().trim();
        String pass = etPass.getText().toString().trim();

        if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Completa correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(correo, pass)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
                    irAMain();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void irAMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
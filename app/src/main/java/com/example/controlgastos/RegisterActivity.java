package com.example.controlgastos;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText etCorreo, etPass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        etCorreo = findViewById(R.id.etCorreoReg);
        etPass = findViewById(R.id.etPassReg);
        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        Button btnVolver = findViewById(R.id.btnVolverLogin);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void registrarUsuario() {
        String correo = etCorreo.getText().toString().trim();
        String pass = etPass.getText().toString().trim();

        if (TextUtils.isEmpty(correo) || pass.length() < 6) {
            Toast.makeText(this, "Ingresa un correo válido y contraseña de al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(correo, pass)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
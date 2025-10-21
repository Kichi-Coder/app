package com.devst.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    EditText txtNombre, txtCorreo;
    Button btnGuardarPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        txtNombre = findViewById(R.id.txtNombre);
        txtCorreo = findViewById(R.id.txtCorreo);
        btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil);

        // Recibir el correo que viene desde el login o home
        String email = getIntent().getStringExtra("email_usuario");

        // Acceder a las preferencias locales
        SharedPreferences prefs = getSharedPreferences("PerfilUsuario", MODE_PRIVATE);

        // Cargar valores guardados si existen
        String nombreGuardado = prefs.getString("nombreUsuario", "");
        String correoGuardado = prefs.getString("correoUsuario", "");

        // Mostrar en pantalla los datos guardados o los nuevos
        if (!correoGuardado.isEmpty()) {
            txtCorreo.setText(correoGuardado);
        } else if (email != null) {
            txtCorreo.setText(email);
        }

        if (!nombreGuardado.isEmpty()) {
            txtNombre.setText(nombreGuardado);
        }

        // Botón Guardar
        btnGuardarPerfil.setOnClickListener(v -> {
            String nombre = txtNombre.getText().toString().trim();
            String correo = txtCorreo.getText().toString().trim();

            if (nombre.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa tu nombre", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardar en SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nombreUsuario", nombre);
            editor.putString("correoUsuario", correo);
            editor.apply();

            // Crear Intent de respuesta hacia HomeActivity
            Intent data = new Intent();
            data.putExtra("nombreUsuario", nombre);
            data.putExtra("correoUsuario", correo);

            setResult(RESULT_OK, data);
            finish();
        });
    }
}
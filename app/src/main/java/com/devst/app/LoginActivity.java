package com.devst.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    // Campos de entrada para correo y contraseña
    private TextInputEditText edtEmail, edtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);                  // Ajusta el contenido a los bordes (status/nav bar)
        setContentView(R.layout.activity_login);  // Infla el layout de login

        // Referencias a vistas
        edtEmail = findViewById(R.id.edtEmail);
        edtPass  = findViewById(R.id.edtPass);
        Button btnLogin = findViewById(R.id.btnLogin);

        // Al tocar el botón, intento validar y “loguear”
        btnLogin.setOnClickListener(v -> intentoInicioSesion());

        // Enlaces informativos (a futuro se pueden reemplazar por Activities reales)
        findViewById(R.id.tvRecuperarpass).setOnClickListener(v ->
                Toast.makeText(this, "Función pendiente: recuperar contraseña", Toast.LENGTH_SHORT).show());
        findViewById(R.id.tvCrear).setOnClickListener(v ->
                Toast.makeText(this, "Función pendiente: crear cuenta", Toast.LENGTH_SHORT).show());
    }

    // Orquesto el flujo de validación simple y, si pasa, navego a HomeActivity
    private void intentoInicioSesion() {
        String email = edtEmail.getText().toString().trim();
        String pass  = edtPass.getText().toString();

        // Limpio errores previos
        edtEmail.setError(null);
        edtPass.setError(null);

        // Validaciones básicas del formulario
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Ingresa tu correo");
            edtEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Correo inválido");
            edtEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            edtPass.setError("Ingresa tu contraseña");
            edtPass.requestFocus();
            return;
        }
        if (pass.length() < 6) {
            edtPass.setError("Mínimo 6 caracteres");
            edtPass.requestFocus();
            return;
        }

        // Simulación de autenticación
        // Para la demo comparo contra credenciales fijas.
        boolean ok = email.equals("estudiante@st.cl") && pass.equals("123456");

        if (ok) {
            Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();

            // Navego a HomeActivity y le paso el email para el saludo
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("email_usuario", email);
            startActivity(intent);

            // Cierro Login para que al back no vuelva a esta pantalla
            finish();
        } else {
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        }
    }
}
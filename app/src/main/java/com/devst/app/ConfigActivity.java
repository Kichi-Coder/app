package com.devst.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ConfigActivity extends AppCompatActivity {

    // Necesitamos estos campos para conectar con el layout
    private TextInputEditText etUrl, etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // Conectamos las cajas de texto y el botón con sus IDs en el XML
        etUrl   = findViewById(R.id.etUrl);
        etPhone = findViewById(R.id.etPhone);
        MaterialButton btnGuardar = findViewById(R.id.btnGuardar);

        // Vamos a buscar los datos guardados. Es importante usar el mismo nombre de archivo
        // que HomeActivity espera (Prefs.FILE_CONFIG).
        SharedPreferences prefs = getSharedPreferences(Prefs.FILE_CONFIG, MODE_PRIVATE);

        // Cargamos la URL y el teléfono guardados. Si están vacíos, usamos un valor por defecto.
        String url   = prefs.getString(Prefs.KEY_URL, "https://www.santotomas.cl");
        String phone = prefs.getString(Prefs.KEY_PHONE, "+56984715248");

        // Ponemos los valores cargados en las cajas de texto para que el usuario los vea
        etUrl.setText(url);
        etPhone.setText(phone);

        // Lo que sucede cuando el usuario presiona el botón "Guardar"
        btnGuardar.setOnClickListener(v -> {
            // Obtenemos el texto nuevo y limpiamos los espacios innecesarios
            String nuevaUrl  = String.valueOf(etUrl.getText()).trim();
            String nuevoFono = String.valueOf(etPhone.getText()).trim();

            // Verificamos que el usuario haya llenado ambos campos
            if (TextUtils.isEmpty(nuevaUrl) || TextUtils.isEmpty(nuevoFono)) {
                Toast.makeText(this, "¡Ups! Tienes que completar los dos campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardamos los nuevos datos usando las claves correctas (Prefs.KEY_URL, etc.)
            prefs.edit()
                    .putString(Prefs.KEY_URL, nuevaUrl)
                    .putString(Prefs.KEY_PHONE, nuevoFono)
                    .apply(); // apply() guarda el cambio en segundo plano, ¡es rápido!

            Toast.makeText(this, "¡Configuración guardada! Ya puedes usarla.", Toast.LENGTH_SHORT).show();

            // Le decimos a la actividad Home que todo salió bien (RESULT_OK)
            setResult(RESULT_OK);

            // Cerramos esta pantalla
            finish();
        });
    }

    // Si el usuario usa el botón de flecha hacia atrás en la barra de la aplicación
    @Override
    public boolean onSupportNavigateUp() {
        // Simplemente cerramos la pantalla
        finish();
        return true;
    }
}
package com.devst.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HomeActivity extends AppCompatActivity {

    // El email del usuario que viene de la pantalla de Login
    private String emailUsuario = "";

    // El espacio donde mostramos el mensaje de bienvenida
    private TextView tvBienvenida;

    // Las preferencias para saber si el usuario ya guardó un nombre en su perfil
    private SharedPreferences perfilPrefs;

    // Gestores de Resultados (ActivityResult API)

    // Abrimos la pantalla de Perfil y esperamos que nos devuelva el nombre editado
    private final ActivityResultLauncher<Intent> editarPerfilLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                // Revisamos si la pantalla de Perfil nos devolvió un resultado exitoso
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Tomamos el nombre usando la clave única que definimos en Prefs
                    String nombre = result.getData().getStringExtra(Prefs.INTENT_NOMBRE_EDITADO);
                    if (nombre != null && !nombre.isEmpty()) {
                        // ¡Listo! Actualizamos el saludo con el nuevo nombre
                        tvBienvenida.setText("Hola, " + nombre);
                    }
                }
            });

    // Abrimos la pantalla de Configuración
    private final ActivityResultLauncher<Intent> configLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                // Cuando el usuario regresa, mostramos una confirmación
                Toast.makeText(this, "Configuración actualizada", Toast.LENGTH_SHORT).show();
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hacemos que la interfaz use toda la pantalla, incluyendo las barras del sistema
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Preparamos la referencia para leer los datos guardados del perfil
        perfilPrefs = getSharedPreferences(Prefs.FILE_PERFIL, MODE_PRIVATE);

        // Configuración de la Barra Superior
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Referencias a los widgets
        tvBienvenida           = findViewById(R.id.tvBienvenida);
        Button btnIrPerfil     = findViewById(R.id.btnIrPerfil);
        Button btnAbrirWeb     = findViewById(R.id.btnAbrirWeb);
        Button btnEnviarCorreo = findViewById(R.id.btnEnviarCorreo);
        Button btnCompartir    = findViewById(R.id.btnCompartir);
        Button btnCamara       = findViewById(R.id.btnCamara);
        Button btnMapa         = findViewById(R.id.btnMapa);
        Button btnTelefono     = findViewById(R.id.btnTelefono);
        Button btnConfig       = findViewById(R.id.btnConfig);

        // Tomamos el email que nos pasó la pantalla de Login
        emailUsuario = getIntent().getStringExtra("email_usuario");
        if (emailUsuario == null) emailUsuario = "";

        // Mostramos el mensaje de bienvenida, usando el nombre si ya está guardado
        actualizarSaludoInicial();


        // ACCIÓN: Ir a Perfil (Intent Explícito)
        btnIrPerfil.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, PerfilActivity.class);
            // Pasamos el email actual por si el usuario aún no lo ha guardado
            i.putExtra(Prefs.KEY_CORREO, emailUsuario);
            // Iniciamos la pantalla esperando que nos devuelva el nombre
            editarPerfilLauncher.launch(i);
        });

        // ACCIÓN: Abrir Sitio Web (Intent Implícito)
        btnAbrirWeb.setOnClickListener(v -> {
            String url = getUrlPredeterminada(); // Buscamos la URL en la configuración

            // Si la URL está vacía, usamos un valor seguro por defecto
            if (url == null || url.trim().isEmpty()) {
                url = "https://www.santotomas.cl";
            }

            // Nos aseguramos de que la URL empiece con http:// o https://
            url = url.trim();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }

            Uri webpage = Uri.parse(url);
            // Pedimos al sistema que abra esta dirección web
            Intent viewWeb = new Intent(Intent.ACTION_VIEW, webpage);
            viewWeb.addCategory(Intent.CATEGORY_BROWSABLE);

            try {
                startActivity(viewWeb);
            } catch (Exception e) {
                // Si no hay ninguna app que pueda abrir la web, avisamos
                Toast.makeText(this, "No hay navegador disponible", Toast.LENGTH_SHORT).show();
            }
        });

        // ACCIÓN: Enviar Correo (Intent Implícito)
        btnEnviarCorreo.setOnClickListener(v -> {
            Intent email = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
            // Usamos el email del usuario como destinatario
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailUsuario});
            email.putExtra(Intent.EXTRA_SUBJECT, "Prueba desde la app");
            email.putExtra(Intent.EXTRA_TEXT, "Hola, esto es un intento de correo.");
            // Mostramos un selector para que elija su app de correo
            startActivity(Intent.createChooser(email, "Enviar correo con:"));
        });

        // ACCIÓN: Compartir Texto (Intent Implícito)
        btnCompartir.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, "Hola desde mi app Android 😎");
            startActivity(Intent.createChooser(share, "Compartir usando:"));
        });

        // ACCIÓN: Abrir Cámara (Intent Explícito)
        btnCamara.setOnClickListener(v ->
                startActivity(new Intent(this, CamaraActivity.class))
        );

        // ACCIÓN: Abrir Mapa (Intent Implícito)
        btnMapa.setOnClickListener(v -> {
            String direccion = "Vergara 165, 8370014 Santiago, Región Metropolitana";

            // Creamos la URI para que Google Maps busque una dirección
            Uri gmm = Uri.parse("geo:0,0?q=" + Uri.encode(direccion));
            Intent maps = new Intent(Intent.ACTION_VIEW, gmm);
            maps.setPackage("com.google.android.apps.maps"); // Intentamos abrir la app de Google Maps

            if (maps.resolveActivity(getPackageManager()) != null) {
                startActivity(maps);
            } else {
                // Si Maps no está, abrimos la búsqueda en el navegador como alternativa
                Uri web = Uri.parse("http://maps.google.com/search?q=" + Uri.encode(direccion));
                startActivity(new Intent(Intent.ACTION_VIEW, web));
            }
        });

        // ACCIÓN: Abrir Marcador Telefónico (Intent Implícito)
        btnTelefono.setOnClickListener(v -> {
            String telefono = getTelefonoPredeterminado(); // Obtenemos el número de la configuración
            // ACTION_DIAL abre el teclado del teléfono con el número cargado
            Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telefono));
            startActivity(dial);
        });

        // ACCIÓN: Abrir Configuración (Intent Explícito)
        btnConfig.setOnClickListener(v ->
                // Usamos el lanzador para que ConfigActivity nos avise cuando regrese
                configLauncher.launch(new Intent(this, ConfigActivity.class))
        );
    }

    // Revisa si hay un nombre guardado y lo usa para el saludo, si no, usa el email
    private void actualizarSaludoInicial() {
        // Lee el nombre guardado con la clave Prefs.KEY_NOMBRE
        String nombreGuardado = perfilPrefs.getString(Prefs.KEY_NOMBRE, null);

        if (nombreGuardado != null && !nombreGuardado.isEmpty()) {
            tvBienvenida.setText("Hola, " + nombreGuardado);
        } else {
            tvBienvenida.setText("Bienvenido: " + emailUsuario);
        }
    }


    // --- Menú (opciones toolbar) ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflamos el menú de la barra superior
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_perfil) {
            // Abrir Perfil desde el menú (mismo Intent que el botón)
            Intent i = new Intent(this, PerfilActivity.class);
            i.putExtra(Prefs.KEY_CORREO, emailUsuario);
            editarPerfilLauncher.launch(i);
            return true;
        } else if (id == R.id.action_web) {
            // Abrir la documentación de Android
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com")));
            return true;
        } else if (id == R.id.action_salir) {
            // Cerramos esta pantalla
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Busca la URL en el archivo de configuración usando la clave KEY_URL
    private String getUrlPredeterminada() {
        SharedPreferences prefs = getSharedPreferences(Prefs.FILE_CONFIG, MODE_PRIVATE);
        return prefs.getString(Prefs.KEY_URL, "https://www.santotomas.cl");
    }

    // Busca el teléfono en el archivo de configuración usando la clave KEY_PHONE
    private String getTelefonoPredeterminado() {
        SharedPreferences prefs = getSharedPreferences(Prefs.FILE_CONFIG, MODE_PRIVATE);
        return prefs.getString(Prefs.KEY_PHONE, "+56984715248");
    }
}
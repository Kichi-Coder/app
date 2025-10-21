package com.devst.app;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CamaraActivity extends AppCompatActivity {

    // Donde mostramos la foto tomada
    private ImageView imagenPrevia;
    // El botón para guardar la foto en la galería (MediaStore)
    private Button btnGuardarGaleria;
    // Archivo temporal donde la cámara escribe la imagen capturada
    private File archivoTemporal;
    // URI segura (FileProvider) para pasarle a la cámara y para mostrar preview
    private Uri urlImagen;
    // Formato para nombrar archivos con timestamp
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());


    //LANZADORES DE ACTIVIDAD

    private final ActivityResultLauncher<String> permisoCamaraLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) checkPermisoCamaraYAlmacenamiento();
                else Toast.makeText(this, "¡Uy! Necesitas dar permiso a la cámara.", Toast.LENGTH_SHORT).show();
            });

    private final ActivityResultLauncher<String> permisoAlmacenamientoLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) tomarFoto();
                else Toast.makeText(this, "Permiso de almacenamiento denegado.", Toast.LENGTH_SHORT).show();
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && urlImagen != null) {
                    imagenPrevia.setImageURI(urlImagen);
                    btnGuardarGaleria.setEnabled(true);
                    Toast.makeText(this, "Foto capturada. Presiona GUARDAR.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Captura cancelada", Toast.LENGTH_SHORT).show();
                    btnGuardarGaleria.setEnabled(false);
                    if (archivoTemporal != null && archivoTemporal.exists()) {
                        archivoTemporal.delete();
                        archivoTemporal = null;
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camara);

        Button btnTomarFoto = findViewById(R.id.btnTomarFoto);
        imagenPrevia = findViewById(R.id.imgPreview);
        btnGuardarGaleria = findViewById(R.id.btnGuardar);

        btnGuardarGaleria.setEnabled(false);

        btnTomarFoto.setOnClickListener(v -> checkPermisoCamaraYAlmacenamiento());

        btnGuardarGaleria.setOnClickListener(v -> {
            saveFileToPublicStorageModern();
            btnGuardarGaleria.setEnabled(false);
        });
    }

    //  REVISIÓN DE PERMISOS

    private void checkPermisoCamaraYAlmacenamiento() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permisoCamaraLauncher.launch(Manifest.permission.CAMERA);
            return;
        }

        // Revisamos el permiso de escritura solo si es Android 9 o anterior
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permisoAlmacenamientoLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return;
            }
        }

        tomarFoto();
    }

    // Inicia la cámara creando antes un archivo temporal y pasándole su URI al intent de captura
    private void tomarFoto() {
        try {
            archivoTemporal = crearArchivoImagen();

            urlImagen = FileProvider.getUriForFile(
                    this, getPackageName() + ".fileprovider", archivoTemporal);

            takePictureLauncher.launch(urlImagen);
        } catch (IOException e) {
            Toast.makeText(this, "No se pudo iniciar la cámara: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Genera un archivo temporal único en la carpeta Pictures privada de la app
    private File crearArchivoImagen() throws IOException {
        String timeStamp = dateFormat.format(new Date());
        String nombre = "IMG_" + timeStamp + "_";
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(nombre, ".jpg", dir);
    }


    // Guarda el archivo temporal en la galería (MediaStore) en Pictures/AppName
    // Compatible con scoped storage (API modernas)
    private void saveFileToPublicStorageModern() {
        if (archivoTemporal == null || !archivoTemporal.exists()) {
            Toast.makeText(this, "Error: No hay foto para guardar.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "IMG_" + dateFormat.format(new Date()) + ".jpg";

        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();

        // Le damos metadatos al sistema
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        // Indicamos la carpeta pública: Pictures con el nombre de la app
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_name));

        Uri imageUri = null;
        OutputStream outputStream = null;

        try {
            // Creamos la entrada en MediaStore y obtenemos la URI pública
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (imageUri == null) throw new IOException("Fallo al crear la entrada MediaStore.");

            outputStream = resolver.openOutputStream(imageUri);
            if (outputStream == null) throw new IOException("Fallo al abrir OutputStream.");

            // Copiamos los datos del archivo temporal al archivo público
            try (InputStream inputStream = new FileInputStream(archivoTemporal)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }

            Toast.makeText(this, "¡Foto guardada!", Toast.LENGTH_LONG).show();

            // Abrimos la galería para que el usuario vea su foto
            redirigirAGaleria(imageUri);

            // Limpieza: eliminamos el temporal
            archivoTemporal.delete();
            archivoTemporal = null;

        } catch (IOException e) {
            e.printStackTrace();
            // Si falla, borramos la entrada incompleta de la galería
            if (imageUri != null) {
                resolver.delete(imageUri, null, null);
            }
            Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // Ignorar
                }
            }
        }
    }

    // Intenta abrir la imagen guardada con una app de galería
    private void redirigirAGaleria(Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo abrir la galería.", Toast.LENGTH_SHORT).show();
        }
    }
}
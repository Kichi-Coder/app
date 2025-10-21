package com.devst.app;

public final class Prefs {

    private Prefs() {}

    // NOMBRES DE ARCHIVOS XML
    public static final String FILE_CONFIG  = "app_config_prefs";
    public static final String FILE_PERFIL  = "perfil_usuario_prefs";

    // CLAVES DE CONFIGURACIÓN
    public static final String KEY_URL   = "pref_url";
    public static final String KEY_PHONE = "pref_phone";

    // CLAVES DE PERFIL
    public static final String KEY_NOMBRE = "nombreUsuario";
    public static final String KEY_CORREO = "correoUsuario";

    // CLAVE DE INTENT
    public static final String INTENT_NOMBRE_EDITADO = KEY_NOMBRE;
}
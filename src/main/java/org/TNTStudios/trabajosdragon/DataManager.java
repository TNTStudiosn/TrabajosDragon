package org.TNTStudios.trabajosdragon;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.TNTStudios.trabajosdragon.entidades.*;
import org.TNTStudios.trabajosdragon.trabajos.*;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.UUID;

/**
 * Clase para manejar la carga y el guardado de datos persistentes.
 */
public class DataManager {
    private static final Gson gson = new Gson();
    private static final String DATA_DIR = "config/trabajosdragon";
    private static final String TRABAJOS_FILE = "trabajos.json";
    private static final String LIMITES_FILE = "limites.json";
    private static LocalDate ultimaFechaGuardada = obtenerFechaCDMX();

    /**
     * Inicializa el DataManager registrando los eventos de carga y guardado.
     */
    public static void initialize() {
        // Crear el directorio de datos si no existe
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        // Cargar datos al iniciar el servidor
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            loadData();
            verificarResetDiario();
        });

        // Guardar datos al detener el servidor
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> saveData());
    }

    /**
     * Guarda los datos actuales en archivos JSON.
     */
    public static void saveData() {
        saveTrabajos(new File(DATA_DIR, TRABAJOS_FILE));
        saveLimites(new File(DATA_DIR, LIMITES_FILE));
    }

    /**
     * Carga los datos desde archivos JSON.
     */
    public static void loadData() {
        loadTrabajos(new File(DATA_DIR, TRABAJOS_FILE));
        loadLimites(new File(DATA_DIR, LIMITES_FILE));
    }

    /**
     * Guarda el mapa de trabajos en un archivo JSON.
     */
    private static void saveTrabajos(File file) {
        try (Writer writer = new FileWriter(file)) {
            HashMap<String, String> trabajosStringMap = new HashMap<>();
            for (HashMap.Entry<UUID, String> entry : TrabajoManager.getTrabajos().entrySet()) {
                trabajosStringMap.put(entry.getKey().toString(), entry.getValue());
            }
            gson.toJson(trabajosStringMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga el mapa de trabajos desde un archivo JSON.
     */
    private static void loadTrabajos(File file) {
        if (!file.exists()) return;

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            HashMap<String, String> trabajosStringMap = gson.fromJson(reader, type);
            HashMap<UUID, String> trabajos = new HashMap<>();
            for (HashMap.Entry<String, String> entry : trabajosStringMap.entrySet()) {
                trabajos.put(UUID.fromString(entry.getKey()), entry.getValue());
            }
            TrabajoManager.setTrabajos(trabajos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Guarda los límites diarios en un archivo JSON.
     */
    private static void saveLimites(File file) {
        try (Writer writer = new FileWriter(file)) {
            HashMap<String, HashMap<String, Integer>> limites = new HashMap<>();
            limites.put("Minero", convertirUUIDaString(LimitePagoDiario.getPagosDiarios()));
            limites.put("Cazador", convertirUUIDaString(LimitePagoDiarioCazador.getPagosDiarios()));
            limites.put("Lenador", convertirUUIDaString(LimitePagoDiarioLenador.getPagosDiarios()));
            limites.put("Agricultor", convertirUUIDaString(AgricultorEntity.getPagosDiarios()));
            limites.put("Pescador", convertirUUIDaString(PescadorEntity.getPagosDiarios()));
            limites.put("Cartografo", convertirUUIDaString(CartografoEntity.getPagosDiarios()));
            limites.put("Carnicero", convertirUUIDaString(CarniceroEntity.getPagosDiarios()));

            gson.toJson(limites, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga los límites diarios desde un archivo JSON y reinicia si es necesario.
     */
    private static void loadLimites(File file) {
        if (!file.exists()) return;

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {}.getType();
            HashMap<String, HashMap<String, Integer>> limites = gson.fromJson(reader, type);

            if (limites.containsKey("Minero")) {
                LimitePagoDiario.setPagosDiarios(convertirStringaUUID(limites.get("Minero")));
            }
            if (limites.containsKey("Cazador")) {
                LimitePagoDiarioCazador.setPagosDiarios(convertirStringaUUID(limites.get("Cazador")));
            }
            if (limites.containsKey("Lenador")) {
                LimitePagoDiarioLenador.setPagosDiarios(convertirStringaUUID(limites.get("Lenador")));
            }
            if (limites.containsKey("Agricultor")) {
                AgricultorEntity.setPagosDiarios(convertirStringaUUID(limites.get("Agricultor")));
            }
            if (limites.containsKey("Pescador")) {
                PescadorEntity.setPagosDiarios(convertirStringaUUID(limites.get("Pescador")));
            }
            if (limites.containsKey("Cartografo")) {
                CartografoEntity.setPagosDiarios(convertirStringaUUID(limites.get("Cartografo")));
            }
            if (limites.containsKey("Carnicero")) {
                CarniceroEntity.setPagosDiarios(convertirStringaUUID(limites.get("Carnicero")));
            }

            verificarResetDiario();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reinicia los límites de pago si ha cambiado el día.
     */
    private static void verificarResetDiario() {
        LocalDate fechaActual = obtenerFechaCDMX();
        if (!fechaActual.equals(ultimaFechaGuardada)) {
            LimitePagoDiario.resetearLimitesDiarios();
            LimitePagoDiarioAgricultor.resetearLimitesDiarios();
            LimitePagoDiarioCarnicero.resetearLimitesDiarios();
            LimitePagoDiarioCartografo.resetearLimitesDiarios();
            LimitePagoDiarioCazador.resetearLimitesDiarios();
            LimitePagoDiarioLenador.resetearLimitesDiarios();
            LimitePagoDiarioPescador.resetearLimitesDiarios();
            ultimaFechaGuardada = fechaActual;
            saveData();
        }
    }

    /**
     * Obtiene la fecha actual en la zona horaria de CDMX.
     */
    private static LocalDate obtenerFechaCDMX() {
        return LocalDate.now(ZoneId.of("America/Mexico_City"));
    }

    /**
     * Convierte un HashMap con claves UUID a un HashMap con claves String.
     */
    private static HashMap<String, Integer> convertirUUIDaString(HashMap<UUID, Integer> original) {
        HashMap<String, Integer> convertido = new HashMap<>();
        for (HashMap.Entry<UUID, Integer> entry : original.entrySet()) {
            convertido.put(entry.getKey().toString(), entry.getValue());
        }
        return convertido;
    }

    /**
     * Convierte un HashMap con claves String a un HashMap con claves UUID.
     */
    private static HashMap<UUID, Integer> convertirStringaUUID(HashMap<String, Integer> original) {
        HashMap<UUID, Integer> convertido = new HashMap<>();
        for (HashMap.Entry<String, Integer> entry : original.entrySet()) {
            try {
                convertido.put(UUID.fromString(entry.getKey()), entry.getValue());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return convertido;
    }
}

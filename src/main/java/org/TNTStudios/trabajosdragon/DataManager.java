package org.TNTStudios.trabajosdragon;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.TNTStudios.trabajosdragon.trabajos.LimitePagoDiario;
import org.TNTStudios.trabajosdragon.trabajos.LimitePagoDiarioCazador;
import org.TNTStudios.trabajosdragon.trabajos.LimitePagoDiarioLenador;
import org.TNTStudios.trabajosdragon.trabajos.TrabajoManager;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

public class DataManager {
    private static final Gson gson = new Gson();
    private static final String DATA_DIR = "config/trabajosdragon";
    private static final String TRABAJOS_FILE = "trabajos.json";
    private static final String LIMITES_FILE = "limites.json";

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
        ServerLifecycleEvents.SERVER_STARTED.register(server -> loadData());

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
     *
     * @param file Archivo donde se guardarán los trabajos.
     */
    private static void saveTrabajos(File file) {
        try (Writer writer = new FileWriter(file)) {
            // Convertir HashMap<UUID, String> a HashMap<String, String>
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
     *
     * @param file Archivo de donde se cargarán los trabajos.
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
     *
     * @param file Archivo donde se guardarán los límites.
     */
    private static void saveLimites(File file) {
        try (Writer writer = new FileWriter(file)) {
            // Combina todos los límites en un solo objeto para simplificar
            HashMap<String, HashMap<String, Integer>> limites = new HashMap<>();

            // Convertir cada HashMap<UUID, Integer> a HashMap<String, Integer>
            limites.put("Minero", convertirUUIDaString(LimitePagoDiario.getPagosDiarios()));
            limites.put("Cazador", convertirUUIDaString(LimitePagoDiarioCazador.getPagosDiarios()));
            limites.put("Lenador", convertirUUIDaString(LimitePagoDiarioLenador.getPagosDiarios()));

            gson.toJson(limites, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga los límites diarios desde un archivo JSON.
     *
     * @param file Archivo de donde se cargarán los límites.
     */
    private static void loadLimites(File file) {
        if (!file.exists()) return;

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {}.getType();
            HashMap<String, HashMap<String, Integer>> limites = gson.fromJson(reader, type);

            if (limites.containsKey("Minero")) {
                HashMap<UUID, Integer> limitesMinero = convertirStringaUUID(limites.get("Minero"));
                LimitePagoDiario.setPagosDiarios(limitesMinero);
            }

            if (limites.containsKey("Cazador")) {
                HashMap<UUID, Integer> limitesCazador = convertirStringaUUID(limites.get("Cazador"));
                LimitePagoDiarioCazador.setPagosDiarios(limitesCazador);
            }

            if (limites.containsKey("Lenador")) {
                HashMap<UUID, Integer> limitesLenador = convertirStringaUUID(limites.get("Lenador"));
                LimitePagoDiarioLenador.setPagosDiarios(limitesLenador);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convierte un HashMap con claves UUID a un HashMap con claves String.
     *
     * @param original Mapa original con claves UUID.
     * @return Nuevo mapa con claves convertidas a String.
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
     *
     * @param original Mapa original con claves String.
     * @return Nuevo mapa con claves convertidas a UUID.
     */
    private static HashMap<UUID, Integer> convertirStringaUUID(HashMap<String, Integer> original) {
        HashMap<UUID, Integer> convertido = new HashMap<>();
        for (HashMap.Entry<String, Integer> entry : original.entrySet()) {
            try {
                convertido.put(UUID.fromString(entry.getKey()), entry.getValue());
            } catch (IllegalArgumentException e) {
                // Clave inválida, puedes manejar el error aquí si lo deseas
                e.printStackTrace();
            }
        }
        return convertido;
    }
}

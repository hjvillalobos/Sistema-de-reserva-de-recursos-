package una.eif206.reservas.cliente;

import javafx.concurrent.Task;
import javafx.scene.control.Label;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/** Ejecuta red fuera del hilo de JavaFX y devuelve el resultado a la interfaz. */
public final class TareasUI {
    private static final java.util.Set<Label> OCUPADAS = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
    private TareasUI() { }
    public static boolean ocupada(Label etiqueta) { return OCUPADAS.contains(etiqueta); }
    public static <T> void ejecutar(Label error, Callable<T> trabajo, Consumer<T> exito) {
        ejecutar(error, trabajo, exito, () -> { });
    }
    public static <T> void ejecutar(Label error, Callable<T> trabajo, Consumer<T> exito, Runnable terminado) {
        if (!OCUPADAS.add(error)) return;
        error.setText("Procesando...");
        Task<T> tarea = new Task<>() { @Override protected T call() throws Exception { return trabajo.call(); } };
        tarea.setOnSucceeded(e -> {
            OCUPADAS.remove(error);
            error.setText("");
            try {
                if (error.getScene() != null && error.getScene().getWindow() != null
                        && !error.getScene().getWindow().isShowing()) return;
                exito.accept(tarea.getValue());
            } finally { terminado.run(); }
        });
        tarea.setOnFailed(e -> {
            OCUPADAS.remove(error);
            error.setText(tarea.getException().getMessage() == null ? "No se pudo completar la operación." : tarea.getException().getMessage());
            terminado.run();
        });
        Thread.ofVirtual().name("operacion-interfaz").start(tarea);
    }
}

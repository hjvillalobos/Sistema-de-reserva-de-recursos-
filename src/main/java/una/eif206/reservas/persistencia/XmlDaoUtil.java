// persistencia/XmlDaoUtil.java
package una.eif206.reservas.persistencia;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class XmlDaoUtil {

    private XmlDaoUtil() {}

    public static <T> T cargar(String ruta, Class<T> clase) {
        try {
            File archivo = new File(ruta);
            if (!archivo.exists()) {
                return clase.getDeclaredConstructor().newInstance();
            }
            Unmarshaller unmarshaller = JAXBContext.newInstance(clase).createUnmarshaller();
            return clase.cast(unmarshaller.unmarshal(archivo));
        } catch (Exception e) {
            throw new PersistenciaException("Error al cargar " + ruta, e);
        }
    }

    public static <T> void guardar(String ruta, T objeto, Class<T> clase) {
        try {
            Path path = Path.of(ruta);
            if (path.getParent() != null) Files.createDirectories(path.getParent());
            Marshaller marshaller = JAXBContext.newInstance(clase).createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(objeto, path.toFile());
        } catch (Exception e) {
            throw new PersistenciaException("Error al guardar " + ruta, e);
        }
    }
}
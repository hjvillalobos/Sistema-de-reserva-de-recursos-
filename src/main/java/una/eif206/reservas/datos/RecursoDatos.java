package una.eif206.reservas.datos;

import una.eif206.reservas.DTO.RecursoDTO;

import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class RecursoDatos {

    private String filePath;

    public RecursoDatos(){this.filePath="data/recursos.xml";}
    public RecursoDatos(String filePath){this.filePath=filePath;}

    public List<RecursoDTO> obtenerTodos(){
        List<RecursoDTO> resultado=new ArrayList<>();
        for(RecursoXML interno: deserializar()){
            resultado.add(convertirDTO(interno));
        }
        return resultado;
    }

    public RecursoDTO buscarPorId(String id){
        List<RecursoDTO> recursos=obtenerTodos();
        for(RecursoDTO recurso: recursos){
            if(recurso.getId().equalsIgnoreCase(id)){
                return recurso;
            }
        }
        return null;
    }

    public List<RecursoDTO> buscarPorCategoria(String categoriaId){
        List<RecursoDTO> resultado=new ArrayList<>();
        List<RecursoDTO> recursos=obtenerTodos();
        for(RecursoDTO recurso: recursos){
            if(recurso.getCategoriaId().equalsIgnoreCase(categoriaId)){
                resultado.add(recurso);
            }
        }
        return resultado;
    }

    public void guardar(RecursoDTO reserva){
        List<RecursoXML> internos=deserializar();
        internos.removeIf(a->a.id.equalsIgnoreCase(reserva.getId()));
        internos.add(convertirXML(reserva));
        serializar(internos);
    }

    public void eliminar(String id){
        List<RecursoXML> internos=deserializar();
        internos.removeIf(a->a.id.equalsIgnoreCase(id));
        serializar(internos);
    }

    private RecursoDTO convertirDTO(RecursoXML interno){
        return new RecursoDTO(interno.id, interno.categoriaId,interno.descripcion);
    }

    private RecursoXML convertirXML(RecursoDTO recurso){
        RecursoXML interno =new RecursoXML();
        interno.id=recurso.getId();
        interno.categoriaId=recurso.getCategoriaId();
        interno.descripcion=recurso.getDescripcion();
        return interno;
    }

    private  List<RecursoXML> deserializar(){
        try{
            File archivo=new File(filePath);
            if(!archivo.exists()){
                return new ArrayList<>();
            }
            else{
                JAXBContext context=JAXBContext.newInstance(ListaRecursosXML.class);
                Unmarshaller unmarshaller=context.createUnmarshaller();
                ListaRecursosXML lista=(ListaRecursosXML) unmarshaller.unmarshal(archivo);
                if(lista.recursos!=null){
                    return lista.recursos;
                }
                else{
                    return new ArrayList<>();
                }
            }
        }catch (JAXBException error){
            throw new RuntimeException("Error al leer "+filePath, error);
        }
    }

    private  void serializar(List<RecursoXML> recursos){
        try{
            ListaRecursosXML lista=new ListaRecursosXML();
            lista.recursos=recursos;
            JAXBContext context=JAXBContext.newInstance(ListaRecursosXML.class);
            Marshaller marshaller=context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
            marshaller.marshal(lista,new File(filePath));
        }catch (JAXBException error){
            throw new RuntimeException("Error al leer "+filePath, error);
        }
    }

    @XmlRootElement(name="recurso")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class RecursoXML{
        @XmlAttribute
        String id;
        String categoriaId;
        String descripcion;
    }

    @XmlRootElement(name="recursos")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class ListaRecursosXML{
        @XmlElement(name="recurso")
        List<RecursoXML> recursos=new ArrayList<>();
    }

}

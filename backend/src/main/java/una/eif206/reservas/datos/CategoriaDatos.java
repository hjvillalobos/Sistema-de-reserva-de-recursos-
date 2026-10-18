package una.eif206.reservas.datos;

import una.eif206.reservas.DTO.CategoriaDTO;

import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.*;

import una.eif206.reservas.DTO.CategoriaDTO;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class CategoriaDatos {
    private String filePath;

    public CategoriaDatos(){this.filePath="data/categorias.xml";}
    public CategoriaDatos(String filePath){this.filePath=filePath;}

    public List<CategoriaDTO> obtenerTodos(){
        List<CategoriaDTO> resultado=new ArrayList<>();
        for(CategoriaXML interno: deserializar()){
            resultado.add(convertirDTO(interno));
        }
        return resultado;
    }

    public List<CategoriaDTO> buscarPorDescripcion(String des){
        List<CategoriaDTO> resultado=new ArrayList<>();
        List<CategoriaDTO> categorias=obtenerTodos();
        String buscado=des.toLowerCase();
        for(CategoriaDTO categoria: categorias){
            if(categoria.getDescripcion().toLowerCase().contains(buscado)){
                resultado.add(categoria);
            }
        }
        return resultado;
    }

    public CategoriaDTO buscarPorId(String id){
        List<CategoriaDTO> categorias=obtenerTodos();
        for(CategoriaDTO categoria: categorias){
            if(categoria.getId().equalsIgnoreCase(id)){
                return categoria;
            }
        }
        return null;
    }

    public void guardar(CategoriaDTO categoria){
        List<CategoriaXML> internos=deserializar();
        internos.removeIf(a->a.id.equalsIgnoreCase(categoria.getId()));
        internos.add(convertirXML(categoria));
        serializar(internos);
    }

    public void eliminar(String id){
        List<CategoriaXML> internos=deserializar();
        internos.removeIf(a->a.id.equalsIgnoreCase(id));
        serializar(internos);
    }

    private CategoriaDTO convertirDTO(CategoriaXML interno){
        return new CategoriaDTO(interno.id, interno.descripcion);
    }

    private CategoriaXML convertirXML(CategoriaDTO categoria){
        CategoriaXML interno =new CategoriaXML();
        interno.id=categoria.getId();
        interno.descripcion=categoria.getDescripcion();
        return interno;
    }

    private  List<CategoriaXML> deserializar(){
        try{
            File archivo=new File(filePath);
            if(!archivo.exists()){
                return new ArrayList<>();
            }
            else{
                JAXBContext context=JAXBContext.newInstance(ListaCategoriasXML.class);
                Unmarshaller unmarshaller=context.createUnmarshaller();
                ListaCategoriasXML lista=(ListaCategoriasXML) unmarshaller.unmarshal(archivo);
                if(lista.categorias!=null){
                    return lista.categorias;
                }
                else{
                    return new ArrayList<>();
                }
            }
        }catch (JAXBException error){
            throw new RuntimeException("Error al leer "+filePath, error);
        }
    }

    private  void serializar(List<CategoriaXML> categorias){
        try{
            ListaCategoriasXML lista=new ListaCategoriasXML();
            lista.categorias=categorias;
            JAXBContext context=JAXBContext.newInstance(ListaCategoriasXML.class);
            Marshaller marshaller=context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
            marshaller.marshal(lista,new File(filePath));
        }catch (JAXBException error){
            throw new RuntimeException("Error al leer "+filePath, error);
        }
    }

    @XmlRootElement(name="categoria")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class CategoriaXML{
        @XmlAttribute
        String id;
        String descripcion;
    }

    @XmlRootElement(name="categorias")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class ListaCategoriasXML{
        @XmlElement(name="categoria")
        List<CategoriaXML> categorias=new ArrayList<>();
    }

}

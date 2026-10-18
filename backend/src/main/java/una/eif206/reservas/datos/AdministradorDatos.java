package una.eif206.reservas.datos;

import una.eif206.reservas.DTO.UsuarioDTO;
import una.eif206.reservas.DTO.RolDTO;

import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.ArrayList;


public class AdministradorDatos {
    private String filePath;

    public AdministradorDatos(){this.filePath="data/administradores.xml";}
    public AdministradorDatos(String filePath){this.filePath=filePath;}

    public List<UsuarioDTO> obtenerTodos(){
        List<UsuarioDTO> resultado=new ArrayList<>();
        for(AdministradorXML interno: deserializar()){
            resultado.add(convertirDTO(interno));
        }
        return resultado;
    }

    public UsuarioDTO buscarPorId(String id){
        List<UsuarioDTO> usuarios=obtenerTodos();
        for(UsuarioDTO usuario: usuarios){
            if(usuario.getId().equalsIgnoreCase(id)){
                return usuario;
            }
        }
        return null;
    }

    public void guardar(UsuarioDTO administrador){
        List<AdministradorXML> internos=deserializar();
        internos.removeIf(a->a.id.equalsIgnoreCase(administrador.getId()));
        internos.add(convertirXML(administrador));
        serializar(internos);
    }

    public void eliminar(String id){
        List<AdministradorXML> internos=deserializar();
        internos.removeIf(a->a.id.equalsIgnoreCase(id));
        serializar(internos);
    }

    private UsuarioDTO convertirDTO(AdministradorXML interno){
        return new UsuarioDTO(interno.id, interno.clave, RolDTO.ADMINISTRADOR);
    }

    private AdministradorXML convertirXML(UsuarioDTO usuario){
        AdministradorXML interno =new AdministradorXML();
        interno.id=usuario.getId();
        interno.clave=usuario.getClave();
        return interno;
    }

    private  List<AdministradorXML> deserializar(){
        try{
            File archivo=new File(filePath);
            if(!archivo.exists()){
                return new ArrayList<>();
            }
            else{
                JAXBContext context=JAXBContext.newInstance(ListaAdministradoresXML.class);
                Unmarshaller unmarshaller=context.createUnmarshaller();
                ListaAdministradoresXML lista=(ListaAdministradoresXML) unmarshaller.unmarshal(archivo);
                if(lista.administradores!=null){
                    return lista.administradores;
                }
                else{
                    return new ArrayList<>();
                }
            }
        }catch (JAXBException error){
            throw new RuntimeException("Error al leer "+filePath, error);
        }
    }

    private  void serializar(List<AdministradorXML> administradores){
        try{
            ListaAdministradoresXML lista=new ListaAdministradoresXML();
            lista.administradores=administradores;
            JAXBContext context=JAXBContext.newInstance(ListaAdministradoresXML.class);
            Marshaller marshaller=context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
            marshaller.marshal(lista,new File(filePath));
        }catch (JAXBException error){
            throw new RuntimeException("Error al leer "+filePath, error);
        }
    }

    @XmlRootElement(name="administrador")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class AdministradorXML{
        @XmlAttribute
        String id;
        String clave;
    }

    @XmlRootElement(name="administradores")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class ListaAdministradoresXML{
        @XmlElement(name="administrador")
        List<AdministradorXML> administradores=new ArrayList<>();
    }



}

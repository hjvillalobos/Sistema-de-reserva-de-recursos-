package una.eif206.reservas.datos;

import una.eif206.reservas.DTO.FuncionarioDTO;

import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class FuncionarioDatos {

    private String filePath;

    public FuncionarioDatos(){this.filePath="data/funcionarios.xml";}
    public FuncionarioDatos(String filePath){this.filePath=filePath;}

    public List<FuncionarioDTO> obtenerTodos(){
        List<FuncionarioDTO> resultado=new ArrayList<>();
        for(FuncionarioXML interno: deserializar()){
            resultado.add(convertirDTO(interno));
        }
        return resultado;
    }

    public FuncionarioDTO buscarPorId(String id){
        List<FuncionarioDTO> funcionarios=obtenerTodos();
        for(FuncionarioDTO funcionario: funcionarios){
            if(funcionario.getId().equalsIgnoreCase(id)){
                return funcionario;
            }
        }
        return null;
    }

    public List<FuncionarioDTO> buscarPorNombre(String nombre){
        List<FuncionarioDTO> resultado=new ArrayList<>();
        List<FuncionarioDTO> funcionarios=obtenerTodos();
        String buscado=nombre.toLowerCase();
        for(FuncionarioDTO funcionario: funcionarios){
            if(funcionario.getNombre().toLowerCase().contains(buscado)){
                resultado.add(funcionario);
            }
        }
        return resultado;
    }

    public void guardar(FuncionarioDTO funcionario){
        List<FuncionarioXML> internos=deserializar();
        internos.removeIf(a->a.id.equalsIgnoreCase(funcionario.getId()));
        internos.add(convertirXML(funcionario));
        serializar(internos);
    }

    public void eliminar(String id){
        List<FuncionarioXML> internos=deserializar();
        internos.removeIf(a->a.id.equalsIgnoreCase(id));
        serializar(internos);
    }

    private FuncionarioDTO convertirDTO(FuncionarioXML interno){
        return new FuncionarioDTO(interno.id, interno.clave, interno.nombre,interno.telefono);
    }

    private FuncionarioXML convertirXML(FuncionarioDTO funcionario){
        FuncionarioXML interno =new FuncionarioXML();
        interno.id=funcionario.getId();
        interno.clave=funcionario.getClave();
        interno.nombre=funcionario.getNombre();
        interno.telefono=funcionario.getTelefono();
        return interno;
    }

    private  List<FuncionarioXML> deserializar(){
        try{
            File archivo=new File(filePath);
            if(!archivo.exists()){
                return new ArrayList<>();
            }
            else{
                JAXBContext context=JAXBContext.newInstance(ListaFuncionariosXML.class);
                Unmarshaller unmarshaller=context.createUnmarshaller();
                ListaFuncionariosXML lista=(ListaFuncionariosXML) unmarshaller.unmarshal(archivo);
                if(lista.funcionarios!=null){
                    return lista.funcionarios;
                }
                else{
                    return new ArrayList<>();
                }
            }
        }catch (JAXBException error){
            throw new RuntimeException("Error al leer "+filePath, error);
        }
    }

    private  void serializar(List<FuncionarioXML> funcionarios){
        try{
            ListaFuncionariosXML lista=new FuncionarioDatos.ListaFuncionariosXML();
            lista.funcionarios=funcionarios;
            JAXBContext context=JAXBContext.newInstance(FuncionarioDatos.ListaFuncionariosXML.class);
            Marshaller marshaller=context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
            marshaller.marshal(lista,new File(filePath));
        }catch (JAXBException error){
            throw new RuntimeException("Error al leer "+filePath, error);
        }
    }

    @XmlRootElement(name="funcionario")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class FuncionarioXML{
        @XmlAttribute
        String id;
        String clave;
        String nombre;
        String telefono;
    }

    @XmlRootElement(name="funcionarios")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class ListaFuncionariosXML{
        @XmlElement(name="funcionario")
        List<FuncionarioDatos.FuncionarioXML> funcionarios=new ArrayList<>();
    }
}

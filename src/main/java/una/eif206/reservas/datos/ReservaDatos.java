package una.eif206.reservas.datos;

import una.eif206.reservas.DTO.ReservaDTO;

import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class ReservaDatos {

    private String filePath;

    public ReservaDatos(){this.filePath="data/reservas.xml";}
    public ReservaDatos(String filePath){this.filePath=filePath;}

    public List<ReservaDTO> obtenerTodos(){
        List<ReservaDTO> resultado=new ArrayList<>();
        for(ReservaXML interno: deserializar()){
            resultado.add(convertirDTO(interno));
        }
        return resultado;
    }

    public ReservaDTO buscarPorId(String id){
        List<ReservaDTO> reservas=obtenerTodos();
        for(ReservaDTO reserva: reservas){
            if(reserva.getId().equalsIgnoreCase(id)){
                return reserva;
            }
        }
        return null;
    }

    public List<ReservaDTO> buscarPorFuncionario(String funcionarioId){
        List<ReservaDTO> resultado=new ArrayList<>();
        List<ReservaDTO> reservas=obtenerTodos();
        for(ReservaDTO reserva: reservas){
            if(reserva.getFuncionarioId().toLowerCase().contains(funcionarioId)){
                resultado.add(reserva);
            }
        }
        return resultado;
    }

    public void guardar(ReservaDTO reserva){
        List<ReservaXML> internos=deserializar();
        internos.removeIf(a->a.id.equalsIgnoreCase(reserva.getId()));
        internos.add(convertirXML(reserva));
        serializar(internos);
    }

    public String generarSiguienteId(){
        return "RES-"+String.format("%06d",obtenerTodos().size()+1);
    }

    private ReservaDTO convertirDTO(ReservaXML interno){
        return new ReservaDTO(interno.id, interno.actividad, interno.fecha,interno.horaInicio,
                interno.horaFin, interno.funcionarioId, interno.recursosAsignados,interno.estado);
    }

    private ReservaXML convertirXML(ReservaDTO reserva){
        ReservaXML interno =new ReservaXML();
        interno.id=reserva.getId();
        interno.actividad=reserva.getActividad();
        interno.fecha=reserva.getFecha();
        interno.horaInicio=reserva.getHoraInicio();
        interno.horaFin=reserva.getHoraFin();
        interno.funcionarioId=reserva.getFuncionarioId();
        interno.recursosAsignados=reserva.getRecursosAsignados();
        interno.estado=reserva.getEstado();
        return interno;
    }

    private  List<ReservaXML> deserializar(){
        try{
            File archivo=new File(filePath);
            if(!archivo.exists()){
                return new ArrayList<>();
            }
            else{
                JAXBContext context=JAXBContext.newInstance(ListaReservasXML.class);
                Unmarshaller unmarshaller=context.createUnmarshaller();
                ListaReservasXML lista=(ListaReservasXML) unmarshaller.unmarshal(archivo);
                if(lista.reservas!=null){
                    return lista.reservas;
                }
                else{
                    return new ArrayList<>();
                }
            }
        }catch (JAXBException error){
            throw new RuntimeException("Error al leer "+filePath, error);
        }
    }

    private  void serializar(List<ReservaXML> reservas){
        try{
            ListaReservasXML lista=new ListaReservasXML();
            lista.reservas=reservas;
            JAXBContext context=JAXBContext.newInstance(ListaReservasXML.class);
            Marshaller marshaller=context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
            marshaller.marshal(lista,new File(filePath));
        }catch (JAXBException error){
            throw new RuntimeException("Error al leer "+filePath, error);
        }
    }

    @XmlRootElement(name="reserva")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class ReservaXML{
        @XmlAttribute
        String id;
        String actividad;
        String fecha;
        String horaInicio;
        String horaFin;
        String funcionarioId;
        @XmlElementWrapper(name="recursosAsignados")
        @XmlElement(name="recursoId")
        List<String> recursosAsignados=new ArrayList<>();
        String estado;
    }

    @XmlRootElement(name="reservas")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class ListaReservasXML{
        @XmlElement(name="reserva")
        List<ReservaXML> reservas=new ArrayList<>();
    }
}

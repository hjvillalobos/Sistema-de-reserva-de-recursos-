package una.eif206.reservas.protocolo;

import java.io.Serializable;
import una.eif206.reservas.DTO.ReservaDTO;

/** Punto de integración con el futuro panel de alertas del compañero B. */
public record EventoReserva(ReservaDTO reserva) implements Serializable { }

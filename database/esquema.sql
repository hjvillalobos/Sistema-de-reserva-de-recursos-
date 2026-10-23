-- Para una base vacía seleccionada previamente con USE. No elimina datos.
-- En una base existente, IF NOT EXISTS no sustituye la revisión de su estructura.
CREATE TABLE IF NOT EXISTS usuarios (
 id VARCHAR(100) NOT NULL PRIMARY KEY,
 clave_hash VARCHAR(255) NOT NULL,
 rol ENUM('ADMINISTRADOR','FUNCIONARIO') NOT NULL
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS funcionarios (
 usuario_id VARCHAR(100) NOT NULL PRIMARY KEY,
 nombre VARCHAR(150) NOT NULL, telefono VARCHAR(30) NOT NULL,
 CONSTRAINT fk_funcionario_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
 ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS categorias (
 id VARCHAR(100) NOT NULL PRIMARY KEY, descripcion VARCHAR(255) NOT NULL
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS recursos (
 id VARCHAR(100) NOT NULL PRIMARY KEY, categoria_id VARCHAR(100) NOT NULL,
 descripcion VARCHAR(255) NOT NULL,
 CONSTRAINT fk_recurso_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
 ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS reservas (
 id VARCHAR(100) NOT NULL PRIMARY KEY, actividad VARCHAR(255) NOT NULL,
 fecha DATE NOT NULL, hora_inicio TIME NOT NULL, hora_fin TIME NOT NULL,
 funcionario_id VARCHAR(100) NOT NULL,
 estado ENUM('ACTIVA','CANCELADA') NOT NULL DEFAULT 'ACTIVA',
 CONSTRAINT fk_reserva_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionarios(usuario_id)
 ON DELETE RESTRICT ON UPDATE CASCADE,
 CONSTRAINT chk_reserva_horario CHECK (hora_inicio >= '00:00:00' AND hora_fin < '24:00:00' AND hora_inicio < hora_fin)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS reserva_recursos (
 reserva_id VARCHAR(100) NOT NULL, recurso_id VARCHAR(100) NOT NULL,
 PRIMARY KEY (reserva_id, recurso_id),
 CONSTRAINT fk_detalle_reserva FOREIGN KEY (reserva_id) REFERENCES reservas(id) ON DELETE RESTRICT ON UPDATE CASCADE,
 CONSTRAINT fk_detalle_recurso FOREIGN KEY (recurso_id) REFERENCES recursos(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

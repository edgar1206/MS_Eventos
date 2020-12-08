package mx.com.nmp.eventos.repository;

import mx.com.nmp.eventos.model.nr.LogIncidencia;

public interface ServiceIncidencia  {

    Iterable<LogIncidencia> getAllIncidencias();

    LogIncidencia saveIncidencias(LogIncidencia incidencia);

}

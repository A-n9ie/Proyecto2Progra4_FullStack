import {useContext, useEffect, useState} from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCheck, faEye, faTimes } from '@fortawesome/free-solid-svg-icons';
import '../Users/History.css';
import '../Principal/principal.css';

function History(){
    const [citas, setCitas] = useState([]);
    const [citasFiltradas, setCitasFiltradas] = useState([]);
    const [nombreMedico, setNombreMedico] = useState('');
    const [status, setStatus] = useState('');
    const [patient, setPatient] = useState('');

    useEffect(() => {
        handleCitas();
    }, []);

    const backend = "http://localhost:8080/medicos";

    async function citasMedicas() {
        const token = localStorage.getItem("token");
        if (token) {
            try {
                const res = await fetch(backend + "/history", {
                    headers: {
                        "Authorization": "Bearer " + token
                    }
                });
                if (!res.ok) throw new Error("Error en la petición");
                const data = await res.json();

                setNombreMedico(data.medico);
                return data.citas;
            } catch (err) {
                console.error("Error al cargar citas... ", err);
                return [];
            }
        } else {
            return [];
        }
    }

    function handleCitas() {
        (async () => {
            const appointments = await citasMedicas();
            setCitas(appointments);
            setCitasFiltradas(appointments);
        })();
    }

    async function cancel(citaId) {
        const token = localStorage.getItem("token");

        if (!token) {
            console.error("No hay token disponible.");
            return;
        }

        try {
            const res = await fetch(backend + "/cancel", {
                method: "PUT",
                headers: {
                    "Authorization": "Bearer " + token,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ citaId })
            });

            if (!res.ok) {
                throw new Error("Error al cancelar la cita");
            }

            handleCitas();

        } catch (error) {
            console.error("Error al cancelar cita:", error);
        }
    }

    async function attend(citaId, notas, onSuccess){
        const token = localStorage.getItem("token");

        if (!token) {
            console.error("No hay token disponible.");
            return;
        }

        try {
            const res = await fetch(backend + "/saveNote", {
                method: "PUT",
                headers: {
                    "Authorization": "Bearer " + token,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ citaId, notas })
            });

            if (!res.ok) {
                throw new Error("Error al atender la cita");
            }

            handleCitas();
            onSuccess?.();
        } catch (error) {
            console.error("Error al atender cita:", error);
        }
    }

    function filtrarCitas(statusFiltro, pacienteFiltro) {
        if (!statusFiltro && !pacienteFiltro) {
            setCitasFiltradas(citas);
            return;
        }

        let filtradas = citas;

        if (statusFiltro) {
            filtradas = filtradas.filter(cita =>
                cita.estado.toLowerCase().includes(statusFiltro.toLowerCase())
            );
        }

        if (pacienteFiltro) {
            filtradas = filtradas.filter(cita =>
                cita.nombrePaciente.toLowerCase().includes(pacienteFiltro.toLowerCase())
            );
        }

        setCitasFiltradas(filtradas);
    }

    function handleSearch(e) {
        e.preventDefault();
        filtrarCitas(status, patient);
    }

    return (
        <>
            <Show
                citas={citasFiltradas}
                nombreMedico={nombreMedico}
                cancel={cancel}
                attend={attend}
                status={status}
                setStatus={setStatus}
                patient={patient}
                setPatient={setPatient}
                handleSearch={handleSearch}
            />
        </>
    );

}


function Show({ citas, cancel, attend, status, setStatus, patient, setPatient, handleSearch, nombreMedico }) {
    const [citaSeleccionada, setCitaSeleccionada] = useState(null);

    return (
        <div className="cuerpoH historial_col">
            <div className="datos">
                <h1>Médico - </h1>
                <h1>{nombreMedico}</h1>
                <h1> - Historial de citas</h1>
            </div>

            <div className="datos" id="fila_historial">
                <div className="buscador">
                <form className="buscar_especialidad_lugar" onSubmit={handleSearch}>
                        <span>Estado</span>
                        <input
                            type="text"
                            name="status"
                            placeholder="All"
                            value={status}
                            onChange={(e) => setStatus(e.target.value)}
                        />
                        <span>Paciente</span>
                        <input
                            type="text"
                            name="patient"
                            placeholder=""
                            value={patient}
                            onChange={(e) => setPatient(e.target.value)}
                        />
                        <button type="submit" name="buscar">Buscar</button>
                    </form>
                </div>
            </div>

            {citas.map((c) => (
                <div className="informacion_medico historial_col" key={c.id}>
                    <img src={`${c.fotoUrlPaciente}`} height="512" width="512" alt="Foto de perfil"/>
                    <div className="informacion_personal">
                        <div className="separacion">
                            <h5 className="nombre_medico">
                                <span>{c.nombrePaciente}</span>
                            </h5>
                        </div>
                    </div>

                    <div className="cada_cita">
                    <div className="dias">
                            <p>{c.fechaCita} - {c.horaCita}</p>
                        </div>

                        <button name="estado_cita" id={c.estado}>{c.estado}</button>
                        <div className="estado_y_icono">
                            {c.estado === 'Pendiente' && (!citaSeleccionada || citaSeleccionada.id !== c.id) && (
                                <>
                                    <a onClick={() => setCitaSeleccionada(c)} style={{color: 'green'}}>
                                        <FontAwesomeIcon icon={faCheck} className="close"/> Atender
                                    </a>

                                    <a onClick={() => cancel(c.id)} style={{color: 'red'}}>
                                        <FontAwesomeIcon icon={faTimes} className="close"/> Cancelar
                                    </a>
                                </>
                            )}

                            {c.estado === "Atendida" && (!citaSeleccionada || citaSeleccionada.id !== c.id) && (
                                <a
                                    href="#"
                                    onClick={(e) => {
                                        e.preventDefault();
                                        setCitaSeleccionada(c);
                                    }}
                                >
                                    <FontAwesomeIcon icon={faEye} className="icono-ojo" />
                                </a>
                            )}
                        </div>

                            {citaSeleccionada?.id === c.id && (
                                <div className="modal">
                                    <div className="modal-content-H">
                                        <span className="close" onClick={() => setCitaSeleccionada(null)}>&times;</span>
                                        <h2>Detalles</h2>
                                        <p><strong>Fecha:</strong> {citaSeleccionada.fechaCita}</p>
                                        <p><strong>Hora:</strong> {citaSeleccionada.horaCita}</p>
                                        <p><strong>Paciente:</strong> {citaSeleccionada.nombrePaciente}</p>
                                        <p><strong>Médico:</strong> {citaSeleccionada.nombreMedico}</p>
                                        <p><strong>Estado:</strong> {citaSeleccionada.estado}</p>
                                        <p><strong>Anotaciones:</strong> </p>
                                        <p>{citaSeleccionada.anotaciones || ''}</p>

                                        {c.estado === 'Pendiente' && (
                                            <form onSubmit={(e) => {
                                                e.preventDefault();
                                                const nota = e.target.anotaciones.value;
                                                attend(citaSeleccionada.id, nota, () => setCitaSeleccionada(null));
                                            }}>
                                                <textarea id="anotaciones" name="anotaciones" rows="4" maxLength="200"
                                                          required/>
                                                <button type="submit">Guardar</button>
                                            </form>
                                        )}
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                    ))}
                </div>
            );
}

export default History;
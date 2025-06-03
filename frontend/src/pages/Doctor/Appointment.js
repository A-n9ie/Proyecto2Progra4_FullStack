import {useContext, useEffect, useState} from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCheck, faEye, faTimes } from '@fortawesome/free-solid-svg-icons';
import './History.css';
import '../Principal/principal.css';

function History(){
    const [citas, setCitas] = useState([]);
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

    async function filtrarCitas(statusFiltro, pacienteFiltro) {
        const token = localStorage.getItem("token");
        if (!token) return;

        const params = new URLSearchParams();
        if (statusFiltro) params.append("status", statusFiltro);
        if (pacienteFiltro) params.append("patient", pacienteFiltro);

        try {
            const res = await fetch(`${backend}/history/filter?${params.toString()}`, {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token,
                }
            });

            if (!res.ok) {
                throw new Error("Error al filtrar las citas");
            }

            const data = await res.json();
            setCitas(data);
        } catch (error) {
            console.error("Error en el filtrado:", error);
        }
    }

    async function handleSearch(e) {
        e.preventDefault();
        await filtrarCitas(status, patient);
    }

    return (
        <>
            <Show
                citas={citas}
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
        <div className="cuerpo historial_col">
            <div className="datos">
                <h1>Doctor - </h1>
                <h1>{nombreMedico}</h1>
                <h1> - appointment history</h1>
            </div>

            <div className="datos" id="fila_historial">
                <div className="buscador">
                <form className="buscar_especialidad_lugar" onSubmit={handleSearch}>
                        <span>Status</span>
                        <input
                            type="text"
                            name="status"
                            placeholder="All"
                            value={status}
                            onChange={(e) => setStatus(e.target.value)}
                        />
                        <span>Patient</span>
                        <input
                            type="text"
                            name="patient"
                            placeholder=""
                            value={patient}
                            onChange={(e) => setPatient(e.target.value)}
                        />
                        <button type="submit" name="buscar">Search</button>
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
                            {c.estado === 'Pendiente' && (
                                <>
                                    <a onClick={() => setCitaSeleccionada(c)} style={{color: 'green'}}>
                                        <FontAwesomeIcon icon={faCheck}/> Attend
                                    </a>

                                    <a onClick={() => cancel(c.id)}  style={{color: 'red'}}>
                                        <FontAwesomeIcon icon={faTimes}/> Cancel
                                    </a>
                                </>
                            )}

                            {c.estado === 'Atendida' && (
                                <a onClick={() => setCitaSeleccionada(c)} style={{color: 'blue'}}>
                                    <FontAwesomeIcon icon={faEye}/>
                                </a>
                            )}
                        </div>

                            {citaSeleccionada?.id === c.id && (
                                <div className="modal">
                                    <div className="modal-content">
                                        <span className="close" onClick={() => setCitaSeleccionada(null)}>&times;</span>
                                        <h2>Appointment Details</h2>
                                        <p><strong>Date:</strong> {citaSeleccionada.fechaCita}</p>
                                        <p><strong>Time:</strong> {citaSeleccionada.horaCita}</p>
                                        <p><strong>Patient:</strong> {citaSeleccionada.nombrePaciente}</p>
                                        <p><strong>Doctor:</strong> {citaSeleccionada.nombreMedico}</p>
                                        <p><strong>Status:</strong> {citaSeleccionada.estado}</p>
                                        <p><strong>Annotations:</strong> {citaSeleccionada.anotaciones || 'N/A'}</p>

                                        {c.estado === 'Pendiente' && (
                                            <form onSubmit={(e) => {
                                                e.preventDefault();
                                                const nota = e.target.anotaciones.value;
                                                attend(citaSeleccionada.id, nota, () => setCitaSeleccionada(null));
                                            }}>
                                                <label htmlFor="anotaciones">Note:</label>
                                                <textarea id="anotaciones" name="anotaciones" rows="4" maxLength="200"
                                                          required/>
                                                <button type="submit">Save</button>
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
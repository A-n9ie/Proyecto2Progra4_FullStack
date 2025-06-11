import {useContext, useEffect, useState} from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEye } from '@fortawesome/free-solid-svg-icons';
import './History.css';
import '../Principal/principal.css';

function History(){
    const [citas, setCitas] = useState([]);
    const [citasFiltradas, setCitasFiltradas] = useState([]);
    const [nombrePaciente, setNombrePaciente] = useState('');
    const [status, setStatus] = useState('');
    const [doctor, setDoctor] = useState('');

    useEffect(() => {
        handleCitas();
    }, []);

    const backend = "http://localhost:8080/pacientes";

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

                setNombrePaciente(data.paciente);
                return data.citas;
            } catch (err) {
                console.error("Error al cargar citas... ", err);
                return null;
            }
        } else {
            return null;
        }
    }

    function handleCitas() {
        (async () => {
            const appointments = await citasMedicas();
            setCitas(appointments);
            setCitasFiltradas(appointments);
        })();
    }

    function filtrarCitas(statusFiltro, doctorFiltro) {
        if (!statusFiltro && !doctorFiltro) {
            setCitasFiltradas(citas);
            return;
        }

        let filtradas = citas;

        if (statusFiltro) {
            filtradas = filtradas.filter(cita =>
                cita.estado.toLowerCase().includes(statusFiltro.toLowerCase())
            );
        }

        if (doctorFiltro) {
            filtradas = filtradas.filter(cita =>
                cita.nombreMedico.toLowerCase().includes(doctorFiltro.toLowerCase())
            );
        }

        setCitasFiltradas(filtradas);
    }

    function handleSearch(e) {
        e.preventDefault();
        filtrarCitas(status, doctor);
    }

    return (
        <>
            <Show
                citas={citasFiltradas}
                nombrePaciente={nombrePaciente}
                status={status}
                setStatus={setStatus}
                doctor={doctor}
                setDoctor={setDoctor}
                handleSearch={handleSearch}
            />
        </>
    );
}


function Show({ citas, status, setStatus, doctor, setDoctor, handleSearch, nombrePaciente }) {
    const [citaSeleccionada, setCitaSeleccionada] = useState(null);

    return (
        <>
            <div className="cuerpo historial_col">
                <div className="datos">
                    <h1>Paciente - </h1>
                    <h1>{nombrePaciente} </h1>
                    <h1> - Historial de citas</h1>
                </div>

                <div className="datos" id="fila_historial">
                    <div className="buscador">
                        <form className="buscar_especialidad_lugar" onSubmit={handleSearch}>
                            <span>Estado</span>
                            <input type="text" name="status" placeholder="All" value={status}
                                   onChange={(e) => setStatus(e.target.value)}/>
                            <span>Médico</span>
                            <input type="text" name="doctor" placeholder="" value={doctor}
                                   onChange={(e) => setDoctor(e.target.value)}/>
                            <button type="submit" name="buscar">
                                Buscar
                            </button>
                        </form>
                    </div>
                </div>

                {citas.map((c) => (
                    <div key={c.id} className="informacion_medico historial_col">
                        <img
                            src={`${c.fotoUrlMedico}`}
                            height="512"
                            width="512"
                            alt="Foto de perfil"
                        />
                        <div className="informacion_personal">
                            <div className="separacion">
                                <h5 className="nombre_medico">
                                    <span>{c.nombreMedico}</span>
                                    <span className="id_medico">{c.costoConsulta}</span>
                                </h5>
                                <small className="especialidad_medico">{c.especialidad}</small>
                            </div>
                            <p className="lugar_atencion">
                                <span>{c.lugarAtencion}</span>
                            </p>
                        </div>
                        <div className="cada_cita">
                            <div className="dias">
                                <p>{c.fechaCita} - {c.horaCita}</p>
                            </div>
                            <div className="estado_y_icono">
                                <button
                                    name="estado_cita"
                                    id={c.estado}
                                >
                                    {c.estado}
                                </button>
                                {c.estado === "Atendida" && (
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


                            {citaSeleccionada && citaSeleccionada.id === c.id && (
                                <div className="modal">
                                    <div className="modal-content">
                                        <a
                                            href="#"
                                            className="close"
                                            onClick={(e) => {
                                                e.preventDefault();
                                                setCitaSeleccionada(null);
                                            }}
                                        >
                                            &times;
                                        </a>
                                        <h2>Detalles</h2>
                                        <p>
                                            <strong>Fecha:</strong>{" "}
                                            <span>{citaSeleccionada.fechaCita}</span>
                                        </p>
                                        <p>
                                            <strong>Hora:</strong>{" "}
                                            <span>{citaSeleccionada.horaCita}</span>
                                        </p>
                                        <p>
                                            <strong>Paciente:</strong>{" "}
                                            <span>{citaSeleccionada.nombrePaciente}</span>
                                        </p>
                                        <p>
                                            <strong>Médico:</strong>{" "}
                                            <span>{citaSeleccionada.nombreMedico}</span>
                                        </p>
                                        <p>
                                            <strong>Estado:</strong>{" "}
                                            <span>{citaSeleccionada.estado}</span>
                                        </p>
                                        <p>
                                            <strong>Anotaciones:</strong>{" "}
                                            <span>{citaSeleccionada.anotaciones}</span>
                                        </p>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                ))}
            </div>
        </>
    );
}

export default History;
import {Link, useLocation, useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";
import "./schedule.css";

function HorarioMedico({medicoId}) {
    const location = useLocation();
    const navigate = useNavigate();
    const { medico, page: initialPage = 0, pageSize = 4} = location.state || {};
    const [page, setPage] = useState(0);
    const [horarios, setHorarios] = useState({});
    const [totalPages, setTotalPages] = useState(1);

    useEffect(() => {
        handleListHorarios();
    }, [medico, page]);

    if (!medico || !horarios) return <p>Error: datos incompletos</p>;

    const backend = "http://localhost:8080/medicos";

    async function listarHorarioMedico(medicoId, page, pageSize){
        const token = localStorage.getItem("token");
        if (!token) {
            navigate('/login');
            return;
        }
        try {
            const response = await fetch(`${backend}/horarios/${medicoId}?page=${page}&pageSize=${pageSize}`,
                {method: 'GET', headers:{ }});

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error("Error del backend: " + errorText);
            }
            return await response.json();
        } catch (error) {
            console.error('Error al obtener horarios', error);
        }
    }

    async function handleListHorarios(){
        if (!medico) return;

        const data = await listarHorarioMedico(medico.idMedico, page, pageSize);

        if (!data) return;

        setHorarios(data.horarios || {});
        setTotalPages(data.totalPages || 1);
    }


    return (
        <div className="info-completa_ex">
            <div className="informacion_personal_medico">
                <img src={`http://localhost:8080/imagenes/ver/${medico.fotoUrl}`} alt="Foto del médico"
                     alt="Foto del médico"
                />
                <div className="informacion_personal_ex">
                        <h5 className="nombre_medico_ex">
                            <span>{medico.nombre} </span>
                            <span className="id_medico_ex">{medico.costoConsulta}</span>
                        </h5>
                        <small className="especialidad_medico_ex">{medico.especialidad}</small>
                    <p className="lugar_atencion_ex">
                        <span>{medico.lugarAtencion}</span>
                    </p>
                </div>
            </div>

            <div className="cada_cita_ex">
                {Object.keys(horarios).length === 0 ? (
                    <p>No hay horarios disponibles.</p>
                ) : (
                    Object.entries(horarios).map(([fecha, horas]) => (
                        <div key={fecha} className="dias">
                            <p>{fecha}</p>
                            <div className="horarios_ex">
                                {horas.map((hora, idx) => (
                                        <Link
                                            key={idx}
                                            to="/agendar"
                                            state={{medico, fecha: fecha, hora: hora}}
                                            className="btn_schedule_ex"
                                        >
                                            {hora}
                                        </Link>
                                ))}
                            </div>
                            </div>
                            ))
                            )}
                        </div>
                    <button onClick={() => setPage((p) => Math.max(p - 1, 0))} disabled={page === 0}
                    className="botones_ex">
                        Anterior
                    </button>
                    <span style={{margin: "0 1rem"}}>
        Página {page + 1} de {totalPages}
      </span>
                    <button
                        onClick={() => setPage((p) => Math.min(p + 1, totalPages - 1))}
                        disabled={page >= totalPages - 1} className="botones_ex"
                    >
                        Siguiente
                    </button>
        </div>
    );
}

export default HorarioMedico;
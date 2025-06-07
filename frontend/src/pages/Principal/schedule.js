import { useLocation } from "react-router-dom";
import {useContext, useEffect, useState} from "react";
import {AppContext} from "../AppProvider";

function HorarioMedico({medicoId}) {
    const location = useLocation();
    const { medicoState, setMedicoState} = useContext(AppContext);
    const { medico, page: initialPage = 0, pageSize = 3} = location.state || {};
    const [page, setPage] = useState(0);
    const [horarios, setHorarios] = useState({});
    const [totalPages, setTotalPages] = useState(1);

    useEffect(() => {
        handleListHorarios();
    }, [medico, page]);

    if (!medico || !horarios) return <p>Error: datos incompletos</p>;

    const backend = "http://localhost:8080/medicos";

    async function listarHorarioMedico(medicoId, page, pageSize){
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
        <div>
            <h2>Horarios del Dr. {medico.nombre}</h2>

            {Object.keys(horarios).length === 0 ? (
                <p>No hay horarios disponibles.</p>
            ) : (
                Object.entries(horarios).map(([fecha, horas]) => (
                    <div key={fecha}>
                        <h4>{fecha}</h4>
                        <div style={{ display: "flex", flexWrap: "wrap", gap: "0.5rem" }}>
                            {horas.map((hora, idx) => (
                                <button key={idx} className="btn_schedule">
                                    {hora}
                                </button>
                            ))}
                        </div>
                    </div>
                ))
            )}

            <div className="pagination" style={{ marginTop: "1rem" }}>
                <button onClick={() => setPage((p) => Math.max(p - 1, 0))} disabled={page === 0}>
                    Anterior
                </button>
                <span style={{ margin: "0 1rem" }}>
        Página {page + 1} de {totalPages}
      </span>
                <button
                    onClick={() => setPage((p) => Math.min(p + 1, totalPages - 1))}
                    disabled={page >= totalPages - 1}
                >
                    Siguiente
                </button>
            </div>
        </div>
    );
}

export default HorarioMedico;
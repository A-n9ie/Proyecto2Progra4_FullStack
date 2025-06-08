import React, { useEffect, useState } from 'react';
import './management.css';

const backend = "http://localhost:8080";

function Management() {
    const [doctores, setDoctores] = useState([]);

    useEffect(() => {
        fetch(`${backend}/medicos/pendientes`, {
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("token")
            }
        })
            .then(res => res.json())
            .then(data => setDoctores(data))
            .catch(err => {
                console.error("Error cargando doctores:", err);
            });
    }, []);

    const aprobarDoctor = async (id) => {
        try {
            const response = await fetch(`${backend}/aprobar`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    "Authorization": "Bearer " + localStorage.getItem("token")
                },
                body: JSON.stringify({ id })
            });

            if (response.ok) {

                setDoctores(prev => prev.map(doc =>
                    doc.id === id ? { ...doc, estado: true } : doc
                ));

            } else {
                alert("Error al aprobar el doctor.");
            }
        } catch (error) {
            console.error("Error al aprobar:", error);
        }
    };

    return (
        <div className="cuerpoAdmin">
            {doctores.map((m) => (
                <div className="medicosAdmin" key={m.id}>
                    <div className="imagenAqui">
                        <img
                            src={`${backend}/fotosPerfil/${m.fotoPerfil}`}
                            alt="Foto de perfil"
                            className="SolicitanteFoto"
                        />
                    </div>
                    <h5 className="nombreSolicitante">
                        <span>{m.nombre}</span>
                        <span>{m.id}</span>
                    </h5>
                    <div className="BotonesAprobar">
                        {m.estado ? (
                            <span>Aprobado</span>
                        ) : (
                            <button className="aprobar" onClick={() => aprobarDoctor(m.id)}>
                                Aprobar
                            </button>
                        )}
                    </div>
                </div>
            ))}
        </div>
    );
}

export default Management;

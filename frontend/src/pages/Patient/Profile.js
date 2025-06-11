import {useContext, useEffect, useState} from 'react';
import {AppContext} from '../AppProvider';
import '../Users/Profile.css';

function Profile({user}) {
    const {pacientesState, setPacientesState} = useContext(AppContext);

    useEffect(() => {
        handlePatient();
    }, []);

    const backend = "http://localhost:8080/pacientes";

    async function patient(peticion) {
        const token = localStorage.getItem("token");
        if (token) {
            try {
                const res = await fetch(backend + peticion, {
                    headers: {
                        "Authorization": "Bearer " + token
                    }
                });
                if (!res.ok) throw new Error("Error en la petición");
                const data = await res.json();
                return data;
            } catch (err) {
                console.error("No se pudo cargar el perfil", err);
                return null;
            }
        } else {
            return null;
        }
    }

    function handleChange(event){
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let personaChanged;
        personaChanged = {...pacientesState.paciente};
        personaChanged[name] = value;
        setPacientesState({
            ...pacientesState,
            paciente: personaChanged
        });
    }

    function handlePatient() {
        (async () => {
            const paciente = await patient("/me");
            if (!paciente) return;
            setPacientesState(prev => ({ ...prev, paciente }));
        })();
    }

    async function handleSavePatient(event) {
        event.preventDefault();
        const patientUpdate = {
            cedula: pacientesState.paciente.cedula,
            nombre: pacientesState.paciente.nombre,
            telefono: pacientesState.paciente.telefono,
            direccion: pacientesState.paciente.direccion
        }

        const token = localStorage.getItem('token');
        try {
            const response = await fetch(backend + "/update", {
                method: 'PUT',
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify(patientUpdate)
            });

            if (!response.ok) {
                alert(`Error: ${response.status} ${response.statusText}`);
                return;
            }
            handlePatient();
        } catch (error) {
            console.error("Error actualizando perfil:", error);
            alert("Error actualizando perfil");
        }
    }

    return (
        <>
            <ShowPaciente
                user={user}
                entity={pacientesState.paciente}
                handleChange={handleChange}
                handleSave={handleSavePatient}
            />
        </>
    );

}
function ShowPaciente({ entity, handleChange, handleSave, user}) {
    return (
        <div className="cuerpo">
            <form onSubmit={handleSave}>
                <div className="datos">
                    <div className="col_datos">
                        <img
                            src={entity.fotoUrl}
                            height="512"
                            width="512"
                            alt="Foto de perfil"
                        />

                        <div className="datos">
                            <div className="col_datos">
                                <label htmlFor="username">Usuario:</label>
                                <input
                                    type="text"
                                    id="username"
                                    name="username"
                                    value={user.name}
                                    readOnly
                                />

                                <br/><br/>
                            </div>
                        </div>

                        <div className="datos">
                            <div className="col_datos">
                            <label htmlFor="cedula">ID:</label>
                                <input
                                    type="text"
                                    id="cedula"
                                    name="cedula"
                                    value={entity.cedula}
                                    readOnly
                                />
                                <br/><br/>
                            </div>
                            <div className="col_datos">
                                <label htmlFor="nombre">Nombre:</label>
                                <input
                                    type="text"
                                    id="nombre"
                                    name="nombre"
                                    value={entity.nombre}
                                    onChange={handleChange}
                                    required
                                />
                                <br/><br/>
                            </div>
                        </div>

                        <div className="datos">
                            <div className="col_datos">
                                <label htmlFor="telefono">Teléfono:</label>
                                <input
                                    type="text"
                                    id="telefono"
                                    name="telefono"
                                    value={entity.telefono}
                                    onChange={handleChange}
                                />
                                <br/><br/>
                            </div>
                            <div className="col_datos">
                                <label htmlFor="direccion">Dirección:</label>
                                <input
                                    type="text"
                                    id="direccion"
                                    name="direccion"
                                    value={entity.direccion}
                                    onChange={handleChange}
                                />
                                <br/><br/>
                            </div>
                        </div>
                        <button type="submit">Guardar cambios</button>
                    </div>
                </div>
            </form>
        </div>
    );
}


export default Profile;
import {useContext, useEffect, useState} from 'react';
import {AppContext} from '../AppProvider';
import './Profile.css';

function Profile() {
    const {medicosState, setMedicosState} = useContext(AppContext);

    useEffect(() => {
        handleDoctor();
    }, []);

    const backend = "http://localhost:8080/medicos";

    async function user(peticion) {
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

    function handleDoctor() {
        (async () => {
            const medico = await user("/me");
            if (!medico) return;

            if (medico.frecuenciaCitas) {
                const [cantidad, tipo] = medico.frecuenciaCitas.split(" ");
                medico.frecuenciaCantidad = parseInt(cantidad);
                medico.frecuenciaTipo = tipo;
            }

            setMedicosState(prev => ({
                ...prev,
                medico: {
                    ...medico,
                    dias: medico.dias || [],
                }
            }));

        })();
    }

    function handleChange(event){
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let personaChanged;
        personaChanged = {...medicosState.medico};
        personaChanged[name] = value;
        setMedicosState({
            ...medicosState,
            medico: personaChanged
        });
    }

    function handleDiaChange(day, checked) {
        let updatedDias;
        if (checked) {
            // Añadir el día con horarios por defecto si no existe
            if (!medicosState.medico.dias.some(d => d.dia === day)) {
                updatedDias = [
                    ...medicosState.medico.dias,
                    { dia: day, horarioInicio: '08:00', horarioFin: '17:00' } // horarios default, puedes cambiar
                ];
            } else {
                updatedDias = [...medicosState.medico.dias];
            }
        } else {
            updatedDias = medicosState.medico.dias.filter(d => d.dia !== day);
        }

        setMedicosState(prev => ({
            ...prev,
            medico: {
                ...prev.medico,
                dias: updatedDias
            }
        }));
    }
    function handleHorarioDiaChange(idx, field, value) {
        setMedicosState(prev => {
            const newDias = [...prev.medico.dias];
            newDias[idx] = {...newDias[idx], [field]: value};
            return {
                ...prev,
                medico: {
                    ...prev.medico,
                    dias: newDias
                }
            };
        });
    }

    function validar() {
            if (medicosState.medico.costoConsulta <= 0) {
                alert("Consultation fee must be a positive value.");
                return false;
            }

            if (!medicosState.medico.dias || medicosState.medico.dias.length === 0) {
                alert("Please select at least one available day.");
                return false;
            }

            if (medicosState.medico.horarioInicio >= medicosState.medico.horarioFin) {
                alert("End time must be later than start time.");
                return false;
            }

        return true;
    }

    async function handleSaveDoctor(event) {
        event.preventDefault();

        //if (!validar()) return;

        const medicoUpdate = {
            cedula: medicosState.medico.cedula,
            nombre: medicosState.medico.nombre,
            especialidad: medicosState.medico.especialidad,
            costoConsulta: medicosState.medico.costoConsulta,
            lugarAtencion: medicosState.medico.lugarAtencion,
            frecuenciaCitas: `${medicosState.medico.frecuenciaCantidad} ${medicosState.medico.frecuenciaTipo}`,
            fotoUrl: medicosState.medico.fotoUrl,
            presentacion: medicosState.medico.presentacion,
            horarioInicio: medicosState.medico.horarioInicio,
            horarioFin: medicosState.medico.horarioFin,
            dias: medicosState.medico.dias
        };

        const token = localStorage.getItem('token');
        if (!token) {
            alert("No token found. Please login.");
            return;
        }

        try {
            const response = await fetch(backend + "/update", {
                method: 'PUT',
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify(medicoUpdate)
            });

            if (!response.ok) {
                alert(`Error: ${response.status} ${response.statusText}`);
                return;
            }

            handleDoctor();
            window.scrollTo(0, 0);

        } catch (error) {
            console.error("Error actualizando perfil:", error);
            alert("Error actualizando perfil");
        }
    }

return (
        <>
            <Show
                entity={medicosState.medico}
                handleChange={handleChange}
                handleSave={handleSaveDoctor}
                handleDiaChange={handleDiaChange}
                handleHorarioDiaChange={handleHorarioDiaChange}
            />
        </>
    );

}

function Show({ entity, handleChange, handleSave, handleHorarioDiaChange}) {

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
                                <label htmlFor="username">Username:</label>
                                <input
                                    type="text"
                                    id="usernameMedico"
                                    name="username"
                                    value={entity.usuario}
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
                                <label htmlFor="nombre">Name:</label>
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
                                <label htmlFor="especialidad">Speciality:</label>
                                <input
                                    type="text"
                                    id="especialidad"
                                    name="especialidad"
                                    value={entity.especialidad}
                                    onChange={handleChange}
                                    required
                                />
                                <br/><br/>
                            </div>
                            <div className="col_datos">
                                <label htmlFor="costo_consulta">Consultation fee:</label>
                                <input
                                    type="number"
                                    id="costoConsulta"
                                    name="costoConsulta"
                                    value={entity.costoConsulta}
                                    onChange={handleChange}
                                    required
                                />
                                <br/><br/>
                            </div>
                        </div>

                        <div className="datos">
                            <div className="col_datos">
                                <label htmlFor="lugar_atencion">Location:</label>
                                <input
                                    type="text"
                                    id="lugar_atencion"
                                    name="lugar_atencion"
                                    value={entity.lugarAtencion}
                                    onChange={handleChange}
                                    required
                                />
                                <br/><br/>
                            </div>
                        </div>

                        <div className="col_datos">
                            <label>Service hours</label>
                            <div className="datos">
                                <div className="col_datos">
                                    <label htmlFor="horarioInicio">Start time:</label>
                                    <input
                                        type="time"
                                        id="horarioInicio"
                                        name="horarioInicio"
                                        value={entity.horarioInicio}
                                        onChange={handleChange}
                                        required
                                    />
                                    <br/><br/>
                                </div>
                                <div className="col_datos">
                                    <label htmlFor="horarioFin">End time:</label>
                                    <input
                                        type="time"
                                        id="horarioFin"
                                        name="horarioFin"
                                        value={entity.horarioFin}
                                        onChange={handleChange}
                                        required
                                    />
                                    <br/><br/>
                                </div>
                            </div>

                            <div className="col_datos">
                                <label htmlFor="frecuencia_citas">Frequency:</label>
                                <div className="input-container">
                                    <span className="prefix">Every</span>
                                    <input
                                        type="number"
                                        id="frecuenciaCantidad"
                                        name="frecuenciaCantidad"
                                        value={entity.frecuenciaCantidad ?? ''}
                                        onChange={handleChange}
                                        required
                                    />
                                    <br/><br/>
                                    <select
                                        id="frecuenciaTipo"
                                        name="frecuenciaTipo"
                                        value={entity.frecuenciaTipo || 'minutes'}
                                        onChange={handleChange}
                                        required
                                    >
                                        <option value="horas">hours</option>
                                        <option value="minutos">minutes</option>
                                    </select>

                                </div>
                            </div>

                            <div className="col_datos">
                                <label>Days:</label>
                                <div className="datos">
                                    {(entity.dias || []).map(({dia, horarioInicio, horarioFin}, idx) => (
                                        <div key={dia}>
                                            <label>{dia} Start:</label>
                                            <input
                                                type="time"
                                                value={horarioInicio}
                                                onChange={(e) => handleHorarioDiaChange(idx, 'horarioInicio', e.target.value)}
                                            />
                                            <label>{dia} End:</label>
                                            <input
                                                type="time"
                                                value={horarioFin}
                                                onChange={(e) => handleHorarioDiaChange(idx, 'horarioFin', e.target.value)}
                                            />
                                        </div>
                                    ))}
                                    <br/><br/>
                                </div>
                            </div>

                            <div className="col_datos">
                                <div id="perfil_in">
                                    <label htmlFor="presentacion">Presentation:</label>
                                    <textarea
                                        id="presentacion"
                                        name="presentacion"
                                        value={entity.presentacion}
                                        onChange={handleChange}
                                        required
                                    />
                                    <br/><br/>
                                </div>
                            </div>
                        </div>

                        <button type="submit">Save changes</button>
                    </div>
                </div>
            </form>
        </div>
    );
}

export default Profile;
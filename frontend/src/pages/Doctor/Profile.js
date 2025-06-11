import {useContext, useEffect, useState} from 'react';
import {AppContext} from '../AppProvider';
import '../Users/Profile.css';

function Profile() {
    const {medicosState, setMedicosState} = useContext(AppContext);
    const diasSemana = ["LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"];

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
            if (!medicosState.medico.dias.some(d => d.diaSemana === day)) {
                updatedDias = [
                    ...medicosState.medico.dias,
                    { diaSemana: day, horarioInicio: '00:00', horarioFin: '00:00' }
                ];
            } else {
                updatedDias = [...medicosState.medico.dias];
            }
        } else {
            updatedDias = medicosState.medico.dias.filter(d => d.diaSemana !== day);
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

            for (const dia of medicosState.medico.dias) {
                if (dia.horarioInicio >= dia.horarioFin) {
                    alert(`End time must be later than start time for ${dia.diaSemana}.`);
                    return false;
                }
            }

        return true;
    }

    async function handleSaveDoctor(event) {
        event.preventDefault();

        //crer el objeto con la info para enviar al servidor
        const medicoUpdate = {
                nombre: medicosState.medico.nombre,
                especialidad: medicosState.medico.especialidad,
                costoConsulta: medicosState.medico.costoConsulta,
                lugarAtencion: medicosState.medico.lugarAtencion,
                presentacion: medicosState.medico.presentacion,
                dias: medicosState.medico.dias
        };

        const token = localStorage.getItem('token');
        if (!token) {
            alert("No token found. Please login.");
            return;
        }

        try {
            if(!validar())
                return;

            const response = await fetch(backend + "/update", {
                method: 'PUT',
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify(medicoUpdate)
            });

            if (!response.ok) {
                const errorData = await response.json();
                alert("Error al actualizar el perfil: " + errorData.message);
                return;
            }

            handleDoctor();
            window.scrollTo(0, 0);

        } catch (error) {
            alert("Error al realizar la solicitud: " + error.message);
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
                diasSemana={diasSemana}
            />
        </>
    );

}

function Show({ entity, handleChange, handleSave, handleHorarioDiaChange, diasSemana, handleDiaChange}) {
    if (!entity) return <p>Cargando datos del médico...</p>;
        //dias seleccionados
    const isDiaSelected = (dia) => entity.dias?.some(d => d.diaSemana === dia);
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
                                    id="datos"
                                    name="username"
                                    value={entity.usuario || ''}
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
                                    id="datos"
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
                                    id="datos"
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
                                <label htmlFor="especialidad">Especialidad:</label>
                                <input
                                    type="text"
                                    id="datos"
                                    name="especialidad"
                                    value={entity.especialidad}
                                    onChange={handleChange}
                                    required
                                />
                                <br/><br/>
                            </div>
                            <div className="col_datos">
                                <label htmlFor="costo_consulta">Costo de consulta:</label>
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
                                <label htmlFor="lugar_atencion">Lugar de atención:</label>
                                <input
                                    type="text"
                                    id="lugar_atencion"
                                    name="lugarAtencion"
                                    value={entity.lugarAtencion}
                                    onChange={handleChange}
                                    required
                                />
                                <br/><br/>
                            </div>
                        </div>
                        {/*los que ya tiene*/}
                        <div className="datos">
                            <div className="col_datos">
                                <label>Horario</label>
                                <div className="datos">
                                    {(entity.dias || []).map(({
                                                                  diaSemana,
                                                                  horarioInicio,
                                                                  horarioFin,
                                                                  frecuenciaCitas
                                                              }, idx) => (
                                        <div key={diaSemana}>
                                            <div key={diaSemana}>
                                                <label>{diaSemana} <br/> Inicio: {horarioInicio} <br/> Fin: {horarioFin}
                                                </label>
                                            </div>
                                            <label>Frecuencia (minutos): {frecuenciaCitas}</label>
                                        </div>
                                    ))}
                                    <br/><br/>
                                </div>
                            </div>
                        </div>
                        <div className="datos">
                            <div className="col_datos">
                                <div id="perfil_in">
                                    <label htmlFor="presentacion">Presentación:</label>
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

                        <div className="datos">
                            <div className="col_datos">
                                <label>Días:</label>
                                <div className="datos">
                                    {diasSemana.map(dia => (
                                        <div key={dia}>
                                            <label>
                                                <input
                                                    type="checkbox"
                                                    checked={isDiaSelected(dia)}
                                                    onChange={(e) => handleDiaChange(dia, e.target.checked)}
                                                />
                                                {dia}
                                            </label>
                                            {isDiaSelected(dia) && (
                                                <div>
                                                    <label>
                                                        Inicio:</label>
                                                    <input id={"tiempo"}
                                                        type="time"
                                                        value={entity.dias.find(d => d.diaSemana === dia)?.horarioInicio || '00:00'}
                                                        onChange={(e) => {
                                                            const idx = entity.dias.findIndex(d => d.diaSemana === dia);
                                                            handleHorarioDiaChange(idx, 'horarioInicio', e.target.value);
                                                        }}
                                                    />

                                                    <label>
                                                        Fin:
                                                    </label>
                                                    <input id={"tiempo"}
                                                        type="time"
                                                        value={entity.dias.find(d => d.diaSemana === dia)?.horarioFin || '00:00'}
                                                        onChange={(e) => {
                                                            const idx = entity.dias.findIndex(d => d.diaSemana === dia);
                                                            handleHorarioDiaChange(idx, 'horarioFin', e.target.value);
                                                        }}
                                                    />
                                                    <label>
                                                        Frecuencia:
                                                        <input id={"frecuencia"}
                                                            type="number"
                                                            min="5"
                                                            step="5"
                                                            value={entity.dias.find(d => d.diaSemana === dia)?.frecuenciaCitas || ''}
                                                            onChange={(e) => {
                                                                const idx = entity.dias.findIndex(d => d.diaSemana === dia);
                                                                handleHorarioDiaChange(idx, 'frecuenciaCitas', parseInt(e.target.value));
                                                            }}
                                                        />
                                                    </label>
                                                </div>
                                            )}
                                        </div>
                                    ))}
                                </div>
                                <button type="submit">Guardar cambios</button>
                            </div>
                        </div>
                    </div>
                    </div>
            </form>
        </div>
)
    ;
}

export default Profile;
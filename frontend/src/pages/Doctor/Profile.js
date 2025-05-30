import {useContext, useEffect, useState} from 'react';
import {AppContext} from '../AppProvider';
import './Profile.css';

function Profile({ user }) {
    const {medicosState, setMedicosState, usuariosState, setUsuariosState} = useContext(AppContext);
    const [horariosState, setHorariosState] = useState({
        horarios: [],
        horario: []
    });
    const [diasSeleccionados, setDiasSeleccionados] = useState([]);

    useEffect(()=> {
        handleDoctor();
    }, []);

    useEffect(() => {
        if (horariosState.horario) {
            const dias = horariosState.horario.map(h => h.dia);
            setDiasSeleccionados(dias);
        }
    }, [horariosState.horario]);

    const backend = "http://localhost:8080";

    function handleChange(event){
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let doctorChanged = {...medicosState.medico};
        doctorChanged[name] = value;
        setMedicosState({
            ...medicosState,
            medico: doctorChanged
        });
    }

    function handleDoctor() {
        (async () => {
            const medico = await doctor();
            if (!medico) return;

            if (medico.frecuenciaCitas) {
                const [cantidad, tipo] = medico.frecuenciaCitas.split(" ");
                medico.frecuenciaCantidad = parseInt(cantidad);
                medico.frecuenciaTipo = tipo;
            }

            setMedicosState(prev => ({ ...prev, medico }));

            handlerDias(medico.cedula);
        })();
    }

    async function doctor(){
        const request = new Request(`${backend}/medicos/profile/${user.name}`, {
            method: 'GET',
            headers: {}
        });

        const response = await fetch(request);
        if(!response.ok){alert("Error: " + response.status);
            return;}
        return await response.json();
    }

    function handlerDias(cedula) {
        (async () => {
            const horario = await horarioDias(cedula);
            if (horario) {
                setHorariosState(prev => ({ ...prev, horario }));
            }
        })();
    }

    async function horarioDias(cedula) {
        const request = new Request(`${backend}/medicos/profile/days/${cedula}`, { method: 'GET' });
        const response = await fetch(request);
        if (!response.ok) {
            alert("Error: " + response.status);
            return;
        }
        return await response.json();
    }

    function validar() {
        /*if (!medicosState.medico.cedula || !medicosState.medico.nombre || !medicosState.medico.especialidad
            || !medicosState.medico.costo_consulta || !medicosState.medico.lugar_atencion
            || !medicosState.medico.presentacion || !medicosState.medico.frecuencia_citas) {
            alert("Todos los campos deben ser llenados.");
            return false;
        }*/
        if (medicosState.medico.costoConsulta <= 0) {
            alert("Consultation fee must be a positive value.");
            return false;
        }

        if (!diasSeleccionados || diasSeleccionados.length === 0) {
            alert("Please select at least one available day.");
            return false;
        }

        if (medicosState.medico.horarioInicio >= medicosState.medico.horarioFin) {
            alert("End time must be later than start time.");
            return false;
        }

        return true;
    }

    async function saveDias(cedula, dias) {
        const request = new Request(`${backend}/medicos/profile/saveDays/${cedula}`, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(dias)
        });

        const response = await fetch(request);
        if (!response.ok) {
            alert("Error al guardar días: " + response.status);
        }
    }

    function handleSave(event) {
        if (!validar()) return;
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
            horarioFin: medicosState.medico.horarioFin
        };

        let request = new Request(`${backend}/medicos/profile/update/${user.name}`, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(medicoUpdate)
        });
        console.log("Medico enviado:", JSON.stringify(medicoUpdate, null, 2));
        (async () => {
            const response = await fetch(request);
            if (!response.ok) {
                alert("Error: " + response.status);
                return;
            }
            await saveDias(medicosState.medico.cedula, diasSeleccionados);
            handleDoctor();
            window.scrollTo(0, 0);
        })();
    }

    return (
        <>
            <Show
                entity={medicosState.medico}
                handleChange={handleChange}
                handleSave={handleSave}
                diasSeleccionados={diasSeleccionados}
                setDiasSeleccionados={setDiasSeleccionados}
            />

        </>
    );
}

function Show({ entity, handleChange, handleSave, diasSeleccionados, setDiasSeleccionados }) {
    const { horariosState } = useContext(AppContext);

    return (
        <div className="cuerpo">
            <form onSubmit={(e) => { e.preventDefault(); handleSave(); }}>
                <div className="datos">
                    <div className="col_datos">
                        <img
                            src={"imagenper.png"}
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
                                <br /><br />
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
                                <br /><br />
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
                                <br /><br />
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
                                <br /><br />
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
                                <br /><br />
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
                                    {['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'].map((day) => (
                                        <label key={day}>
                                            <input
                                                type="checkbox"
                                                value={day}
                                                checked={diasSeleccionados.includes(day)}
                                                onChange={(e) => {
                                                    const checked = e.target.checked;
                                                    const updatedDias = checked
                                                        ? [...diasSeleccionados, day]
                                                        : diasSeleccionados.filter(d => d !== day);
                                                    setDiasSeleccionados(updatedDias);
                                                }}
                                            />
                                            <span>{day}</span>
                                        </label>
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
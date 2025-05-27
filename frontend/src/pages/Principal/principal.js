import './principal.css';
import {AppContext} from '../AppProvider';
import {useContext, useEffect, useState} from 'react';

function Medicos(){
    const { medicosState, setMedicosState, horariosState, setHorariosState } = useContext(AppContext);

    useEffect(()=> {
        handleList();
    }, []);

    const backend = "http://localhost:8080/medicos";

    function handleChange(event){
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let medicoChanged = {...medicosState.medico};
        medicoChanged[name] = value;
        setMedicosState({
            ...medicosState,
            medico: medicoChanged
        });
    }

    function handleList(){
        (async ()=>{
            const medicos = await list();
            const horarios = await listHorarios();
            setMedicosState({...medicosState, medicos: medicos});
            setHorariosState({...horariosState, horarios : horarios});
        })();
    }


     function enableSchedule(){
        // const request = new Request(backend, {method: 'GET', headers:{ }});
        // const response = await fetch(request);
        // setHorariosState({...horariosState})
        // if(!response.ok){
        //     alert("Error: " + response.status);
        //     return;
        // }
        // return await response.json();
         (async ()=>{
             const horarios = await listHorarios();
             setHorariosState({...horariosState, horarios: horarios});
         })();
    }

    async function list(){
        const request = new Request(backend, {method: 'GET', headers:{ }});
        const response = await fetch(request);
        if(!response.ok){alert("Error: " + response.status);
            return;}
        return await response.json();
    }
    async function listHorarios(){
        const request = new Request(backend+'/horarios', {method: 'GET', headers:{ }});
        const response = await fetch(request);
        if(!response.ok){alert("Error: " + response.status);
            return;}
        const data = await response.json(); // Esto es un objeto tipo: { "1": ["Lunes 10:00", "Martes 11:00"], ... }
        // Convertir a array de objetos
        const horariosList = Object.entries(data).map(([medicoId, fechas]) => ({
            medicoId: parseInt(medicoId),
            horarios: fechas // { fecha: [horas] }
        }));
        return horariosList;
    }

    return (
        <List
            list={medicosState.medicos}
            listHorarios={horariosState.horarios}
        />
    );
}

function List({list, listHorarios}) {
    return (
        <div id="cuerpo_div_index" className="cuerpoIndex">
            <div className="buscar_lugar_especialidad">
                <div className="buscador">
                    <form className="buscar_especialidad_lugar" action="/search" method="get">
                        <span>Speciality</span>
                        <input type="text" name="speciality" placeholder="" />
                        <span>City</span>
                        <input type="text" name="city" placeholder="" />
                        <button type="submit" name="buscar">
                            Search
                        </button>
                    </form>
                </div>
            </div>

            <div className="medicos">
                {list
                    .filter(m => m.aprobado)
                    .map((m) => (
                        <div className="cada_medico" key={m.cedula}>
                            <div className="info_citas">
                                <div className="informacion_medico">
                                    <img src={`/fotosPerfil/${m.fotoUrl}`} height="512" width="512"
                                         alt="Foto de perfil"/>
                                    <div className="informacion_personal">
                                        <div className="separacion">
                                            <h5 className="nombre_medico">
                                                <span>{m.nombre}</span>
                                                <span className="id_medico">{m.costoConsulta}</span>
                                            </h5>
                                            <small className="especialidad_medico">{m.especialidad}</small>
                                        </div>
                                        <p className="lugar_atencion">
                                            <span>{m.lugarAtencion}</span>
                                        </p>
                                        </div>
                                    <div className="cada_cita">
                                        {listHorarios.filter(h => h.medicoId === m.id).map ((horario) => (
                                            <div className="dias_disponibles" key={horario.medicoId}>
                                                {Object.entries(horario.horarios).map(([fecha, horas], index) => (
                                                    <div key={index} className={index >= 3 ? 'oculto' : ''}>
                                                        <p>{fecha}</p>
                                                        <div className="citas_disponibles">
                                                            <div className="citas_disponibles">
                                                                <form>
                                                                    {horas.map((hora, hIndex) => (
                                                                        <button
                                                                            key={hIndex}
                                                                            type="submit"
                                                                            name="hora"
                                                                            value={hora}
                                                                        >
                                                                            {hora}
                                                                        </button>
                                                                    ))}
                                                                    <input type="hidden" name="fecha" value={fecha}/>
                                                                    <input type="hidden" name="medicoId"
                                                                           value={horario.medicoId}/>
                                                                </form>
                                                            </div>
                                                        </div>
                                                        </div>
                                                        ))}
                                                    </div>
                                                ))}
                                            </div>
                                            </div>
                                            </div>
                                            </div>
                                            ))}
                                    </div>
                                </div>
                                );
                                }

export default Medicos;
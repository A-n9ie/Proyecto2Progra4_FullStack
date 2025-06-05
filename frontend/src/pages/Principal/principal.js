import './principal.css';
import {AppContext} from '../AppProvider';
import {useContext, useEffect, useState} from 'react';
import { Link } from 'react-router-dom';

function Medicos(){
    const { medicosState, setMedicosState, horariosState, setHorariosState } = useContext(AppContext);

    useEffect(()=> {
        handleList();
    }, []);

    const backend = "http://localhost:8080/medicos";

    async function handleList() {
        const data = await listHorarios();

        if (!data) {
            console.error("No se pudo cargar la información de los médicos y horarios.");
            return;
        }

        // const perfiles = data.map(item => item.perfilMedico);
        // const horarios = data.map(item => ({
        //     medicoId: item.perfilMedico.id,
        //     horarios: item.horariosMedicos || {}
        // }));

        setMedicosState({medicos: data});
        //setHorariosState({...horariosState, horarios});
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
            return;
        }
        const data = await response.json();

        return data;
    }

    return (
        <List
            list={medicosState.medicos}
        />
    );
}

function List({ list }) {
    return (
        <div id="cuerpo_div_index" className="cuerpoIndex">
            <div className="buscar_lugar_especialidad">
                <div className="buscador">
                    <form className="buscar_especialidad_lugar" action="/search" method="get">
                        <span>Speciality</span>
                        <input type="text" name="speciality" placeholder="" />
                        <span>City</span>
                        <input type="text" name="city" placeholder="" />
                        <button type="submit" name="buscar" className="btn_schedule">
                            Search
                        </button>
                    </form>
                </div>
            </div>

            <div className="medicos">
                {list.filter(item => item && item.nombre)
                    .map((m) => {
                        const horarios = m.horarios;

                    return (
                        <div key={m.id} className="cada_medico">
                            <div className="info_citas">
                                <div className="informacion_medico">
                                    <img src={`http://localhost:8080/fotosPerfil/${m.fotoUrl || 'default.jpg'}`}/>
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
                                        {horarios &&
                                            Object.entries(horarios).map(([fecha, horas], index) => (
                                                <div key={fecha} className={index >= 3 ? "oculto" : ""}>
                                                    <div className="dias">
                                                        <p>{fecha}</p>
                                                    </div>
                                                    <div className="citas_disponibles">
                                                        <form>
                                                            {Array.isArray(horas) &&
                                                                horas.map((hora, hIndex) => (
                                                                    <Link
                                                                        key={hIndex}
                                                                        to="/agendar"
                                                                        state={{medico: m, fecha: fecha, hora: hora}}
                                                                        className="btn_schedule"
                                                                    >
                                                                        {hora}
                                                                    </Link>
                                                                ))}
                                                        </form>
                                                    </div>
                                                </div>
                                            ))}
                                    </div>
                                </div>
                            </div>
                        </div>
                    );
                    })}
            </div>
        </div>
    );
}
export default Medicos;
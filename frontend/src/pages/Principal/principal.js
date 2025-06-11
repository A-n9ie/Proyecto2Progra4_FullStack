import './principal.css';
import {AppContext} from '../AppProvider';
import {useContext, useEffect, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';

function Medicos(){
    const { medicosState, setMedicosState} = useContext(AppContext);

    useEffect(()=> {
        handleList();
    }, []);

    const backend = "http://localhost:8080/medicos";
    const navigate = useNavigate();

    async function handleList(filtros = {}) {
        const data = Object.keys(filtros).length === 0
            ? await listHorarios()
            : await busqueda(filtros);

        if (!data) {
            console.error("No se pudo cargar la información de los médicos y horarios.");
            return;
        }
        setMedicosState({medicos: data});
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

    async function busqueda(filtros = {}){

        const query = new URLSearchParams(filtros).toString();
        const request = new Request(`http://localhost:8080/pacientes/buscar?${query}`, {
            method: 'GET', headers: {}
        });
        const response = await fetch(request);

        if(!response.ok){
            alert("Error: " + response.status);
            return;
        }
        return await response.json();

    }

    return (
        <List
            list={medicosState.medicos} handleList={handleList}
        />
    );
}

function List({ list, handleList}) {
    return (
        <div id="cuerpo_div_index" className="cuerpoIndex">
            <div className="buscar_lugar_especialidad">
                <div className="buscador">
                    <form className="buscar_especialidad_lugar" onSubmit={(e) =>{
                        e.preventDefault();
                        const formData = new FormData(e.target);
                        const speciality = formData.get('speciality');
                        const city = formData.get('city');
                        handleList({ speciality, city });
                    }}>
                        <span>Especialidad</span>
                        <input type="text" name="speciality" placeholder=""/>
                        <span>Ciudad</span>
                        <input type="text" name="city" placeholder=""/>
                        <button
                            type="submit"
                            className={"btn_schedule"}
                        >
                            BUSCAR
                        </button>
                    </form>
                </div>
            </div>

            <div className="medicos">
                {list.filter(item => item && item.nombre && item.estado === true &&
                    item.especialidad &&
                    item.lugarAtencion &&
                    item.costoConsulta &&
                    item.horarios &&
                    Object.keys(item.horarios).length > 0)
                    .map((m) => {
                        const horarios = m.horarios;
                    return (
                        <div key={m.idMedico} className="cada_medico">
                            <div className="info_citas">
                                <div className="informacion_medico">
                                        <img src={`http://localhost:8080/imagenes/ver/${m.fotoUrl}`} alt="Foto del médico"
                                        alt="Foto del médico"
                                        />
                                    <div className="informacion_personal">
                                        <div className="separacion">
                                            <h5 className="nombre_medico">
                                                <span>{m.nombre} </span>
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

                                            ))}
                                            <div className={"ver_mas"}>
                                                <Link
                                                    to="/horarios_medico"
                                                    state={{medico: m,
                                                        page: 0,
                                                        pageSize: 3
                                                        }}
                                                    className="btn_schedule"
                                                >
                                                    Ver mas horarios
                                                </Link>
                                            </div>
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
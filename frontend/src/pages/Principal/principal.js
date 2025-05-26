import './principal.css';
import {AppContext} from '../AppProvider';
import {useContext, useEffect, useState} from 'react';

function Medicos(){
    const { medicosState, setMedicosState, horariosState, setHorariosState } = useContext(AppContext);

    useEffect(()=> {
        handleList();
    }, []);

    const backend = "http://localhost:8080";

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
            const horarios = await schedulelist();
            setMedicosState({...medicosState, medicos: medicos});
            setHorariosState({...horariosState, horarios : horarios});
        })();
    }

    async function list(){
        const request = new Request(backend+'/medicos', {method: 'GET', headers:{ }});
        const response = await fetch(request);
        if(!response.ok){alert("Error: " + response.status);
            return;}
        return await response.json();
    }

    async function schedulelist(){
        const request = new Request(backend+'/horarios', {method: 'GET', headers:{ }});
        const response = await fetch(request);
        if(!response.ok){alert("Error: " + response.status);
            return;}
        return await response.json();
    }

    return (
        <List
            list={medicosState.medicos}
            schedulelist={horariosState.horarios}
        />
    );
}

function List({list, schedulelist}) {
    return (
        <div id="cuerpo_div_index" className="cuerpoIndex">
            <div className="buscar_lugar_especialidad">
                <div className="buscador">
                    <form className="buscar_especialidad_lugar" action="/search" method="get">
                        <span>Speciality</span>
                        <input type="text" name="speciality" placeholder=""/>
                        <span>City</span>
                        <input type="text" name="city" placeholder=""/>
                        <button type="submit" name="buscar">
                            Search
                        </button>
                    </form>
                </div>
            </div>

            <div className="medicos">
                {list
                    .filter(m => m.aprobado)
                    .map((m) => {
                        const MedicosConHorarios = schedulelist.find(h => h.medicoId === m.id);
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
                                        {MedicosConHorarios?.horarios.map((horario) => (
                                            // <div className="dias_disponibles"
                                            <div key={horario.id} className="dias_disponibles">
                                                <p>{horario.dia}</p>
                                                {/*<div th:id="'ver-mas-' + ${m.getId()}" className="dias">*/}
                                                {/*    <div th:each="dia, iter : ${medicoEntry.value}">*/}
                                                {/*        <div th:classappend="${iter.index >= 3} ? 'oculto' : ''">*/}
                                                {/*            <p th:text="${dia}"></p>*/}
                                                {/*            <div className="citas_disponibles">*/}
                                                {/*                <form th:action="@{/presentation/patient/book/save}"*/}
                                                {/*                      method="post">*/}
                                                {/*                    <div*/}
                                                {/*                        th:each="hora : ${m.citasDisponibles(dia)}">*/}
                                                {/*                        <button type="submit" th:name="hora"*/}
                                                {/*                                th:value="${hora}"*/}
                                                {/*                                th:text="${hora}"></button>*/}
                                                {/*                    </div>*/}
                                                {/*                    <input type="hidden" th:name="dia"*/}
                                                {/*                           th:value="${dia}"/>*/}
                                                {/*                    <input type="hidden" th:name="medicoId"*/}
                                                {/*                           th:value="${m.getId()}"/>*/}
                                                {/*                </form>*/}
                                                {/*            </div>*/}
                                                {/*        </div>*/}
                                                {/*horario.horas?.map(hora => (*/}
                                                {/*<button key={hora}>{hora}</button>*/}
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        </div>
                    })}
            </div>
        </div>
    );
}
                                export default Medicos;
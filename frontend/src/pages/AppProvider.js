import { createContext, useState } from 'react';

export const AppContext = createContext();

function AppProvider(props) {

    const [personasState, setPersonasState] = useState({
        personas: [],
        persona: {nombre: '', cedula: '', usuario: ''}
    });

    const [usuariosState, setUsuariosState] = useState({
        usuarios: [],
        usuario: { id: '', usuario: '', clave: '', rol: '' }
    });

    const [medicosState, setMedicosState] = useState({
        medicos: [],
        medico: {
            id: '',
            usuario_id: '',
            cedula: '',
            nombre: '',
            aprobado: false,
            especialidad: '',
            costo_consulta: 0,
            lugar_atencion: '',
            horario_inicio: '',
            horario_fin: '',
            frecuencia_citas: '',
            frecuencia_cantidad: 0,
            frecuencia_tipo: '',
            foto_url: '',
            presentacion: ''
        }
    });

    const [horariosState, setHorariosState] = useState({
        horarios: [],
        horario: { id: '', medico_id: '', dia: '' }
    });

    const [pacientesState, setPacientesState] = useState({
        pacientes: [],
        paciente: {
            id: '',
            usuario_id: '',
            cedula: '',
            nombre: '',
            telefono: '',
            direccion: '',
            foto_url: ''
        }
    });

    const [citasState, setCitasState] = useState({
        citas: [],
        cita: {
            id: '',
            medico_id: '',
            paciente_id: '',
            fecha_cita: '',
            hora_cita: '',
            estado: 'Pendiente',
            anotaciones: ''
        }
    });

    return (
        <AppContext.Provider
            value={{
                usuariosState, setUsuariosState,
                medicosState, setMedicosState,
                horariosState, setHorariosState,
                pacientesState, setPacientesState,
                citasState, setCitasState,
                personasState, setPersonasState
            }}
        >
            {props.children}
        </AppContext.Provider>
    );
}

export default AppProvider;

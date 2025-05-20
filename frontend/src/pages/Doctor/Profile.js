import React, { useState } from 'react';
import {Link, useNavigate} from 'react-router-dom';
import './Profile.css';

function Profile() {
    const [formData, setFormData] = useState({
        especialidad:'',
        costo_consulta: '',
        lugar_atencion:'',
        horario_inicio: '',
        horario_fin: '',
        frecuencia:'',
        foto:null,
        presentacion:''
    });

    const[error, setError] = useState('');

    const handleChange = (e) => {
        setFormData({...formData, [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
    };

    return(
        <div className="doctorProfileBody">
            <div className="divFormProfile">
                <h1>Perfil</h1>
                <form onSubmit={handleSubmit}>

                    <div className="col_datos">
                        <label htmlFor="especialidad">Especialidad:</label>
                        <input
                            type="text"
                            name="especialidad"
                            value={formData.especialidad}
                            onChange={handleChange}
                        />
                    </div>
                    <div className="col_datos">
                        <label htmlFor="horario_inicio">Horario de inicio:</label>
                        <select
                            name="horario_inicio"
                            value={formData.horario_inicio}
                            onChange={handleChange}
                        >
                            {Array.from({length: 24}, (_, index) => {
                                const hour = String(index).padStart(2, '0') + ':00';
                                return (
                                    <option key={hour} value={hour}>
                                        {hour}
                                    </option>
                                );
                            })}
                        </select>

                        <label htmlFor="horario_fin">Horario de fin:</label>
                        <select
                            name="horario_fin"
                            value={formData.horario_fin}
                            onChange={handleChange}
                        >
                            {Array.from({length: 24}, (_, index) => {
                                const hour = String(index).padStart(2, '0') + ':00';
                                return (
                                    <option key={hour} value={hour}>
                                        {hour}
                                    </option>
                                );
                            })}
                        </select>
                    </div>
                    <div className="col_datos">
                        <label htmlFor="costo_consulta">Costo de Consulta:</label>
                        <input
                            type="number"
                            name="costo_consulta"
                            value={formData.costo_consulta}
                            onChange={handleChange}
                        />
                    </div>
                    <div className="col_datos">
                        <label htmlFor="lugar_atencion">Lugar de Atención:</label>
                        <input
                            type="text"
                            name="lugar_atencion"
                            value={formData.lugar_atencion}
                            onChange={handleChange}
                        />
                    </div>
                    <div className="col_datos">
                        <label htmlFor="presentacion">Presentación</label>
                        <textarea
                            name="presentacion"
                            value={formData.presentacion}
                            onChange={handleChange}
                        ></textarea>
                    </div>
                    <div className="col_datos">
                        <button type="submit">Guardar</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default Profile;
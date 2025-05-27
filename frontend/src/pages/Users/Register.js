import './Login.css';
import React, {useContext, useEffect, useState} from 'react';
import './Register.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {faIdCard, faUser, faUserCircle, faKey, faLock, faUserTag, faFolder} from '@fortawesome/free-solid-svg-icons';
import {AppContext} from "../AppProvider";

function Register() {
    const {personasState, setPersonasState } = useContext(AppContext);
    const [error, setError] = useState();
    const [password, setPassword] = useState({password_c: ''});

    const backend = "http://localhost:8080";

    function handleChange(event) {
        const { name, value, files } = event.target;

        let updatedPersona = { ...personasState.persona };

        if (name === "cedula" || name === "nombre") {
            updatedPersona[name] = value;
        } else if (name === "username" || name === "clave" || name === "rol") {
            updatedPersona.usuario = {
                ...updatedPersona.usuario,
                [name]: value
            };
        } else if (name === "password_c") {
            setPassword({ password_c: value });
            return;
        } else if (name === "photo") {
            const file = files[0];
            if (file) {
                updatedPersona.foto_file = file; // Archivo real
                updatedPersona.foto_url = URL.createObjectURL(file); // Previsualización (opcional)
            }
        }

        setPersonasState(prev => ({
            ...prev,
            persona: updatedPersona
        }));
    }

    function clear() {
        setPersonasState({
            persona: {
                cedula: '',
                nombre: '',
                foto_url: '',
                foto_file: null,
                usuario: {
                    username: '',
                    clave: '',
                    rol: ''
                }
            }
        });
        setPassword({ password_c: '' });
    }


    function validar() {
        if (password.password_c !== personasState.persona.usuario.clave) {
            alert("Passwords do NOT match");
            return false;
        }

        const { cedula, nombre, usuario } = personasState.persona;
        if (!cedula || !nombre || !usuario.username || !usuario.clave || !usuario.rol) {
            alert("Todos los campos deben estar completos");
            return false;
        }

        return true;
    }

    function handleSave(event) {
        event.preventDefault();
        if (!validar()) return;
        const userAdd = {
            cedula: personasState.persona.cedula,
            nombre: personasState.persona.nombre,
            foto_url: personasState.persona.foto_url,
            usuario: personasState.persona.usuario
        };
        let request
        if(userAdd.usuario.rol === "Paciente") {
            request = new Request(`${backend}/pacientes/save`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(userAdd)
            });
        }
        else{
            request = new Request(`${backend}/medicos/save`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(userAdd)
            });
        }
        (async () => {
            try {
                const response = await fetch(request);

                if (!response.ok) {
                    const errorData = await response.json();
                    setError(errorData.message);
                    return;
                }

                setError(null);

            } catch (err) {
                setError("Error de red o del servidor");
            }
        })();
    }

    return (
        <div className="LoginBody" id="noPadding">
            <form className="register" onSubmit={handleSave} encType="multipart/form-data">
                <div>
                    <img src="/images/Registro%20Icon2.png" height="128" alt="Icon" />
                    <h2>REGISTER</h2>
                </div>

                {error && (
                    <div className="error-message">
                        <p>{error}</p>
                    </div>
                )}

                <div className="campo">
                    <FontAwesomeIcon icon={faIdCard} className="icon" />
                    <input
                        type="text"
                        name="cedula"
                        placeholder="ID"
                        required
                        value={personasState.persona.cedula}
                        onChange={handleChange}
                    />
                </div>

                <div className="campo">
                    <FontAwesomeIcon icon={faUser} className="icon" />
                    <input
                        type="text"
                        name="nombre"
                        placeholder="Name"
                        required
                        value={personasState.persona.nombre}
                        onChange={handleChange}
                    />
                </div>

                <div className="campo">
                    <FontAwesomeIcon icon={faUserCircle} className="icon" />
                    <input
                        type="text"
                        name="username"
                        placeholder="Username"
                        required
                        value={personasState.persona.usuario.username}
                        onChange={handleChange}
                    />
                </div>

                <div className="campo">
                    <FontAwesomeIcon icon={faKey} className="icon" />
                    <input
                        type="password"
                        name="clave"
                        placeholder="Password"
                        required
                        value={personasState.persona.usuario.clave}
                        onChange={handleChange}
                    />
                </div>

                <div className="campo">
                    <FontAwesomeIcon icon={faLock} className="icon" />
                    <input
                        type="password"
                        name="password_c"
                        placeholder="Confirm Password"
                        required
                        value={password.password_c}
                        onChange={handleChange}
                    />
                </div>

                <div className="campo">
                    <FontAwesomeIcon icon={faUserTag} className="icon"/>
                    <select
                        name="rol"
                        required
                        value={personasState.persona.usuario.rol || ''}
                        onChange={handleChange}
                    >
                        <option value="">Select role</option>
                        <option value="Paciente">Patient</option>
                        <option value="Medico">Doctor</option>
                    </select>

                </div>

                <div className="campo">
                    <FontAwesomeIcon icon={faFolder} className="icon"/>
                    <label htmlFor="photo" className="upload">Photo</label>
                    <input
                        type="file"
                        id="photo"
                        name="photo"
                        accept="image/*"
                        onChange={handleChange}
                    />
                </div>

                <div className="campo">
                    <button type="submit">Register</button>
                </div>
            </form>
        </div>
    );
}

export default Register;


import './Login.css';
import React, {useContext, useEffect, useState} from 'react';
import './Register.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {faIdCard, faUser, faUserCircle, faKey, faLock, faUserTag, faFolder} from '@fortawesome/free-solid-svg-icons';
import {AppContext} from "../AppProvider";
import { useRef } from 'react';

function Register({ onSuccess }) {
    const {personasState, setPersonasState } = useContext(AppContext);
    const [error, setError] = useState();
    const [password, setPassword] = useState({password_c: ''});
    const photoInput = useRef();

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
                updatedPersona.foto_file = file;
                updatedPersona.foto_url = URL.createObjectURL(file);
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
        if (photoInput.current) {
            photoInput.current.value = null;
        }
    }


    function validar() {
        if (password.password_c !== personasState.persona.usuario.clave) {
            setError("Las contraseñas no coinciden");
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

        const formData = new FormData();
        const persona = personasState.persona;

        formData.append("cedula", persona.cedula);
        formData.append("nombre", persona.nombre);
        formData.append("username", persona.usuario.username);
        formData.append("clave", persona.usuario.clave);
        formData.append("rol", persona.usuario.rol);

        if (persona.foto_file) {
            formData.append("foto", persona.foto_file);
        }

        const endpoint = backend + "/usuarios/register"

        fetch(endpoint, {
            method: "POST",
            body: formData, // NO headers here; browser sets correct boundary for multipart/form-data
        })
            .then(async (response) => {
                if (!response.ok) {
                    const errorText = await response.text();
                    setError(errorText);
                    return;
                }
                setError(null);
                clear();
                if (onSuccess) onSuccess();

            })
            .catch(() => {
                setError("Error de red o del servidor");
            });
    }

    return (
            <form className="register slide-down" onSubmit={handleSave} encType="multipart/form-data">
                <div>
                    <img src="/images/Registro%20Icon2.png" height="128" alt="Icon"/>
                    <h2>REGISTRO</h2>
                </div>

                {error && (
                    <div className="error-message">
                        <p>{error}</p>
                    </div>
                )}

                <div className="campo">
                    <FontAwesomeIcon icon={faIdCard} className="icon"/>
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
                    <FontAwesomeIcon icon={faUser} className="icon"/>
                    <input
                        type="text"
                        name="nombre"
                        placeholder="Nombre"
                        required
                        value={personasState.persona.nombre}
                        onChange={handleChange}
                    />
                </div>

                <div className="campo">
                    <FontAwesomeIcon icon={faUserCircle} className="icon"/>
                    <input
                        type="text"
                        name="username"
                        placeholder="Usuario"
                        required
                        value={personasState.persona.usuario.username}
                        onChange={handleChange}
                    />
                </div>

                <div className="campo">
                    <FontAwesomeIcon icon={faKey} className="icon"/>
                    <input
                        type="password"
                        name="clave"
                        placeholder="Contraseña"
                        required
                        value={personasState.persona.usuario.clave}
                        onChange={handleChange}
                    />
                </div>

                <div className="campo">
                    <FontAwesomeIcon icon={faLock} className="icon"/>
                    <input
                        type="password"
                        name="password_c"
                        placeholder="Confirmar contraseña"
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
                        <option value="">Seleccione rol</option>
                        <option value="Paciente">Paciente</option>
                        <option value="Medico">Medico</option>
                    </select>

                </div>

                <div className="campo">
                    <FontAwesomeIcon icon={faFolder} className="icon"/>
                    <label htmlFor="photo" className="upload">Foto</label>
                    <input
                        type="file"
                        id="photo"
                        name="photo"
                        required
                        accept="image/*"
                        onChange={handleChange}
                        ref={photoInput}
                    />
                </div>

                <div className="campo">
                    <button type="submit">Registrarse</button>
                </div>
            </form>
    );
}

export default Register;

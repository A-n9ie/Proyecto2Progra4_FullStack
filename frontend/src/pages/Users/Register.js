import './Login.css';
import React, { useState } from 'react';
import './Register.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {faIdCard, faUser, faUserCircle, faKey, faLock, faUserTag, faFolder} from '@fortawesome/free-solid-svg-icons';

function Register() {
    const [formData, setFormData] = useState({
        cedula: '',
        nombre: '',
        username: '',
        clave: '',
        password_c: '',
        rol: 'Paciente',
        photo: null,
    });

    const [error, setError] = useState('');

    const handleChange = (e) => {
        const { name, value, type, files } = e.target;
        setFormData({
            ...formData,
            [name]: type === 'file' ? files[0] : value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (formData.clave !== formData.password_c) {
            setError('Las contraseñas no coinciden');
            return;
        }

        setError('');
        // TODO: enviar los datos al backend con fetch o axios
        console.log('Datos enviados:', formData);
    };

    return (
        <div className="LoginBody" id="noPadding">
            <form className="register" onSubmit={handleSubmit} encType="multipart/form-data">
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
                        value={formData.cedula}
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
                        value={formData.nombre}
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
                        value={formData.username}
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
                        value={formData.clave}
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
                        value={formData.password_c}
                        onChange={handleChange}
                    />
                </div>

                <div className="campo">
                    <FontAwesomeIcon icon={faUserTag} className="icon" />
                    <select
                        name="rol"
                        required
                        value={formData.rol}
                        onChange={handleChange}
                    >
                        <option value="Paciente">Patient</option>
                        <option value="Medico">Doctor</option>
                    </select>
                </div>

                <div className="campo">
                    <FontAwesomeIcon icon={faFolder} className="icon" />
                    <label htmlFor="photo" className="upload">Photo</label>
                    <input
                        type="file"
                        id="photo"
                        name="photo"
                        accept="image/*"
                        onChange={handleChange}
                        required
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

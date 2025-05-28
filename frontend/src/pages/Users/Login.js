import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Login.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser, faLock } from '@fortawesome/free-solid-svg-icons';

function Login({ handleLogin }) {
    const [formData, setFormData] = useState({ usuario: '', clave: '' });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await handleLogin(formData);
            navigate("/home"); // redirección SPA (sin recarga)
        } catch (err) {
            setError("Credenciales inválidas desde LOGIN.JS");
        }
    };

    return (
        <div className="LoginBody">
            <div className="divFormLogin">
                <form onSubmit={handleSubmit}>
                    <div className="loginSeccion">
                        <img src="/images/login-icon.png" alt="Login" className="iconLogin" />
                        <div className="TituloLogin">Login</div>
                    </div>

                    <div className="loginFieldsContainer">
                        <div className="loginField">
                            <FontAwesomeIcon icon={faUser} className="icon" />
                            <input
                                type="text"
                                name="usuario"
                                value={formData.usuario}
                                onChange={handleChange}
                                placeholder="Usuario"
                                required
                            />
                        </div>
                    </div>

                    <div className="loginFieldsContainer">
                        <div className="loginField">
                            <FontAwesomeIcon icon={faLock} className="icon" />
                            <input
                                type="password"
                                name="clave"
                                value={formData.clave}
                                onChange={handleChange}
                                placeholder="Contraseña"
                                required
                            />
                        </div>
                    </div>

                    <div className="loginSeccion">
                        <input type="submit" value="Login" className="buttonLogin" />
                    </div>
                </form>

                <div className="RegisterHere">
                    ¿No tienes cuenta aún?
                    <p><Link to="/register">Regístrate aquí.</Link></p>
                </div>

                {error && (
                    <div className="toast">
                        <p>{error}</p>
                    </div>
                )}
            </div>
        </div>
    );
}

export default Login;

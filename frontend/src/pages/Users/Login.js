import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Login.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser, faLock } from '@fortawesome/free-solid-svg-icons';
import Register from './Register'

function Login({ handleLogin }) {
    const [formData, setFormData] = useState({ usuario: '', clave: '' });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    //Pop Up
    const [showRegister, setShowRegister] = useState(false);
    const [isClosing, setIsClosing] = useState(false);

    const closeModal = () => {
        setIsClosing(true);
        setTimeout(() => {
            setShowRegister(false);
            setIsClosing(false);
        }, 400); // mismo tiempo que la animación CSS
    };
    //-----

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
                        <img src="/images/login-icon.png" alt="Login" className="iconLogin"/>
                        <div className="TituloLogin">Login</div>
                    </div>

                    <div className="loginFieldsContainer">
                        <div className="loginField">
                            <FontAwesomeIcon icon={faUser} className="icon"/>
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
                            <FontAwesomeIcon icon={faLock} className="icon"/>
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
                        <input type="submit" value="Login" className="buttonLogin"/>
                    </div>
                </form>

                <div className="RegisterHere">
                    ¿No tienes cuenta aún?
                    <p>
                        <button className="linkButton" onClick={() => setShowRegister(true)}>Regístrate aquí.</button>
                    </p>
                </div>

                {error && (
                    <div className="toast">
                        <p>{error}</p>
                    </div>
                )}
            </div>
            {showRegister && (
                <div className="modal-overlay">
                    <div className={`modal-content ${isClosing ? 'slide-up' : 'slide-down'}`}>
                        <button className="closeModal" onClick={closeModal}>X</button>
                        <Register onSuccess={() => {
                            closeModal();
                            setTimeout(() => {
                                alert("Successfull");
                            }, 500);
                        }} />
                    </div>
                </div>
            )}
        </div>
    );
}

export default Login;

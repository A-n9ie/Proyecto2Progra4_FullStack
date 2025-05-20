import React, { useState } from 'react';
import {Link, useNavigate} from 'react-router-dom';
import './Login.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser, faLock } from '@fortawesome/free-solid-svg-icons';

function Login() {
    const [formData, setFormData] = useState({ username: '', password: '' });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
            setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (formData.username === 'admin' && formData.password === '123') {
            setError('');
            navigate('/');
        } else {
            setError('Usuario no existe o contraseña incorrecta');
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
                                name="username"
                                value={formData.username}
                                onChange={handleChange}
                                placeholder="Username"
                                required
                            />
                        </div>
                    </div>

                    <div className="loginFieldsContainer">
                        <div className="loginField">
                            <FontAwesomeIcon icon={faLock} className="icon" />
                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                placeholder="Password"
                                required
                            />
                        </div>
                    </div>

                    <div className="loginSeccion">
                        <input type="submit" value="Login" className="buttonLogin" />
                    </div>
                </form>

                <div className="RegisterHere">
                    Don't have an account yet?
                    <p><Link to="/register">Sign up here.</Link></p>
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

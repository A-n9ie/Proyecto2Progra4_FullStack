import './App.css';
import AppProvider from './pages/AppProvider';
import Register from './pages/Users/Register'
import Login from './pages/Users/Login'
import Profile from './pages/Doctor/Profile'
import Principal from "./pages/Principal/principal";
// import {Link, BrowserRouter, Routes, Route, useNavigate} from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPhoneAlt } from '@fortawesome/free-solid-svg-icons';
import { faTwitter, faFacebookF, faInstagram } from '@fortawesome/free-brands-svg-icons';
import React, { useState, useEffect } from 'react';
import { Link, BrowserRouter, Routes, Route, useNavigate, Navigate } from 'react-router-dom';

//Authorization: 'Bearer ' + localStorage.getItem('token')
//Para obtener los datos se usa eso

// const backend = "http://localhost:8080";

function App() {
    const [user, setUser] = useState({ id: null, rol: '', name: '' });

    // Carga usuario de token si hay en localStorage al iniciar la app
    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            // Validar el token con el backend
            fetch("http://localhost:8080/usuarios/verificar-token", {
                headers: {
                    "Authorization": "Bearer " + token
                }
            })
                .then(res => {
                    if (!res.ok) throw new Error();
                    const usuario = getUser(token);
                    setUser(usuario);
                })
                .catch(() => {
                    // Token inválido o backend caído
                    localStorage.removeItem("token");
                    setUser({ id: null, rol: '', name: '' });
                });
        }
    }, []);


    function getUser(token) {
        try {
            const parts = token.split(".");
            const payload = JSON.parse(atob(parts[1]));
            return { id: payload.id, rol: payload.scope[0], name: payload.name };
        } catch (err) {
            return { id: null, rol: '', name: '' };
        }
    }

    async function handleLogin(userData) {
        const url = "http://localhost:8080/usuarios/login";
        const request = new Request(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(userData),
        });

        const response = await fetch(request);
        if (!response.ok) throw new Error("Login failed");

        const data = await response.json();
        const token = data.token;

        localStorage.setItem("token", token);
        const usuario = getUser(token);
        setUser(usuario);
        return usuario;
    }

    function handleLogout() {
        localStorage.removeItem("token");
        setUser({ id: null, rol: '', name: '' });
    }

    return (
        <div className="App">
            <BrowserRouter>
                <Header user={user} handleLogout={handleLogout} />
                <Main user={user} handleLogin={handleLogin} />
                <Footer />
            </BrowserRouter>
        </div>
    );
}

function Main({ user, handleLogin }) {
    return (
        <div className={"App-main"}>
            <AppProvider>
                <Routes>
                    <Route path="/" element={<Principal />} />
                    <Route path="/login" element={user.id ? <Navigate to="/" /> : <Login handleLogin={handleLogin} />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/profile" element={user.id ? <Profile /> : <Navigate to="/login" />} />
                </Routes>
            </AppProvider>
        </div>
    );
}

function Header({ user, handleLogout }) {
    const navigate = useNavigate();

    function onLogout() {
        handleLogout();
        navigate("/");
    }

    return (
        <header>
            <div className="header-content">
                <Link to="/" className="invicible-link">
                    <h2>
                        <img src="/images/dasdasd.jpg" height="80" alt="logo" />
                        Medical Appointment
                    </h2>
                </Link>

                <div className="telefono">
                    <p>
                        <FontAwesomeIcon icon={faPhoneAlt} style={{ fontSize: '15px', color: 'orange' }} />
                        +506 5467 0937
                    </p>
                </div>

                <div className="header-links">
                    <p>About</p>
                    <p>Search</p>
                    <nav>
                        <ul className="Menu">
                            <li><Link to="/">Inicio</Link></li>
                            {!user.id && <li><Link to="/login">Login</Link></li>}
                            {user.id && <li><Link to="/profile">Profile</Link></li>}
                            {user.id && <li><Link to="/" onClick={onLogout}>Logout</Link></li>}
                        </ul>
                    </nav>
                </div>
            </div>
        </header>
    );
}

function Footer() {
    return (
        <footer>
            <div className="footer-content">
                Total Soft Inc.
                <div>
                    <Link to="#" className="icon"><FontAwesomeIcon icon={faTwitter} /></Link>
                    <Link to="#" className="icon"><FontAwesomeIcon icon={faFacebookF} /></Link>
                    <Link to="#" className="icon"><FontAwesomeIcon icon={faInstagram} /></Link>
                </div>
                <div className="AnioInf">©2019 Tsf, Inc.</div>
            </div>
        </footer>
    );
}


export default App;
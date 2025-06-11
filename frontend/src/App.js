import './App.css';
import AppProvider from './pages/AppProvider';
import Register from './pages/Users/Register'
import Login from './pages/Users/Login'
import ProfileDoctor from './pages/Doctor/Profile'
import ProfilePatient from './pages/Patient/Profile'
import Principal from "./pages/Principal/principal";
import NewAppointment from "./pages/Appointments/newAppointment";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPhoneAlt } from '@fortawesome/free-solid-svg-icons';
import { faTwitter, faFacebookF, faInstagram } from '@fortawesome/free-brands-svg-icons';
import React, { useState, useEffect } from 'react';
import { Link, BrowserRouter, Routes, Route, useNavigate, Navigate } from 'react-router-dom';
import History from "./pages/Patient/History";
import Appointment from "./pages/Doctor/Appointment";
import Management from "./pages/Admin/management";
import Schedule from "./pages/Principal/schedule";


function App() {
    const [user, setUser] = useState({ id: null, rol: '', name: '' });
    const [estadoPerfil, setEstadoPerfil] = useState(null);

    // Carga usuario de token
    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            fetch("http://localhost:8080/usuarios/verificar-token", {
                headers: { "Authorization": "Bearer " + token }
            })
                .then(res => {
                    if (!res.ok) throw new Error();
                    // Obtener usuario desde token y setear en estado
                    const usuario = getUser(token);
                    setUser(usuario);
                    setEstadoPerfil(usuario.estadoPerfil); // <---- Aquí actualizo estadoPerfil

                    return fetch("http://localhost:8080/usuarios/me", {
                        headers: { "Authorization": "Bearer " + token }
                    });
                })
                .then(res => res.json())
                .catch(() => {
                    localStorage.removeItem("token");
                    setUser({ id: null, rol: '', name: '' });
                    setEstadoPerfil(null);
                });
        }
    }, []);

    // Decodifica token para obtener info usuario
    function getUser(token) {
        try {
            const parts = token.split(".");
            const payload = JSON.parse(atob(parts[1]));
            return { id: payload.id, rol: payload.scope[0], name: payload.name, estadoPerfil: payload.estadoPerfil };
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
        const estadoPerfil = data.estadoPerfil || null;  // <-- acá obtienes directamente el estadoPerfil

        localStorage.setItem("token", token);

        // Aquí setea el usuario decodificando el token, y el estadoPerfil directo desde data
        const usuario = getUser(token);

        setUser(usuario);
        setEstadoPerfil(estadoPerfil);

        return { usuario, estadoPerfil };
    }


    function handleLogout() {
        localStorage.removeItem("token");
        setUser({ id: null, rol: '', name: '' });
        setEstadoPerfil(null);
    }

    return (
        <div className="App">
            <BrowserRouter>
                <Header user={user} handleLogout={handleLogout} />
                {/* Paso estadoPerfil a Main */}
                <Main user={user} estadoPerfil={estadoPerfil} handleLogin={handleLogin} />
                <Footer />
            </BrowserRouter>
        </div>
    );
}

function Main({ user, estadoPerfil, handleLogin }) {
    console.log("Main render - estadoPerfil:", estadoPerfil, "user:", user);
    return (
        <div className={"App-main"}>
            <AppProvider>
                <Routes>
                    <Route path="/" element={<Principal />} />

                    <Route path="/login" element={
                        user.id
                            ? (user.rol === 'Medico'
                                ? (estadoPerfil === 'incompleto'
                                    ? <Navigate to="/profile" />
                                    : <Navigate to="/history" />)
                                : user.rol === 'Administrador'
                                    ? <Navigate to="/management" />
                                    : <Navigate to="/" />)
                            : <Login handleLogin={handleLogin} />
                    } />

                    {/* Registro accesible para todos */}
                    <Route path="/register" element={<Register />} />

                    {/* Si el usuario no es Admin, mostrarle sus rutas */}
                    {user.rol !== 'Administrador' && (
                        <>
                            <Route path="/agendar" element={<NewAppointment />} />
                            <Route path="/profileDoctor" element={<ProfileDoctor />} />
                            <Route path="/profilePatient" element={<ProfilePatient user={user} />} />
                            <Route path="/profile" element={
                                user.id
                                    ? (user.rol === 'Medico'
                                        ? <Navigate to="/profileDoctor" />
                                        : <Navigate to="/profilePatient" />)
                                    : <Navigate to="/login" />
                            } />
                            <Route path="/horarios_medico" element={<Schedule/>} />
                            <Route path="/historyPatient" element={<History />} />
                            <Route path="/appointments" element={<Appointment />} />
                            <Route path="/history" element={
                                user.id
                                    ? (user.rol === 'Medico'
                                        ? <Navigate to="/appointments" />
                                        : <Navigate to="/historyPatient" />)
                                    : <Navigate to="/login" />
                            } />
                        </>
                    )}

                    {/* Ruta exclusiva para admin */}
                    {user.rol === 'Administrador' && (
                        <Route path="/management" element={<Management />} />
                    )}
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
                            {!user.id && <li><Link to="/login">Login</Link></li>}

                            {user.id && (
                                <li className="dropdown">
                                    <input type="checkbox" id="user-toggle" className="dropdown-toggle" />
                                    <label htmlFor="user-toggle" className="dropbtn">{user.name}</label>
                                    <ul className="dropdown-content">
                                        {/* Mostrar solo si NO es administrador */}
                                        {user.rol !== 'Administrador' && (
                                            <>
                                                <li><Link to="/profile">Perfil</Link></li>
                                                <li><Link to="/history">Historial</Link></li>
                                            </>
                                        )}
                                        {/* Mostrar solo si ES administrador */}
                                        {user.rol === 'Administrador' && (
                                            <li><Link to="/management">Gestión</Link></li>
                                        )}
                                        <li><button onClick={onLogout}>Cerrar sesión</button></li>
                                    </ul>
                                </li>
                            )}
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
                    <Link to="#" className="icon"><FontAwesomeIcon icon={faTwitter}/></Link>
                    <Link to="#" className="icon"><FontAwesomeIcon icon={faFacebookF}/></Link>
                    <Link to="#" className="icon"><FontAwesomeIcon icon={faInstagram}/></Link>
                </div>
                <div className="AnioInf">©2019 Tsf, Inc.</div>
            </div>
        </footer>
    );
}


export default App;
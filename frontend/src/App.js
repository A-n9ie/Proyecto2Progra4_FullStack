import './App.css';
import AppProvider from './pages/AppProvider';
import Register from './pages/Users/Register'
import Login from './pages/Users/Login'
import Profile from './pages/Doctor/Profile'
import Principal from "./pages/Principal/principal";
import { Link, BrowserRouter, Routes, Route } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPhoneAlt } from '@fortawesome/free-solid-svg-icons';
import { faTwitter, faFacebookF, faInstagram } from '@fortawesome/free-brands-svg-icons';

function App() {
  return (
      <div className = "App">
        <BrowserRouter>
          <Header />
          <Main />
          <Footer />
        </BrowserRouter>
      </div>
  );
}


function Main(){
  return(
      <div className={"App-main"}>
        <AppProvider>
          <Routes>
            <Route exact path="/" element={<Principal/>}/>
            <Route exact path="/login" element={<Login />}/>
            <Route path="/register" element={<Register />} />
            <Route path="/profile" element={<Profile/>} />
          </Routes>
        </AppProvider>
      </div>
  );
}

function Header() {
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
                <li>
                    <Link to="/login">Login</Link>
                    <ul>
                        <li><Link to="/profile">Profile</Link></li>
                        <li><Link to="/logout">Logout</Link></li>
                    </ul>
                </li>
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
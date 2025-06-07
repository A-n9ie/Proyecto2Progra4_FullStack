
import {useLocation, useNavigate} from 'react-router-dom';
import './newAppointment.css';


function AgendarCitas() {
    const location = useLocation();
    const {medico, fecha, hora} = location.state || {};
    const navigate = useNavigate();


    if (!medico || !fecha || !hora) {
        return <p>poner un mensaje de error </p>
    }

    const backend = "http://localhost:8080/pacientes";

    const handleConfirmar = async () => {
        try {
            const formData = new URLSearchParams();
            formData.append('si', 'confirmar');
            formData.append('dia', fecha);
            formData.append('hora', hora);
            formData.append('idMedico', medico.idMedico,
                );

            const response = await fetch(backend+'/confirmar', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: formData.toString(),
            });
            const texto = await response.text();
            console.log('Respuesta backend:', texto);

            if (response.ok) {
                navigate('/');
            } else {
                alert('Error al confirmar la cita: ' + texto);
            }
        } catch (error) {
            console.error('Error al confirmar la cita:', error);
        }
    };

    const handleCancelar = () => {

        window.history.back();
    };

    return (
        <div className="confirmar_cita">
            <h2>Confirmar Cita</h2>

            <div className="perfil_medico">
                <img src={`http://localhost:8080/imagenes/ver/${medico.fotoUrl}`}
                    alt={`Foto de ${medico.nombre}`}
                    className="foto_medico"
                    />
                <div className="info_medico">
                    <p><strong>{medico.nombre}</strong></p>
                    <p>{fecha} - {hora}</p>
                    <p>{medico.lugarAtencion}</p>
                </div>
            </div>

            <div className="botones_confirmar">
                <button className="botones" onClick={handleConfirmar}>Confirmar</button>
                <button className="botones" onClick={handleCancelar}>Cancelar</button>
            </div>
        </div>
    );
}

export default AgendarCitas;
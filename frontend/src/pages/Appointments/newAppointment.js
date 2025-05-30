
import { useLocation } from 'react-router-dom';

function AgendarCitas({user}) {
    const location = useLocation();
    const {medico, fecha, hora} = location.state || {};

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
            formData.append('medicoId', medico.id,
                );

            console.log(fecha);
            console.log(hora);

            const response = await fetch(backend+'/confirmar', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: formData.toString(),
            });
            const texto = await response.text();
            console.log('Respuesta backend:', texto);

            if (response.ok) {
                alert('Cita confirmada exitosamente');
            } else {
                alert('Error al confirmar la cita: ' + texto);
            }
        } catch (error) {
            console.error('Error al confirmar la cita:', error);
        }
    };

    const handleCancelar = () => {

        window.history.back(); // o navigate('/otroPath');
    };

    return (
        <div className="confirmar_cita">
            <h2>Confirmar Cita</h2>
            <img src={`/fotosPerfil/${medico.fotoUrl}`} height="512" width="512" alt="Foto de perfil"/>
            <p><i className="fa-solid fa-user"></i>{medico.nombre}</p>
            <p><i className="fa-solid fa-calendar"></i> {fecha} {hora} </p>
            <p><i className="fa-solid fa-location-dot"></i> {medico.lugarAtencion}</p>

            {/*guardarlo en la base de datos*/}
            {/*<form action= backend + "confirmar" method="post">*/}
            {/*    <input type="hidden" name="dia" value={fecha}/>*/}
            {/*    <input type="hidden" name="hora" value={hora}/>*/}
            {/*    <input type="hidden" name="medicoId" value={medico.id}/>*/}
            <div>
                <button onClick={handleConfirmar}>Confirmar</button>
                <button onClick={handleCancelar}>Cancelar</button>
            </div>
        </div>
    )
        ;
}

export default AgendarCitas;
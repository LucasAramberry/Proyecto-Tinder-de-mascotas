package edu.egg.web.tinder.servicios;

import edu.egg.web.tinder.entidades.Mascota;
import edu.egg.web.tinder.entidades.Voto;
import edu.egg.web.tinder.errores.ErrorServicio;
import edu.egg.web.tinder.repositorios.MascotaRepositorio;
import edu.egg.web.tinder.repositorios.VotoRepositorio;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VotoServicio {

    @Autowired
    private VotoRepositorio votoRepositorio;
    @Autowired
    private MascotaRepositorio mascotaRepositorio;
    @Autowired
    private NotificacionServicio notificacionServicio;

    public void votar(String idUsuario, String idMascota1, String idMascota2) throws ErrorServicio {

        Voto voto = new Voto();
        voto.setFecha(new Date());
        if (idMascota1.equals(idMascota2)) {
            throw new ErrorServicio("No puede votarse a si mismo");
        }
        Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascota1);
        if (respuesta.isPresent()) {
            Mascota mascota1 = respuesta.get();
            if (mascota1.getUsuario().getId().equals(idUsuario)) {
                voto.setMascota1(mascota1);
            } else {
                throw new ErrorServicio("No tiene permisos para realizar la operacion solicitada");
            }
        } else {
            throw new ErrorServicio("No existe una mascota vinculada a ese identificador");
        }

        Optional<Mascota> respuesta2 = mascotaRepositorio.findById(idMascota2);
        if (respuesta2.isPresent()) {
            Mascota mascota2 = respuesta2.get();
            voto.setMascota2(mascota2);
            notificacionServicio.enviar("Tu mascota ha sido votada.", "Tinder de mascota", mascota2.getUsuario().getMail());
        } else {
            throw new ErrorServicio("No existe una mascota vinculada a ese identificador");
        }
        votoRepositorio.save(voto);
    }

    public void responder(String idUsuario, String idVoto) throws ErrorServicio {

        Optional<Voto> respuesta = votoRepositorio.findById(idVoto);
        if (respuesta.isPresent()) {
            Voto voto = respuesta.get();
            voto.setRepuesta(new Date());
            if (voto.getMascota2().getId().equals(idUsuario)) {
                notificacionServicio.enviar("Tu voto fue correspondido.", "Tinder de mascota", voto.getMascota1().getUsuario().getMail());
                votoRepositorio.save(voto);
            } else {
                throw new ErrorServicio("No tiene permisos para realizar la operacion solicitada");
            }
        } else {
            throw new ErrorServicio("No existe el voto solicitado");
        }
    }

}

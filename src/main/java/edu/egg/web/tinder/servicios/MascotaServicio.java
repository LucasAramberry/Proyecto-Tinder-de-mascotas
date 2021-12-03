package edu.egg.web.tinder.servicios;

import edu.egg.web.tinder.entidades.Foto;
import edu.egg.web.tinder.entidades.Mascota;
import edu.egg.web.tinder.entidades.Usuario;
import edu.egg.web.tinder.enumeraciones.Sexo;
import edu.egg.web.tinder.enumeraciones.Tipo;
import edu.egg.web.tinder.errores.ErrorServicio;
import edu.egg.web.tinder.repositorios.MascotaRepositorio;
import edu.egg.web.tinder.repositorios.UsuarioRepositorio;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MascotaServicio {

    @Autowired
    private MascotaRepositorio mascotaRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private FotoServicio fotoServicio;

    @Transactional
    public void agregarMascota(MultipartFile archivo, String idUsuario, String nombre, Sexo sexo, Tipo tipo) throws ErrorServicio {

        Usuario usuario = usuarioRepositorio.findById(idUsuario).get();
        validarMascota(nombre, sexo);
        Mascota mascota = new Mascota();
        mascota.setNombre(nombre);
        mascota.setSexo(sexo);
        mascota.setAlta(new Date());
        mascota.setUsuario(usuario);
        mascota.setTipo(tipo);
        Foto foto = fotoServicio.guardar(archivo);
        mascota.setFoto(foto);
        mascotaRepositorio.save(mascota);
    }

    @Transactional
    public void modificarMascota(MultipartFile archivo, String idUsuario, String idMascota, String nombre, Sexo sexo, Tipo tipo) throws ErrorServicio {

        validarMascota(nombre, sexo);
        Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascota);
        if (respuesta.isPresent()) {
            Mascota mascota = respuesta.get();
            if (mascota.getUsuario().getId().equals(idUsuario)) {
                mascota.setNombre(nombre);
                mascota.setSexo(sexo);

                String idFoto = null;
                if (mascota.getFoto() != null) {
                    idFoto = mascota.getFoto().getId();
                }
                Foto foto = fotoServicio.actualizar(idFoto, archivo);
                mascota.setFoto(foto);
                mascota.setTipo(tipo);

                mascotaRepositorio.save(mascota);
            } else {
                throw new ErrorServicio("No tiene permisos suficientes para realizar la operacion");
            }
        } else {
            throw new ErrorServicio("No existe una mascota con el identificador solicitado");
        }
    }

    @Transactional
    public void eliminarMascota(String idUsuario, String idMascota) throws ErrorServicio {
        Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascota);
        if (respuesta.isPresent()) {
            Mascota mascota = respuesta.get();
            if (mascota.getUsuario().getId().equals(idUsuario)) {
                mascota.setBaja(new Date());
                mascotaRepositorio.save(mascota);
            }
        } else {
            throw new ErrorServicio("No existe una mascota con el identificador solicitado");
        }
    }

    public void validarMascota(String nombre, Sexo sexo) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre de la mascota no puede ser nulo o vacio");
        }
        if (sexo == null) {
            throw new ErrorServicio("El sexo de la mascota no puede ser nulo");
        }
    }

    public Mascota buscarMascotaPorId(String id) throws ErrorServicio {
        Optional<Mascota> respuesta = mascotaRepositorio.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new ErrorServicio("No se encontro la mascota solicitada");
        }
    }

    public List<Mascota> buscarMascotasPorUsuario(String id) {
        return mascotaRepositorio.buscarMascotasPorUsuario(id);
    }
}

package modelo.services;



import java.util.Optional;

import modelo.dao.SesionLlamadaDAO;
import modelo.dao.UsuarioDAO;
import modelo.entities.LlamadaAcceso;
import modelo.entities.LlamadaTokenRespuesta;
import modelo.entities.Rol;
import modelo.entities.SesionLlamada;
import modelo.entities.Usuario;
import util.EmailService;
import util.LiveKitTokenService;
import util.LlamadaSalaUtil;

public class LlamadaService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final SesionLlamadaDAO sesionLlamadaDAO = new SesionLlamadaDAO();
    private final LiveKitTokenService liveKitTokenService = new LiveKitTokenService();

    public static class ResultadoLlamada {
        public final SesionLlamada sesion;
        public final String linkTutor;
        public final String linkEstudiante;

        public ResultadoLlamada(SesionLlamada sesion, String linkTutor, String linkEstudiante) {
            this.sesion = sesion;
            this.linkTutor = linkTutor;
            this.linkEstudiante = linkEstudiante;
        }
    }

    public ResultadoLlamada crearLlamada(Long tutorId, Long studentId) {
        Optional<Usuario> tutorOpt = usuarioDAO.buscarPorId(tutorId);
        if (tutorOpt.isEmpty() || tutorOpt.get().getRol() != Rol.TUTOR) {
            throw new IllegalArgumentException("El tutor indicado no es válido.");
        }

        Optional<Usuario> studentOpt = usuarioDAO.buscarPorId(studentId);
        if (studentOpt.isEmpty() || studentOpt.get().getRol() != Rol.ESTUDIANTE) {
            throw new IllegalArgumentException("El estudiante indicado no es válido.");
        }

        Usuario tutor = tutorOpt.get();
        Usuario estudiante = studentOpt.get();

        SesionLlamada sesion = new SesionLlamada();
        sesion.setTutorId(tutorId);
        sesion.setStudentId(studentId);
        sesion.setCompletada(false);
        sesionLlamadaDAO.guardar(sesion);

        LlamadaTokenRespuesta respuestaTokens;
        try {
            respuestaTokens = liveKitTokenService.crearSalaYTokens(sesion.getId(), tutorId, studentId);
        } catch (RuntimeException e) {
            System.err.println("ERROR al crear sala de llamada para sesionId=" + sesion.getId());
            e.printStackTrace();
            throw new IllegalStateException("No se pudo crear la sala de llamada.", e);
        }

        sesion.setRoomName(respuestaTokens.roomName());
        sesion.setLivekitUrl(respuestaTokens.livekitUrl());
        sesion.setTutorToken(respuestaTokens.tutorToken());
        sesion.setStudentToken(respuestaTokens.studentToken());
        sesionLlamadaDAO.actualizar(sesion);

        LlamadaAcceso accesoTutor = LlamadaSalaUtil.crearAcceso(
                "Tutor", tutor.getEmail(), respuestaTokens, sesion.getId(), respuestaTokens.tutorToken());
        LlamadaAcceso accesoEstudiante = LlamadaSalaUtil.crearAcceso(
                "Estudiante", estudiante.getEmail(), respuestaTokens, sesion.getId(), respuestaTokens.studentToken());

        EmailService.enviarEnlaceLlamada(tutor.getEmail(), accesoTutor.enlace(), accesoTutor.rol());
        EmailService.enviarEnlaceLlamada(estudiante.getEmail(), accesoEstudiante.enlace(), accesoEstudiante.rol());

        return new ResultadoLlamada(sesion, accesoTutor.enlace(), accesoEstudiante.enlace());
    }
}
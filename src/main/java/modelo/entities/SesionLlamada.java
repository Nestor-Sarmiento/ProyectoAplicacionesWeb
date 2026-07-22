package modelo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "sesion_llamada")
public class SesionLlamada implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tutor_id", nullable = false)
    private Long tutorId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "room_name", length = 255)
    private String roomName;

    @Column(name = "livekit_url", length = 500)
    private String livekitUrl;

    @Column(name = "tutor_token", length = 2000)
    private String tutorToken;

    @Column(name = "student_token", length = 2000)
    private String studentToken;

    private Integer calificacion;

    @Column(length = 1000)
    private String comentario;

    @Column(nullable = false)
    private boolean completada = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getLivekitUrl() {
        return livekitUrl;
    }

    public void setLivekitUrl(String livekitUrl) {
        this.livekitUrl = livekitUrl;
    }

    public String getTutorToken() {
        return tutorToken;
    }

    public void setTutorToken(String tutorToken) {
        this.tutorToken = tutorToken;
    }

    public String getStudentToken() {
        return studentToken;
    }

    public void setStudentToken(String studentToken) {
        this.studentToken = studentToken;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }
}
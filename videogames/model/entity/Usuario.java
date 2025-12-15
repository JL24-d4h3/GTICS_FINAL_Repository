package com.ex2.videogames.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuario")
    private Integer id;

    @Column(name = "nombres")
    private String nombre;

    @Column(name = "apellidos")
    private String apellido;

    @Column(name = "correos")
    private String correo;

    @Column(name = "password")
    private String contrasena;

    @Column(name = "autorizacion")
    private String autorizacion;

    @Column(name = "enabled")
    private Boolean activo;
}

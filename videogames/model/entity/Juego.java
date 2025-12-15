package com.ex2.videogames.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
@Entity
@Table(name = "juegos")
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idjuego", unique = true, nullable = false)
    private Integer id;

    @Size(min = 1, max = 50)
    @Column(name = "nombre")
    private String nombre;

    @Size(min = 1, max = 448)
    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio")
    private Double precio;

    @Size(min = 1, max = 400)
    @Column(name = "image")
    private String imagen;

    @ManyToOne
    @JoinColumn(name = "idgenero")
    private Genero genero;

    @ManyToOne
    @JoinColumn(name = "editoras")
    private Editora editora;

    @ManyToOne
    @JoinColumn(name = "distribuidoras")
    private Distribuidora distribuidora;

    @ManyToOne
    @JoinColumn(name = "plataformas")
    private Plataforma plataforma;
}

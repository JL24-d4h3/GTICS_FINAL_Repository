package com.ex2.videogames.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name  = "idfactura", unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false, name = "fechaEnvio")
    private Date fecha;

    @Column(nullable = false, name = "tarjeta")
    private String tarjeta;

    @Column(nullable = false, name = "codigoVerificacion")
    private String codigo;

    @Column(nullable = false, name = "direccion")
    private String direccion;

    @Column(nullable = false, name = "monto")
    private Float monto;
}

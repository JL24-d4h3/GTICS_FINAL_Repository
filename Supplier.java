package com.lab8.servicio_web_restful.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Suppliers")
@Getter
@Setter
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SupplierID")
    private Integer id;

    @Column(name = "SupplierName", length = 50)
    private String supplierName;

    @Column(name = "ContactName", length = 50)
    private String contactName;

    @Column(name = "Address", length = 50)
    private String address;

    @Column(name = "City", length = 20)
    private String city;

    @Column(name = "PostalCode", length = 10)
    private String postalCode;

    @Column(name = "Country", length = 15)
    private String country;

    @Column(name = "Phone", length = 15)
    private String phone;
}

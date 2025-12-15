package com.ex2.videogames.service;

import com.ex2.videogames.model.entity.*;
import com.ex2.videogames.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JuegoService {

    private final JuegoRepository juegoRepository;
    private final GeneroRepository generoRepository;
    private final PlataformaRepository plataformaRepository;
    private final EditoraRepository editoraRepository;
    private final DistribuidoraRepository distribuidoraRepository;

    public JuegoService(JuegoRepository juegoRepository, GeneroRepository generoRepository, PlataformaRepository plataformaRepository, EditoraRepository editoraRepository, DistribuidoraRepository distribuidoraRepository) {
        this.juegoRepository = juegoRepository;
        this.generoRepository = generoRepository;
        this.plataformaRepository = plataformaRepository;
        this.editoraRepository = editoraRepository;
        this.distribuidoraRepository = distribuidoraRepository;
    }

    public List<Juego> obtenerTodos () {

        return juegoRepository.findAll();
    }

    public Optional<Juego> obtenerPorId(Integer id) {

        return juegoRepository.findById(id);
    }

    public Juego crear(Juego juego) {
        if (juego == null) {
            return null;
        }

        // Validar que los campos requeridos no estén vacíos
        if (juego.getNombre() == null || juego.getNombre().trim().isEmpty()) {
            return null;
        }

        // Validar que las relaciones no sean nulas y tengan ID
        if (juego.getGenero() == null || juego.getGenero().getId() == null ||
            juego.getPlataforma() == null || juego.getPlataforma().getId() == null ||
            juego.getEditora() == null || juego.getEditora().getId() == null ||
            juego.getDistribuidora() == null || juego.getDistribuidora().getId() == null) {
            return null;
        }

        // Verificar que las entidades relacionadas existan en la base de datos
        Genero genero = generoRepository.findById(juego.getGenero().getId()).orElse(null);
        Plataforma plataforma = plataformaRepository.findById(juego.getPlataforma().getId()).orElse(null);
        Editora editora = editoraRepository.findById(juego.getEditora().getId()).orElse(null);
        Distribuidora distribuidora = distribuidoraRepository.findById(juego.getDistribuidora().getId()).orElse(null);

        if (genero == null || plataforma == null || editora == null || distribuidora == null) {
            return null;
        }

        // Establecer las entidades completas
        juego.setGenero(genero);
        juego.setPlataforma(plataforma);
        juego.setEditora(editora);
        juego.setDistribuidora(distribuidora);

        return juegoRepository.save(juego);
    }

    public Juego actualizar(Juego juego) {
        if (juego == null || juego.getId() == null) {
            return null;
        }

        // Verificar que el juego exista
        if (!juegoRepository.existsById(juego.getId())) {
            return null;
        }

        // Validar que los campos requeridos no estén vacíos
        if (juego.getNombre() == null || juego.getNombre().trim().isEmpty()) {
            return null;
        }

        // Validar que las relaciones no sean nulas y tengan ID
        if (juego.getGenero() == null || juego.getGenero().getId() == null ||
            juego.getPlataforma() == null || juego.getPlataforma().getId() == null ||
            juego.getEditora() == null || juego.getEditora().getId() == null ||
            juego.getDistribuidora() == null || juego.getDistribuidora().getId() == null) {
            return null;
        }

        // Verificar que las entidades relacionadas existan en la base de datos
        Genero genero = generoRepository.findById(juego.getGenero().getId()).orElse(null);
        Plataforma plataforma = plataformaRepository.findById(juego.getPlataforma().getId()).orElse(null);
        Editora editora = editoraRepository.findById(juego.getEditora().getId()).orElse(null);
        Distribuidora distribuidora = distribuidoraRepository.findById(juego.getDistribuidora().getId()).orElse(null);

        if (genero == null || plataforma == null || editora == null || distribuidora == null) {
            return null;
        }

        // Establecer las entidades completas
        juego.setGenero(genero);
        juego.setPlataforma(plataforma);
        juego.setEditora(editora);
        juego.setDistribuidora(distribuidora);

        return juegoRepository.save(juego);
    }

    public void eliminar(Juego juego) {
        if (juego == null || juego.getId() == null) {
            return;
        }

        if (juegoRepository.existsById(juego.getId())) {
            juegoRepository.deleteById(juego.getId());
        }
    }
}

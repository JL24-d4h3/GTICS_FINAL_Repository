package com.ex2.videogames.controller;

import com.ex2.videogames.model.entity.Juego;
import com.ex2.videogames.service.JuegoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/juegos")
public class JuegoController {

    @Autowired
    private JuegoService juegoService;

    @GetMapping
    public ResponseEntity<?> getAllGames() {
        return ResponseEntity.ok(juegoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getId(@PathVariable Integer id) {
        try {
            Optional<Juego> juegoOptional = juegoService.obtenerPorId(id);

            if (juegoOptional.isEmpty()) {
                Map<String, String> error = Map.of(
                    "estado", "error",
                    "mensaje", "No se encontró el juego con ID: " + id
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            return ResponseEntity.ok(juegoOptional.get());
        } catch (Exception e) {
            Map<String, String> error = Map.of(
                "estado", "error",
                "mensaje", "Error al obtener el juego: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody Juego juego) {
        try {
            if (juego == null) {
                Map<String, String> error = Map.of(
                    "estado", "error",
                    "mensaje", "El cuerpo de la solicitud no puede estar vacío"
                );
                return ResponseEntity.badRequest().body(error);
            }

            Juego juegoGuardado = juegoService.crear(juego);

            if (juegoGuardado == null) {
                Map<String, String> error = Map.of(
                    "estado", "error",
                    "mensaje", "No se pudo crear el juego. Verifique que el género, plataforma, editora y distribuidora existan"
                );
                return ResponseEntity.badRequest().body(error);
            }

            Map<String, Object> response = Map.of(
                "estado", "creado",
                "mensaje", "Juego creado exitosamente",
                "id", juegoGuardado.getId(),
                "juego", juegoGuardado
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = Map.of(
                "estado", "error",
                "mensaje", "Error interno al guardar el juego: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Juego juego) {
        try {
            if (juego == null) {
                Map<String, String> error = Map.of(
                    "estado", "error",
                    "mensaje", "El cuerpo de la solicitud no puede estar vacío"
                );
                return ResponseEntity.badRequest().body(error);
            }

            if (!id.equals(juego.getId())) {
                Map<String, String> error = Map.of(
                    "estado", "error",
                    "mensaje", "El ID del juego en la URL no coincide con el ID en el cuerpo de la solicitud"
                );
                return ResponseEntity.badRequest().body(error);
            }

            Juego juegoActualizado = juegoService.actualizar(juego);

            if (juegoActualizado == null) {
                Map<String, String> error = Map.of(
                    "estado", "error",
                    "mensaje", "No se pudo actualizar el juego. Verifique que el juego exista y que el género, plataforma, editora y distribuidora sean válidos"
                );
                return ResponseEntity.badRequest().body(error);
            }

            Map<String, Object> response = Map.of(
                "estado", "actualizado",
                "mensaje", "Juego actualizado exitosamente",
                "id", juegoActualizado.getId(),
                "juego", juegoActualizado
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = Map.of(
                "estado", "error",
                "mensaje", "Error interno al actualizar el juego: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            Optional<Juego> juegoOptional = juegoService.obtenerPorId(id);

            if (juegoOptional.isEmpty()) {
                Map<String, String> error = Map.of(
                    "estado", "error",
                    "mensaje", "No se encontró el juego con ID: " + id
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            juegoService.eliminar(juegoOptional.get());

            Map<String, String> response = Map.of(
                "estado", "eliminado",
                "mensaje", "Juego eliminado exitosamente",
                "id", id.toString()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = Map.of(
                "estado", "error",
                "mensaje", "Error interno al eliminar el juego: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

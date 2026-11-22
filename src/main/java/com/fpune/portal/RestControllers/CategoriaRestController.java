/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fpune.portal.RestControllers;

import com.fpune.portal.entity.Categoria;
import com.fpune.portal.services.CategoriaService;
import com.fpune.portal.status.CategoriaStatusEnum;
import java.net.URI;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author marti
 */

@RestController
@RequestMapping("/categorias")
public class CategoriaRestController {
    @Autowired
    private CategoriaService categoriaService;

    /** Público: categorías activas para el formulario de solicitud. */
    @GetMapping
    public ResponseEntity<ArrayList<Categoria>> getCategoriasActivas() {
        return ResponseEntity.ok(categoriaService.listarCategoriasActivas());
    }

    /** Admin: todas, incluidas las desactivadas. */
    @GetMapping("/todas")
    public ResponseEntity<ArrayList<Categoria>> getCategorias() {
        return ResponseEntity.ok(categoriaService.listarCategorias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getCategoria(@PathVariable int id) {
        Categoria categoria = categoriaService.obtenerCategoria(id);
        return categoria == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(categoria);
    }
    
     @PostMapping
    public ResponseEntity<?> postCategoria(@RequestBody Categoria categoria) {
        int r = categoriaService.insertarCategoria(categoria);
        if (r == CategoriaStatusEnum.CATEGORIA_CREADO.getCodigo()) {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(categoria.getId())
                    .toUri();
            return ResponseEntity.created(location).body(categoria);
        } else if (r == CategoriaStatusEnum.CATEGORIA_YA_EXISTE.getCodigo()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe una categoría con ese nombre");
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("Datos de la categoría no válidos");
        }
    }

    @PutMapping
    public ResponseEntity<?> putCategoria(@RequestBody Categoria categoria) {
        int r = categoriaService.modificarCategoria(categoria);
        if (r == CategoriaStatusEnum.CATEGORIA_EDITADO.getCodigo()) {
            return ResponseEntity.ok(categoriaService.obtenerCategoria(categoria.getId()));
        } else if (r == CategoriaStatusEnum.CATEGORIA_NO_EXISTE.getCodigo()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La categoría no existe");
        } else if (r == CategoriaStatusEnum.CATEGORIA_YA_EXISTE.getCodigo()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe una categoría con ese nombre");
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("Datos de la categoría no válidos");
        }
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<?> activarCategoria(@PathVariable int id) {
        return respuestaCambioEstado(categoriaService.cambiarEstadoCategoria(id, true), id);
    }

    /** En lugar de DELETE: la categoría deja de aparecer en el formulario, las solicitudes viejas la conservan. */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivarCategoria(@PathVariable int id) {
        return respuestaCambioEstado(categoriaService.cambiarEstadoCategoria(id, false), id);
    }

    private ResponseEntity<?> respuestaCambioEstado(int r, int id) {
        if (r == CategoriaStatusEnum.CATEGORIA_EDITADO.getCodigo()) {
            return ResponseEntity.ok(categoriaService.obtenerCategoria(id));
        } else if (r == CategoriaStatusEnum.CATEGORIA_NO_EXISTE.getCodigo()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La categoría no existe");
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body("No se pudo cambiar el estado");
    }
}

package com.idgs12.programaeducativo.programaeducativo_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.idgs12.programaeducativo.programaeducativo_service.dto.ProgramaDivisionDTO;
import com.idgs12.programaeducativo.programaeducativo_service.dto.ProgramaEducativoDTO;
import com.idgs12.programaeducativo.programaeducativo_service.entity.ProgramaEducativoEntity;
import com.idgs12.programaeducativo.programaeducativo_service.services.ProgramaEducativoService;

@RestController
@RequestMapping("/programas")
@CrossOrigin(origins = "*")
public class ProgramaEducativoController {

    @Autowired
    private ProgramaEducativoService service;

    @PostMapping
    public ResponseEntity<ProgramaEducativoEntity> crear(@RequestBody ProgramaEducativoDTO dto) {
        ProgramaEducativoEntity programa = service.crearPrograma(dto);
        return ResponseEntity.ok(programa);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProgramaDivisionDTO>> findAll() {
        List<ProgramaDivisionDTO> programas = service.findAll();
        return ResponseEntity.ok(programas);
    }

    //Habilitar programa -- Maria Fernanda Rosas Briones IDGS12--
    @PutMapping("/habilitar/{id}")
    public ResponseEntity<ProgramaEducativoEntity> habilitar(@PathVariable Integer id) {
        ProgramaEducativoEntity programa = service.habilitarPrograma(id);
        return ResponseEntity.ok(programa);
    }

    // Deshabilitar Programa Educativo Cecilia Mendoza Artega
    @PutMapping("/deshabilitar/{id}")
    public ResponseEntity<ProgramaEducativoEntity> deshabilitarPrograma(@PathVariable Integer id) {
        ProgramaEducativoEntity programa = service.deshabilitarPrograma(id);
        return ResponseEntity.ok(programa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgramaEducativoEntity> editar(
            @PathVariable Integer id,
            @RequestBody ProgramaEducativoDTO dto) {
        ProgramaEducativoEntity programa = service.editarPrograma(id, dto);
        return ResponseEntity.ok(programa);
    }

}

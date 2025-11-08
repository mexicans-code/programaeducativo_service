package com.idgs12.programaeducativo.programaeducativo_service.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.idgs12.programaeducativo.programaeducativo_service.dto.*;
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
}

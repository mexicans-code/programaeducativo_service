package com.idgs12.programaeducativo.programaeducativo_service.services;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idgs12.programaeducativo.programaeducativo_service.FeignClient.DivisionFeignClient;
import com.idgs12.programaeducativo.programaeducativo_service.dto.*;
import com.idgs12.programaeducativo.programaeducativo_service.entity.*;
import com.idgs12.programaeducativo.programaeducativo_service.repository.ProgramaEducativoRepository;

@Service
public class ProgramaEducativoService {

    @Autowired
    private ProgramaEducativoRepository programaEducativoRepository;

    @Autowired
    private DivisionFeignClient divisionFeignClient;

    @Transactional
    public ProgramaEducativoEntity crearPrograma(ProgramaEducativoDTO dto) {
        DivisionDTO division = divisionFeignClient.getDivisionById(dto.getIdDivision());

        if (division == null) {
            throw new RuntimeException("La división con ID " + dto.getIdDivision() + " no existe");
        }

        ProgramaEducativoEntity programa = new ProgramaEducativoEntity();
        programa.setNombre(dto.getNombre());
        programa.setDescripcion(dto.getDescripcion());
        programa.setActivo(true);

        DivisionProgramaEntity relacion = new DivisionProgramaEntity();
        relacion.setIdDivision(dto.getIdDivision());

        programa.setDivisionProgramas(Arrays.asList(relacion));

        return programaEducativoRepository.save(programa);
    }

    @Transactional(readOnly = true)
    public List<ProgramaDivisionDTO> findAll() {
        List<ProgramaEducativoEntity> programas = programaEducativoRepository.findAll();

        List<Long> idsDivisiones = programas.stream()
                .flatMap(p -> p.getDivisionProgramas().stream())
                .map(DivisionProgramaEntity::getIdDivision)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> divisionIdNombreMap = new HashMap<>();
        List<DivisionDTO> divisiones = divisionFeignClient.obtenerDivisionesPorIds(idsDivisiones);

        if (divisiones != null) {
            for (DivisionDTO dto : divisiones) {
                if (dto != null) {
                    divisionIdNombreMap.put(dto.getId(), dto.getNombre());
                }
            }
        }

        List<ProgramaDivisionDTO> resultado = programas.stream()
                .map(programa -> {
                    ProgramaDivisionDTO dto = new ProgramaDivisionDTO();
                    dto.setId(programa.getId());
                    dto.setNombrePrograma(programa.getNombre());
                    dto.setDescripcionPrograma(programa.getDescripcion());
                    dto.setActivo(programa.isActivo());

                    if (programa.getDivisionProgramas() != null && !programa.getDivisionProgramas().isEmpty()) {
                        DivisionProgramaEntity relacion = programa.getDivisionProgramas().get(0);
                        if (relacion != null && relacion.getIdDivision() != null) {
                            String nombreDivision = divisionIdNombreMap.get(relacion.getIdDivision());
                            dto.setNombreDivision(nombreDivision != null ? nombreDivision : "Sin dato de división");
                        }
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        return resultado;
    }

}

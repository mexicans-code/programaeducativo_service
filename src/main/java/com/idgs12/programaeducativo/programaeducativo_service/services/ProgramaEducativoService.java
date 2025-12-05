package com.idgs12.programaeducativo.programaeducativo_service.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idgs12.programaeducativo.programaeducativo_service.FeignClient.DivisionFeignClient;
import com.idgs12.programaeducativo.programaeducativo_service.dto.DivisionDTO;
import com.idgs12.programaeducativo.programaeducativo_service.dto.ProgramaDivisionDTO;
import com.idgs12.programaeducativo.programaeducativo_service.dto.ProgramaEducativoDTO;
import com.idgs12.programaeducativo.programaeducativo_service.entity.DivisionProgramaEntity;
import com.idgs12.programaeducativo.programaeducativo_service.entity.ProgramaEducativoEntity;
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

    // Listar todos los programas con información de divisiones
    @Transactional(readOnly = true)
    public List<ProgramaDivisionDTO> findAll() {
        List<ProgramaEducativoEntity> programas = programaEducativoRepository.findAll();

        List<Long> idsDivisiones = programas.stream()
                .flatMap(p -> p.getDivisionProgramas().stream())
                .map(DivisionProgramaEntity::getIdDivision)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> divisionIdNombreMap = new HashMap<>();

        if (!idsDivisiones.isEmpty()) {
            List<DivisionDTO> divisiones = divisionFeignClient.obtenerDivisionesPorIds(idsDivisiones);

            if (divisiones != null) {
                for (DivisionDTO dto : divisiones) {
                    if (dto != null) {
                        divisionIdNombreMap.put(dto.getId(), dto.getNombre());
                    }
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
    
    // Deshabilitar un Programa Educativo - Cecilia Mendoza Artega 
    @Transactional
    public ProgramaEducativoEntity deshabilitarPrograma(Integer id) {
        // Buscar el programa por ID
        ProgramaEducativoEntity programa = programaEducativoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Programa no encontrado con ID: " + id));

        // Marcar como inactivo
        programa.setActivo(false);

        // Guardar los cambios en la base de datos
        return programaEducativoRepository.save(programa);
        return programas.stream().map(programa -> {
            ProgramaDivisionDTO dto = new ProgramaDivisionDTO();
            dto.setId(programa.getId());
            dto.setNombrePrograma(programa.getNombre());
            dto.setDescripcionPrograma(programa.getDescripcion());
            dto.setActivo(programa.isActivo());

            if (programa.getDivisionProgramas() != null && !programa.getDivisionProgramas().isEmpty()) {
                DivisionProgramaEntity relacion = programa.getDivisionProgramas().get(0);
                if (relacion != null && relacion.getIdDivision() != null) {
                    Long idDivision = relacion.getIdDivision();
                    dto.setIdDivision(idDivision);
                    String nombreDivision = divisionIdNombreMap.get(idDivision);
                    dto.setNombreDivision(nombreDivision != null ? nombreDivision : "Sin dato de división");
                }
            }
            return dto;
        }).collect(Collectors.toList());
    }

    // Habilitar un Programa Educativo -- Maria Fernanda Rosas Briones IDGS12--
    @Transactional
    public ProgramaEducativoEntity habilitarPrograma(Integer id) {
        ProgramaEducativoEntity programa = programaEducativoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Programa no encontrado con ID: " + id));

        programa.setActivo(true);
        return programaEducativoRepository.save(programa);
    }

    //Editar programa educativo - Pedro Javier
    // Editar un Programa Educativo
    @Transactional
    public ProgramaEducativoEntity editarPrograma(Integer id, ProgramaEducativoDTO dto) {
        ProgramaEducativoEntity programa = programaEducativoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Programa no encontrado con ID: " + id));

        if (dto.getIdDivision() != null) {
            DivisionDTO division = divisionFeignClient.getDivisionById(dto.getIdDivision());
            if (division == null) {
                throw new RuntimeException("La división con ID " + dto.getIdDivision() + " no existe");
            }

            if (!programa.getDivisionProgramas().isEmpty()) {
                DivisionProgramaEntity relacion = programa.getDivisionProgramas().get(0);
                relacion.setIdDivision(dto.getIdDivision());
            }
        }

        if (dto.getNombre() != null) {
            programa.setNombre(dto.getNombre());
        }
        if (dto.getDescripcion() != null) {
            programa.setDescripcion(dto.getDescripcion());
        }

        return programaEducativoRepository.save(programa);
    }

    // Deshabilitar un Programa Educativo

    // Obtener programa por ID
    @Transactional(readOnly = true)
    public ProgramaEducativoDTO obtenerPorId(Integer id) {
        ProgramaEducativoEntity programa = programaEducativoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Programa no encontrado con ID: " + id));

        ProgramaEducativoDTO dto = new ProgramaEducativoDTO();
        dto.setId(programa.getId());
        dto.setNombre(programa.getNombre());
        dto.setDescripcion(programa.getDescripcion());
        dto.setActivo(programa.isActivo());

        if (!programa.getDivisionProgramas().isEmpty()) {
            DivisionProgramaEntity relacion = programa.getDivisionProgramas().get(0);
            dto.setIdDivision(relacion.getIdDivision());
        }

        return dto;
    }

    // Obtener programas por lista de IDs (para el FeignClient POST /by-ids)
    @Transactional(readOnly = true)
    public List<ProgramaEducativoDTO> obtenerProgramasPorIds(List<Integer> ids) {
        List<ProgramaEducativoEntity> programas = programaEducativoRepository.findAllById(ids);
        return programas.stream().map(programa -> {
            ProgramaEducativoDTO dto = new ProgramaEducativoDTO();
            dto.setId(programa.getId());
            dto.setNombre(programa.getNombre());
            dto.setDescripcion(programa.getDescripcion());
            dto.setActivo(programa.isActivo());

            if (!programa.getDivisionProgramas().isEmpty()) {
                DivisionProgramaEntity relacion = programa.getDivisionProgramas().get(0);
                dto.setIdDivision(relacion.getIdDivision());
            }
            return dto;
        }).collect(Collectors.toList());
    }
}

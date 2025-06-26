package com.biopark.cepex_system.domain.monitoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitoriaUpdateDTO {
    
    @NotBlank(message = "O título da monitoria não pode estar em branco.")
    private String title;
    
    private String description;
    
    @NotNull(message = "O campo remoto não pode ser nulo.")
    private Boolean remote;
    
    private String location;
    
    @NotNull(message = "O número de vagas não pode ser nulo.")
    private Integer vacancies;
    
    @NotNull(message = "A carga horária não pode ser nula.")
    private Integer workload;
    
    @NotNull
    private LocalDate inicialDate;
    
    @NotNull
    private LocalDate finalDate;
    
    @NotNull
    private LocalDate inicialIngressDate;
    
    @NotNull(message = "A data final de ingresso não pode ser nula.")
    private LocalDate finalIngressDate;
    
    @NotNull(message = "O tipo de seleção não pode ser nulo.")
    private SelectionType selectionType;
    
    private LocalDate selectionDate;
    
    private String selectionTime;
    
    private LocalDate divulgationDate;
    
    @NotNull(message = "O status da monitoria não pode ser nulo.")
    private StatusMonitoria statusMonitoria;
    
    @NotNull(message = "A monitoria deve estar associada a um curso.")
    private UUID courseId;
    
    @NotNull(message = "A monitoria deve estar associada a uma disciplina.")
    private UUID subjectId;
    
    @NotNull(message = "A monitoria deve estar associada a um professor.")
    private UUID professorId;
} 
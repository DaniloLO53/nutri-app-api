package org.nutri.app.nutri_app_api.models.clinicalInformations.symptoms;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.nutri.app.nutri_app_api.models.clinicalInformations.ClinicalInformation;

@Entity
@Table(name = "information_symptoms")
@Getter
@Setter
public class InformationSymptom {

    @EmbeddedId // 1. Usa a classe da chave composta como ID
    private InformationSymptomId id;

    // 2. Mapeia a parte 'informationId' da chave para a entidade ClinicalInformation
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("informationId")
    @JoinColumn(name = "information_id")
    private ClinicalInformation clinicalInformation;

    // 3. Mapeia a parte 'symptomId' da chave para a entidade Symptom
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("symptomId")
    @JoinColumn(name = "symptom_id")
    private Symptom symptom;

    // 4. Mapeia as colunas extras da tabela de ligação
    @Column(name = "intensity")
    private Integer intensity;

    @Column(name = "frequency", length = 100)
    private String frequency;

    @Column(name = "duration", length = 100)
    private String duration;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
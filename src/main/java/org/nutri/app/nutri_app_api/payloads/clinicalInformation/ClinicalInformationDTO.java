package org.nutri.app.nutri_app_api.payloads.clinicalInformation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClinicalInformationDTO {
    private UUID id;
    private UUID patientId;
    private LocalDate assessmentDate;

    // Categoria: Anamnese Clínica e Objetivos
    private String mainGoal;
    private String previousDietHistory;

    // Categoria: Sinais, Sintomas e Saúde Geral
    private String intestinalFunction;
    private String sleepQuality;
    private Integer energyLevel;
    private String menstrualCycleDetails;

    // Categoria: Avaliação Antropométrica
    private BigDecimal weightKg;
    private BigDecimal heightCm;
    private BigDecimal waistCircumferenceCm;
    private BigDecimal upperArmCircumferenceCm;
    private BigDecimal abdomenCircumferenceCm;
    private BigDecimal hipCircumferenceCm;

    // Categoria: Composição Corporal Detalhada
    private BigDecimal bodyFatPercentage;
    private BigDecimal muscleMassKg;

    // Categoria: Marcadores de Saúde Adicionais
    private String bloodPressure;
    private String skinHairNailsHealth;
    private Integer libidoLevel;

    // Categoria: Saúde Comportamental e Emocional
    private String emotionalEatingDetails;
    private String mainFoodDifficulties;

    // Categoria: Detalhes de Hábitos
    private String chewingDetails;
    private Integer weeklyEatingOutFrequency;
    private String waterIntakePerception;

    // Categoria: Hábitos Alimentares (Avaliação Dietética)
    private String foodRecall24h;
    private String dailyHydrationDetails;
    private String alcoholConsumption;
    private String mealTimesAndLocations;
    private String sugarAndSweetenerUse;

    // Categoria: Rotina e Estilo de Vida
    private String professionAndWorkRoutine;
    private String physicalActivityDetails;
    private String smokingHabits;
    private String weekendRoutineChanges;
    private String whoPreparesMeals;

    // Categoria: Dados Complementares
    private String recentLabResults;

    // Categoria: Dados Normalizados (Relações Muitos-para-Muitos)
    private List<SymptomDTO> symptoms;
    private List<DiagnosedDiseaseDTO> diagnosedDiseases;
    private List<FamilyDiseaseDTO> familyDiseases;
    private List<MedicationDTO> medications;
    private List<AllergenDTO> allergens;
    private List<FoodPreferenceDTO> foodPreferencesAndAversions;

    // Categoria: Campos Customizados
    private Map<String, Object> customData;
}
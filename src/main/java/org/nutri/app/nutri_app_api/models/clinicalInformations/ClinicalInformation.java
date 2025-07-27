package org.nutri.app.nutri_app_api.models.clinicalInformations;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.nutri.app.nutri_app_api.models.clinicalInformations.allergens.InformationAllergen;
import org.nutri.app.nutri_app_api.models.clinicalInformations.diseases.InformationDiagnosedDisease;
import org.nutri.app.nutri_app_api.models.clinicalInformations.diseases.InformationFamilyDisease;
import org.nutri.app.nutri_app_api.models.clinicalInformations.foods.InformationFood;
import org.nutri.app.nutri_app_api.models.clinicalInformations.medications.InformationMedication;
import org.nutri.app.nutri_app_api.models.clinicalInformations.symptoms.InformationSymptom;
import org.nutri.app.nutri_app_api.security.models.users.Patient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "clinical_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ClinicalInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "assessment_date", nullable = false)
    private LocalDate assessmentDate;

    // --- Categoria: Anamnese Clínica e Objetivos ---
    @Column(columnDefinition = "TEXT")
    private String mainGoal;
    @Column(columnDefinition = "TEXT")
    private String previousDietHistory;

    // --- Categoria: Sinais, Sintomas e Saúde Geral ---
    @Column(columnDefinition = "TEXT")
    private String intestinalFunction;
    @Column(columnDefinition = "TEXT")
    private String sleepQuality;
    private Integer energyLevel;
    @Column(columnDefinition = "TEXT")
    private String menstrualCycleDetails;

    // --- Categoria: Avaliação Antropométrica ---
    @Column(name = "weight_kg")
    private BigDecimal weightKg;
    @Column(name = "height_cm")
    private BigDecimal heightCm;
    @Column(name = "waist_circumference_cm")
    private BigDecimal waistCircumferenceCm;
    @Column(name = "upper_arm_circumference_cm")
    private BigDecimal upperArmCircumferenceCm;
    @Column(name = "abdomen_circumference_cm")
    private BigDecimal abdomenCircumferenceCm;
    @Column(name = "hip_circumference_cm")
    private BigDecimal hipCircumferenceCm;

    // --- Categoria: Composição Corporal Detalhada ---
    @Column(name = "body_fat_percentage")
    private BigDecimal bodyFatPercentage;
    @Column(name = "muscle_mass_kg")
    private BigDecimal muscleMassKg;

    // --- Categoria: Marcadores de Saúde Adicionais ---
    @Column(name = "blood_pressure")
    private String bloodPressure;
    @Column(name = "skin_hair_nails_health", columnDefinition = "TEXT")
    private String skinHairNailsHealth;
    private Integer libidoLevel;

    // --- Categoria: Saúde Comportamental e Emocional ---
    @Column(name = "emotional_eating_details", columnDefinition = "TEXT")
    private String emotionalEatingDetails;
    @Column(name = "main_food_difficulties", columnDefinition = "TEXT")
    private String mainFoodDifficulties;

    // --- Categoria: Detalhes de Hábitos ---
    @Column(name = "chewing_details", columnDefinition = "TEXT")
    private String chewingDetails;
    private Integer weeklyEatingOutFrequency;
    private String waterIntakePerception;

    // --- Categoria: Hábitos Alimentares (Avaliação Dietética) ---
    @Column(name = "food_recall_24h", columnDefinition = "TEXT")
    private String foodRecall24h;
    @Column(name = "daily_hydration_details", columnDefinition = "TEXT")
    private String dailyHydrationDetails;
    @Column(name = "alcohol_consumption", columnDefinition = "TEXT")
    private String alcoholConsumption;
    @Column(name = "meal_times_and_locations", columnDefinition = "TEXT")
    private String mealTimesAndLocations;
    @Column(name = "sugar_and_sweetener_use", columnDefinition = "TEXT")
    private String sugarAndSweetenerUse;

    // --- Categoria: Rotina e Estilo de Vida ---
    @Column(name = "profession_and_work_routine", columnDefinition = "TEXT")
    private String professionAndWorkRoutine;
    @Column(name = "physical_activity_details", columnDefinition = "TEXT")
    private String physicalActivityDetails;
    @Column(name = "smoking_habits", columnDefinition = "TEXT")
    private String smokingHabits;
    @Column(name = "weekend_routine_changes", columnDefinition = "TEXT")
    private String weekendRoutineChanges;
    @Column(name = "who_prepares_meals", columnDefinition = "TEXT")
    private String whoPreparesMeals;

    // --- Categoria: Dados Complementares ---
    @Column(name = "recent_lab_results", columnDefinition = "TEXT")
    private String recentLabResults;

    // --- Categoria: Campos Customizados ---
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_data", columnDefinition = "jsonb")
    private Map<String, Object> customData = new HashMap<>();

    // --- Relações Muitos-para-Muitos ---
    @OneToMany(mappedBy = "clinicalInformation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InformationSymptom> symptoms = new HashSet<>();

    @OneToMany(mappedBy = "clinicalInformation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InformationDiagnosedDisease> diagnosedDiseases = new HashSet<>();

    @OneToMany(mappedBy = "clinicalInformation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InformationFamilyDisease> familyDiseases = new HashSet<>();

    @OneToMany(mappedBy = "clinicalInformation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InformationMedication> medications = new HashSet<>();

    @OneToMany(mappedBy = "clinicalInformation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InformationAllergen> allergens = new HashSet<>();

    @OneToMany(mappedBy = "clinicalInformation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InformationFood> foodPreferencesAndAversions = new HashSet<>();
}

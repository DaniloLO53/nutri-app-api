package org.nutri.app.nutri_app_api.services.clinicalInformationService;

import org.modelmapper.ModelMapper;
import org.nutri.app.nutri_app_api.exceptions.ResourceNotFoundException;
import org.nutri.app.nutri_app_api.models.clinicalInformations.ClinicalInformation;
import org.nutri.app.nutri_app_api.models.clinicalInformations.allergens.Allergen;
import org.nutri.app.nutri_app_api.models.clinicalInformations.allergens.AllergenType;
import org.nutri.app.nutri_app_api.models.clinicalInformations.allergens.InformationAllergen;
import org.nutri.app.nutri_app_api.models.clinicalInformations.allergens.InformationAllergenId;
import org.nutri.app.nutri_app_api.models.clinicalInformations.diseases.*;
import org.nutri.app.nutri_app_api.models.clinicalInformations.foods.Food;
import org.nutri.app.nutri_app_api.models.clinicalInformations.foods.FoodPreferenceType;
import org.nutri.app.nutri_app_api.models.clinicalInformations.foods.InformationFood;
import org.nutri.app.nutri_app_api.models.clinicalInformations.foods.InformationFoodId;
import org.nutri.app.nutri_app_api.models.clinicalInformations.medications.InformationMedication;
import org.nutri.app.nutri_app_api.models.clinicalInformations.medications.InformationMedicationId;
import org.nutri.app.nutri_app_api.models.clinicalInformations.medications.MedicationSupplement;
import org.nutri.app.nutri_app_api.models.clinicalInformations.medications.MedicationType;
import org.nutri.app.nutri_app_api.models.clinicalInformations.symptoms.InformationSymptom;
import org.nutri.app.nutri_app_api.models.clinicalInformations.symptoms.InformationSymptomId;
import org.nutri.app.nutri_app_api.models.clinicalInformations.symptoms.Symptom;
import org.nutri.app.nutri_app_api.payloads.clinicalInformation.*;
import org.nutri.app.nutri_app_api.payloads.clinicalInformation.masterData.*;
import org.nutri.app.nutri_app_api.repositories.clinicalInformationRepository.*;
import org.nutri.app.nutri_app_api.repositories.patientRepository.PatientRepository;
import org.nutri.app.nutri_app_api.security.models.users.Patient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClinicalInformationServiceImpl implements ClinicalInformationService {
    private final ClinicalInformationRepository clinicalInformationRepository;
    private final PatientRepository patientRepository;
    private final FoodRepository foodRepository;
    private final AllergenRepository allergenRepository;
    private final SymptomRepository symptomRepository;
    private final MedicationRepository medicationRepository;
    private final DiseaseRepository diseaseRepository;
    private final ModelMapper modelMapper;

    public ClinicalInformationServiceImpl(
            ClinicalInformationRepository clinicalInformationRepository,
            PatientRepository patientRepository,
            FoodRepository foodRepository,
            AllergenRepository allergenRepository,
            SymptomRepository symptomRepository,
            MedicationRepository medicationRepository,
            DiseaseRepository diseaseRepository,
            ModelMapper modelMapper
    ) {
        this.clinicalInformationRepository = clinicalInformationRepository;
        this.patientRepository = patientRepository;
        this.foodRepository = foodRepository;
        this.allergenRepository = allergenRepository;
        this.symptomRepository = symptomRepository;
        this.medicationRepository = medicationRepository;
        this.diseaseRepository = diseaseRepository;
        this.modelMapper = modelMapper;
    }

    // TODO: mudar esse codigo absurdo
    @Override
    public void createClinicalInformation(UUID id, UUID patientId, ClinicalInformationDTO dto) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId.toString()));

        // 2. Crie a instância principal e mapeie os campos simples (flat properties)
        ClinicalInformation clinicalInformation = new ClinicalInformation();
        clinicalInformation.setPatient(patient);
        clinicalInformation.setAssessmentDate(dto.getAssessmentDate());
        clinicalInformation.setMainGoal(dto.getMainGoal());
        clinicalInformation.setPreviousDietHistory(dto.getPreviousDietHistory());
        clinicalInformation.setIntestinalFunction(dto.getIntestinalFunction());
        clinicalInformation.setSleepQuality(dto.getSleepQuality());
        clinicalInformation.setEnergyLevel(dto.getEnergyLevel());
        clinicalInformation.setMenstrualCycleDetails(dto.getMenstrualCycleDetails());
        clinicalInformation.setWeightKg(dto.getWeightKg());
        clinicalInformation.setHeightCm(dto.getHeightCm());
        clinicalInformation.setWaistCircumferenceCm(dto.getWaistCircumferenceCm());
        clinicalInformation.setUpperArmCircumferenceCm(dto.getUpperArmCircumferenceCm());
        clinicalInformation.setAbdomenCircumferenceCm(dto.getAbdomenCircumferenceCm());
        clinicalInformation.setHipCircumferenceCm(dto.getHipCircumferenceCm());
        clinicalInformation.setBodyFatPercentage(dto.getBodyFatPercentage());
        clinicalInformation.setMuscleMassKg(dto.getMuscleMassKg());
        clinicalInformation.setBloodPressure(dto.getBloodPressure());
        clinicalInformation.setSkinHairNailsHealth(dto.getSkinHairNailsHealth());
        clinicalInformation.setLibidoLevel(dto.getLibidoLevel());
        clinicalInformation.setEmotionalEatingDetails(dto.getEmotionalEatingDetails());
        clinicalInformation.setMainFoodDifficulties(dto.getMainFoodDifficulties());
        clinicalInformation.setChewingDetails(dto.getChewingDetails());
        clinicalInformation.setWeeklyEatingOutFrequency(dto.getWeeklyEatingOutFrequency());
        clinicalInformation.setWaterIntakePerception(dto.getWaterIntakePerception());
        clinicalInformation.setFoodRecall24h(dto.getFoodRecall24h());
        clinicalInformation.setDailyHydrationDetails(dto.getDailyHydrationDetails());
        clinicalInformation.setAlcoholConsumption(dto.getAlcoholConsumption());
        clinicalInformation.setMealTimesAndLocations(dto.getMealTimesAndLocations());
        clinicalInformation.setSugarAndSweetenerUse(dto.getSugarAndSweetenerUse());
        clinicalInformation.setProfessionAndWorkRoutine(dto.getProfessionAndWorkRoutine());
        clinicalInformation.setPhysicalActivityDetails(dto.getPhysicalActivityDetails());
        clinicalInformation.setSmokingHabits(dto.getSmokingHabits());
        clinicalInformation.setWeekendRoutineChanges(dto.getWeekendRoutineChanges());
        clinicalInformation.setWhoPreparesMeals(dto.getWhoPreparesMeals());
        clinicalInformation.setRecentLabResults(dto.getRecentLabResults());
        clinicalInformation.setCustomData(dto.getCustomData());


        // 3. Processe as listas de relações usando os métodos auxiliares

        // --- Processa Sintomas ---
        if (dto.getSymptoms() != null) {
            dto.getSymptoms().forEach(symptomDto -> {
                Symptom symptom = symptomRepository.findById(symptomDto.getSymptomId())
                        .orElseThrow(() -> new ResourceNotFoundException("Symptom", "id", symptomDto.getSymptomId().toString()));
                InformationSymptom link = new InformationSymptom();
                link.setId(new InformationSymptomId());
                link.setSymptom(symptom);
                link.setIntensity(symptomDto.getIntensity());
                link.setFrequency(symptomDto.getFrequency());
                link.setDuration(symptomDto.getDuration());
                link.setNotes(symptomDto.getNotes());
                clinicalInformation.addSymptom(link);
            });
        }

        // --- Processa Doenças Diagnosticadas ---
        if (dto.getDiagnosedDiseases() != null) {
            dto.getDiagnosedDiseases().forEach(diseaseDto -> {
                Disease disease = diseaseRepository.findById(diseaseDto.getDiseaseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Disease", "id", diseaseDto.getDiseaseId().toString()));
                InformationDiagnosedDisease link = new InformationDiagnosedDisease();
                link.setId(new InformationDiagnosedDiseaseId());
                link.setDisease(disease);
                link.setNotes(diseaseDto.getNotes());
                clinicalInformation.addDiagnosedDisease(link);
            });
        }

        // --- Processa Histórico Familiar de Doenças ---
        if (dto.getFamilyDiseases() != null) {
            dto.getFamilyDiseases().forEach(diseaseDto -> {
                Disease disease = diseaseRepository.findById(diseaseDto.getDiseaseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Disease", "id", diseaseDto.getDiseaseId().toString()));
                InformationFamilyDisease link = new InformationFamilyDisease();
                link.setId(new InformationFamilyDiseaseId());
                link.setDisease(disease);
                link.getId().setFamilyMember(diseaseDto.getFamilyMember());
                clinicalInformation.addFamilyDisease(link);
            });
        }

        // --- Processa Medicamentos e Suplementos ---
        if (dto.getMedications() != null) {
            dto.getMedications().forEach(medDto -> {
                MedicationSupplement med = medicationRepository.findById(medDto.getMedicationId())
                        .orElseThrow(() -> new ResourceNotFoundException("Medication", "id", medDto.getMedicationId().toString()));
                InformationMedication link = new InformationMedication();
                link.setId(new InformationMedicationId());
                link.setMedicationSupplement(med);
                link.setDosage(medDto.getDosage());
                link.setNotes(medDto.getNotes());
                clinicalInformation.addMedication(link);
            });
        }

        // --- Processa Alergias e Intolerâncias ---
        if (dto.getAllergens() != null) {
            dto.getAllergens().forEach(allergenDto -> {
                Allergen allergen = allergenRepository.findById(allergenDto.getAllergenId())
                        .orElseThrow(() -> new ResourceNotFoundException("Allergen", "id", allergenDto.getAllergenId().toString()));
                InformationAllergen link = new InformationAllergen();
                link.setId(new InformationAllergenId());
                link.setAllergen(allergen);
                link.setReactionDetails(allergenDto.getReactionDetails());
                clinicalInformation.addAllergen(link);
            });
        }

        // --- Processa Preferências e Aversões Alimentares ---
        if (dto.getFoodPreferencesAndAversions() != null) {
            dto.getFoodPreferencesAndAversions().forEach(foodDto -> {
                Food food = foodRepository.findById(foodDto.getFoodId())
                        .orElseThrow(() -> new ResourceNotFoundException("Food", "id", foodDto.getFoodId().toString()));
                InformationFood link = new InformationFood();
                link.setId(new InformationFoodId());
                link.setFood(food);
                if (foodDto.getType() != null) {
                    link.setType(modelMapper.map(foodDto.getType(), FoodPreferenceType.class));
                }
                clinicalInformation.addFoodPreferencesAndAversions(link);
            });
        }

        clinicalInformation.setAssessmentDate(LocalDate.now());

        // 4. Salve a entidade pai. O Cascade cuidará de salvar as entidades de ligação corretamente.
        clinicalInformationRepository.save(clinicalInformation);
    }

    @Override
    public ClinicalInformationMasterDataDTO getClinicalInformationMasterData() {
        List<MasterDataProjection> projections = clinicalInformationRepository.findClinicalInformationMasterData();
        return buildMasterDataFromProjection(projections);
    }

    @Override
    public ClinicalInformationDTO getClinicalInformation(UUID userId, UUID patientId) {
        System.out.println("Patient id: " + patientId);
        ClinicalInformation entity = clinicalInformationRepository.findMostRecentByPatientAndNutritionistNative(patientId, userId);

        if (entity == null) return null;

        ClinicalInformationDTO dto = new ClinicalInformationDTO();

        // 1. Mapear os campos simples (propriedades "planas")
        dto.setId(entity.getId());
        dto.setPatientId(entity.getPatient().getId());
        dto.setAssessmentDate(entity.getAssessmentDate());
        dto.setMainGoal(entity.getMainGoal());
        dto.setPreviousDietHistory(entity.getPreviousDietHistory());
        dto.setIntestinalFunction(entity.getIntestinalFunction());
        dto.setSleepQuality(entity.getSleepQuality());
        dto.setEnergyLevel(entity.getEnergyLevel());
        dto.setMenstrualCycleDetails(entity.getMenstrualCycleDetails());
        dto.setWeightKg(entity.getWeightKg());
        dto.setHeightCm(entity.getHeightCm());
        dto.setWaistCircumferenceCm(entity.getWaistCircumferenceCm());
        dto.setUpperArmCircumferenceCm(entity.getUpperArmCircumferenceCm());
        dto.setAbdomenCircumferenceCm(entity.getAbdomenCircumferenceCm());
        dto.setHipCircumferenceCm(entity.getHipCircumferenceCm());
        dto.setBodyFatPercentage(entity.getBodyFatPercentage());
        dto.setMuscleMassKg(entity.getMuscleMassKg());
        dto.setBloodPressure(entity.getBloodPressure());
        dto.setSkinHairNailsHealth(entity.getSkinHairNailsHealth());
        dto.setLibidoLevel(entity.getLibidoLevel());
        dto.setEmotionalEatingDetails(entity.getEmotionalEatingDetails());
        dto.setMainFoodDifficulties(entity.getMainFoodDifficulties());
        dto.setChewingDetails(entity.getChewingDetails());
        dto.setWeeklyEatingOutFrequency(entity.getWeeklyEatingOutFrequency());
        dto.setWaterIntakePerception(entity.getWaterIntakePerception());
        dto.setFoodRecall24h(entity.getFoodRecall24h());
        dto.setDailyHydrationDetails(entity.getDailyHydrationDetails());
        dto.setAlcoholConsumption(entity.getAlcoholConsumption());
        dto.setMealTimesAndLocations(entity.getMealTimesAndLocations());
        dto.setSugarAndSweetenerUse(entity.getSugarAndSweetenerUse());
        dto.setProfessionAndWorkRoutine(entity.getProfessionAndWorkRoutine());
        dto.setPhysicalActivityDetails(entity.getPhysicalActivityDetails());
        dto.setSmokingHabits(entity.getSmokingHabits());
        dto.setWeekendRoutineChanges(entity.getWeekendRoutineChanges());
        dto.setWhoPreparesMeals(entity.getWhoPreparesMeals());
        dto.setRecentLabResults(entity.getRecentLabResults());
        dto.setCustomData(entity.getCustomData());

        // 2. Mapear as listas de relações (coleções)
        System.out.println("********* ENTITY ID: " + entity.getId());
        System.out.println("********* SYMP: " + entity.getSymptoms());
        System.out.println("********* MED: " + entity.getMedications());
        if (entity.getSymptoms() != null) {
            List<SymptomDTO> symptomDTOs = entity.getSymptoms().stream().map(link ->
                    new SymptomDTO(
                            link.getSymptom().getId(),
                            link.getSymptom().getName(),
                            link.getIntensity(),
                            link.getFrequency(),
                            link.getDuration(),
                            link.getNotes()
                    )).collect(Collectors.toList());
            dto.setSymptoms(symptomDTOs);
        }

        if (entity.getDiagnosedDiseases() != null) {
            List<DiagnosedDiseaseDTO> diseaseDTOs = entity.getDiagnosedDiseases().stream().map(link ->
                    new DiagnosedDiseaseDTO(
                            link.getDisease().getId(),
                            link.getDisease().getName(),
                            link.getNotes()
                    )).collect(Collectors.toList());
            dto.setDiagnosedDiseases(diseaseDTOs);
        }

        if (entity.getFamilyDiseases() != null) {
            List<FamilyDiseaseDTO> familyDiseaseDTOs = entity.getFamilyDiseases().stream().map(link ->
                    new FamilyDiseaseDTO(
                            link.getDisease().getId(),
                            link.getDisease().getName(),
                            link.getId().getFamilyMember()
                    )).collect(Collectors.toList());
            dto.setFamilyDiseases(familyDiseaseDTOs);
        }

        if (entity.getMedications() != null) {
            List<MedicationDTO> medicationDTOs = entity.getMedications().stream().map(link ->
                    new MedicationDTO(
                            link.getMedicationSupplement().getId(),
                            link.getMedicationSupplement().getName(),
                            link.getDosage(),
                            link.getNotes()
                    )).collect(Collectors.toList());
            dto.setMedications(medicationDTOs);
        }

        if (entity.getAllergens() != null) {
            List<AllergenDTO> allergenDTOs = entity.getAllergens().stream().map(link ->
                    new AllergenDTO(
                            link.getAllergen().getId(),
                            link.getAllergen().getName(),
                            link.getReactionDetails()
                    )).collect(Collectors.toList());
            dto.setAllergens(allergenDTOs);
        }

        if (entity.getFoodPreferencesAndAversions() != null) {
            List<FoodPreferenceDTO> foodDTOs = entity.getFoodPreferencesAndAversions().stream().map(link ->
                    new FoodPreferenceDTO(
                            link.getFood().getId(),
                            link.getFood().getName(),
                            org.nutri.app.nutri_app_api.payloads.clinicalInformation.FoodPreferenceType.valueOf(link.getType().name())
                    )).collect(Collectors.toList());
            dto.setFoodPreferencesAndAversions(foodDTOs);
        }

        return dto;
    }

    private ClinicalInformationMasterDataDTO buildMasterDataFromProjection(List<MasterDataProjection> projections) {
        // Inicializa as listas que irão compor o resultado final
        List<MasterSymptomDTO> symptomList = new ArrayList<>();
        List<MasterAllergenDTO> allergenList = new ArrayList<>();
        List<MasterDiseaseDTO> diseaseList = new ArrayList<>();
        List<MasterMedicationDTO> medicationList = new ArrayList<>();
        List<MasterFoodDTO> foodList = new ArrayList<>();

        // Itera sobre cada item da projeção e o coloca na lista correta
        for (MasterDataProjection proj : projections) {

            switch (ClinicalInformationMasterDataType.valueOf(proj.getSource())) {
                case ClinicalInformationMasterDataType.SYMPTOM:
                    symptomList.add(new MasterSymptomDTO(proj.getId(), proj.getName()));
                    break;
                case ClinicalInformationMasterDataType.DISEASE:
                    diseaseList.add(new MasterDiseaseDTO(proj.getId(), proj.getName()));
                    break;
                case ClinicalInformationMasterDataType.FOOD:
                    foodList.add(new MasterFoodDTO(proj.getId(), proj.getName()));
                    break;
                case ClinicalInformationMasterDataType.ALLERGEN:
                    AllergenType allergenType = AllergenType.valueOf(proj.getType());
                    allergenList.add(new MasterAllergenDTO(proj.getId(), proj.getName(), allergenType));
                    break;
                case ClinicalInformationMasterDataType.MEDICATION_SUPPLEMENT:
                    MedicationType medType = MedicationType.valueOf(proj.getType());
                    medicationList.add(new MasterMedicationDTO(proj.getId(), proj.getName(), medType));
                    break;
                default:
                    // Opcional: Logar um aviso se uma fonte desconhecida aparecer
                    break;
            }
        }

        // Constrói o DTO final com todas as listas populadas
        return new ClinicalInformationMasterDataDTO(
                symptomList,
                allergenList,
                diseaseList,
                medicationList,
                foodList
        );
    }
}

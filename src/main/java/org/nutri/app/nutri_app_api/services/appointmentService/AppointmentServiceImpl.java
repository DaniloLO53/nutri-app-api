package org.nutri.app.nutri_app_api.services.appointmentService;

import org.modelmapper.ModelMapper;
import org.nutri.app.nutri_app_api.exceptions.ConflictException;
import org.nutri.app.nutri_app_api.exceptions.ForbiddenException;
import org.nutri.app.nutri_app_api.exceptions.ResourceNotFoundException;
import org.nutri.app.nutri_app_api.models.appointments.Appointment;
import org.nutri.app.nutri_app_api.models.appointments.AppointmentStatus;
import org.nutri.app.nutri_app_api.models.appointments.AppointmentStatusName;
import org.nutri.app.nutri_app_api.models.schedules.Schedule;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.*;
import org.nutri.app.nutri_app_api.payloads.patientDTOs.PatientSearchByNameDTO;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.AppointmentOrSchedule;
import org.nutri.app.nutri_app_api.repositories.AppointmentStatusRepository;
import org.nutri.app.nutri_app_api.repositories.appointmentRepository.AppointmentPatientProjection;
import org.nutri.app.nutri_app_api.repositories.appointmentRepository.AppointmentNutritionistProjection;
import org.nutri.app.nutri_app_api.repositories.appointmentRepository.AppointmentRepository;
import org.nutri.app.nutri_app_api.repositories.scheduleRepository.ScheduleRepository;
import org.nutri.app.nutri_app_api.repositories.patientRepository.PatientRepository;
import org.nutri.app.nutri_app_api.repositories.nutritionistRepository.ProfileRepository;
import org.nutri.app.nutri_app_api.security.models.users.Patient;
import org.nutri.app.nutri_app_api.security.models.users.Nutritionist;
import org.nutri.app.nutri_app_api.security.models.users.RoleName;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final static String DEFAULT_STATUS_NAME = AppointmentStatusName.AGENDADO.name();

    private final ModelMapper modelMapper;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentStatusRepository appointmentStatusRepository;
    private final ProfileRepository nutritionistRepository;
    private final PatientRepository patientRepository;
    private final ScheduleRepository nutritionistScheduleRepository;
    private final ScheduleRepository scheduleRepository;

    public AppointmentServiceImpl(
            ModelMapper modelMapper,
            AppointmentRepository appointmentRepository,
            AppointmentStatusRepository appointmentStatusRepository,
            ProfileRepository nutritionistRepository,
            PatientRepository patientRepository,
            ScheduleRepository nutritionistScheduleRepository, ScheduleRepository scheduleRepository) {
        this.modelMapper = modelMapper;
        this.appointmentRepository = appointmentRepository;
        this.appointmentStatusRepository = appointmentStatusRepository;
        this.nutritionistRepository = nutritionistRepository;
        this.patientRepository = patientRepository;
        this.nutritionistScheduleRepository = nutritionistScheduleRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    @Transactional
    public ResponseToCreateAppointment deleteAppointment(UUID userId, UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId.toString()));

        // Verifique se o usuário que está pedindo a exclusão é o paciente ou o farmacêutico da consulta
        UUID patientUserId = appointment.getPatient().getUser().getId();
        UUID nutritionistUserId = appointment.getSchedule().getNutritionist().getUser().getId();
        if (!userId.equals(patientUserId) && !userId.equals(nutritionistUserId)) {
            throw new ForbiddenException("You are not authorized to delete this appointment.");
        }

        Schedule schedule = appointment.getSchedule();

        if (schedule != null) {
            schedule.setAppointment(null);
            // Não é estritamente necessário salvar aqui, o 'dirty checking' do @Transactional faria isso,
            // mas ser explícito ajuda na clareza.
            scheduleRepository.save(schedule);
        }

        appointmentRepository.delete(appointment);

        ResponseToCreateAppointment responseToCreateAppointment = new ResponseToCreateAppointment();

        responseToCreateAppointment.setId(appointment.getId());
        responseToCreateAppointment.setType(AppointmentOrSchedule.APPOINTMENT);
        responseToCreateAppointment.setStatus(AppointmentStatusName.valueOf(appointment.getAppointmentStatus().getName()));
        responseToCreateAppointment.setDurationMinutes(appointment.getSchedule().getDurationMinutes());
        responseToCreateAppointment.setStartTime(appointment.getSchedule().getStartTime());

        PatientSearchByNameDTO patientSearchByNameDTO = new PatientSearchByNameDTO();
        patientSearchByNameDTO.setId(appointment.getId());
        patientSearchByNameDTO.setName(appointment.getPatient().getUser().getFirstName() + " " + appointment.getPatient().getUser().getLastName());
        patientSearchByNameDTO.setEmail(appointment.getPatient().getUser().getEmail());

        responseToCreateAppointment.setPatient(patientSearchByNameDTO);

        return responseToCreateAppointment;
    }

    @Override
    @Transactional
    public ResponseToCreateAppointment createAppointment(UserDetailsImpl userDetails, CreateAppointmentDTO createAppointmentDTO) {
        UUID userId = userDetails.getId();
        String userRole = userDetails.getAuthorities().iterator().next().getAuthority();

        UUID patientId = createAppointmentDTO.getPatientId();
        UUID nutritionistScheduleId = createAppointmentDTO.getScheduleId();
        Boolean isRemote = createAppointmentDTO.getIsRemote();

        Schedule schedule = nutritionistScheduleRepository.findFirstById(nutritionistScheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade", "id", nutritionistScheduleId.toString()));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", patientId.toString()));

        Nutritionist nutritionist = nutritionistRepository
                .findById(schedule.getNutritionist().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Farmacêutico", "id", schedule.getNutritionist().getId().toString()));

        if (userRole.equals(RoleName.ROLE_PATIENT.name()) && !patient.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Pacientes só podem criar consultas para si mesmos");
        }

        if (userRole.equals(RoleName.ROLE_NUTRITIONIST.name()) && !nutritionist.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Nutricionistas só podem criar consultas para si mesmos");
        }

        AppointmentStatus appointmentStatus = appointmentStatusRepository.findFirstByName(DEFAULT_STATUS_NAME)
                .orElseThrow(() -> new ResourceNotFoundException("Status", "nome", DEFAULT_STATUS_NAME));

        boolean patientHasAppointment = appointmentRepository.patientAlreadyHasSchedule(patient.getId(),AppointmentStatusName.AGENDADO.name(), AppointmentStatusName.CONFIRMADO.name(), schedule.getStartTime());
        if (patientHasAppointment) {
            throw new ConflictException("Esse paciente já possui consulta agendada");
        }

        // Verifica se a vaga escolhida já está associada a outra consulta.
        if (schedule.getAppointment() != null) {
            throw new ConflictException("Esse horário já está agendado");
        }

        Appointment appointment = new Appointment();

        appointment.setAppointmentStatus(appointmentStatus);
        appointment.setPatient(patient);
        appointment.setIsRemote(isRemote);

        appointment.setSchedule(schedule);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        NutritionistFutureAppointmentDTO nutritionistFutureAppointmentDTO = new NutritionistFutureAppointmentDTO();
        nutritionistFutureAppointmentDTO.setId(savedAppointment.getId().toString());
        nutritionistFutureAppointmentDTO.setStartTime(savedAppointment.getSchedule().getStartTime());
        nutritionistFutureAppointmentDTO.setDurationMinutes(savedAppointment.getSchedule().getDurationMinutes());
        nutritionistFutureAppointmentDTO.setType(EventType.APPOINTMENT);

        NutritionistFutureAppointmentPatientDTO appointmentPatient = new NutritionistFutureAppointmentPatientDTO();
        appointmentPatient.setName(patient.getUser().getFirstName() + " " + patient.getUser().getLastName());
        appointmentPatient.setEmail(patient.getUser().getEmail());
        appointmentPatient.setId(patientId);
        nutritionistFutureAppointmentDTO.setPatient(appointmentPatient);

        ResponseToCreateAppointment responseToCreateAppointment = new ResponseToCreateAppointment();

        responseToCreateAppointment.setId(savedAppointment.getId());
        responseToCreateAppointment.setType(AppointmentOrSchedule.APPOINTMENT);
        responseToCreateAppointment.setStatus(AppointmentStatusName.valueOf(savedAppointment.getAppointmentStatus().getName()));
        responseToCreateAppointment.setDurationMinutes(savedAppointment.getSchedule().getDurationMinutes());
        responseToCreateAppointment.setStartTime(savedAppointment.getSchedule().getStartTime());

        PatientSearchByNameDTO patientSearchByNameDTO = new PatientSearchByNameDTO();
        patientSearchByNameDTO.setId(savedAppointment.getId());
        patientSearchByNameDTO.setName(savedAppointment.getPatient().getUser().getFirstName() + " " + savedAppointment.getPatient().getUser().getLastName());
        patientSearchByNameDTO.setEmail(savedAppointment.getPatient().getUser().getEmail());

        responseToCreateAppointment.setPatient(patientSearchByNameDTO);

        return responseToCreateAppointment;
    }

    @Override
    public Set<PatientFutureAppointmentDTO> getPatientFutureAppointments(UUID userId) {
        Set<AppointmentPatientProjection> projections = appointmentRepository.findPatientFutureAppointments(userId);

        return projections.stream().map(projection -> {
            PatientFutureAppointmentDTO patientFutureAppointmentDTO = new PatientFutureAppointmentDTO();

            patientFutureAppointmentDTO.setId(projection.getId().toString());
            patientFutureAppointmentDTO.setIsRemote(projection.isRemote());
            patientFutureAppointmentDTO.setStartTime(projection.getStartTime());
            patientFutureAppointmentDTO.setDurationMinutes(projection.getDurationMinutes());
            patientFutureAppointmentDTO.setStatus(AppointmentStatusName.valueOf(projection.getStatus()));
            patientFutureAppointmentDTO.setType(EventType.APPOINTMENT);

            PatientFutureAppointmentNutritionistDTO patientFutureAppointmentNutritionistDTO = new PatientFutureAppointmentNutritionistDTO();
            patientFutureAppointmentNutritionistDTO.setId(projection.getNutritionistId());
            patientFutureAppointmentNutritionistDTO.setName(projection.getNutritionistName());
            patientFutureAppointmentNutritionistDTO.setEmail(projection.getNutritionistEmail());

            patientFutureAppointmentDTO.setNutritionist(patientFutureAppointmentNutritionistDTO);

            return patientFutureAppointmentDTO;
        }).collect(Collectors.toSet());
    }

    @Override
    public Set<NutritionistFutureAppointmentDTO> getNutritionistFutureAppointments(UUID userId) {
        Set<AppointmentNutritionistProjection> projections = appointmentRepository.findNutritionistFutureAppointments(userId);

        return projections.stream().map(projection -> {
            NutritionistFutureAppointmentDTO nutritionistFutureAppointmentDTO = new NutritionistFutureAppointmentDTO();

            nutritionistFutureAppointmentDTO.setId(projection.getId().toString());
            nutritionistFutureAppointmentDTO.setIsRemote(projection.isRemote());
            nutritionistFutureAppointmentDTO.setStartTime(projection.getStartTime());
            nutritionistFutureAppointmentDTO.setDurationMinutes(projection.getDurationMinutes());
            nutritionistFutureAppointmentDTO.setStatus(AppointmentStatusName.valueOf(projection.getStatus()));
            nutritionistFutureAppointmentDTO.setType(EventType.APPOINTMENT);

            NutritionistFutureAppointmentPatientDTO nutritionistFutureAppointmentPatientDTO = new NutritionistFutureAppointmentPatientDTO();
            nutritionistFutureAppointmentPatientDTO.setId(projection.getPatientId());
            nutritionistFutureAppointmentPatientDTO.setName(projection.getPatientName());
            nutritionistFutureAppointmentPatientDTO.setEmail(projection.getPatientEmail());

            nutritionistFutureAppointmentDTO.setPatient(nutritionistFutureAppointmentPatientDTO);

            return nutritionistFutureAppointmentDTO;
        }).collect(Collectors.toSet());
    }
}

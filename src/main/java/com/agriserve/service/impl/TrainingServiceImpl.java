package com.agriserve.service.impl;

import com.agriserve.dto.request.RegisterParticipationRequest;
import com.agriserve.dto.request.SubmitFeedbackAndRatingRequest;
import com.agriserve.dto.request.TrainingProgramRequest;
import com.agriserve.dto.request.WorkshopRequest;
import com.agriserve.dto.response.ParticipationResponse;
import com.agriserve.dto.response.TrainingProgramResponse;
import com.agriserve.dto.response.WorkshopResponse;
import com.agriserve.entity.Farmer;
import com.agriserve.entity.Participation;
import com.agriserve.entity.TrainingProgram;
import com.agriserve.entity.User;
import com.agriserve.entity.Workshop;
import com.agriserve.entity.enums.AttendanceStatus;
import com.agriserve.entity.enums.Status;
import com.agriserve.exception.BusinessException;
import com.agriserve.exception.ResourceNotFoundException;
import com.agriserve.repository.FarmerRepository;
import com.agriserve.repository.ParticipationRepository;
import com.agriserve.repository.TrainingProgramRepository;
import com.agriserve.repository.UserRepository;
import com.agriserve.repository.WorkshopRepository;
import com.agriserve.service.AuditLogService;
import com.agriserve.service.TrainingService;
import com.agriserve.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Manages training programs, workshops, and farmer participation tracking.
 *
 * <p>
 * <strong>Participation lifecycle (enforced in this class):</strong>
 * </p>
 * <ol>
 * <li>Officer → {@link #registerParticipation} — creates record, attendance =
 * ABSENT</li>
 * <li>Workshop → COMPLETED</li>
 * <li>Officer/Admin → {@link #updateAttendance} — marks PRESENT / ABSENT /
 * etc.</li>
 * <li>Farmer (if PRESENT) → {@link #submitFeedbackAndRating} — submits
 * rating/feedback</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingProgramRepository programRepository;
    private final WorkshopRepository workshopRepository;
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final FarmerRepository farmerRepository;
    private final AuditLogService auditLogService;

    // ─── Training Programs ────────────────────────────────────────────────────

    @Override
    @Transactional
    public TrainingProgramResponse createProgram(TrainingProgramRequest request) {
        TrainingProgram program = TrainingProgram.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus() != null ? request.getStatus() : Status.DRAFT)
                .build();
        TrainingProgram saved = programRepository.save(program);

        log.info("Program created: {}", saved.getTitle());
        auditLogService.log(SecurityUtils.getCurrentUserId(), "CREATE_PROGRAM",
                "Program#" + saved.getProgramId());

        return TrainingProgramResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingProgramResponse getProgramById(Long programId) {
        return TrainingProgramResponse.from(findProgramOrThrow(programId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingProgramResponse> getAllPrograms(Status status, Pageable pageable) {
        if (status != null) {
            return programRepository.findAllByStatus(status, pageable).map(TrainingProgramResponse::from);
        }
        return programRepository.findAll(pageable).map(TrainingProgramResponse::from);
    }

    @Override
    @Transactional
    public TrainingProgramResponse updateProgram(Long programId, TrainingProgramRequest request) {
        TrainingProgram program = findProgramOrThrow(programId);
        program.setTitle(request.getTitle());
        program.setDescription(request.getDescription());
        program.setStartDate(request.getStartDate());
        program.setEndDate(request.getEndDate());
        if (request.getStatus() != null)
            program.setStatus(request.getStatus());
        TrainingProgram updated = programRepository.save(program);

        log.info("Program updated: {}", programId);
        auditLogService.log(SecurityUtils.getCurrentUserId(), "UPDATE_PROGRAM",
                "Program#" + programId);

        return TrainingProgramResponse.from(updated);
    }

    @Override
    @Transactional
    public void deleteProgram(Long programId) {
        TrainingProgram program = findProgramOrThrow(programId);
        programRepository.delete(program);
    }

    // ─── Workshops ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public WorkshopResponse createWorkshop(WorkshopRequest request) {
        TrainingProgram program = findProgramOrThrow(request.getProgramId());
        User officer = userRepository.findById(request.getOfficerId())
                .orElseThrow(() -> new ResourceNotFoundException("User (Officer)", "id", request.getOfficerId()));

        Workshop workshop = Workshop.builder()
                .program(program)
                .officer(officer)
                .location(request.getLocation())
                .workshopDate(request.getWorkshopDate())
                .status(Status.PENDING)
                .build();
        Workshop saved = workshopRepository.save(workshop);

        log.info("Workshop created: program={}, officer={}", request.getProgramId(), request.getOfficerId());
        auditLogService.log(SecurityUtils.getCurrentUserId(), "CREATE_WORKSHOP",
                "Workshop#" + saved.getWorkshopId() + ", programId=" + program.getProgramId());

        return WorkshopResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkshopResponse getWorkshopById(Long workshopId) {
        return WorkshopResponse.from(findWorkshopOrThrow(workshopId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkshopResponse> getWorkshopsByProgram(Long programId, Pageable pageable) {
        findProgramOrThrow(programId);
        return workshopRepository.findAllByProgram_ProgramId(programId, pageable).map(WorkshopResponse::from);
    }

    /**
     * Updates workshop status and stamps {@code completedAt} when transitioning
     * to COMPLETED — enabling the participation feedback window to open.
     */
    @Override
    @Transactional
    public WorkshopResponse updateWorkshopStatus(Long workshopId, Status status) {
        Workshop workshop = findWorkshopOrThrow(workshopId);
        workshop.setStatus(status);

        if (status == Status.COMPLETED && workshop.getCompletedAt() == null) {
            workshop.setCompletedAt(LocalDateTime.now());
            log.info("Workshop {} marked COMPLETED at {}", workshopId, workshop.getCompletedAt());
        }

        Workshop updated = workshopRepository.save(workshop);

        log.info("Workshop status updated: workshop={}, status={}", workshopId, status);
        auditLogService.log(SecurityUtils.getCurrentUserId(), "UPDATE_WORKSHOP_STATUS",
                "Workshop#" + workshopId + " -> " + status);

        return WorkshopResponse.from(updated);
    }

    // ─── Participation ────────────────────────────────────────────────────────

    /**
     * Registers a farmer for a workshop. Called by EXTENSION_OFFICER only.
     *
     * <ul>
     * <li>Farmer must be ACTIVE.</li>
     * <li>Farmer cannot be registered twice for the same workshop.</li>
     * <li>Attendance defaults to ABSENT; no rating/feedback accepted here.</li>
     * </ul>
     */
    @Override
    @Transactional
    public ParticipationResponse registerParticipation(RegisterParticipationRequest request) {
        Workshop workshop = findWorkshopOrThrow(request.getWorkshopId());
        Farmer farmer = farmerRepository.findById(request.getFarmerId())
                .orElseThrow(() -> new ResourceNotFoundException("Farmer", "id", request.getFarmerId()));

        // Farmer must be ACTIVE to register
        if (farmer.getStatus() != Status.ACTIVE) {
            throw new BusinessException(
                    "Farmer [" + farmer.getFarmerId() + "] is not ACTIVE (current status: "
                            + farmer.getStatus() + "). Only ACTIVE farmers can be registered for workshops.");
        }

        // Duplicate-registration guard
        participationRepository.findByWorkshop_WorkshopIdAndFarmer_FarmerId(
                request.getWorkshopId(), request.getFarmerId())
                .ifPresent(p -> {
                    throw new BusinessException("Farmer is already registered for this workshop.");
                });

        Participation participation = Participation.builder()
                .workshop(workshop)
                .farmer(farmer)
                .attendanceStatus(AttendanceStatus.ABSENT) // explicitly default; no input accepted
                .build();
        Participation saved = participationRepository.save(participation);

        log.info("Participation registered: farmer={}, workshop={}",
                farmer.getFarmerId(), workshop.getWorkshopId());
        auditLogService.log(SecurityUtils.getCurrentUserId(), "REGISTER_PARTICIPATION",
                "Participation#" + saved.getParticipationId() + ", workshopId=" + workshop.getWorkshopId());

        return ParticipationResponse.from(saved);
    }

    /**
     * Updates the attendance status for a participation record.
     * Called by EXTENSION_OFFICER or ADMIN — ONLY after the workshop is COMPLETED.
     * Feedback and rating are not touched here.
     */
    @Override
    @Transactional
    public ParticipationResponse updateAttendance(Long participationId, AttendanceStatus attendanceStatus) {

        if (attendanceStatus == null) {
            throw new BusinessException("Attendance status is required");
        }

        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new ResourceNotFoundException("Participation", "id", participationId));

        // Gate: workshop must be COMPLETED before attendance can be recorded
        if (participation.getWorkshop().getStatus() != Status.COMPLETED) {
            throw new BusinessException(
                    "Attendance can only be updated after the workshop is COMPLETED. " +
                            "Current workshop status: " + participation.getWorkshop().getStatus());
        }

        participation.setAttendanceStatus(attendanceStatus);
        Participation updated = participationRepository.save(participation);

        log.info("Attendance updated: participation={}, status={}", participationId, attendanceStatus);
        auditLogService.log(SecurityUtils.getCurrentUserId(), "UPDATE_ATTENDANCE",
                "Participation#" + participationId + "status=" + attendanceStatus);

        return ParticipationResponse.from(updated);
    }

    /**
     * Submits workshop feedback and/or a satisfaction rating on behalf of the
     * farmer.
     * Called by FARMER only (ownership verified at controller via @PreAuthorize).
     *
     * <ul>
     * <li>Workshop must be COMPLETED.</li>
     * <li>Farmer must have PRESENT attendance on this participation record.</li>
     * </ul>
     */
    @Override
    @Transactional
    public ParticipationResponse submitFeedbackAndRating(Long participationId,
            SubmitFeedbackAndRatingRequest request) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new ResourceNotFoundException("Participation", "id", participationId));


        if (participation.getWorkshop().getStatus() != Status.COMPLETED) {
            throw new BusinessException(
                    "Workshop must be COMPLETED before submitting feedback. " +
                            "Current workshop status: " + participation.getWorkshop().getStatus());
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        Farmer currentFarmer = farmerRepository.findByUser_UserId(currentUserId)
                .orElseThrow(() -> new BusinessException("Farmer profile not found for current user"));

// 3. Compare the participation's farmer with the logged-in farmer
        if (!participation.getFarmer().getFarmerId().equals(currentFarmer.getFarmerId())) {
            throw new BusinessException("You cannot give feedback for another farmer's session.");
        }


        if (participation.getAttendanceStatus() != AttendanceStatus.PRESENT) {
            throw new BusinessException(
                    "Only farmers who attended (PRESENT) the workshop can submit feedback. " +
                            "Current attendance status: " + participation.getAttendanceStatus());
        }

        if (request.getFeedback() != null) {
            participation.setFeedback(request.getFeedback());
        }
        if (request.getRating() != null) {
            participation.setWorkshopRating(request.getRating());
        }
        Participation updated = participationRepository.save(participation);

        log.info("Feedback submitted: participation={}, rating={}", participationId, request.getRating());
        auditLogService.log(participation.getFarmer().getUser(), "SUBMIT_FEEDBACK_RATING",
                "Participation#" + participationId,
                "rating=" + request.getRating());

        return ParticipationResponse.from(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParticipationResponse> getParticipationsByWorkshop(Long workshopId, Pageable pageable) {
        return participationRepository.findAllByWorkshop_WorkshopId(workshopId, pageable)
                .map(ParticipationResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParticipationResponse> getParticipationsByFarmer(Long farmerId, Pageable pageable) {
        return participationRepository.findAllByFarmer_FarmerId(farmerId, pageable)
                .map(ParticipationResponse::from);
    }

    // ─── Private Helpers ─────────────────────────────────────────────────────

    private TrainingProgram findProgramOrThrow(Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TrainingProgram", "id", id));
    }

    private Workshop findWorkshopOrThrow(Long id) {
        return workshopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workshop", "id", id));
    }
}

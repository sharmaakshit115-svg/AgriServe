package com.agriserve.service;

import com.agriserve.dto.request.RegisterParticipationRequest;
import com.agriserve.dto.request.SubmitFeedbackAndRatingRequest;
import com.agriserve.dto.request.TrainingProgramRequest;
import com.agriserve.entity.enums.AttendanceStatus;
import com.agriserve.dto.request.WorkshopRequest;
import com.agriserve.dto.response.ParticipationResponse;
import com.agriserve.dto.response.TrainingProgramResponse;
import com.agriserve.dto.response.WorkshopResponse;
import com.agriserve.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Contract for Training Program, Workshop, and Participation management.
 *
 * <p>
 * Participation lifecycle:
 * </p>
 * <ol>
 * <li>EXTENSION_OFFICER calls {@link #registerParticipation} — creates the
 * record (ABSENT by default)</li>
 * <li>Workshop is marked COMPLETED</li>
 * <li>EXTENSION_OFFICER or ADMIN calls {@link #updateAttendance} — sets PRESENT
 * / ABSENT / PARTIAL</li>
 * <li>FARMER (if PRESENT) calls {@link #submitFeedbackAndRating} — submits
 * rating and/or feedback</li>
 * </ol>
 */
public interface TrainingService {

    // ─── Training Programs ───────────────────────────────────────────────────

    TrainingProgramResponse createProgram(TrainingProgramRequest request);

    TrainingProgramResponse getProgramById(Long programId);

    Page<TrainingProgramResponse> getAllPrograms(Status status, Pageable pageable);

    TrainingProgramResponse updateProgram(Long programId, TrainingProgramRequest request);

    void deleteProgram(Long programId);

    // ─── Workshops ───────────────────────────────────────────────────────────

    WorkshopResponse createWorkshop(WorkshopRequest request);

    WorkshopResponse getWorkshopById(Long workshopId);

    Page<WorkshopResponse> getWorkshopsByProgram(Long programId, Pageable pageable);

    WorkshopResponse updateWorkshopStatus(Long workshopId, Status status);

    // ─── Participation ───────────────────────────────────────────────────────

    /**
     * Registers a farmer for a workshop. EXTENSION_OFFICER only.
     * Sets attendance to ABSENT by default; no rating or feedback accepted.
     */
    ParticipationResponse registerParticipation(RegisterParticipationRequest request);

    /**
     * Updates the attendance status for a participation record.
     * EXTENSION_OFFICER or ADMIN only. Workshop must be COMPLETED first.
     */
    ParticipationResponse updateAttendance(Long participationId, AttendanceStatus attendanceStatus);

    /**
     * Submits workshop feedback and/or a satisfaction rating.
     * FARMER only, and only if their attendance is PRESENT on a COMPLETED workshop.
     */
    ParticipationResponse submitFeedbackAndRating(Long participationId, SubmitFeedbackAndRatingRequest request);

    Page<ParticipationResponse> getParticipationsByWorkshop(Long workshopId, Pageable pageable);

    Page<ParticipationResponse> getParticipationsByFarmer(Long farmerId, Pageable pageable);
}

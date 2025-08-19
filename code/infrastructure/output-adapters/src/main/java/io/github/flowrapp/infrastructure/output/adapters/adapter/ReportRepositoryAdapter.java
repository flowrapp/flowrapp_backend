package io.github.flowrapp.infrastructure.output.adapters.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import io.github.flowrapp.infrastructure.jpa.businessbd.entity.QReportEntity;
import io.github.flowrapp.infrastructure.jpa.businessbd.repository.ReportJpaRepository;
import io.github.flowrapp.infrastructure.output.adapters.mapper.ReportEntityMapper;
import io.github.flowrapp.model.Report;
import io.github.flowrapp.value.TimesheetFilterRequest;
import io.github.flowrapp.port.output.ReportRepositoryOutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportRepositoryAdapter implements ReportRepositoryOutput {

  private final ReportJpaRepository reportJpaRepository;

  private final ReportEntityMapper reportEntityMapper;

  @Override
  public List<Report> findAll(@NonNull TimesheetFilterRequest filter) {
    val qReport = QReportEntity.reportEntity;
    val predicate = qReport.isNotNull()
        .and(filter.userId() != null ? qReport.user.id.eq(filter.userId()) : null)
        .and(filter.businessId() != null ? qReport.business.id.eq(filter.businessId()) : null)
        .and(filter.from() != null ? qReport.id.clockDay.goe(filter.from()) : null)
        .and(filter.to() != null ? qReport.id.clockDay.loe(filter.to()) : null);

    return reportEntityMapper.infra2domain(
        reportJpaRepository.findAll(predicate));
  }

  @Override
  public Optional<Report> getByDay(@NonNull Integer userId, @NonNull Integer businessId, @NonNull LocalDate day) {
    val qReport = QReportEntity.reportEntity;
    val predicate = qReport.isNotNull()
        .and(qReport.user.id.eq(userId))
        .and(qReport.business.id.eq(businessId))
        .and(qReport.id.clockDay.eq(day));

    return reportJpaRepository.findOne(predicate)
        .map(reportEntityMapper::infra2domain);
  }

  @Override
  public Report save(Report report) {
    return reportEntityMapper.infra2domain(
        reportJpaRepository.save(
            reportEntityMapper.domain2Infra(report)));
  }

}

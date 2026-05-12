package com.ute.library.service;

import com.ute.library.dao.ReportDao;
import com.ute.library.dto.BorrowedBookReportDTO;
import com.ute.library.dto.CategoryOptionDTO;
import com.ute.library.dto.OverdueBookReportDTO;
import com.ute.library.dto.ReportFilterDTO;
import com.ute.library.dto.ReportSummaryDTO;
import com.ute.library.dto.TimeStatisticsDTO;
import com.ute.library.dto.TopBorrowedBookDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    private static final int DEFAULT_TOP_LIMIT = 10;
    private static final int MAX_TOP_LIMIT = 100;

    private final ReportDao reportDao;

    public ReportServiceImpl(ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    @Override
    public ReportSummaryDTO getSummary() {
        return reportDao.getSummary();
    }

    @Override
    public List<BorrowedBookReportDTO> findBorrowedBooks(ReportFilterDTO filter) {
        return reportDao.findBorrowedBooks(normalize(filter));
    }

    @Override
    public List<OverdueBookReportDTO> findOverdueBooks(ReportFilterDTO filter) {
        return reportDao.findOverdueBooks(normalize(filter));
    }

    @Override
    public List<TopBorrowedBookDTO> findTopBorrowedBooks(ReportFilterDTO filter) {
        ReportFilterDTO normalizedFilter = normalize(filter);
        if (normalizedFilter.getLimit() == null || normalizedFilter.getLimit() <= 0) {
            normalizedFilter.setLimit(DEFAULT_TOP_LIMIT);
        }
        if (normalizedFilter.getLimit() > MAX_TOP_LIMIT) {
            normalizedFilter.setLimit(MAX_TOP_LIMIT);
        }
        return reportDao.findTopBorrowedBooks(normalizedFilter);
    }

    @Override
    public TimeStatisticsDTO getTimeStatistics(ReportFilterDTO filter) {
        return reportDao.getTimeStatistics(normalize(filter));
    }

    @Override
    public List<CategoryOptionDTO> findCategoryOptions() {
        return reportDao.findCategoryOptions();
    }

    private ReportFilterDTO normalize(ReportFilterDTO filter) {
        ReportFilterDTO normalizedFilter = filter == null ? new ReportFilterDTO() : filter;

        normalizedFilter.setReaderKeyword(trimToNull(normalizedFilter.getReaderKeyword()));
        normalizedFilter.setBookKeyword(trimToNull(normalizedFilter.getBookKeyword()));
        if (normalizedFilter.getCategoryId() != null && normalizedFilter.getCategoryId() <= 0) {
            normalizedFilter.setCategoryId(null);
        }

        if (normalizedFilter.getFromDate() != null
            && normalizedFilter.getToDate() != null
            && normalizedFilter.getFromDate().isAfter(normalizedFilter.getToDate())) {
            throw new IllegalArgumentException("Tu ngay khong duoc lon hon den ngay.");
        }

        return normalizedFilter;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }
}

package com.ute.library.dao;

import com.ute.library.dto.BorrowedBookReportDTO;
import com.ute.library.dto.CategoryOptionDTO;
import com.ute.library.dto.OverdueBookReportDTO;
import com.ute.library.dto.ReportFilterDTO;
import com.ute.library.dto.ReportSummaryDTO;
import com.ute.library.dto.TimeStatisticsDTO;
import com.ute.library.dto.TopBorrowedBookDTO;

import java.util.List;

public interface ReportDao {
    ReportSummaryDTO getSummary();

    List<BorrowedBookReportDTO> findBorrowedBooks(ReportFilterDTO filter);

    List<OverdueBookReportDTO> findOverdueBooks(ReportFilterDTO filter);

    List<TopBorrowedBookDTO> findTopBorrowedBooks(ReportFilterDTO filter);

    TimeStatisticsDTO getTimeStatistics(ReportFilterDTO filter);

    List<CategoryOptionDTO> findCategoryOptions();
}

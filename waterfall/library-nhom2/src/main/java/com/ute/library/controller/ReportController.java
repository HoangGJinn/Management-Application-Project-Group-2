package com.ute.library.controller;

import com.ute.library.dto.ReportFilterDTO;
import com.ute.library.dto.ReportSummaryDTO;
import com.ute.library.dto.TimeStatisticsDTO;
import com.ute.library.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

@Controller
@RequestMapping("/reports")
public class ReportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);
    private static final String FILTER_ERROR_MESSAGE = "Tu ngay khong duoc lon hon den ngay.";
    private static final String DATA_ERROR_MESSAGE =
        "Khong the tai du lieu bao cao. Vui long kiem tra ket noi MySQL va cau truc bang.";

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String summary(Model model) {
        try {
            model.addAttribute("summary", reportService.getSummary());
        } catch (DataAccessException ex) {
            LOGGER.warn("Cannot load report summary", ex);
            model.addAttribute("summary", new ReportSummaryDTO());
            model.addAttribute("errorMessage", DATA_ERROR_MESSAGE);
        }
        return "reports";
    }

    @GetMapping("/borrowed")
    public String borrowedBooks(@ModelAttribute("filter") ReportFilterDTO filter, Model model) {
        try {
            model.addAttribute("borrowedBooks", reportService.findBorrowedBooks(filter));
        } catch (IllegalArgumentException ex) {
            model.addAttribute("borrowedBooks", Collections.emptyList());
            model.addAttribute("errorMessage", FILTER_ERROR_MESSAGE);
        } catch (DataAccessException ex) {
            LOGGER.warn("Cannot load borrowed book report", ex);
            model.addAttribute("borrowedBooks", Collections.emptyList());
            model.addAttribute("errorMessage", DATA_ERROR_MESSAGE);
        }
        return "reports-borrowed";
    }

    @GetMapping("/overdue")
    public String overdueBooks(@ModelAttribute("filter") ReportFilterDTO filter, Model model) {
        try {
            model.addAttribute("overdueBooks", reportService.findOverdueBooks(filter));
        } catch (IllegalArgumentException ex) {
            model.addAttribute("overdueBooks", Collections.emptyList());
            model.addAttribute("errorMessage", FILTER_ERROR_MESSAGE);
        } catch (DataAccessException ex) {
            LOGGER.warn("Cannot load overdue book report", ex);
            model.addAttribute("overdueBooks", Collections.emptyList());
            model.addAttribute("errorMessage", DATA_ERROR_MESSAGE);
        }
        return "reports-overdue";
    }

    @GetMapping("/top-books")
    public String topBooks(@ModelAttribute("filter") ReportFilterDTO filter, Model model) {
        try {
            model.addAttribute("topBooks", reportService.findTopBorrowedBooks(filter));
        } catch (IllegalArgumentException ex) {
            model.addAttribute("topBooks", Collections.emptyList());
            model.addAttribute("errorMessage", FILTER_ERROR_MESSAGE);
        } catch (DataAccessException ex) {
            LOGGER.warn("Cannot load top borrowed book report", ex);
            model.addAttribute("topBooks", Collections.emptyList());
            model.addAttribute("errorMessage", DATA_ERROR_MESSAGE);
        }

        try {
            model.addAttribute("categories", reportService.findCategoryOptions());
        } catch (DataAccessException ex) {
            LOGGER.warn("Cannot load category options", ex);
            model.addAttribute("categories", Collections.emptyList());
        }
        return "reports-top-books";
    }

    @GetMapping("/statistics")
    public String statistics(@ModelAttribute("filter") ReportFilterDTO filter, Model model) {
        try {
            model.addAttribute("statistics", reportService.getTimeStatistics(filter));
        } catch (IllegalArgumentException ex) {
            model.addAttribute("statistics", new TimeStatisticsDTO());
            model.addAttribute("errorMessage", FILTER_ERROR_MESSAGE);
        } catch (DataAccessException ex) {
            LOGGER.warn("Cannot load time statistics report", ex);
            model.addAttribute("statistics", new TimeStatisticsDTO());
            model.addAttribute("errorMessage", DATA_ERROR_MESSAGE);
        }
        return "reports-statistics";
    }
}

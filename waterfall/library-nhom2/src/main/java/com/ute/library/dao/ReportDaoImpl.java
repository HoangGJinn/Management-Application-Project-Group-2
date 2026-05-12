package com.ute.library.dao;

import com.ute.library.dto.BorrowedBookReportDTO;
import com.ute.library.dto.CategoryOptionDTO;
import com.ute.library.dto.OverdueBookReportDTO;
import com.ute.library.dto.ReportFilterDTO;
import com.ute.library.dto.ReportSummaryDTO;
import com.ute.library.dto.TimeStatisticsDTO;
import com.ute.library.dto.TopBorrowedBookDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReportDaoImpl implements ReportDao {
    private final JdbcTemplate jdbcTemplate;

    public ReportDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ReportSummaryDTO getSummary() {
        String sql = """
            SELECT
                (SELECT COUNT(*) FROM books WHERE status = 'ACTIVE') AS active_book_titles,
                (SELECT COALESCE(SUM(quantity), 0) FROM books WHERE status = 'ACTIVE') AS total_book_copies,
                (SELECT COALESCE(SUM(available_quantity), 0) FROM books WHERE status = 'ACTIVE') AS available_book_copies,
                (
                    SELECT COALESCE(SUM(bi.quantity), 0)
                    FROM borrow_items bi
                    JOIN borrow_slips bs ON bs.id = bi.borrow_slip_id
                    WHERE bs.status = 'BORROWING' AND bi.item_status = 'BORROWING'
                ) AS borrowed_book_copies,
                (SELECT COUNT(*) FROM readers WHERE status = 'ACTIVE') AS active_readers,
                (SELECT COUNT(*) FROM borrow_slips WHERE status = 'BORROWING') AS open_borrow_slips,
                (
                    SELECT COALESCE(SUM(bi.quantity), 0)
                    FROM borrow_items bi
                    JOIN borrow_slips bs ON bs.id = bi.borrow_slip_id
                    WHERE bs.status = 'BORROWING'
                      AND bi.item_status = 'BORROWING'
                      AND bs.due_date < CURRENT_DATE
                ) AS overdue_book_copies,
                (SELECT COALESCE(SUM(quantity), 0) FROM borrow_items WHERE item_status = 'LOST') AS lost_book_copies
            """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new ReportSummaryDTO(
            rs.getLong("active_book_titles"),
            rs.getLong("total_book_copies"),
            rs.getLong("available_book_copies"),
            rs.getLong("borrowed_book_copies"),
            rs.getLong("active_readers"),
            rs.getLong("open_borrow_slips"),
            rs.getLong("overdue_book_copies"),
            rs.getLong("lost_book_copies")
        ));
    }

    @Override
    public List<BorrowedBookReportDTO> findBorrowedBooks(ReportFilterDTO filter) {
        StringBuilder sql = new StringBuilder("""
            SELECT
                bs.slip_code,
                r.reader_code,
                r.full_name AS reader_name,
                b.book_code,
                b.title AS book_title,
                b.author,
                bi.quantity,
                bs.borrow_date,
                bs.due_date,
                l.full_name AS librarian_name,
                bi.item_status AS status
            FROM borrow_slips bs
            JOIN borrow_items bi ON bi.borrow_slip_id = bs.id
            JOIN books b ON b.id = bi.book_id
            JOIN readers r ON r.id = bs.reader_id
            JOIN librarians l ON l.id = bs.librarian_id
            WHERE bs.status = 'BORROWING'
              AND bi.item_status = 'BORROWING'
            """);
        List<Object> params = new ArrayList<>();

        addDateRange(sql, params, "bs.borrow_date", filter);
        addKeywordFilter(sql, params, filter.getReaderKeyword(), "r.reader_code", "r.full_name");
        addKeywordFilter(sql, params, filter.getBookKeyword(), "b.book_code", "b.title");

        sql.append(" ORDER BY bs.borrow_date DESC, bs.id DESC, bi.id DESC");
        return jdbcTemplate.query(sql.toString(), this::mapBorrowedBook, params.toArray());
    }

    @Override
    public List<OverdueBookReportDTO> findOverdueBooks(ReportFilterDTO filter) {
        StringBuilder sql = new StringBuilder("""
            SELECT
                bs.slip_code,
                r.reader_code,
                r.full_name AS reader_name,
                r.email,
                r.phone,
                b.book_code,
                b.title AS book_title,
                bi.quantity,
                bs.borrow_date,
                bs.due_date,
                DATEDIFF(CURRENT_DATE, bs.due_date) AS overdue_days
            FROM borrow_slips bs
            JOIN borrow_items bi ON bi.borrow_slip_id = bs.id
            JOIN books b ON b.id = bi.book_id
            JOIN readers r ON r.id = bs.reader_id
            WHERE bs.status = 'BORROWING'
              AND bi.item_status = 'BORROWING'
              AND bs.due_date < CURRENT_DATE
            """);
        List<Object> params = new ArrayList<>();

        addDateRange(sql, params, "bs.borrow_date", filter);
        addKeywordFilter(sql, params, filter.getReaderKeyword(), "r.reader_code", "r.full_name");
        addKeywordFilter(sql, params, filter.getBookKeyword(), "b.book_code", "b.title");

        sql.append(" ORDER BY overdue_days DESC, bs.due_date ASC, bs.id DESC");
        return jdbcTemplate.query(sql.toString(), this::mapOverdueBook, params.toArray());
    }

    @Override
    public List<TopBorrowedBookDTO> findTopBorrowedBooks(ReportFilterDTO filter) {
        StringBuilder sql = new StringBuilder("""
            SELECT
                b.book_code,
                b.title AS book_title,
                b.author,
                COALESCE(c.category_name, '') AS category_name,
                COALESCE(p.publisher_name, '') AS publisher_name,
                COUNT(bi.id) AS total_borrow_times,
                COALESCE(SUM(bi.quantity), 0) AS total_borrowed_quantity
            FROM borrow_items bi
            JOIN borrow_slips bs ON bs.id = bi.borrow_slip_id
            JOIN books b ON b.id = bi.book_id
            LEFT JOIN categories c ON c.id = b.category_id
            LEFT JOIN publishers p ON p.id = b.publisher_id
            WHERE bs.status <> 'CANCELLED'
            """);
        List<Object> params = new ArrayList<>();

        addDateRange(sql, params, "bs.borrow_date", filter);
        if (filter.getCategoryId() != null) {
            sql.append(" AND b.category_id = ?");
            params.add(filter.getCategoryId());
        }

        sql.append("""
            GROUP BY b.id, b.book_code, b.title, b.author, c.category_name, p.publisher_name
            ORDER BY total_borrowed_quantity DESC, total_borrow_times DESC, b.title ASC
            LIMIT ?
            """);
        params.add(filter.getLimit());

        return jdbcTemplate.query(sql.toString(), this::mapTopBorrowedBook, params.toArray());
    }

    @Override
    public TimeStatisticsDTO getTimeStatistics(ReportFilterDTO filter) {
        long createdSlipCount = queryForLong(
            "SELECT COUNT(*) FROM borrow_slips bs WHERE 1 = 1" + dateRangeSql("DATE(bs.created_at)", filter),
            dateRangeParams(filter)
        );
        long borrowedQuantity = queryForLong(
            """
            SELECT COALESCE(SUM(bi.quantity), 0)
            FROM borrow_items bi
            JOIN borrow_slips bs ON bs.id = bi.borrow_slip_id
            WHERE bs.status <> 'CANCELLED'
            """ + dateRangeSql("bs.borrow_date", filter),
            dateRangeParams(filter)
        );
        long returnedQuantity = queryForLong(
            """
            SELECT COALESCE(SUM(bi.quantity), 0)
            FROM borrow_items bi
            WHERE bi.item_status = 'RETURNED'
              AND bi.return_date IS NOT NULL
            """ + dateRangeSql("bi.return_date", filter),
            dateRangeParams(filter)
        );
        long overdueQuantity = queryForLong(
            """
            SELECT COALESCE(SUM(bi.quantity), 0)
            FROM borrow_items bi
            JOIN borrow_slips bs ON bs.id = bi.borrow_slip_id
            WHERE bs.status = 'BORROWING'
              AND bi.item_status = 'BORROWING'
              AND bs.due_date < CURRENT_DATE
            """ + dateRangeSql("bs.due_date", filter),
            dateRangeParams(filter)
        );
        long completedSlipCount = queryForLong(
            """
            SELECT COUNT(*)
            FROM borrow_slips bs
            WHERE bs.status = 'RETURNED'
              AND bs.closed_at IS NOT NULL
            """ + dateRangeSql("DATE(bs.closed_at)", filter),
            dateRangeParams(filter)
        );
        long borrowingSlipCount = queryForLong(
            "SELECT COUNT(*) FROM borrow_slips bs WHERE bs.status = 'BORROWING'" + dateRangeSql("bs.borrow_date", filter),
            dateRangeParams(filter)
        );

        return new TimeStatisticsDTO(
            createdSlipCount,
            borrowedQuantity,
            returnedQuantity,
            overdueQuantity,
            completedSlipCount,
            borrowingSlipCount
        );
    }

    @Override
    public List<CategoryOptionDTO> findCategoryOptions() {
        String sql = "SELECT id, category_name FROM categories ORDER BY category_name ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new CategoryOptionDTO(
            rs.getInt("id"),
            rs.getString("category_name")
        ));
    }

    private BorrowedBookReportDTO mapBorrowedBook(ResultSet rs, int rowNum) throws SQLException {
        return new BorrowedBookReportDTO(
            rs.getString("slip_code"),
            rs.getString("reader_code"),
            rs.getString("reader_name"),
            rs.getString("book_code"),
            rs.getString("book_title"),
            rs.getString("author"),
            rs.getInt("quantity"),
            getLocalDate(rs, "borrow_date"),
            getLocalDate(rs, "due_date"),
            rs.getString("librarian_name"),
            rs.getString("status")
        );
    }

    private OverdueBookReportDTO mapOverdueBook(ResultSet rs, int rowNum) throws SQLException {
        return new OverdueBookReportDTO(
            rs.getString("slip_code"),
            rs.getString("reader_code"),
            rs.getString("reader_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("book_code"),
            rs.getString("book_title"),
            rs.getInt("quantity"),
            getLocalDate(rs, "borrow_date"),
            getLocalDate(rs, "due_date"),
            rs.getInt("overdue_days")
        );
    }

    private TopBorrowedBookDTO mapTopBorrowedBook(ResultSet rs, int rowNum) throws SQLException {
        return new TopBorrowedBookDTO(
            rs.getString("book_code"),
            rs.getString("book_title"),
            rs.getString("author"),
            rs.getString("category_name"),
            rs.getString("publisher_name"),
            rs.getLong("total_borrow_times"),
            rs.getLong("total_borrowed_quantity")
        );
    }

    private LocalDate getLocalDate(ResultSet rs, String columnName) throws SQLException {
        Date date = rs.getDate(columnName);
        return date == null ? null : date.toLocalDate();
    }

    private void addDateRange(StringBuilder sql, List<Object> params, String columnName, ReportFilterDTO filter) {
        if (filter.getFromDate() != null) {
            sql.append(" AND ").append(columnName).append(" >= ?");
            params.add(Date.valueOf(filter.getFromDate()));
        }
        if (filter.getToDate() != null) {
            sql.append(" AND ").append(columnName).append(" <= ?");
            params.add(Date.valueOf(filter.getToDate()));
        }
    }

    private void addKeywordFilter(StringBuilder sql, List<Object> params, String keyword, String... columnNames) {
        if (keyword == null || keyword.isBlank()) {
            return;
        }

        String likeValue = "%" + keyword.toLowerCase() + "%";
        sql.append(" AND (");
        for (int i = 0; i < columnNames.length; i++) {
            if (i > 0) {
                sql.append(" OR ");
            }
            sql.append("LOWER(COALESCE(").append(columnNames[i]).append(", '')) LIKE ?");
            params.add(likeValue);
        }
        sql.append(")");
    }

    private String dateRangeSql(String columnName, ReportFilterDTO filter) {
        StringBuilder sql = new StringBuilder();
        if (filter.getFromDate() != null) {
            sql.append(" AND ").append(columnName).append(" >= ?");
        }
        if (filter.getToDate() != null) {
            sql.append(" AND ").append(columnName).append(" <= ?");
        }
        return sql.toString();
    }

    private Object[] dateRangeParams(ReportFilterDTO filter) {
        List<Object> params = new ArrayList<>();
        if (filter.getFromDate() != null) {
            params.add(Date.valueOf(filter.getFromDate()));
        }
        if (filter.getToDate() != null) {
            params.add(Date.valueOf(filter.getToDate()));
        }
        return params.toArray();
    }

    private long queryForLong(String sql, Object[] params) {
        Number result = jdbcTemplate.queryForObject(sql, Number.class, params);
        return result == null ? 0L : result.longValue();
    }
}

# Test case chức năng Thống kê, báo cáo

| Test case ID | Tên test | Dữ liệu đầu vào | Các bước thực hiện | Kết quả mong đợi |
| --- | --- | --- | --- | --- |
| RP-001 | Xem tổng quan báo cáo | Database có đủ bảng báo cáo | Mở `/reports` | Hiển thị đủ 8 chỉ số: đầu sách hoạt động, tổng bản sách, bản còn mượn, bản đang mượn, độc giả hoạt động, phiếu đang mở, sách quá hạn, sách mất |
| RP-002 | Lọc sách đang mượn theo ngày | `fromDate`, `toDate` hợp lệ | Mở `/reports/borrowed`, nhập khoảng ngày, bấm `Lọc` | Chỉ hiển thị phiếu có `borrow_date` trong khoảng và trạng thái đang mượn |
| RP-003 | Lọc sách đang mượn theo độc giả | `readerKeyword = DG001` hoặc tên độc giả | Mở `/reports/borrowed`, nhập từ khóa độc giả, bấm `Lọc` | Danh sách chỉ gồm độc giả có mã hoặc tên khớp từ khóa |
| RP-004 | Lọc sách đang mượn theo sách | `bookKeyword = Java` hoặc mã sách | Mở `/reports/borrowed`, nhập từ khóa sách, bấm `Lọc` | Danh sách chỉ gồm sách có mã hoặc tên khớp từ khóa |
| RP-005 | Xem sách quá hạn | Có phiếu `BORROWING`, item `BORROWING`, `due_date < CURRENT_DATE` | Mở `/reports/overdue` | Hiển thị sách quá hạn, sắp xếp quá hạn lâu nhất trước |
| RP-006 | Xem top sách mặc định | Có dữ liệu mượn sách | Mở `/reports/top-books` không nhập limit | Hiển thị tối đa 10 sách, không tính phiếu `CANCELLED` |
| RP-007 | Lọc top sách theo thể loại | Chọn một thể loại | Mở `/reports/top-books`, chọn thể loại, bấm `Lọc` | Chỉ hiển thị sách thuộc thể loại đã chọn |
| RP-008 | Thống kê theo thời gian | `fromDate`, `toDate` hợp lệ | Mở `/reports/statistics`, nhập khoảng ngày, bấm `Lọc` | Hiển thị số phiếu tạo, số lượng mượn/trả/quá hạn, phiếu hoàn tất, phiếu đang mượn trong khoảng |
| RP-009 | Kiểm tra khoảng ngày sai | `fromDate > toDate` | Nhập ngày bắt đầu lớn hơn ngày kết thúc ở một trang lọc | Hiển thị thông báo lỗi và không hiển thị dữ liệu sai |
| RP-010 | Không có dữ liệu | Database không có bản ghi phù hợp filter | Lọc với từ khóa/khoảng ngày không tồn tại | Hiển thị thông báo không có dữ liệu |

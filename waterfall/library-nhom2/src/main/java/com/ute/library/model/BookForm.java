package com.ute.library.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookForm {
    private Integer id;

    @NotBlank(message = "Mã sách không được để trống")
    private String bookCode;

    @NotBlank(message = "Tên sách không được để trống")
    private String title;

    @NotBlank(message = "Tác giả không được để trống")
    private String author;

    @NotNull(message = "Vui lòng chọn thể loại")
    private Integer categoryId;

    @NotNull(message = "Vui lòng chọn nhà xuất bản")
    private Integer publisherId;

    private Integer publishYear;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private Integer quantity;

    @NotNull(message = "Số lượng còn lại không được để trống")
    @Min(value = 0, message = "Số lượng còn lại phải lớn hơn hoặc bằng 0")
    private Integer availableQuantity;

    @NotBlank(message = "Trạng thái không được để trống")
    private String status;
}


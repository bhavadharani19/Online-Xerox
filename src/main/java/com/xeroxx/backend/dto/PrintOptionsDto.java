package com.xeroxx.backend.dto;

import com.xeroxx.backend.entity.PaperSize;
import com.xeroxx.backend.entity.PrintType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrintOptionsDto {
    @NotNull
    private PaperSize paperSize;
    @NotNull
    private PrintType printType;
    @Min(1)
    @Max(500)
    private int copies;
    private boolean binding;
    private boolean lamination;
    private boolean urgent;
    private boolean duplex;
    private String additionalNotes;
}




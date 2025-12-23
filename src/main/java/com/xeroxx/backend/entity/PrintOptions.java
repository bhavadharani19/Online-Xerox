package com.xeroxx.backend.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PrintOptions {

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaperSize paperSize;

    @Enumerated(EnumType.STRING)
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




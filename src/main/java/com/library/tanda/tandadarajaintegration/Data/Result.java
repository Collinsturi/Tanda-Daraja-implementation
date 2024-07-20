package com.library.tanda.tandadarajaintegration.Data;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Result {
    private UUID id;
    private String status;
    private String ref;
}

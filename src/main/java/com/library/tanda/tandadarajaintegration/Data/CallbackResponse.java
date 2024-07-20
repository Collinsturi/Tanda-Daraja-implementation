package com.library.tanda.tandadarajaintegration.Data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallbackResponse {
    private UUID id;
    private String status;
    private String ref;
}

package org.example.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasicResponse {
    @JsonIgnore
    private final int statusCode;

    public static BasicResponse ok() {
        return new BasicResponse(200);
    }

    public static BasicResponse noContent() {
        return new BasicResponse(204);
    }
}

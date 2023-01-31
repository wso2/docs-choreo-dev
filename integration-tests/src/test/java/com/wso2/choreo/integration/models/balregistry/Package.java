package com.wso2.choreo.integration.models.balregistry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Package {
    String organization;
    String name;
    String version;

    @Override
    public String toString() {
        return "Package{" +
                "organization='" + organization + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}

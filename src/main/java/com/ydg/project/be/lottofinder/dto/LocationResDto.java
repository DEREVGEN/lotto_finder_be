package com.ydg.project.be.lottofinder.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Setter
@Getter
public class LocationResDto {

    @Range(min = -90, max = 90, message = "out of range")
    double lat;
    @Range(min = -180, max = 180, message = "out of range")
    double lon;
}



package com.kinopoisklite.movieguide.model.dto;

import com.kinopoisklite.movieguide.model.AgeRating;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MovieDTO {
    private String title;
    private Integer releaseYear;
    private Integer duration;
    private String country;
    private String genre;
    private String ratingCategory;
    private String description;
    private Cover cover;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Cover {
        private String fileName;
        private String content;
    }
}

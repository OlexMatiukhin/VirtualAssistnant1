package dto;


public record GeoResponseDTO( String name,
                              Double lat,
                              Double lon,
                              String country,
                              String state) {}

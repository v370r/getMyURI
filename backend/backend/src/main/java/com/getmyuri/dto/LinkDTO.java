package com.getmyuri.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LinkDTO {
    private String alias;
    private String link;
    @Builder.Default
    private int clicks = 0;
    @Builder.Default
    private String password = "";
    @Builder.Default
    private List<LinkDTO> sublinks = new ArrayList<>();
    private String username;
}

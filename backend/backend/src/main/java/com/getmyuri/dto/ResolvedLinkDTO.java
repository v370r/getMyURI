package com.getmyuri.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResolvedLinkDTO {
    private String alias;
    private String link;
    private String username;
    private boolean passwordProtected;
}

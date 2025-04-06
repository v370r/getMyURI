package com.getmyuri.service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.getmyuri.dto.LinkDTO;
import com.getmyuri.dto.ResolvedLinkDTO;
import com.getmyuri.model.DataObjectFormat;
import com.getmyuri.repository.LinkRepository;
import com.getmyuri.util.DateCalculations;

@Service
public class LinkService {
    @Autowired
    private LinkRepository linkRepository;

    @Value("${shortlink.default.ttl}")
    private String defaultTtlString;

    public DataObjectFormat createLink(LinkDTO linkDTO) {
        String[] aliasParts = linkDTO.getAlias().split("/");

        Date startTime = linkDTO.getStartTime() != null ? linkDTO.getStartTime() : new Date();
        String futureExpiry = linkDTO.getExpiresAt() != null ? linkDTO.getExpiresAt() : defaultTtlString;
        Date expiresAt = DateCalculations.calculateExpiryFrom(startTime, futureExpiry);
        if (aliasParts.length == 1) {
            DataObjectFormat root = DataObjectFormat.builder().alias(aliasParts[0]).link(linkDTO.getLink())
                    .password(linkDTO.getPassword()).username(linkDTO.getUsername()).location(linkDTO.getLocation())
                    .radius(linkDTO.getRadius()).startTime(startTime).expiresAt(expiresAt)
                    .build();
            return linkRepository.save(root);

        }

        String rootAlias = aliasParts[0];
        Optional<DataObjectFormat> existingRoot = linkRepository.findByAlias(rootAlias);
        DataObjectFormat root = existingRoot.orElse(null);

        if ((root == null) && aliasParts.length == 1) {
            root = DataObjectFormat.builder().alias(aliasParts[0]).link(linkDTO.getLink())
                    .password(linkDTO.getPassword()).username(linkDTO.getUsername()).radius(linkDTO.getRadius())
                    .build();
        } else if (root == null) {
            root = DataObjectFormat.builder().alias(aliasParts[0]).username(linkDTO.getUsername()).build();
        }

        List<DataObjectFormat> currentLevel = root.getSublinks();
        DataObjectFormat current = null;

        for (int i = 1; i < aliasParts.length; i++) {
            String currentAlias = aliasParts[i];

            if (currentLevel == null) {
                currentLevel = new ArrayList<>();
                if (i == 1) {
                    root.setSublinks(currentLevel);
                } else if (current != null) {
                    current.setSublinks(currentLevel);
                }
            }

            Optional<DataObjectFormat> existing = currentLevel.stream()
                    .filter(sublink -> sublink.getAlias().equals(currentAlias))
                    .findFirst();

            if (existing.isPresent()) {
                current = existing.get();
            } else {
                current = DataObjectFormat.builder().alias(currentAlias).build();
                currentLevel.add(current);
            }

            if (i == aliasParts.length - 1) {
                current.setLink(linkDTO.getLink());
                current.setPassword(linkDTO.getPassword());
                current.setClicks(0);
                current.setLocation(linkDTO.getLocation());
                current.setRadius(linkDTO.getRadius());
                current.setStartTime(startTime);
                current.setExpiresAt(expiresAt);
            }

            currentLevel = current.getSublinks();
        }

        return linkRepository.save(root);
    }

    public Optional<ResolvedLinkDTO> getLink(String aliasPath) {
        String[] parts = aliasPath.split("/");
        if (parts.length == 0)
            return Optional.empty();

        Optional<DataObjectFormat> rootOpt = linkRepository.findByAlias(parts[0]);
        if (rootOpt.isEmpty())
            return Optional.empty();

        DataObjectFormat current = traverseSublinks(rootOpt.get().getSublinks(),
                Arrays.copyOfRange(parts, 1, parts.length));
        if (current.getStartTime() != null && new Date().before(current.getStartTime())) {
            return Optional.empty(); // or throw 403 if in controller
        }
        if (parts.length == 1)
            return Optional.of(ResolvedLinkDTO.builder().alias(aliasPath).link(rootOpt.get().getLink()).build());

        if ((current != null)
                && (current.getUsername() == null)) {
            return Optional.of(
                    ResolvedLinkDTO.builder()
                            .alias(aliasPath)
                            .link(current.getLink())
                            .password(current.getPassword())
                            .username(rootOpt.get().getUsername())
                            .location(current.getLocation())
                            .radius(current.getRadius())
                            .build());
        }

        return Optional.empty();
    }

    private DataObjectFormat traverseSublinks(List<DataObjectFormat> list, String[] path) {
        if (path.length == 0)
            return null;
        DataObjectFormat current = null;
        for (String part : path) {
            if (list == null)
                return null;
            current = list.stream().filter(l -> l.getAlias().equals(part)).findFirst().orElse(null);
            if (current == null)
                return null;
            list = current.getSublinks();
        }
        return current;
    }

    public List<DataObjectFormat> getAllLinks() {
        return linkRepository.findAll();
    }

    public Optional<DataObjectFormat> getLinkByAlias(String alias) {
        return linkRepository.findByAlias(alias);
    }

    public boolean deleteLink(String id) {
        if (linkRepository.existsById(id)) {
            linkRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
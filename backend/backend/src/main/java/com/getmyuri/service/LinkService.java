package com.getmyuri.service;

import java.time.Instant;
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
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import com.getmyuri.dto.LinkDTO;
import com.getmyuri.dto.ResolvedLinkDTO;
import com.getmyuri.model.ClickMetric;
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

        Date startTime = linkDTO.getStartTime() != null ? linkDTO.getStartTime() : Date.from(Instant.now());
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

        if (rootOpt.get().getSublinks() == null) {
            return Optional.of(ResolvedLinkDTO.builder()
                    .alias(aliasPath)
                    .link(rootOpt.get().getLink())
                    .password(rootOpt.get().getPassword())
                    .username(rootOpt.get().getUsername())
                    .location(rootOpt.get().getLocation())
                    .radius(rootOpt.get().getRadius())
                    .build());

        }

        DataObjectFormat current = traverseSublinks(rootOpt.get().getSublinks(),
                Arrays.copyOfRange(parts, 1, parts.length));
        // if (current.getStartTime() != null &&
        // Date.from(Instant.now()).before(current.getStartTime())) {
        // System.out.println(Date.from(Instant.now()));
        // return Optional.empty(); // or throw 403 if in controller
        // }
        if (current.getStartTime() != null) {
            Instant now = Instant.now();
            Date nowUtc = Date.from(now);

            System.out.println(" UTC Now        : " + nowUtc);
            System.out.println(" Start Time UTC : " + current.getStartTime());

            if (nowUtc.before(current.getStartTime())) {
                System.out.println(" Link is not yet active!");
                return Optional.empty(); // or throw 403
            }
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

    public boolean deleteLinkByAliasPath(String aliasPath) {
        String[] parts = aliasPath.split("/");
        if (parts.length == 0)
            return false;

        Optional<DataObjectFormat> rootOpt = linkRepository.findByAlias(parts[0]);
        if (rootOpt.isEmpty())
            return false;

        if (parts.length == 1) {
            // deleting root alias
            linkRepository.deleteById(rootOpt.get().getId());
            return true;
        }

        DataObjectFormat root = rootOpt.get();
        List<DataObjectFormat> currentLevel = root.getSublinks();
        DataObjectFormat parent = null;
        DataObjectFormat toDelete = null;

        for (int i = 1; i < parts.length; i++) {
            String currentAlias = parts[i];

            if (currentLevel == null)
                return false;

            Optional<DataObjectFormat> match = currentLevel.stream()
                    .filter(link -> link.getAlias().equals(currentAlias))
                    .findFirst();

            if (match.isEmpty())
                return false;

            parent = toDelete;
            toDelete = match.get();

            if (i < parts.length - 1) {
                currentLevel = toDelete.getSublinks();
            }
        }

        if (toDelete != null && parent != null && parent.getSublinks() != null) {
            parent.getSublinks().remove(toDelete);
        } else if (toDelete != null && root.getSublinks() != null) {
            root.getSublinks().remove(toDelete);
        }

        linkRepository.save(root); // persist the updated root tree
        return true;
    }

}
package com.pushpak.prod_ready_feature.controllers;

import com.pushpak.prod_ready_feature.entities.PostEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/audit")
public class AuditController {
    @Autowired
    private EntityManager entityManager;


    @GetMapping("/posts/{postId}")
    List<PostEntity> getPostRevisions(@PathVariable Long postId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List<Number> revisions = auditReader.getRevisions(PostEntity.class, postId);
        return revisions
                .stream()
                .map(revision -> auditReader.find(PostEntity.class, postId, revision))
                .collect(Collectors.toList());
    }

}

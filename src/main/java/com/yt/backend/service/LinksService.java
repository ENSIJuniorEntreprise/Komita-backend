package com.yt.backend.service;

import com.yt.backend.model.Links;
import com.yt.backend.repository.LinksRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import java.util.NoSuchElementException;


@Service
@AllArgsConstructor
public class LinksService {
    private final LinksRepository linksRepository;
    public List<Links> getAllLinks() {
        return linksRepository.findAll();
    }
    public Links createLink(Links link) {
        return linksRepository.save(link);
    }
    public Optional<Links> getLinkById(Long id) {

        return linksRepository.findById(id);
    }
    public Links updateLink(Long id, Links newLinkData) {
        Links existingLink = linksRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Link not found with id: " + id));

        // Update the existing link with new data
        existingLink.setFacebookURL(newLinkData.getFacebookURL());
        existingLink.setInstagramURL(newLinkData.getInstagramURL());
        existingLink.setWebsiteURL(newLinkData.getWebsiteURL());

        return linksRepository.save(existingLink);
    }
    public void deleteLink(Long id) {
        linksRepository.deleteById(id);
    }

}

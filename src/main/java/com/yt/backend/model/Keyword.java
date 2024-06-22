package com.yt.backend.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="keyword_table")
public class Keyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String keywordName;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "Service_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Service service;
    // Ensure that the stemmedKeyword is updated whenever the keywordName is set
    @PrePersist
    @PreUpdate
    private void updateStemmedKeyword() {
        this.keywordName = stemKeyword(keywordName);
    }
    private String stemKeyword(String keyword) {
        List<String> stemmedWords = new ArrayList<>();

        try (StringReader reader = new StringReader(keyword);
             StandardTokenizer tokenizer = new StandardTokenizer()) {

            tokenizer.setReader(reader);

            // Use PorterStemFilter for stemming
            TokenStream tokenStream = new PorterStemFilter(tokenizer);

            // Get the stemmed words
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                stemmedWords.add(charTermAttribute.toString());
            }
            tokenStream.end();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // If there are stemmed words, return the first one; otherwise, return the original keyword
        return !stemmedWords.isEmpty() ? stemmedWords.get(0) : keyword;
    }
}

package io.azrina.nlp.summarizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.stats.Counters;
import edu.stanford.nlp.util.CoreMap;

/**
 * <h1>Summarizer</h1> A class representing a text summarizer based on a Text
 * Teaser algorithm (https://github.com/MojoJolo/textteaser). The algorithm
 * generate summary by selecting 'most important' sentences based on series of
 * features.
 *
 *
 **/
public class Summarizer {

    Parser parser;

    // default constructor
    public Summarizer() {

        this.parser = new Parser();
    }

    // constructor with param
    public Summarizer(String lang){
        this.parser = new Parser(lang);
    }

    /**
     * This method is used to summarize a text.
     *
     * @param String title Text title
     * @param String document Text to be summarized
     * @param String int numSentences Number of maximum sentences in the summary
     * @param String delimiter Delimiter between sentences in generated summary
     * @param String language
     * @return String Summarized text
     * @throws IOException
     */
    public String summarize(String title, String document, int numSentences, String delimiter)
            throws IOException {
        // Getting sentences from the document
        List<CoreMap> sentences = parser.getSentences(document);

        // Get terms and its frequencies
        Counter<String> tfs = parser.getTermFrequencies(sentences);

        // Get top ten terms
        List<String> topTenTerm = Counters.topKeys(tfs, 10);

        // Get top keywords and its computed score, sorted by score
        Map<String, Double> topKeywords = getArticleScore(topTenTerm, tfs);
        // System.out.println(topKeywords);

        // Get words from title
        List<String> title_words = parser.tokenize(title, false);

        // Compute and rank sentences
        List<CoreMap> ranked_sentences = rankSentences(tfs, sentences, title_words, topKeywords, sentences.size());

        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < (ranked_sentences.size() < numSentences ? ranked_sentences.size() : numSentences); i++) {
            ret.append(ranked_sentences.get(i));
            if (delimiter != null) {
                ret.append(delimiter);
            } else {
                ret.append(delimiter);
            }
        }

        return ret.toString();
    }

    /**
     * This method is used to rank sentences and get the top n-th one as the
     * document's summary.
     *
     * @param Counter termFrequencies List of terms in a text and number of its occurence in a document
     * @param List<CoreMap> sentences List of sentences in a document
     * @param List<String> titleWords List of unique keywords in document's title
     * @param Map<String, Double> topKeywords List of n most frequent keywords in a document and its score
     * @param int sentence_count Total number of sentences in a document
     * @return List<CoreMap> Sorted list of sentence based on its score
     */
    public List<CoreMap> rankSentences(Counter<String> termFrequencies, List<CoreMap> sentences,
                                               List<String> titleWords, Map<String, Double> topKeywords, int sentence_count) {
        Collections.sort(sentences, new SentenceComparator(termFrequencies, titleWords, topKeywords, sentence_count));
        return sentences;
    }

    /**
     * Implementation of Comparator<CoreMap> to rank sentences based on
     * sentence's score which is calculate from series of feature.
     */
    private class SentenceComparator implements Comparator<CoreMap> {

        // Local variable declaration
        private final Counter<String> termFrequencies;
        private final List<String> titleWords;
        private final Map<String, Double> topKeywords;
        private final int sentence_count;

        /**
         * Sentence comparator constructor.
         *
         * @param termFrequencies
         * @param titleWords
         * @param topKeywords
         * @param sentence_count
         */
        private SentenceComparator(Counter<String> termFrequencies, List<String> titleWords,
                                  Map<String, Double> topKeywords, int sentence_count) {
            this.termFrequencies = termFrequencies;
            this.titleWords = titleWords;
            this.topKeywords = topKeywords;
            this.sentence_count = sentence_count;
        }

        @Override
        public int compare(CoreMap o1, CoreMap o2){
            int value = (int) Math.round(score(o2) - score(o1));

            if (value > 0) {
                return 1;
            } else if (value < 0) {
                return -1;
            } else {
                return 0;
            }

        }

        /**
         * Method to calculate sentence score from a given sentence.
         *
         * @param CoreMap sentence A preprocessed sentence
         * @return Double Sentence's score
         * @throws IOException
         */
        private double score(CoreMap sentence)  {

            // Get sentence index (positon) and length
            int sentence_index = sentence.get(CoreAnnotations.SentenceIndexAnnotation.class);

            // Get words from sentence
            List<String> sentence_words = parser.tokenize(sentence.toString(), false);

            // Calculate Summation-Based Selection feature
            Double sbs_feature = getSbs(sentence_words, topKeywords);
            // System.out.println(sbs_feature);

            // Calculate Density-Based Selection feature
            Double dbs_feature = getDbs(termFrequencies, sentence_words, topKeywords);
            // System.out.println(dbs_feature);

            // Get title feature
            Double title_feature = parser.getTitleFeature(titleWords, sentence_words);
            // System.out.println(title_feature);

            // Get sentence length feature
            Double length_feature = parser.getLengthFeature(sentence_words);
            // System.out.println(length_feature);

            // Get sentence position feature
            Double position_feature = parser.getPositionFeature(sentence_index, sentence_count);
            // System.out.println(position_feature);

            // Get keywords frequency feature
            Double keywords_frequency = (sbs_feature + dbs_feature) / 2.0 * 10.0;
            // System.out.println(keywworords_frequency);

            // Get total score
            Double total_score = ((title_feature * 1.5) + (keywords_frequency * 2.0) + (length_feature * 0.5)
                    + (position_feature * 1.0) / 4.0);

            // System.out.println(total_score);

            return total_score * 10000;
        }
    }

    /**
     * Method to get score of keyword from given list of keywords, based on
     * number of its occurence in a text, normalized by number of total terms in
     * the document.
     *
     * @param List<String> keywords Keywords whose count needs to be searched
     * @param Counter<term> termFrequencies List of keywords and its frequencies
     * @return Map<String,Double> Map of keywords from param and its score
     */
    public static Map<String, Double> getArticleScore(List<String> keywords, Counter<String> termFrequencies) {

        Map<String, Double> topKeywords = new HashMap<String, Double>();
        Double termCount = termFrequencies.totalCount();
        if (keywords != null) {
            for (String keyword : keywords) {
                Double count = termFrequencies.getCount(keyword);
                Double articleScore = count / termCount;
                topKeywords.put(keyword.toLowerCase(), articleScore);
            }
        }

        return topKeywords;
    }

    /**
     * Method to calculate SBS (Summation-Based Feature) from a given sentence.
     * Checked if sentence contains top ten keywords, and sum its article score.
     *
     * @param sentence_words List<String> List of words in the sentence
     * @param topKeywords List<String> List of top keywords along with its article score
     * @return Double SBS score
     */
    public static Double getSbs(List<String> sentence_words, Map<String, Double> topKeywords) {
        Double score = 0.0;
        if (sentence_words.size() > 0) {
            for (String word : sentence_words) {
                Double keyword_score = topKeywords.get(word.toLowerCase());
                if (keyword_score != null) {
                    score += keyword_score;
                }
            }
        }
        if (sentence_words.size() == 0) return 0.0;
        return (1.0 / sentence_words.size()) * score;
    }

    /**
     * Method to calculate Density-Based-Selection feature from a given
     * sentence, by taking into account the occurences of top keywords in the
     * sentence and the distance between keywords in the sentence.
     *
     * @param termFrequencies Counter<String> List of terms in a text and number of its occurence in a document
     * @param sentence_words List<String> List of words in a sentence
     * @param topKeywords Map<String, Double> List of n most frequent keywords in a document and its score
     * @return DBS Score
     */
    public static Double getDbs(Counter<String> termFrequencies, List<String> sentence_words,
                                 Map<String, Double> topKeywords) {
        Double summ = 0.0;

        List<String> topTenTermSorted = Counters.topKeys(termFrequencies, 10);
        List<String> intersection = new ArrayList<String>(sentence_words);
        intersection.retainAll(topTenTermSorted);
        int k = intersection.size();

        String first_word = "";
        String second_word = "";
        int first_idx = 0;
        int second_idx = 0;
        int counter = 0;

        for (String word : sentence_words) {

            Double keyword_score = topKeywords.get(word.toLowerCase());
            if (keyword_score != null) { // if current word is one of the top keyword
                if (first_word == "") {
                    first_word = word;
                    first_idx = counter;
                } else {
                    second_word = first_word;
                    second_idx = first_idx;
                    first_word = word;
                    first_idx = counter;

                    int distance = (first_idx - second_idx);
                    summ += ((topKeywords.get(first_word) * topKeywords.get(second_word)) / Math.pow(distance, 2));
                }
            }

            counter += 1;
        }

        if (k == 0) return 0.0;
        return (1.0 / k * (k + 1.0)) * summ;
    }

}
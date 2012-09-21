package de.tudarmstadt.ukp.similarity.experiments.wordchoice;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.similarity.experiments.wordchoice.util.WordChoiceAnnotationPair;
import de.tudarmstadt.ukp.similarity.experiments.wordchoice.util.WordChoiceAnnotationPairFactory;
import de.tudarmstadt.ukp.similarity.type.WordChoiceProblem;
import de.tudarmstadt.ukp.similarity.type.WordPair;

/**
 * Writes all unique word pair annotations that can occur in the word choice problems.
 *
 * @author zesch
 *
 */
public class WordChoiceWordPairAnnotator extends JCasAnnotator_ImplBase {
    private List<WordChoiceAnnotationPair> wordChoiceAnnotationPairList;

    @Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
        wordChoiceAnnotationPairList = new ArrayList<WordChoiceAnnotationPair>();
    }

    @Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

        // possible pairs are only within word choice problems between the target and the candidates
        // candidates map to sentences in a word choice problem
        for (WordChoiceProblem wcp : JCasUtil.select(jcas, WordChoiceProblem.class)) { 
            wordChoiceAnnotationPairList.addAll( WordChoiceAnnotationPairFactory.getWordChoiceAnnotationPairs(jcas, wcp));
        }

        // within word choice problems possible pairs are single tokens or whole candidates
        for (WordChoiceAnnotationPair wcap : wordChoiceAnnotationPairList) {
            String targetString = wcap.getTargetString();
            POS targetPos       = wcap.getTargetPos();

            // also add the single lemmas
            if (wcap.getCandidateLemmas().size() > 1) {
                for (int i=0; i<wcap.getCandidateLemmas().size(); i++) {
                    addWordPairAnnotation(jcas, targetString, targetPos, wcap.getCandidateStrings().get(i), wcap.getCandidatePos().get(i));
                }
            }

            // always add the full candidate
            // the pos of the full candidate is either the POS of the only lemma in it, or the POS of the head (rightmost POS in a list of lemmas)
            addWordPairAnnotation(jcas, targetString, targetPos, StringUtils.join(wcap.getCandidateStrings(), ""), wcap.getCandidatePos().get(wcap.getCandidatePos().size()-1));

            // Add indicator word pairs that are used in the evaluator to auto-detect whether a measure is a relatedness or a distance measure.
            // As we do not know which target string are present in the knowledge source, we add all possible indicator pairs.
            // This doubles the number of annotations, but almost all measurs shortcut the computation in case of equal terms, so it should not hurt performance too much.
            addWordPairAnnotation(jcas, targetString, targetPos, targetString, targetPos);
        }
    }

    private void addWordPairAnnotation(JCas jcas, String targetString, POS targetPos, String candidateString, POS candidatePos) {
        WordPair wordPairAnnotation = new WordPair(jcas);
        wordPairAnnotation.setWord1(targetString);
        wordPairAnnotation.setWord2(candidateString);
        wordPairAnnotation.setPos1(targetPos);
        wordPairAnnotation.setPos2(candidatePos);

        wordPairAnnotation.addToIndexes();
    }
}
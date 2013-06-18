/*******************************************************************************
 * Copyright 2013
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 ******************************************************************************/
package de.tudarmstadt.ukp.similarity.experiments.rte.attic;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;
import de.tudarmstadt.ukp.similarity.entailment.type.EntailmentClassificationOutcome;

public class RteResultsPrinter
    extends JCasAnnotator_ImplBase
{

    @Override
    public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {
        try {
            JCas view1 = jcas.getView(CombinationReader.VIEW_1);
            JCas view2 = jcas.getView(CombinationReader.VIEW_2);
            
            System.out.println(view1.getDocumentText());
            System.out.println(view2.getDocumentText());
            
            EntailmentClassificationOutcome outcome = JCasUtil.selectSingle(jcas, EntailmentClassificationOutcome.class);
            System.out.println(outcome);
        }
        catch (CASException e) {
            throw new AnalysisEngineProcessException(e);
        }
    }
}
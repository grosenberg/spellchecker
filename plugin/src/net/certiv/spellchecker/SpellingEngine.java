/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package net.certiv.spellchecker;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.spelling.ISpellingEngine;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;

import net.certiv.spellchecker.engine.ISpellCheckEngine;
import net.certiv.spellchecker.engine.ISpellChecker;
import net.certiv.spellchecker.engine.ISpellEvent;
import net.certiv.spellchecker.engine.ISpellEventListener;
import net.certiv.spellchecker.etc.PreferenceConstants;

/**
 * Internal abstract spelling engine, subclasses provide a content-type specific
 * implementation.
 *
 * @since 3.1
 */
public abstract class SpellingEngine implements ISpellingEngine {

	/**
	 * {@link ISpellEvent}listener that forwards events as
	 * {@link org.eclipse.ui.texteditor.spelling.SpellingProblem}.
	 */
	protected static class SpellEventListener implements ISpellEventListener {

		/** Spelling problem collector */
		private ISpellingProblemCollector fCollector;

		/**
		 * The document.
		 *
		 * @since 3.3
		 */
		private IDocument fDocument;

		private int fProblemsThreshold;
		private int fProblemCount;

		/**
		 * Initialize with the given spelling problem collector.
		 *
		 * @param collector the spelling problem collector
		 * @param document the document
		 */
		public SpellEventListener(ISpellingProblemCollector collector, IDocument document) {
			fCollector = collector;
			fDocument = document;
			fProblemsThreshold = store.getInt(PreferenceConstants.SPELLING_PROBLEMS_THRESHOLD);
		}

		@Override
		public void handle(ISpellEvent event) {
			if (isProblemsThresholdReached()) return;
			fProblemCount++;
			fCollector.accept(new JavaSpellingProblem(event, fDocument));
		}

		boolean isProblemsThresholdReached() {
			return fProblemCount >= fProblemsThreshold;
		}
	}

	private static IPreferenceStore store;

	public SpellingEngine(IPreferenceStore store) {
		SpellingEngine.store = store;
	}

	@Override
	public void check(IDocument document, IRegion[] regions, SpellingContext context,
			ISpellingProblemCollector collector, IProgressMonitor monitor) {
		if (collector != null) {
			final ISpellCheckEngine spellingEngine = SpellCheckEngine.getInstance(store);
			ISpellChecker checker = spellingEngine.getSpellChecker();
			if (checker != null) {
				check(document, regions, checker, collector, monitor);
			}
		}
	}

	/**
	 * Spell checks the given document regions with the given arguments.
	 *
	 * @param document the document
	 * @param regions the regions
	 * @param checker the spell checker
	 * @param collector the spelling problem collector
	 * @param monitor the progress monitor, can be {@code null}
	 */
	protected abstract void check(IDocument document, IRegion[] regions, ISpellChecker checker,
			ISpellingProblemCollector collector, IProgressMonitor monitor);
}

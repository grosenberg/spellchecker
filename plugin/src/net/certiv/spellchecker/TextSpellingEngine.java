/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
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
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;

import net.certiv.spellchecker.engine.ISpellChecker;

/**
 * Text spelling engine
 *
 * @since 3.1
 */
public class TextSpellingEngine extends SpellingEngine {

	public TextSpellingEngine(IPreferenceStore store) {
		super(store);
	}

	@Override
	protected void check(IDocument document, IRegion[] regions, ISpellChecker checker,
			ISpellingProblemCollector collector, IProgressMonitor monitor) {
		SpellEventListener listener = new SpellEventListener(collector, document);
		for (IRegion region : regions) {
			if (monitor != null && monitor.isCanceled()) return;
			if (listener.isProblemsThresholdReached()) return;
			checker.execute(listener, new SpellCheckIterator(document, region, checker.getLocale()));
		}
	}
}

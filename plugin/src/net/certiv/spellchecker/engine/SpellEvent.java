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
package net.certiv.spellchecker.engine;

import java.util.Set;

/**
 * Spell event fired for words detected by a spell check iterator.
 *
 * @since 3.0
 */
public class SpellEvent implements ISpellEvent {

	private final ISpellChecker fChecker;
	private final int fBegin;
	private final int fEnd;
	private final String fWord;
	private final boolean fSentence;
	private final boolean fMatch;

	/**
	 * Creates a new spell event.
	 *
	 * @param checker The spell checker that causes the event
	 * @param word The word that causes the event
	 * @param begin The begin index of the word in the spell checkable medium
	 * @param end The end index of the word in the spell checkable medium
	 * @param sentence {@code true} iff the word starts a new sentence, {@code false}
	 *            otherwise
	 * @param match {@code true} iff the word was found in the dictionary, {@code false}
	 *            otherwise
	 */
	protected SpellEvent(final ISpellChecker checker, final String word, final int begin, final int end,
			final boolean sentence, final boolean match) {
		fChecker = checker;
		fEnd = end;
		fBegin = begin;
		fWord = word;
		fSentence = sentence;
		fMatch = match;
	}

	@Override
	public final int getBegin() {
		return fBegin;
	}

	@Override
	public final int getEnd() {
		return fEnd;
	}

	@Override
	public final Set<RankedWordProposal> getProposals() {
		return fChecker.getProposals(fWord, fSentence);
	}

	@Override
	public final String getWord() {
		return fWord;
	}

	@Override
	public final boolean isMatch() {
		return fMatch;
	}

	@Override
	public final boolean isStart() {
		return fSentence;
	}
}

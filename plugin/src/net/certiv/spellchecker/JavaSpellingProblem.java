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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.TextInvocationContext;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;

import net.certiv.spellchecker.engine.ISpellCheckEngine;
import net.certiv.spellchecker.engine.ISpellChecker;
import net.certiv.spellchecker.engine.ISpellEvent;
import net.certiv.spellchecker.engine.RankedWordProposal;
import net.certiv.spellchecker.etc.IJavaCompletionProposal;
import net.certiv.spellchecker.etc.PreferenceConstants;
import net.certiv.spellchecker.messages.JavaUIMessages;
import net.certiv.spellchecker.messages.Messages;

/**
 * A {@link SpellingProblem} that adapts a {@link ISpellEvent}.
 * <p>
 * TODO: remove {@link ISpellEvent} notification mechanism
 */
public class JavaSpellingProblem extends SpellingProblem {

	private ISpellEvent fSpellEvent;
	private IDocument fDocument;

	/**
	 * Initialize with the given spell event.
	 *
	 * @param spellEvent the spell event
	 * @param document the document
	 */
	public JavaSpellingProblem(ISpellEvent spellEvent, IDocument document) {
		Assert.isLegal(document != null);
		Assert.isLegal(spellEvent != null);
		fSpellEvent = spellEvent;
		fDocument = document;
	}

	@Override
	public int getOffset() {
		return fSpellEvent.getBegin();
	}

	@Override
	public int getLength() {
		return fSpellEvent.getEnd() - fSpellEvent.getBegin() + 1;
	}

	@Override
	public String getMessage() {
		if (isSentenceStart() && isDictionaryMatch())
			return Messages.format(JavaUIMessages.Spelling_error_case_label, new String[] { fSpellEvent.getWord() });

		return Messages.format(JavaUIMessages.Spelling_error_label, new String[] { fSpellEvent.getWord() });
	}

	@Override
	public ICompletionProposal[] getProposals() {
		return getProposals(null);
	}

	@Override
	public ICompletionProposal[] getProposals(IQuickAssistInvocationContext context) {
		String[] arguments = getArguments();
		if (arguments == null) return new ICompletionProposal[0];

		if (arguments[0].indexOf('&') != -1 && isIgnoringAmpersand()) return new ICompletionProposal[0]; // no proposals
																										 // for now

		final int threshold = Activator.getDefault().getPreferenceStore()
				.getInt(PreferenceConstants.SPELLING_PROPOSAL_THRESHOLD);
		int size = 0;
		List<RankedWordProposal> proposals = null;

		RankedWordProposal proposal = null;
		IJavaCompletionProposal[] result = null;
		int index = 0;

		boolean fixed = false;
		boolean match = false;
		boolean sentence = false;

		final ISpellCheckEngine engine = SpellCheckEngine.getInstance();
		final ISpellChecker checker = engine.getSpellChecker();

		if (checker != null) {

			if (context == null) context = new TextInvocationContext(null, getOffset(), getLength());
			else context = new TextInvocationContext(context.getSourceViewer(), getOffset(), getLength());

			// FIXME: this is a pretty ugly hack
			fixed = arguments[0].charAt(0) == IHtmlTagConstants.HTML_TAG_PREFIX
					|| arguments[0].charAt(0) == IJavaDocTagConstants.JAVADOC_TAG_PREFIX;

			if ((sentence && match) && !fixed) result = new IJavaCompletionProposal[] {
					new ChangeCaseProposal(arguments, getOffset(), getLength(), context, engine.getLocale()) };
			else {

				proposals = new ArrayList<>(checker.getProposals(arguments[0], sentence));
				size = proposals.size();

				if (threshold > 0 && size > threshold) {
					Collections.sort(proposals);
					proposals = proposals.subList(size - threshold - 1, size - 1);
					size = proposals.size();
				}

				boolean extendable = !fixed ? (checker.acceptsWords() || AddWordProposal.canAskToConfigure()) : false;
				result = new IJavaCompletionProposal[size + (extendable ? 3 : 2)];

				for (index = 0; index < size; index++) {

					proposal = proposals.get(index);
					result[index] = new WordCorrectionProposal(proposal.getText(), arguments, getOffset(), getLength(),
							context, proposal.getRank());
				}

				if (extendable) result[index++] = new AddWordProposal(arguments[0], context);

				result[index++] = new WordIgnoreProposal(arguments[0], context);
				result[index++] = new DisableSpellCheckingProposal(context);
			}
		}

		return result;
	}

	private boolean isIgnoringAmpersand() {
		return Activator.getDefault().getPreferenceStore()
				.getBoolean(PreferenceConstants.SPELLING_IGNORE_AMPERSAND_IN_PROPERTIES);
	}

	public String[] getArguments() {
		String prefix = ""; //$NON-NLS-1$
		String postfix = ""; //$NON-NLS-1$
		String word;
		try {
			word = fDocument.get(getOffset(), getLength());
		} catch (BadLocationException e) {
			return null;
		}

		try {

			IRegion line = fDocument.getLineInformationOfOffset(getOffset());
			prefix = fDocument.get(line.getOffset(), getOffset() - line.getOffset());
			int postfixStart = getOffset() + getLength();
			postfix = fDocument.get(postfixStart, line.getOffset() + line.getLength() - postfixStart);

		} catch (BadLocationException exception) {}
		return new String[] { word, prefix, postfix,
				isSentenceStart() ? Boolean.toString(true) : Boolean.toString(false),
				isDictionaryMatch() ? Boolean.toString(true) : Boolean.toString(false) };
	}

	/**
	 * Returns {@code true} iff the corresponding word was found in the dictionary.
	 * <p>
	 * NOTE: to be removed, see {@link #getProposals()}
	 *
	 * @return {@code true} iff the corresponding word was found in the dictionary
	 */
	public boolean isDictionaryMatch() {
		return fSpellEvent.isMatch();
	}

	/**
	 * Returns {@code true} iff the corresponding word starts a sentence.
	 * <p>
	 * NOTE: to be removed, see {@link #getProposals()}
	 *
	 * @return {@code true} iff the corresponding word starts a sentence
	 */
	public boolean isSentenceStart() {
		return fSpellEvent.isStart();
	}
}

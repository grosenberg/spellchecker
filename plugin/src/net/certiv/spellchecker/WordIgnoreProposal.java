/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package net.certiv.spellchecker;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;

import net.certiv.spellchecker.engine.ISpellCheckEngine;
import net.certiv.spellchecker.engine.ISpellChecker;
import net.certiv.spellchecker.etc.IJavaCompletionProposal;
import net.certiv.spellchecker.etc.IProposalRelevance;
import net.certiv.spellchecker.etc.JavaPluginImages;
import net.certiv.spellchecker.messages.JavaUIMessages;
import net.certiv.spellchecker.messages.Messages;

/**
 * Proposal to ignore the word during the current editing session.
 *
 * @since 3.0
 */
public class WordIgnoreProposal implements IJavaCompletionProposal {

	/** The invocation context */
	private IQuickAssistInvocationContext fContext;

	/** The word to ignore */
	private String fWord;

	/**
	 * Creates a new spell ignore proposal.
	 *
	 * @param word The word to ignore
	 * @param context The invocation context
	 */
	public WordIgnoreProposal(final String word, final IQuickAssistInvocationContext context) {
		fWord = word;
		fContext = context;
	}

	/*
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.
	 * text.IDocument)
	 */
	@Override
	public final void apply(final IDocument document) {

		final ISpellCheckEngine engine = SpellCheckEngine.getInstance();
		final ISpellChecker checker = engine.getSpellChecker();

		if (checker != null) {
			checker.ignoreWord(fWord);
			ISourceViewer sourceViewer = fContext.getSourceViewer();
			if (sourceViewer != null) SpellingProblem.removeAll(sourceViewer, fWord);
		}
	}

	/*
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo(
	 * )
	 */
	@Override
	public String getAdditionalProposalInfo() {
		return Messages.format(JavaUIMessages.Spelling_ignore_info,
				new String[] { WordCorrectionProposal.getHtmlRepresentation(fWord) });
	}

	/*
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
	 */
	@Override
	public final IContextInformation getContextInformation() {
		return null;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
	 */
	@Override
	public String getDisplayString() {
		return Messages.format(JavaUIMessages.Spelling_ignore_label, new String[] { fWord });
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	@Override
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_OBJS_NLS_NEVER_TRANSLATE);
	}

	/*
	 * @see org.eclipse.jdt.ui.text.java.IJavaCompletionProposal#getRelevance()
	 */
	@Override
	public final int getRelevance() {
		return IProposalRelevance.WORD_IGNORE;
	}

	/*
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.
	 * jface.text.IDocument)
	 */
	@Override
	public final Point getSelection(final IDocument document) {
		return new Point(fContext.getOffset(), fContext.getLength());
	}
}

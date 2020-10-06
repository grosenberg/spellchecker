/*******************************************************************************
 * Copyright (c) 2007, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package net.certiv.spellchecker;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.spelling.SpellingService;

import net.certiv.spellchecker.etc.IJavaCompletionProposal;
import net.certiv.spellchecker.etc.IProposalRelevance;
import net.certiv.spellchecker.etc.JavaPluginImages;
import net.certiv.spellchecker.messages.JavaUIMessages;

/**
 * Proposal to disable spell checking.
 *
 * @since 3.3
 */
public class DisableSpellCheckingProposal implements IJavaCompletionProposal {

	/** The invocation context */
	private IQuickAssistInvocationContext fContext;

	/**
	 * Creates a new proposal.
	 *
	 * @param context the invocation context
	 */
	public DisableSpellCheckingProposal(IQuickAssistInvocationContext context) {
		fContext = context;
	}

	/*
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.
	 * text.IDocument)
	 */
	@Override
	public final void apply(final IDocument document) {
		IPreferenceStore store = EditorsUI.getPreferenceStore();
		store.setValue(SpellingService.PREFERENCE_SPELLING_ENABLED, false);
	}

	/*
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo(
	 * )
	 */
	@Override
	public String getAdditionalProposalInfo() {
		return JavaUIMessages.Spelling_disable_info;
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
		return JavaUIMessages.Spelling_disable_label;
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
		return IProposalRelevance.DISABLE_SPELL_CHECKING;
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

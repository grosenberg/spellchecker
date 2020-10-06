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
 * Event fired by spell checkers.
 *
 * @since 3.0
 */
public interface ISpellEvent {

	/**
	 * Returns the begin index of the incorrectly spelled word.
	 *
	 * @return The begin index of the word
	 */
	int getBegin();

	/**
	 * Returns the end index of the incorrectly spelled word.
	 *
	 * @return The end index of the word
	 */
	int getEnd();

	/**
	 * Returns the proposals for the incorrectly spelled word.
	 *
	 * @return Array of proposals for the word
	 */
	Set<RankedWordProposal> getProposals();

	/**
	 * Returns the incorrectly spelled word.
	 *
	 * @return The incorrect word
	 */
	String getWord();

	/**
	 * Was the incorrectly spelled word found in the dictionary?
	 *
	 * @return {@code true} iff the word was found, {@code false} otherwise
	 */
	boolean isMatch();

	/**
	 * Does the incorrectly spelled word start a new sentence?
	 *
	 * @return {@code true<code> iff the word starts a new sentence, <code>false}
	 *             otherwise
	 */
	boolean isStart();
}

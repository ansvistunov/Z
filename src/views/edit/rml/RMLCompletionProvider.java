package views.edit.rml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.html.AttributeCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;
import org.fife.ui.autocomplete.MarkupTagCompletion;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.autocomplete.Util;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;

public class RMLCompletionProvider extends DefaultCompletionProvider {

	/**
	 * A mapping of tag names to their legal attributes.
	 */
	private Map<String, List<AttributeCompletion>> tagToAttrs;
	
	/**
	 * A mapping of tag names and prop names to their legal values.
	 */
	private Map<String, List<AttributeCompletion>> tagPropsToValues;

	/**
	 * Whether the text last grabbed via
	 * {@link #getAlreadyEnteredText(JTextComponent)} was an HTML tag name.
	 *
	 * @see #lastTagName
	 */
	private boolean isTagName;

	
	private boolean isPropName = false;
	
	/**
	 * Returns the last tag name grabbed via
	 * {@link #getAlreadyEnteredText(JTextComponent)}. This value is only valid
	 * if {@link #isTagName} is <code>false</code>.
	 */
	private String lastTagName;
	
	/**
	 * Last (current) properties name
	 * 
	 * */
	private String lastPropName;
	/**
	 * {,} - начало тэга RML
	 * */
	static final int RMLSeparator = Token.SEPARATOR; 
	/**
	 * tag RML
	 * */
	static final int RMLTag = Token.RESERVED_WORD;
	
	
	/**
	 *  RML им€ свойства
	 * */
	static final int RMLPropName = 	Token.IDENTIFIER;
	/**
	 * Constructor.
	 */
	public RMLCompletionProvider() {

		initCompletions();

		tagToAttrs = new HashMap<String, List<AttributeCompletion>>();
		
		tagPropsToValues = new HashMap<String, List<AttributeCompletion>>();
		
		for (Completion comp : completions) {
			MarkupTagCompletion c = (MarkupTagCompletion) comp;
			String tag = c.getName();
			List<AttributeCompletion> attrs = new ArrayList<AttributeCompletion>();
			tagToAttrs.put(tag.toLowerCase(), attrs);
			for (int j = 0; j < c.getAttributeCount(); j++) {
				Parameter param = c.getAttribute(j);
				attrs.add(new AttributeCompletion(this, param));
				///
				List<AttributeCompletion> values = new ArrayList<AttributeCompletion>();
				tagPropsToValues.put(tag.toLowerCase()+"_"+param.getName().toLowerCase(), values);
				StringTokenizer st = new StringTokenizer(param.getType(),"|");
				while(st.hasMoreTokens()) {
					values.add(new AttributeCompletion(this, new FunctionCompletion.Parameter(param.getType(),st.nextToken()))); //TODO здесь должен быть цикл
				}
				///
			}
		}

		
		
		setAutoActivationRules(false, "{=");

	}

	/**
	 * This nasty hack is just a hook for subclasses (e.g.
	 * <code>PhpCompletionProvider</code>) to be able to get at the
	 * <code>DefaultCompletionProvider</code> implementation.
	 *
	 * @param comp
	 *            The text component.
	 * @return The text, or <code>null</code> if none.
	 */
	protected String defaultGetAlreadyEnteredText(JTextComponent comp) {
		return super.getAlreadyEnteredText(comp);
	}

	/**
	 * Locates the name of the tag a given offset is in. This method assumes
	 * that the caller has already concluded that <code>offs</code> is in fact
	 * inside a tag, and that there is a little "text" just before it.
	 *
	 * @param doc
	 *            The document being parsed.
	 * @param tokenList
	 *            The token list for the current line.
	 * @param offs
	 *            The offset into the document to check.
	 * @return Whether a tag name was found.
	 */
	private final boolean findLastTagNameBefore(RSyntaxDocument doc, Token tokenList, int offs) {

		lastTagName = null;
		boolean foundOpenTag = false;

		for (Token t = tokenList; t != null; t = t.getNextToken()) {
			if (t.containsPosition(offs)) {
				break;
			} else if (t.getType() == RMLTag) {
				lastTagName = t.getLexeme();
			} else if (t.getType() == RMLSeparator) {
				lastTagName = null;
				foundOpenTag = t.isSingleChar('{');
				t = t.getNextToken();
				// Don't check for MARKUP_TAG_NAME to allow for unknown
				// tag names, such as JSP tags
				if (t != null && !t.isWhitespace()) {
					lastTagName = t.getLexeme();
				}
			}
		}

		if (lastTagName == null && !foundOpenTag) {

			Element root = doc.getDefaultRootElement();
			int prevLine = root.getElementIndex(offs) - 1;
			while (prevLine >= 0) {
				tokenList = doc.getTokenListForLine(prevLine);
				for (Token t = tokenList; t != null; t = t.getNextToken()) {
					if (t.getType() == RMLTag) {
						lastTagName = t.getLexeme();
					} else if (t.getType() == RMLSeparator) {
						lastTagName = null;
						foundOpenTag = t.isSingleChar('{');
						t = t.getNextToken();
						// Don't check for MARKUP_TAG_NAME to allow for unknown
						// tag names, such as JSP tags
						if (t != null && !t.isWhitespace()) {
							lastTagName = t.getLexeme();
						}
					}
				}
				if (lastTagName != null || foundOpenTag) {
					break;
				}
				prevLine--;
			}

		}

		return lastTagName != null;

	}
	
	
	
	
	// после свойства ќЅя«ј“≈Ћ№Ќќ должен идти =
	private final boolean findLastPropNameBefore(RSyntaxDocument doc, Token tokenList, int offs) {

		lastPropName = null;
		boolean foundOpenTag = false;
		
		
		
		for (Token t = tokenList; t != null; t = t.getNextToken()) {
			if (t.containsPosition(offs)) {
				break;
			}else if (t.getType() == Token.OPERATOR){
				isPropName = true; //последним непробельным токеном должен быть "="
			}else if (t.getType() == RMLPropName) {
				lastPropName = t.getLexeme();
				isPropName = false;
			} else if (t.getType() == RMLSeparator) {
				lastPropName = null;
				foundOpenTag = t.isSingleChar('{');
				t = t.getNextToken();
				isPropName = false;
				// Don't check for MARKUP_TAG_NAME to allow for unknown
				// tag names, such as JSP tags
			}else if (t.getType() == Token.WHITESPACE || t.getType() == Token.NULL){
				//ничего не делаем
				
			}else {
				//дл€ всех других токенов - сбрасываем флаг
				isPropName = false;
			}
		}

		if (lastPropName == null && !foundOpenTag) { //не нашли на текущей строке

			Element root = doc.getDefaultRootElement();
			int prevLine = root.getElementIndex(offs) - 1;
			while (prevLine >= 0) {
				tokenList = doc.getTokenListForLine(prevLine);
				for (Token t = tokenList; t != null; t = t.getNextToken()) {
					if (t.getType() == RMLPropName) {
						lastPropName = t.getLexeme();
					}
				}
				if (lastPropName != null) {
					break;
				}
				prevLine--;
			}

		}

		System.out.println("propname="+lastPropName);
		return lastPropName != null;

	}
	
	
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAlreadyEnteredText(JTextComponent comp) {

		isTagName = true;
		lastTagName = null;
		lastPropName = null;

		String text = super.getAlreadyEnteredText(comp);
		if (text != null) {

			// Check token just before caret (i.e., what we're typing after).
			int dot = comp.getCaretPosition();
			if (dot > 0) { // Must go back 1

				RSyntaxTextArea textArea = (RSyntaxTextArea) comp;

				try {

					int line = textArea.getLineOfOffset(dot - 1);
					Token list = textArea.getTokenListForLine(line);

					if (list != null) { // Always true?

						Token t = RSyntaxUtilities.getTokenAtOffset(list, dot - 1);

						if (t == null) { // Not sure this ever happens...
							text = null;
						}

						// If we're completing just after a tag delimiter,
						// only offer suggestions for the "inside" of tags,
						// e.g. after "<" and "</".
						else if (t.getType() == RMLSeparator) {
							if (!isTagOpeningToken(t)) {
								text = null;
							}
						}

						// If we're completing after whitespace, we must
						// determine whether we're "inside" a tag.
						else if (t.getType() == Token.WHITESPACE) {
							if (!insideMarkupTag(textArea, list, line, dot)) {
								text = null;
							}
						}

						// Otherwise, only auto-complete if we're appending
						// to text already recognized as a markup tag name or
						// attribute (e.g. we know we're in a tag).
						else if (t.getType() != RMLPropName && t.getType() != RMLTag && t.getType() !=Token.OPERATOR) { //TODO Token.MARKUP_TAG_ATTRIBUTE!!!

							// We also have the case where "dot" was the start
							// offset of the line, so the token list we got was
							// actually for the previous line. So here we must
							// also check for an EOL token that means "we're in
							// a tag."
							// HACK: Using knowledge of HTML/JSP/PHPTokenMaker!
							if (t.getType() > -1 || t.getType() < -9) {
								text = null;
							}

						}

						if (text != null) { // We're going to auto-complete
							t = getTokenBeforeOffset(list, dot - text.length());
							isTagName = t != null && isTagOpeningToken(t);
							if (!isTagName) {
								RSyntaxDocument doc = (RSyntaxDocument) textArea.getDocument();
								findLastTagNameBefore(doc, list, dot);
								findLastPropNameBefore(doc, textArea.getTokenListForLine(line), dot);
								
							}
						}

					}

				} catch (BadLocationException ble) {
					ble.printStackTrace();
				}

			}

			else {
				text = null; // No completions for offset 0
			}

		}

		return text;

	}

	/**
	 * Returns the attributes that can be code-completed for the specified tag.
	 * Subclasses can override this method to handle more than the standard set
	 * of HTML 5 tags and their attributes.
	 *
	 * @param tagName
	 *            The tag whose attributes are being code-completed.
	 * @return A list of attributes, or <code>null</code> if the tag is not
	 *         recognized.
	 */
	protected List<AttributeCompletion> getAttributeCompletionsForTag(String tagName) {
		return tagToAttrs.get(lastTagName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Completion> getCompletionsImpl(JTextComponent comp) {

		List<Completion> retVal = new ArrayList<Completion>();
		String text = getAlreadyEnteredText(comp);
		List<? extends Completion> completions = getTagCompletions();
		if (lastTagName != null) {
			lastTagName = lastTagName.toLowerCase();
			completions = getAttributeCompletionsForTag(lastTagName);
			if (lastPropName!=null && isPropName) {
				lastPropName = lastPropName.toLowerCase();
				completions = tagPropsToValues.get(lastTagName+"_"+lastPropName);
			}
			// System.out.println(completions);
		}

		if (text != null && completions != null) {

			@SuppressWarnings("unchecked")
			int index = Collections.binarySearch(completions, text, comparator);
			if (index < 0) {
				index = -index - 1;
			}

			while (index < completions.size()) {
				Completion c = completions.get(index);
				if (Util.startsWithIgnoreCase(c.getInputText(), text)) {
					retVal.add(c);
					index++;
				} else {
					break;
				}
			}

		}

		return retVal;

	}

	/**
	 * Returns the completions for the basic tag set. This method is here so
	 * subclasses can add to it if they provide additional tags (i.e. JSP).
	 *
	 * @return The completions for the standard tag set.
	 */
	protected List<Completion> getTagCompletions() {
		return this.completions;
	}

	/**
	 * Returns the token before the specified offset.
	 *
	 * @param tokenList
	 *            A list of tokens containing the offset.
	 * @param offs
	 *            The offset.
	 * @return The token before the offset, or <code>null</code> if the offset
	 *         was the first offset in the token list (or not in the token list
	 *         at all, which would be an error).
	 */
	private static final Token getTokenBeforeOffset(Token tokenList, int offs) {
		if (tokenList != null) {
			Token prev = tokenList;
			for (Token t = tokenList.getNextToken(); t != null; t = t.getNextToken()) {
				if (t.containsPosition(offs)) {
					return prev;
				}
				prev = t;
			}
		}
		return null;
	}

	/**
	 * Calls {@link #loadFromXML(String)} to load all standard HTML completions.
	 * Subclasses can override to also load additional standard tags (i.e. JSP's
	 * <code>jsp:*</code> tags).
	 */
	protected void initCompletions() {
		try {
			
			
			
			loadFromXML("data/rml.xml");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Returns whether the given offset is inside a markup tag (and not in
	 * string content, such as an attribute value).
	 *
	 * @param textArea
	 *            The text area being parsed.
	 * @param list
	 *            The list of tokens for the current line (the line containing
	 *            <code>offs</code>.
	 * @param line
	 *            The index of the line containing <code>offs</code>.
	 * @param offs
	 *            The offset into the text area's content to check.
	 * @return Whether the offset is inside a markup tag.
	 */
	private static final boolean insideMarkupTag(RSyntaxTextArea textArea, Token list, int line, int offs) {

		int inside = -1; // -1 => not determined, 0 => false, 1 => true

		for (Token t = list; t != null; t = t.getNextToken()) {
			if (t.containsPosition(offs)) {
				break;
			}
			switch (t.getType()) {
			case RMLTag:
			case RMLPropName:
				inside = 1;
				break;
			case RMLSeparator:
				inside = t.isSingleChar('}') ? 0 : 1;
				break;
			}
		}

		// Still not determined - check how previous line ended.
		if (inside == -1) {
			if (line == 0) {
				inside = 0;
			} else {
				RSyntaxDocument doc = (RSyntaxDocument) textArea.getDocument();
				int prevLastToken = doc.getLastTokenTypeOnLine(line - 1);
				// HACK: This code uses the insider knowledge that token types
				// -1 through -9 mean "something inside a tag" for all
				// applicable markup languages (HTML, JSP, and PHP)!
				// TODO: Remove knowledge of internal token types.
				if (prevLastToken <= -1 && prevLastToken >= -9) {
					inside = 1;
				} else {
					inside = 0;
				}
			}
		}

		return inside == 1;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAutoActivateOkay(JTextComponent tc) {

		boolean okay = super.isAutoActivateOkay(tc);
		
		if (true) return okay; //TODO поправить

		if (okay) {

			RSyntaxTextArea textArea = (RSyntaxTextArea) tc;
			int dot = textArea.getCaretPosition();

			try {

				int line = textArea.getLineOfOffset(dot);
				Token list = textArea.getTokenListForLine(line);

				if (list != null) { // Always true?
					return !insideMarkupTag(textArea, list, line, dot);
				}

			} catch (BadLocationException ble) {
				ble.printStackTrace();
			}

		}

		return okay;
	}

	/**
	 * Returns whether this token's text is "<" or "</". It is assumed that
	 * whether this is a markup delimiter token is checked elsewhere.
	 *
	 * @param t
	 *            The token to check.
	 * @return Whether it is a tag opening token.
	 */
	private static final boolean isTagOpeningToken(Token t) {
		return t.isSingleChar('{'); 
				//|| (t.length() == 2 && t.charAt(0) == '<' && t.charAt(1) == '/');
	}

}
//
// Author: Yves Lafon <ylafon@w3.org>
//
// (c) COPYRIGHT MIT, ERCIM, Keio, Beihang, 2017.
// Please first read the full copyright statement in file COPYRIGHT.html
package org.w3c.css.properties.css3.counterstyle;

import org.w3c.css.util.ApplContext;
import org.w3c.css.util.InvalidParamException;
import org.w3c.css.values.CssExpression;
import org.w3c.css.values.CssIdent;
import org.w3c.css.values.CssTypes;
import org.w3c.css.values.CssValue;
import org.w3c.css.values.CssValueList;

import java.util.ArrayList;

import static org.w3c.css.values.CssOperator.SPACE;

/**
 * @spec https://www.w3.org/TR/2015/CR-css-counter-styles-3-20150611/#descdef-counter-style-symbols
 */
public class CssSymbols extends org.w3c.css.properties.css.counterstyle.CssSymbols {

	/**
	 * Create a new CssSymbols
	 */
	public CssSymbols() {
		value = initial;  // this is wrong...
	}

	/**
	 * Creates a new CssSymbols
	 *
	 * @param expression The expression for this property
	 * @throws org.w3c.css.util.InvalidParamException
	 *          Expressions are incorrect
	 */
	public CssSymbols(ApplContext ac, CssExpression expression, boolean check)
			throws InvalidParamException {
		CssIdent ident;
		CssValue val;
		char op;
		ArrayList<CssValue> values = new ArrayList<>();

		setByUser();

		while (!expression.end()) {
			val = expression.getValue();
			op = expression.getOperator();

			switch (val.getType()) {
				case CssTypes.CSS_IMAGE:
				case CssTypes.CSS_STRING:
					values.add(val);
					break;
				case CssTypes.CSS_IDENT:
					if (CssIdent.isCssWide((CssIdent) val)) {
						throw new InvalidParamException("value",
								val.toString(),
								getPropertyName(), ac);
					}
					values.add(val);
					break;
				default:
					throw new InvalidParamException("value",
							val.toString(),
							getPropertyName(), ac);
			}
			if (op != SPACE) {
				throw new InvalidParamException("operator", op,
						getPropertyName(), ac);
			}
			expression.next();
		}
		if (values.isEmpty()) {
			throw new InvalidParamException("unrecognize", ac);
		}
		value = (values.size() == 1) ? values.get(0) : new CssValueList(values);
	}

	public CssSymbols(ApplContext ac, CssExpression expression)
			throws InvalidParamException {
		this(ac, expression, false);
	}

}


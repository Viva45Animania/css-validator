// $Id$
// Author: Yves Lafon <ylafon@w3.org>
//
// (c) COPYRIGHT MIT, ERCIM and Keio University, 2012.
// Please first read the full copyright statement in file COPYRIGHT.html
package org.w3c.css.properties.css3;

import org.w3c.css.parser.CssStyle;
import org.w3c.css.properties.css.CssProperty;
import org.w3c.css.util.ApplContext;
import org.w3c.css.util.InvalidParamException;
import org.w3c.css.values.CssCheckableValue;
import org.w3c.css.values.CssExpression;
import org.w3c.css.values.CssIdent;
import org.w3c.css.values.CssLayerList;
import org.w3c.css.values.CssTypes;
import org.w3c.css.values.CssValue;
import org.w3c.css.values.CssValueList;

import java.util.ArrayList;

import static org.w3c.css.values.CssOperator.COMMA;
import static org.w3c.css.values.CssOperator.SPACE;

/**
 * @spec http://www.w3.org/TR/2012/WD-css3-transitions-20120403/#transition
 */
public class CssAnimation extends org.w3c.css.properties.css.CssAnimation {

	CssAnimationName cssAnimationName = null;
	CssAnimationDuration cssAnimationDuration = null;
	CssAnimationTimingFunction cssAnimationTimingFunction = null;
	CssAnimationDelay cssAnimationDelay = null;
	CssAnimationDirection cssAnimationDirection = null;
	CssAnimationFillMode cssAnimationFillMode = null;
	CssAnimationIterationCount cssAnimationIterationCount = null;

	/**
	 * Create a new CssAnimation
	 */
	public CssAnimation() {
		value = initial;
		cssAnimationDelay = new CssAnimationDelay();
		cssAnimationDuration = new CssAnimationDuration();
		cssAnimationName = new CssAnimationName();
		cssAnimationTimingFunction = new CssAnimationTimingFunction();
		cssAnimationIterationCount = new CssAnimationIterationCount();
		cssAnimationFillMode = new CssAnimationFillMode();
		cssAnimationDirection = new CssAnimationDirection();
	}

	/**
	 * Creates a new CssAnimation
	 *
	 * @param expression The expression for this property
	 * @throws org.w3c.css.util.InvalidParamException
	 *          Expressions are incorrect
	 */
	public CssAnimation(ApplContext ac, CssExpression expression, boolean check)
			throws InvalidParamException {
		setByUser();
		CssValue val;
		ArrayList<CssValue> values;
		CssExpression single_layer = null;
		char op;

		values = new ArrayList<CssValue>();
		// we just accumulate values and check at validation
		while (!expression.end()) {
			val = expression.getValue();
			op = expression.getOperator();

			if (inherit.equals(val)) {
				if (expression.getCount() > 1) {
					throw new InvalidParamException("value", val,
							getPropertyName(), ac);
				}
				value = inherit;
				expression.next();
				return;
			}
			if (single_layer == null) {
				single_layer = new CssExpression();
			}
			// we will check later
			single_layer.addValue(val);
			single_layer.setOperator(op);
			expression.next();

			if (!expression.end()) {
				// incomplete value followed by a comma... it's complete!
				if (op == COMMA) {
					single_layer.setOperator(SPACE);
					values.add(checkLayer(ac, single_layer, this));
					single_layer = null;
				} else if ((op != SPACE)) {
					throw new InvalidParamException("operator",
							((new Character(op)).toString()), ac);
				}
			}
		}
		// if we reach the end in a value that can come in pair
		if (single_layer != null) {
			values.add(checkLayer(ac, single_layer, this));
		}
		if (values.size() == 1) {
			value = values.get(0);
			CssAnimationValue v = (CssAnimationValue) value;
			if (v.delay != null) {
				cssAnimationDelay = new CssAnimationDelay();
				cssAnimationDelay.value = v.delay;
			}
			if (v.duration != null) {
				cssAnimationDuration = new CssAnimationDuration();
				cssAnimationDuration.value = v.duration;
			}
			if (v.name != null) {
				cssAnimationName = new CssAnimationName();
				cssAnimationName.value = v.name;
			}
			if (v.itercount != null) {
				cssAnimationIterationCount = new CssAnimationIterationCount();
				cssAnimationIterationCount.value = v.itercount;
			}
			if (v.timingfunc != null) {
				cssAnimationTimingFunction = new CssAnimationTimingFunction();
				cssAnimationTimingFunction.value = v.timingfunc;
			}
			if (v.direction != null) {
				cssAnimationDirection = new CssAnimationDirection();
				cssAnimationDirection.value = v.direction;
			}
			if (v.fillmode != null) {
				cssAnimationFillMode = new CssAnimationFillMode();
				cssAnimationFillMode.value = v.fillmode;
			}
		} else {
			// TODO explode the layers for addToStyle...
			value = new CssLayerList(values);
		}
	}

	public CssAnimation(ApplContext ac, CssExpression expression)
			throws InvalidParamException {
		this(ac, expression, false);
	}


	/**
	 * Add this property to the CssStyle.
	 *
	 * @param style The CssStyle
	 */

	public void addToStyle(ApplContext ac, CssStyle style) {
		super.addToStyle(ac, style);
		if (cssAnimationDelay != null) {
			cssAnimationDelay.addToStyle(ac, style);
		}
		if (cssAnimationDuration != null) {
			cssAnimationDuration.addToStyle(ac, style);
		}
		if (cssAnimationName != null) {
			cssAnimationName.addToStyle(ac, style);
		}
		if (cssAnimationTimingFunction != null) {
			cssAnimationTimingFunction.addToStyle(ac, style);
		}
		if (cssAnimationIterationCount != null) {
			cssAnimationIterationCount.addToStyle(ac, style);
		}
		if (cssAnimationDirection != null) {
			cssAnimationDirection.addToStyle(ac, style);
		}
		if (cssAnimationFillMode != null) {
			cssAnimationFillMode.addToStyle(ac, style);
		}
	}

	private CssAnimationValue checkLayer(ApplContext ac,
										  CssExpression expression,
										  CssProperty caller)
			throws InvalidParamException {
		CssAnimationValue v = new CssAnimationValue();
		// here we know we have the right operator, we just need to check
		// everything else.
		if (expression.getCount() > 7) {
			throw new InvalidParamException("unrecognize", ac);
		}
		CssValue val;
		while (!expression.end()) {
			val = expression.getValue();
			switch (val.getType()) {
				case CssTypes.CSS_NUMBER:
					if (v.itercount == null) {
						CssCheckableValue num = val.getCheckableValue();
						num.checkPositiveness(ac, this);
						v.itercount = val;
						break;
					}
					// itercount filled => exit
					throw new InvalidParamException("value",
							val.toString(),
							caller.getPropertyName(), ac);
				case CssTypes.CSS_TIME:
					if (v.duration == null) {
						// we got a duration (first parsable time)
						CssCheckableValue t = val.getCheckableValue();
						t.warnPositiveness(ac, this);
						v.duration = val;
						break;
					}
					if (v.delay == null) {
						v.delay = val;
						break;
					}
					// we already got two times => exit
					throw new InvalidParamException("value",
							val.toString(),
							caller.getPropertyName(), ac);
				case CssTypes.CSS_FUNCTION:
					if (v.timingfunc == null) {
						CssAnimationTimingFunction.parseFunctionValues(ac, val, this);
						v.timingfunc = val;
						break;
					}
					// unknown function
					throw new InvalidParamException("value",
							val.toString(),
							caller.getPropertyName(), ac);
				case CssTypes.CSS_IDENT:
					CssIdent ident = (CssIdent) val;
					CssIdent match;
					if (inherit.equals(ident)) {
						if (expression.getCount() != 1) {
							throw new InvalidParamException("unrecognize", ac);
						}
						v.name = inherit;
						break;
					}
					if (v.timingfunc == null) {
						match = CssAnimationTimingFunction.getAllowedIdent(ident);
						if (match != null) {
							v.timingfunc = match;
							break;
						}
					}
					if (v.direction == null) {
						match = CssAnimationDirection.getAllowedIdent(ident);
						if (match != null) {
							v.direction = match;
							break;
						}
					}
					if (v.fillmode == null) {
						match = CssAnimationFillMode.getAllowedIdent(ident);
						if (match != null) {
							v.fillmode = match;
							break;
						}
					}
					if (v.itercount == null) {
						match = CssAnimationIterationCount.getAllowedIdent(ident);
						if (match != null) {
							v.itercount = match;
							break;
						}
					}
					if (v.name == null) {
						v.name = CssAnimationName.getAllowedIdent(ac, ident);
						break;
					}
					// already set, let it fail
				default:
					throw new InvalidParamException("value",
							val.toString(),
							caller.getPropertyName(), ac);
			}
			expression.next();
		}
		return v;
	}

	private class CssAnimationValue extends CssValueList {

		CssValue delay = null;
		CssValue duration = null;
		CssValue name = null;
		CssValue timingfunc = null;
		CssValue itercount = null;
		CssValue direction = null;
		CssValue fillmode = null;


		public String toString() {
			boolean doneFirst = false;
			StringBuilder sb = new StringBuilder();
			if (name != null) {
				sb.append(name);
				doneFirst = true;
			}
			if (duration != null) {
				if (doneFirst) {
					sb.append(' ');
				}
				sb.append(duration);
				doneFirst = true;
				if (delay != null) {
					sb.append(' ').append(delay);
				}
			}
			if (timingfunc != null) {
				if (doneFirst) {
					sb.append(' ');
				}
				sb.append(timingfunc);
			}
			if (itercount != null) {
				if (doneFirst) {
					sb.append(' ');
				}
				sb.append(itercount);
			}
			if (direction != null) {
				if (doneFirst) {
					sb.append(' ');
				}
				sb.append(direction);
			}
			if (fillmode != null) {
				if (doneFirst) {
					sb.append(' ');
				}
				sb.append(fillmode);
			}

			return sb.toString();
		}
	}
}


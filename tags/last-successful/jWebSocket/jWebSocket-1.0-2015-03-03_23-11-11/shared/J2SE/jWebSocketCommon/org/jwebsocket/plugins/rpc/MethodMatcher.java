//	---------------------------------------------------------------------------
//	jWebSocket MethodMatcher (Community Edition, CE)
//	---------------------------------------------------------------------------
//	Copyright 2010-2014 Innotrade GmbH (jWebSocket.org)
//	Alexander Schulze, Germany (NRW)
//
//	Licensed under the Apache License, Version 2.0 (the "License");
//	you may not use this file except in compliance with the License.
//	You may obtain a copy of the License at
//
//	http://www.apache.org/licenses/LICENSE-2.0
//
//	Unless required by applicable law or agreed to in writing, software
//	distributed under the License is distributed on an "AS IS" BASIS,
//	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//	See the License for the specific language governing permissions and
//	limitations under the License.
//	---------------------------------------------------------------------------
package org.jwebsocket.plugins.rpc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.jwebsocket.plugins.rpc.util.ListConverter;
import org.jwebsocket.plugins.rpc.util.MethodMatcherConversionException;
import org.jwebsocket.token.BaseTokenizable;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;

/**
 *
 * @author Alexander Schulze
 */
@SuppressWarnings("rawtypes")
public class MethodMatcher {

	private Object[] mMethodParameters;
	private Method mMethod;

	/**
	 *
	 * @param aMethod
	 */
	public MethodMatcher(Method aMethod) {
		mMethod = aMethod;
	}

	/**
	 * Should only be used after isMethodMatchingAgainstParameter() is invoked
	 * and returns true.
	 *
	 * @return the list of the parameters you need to invoke the method.
	 */
	public Object[] getMethodParameters() {
		return mMethodParameters;
	}

	/**
	 * Match the given parameters again the method If the parameters match, cast
	 * and save the parameters in mMethodParameters object. Once this method is
	 * called (and if true), getMethodParameters() will contains all the correct
	 * parameters you need to invoke the method.
	 *
	 * @param aArgs
	 * @return true if the method is matching, false otherwise.
	 */
	public boolean isMethodMatchingAgainstParameter(List aArgs) {
		Class[] lParametersType = mMethod.getParameterTypes();
		if (aArgs == null) {
			if (lParametersType.length == 0) {
				mMethodParameters = null;
				return true;
			}
			return false;
		}
		mMethodParameters = new Object[lParametersType.length];
		// We are looking for a method with the same number of parameters
		if (getNumberOfValidParameters(lParametersType) == aArgs.size()) {
			// We make sure the types of the method match
			try {
				int k = -1;
				for (int j = 0; j < lParametersType.length; j++) {
					k++;
					Class lParameterType = lParametersType[j];
					// Try to guess the object type.
					// Only support primitive type+wrapper, String, List and Token.
					// String and Token
					// TODO: with the new Tokens, we don't need anymore to check every argument.
					// Just need to take care of Float/Double and specific types.
					Object lArg = aArgs.get(k);
					// check if argument is a specific class
					if (lArg instanceof Map) {
						Map lMap = (Map) lArg;
						// look for class identifier
						String lClassName = (String) lMap.get(BaseTokenizable.ARG_CLASS_ID);
						if (lClassName != null) {
							// ITokenizable lTokenizable =
							try {
								Class lClass = Class.forName(lClassName);
								Class[] lCA = new Class[]{};
								Constructor lConstr = lClass.getConstructor(lCA);
								lConstr.setAccessible(true);
								Object lCustObj = lConstr.newInstance();
								if (lCustObj instanceof BaseTokenizable) {
									Token lToken = TokenFactory.createToken();
									lToken.setMap(lMap);
									((BaseTokenizable) lCustObj).readFromToken(lToken);
								}
								mMethodParameters[j] = lCustObj;
							} catch (Exception lEx) {
								throw new MethodMatcherConversionException(
										lEx.getClass().getSimpleName() + ": "
										+ lEx.getMessage());
							}
						}
					}

					if (mMethodParameters[j] == null) {
						if (lParameterType == String.class) {
							mMethodParameters[j] = (String) lArg;
						} else if (lParameterType == Token.class) {
							mMethodParameters[j] = (Token) lArg;
						} // Special support for primitive type
						else if (lParameterType == int.class) {
							mMethodParameters[j] = (Integer) lArg;
						} else if (lParameterType == boolean.class) {
							mMethodParameters[j] = (Boolean) lArg;
						} else if (lParameterType == double.class) {
							mMethodParameters[j] = (Double) lArg;
						} // Wrappers of primitive types
						else if (lParameterType == Integer.class) {
							mMethodParameters[j] = (Integer) lArg;
						} else if (lParameterType == Boolean.class) {
							mMethodParameters[j] = (Boolean) lArg;
						} else if (lParameterType == Double.class) {
							mMethodParameters[j] = (Double) lArg;
						} else if (lParameterType == List.class) {
							// try to guess which type of object should be on the List:
							Type genericParameterType = mMethod.getGenericParameterTypes()[j];
							mMethodParameters[j] = ListConverter.convert((List) lArg, genericParameterType);
						} else if (specificMatching(lParameterType, j)) {
							k--;
						} // any other object are *not* supported !
						else {
//							if (mLog.isDebugEnabled()) {
//								mLog.debug("Can't extract an object with type '" + lParameterType.getName() + "' in a Token Object");
//							}
							throw new MethodMatcherConversionException(
									"Can't extract an object with type '"
									+ lParameterType.getName()
									+ "' in a Token Object");
						}
					}
				}
				// If no exception has been thrown, so the method mat
				// We return the list of lArgs ready for the request.
				return true;
			} catch (Exception e) {
				// TODO: shouldn't catch a global Exception e here. Need to change it in the ListConverter class
				// That's the wrong method.
				return false;
			}
		}
		return false;
	}

	/**
	 * @param aListOfParametersType
	 * @return the number of valid paramters inside a method. Specific parameter
	 * can be ignored here. (For instance, a WebSocketConnector is ignored in
	 * ServerMethodMatcher)
	 */
	protected int getNumberOfValidParameters(Class[] aListOfParametersType) {
		return aListOfParametersType.length;
	}

	/**
	 * Set the parameter "aIndice" to aParameter (in the list of parameter used
	 * to invoke the method)
	 *
	 * @param aIndice
	 * @param aParameter
	 */
	protected void setMethodParameters(int aIndice, Object aParameter) {
		mMethodParameters[aIndice] = aParameter;
	}

	/**
	 * Specific method if you need to add a specific matching type for the
	 * server or a client. For instance, specificMatching is redefined in
	 * ServerMethodMatcher to detects if a WebSocketConnector is on of the
	 * parameters.
	 *
	 * @param lParameterType
	 * @param aIndice
	 * @return
	 */
	protected boolean specificMatching(Class lParameterType, int aIndice) {
		return false;
	}
}

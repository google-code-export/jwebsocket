// ---------------------------------------------------------------------------
// jWebSocket - AttributeDefinition (Community Edition, CE)
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
package org.jwebsocket.plugins.jmx.configdefinition;

import javax.management.modelmbean.ModelMBeanAttributeInfo;

/**
 * Class that allows to define the attributes of the plugins or classes to
 * export and their metadata.
 *
 * @author Lisdey Perez Hernandez
 */
public class AttributeDefinition extends FeatureDefinition {

	private Class mType;
	private Boolean mIsReadable = false;
	private Boolean mIsWritable = false;
	private Boolean mIsBoolean = false;

	/**
	 * The class default constructor
	 */
	public AttributeDefinition() {
	}

	/**
	 *
	 * @param aType
	 * @param aName
	 * @param aDescription
	 * @param aIsReadable
	 * @param aIsWritable
	 * @param aIsBoolean
	 */
	public AttributeDefinition(Class aType, String aName, String aDescription,
			Boolean aIsReadable, Boolean aIsWritable, Boolean aIsBoolean) {
		super(aName, aDescription);
		this.mType = aType;
		this.mIsReadable = aIsReadable;
		this.mIsWritable = aIsWritable;
		this.mIsBoolean = aIsBoolean;
	}

	/**
	 *
	 * @return Class
	 */
	public Class getType() {
		if (this.mType != null) {
			return this.mType;
		} else {
			throw new IllegalArgumentException("The attribute type must not be "
					+ "null.");
		}
	}

	/**
	 *
	 * @param aType
	 */
	public void setType(Class aType) {
		if (aType != null) {
			this.mType = aType;
		} else {
			throw new IllegalArgumentException("The attribute type must not be "
					+ "null.");
		}
	}

	/**
	 *
	 * @return Boolean
	 */
	public Boolean isReadable() {
		return mIsReadable;
	}

	/**
	 *
	 * @param aReadable
	 */
	public void setReadable(Boolean aReadable) {
		this.mIsReadable = aReadable;
	}

	/**
	 *
	 * @return Boolean
	 */
	public Boolean isWritable() {
		return mIsWritable;
	}

	/**
	 *
	 * @param aWritable
	 */
	public void setWritable(Boolean aWritable) {
		this.mIsWritable = aWritable;
	}

	/**
	 *
	 * @return Boolean
	 */
	public Boolean isBoolean() {
		return mIsBoolean;
	}

	/**
	 *
	 * @param aIsBoolean
	 */
	public void setIsBoolean(Boolean aIsBoolean) {
		this.mIsBoolean = aIsBoolean;
	}

	/**
	 * Create the metadata of the attribute definition.
	 *
	 * @return ModelMBeanAttributeInfo
	 */
	public ModelMBeanAttributeInfo createMBeanAttributeInfo() {
		if (this.mType != null) {
			return new ModelMBeanAttributeInfo(super.getName(), mType.getName(),
					super.getDescription(), mIsReadable, mIsWritable, mIsBoolean);
		} else {
			throw new IllegalArgumentException("The attribute type must not be null.");
		}
	}
}

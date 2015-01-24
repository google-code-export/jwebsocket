//	---------------------------------------------------------------------------
//	jWebSocket - ClassPathUpdater (Community Edition, CE)
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
package org.jwebsocket.dynamicsql.platform.derby;

import java.sql.Types;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.platform.derby.DerbyBuilder;
import org.apache.ddlutils.util.Jdbc3Utils;

/**
 *
 * @author Marcos Antonio Gonzalez Huerta
 */
public class Derby107Builder extends DerbyBuilder {

	/**
	 * Creates a new builder instance.
	 *
	 * @param platform The plaftform this builder belongs to
	 */
	public Derby107Builder(Platform platform) {
		super(platform);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param column
	 * @return
	 */
	@Override
	protected String getNativeDefaultValue(Column column) {
		if ((column.getTypeCode() == Types.BIT)) {
			return getDefaultValueHelper().convert(column.getDefaultValue(), column.getTypeCode(), Types.SMALLINT).toString();
		} else if ((Jdbc3Utils.supportsJava14JdbcTypes() && (column.getTypeCode() == Jdbc3Utils.determineBooleanTypeCode()))) {
			return column.getDefaultValue();
		} else {
			return super.getNativeDefaultValue(column);
		}
	}
}

package org.jwebsocket.plugins.itemstorage.item;

import java.util.HashMap;
import java.util.Map;
import javolution.util.FastMap;
import org.jwebsocket.plugins.itemstorage.api.IItemDefinition;
import org.jwebsocket.token.Token;
import org.springframework.util.Assert;

/**
 *
 * @author kyberneees
 */
public class ItemDefinition implements IItemDefinition {

	public static final String ATTR_TYPE = "type";
	public static final String ATTR_PK = "pk_attr";
	public static final String ATTR_ATTR_TYPES = "attr_types";
	private Map<String, Object> mData = new FastMap<String, Object>().shared();
	public static final String ITEM_TYPE_REGEXP = "^[a-zA-Z]+([a-zA-Z0-9]+)*";
	public static final String ATTR_NAME_REGEXP = ITEM_TYPE_REGEXP;

	public ItemDefinition() {
		// setting attribute types map to avoid possible 
		// null pointer exceptions
		mData.put(ATTR_ATTR_TYPES, new HashMap<String, String>());
	}

	@Override
	public String getType() {
		return (String) mData.get(ATTR_TYPE);
	}

	@Override
	public void setType(String aType) {
		Assert.isTrue(aType.matches(ITEM_TYPE_REGEXP),
				"Invalid definition type '" + aType + "' argument!");
		mData.put(ATTR_TYPE, aType);
	}

	@Override
	public String getPrimaryKeyAttribute() {
		return (String) mData.get(ATTR_PK);
	}

	@Override
	public void setPrimaryKeyAttribute(String aAttribute) {
		Assert.isTrue(aAttribute.matches(ATTR_NAME_REGEXP),
				"Invalid definition PK attribute '" + aAttribute + "' argument!");
		mData.put(ATTR_PK, aAttribute);
		// registering the primary key as an attribute also
		getAttributeTypes().put(aAttribute, "string");
	}

	@Override
	public Map<String, String> getAttributeTypes() {
		return (Map<String, String>) mData.get(ATTR_ATTR_TYPES);
	}

	@Override
	public void setAttributeTypes(Map<String, String> aAttributes) {
		for (Map.Entry<String, String> lE : aAttributes.entrySet()) {
			Assert.isTrue(lE.getKey().matches(ATTR_NAME_REGEXP),
					"Invalid definition attribute '" + lE.getKey() + "' argument!");
			Assert.isTrue(!lE.getKey().equals(Item.DEFAULT_PK_ATTR_NAME),
					"The attribute name '" + Item.DEFAULT_PK_ATTR_NAME
					+ "' can be used for primary key only!");

			String lType = lE.getValue();
			Assert.isTrue(lType.startsWith("string")
					|| lType.startsWith("integer")
					|| lType.startsWith("double")
					|| lType.startsWith("long")
					|| lType.startsWith("boolean"),
					"Invalid definition attribute type '" + lE.getValue() + "' argument!");

		}
		getAttributeTypes().putAll(aAttributes);
	}

	@Override
	public Boolean containsAttribute(String aAttribute) {
		return getAttributeTypes().containsKey(aAttribute);
	}

	@Override
	public void writeToToken(Token aToken) {
		toMap(aToken.getMap());
	}

	@Override
	public void readFromToken(Token aToken) {
		fromMap(aToken.getMap());
	}

	@Override
	public String toString() {
		return "ItemDefinition{" + "data=" + mData + '}';
	}

	@Override
	public void validate() throws Exception {
		Assert.notNull(getAttributeTypes(), "Missing attribute types on item definition!");

		if (null == getPrimaryKeyAttribute()) {
			setPrimaryKeyAttribute(Item.DEFAULT_PK_ATTR_NAME);
		}
	}

	@Override
	public void toMap(Map<String, Object> aMap) {
		aMap.put(ATTR_TYPE, getType());
		aMap.put(ATTR_PK, getPrimaryKeyAttribute());
		aMap.put(ATTR_ATTR_TYPES, getAttributeTypes());
	}

	@Override
	public void fromMap(Map<String, Object> aMap) {
		setType((String) aMap.get(ATTR_TYPE));
		setPrimaryKeyAttribute((String) aMap.get(ATTR_PK));

		Map lAttrTypes = (Map) aMap.get(ATTR_ATTR_TYPES);
		lAttrTypes.remove(aMap.get(ATTR_PK));
		setAttributeTypes(lAttrTypes);
	}
}

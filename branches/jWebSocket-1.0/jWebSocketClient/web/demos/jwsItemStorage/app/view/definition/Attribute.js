Ext.define('IS.view.definition.Attribute' ,{
	extend: 'Ext.form.FieldSet',
	alias: 'widget.d_attribute',
	title: 'Attribute',
	collapsible: true,
	defaults: {
		layout: {
			type: 'hbox',
			defaultMargins: {
				top: 0, 
				right: 5, 
				bottom: 0, 
				left: 0
			}
		}
	}, 
	items: [{
		xtype: 'textfield',
		name : 'attr',
		fieldLabel: 'Name',
		maskRe: /^[a-zA-Z0-9]/,
		regex: /^[a-zA-Z]+([a-zA-Z0-9]+)*/,
		allowBlank: false
	}, {
		xtype: 'combo',
		name : 'type',
		queryMode: 'local',
		displayField: 'name',
		fieldLabel: 'Type',
		store: 'DataTypes',
		allowBlank: false,
		editable: false
	}, {
		xtype: 'fieldcontainer',
		msgTarget : 'side',
		defaults: {
			hideLabel: true
		},
		items: [{
			xtype: 'button',
			iconCls: 'attr_remove',
			tooltip: 'Remove attribute',
			handler: function(){
				var lFieldSet = this.up('fieldset');
				lFieldSet.up('form').remove(lFieldSet);	
			}
		}, {
			xtype: 'button',
			iconCls: 'attr_edit',
			tooltip: 'Edit attribute type...'
		}]
	}]
});

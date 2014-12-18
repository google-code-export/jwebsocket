/*
 * File: app/view/vpDiagram.js
 *
 * This file was generated by Sencha Architect version 3.1.0.
 * http://www.sencha.com/products/architect/
 *
 * This file requires use of the Ext JS 5.0.x library, under independent license.
 * License of Sencha Architect does not include license for Ext JS 5.0.x. For more
 * details see http://www.sencha.com/license or contact license@sencha.com.
 *
 * This file will be auto-generated each and everytime you save your project.
 *
 * Do NOT hand edit this file.
 */

Ext.define('BPMNEditor.view.vpDiagram', {
	extend: 'Ext.container.Viewport',
	alias: 'widget.vpdiagram',
	requires: [
		'BPMNEditor.view.vpDiagramViewModel',
		'BPMNEditor.view.pToolbox',
		'BPMNEditor.view.tpCanvas',
		'Ext.tab.Panel',
		'Ext.grid.Panel',
		'Ext.grid.View',
		'Ext.grid.column.Number'
	],
	viewModel: {
		type: 'vpdiagram'
	},
	layout: 'border',
	items: [{
			xtype: 'toolbar',
			region: 'north',
			id: 'loginBar',
			cls: 'loginBar',
			ui: 'plain',
			items: [{
					xtype: 'button',
					id: 'connection_status',
					text: 'disconnected',
					baseCls: 'status offline',
					handler: function() {
						var lAnimationOptions = {
							out: true,
							duration: 500,
							autoClear: false
						};
						Ext.Anim.run(this.parent, 'fade', Ext.apply(lAnimationOptions, {
							after: function() {
								var lText = this.getComponent("client_id").getText();
								var lClass = this.getComponent("connection_status").getCls();
								this.parent.getTabBar().add({
									xtype: 'button',
									iconCls: lClass + " minimized",
									ui: 'plain',
									baseCls: 'x-tab-normal x-tab ' +
											'x-iconalign-center x-tab-icon ' +
											'x-layout-box-item x-stretched ' +
											'minimized',
									html: '<span class="x-button-label">' +
											lText.split("@").join("<br/>") + '</span>',
									handler: function() {
										Ext.Anim.run(this, 'fade', Ext.apply(lAnimationOptions, {
											after: function() {
												Ext.Anim.run(Ext.getCmp("loginBar"),
														'fade', {
															out: false,
															duration: 500,
															autoclear: false
														});
												this.destroy();
											},
											scope: this
										}));
									}
								});
							},
							scope: this.parent
						}));
					}
				}, {
					text: "|",
					baseCls: 'status separator'
				}, {
					xtype: 'button',
					text: 'Client id: -',
					id: 'client_id',
					baseCls: 'status'
				}, {
					text: "|",
					id: 'login_btn_separator',
					hidden: true,
					baseCls: 'status separator'
				}, {
					xtype: 'button',
					text: 'login',
					id: 'logout_button',
					cls: 'logout',
					hidden: true,
					handler: function() {
						var lText = this.getText().toLowerCase().trim();
						if (lText === "logout") {
							BPMNEditor.app.redirectTo("security/logout");
						} else {
							BPMNEditor.app.redirectTo("security/showLogin");
						}
					}
				}
			]
		},
		{
			xtype: 'panel',
			region: 'west',
			width: 150,
			layout: 'accordion',
			title: 'Toolbar',
			items: [
				{
					xtype: 'ptoolbox'
				}
			]
		},
		{
			xtype: 'tpcanvas',
			region: 'center'
		},
		{
			xtype: 'gridpanel',
			collapseMode: 'header',
			region: 'south',
			height: 50,
			animCollapse: true,
			collapsed: true,
			collapsible: true,
			title: 'Logs',
			columns: [
				{
					xtype: 'gridcolumn',
					weight: 1,
					width: 720,
					dataIndex: 'string',
					text: 'Log'
				}
			]
		},
		{
			xtype: 'gridpanel',
			region: 'east',
			width: 150,
			title: 'Properties',
			columns: [
				{
					xtype: 'gridcolumn',
					dataIndex: 'string',
					text: 'Property'
				},
				{
					xtype: 'numbercolumn',
					dataIndex: 'number',
					text: 'Value'
				}
			]
		}
	]

});
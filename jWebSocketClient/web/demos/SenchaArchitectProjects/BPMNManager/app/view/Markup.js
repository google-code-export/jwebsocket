/*
 * File: app/view/Markup.js
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

Ext.define('BPMNManager.view.Markup', {
    extend: 'Ext.container.Viewport',
    alias: 'widget.markup',

    requires: [
        'BPMNManager.view.MarkupViewModel',
        'Ext.grid.Panel',
        'Ext.grid.View',
        'Ext.selection.CheckboxModel',
        'Ext.toolbar.Paging',
        'Ext.tree.Panel',
        'Ext.tree.View',
        'Ext.tree.Column'
    ],

    viewModel: {
        type: 'markup'
    },
    layout: 'border',

    items: [
        {
            xtype: 'gridpanel',
            region: 'center',
            title: 'Files',
            forceFit: true,
            columns: [
                {
                    xtype: 'gridcolumn',
                    id: 'file',
                    resizable: false,
                    layout: 'fit',
                    dataIndex: 'string',
                    text: 'File'
                }
            ],
            selModel: {
                selType: 'checkboxmodel'
            },
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    dock: 'bottom',
                    width: 360,
                    displayInfo: true
                }
            ]
        },
        {
            xtype: 'treepanel',
            region: 'west',
            width: 157,
            title: 'Structure',
            viewConfig: {

            },
            columns: [
                {
                    xtype: 'treecolumn',
                    dataIndex: 'text',
                    text: 'Nodes',
                    flex: 1
                }
            ]
        }
    ]

});
/*
 * File: app/controller/BPMNController.js
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

Ext.define('BPMNEditor.controller.BPMNController', {
	extend: 'Ext.app.Controller',
	alias: 'controller.bpmnController',
	refs: {
		canvasTabPanel: 'tpcanvas',
		btnNew: 'pCanvas btnNew',
		uploadField: 'winloaddiagram filefield'
	},
	control: {
		"btnnew": {
			click: 'onMybuttonClick'
		},
		"btnopen": {
			click: 'onButtonClick'
		},
		"button[action=uploadDiagram]": {
			click: 'startUpload'
		}
	},
	onMybuttonClick: function(button, e, eOpts) {
		this.getCanvasTabPanel().add(Ext.create('BPMNEditor.view.pCanvas'));
	},
	onButtonClick: function(button, e, eOpts) {
		Ext.create('BPMNEditor.view.winLoadDiagram').show();
	},
	
	startUpload: function(aButton, aEvent, aOptions) {
		var lFileInputEl = this.getUploadField().fileInputEl, lIdx,
				lFiles = lFileInputEl && lFileInputEl.dom ? lFileInputEl.dom.files : [];
		if (lFiles.length > 0) {
			aButton.up('window').close();
			// Iterate through the file list
			for (lIdx = 0; lIdx < lFiles.length; lIdx++) {
				console.log(lFiles[lIdx]);
				// Now we need to bring up the uploader to upload file by file using the FileAPI from HTML5
			}
			Ext.Msg.alert('Upload in progress', JSON.stringify(lFiles));
		} else {
			this.getUploadField().markInvalid("Please you must select something!");
		}
	}
});

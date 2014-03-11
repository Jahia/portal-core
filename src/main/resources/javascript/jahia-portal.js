// dependencies: Jquery, Jquery-ui
// This file is automatically added to the html page when a portal is detected
// Take a look at the PortalInitFilter jahia filter

/**
 * @namespace
 */
var Jahia = Jahia || {};

Jahia.Utils = {
    getObjectSize: function (obj) {
        var size = 0, key;
        for (key in obj) {
            if (obj.hasOwnProperty(key)) size++;
        }
        return size;
    }
};

/**
 * This object is automatically instantiate by the Jahia filter PortalInitFilter
 *
 * @class Portal
 * @author kevan
 */
Jahia.Portal = function (options) {
    this.conf = Jahia.Portal.default;
    this.debug = options.debug ? options.debug : false;
    this.isModel = options.isModel ? options.isModel :false;
    this.isEditable = options.isEditable ? options.isEditable : false;
    this.isLocked = options.isLocked ? options.isLocked : false;
    this.fullTemplate = options.fullTemplate ? options.fullTemplate : false;

    this.baseURL = options.baseURL;
    this.portalPath = options.portalPath;
    this.portalTabPath = options.portalTabPath;

    this.$areas = [];
    this.areas = [];
    this.widgets = [];
};

/**
 * portal constants
 * @type {{WIDGETS_PORTAL_VIEW: string, TABS_PORTAL_VIEW: string, ADD_WIDGET_ACTION: string, MOVE_WIDGET_ACTION: string, COPY_PORTALMODEL_ACTION: string, FORM_TAB_VIEW: string, WIDGET_EVENT_MOVED_SUCCEEDED: string, WIDGET_EVENT_MOVED_FAILED: string, WIDGET_EVENT_MOVED_CANCELED: string, PORTAL_WIDGET_CLASS: string}}
 */
Jahia.Portal.constants = {
    WIDGETS_PORTAL_VIEW: ".widgets.json",
    TABS_PORTAL_VIEW: ".tabs.json",
    ADD_WIDGET_ACTION: ".addWidget.do",
    MOVE_WIDGET_ACTION: ".moveWidget.do",
    COPY_PORTALMODEL_ACTION: ".copyPortalModel.do",
    FORM_TAB_VIEW: ".form.json",

    WIDGET_EVENT_MOVED_SUCCEEDED: "moveSucceeded",
    WIDGET_EVENT_MOVED_FAILED: "moveFailed",
    WIDGET_EVENT_MOVED_CANCELED: "moveCanceled",

    EXTERNAL_WIDGET_DROP_CLASS: "widget_nodetype",
    EXTERNAL_WIDGET_DROP_NODEYPE: "widget_nodetype",
    EXTERNAL_WIDGET_DROP_VIEW: "widget_view",

    PORTAL_WIDGET_CLASS:    "portal_widget"
};
/**
 * portal conf
 * @type {{sortable_options: {connectWith: string, handle: string, revert: boolean, iframeFix: boolean}}}
 */
Jahia.Portal.default = {
    sortable_options: {
        connectWith: ".portal_area",
        handle: ".widget-header",
        revert: true,
        iframeFix: true
    }
};


Jahia.Portal.prototype = {
    /**
     * Init or re-init the drag&drop in all the portal areas, also attach the portal events to drag&drop actions
     *
     * @this {Portal}
     */
    initDragDrop: function () {
        var instance = this;

        // get all areas
        var $areas = $(instance.conf.sortable_options.connectWith);

        var newWidget = false;
        // sortable update callback
        instance.conf.sortable_options.update = function (event, ui) {
            // Test if we are on the destination col after sort update
            if ($(event.target).attr("id") == ui.item.parent().attr("id")) {
                var toArea = instance.getArea(ui.item.parent(instance.conf.sortable_options.connectWith).data("area-name"));
                var widget = instance.getWidget(ui.item.attr("id"));

                if(widget){
                    widget.performMove(toArea);
                }else {
                    newWidget = true;
                }
            }
        };

        // sortable start callback, used for store initial state of the item, allow to do some check in others callbacks
        instance.conf.sortable_options.start = function(event, ui) {
            ui.item.data('start_index', ui.item.index());
            ui.item.data('start_colId', $(ui.item).parent(instance.conf.sortable_options.connectWith).attr("id"));
        };

        // sortable stop callback
        instance.conf.sortable_options.stop = function(event, ui) {
            if(newWidget){
                //search for widget related datas in the item html
                var nodetypeEl = ui.item.hasClass(Jahia.Portal.constants.EXTERNAL_WIDGET_DROP_CLASS) ? ui.item : ui.item.find("." + Jahia.Portal.constants.EXTERNAL_WIDGET_DROP_CLASS);
                if(nodetypeEl.length > 0){
                    var nodetype = nodetypeEl.data(Jahia.Portal.constants.EXTERNAL_WIDGET_DROP_NODEYPE);
                    var view = nodetypeEl.data(Jahia.Portal.constants.EXTERNAL_WIDGET_DROP_VIEW);
                    if(nodetype){
                        //get next widget if exist
                        var next = ui.item.next();
                        var area = instance.getArea(ui.item.parent(instance.conf.sortable_options.connectWith).data("area-name"));
                        var beforeWidget = undefined;
                        if(next.length > 0){
                            beforeWidget = instance.getWidget(next.attr("id"));
                        }
                        // instanciate this new widget
                        instance.addNewWidget(nodetype, undefined, area, view, beforeWidget, ui.item);
                    }
                }
            }else {
                var start_pos = ui.item.data('start_index');
                var start_colId = ui.item.data('start_colId');
                if (start_pos == ui.item.index() && start_colId == $(ui.item).parent(instance.conf.sortable_options.connectWith).attr("id")) {
                    // User have started to drag the widget but this one is at the same place.
                    var widget = instance.getWidget(ui.item.attr("id"));
                    widget.getjQueryWidget().trigger(Jahia.Portal.constants.WIDGET_EVENT_MOVED_CANCELED);
                }
            }
        };

        if($areas.sortable){
            if(instance.isEditable){
                $areas.sortable(instance.conf.sortable_options);
            }
        }else {
            console.error("Missing portal dependency 'jquery-ui sortable, draggable'")
        }

        instance.$areas = $areas;
    },

    /**
     * Debug message
     *
     * @this {Portal}
     * @param message
     */
    _debug: function (message) {
        var instance = this;
        if (instance.debug) {
            console.debug("Portal: " + message)
        }
    },

    /**
     * Call the widgets types allowed for the current portal
     *
     * @this {Portal}
     * @param callback callback with widgets types in json format
     */
    getWidgetTypes: function (callback) {
        var instance = this;
        $.ajax(instance.baseURL + instance.portalPath + Jahia.Portal.constants.WIDGETS_PORTAL_VIEW).done(function (data) {
            instance._debug(data.length + " widgets loaded");
            callback(data);
        });
    },

    /**
     * Add a new widget to the current portal
     *
     * @this {Portal}
     * @param nodetype {String}
     * @param name {String} Widget name, optional, generated if not present.
     * @param toArea {Area} target area, optional, first if not present.
     * @param view {String} widget view displayed after load, optional, default view used if not present.
     * @param beforeWidget {Widget} insert before widget, optional, insert at the end of the area if not present
     * @param $htmlToReplace {Object} jquery object represent the item dropped in the area, mandatory if toArea is specified
     */
    addNewWidget: function (nodetype, name, toArea, view, beforeWidget, $htmlToReplace) {
        var instance = this;

        toArea = toArea ? toArea : instance.getAreaByIndex(0);
        var areaName = toArea._$area.data('area-name');
        instance._debug("Add widget:[" + name + "] nodetype:[" + nodetype + "] area:[" + areaName + "] beforeWidget:[" + beforeWidget + "]");

        var beforeWidgetPath = undefined;
        if(beforeWidget){
            beforeWidgetPath = beforeWidget._path;
        }

        var data = {
            nodetype: nodetype,
            name: name,
            col: areaName,
            beforeWidget: beforeWidgetPath
        };
        $.ajax({
            type: "POST",
            dataType: "json",
            traditional: true,
            url: instance.baseURL + instance.portalTabPath + Jahia.Portal.constants.ADD_WIDGET_ACTION,
            data: data
        }).done(function (data) {

            if(data.isGadget){
                instance.reloadTab(data.id, view);
            }else{
                var $widget = $("<div></div>").attr("id", "w_" + data.id).attr("class", "portal_widget");
                $widget.data("widget-gadget", false);
                $widget.data("widget-path", data.path);
                if(view){
                    $widget.data("widget-view", view);
                }

                toArea.registerWidget($widget, $htmlToReplace, false);
            }
        });
    },

    /**
     * Register a new Area for the current portal, this function is call by the portal area component automatically
     *
     * @this {Portal}
     * @param htmlID {String} area id, mandatory
     * @param widgetPath {String} widget path to load in this area, optional, used for load a specific widget in this area
     * @param widgetState {String} widget state to load in this area, optional, used for full state
     * @param widgetView {String} widget view to load in this area, optional, used for full view
     */
    registerArea: function (htmlID) {
        var instance = this;
        var $area = $("#" + htmlID);
        instance.areas[$area.data("area-name")] = new Jahia.Portal.Area($area, instance);
        instance.initDragDrop();
    },

    getArea: function (name) {
        var instance = this;
        return instance.areas[name];
    },

    /**
     * return the Area corresponding to a given index
     *
     * @this {Portal}
     * @param index {Number} area index
     * @returns {Area}
     */
    getAreaByIndex: function (index) {
        var instance = this;
        var $area = $($(instance.conf.sortable_options.connectWith).get(index));
        if($area.length == 0){
            instance._debug("No area at index: " + index);
            return undefined;
        }else {
            return instance.getArea($area.data("area-name"));
        }
    },

    /**
     * return the Widget corresponding to a given widget html id
     *
     * @this {Portal}
     * @param htmlId {String} widget html id
     * @returns {Widget}
     */
    getWidget: function (htmlId) {
        var instance = this;
        return instance.widgets[htmlId];
    },

    /**
     * return the Widget parent of the given html id
     *
     * @this {Portal}
     * @param htmlId {String} html id
     * @returns {Widget}
     */
    getCurrentWidget: function (htmlId) {
        var instance = this;
        return instance.getWidget($("#" + htmlId).parents("." + Jahia.Portal.constants.PORTAL_WIDGET_CLASS).attr("id"));
    },

    /**
     * delete a specific widget
     *
     * @this {Portal}
     * @param widget {Widget} widget to delete
     */
    deleteWidget: function(widget) {
        widget.performDelete();
    },

    /**
     * return the portal tab information in JSON format
     *
     * @this {Portal}
     * @param callback {function}
     */
    getTabFormInfo: function (callback) {
        var instance = this;
        instance._debug("Load form infos for portal tab: " + instance.portalTabPath);
        $.ajax({
            type: "GET",
            dataType: "json",
            url: instance.baseURL + instance.portalTabPath + Jahia.Portal.constants.FORM_TAB_VIEW
        }).done(function (data) {
                instance._debug("Portal tab form info successfully loaded");
                if (callback) {
                    callback(data);
                }
            });
    },

    /**
     * save the portal tab form
     *
     * @this {Portal}
     * @param callback {function}
     * @param form {Object} portal tab information to save
     * @param isNew {Boolean}
     */
    saveTabForm: function (form, callback, isNew) {
        var instance = this;
        var action = isNew ? "Add new" : "Edit";
        instance._debug( action + " portal tab: " + form.name);

        $.ajax({
            type: "POST",
            dataType: "json",
            traditional: true,
            url: isNew ? instance.baseURL + instance.portalPath + "/*" : instance.baseURL + instance.portalTabPath,
            data: instance._convertTabFormToJCRProps(form)
        }).done(function (data) {
                instance._debug("Portal tab form successfully saved");
                if(callback){
                    callback(data);
                }
                if(isNew){
                    window.location.href = instance.baseURL + instance.portalPath + "/" + data["j_nodename"] + ".html";
                }else {
                    window.location.reload();
                }
            });
    },

    /**
     * Delete current portal tab
     *
     * @this {Portal}
     * @param callback {function}
     */
    deleteCurrentTab: function(callback) {
        var instance = this;
        instance._debug("Delete tab: " + instance.portalTabPath);

        $.ajax({
            type: "POST",
            dataType: "json",
            traditional: true,
            url: instance.baseURL + instance.portalTabPath,
            data: {
                jcrMethodToCall: "delete"
            }
        }).done(function(data){
                if(callback){
                    callback(data)
                }
            window.location.href = instance.baseURL + instance.portalPath;
            });
    },

    /**
     * Instantiate a new portal based on the current one
     *
     * @this {Portal}
     * @param callback {function}
     */
    initPortalFromModel: function(callback){
        var instance = this;
        if(instance.isModel){
            instance._debug("Init user portal");

            $.ajax({
                type: "POST",
                dataType: "json",
                traditional: true,
                url: instance.baseURL + instance.portalPath + Jahia.Portal.constants.COPY_PORTALMODEL_ACTION,
                data: {}
            }).done(function(data){
                    if(callback){
                        callback(data)
                    }
                    window.location.href = instance.baseURL + data.path;
                });
        }else {
            instance._debug("Impossible to copy this portal, because is not a model");
        }
    },

    /**
     * Return portal tabs for the current portal in json format
     *
     * @this {Portal}
     * @param callback {function}
     */
    getTabs: function(callback) {
        var instance = this;
        instance._debug("Load tabs for portal tab: " + instance.portalPath);
        $.ajax({
            type: "GET",
            dataType: "json",
            url: instance.baseURL + instance.portalPath + Jahia.Portal.constants.TABS_PORTAL_VIEW
        }).done(function (data) {
                instance._debug(data.length + "portal tabs successfully loaded");
                if (callback) {
                    callback(data);
                }
            });
    },

    /**
     * Load a specific widget, construct the url to display a specific widget in specific state, view, and tab template.
     * Reload the page
     *
     * @this {Portal}
     * @param tabTemplate {String} portal tab template, optional, default used if not specify
     * @param widgetIdentifier {String} widget node identifier to load, mandatory
     * @param widgetState {String} widget state (full, box, ...), optional
     * @param widgetView {String} widget view (full, view, edit ...), optional
     */
    loadSingleWidget: function(tabTemplate, widgetIdentifier, widgetState, widgetView){
        var instance = this;
        var url = "";

        if(tabTemplate){
            url = instance.baseURL + instance.portalTabPath + "." + tabTemplate;
        }else {
            url = instance.baseURL + instance.portalTabPath;
        }

        url += (".html?w=" + widgetIdentifier);

        if(widgetState){
            url += ("&w_state=" + widgetState);
        }

        if(widgetView){
            url += ("&w_view=" + widgetView);
        }

        window.location.href = url;
    },

    /**
     * Reload the current portal tab
     *
     * @this {Portal}
     */
    reloadTab: function(widgetId, widgetView, widgetState, widgetSolo){
        var instance = this;
        var url = instance.baseURL + instance.portalTabPath + ".html";

        var paramArray = [];
        if (widgetId){
            paramArray.push("w=" + widgetId);
        }

        if (widgetView){
            paramArray.push("w_view=" + widgetView);
        }

        if (widgetState) {
            paramArray.push("w_state=" + widgetState);
        }

        if (widgetSolo) {
            paramArray.push("w_solo=" + widgetSolo);
        }

        for (var index = 0; index < paramArray.length; ++index) {
            if(index == 0){
                url += "?"
            }
            url += paramArray[index];
            if(index < (paramArray.length - 1)){
                url += "&";
            }
        }

        window.location.href = url;
    },

    lockPortal: function(){
        var instance = this;
        instance._debug("Lock portal");
        instance._makeAjaxPostRequest({
            "jcrNodeType": "jmix:portal",
            "j:locked" : true
        }, instance.baseURL + instance.portalPath, function(){
            instance.reloadTab();
        });
    },

    unlockPortal: function(){
        var instance = this;
        instance._debug("Unlock portal");
        instance._makeAjaxPostRequest({
            "jcrNodeType": "jmix:portal",
            "j:locked" : false
        }, instance.baseURL + instance.portalPath, function(){
            instance.reloadTab();
        });
    },

    _makeAjaxPostRequest: function (data, url, successCb, errorCb){
        $.ajax({
            type: "POST",
            dataType: "json",
            url: url,
            data: data
        }).done(function (data) {
            if(successCb){
                successCb(data)
            }
        });
    },

    _convertTabFormToJCRProps: function (form) {
        return {
            "jcrNodeType": "jnt:portalTab",
            "jcr:title": form.name,
            "j:templateName": form.template.key,
            "j:widgetSkin": form.widgetSkin.key,
            "jcrNormalizeNodeName" : true
        };
    }
};


Jahia.Portal.Area = function ($area, portal) {
    this._portal = portal;
    this._$area = $area;
    this._colPath = this._portal.portalTabPath + "/" + $area.data("area-name");

    this.init();
};

Jahia.Portal.Area.prototype = {

    /**
     * Load all widgets for the current area
     *
     * @this Area
     */
    init: function () {
        var instance = this;
        var areaName = instance._$area.data("area-name");

        instance._portal._debug("Load widgets for area: " + areaName);

        instance._$area.find("." + Jahia.Portal.constants.PORTAL_WIDGET_CLASS).each(function(index, widget){
            var $widget = $(widget);
            instance.registerWidget($widget);
        });
    },

    registerWidget: function ($widget, $htmlToReplace, forcedOriginalView) {
        var instance = this;
        instance._portal.widgets[$widget.attr('id')] = new Jahia.Portal.Widget($widget, $htmlToReplace, forcedOriginalView, instance);
    }
};


Jahia.Portal.Widget = function ($widget, $htmlToReplace, forcedOriginalView, area) {
    this._id = $widget.attr('id');
    this._jcrIdentifier = this._id.substring(2);
    this._isGadget = $widget.data("widget-gadget");
    this._path = $widget.data('widget-path');
    this._area = area;
    this._portal = area._portal;
    this._state = $widget.data('widget-state') ? $widget.data('widget-state') : "box";
    this._originalView = forcedOriginalView ? forcedOriginalView : ($widget.data('widget-view') ? $widget.data('widget-view') : "portal.view");
    this._initView = $widget.data('widget-view') ? $widget.data('widget-view') : "portal.view";
    this._currentView = this._originalView;

    this.init($widget, $htmlToReplace);
};

Jahia.Portal.Widget.prototype = {
    /**
     * Init the widget
     *
     * @param $htmlToReplace
     */
    init: function ($widget, $htmlToReplace) {
        var instance = this;
        instance._portal._debug("Load widget: " + instance._path);

        if($htmlToReplace){
            $htmlToReplace.replaceWith($widget);
        }

        if(!instance._isGadget){
            instance.load(instance._initView);
        }else {
            instance.attachEvents();
        }
    },

    /**
     * Attach event related to drag&drop actions on the widget
     */
    attachEvents: function() {
        var instance = this;
        //detach
        instance.getjQueryWidget().off();

        // Append when the server successfully make the move for the widget in the JCR
        instance.getjQueryWidget().on(Jahia.Portal.constants.WIDGET_EVENT_MOVED_SUCCEEDED, function(){
            instance._portal._debug("Widget successfully moved to " + instance._path);
        });
        // Append when the server failed to perform the move for widget in the JCR
        instance.getjQueryWidget().on(Jahia.Portal.constants.WIDGET_EVENT_MOVED_FAILED, function(){
            instance._portal._debug("Widget " + instance._path + " move failed");

            // Server cannot perform the move so rollback it in the page also
            instance._portal.$areas.sortable('cancel');
        });
        // Append when the widget return to his initial position
        instance.getjQueryWidget().on(Jahia.Portal.constants.WIDGET_EVENT_MOVED_CANCELED, function(){
            instance._portal._debug("Widget stay at " + instance._path);
        });
    },

    /**
     * Return the jQuery object corresponding to the widget
     *
     * @returns {*|jQuery|HTMLElement}
     */
    getjQueryWidget: function() {
        var instance = this;
        return $("#" + instance._id);
    },

    /**
     * Load or reload the widget
     *
     * @param view {String} specify a view, optional
     * @param callback {function}
     */
    load: function (view, callback) {
        var instance = this;
        instance.attachEvents();

        if(instance._isGadget){
            if(view){
                instance._portal.reloadTab(instance._jcrIdentifier, view);
            }else {
                instance._portal.reloadTab();
            }
        }else {
            if(!view){
                view = "portal.view";
            }

            $("#" + instance._id).load(instance._portal.baseURL + instance._path + "." + view + ".html.ajax?includeJavascripts=true", function(){
                if(instance._portal.isEditable){
                    instance._portal.initDragDrop();
                }
                instance._currentView = view;
                instance._portal._debug("widget " + instance._path + " loaded successfully");

                if(callback){
                    callback();
                }
            });
        }
    },

    /**
     * Perform a move to a specific Area
     *
     * @param toArea {Area}
     */
    performMove: function (toArea) {
        var instance = this;
        var areaName = toArea._$area.data("area-name");

        instance._portal._debug("Moved widget " + instance._path + " to " + areaName);

        var onTopOfWidget = instance._portal.getWidget($("#" + instance._id).next("." + Jahia.Portal.constants.PORTAL_WIDGET_CLASS).attr("id"));

        var data = {
            toArea: areaName,
            widget: instance._path,
            onTopOfWidget: onTopOfWidget ? onTopOfWidget._path : ""
        };
        $.ajax({
            type: "POST",
            dataType: "json",
            traditional: true,
            url: instance._portal.baseURL + instance._portal.portalTabPath + Jahia.Portal.constants.MOVE_WIDGET_ACTION,
            data: data
        }).done(function (newPositionInfo) {
                instance._path = newPositionInfo.path;
                instance._area = instance._portal.getArea(toArea);

                instance.getjQueryWidget().trigger(Jahia.Portal.constants.WIDGET_EVENT_MOVED_SUCCEEDED);
            }).fail(function () {
                instance.getjQueryWidget().trigger(Jahia.Portal.constants.WIDGET_EVENT_MOVED_FAILED);
            });
    },

    /**
     * Perform delete action on the current widget
     */
    performDelete: function() {
        var instance = this;
        $.ajax({
            type: "POST",
            data: {
                jcrMethodToCall:"delete"
            },
            dataType: "json",
            traditional: true,
            url: instance._portal.baseURL + instance._path
        }).done(function(){
                instance._portal._debug("Widget " + instance._path + " successfully deleted");
                // delete from html
                $("#" + instance._id).remove();
                // delete from portal
                delete instance._portal.widgets[instance._id];
            });
    },

    /**
     * Perform update action on the current widget
     *
     * @param data {Object} JSON object representing the current widget node
     * @param callback
     */
    performUpdate: function(data, callback) {
        var instance = this;
        $.ajax({
            type: "POST",
            data: data,
            dataType: "json",
            traditional: true,
            url: instance._portal.baseURL + instance._path
        }).done(function(response){
                instance._portal._debug("Widget " + instance._path + " successfully updated");

                if(callback){
                    callback(response);
                }
            }).fail(function(response){
                instance._portal._debug("Widget " + instance._path + " failed to update");
            });
    }
};
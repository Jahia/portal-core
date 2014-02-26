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
                var toArea = instance.getArea(ui.item.parent(instance.conf.sortable_options.connectWith).attr("id"));
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
                        var area = instance.getArea(ui.item.parent(instance.conf.sortable_options.connectWith).attr("id"));
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
        instance._debug("Add widget:[" + name + "] nodetype:[" + nodetype + "] area:[" + toArea + "] beforeWidget:[" + beforeWidget + "]");

        var col = 0;
        if(toArea){
            col = toArea._colIndex;
        }

        var beforeWidgetPath = undefined;
        if(beforeWidget){
            beforeWidgetPath = beforeWidget._path;
        }

        var data = {
            nodetype: nodetype,
            name: name,
            col: col,
            beforeWidget: beforeWidgetPath
        };
        $.ajax({
            type: "POST",
            dataType: "json",
            traditional: true,
            url: instance.baseURL + instance.portalTabPath + Jahia.Portal.constants.ADD_WIDGET_ACTION,
            data: data
        }).done(function (widget) {
            if(toArea){
                toArea.registerWidget(widget.path, $htmlToReplace, undefined, view, "view");
            }else {
                instance.getAreaByColIndex(0).registerWidget(widget.path);
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
    registerArea: function (htmlID, widgetPath, widgetState, widgetView) {
        var instance = this;
        instance.areas[htmlID] = new Jahia.Portal.Area(htmlID, Jahia.Utils.getObjectSize(instance.areas), instance, widgetPath, widgetState, widgetView);
        instance.initDragDrop();
    },

    /**
     * return the Area corresponding to a given id
     *
     * @this {Portal}
     * @param htmlID {String} area id
     * @returns {Area}
     */
    getArea: function (htmlID) {
        var instance = this;
        return instance.areas[htmlID];
    },

    /**
     * return the Area corresponding to a given index
     *
     * @this {Portal}
     * @param index {Number} area index
     * @returns {Area}
     */
    getAreaByColIndex: function (index) {
        var instance = this;
        return instance.getAreaByColName("col-" + index)
    },

    /**
     * return the Area corresponding to a given column name
     *
     * @this {Portal}
     * @param colName {Number} area colname (col-0, col-1, ...)
     * @returns {Area}
     */
    getAreaByColName: function (colName) {
        var instance = this;
        return instance.getArea($("." + colName).attr("id"));
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
    reloadTab: function(){
        var instance = this;
        window.location.href = instance.baseURL + instance.portalTabPath + ".html";
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

/**
 * Portal area object
 *
 * @constructor
 * @param id {String}
 * @param index {number}
 * @param portal {Portal}
 * @param widgetPath {String} optional, used for load a single specific widget in the area
 * @param widgetState {String} optional, used for load a single specific widget in the area
 * @param widgetView {String} optional, used for load a single specific widget in the area
 */
Jahia.Portal.Area = function (id, index, portal, widgetPath, widgetState, widgetView) {
    this._id = id;
    this._portal = portal;
    this._colIndex = index;
    this._colName = "col-" + index;
    this._colPath = this._portal.portalTabPath + "/" + this._colName;

    // Add "col-" jcr name to the html class
    $("#" + id).addClass(this._colName);

    if(widgetPath){
        // load a specific single widget
        this.load(widgetPath, widgetState, widgetView)
    }else {
        // load all coll widget
        this.loadAll();
    }
};

Jahia.Portal.Area.prototype = {
    /**
     * Load a single specific widget
     *
     * @this Area
     * @param path {String}
     * @param state {String}
     * @param view {String}
     */
    load: function (path, state, view) {
        var instance = this;
        instance._portal._debug("Load widget: " + path);

        instance.registerWidget(path, undefined, state, view);
    },

    /**
     * Load all widgets for the current area
     *
     * @this Area
     */
    loadAll: function () {
        var instance = this;

        instance._portal._debug("Load widgets for col: " + instance._colName);

        $.ajax(this._portal.baseURL + this._colPath + ".widgets.json").done(function (data) {
            instance._portal._debug(data.length + " widgets found");

            data.forEach(function (widget) {
                instance.registerWidget(widget.path);
            });
        }).fail(function () {
                instance._portal._debug("No col: " + instance._colName);
            });
    },

    /**
     * Register a widget for the current area
     *
     * @this Area
     * @param path {String} widget path
     * @param state {String} widget state, optional
     * @param view {String} view used to load the widget, optional
     * @param $htmlToReplace {Object} jquery object to replace by the widget content, optional
     * @param forcedOriginalView {String} specify a original view different from the view used to load the widget, optional
     */
    registerWidget: function (path, $htmlToReplace, state, view, forcedOriginalView) {
        var instance = this;
        var widgetHtmlId = "w_" + Math.random().toString(36).substring(7);
        instance._portal.widgets[widgetHtmlId] = new Jahia.Portal.Widget(widgetHtmlId, path, $htmlToReplace, state, view, forcedOriginalView, instance);
    }
};

/**
 * Portal widget object
 *
 * @constructor
 * @param id {String} widget id, mandatory
 * @param path {String} widget path, mandatory
 * @param area {Area} parent area, mandatory
 * @param $htmlToReplace {Object} jquery object to replace by the widget content, optional
 * @param state {String} widget state, optional
 * @param view {String} widget view, optional, used to load the widget
 * @param forcedOriginalView {String} widget original view, optional, used to force original view
 */
Jahia.Portal.Widget = function (id, path, $htmlToReplace, state, view, forcedOriginalView, area) {
    this._id = id;
    this._path = path;
    this._area = area;
    this._portal = area._portal;
    this._state = state ? state : "box";
    this._originalView = forcedOriginalView ? forcedOriginalView : (view ? view : "view");
    this._initView = view ? view : "view";
    this._currentView = this._originalView;

    this.init($htmlToReplace);
};

Jahia.Portal.Widget.prototype = {
    /**
     * Init the widget
     *
     * @param $htmlToReplace
     */
    init: function ($htmlToReplace) {
        var instance = this;
        instance._portal._debug("Load widget: " + instance._path);

        var wrapper = "<div id='" + instance._id + "' class='" + Jahia.Portal.constants.PORTAL_WIDGET_CLASS + "'></div>";
        if($htmlToReplace){
            $htmlToReplace.replaceWith(wrapper);
        }else {
            $("#" + instance._area._id).append(wrapper);
        }

        instance.load(instance._initView);
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

        if(!view){
            view = instance._originalView;
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
    },

    /**
     * Perform a move to a specific Area
     *
     * @param toArea {Area}
     */
    performMove: function (toArea) {
        var instance = this;

        instance._portal._debug("Moved widget " + instance._path + " to " + toArea._colName);

        var onTopOfWidget = instance._portal.getWidget($("#" + instance._id).next("." + Jahia.Portal.constants.PORTAL_WIDGET_CLASS).attr("id"));

        var data = {
            toArea: toArea._colPath,
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
                instance._area = instance._portal.getAreaByColName(newPositionInfo.col);

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
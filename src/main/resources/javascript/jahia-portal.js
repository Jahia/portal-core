// dependencies: Jquery, Jquery-ui

if (!Jahia) {
    var Jahia = [];
}

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
 * Portal object
 * @constructor
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

    PORTAL_WIDGET_CLASS:    "portal_widget"
};

Jahia.Portal.default = {
    sortable_options: {
        connectWith: ".portal_area",
        handle: ".widget-header",
        revert: true,
        iframeFix: true
    }
};

Jahia.Portal.prototype = {
    initDragDrop: function () {
        var instance = this;

        var $areas = $(instance.conf.sortable_options.connectWith);

        instance.conf.sortable_options.update = function (event, ui) {
            // Test if we are on the destination col after sort update
            if ($(event.target).attr("id") == ui.item.parent().attr("id")) {
                var toArea = instance.getArea(ui.item.parent(instance.conf.sortable_options.connectWith).attr("id"));
                var widget = instance.getWidget(ui.item.attr("id"));

                widget.performMove(toArea);
            }
        };

        instance.conf.sortable_options.start = function(event, ui) {
            ui.item.data('start_index', ui.item.index());
            ui.item.data('start_colId', $(ui.item).parent(instance.conf.sortable_options.connectWith).attr("id"));
        };
        instance.conf.sortable_options.stop = function(event, ui) {
            var start_pos = ui.item.data('start_index');
            var start_colId = ui.item.data('start_colId');
            if (start_pos == ui.item.index() && start_colId == $(ui.item).parent(instance.conf.sortable_options.connectWith).attr("id")) {
                // User have started to drag the widget but this one is at the same place.
                var widget = instance.getWidget(ui.item.attr("id"));
                widget.getjQueryWidget().trigger(Jahia.Portal.constants.WIDGET_EVENT_MOVED_CANCELED);
            }
        };

        if($areas.sortable){
            $areas.sortable(instance.conf.sortable_options);
        }else {
            console.error("Missing portal dependency 'jquery-ui sortable, draggable'")
        }

        instance.$areas = $areas;
    },

    _debug: function (message) {
        var instance = this;
        if (instance.debug) {
            console.debug("Portal: " + message)
        }
    },

    getWidgetTypes: function (callback) {
        var instance = this;
        $.ajax(instance.baseURL + instance.portalPath + Jahia.Portal.constants.WIDGETS_PORTAL_VIEW).done(function (data) {
            instance._debug(data.length + " widgets loaded");
            callback(data);
        });
    },

    addNewWidget: function (nodetype, name) {
        var instance = this;
        instance._debug("Add widget: " + name + " [" + nodetype + "]");
        var data = {
            nodetype: nodetype,
            name: name
        };
        $.ajax({
            type: "POST",
            dataType: "json",
            traditional: true,
            url: instance.baseURL + instance.portalTabPath + Jahia.Portal.constants.ADD_WIDGET_ACTION,
            data: data
        }).done(function (widget) {
                instance.getAreaByColIndex(0).registerWidget(widget.path);
            });
    },

    registerArea: function (htmlID, widgetPath, widgetState, widgetView) {
        var instance = this;
        instance.areas[htmlID] = new Jahia.Portal.Area(htmlID, "col-" + Jahia.Utils.getObjectSize(instance.areas), instance, widgetPath, widgetState, widgetView);
    },

    getArea: function (htmlID) {
        var instance = this;
        return instance.areas[htmlID];
    },

    getAreaByColIndex: function (index) {
        var instance = this;
        return instance.getAreaByColName("col-" + index)
    },

    getAreaByColName: function (colName) {
        var instance = this;
        return instance.getArea($("." + colName).attr("id"));
    },

    getWidget: function (htmlId) {
        var instance = this;
        return instance.widgets[htmlId];
    },

    getCurrentWidget: function (htmlId) {
        var instance = this;
        return instance.getWidget($("#" + htmlId).parents("." + Jahia.Portal.constants.PORTAL_WIDGET_CLASS).attr("id"));
    },

    deleteWidget: function(widget) {
        widget.performDelete();
    },

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
 * @param id
 * @param name
 * @param portal
 * @param path
 * @constructor
 */
Jahia.Portal.Area = function (id, name, portal, widgetPath, widgetState, widgetView) {
    this._id = id;
    this._portal = portal;
    this._colName = name;
    this._colPath = this._portal.portalTabPath + "/" + this._colName;

    if(widgetPath){
        // load a specific single widget
        this.load(widgetPath, widgetState, widgetView)
    }else {
        // load all coll widget
        this.loadAll();
    }
};

Jahia.Portal.Area.prototype = {
    load: function (path, state, view) {
        var instance = this;

        // Add "col-" jcr name to the html class
        $("#" + instance._id).addClass(instance._colName);

        instance._portal._debug("Load widget: " + path);

        instance.registerWidget(path, state, view);
    },

    loadAll: function () {
        var instance = this;

        // Add "col-" jcr name to the html class
        $("#" + instance._id).addClass(instance._colName);

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

    registerWidget: function (path, state, view) {
        var instance = this;
        var widgetHtmlId = "w_" + Math.random().toString(36).substring(7);
        instance._portal.widgets[widgetHtmlId] = new Jahia.Portal.Widget(widgetHtmlId, path, state, view, instance);
    }
};

/**
 * Portal widget object
 * @param id
 * @param path
 * @param area
 * @constructor
 */
Jahia.Portal.Widget = function (id, path, state, view, area) {
    this._id = id;
    this._path = path;
    this._area = area;
    this._portal = area._portal;
    this._state = state ? state : "box";
    this._originalView = view ? view : "view";
    this._currentView = this._originalView;

    this.init();
};

Jahia.Portal.Widget.prototype = {
    init: function () {
        var instance = this;
        instance._portal._debug("Load widget: " + instance._path);

        var wrapper = "<div id='" + instance._id + "' class='" + Jahia.Portal.constants.PORTAL_WIDGET_CLASS + "'></div>";
        $("#" + instance._area._id).append(wrapper);

        instance.load();
    },

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

    getjQueryWidget: function() {
        var instance = this;
        return $("#" + instance._id);
    },

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



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
Jahia.Portal = function () {
    this.conf = Jahia.Portal.default;
    this.activated = false;
    this.areas = [];
    this.widgets = [];

    //register portal in the window object
    window.portal = this;
};

Jahia.Portal.constants = {
    WIDGETS_PORTAL_VIEW: ".widgets.json",
    ADD_WIDGET_ACTION: ".addWidget.do",
    FORM_TAB_VIEW: ".form.json",

    PORTAL_WIDGET_CLASS:    "portal_widget"
};

Jahia.Portal.default = {
    debug: true,
    sortable_options: {
        connectWith: ".portal_area",
        handle: ".widget-header"
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

                widget.performMove(toArea, function () {
                    $areas.sortable('cancel');
                });
            }
        };

        $areas.sortable(instance.conf.sortable_options);
    },

    _debug: function (message) {
        var instance = this;
        if (instance.conf.debug) {
            console.debug("Portal: " + message)
        }
    },

    setPortalPath: function (path) {
        var instance = this;
        instance.portalPath = path;
    },

    setPortalTabPath: function (path) {
        var instance = this;
        instance.portalTabPath = path;
    },

    setUrlBase: function (base) {
        var instance = this;
        instance.urlBase = base;
    },

    getWidgetTypes: function (callback) {
        var instance = this;
        $.ajax(instance.urlBase + instance.portalPath + Jahia.Portal.constants.WIDGETS_PORTAL_VIEW).done(function (data) {
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
            url: instance.urlBase + instance.portalTabPath + Jahia.Portal.constants.ADD_WIDGET_ACTION,
            data: data
        }).done(function (widget) {
                instance.getAreaByColIndex(0).registerWidget(widget.path);
            });
    },

    registerArea: function (urlBase, portalPath, portalTabPath, htmlID) {
        var instance = this;

        // init portals relative paths
        if (!instance.activated) {
            instance.activated = true;
            instance.portalPath = portalPath;
            instance.portalTabPath = portalTabPath;
            instance.urlBase = urlBase;
        }

        instance.areas[htmlID] = new Jahia.Portal.Area(htmlID, "col-" + Jahia.Utils.getObjectSize(instance.areas), instance);
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
        return instance.getWidget($("#" + htmlId).parent("." + Jahia.Portal.constants.PORTAL_WIDGET_CLASS).attr("id"));
    },

    deleteWidget: function(widget) {
        var instance = this;
        widget.performDelete();
    },

    getTabFormInfo: function (callback) {
        var instance = this;
        instance._debug("Load form infos for portal tab: " + instance.portalTabPath);
        $.ajax({
            type: "GET",
            dataType: "json",
            url: instance.urlBase + instance.portalTabPath + Jahia.Portal.constants.FORM_TAB_VIEW
        }).done(function (data) {
                instance._debug("Portal tab form info successfully loaded");
                if (callback) {
                    callback(data);
                }
            });
    },

    saveTabForm: function (form, callback) {
        var instance = this;
        instance._debug("Save form for portal tab: " + instance.portalTabPath);

        $.ajax({
            type: "POST",
            dataType: "json",
            traditional: true,
            url: instance.urlBase + instance.portalTabPath,
            data: instance._convertTabFormToJCRProps(form)
        }).done(function (data) {
                instance._debug("Portal tab form successfully saved");
                if(callback){
                    callback();
                }
                window.location.reload();
            });
    },

    _convertTabFormToJCRProps: function (form) {
        return {
            "jcrNodeType": "jnt:portalTab",
            "jcr:title": form.name,
            "j:templateName": form.template.key,
            "j:widgetsSkin": form.widgetsSkin.key
        };
    }
};

/**
 * Portal area object
 * @param id
 * @param name
 * @param portal
 * @constructor
 */
Jahia.Portal.Area = function (id, name, portal) {
    this._id = id;
    this._portal = portal;
    this._colName = name;
    this._colPath = this._portal.portalTabPath + "/" + this._colName;

    this.load();
};

Jahia.Portal.Area.prototype = {
    load: function () {
        var instance = this;

        // Add "col-" jcr name to the html class
        $("#" + instance._id).addClass(instance._colName);

        instance._portal._debug("Load widgets for col: " + instance._colName);

        $.ajax(this._portal.urlBase + this._colPath + ".widgets.json").done(function (data) {
            instance._portal._debug(data.length + " widgets found");

            data.forEach(function (widget) {
                instance.registerWidget(widget.path);
            });
        }).fail(function () {
                instance._portal._debug("No col: " + instance._colName);
            });
    },

    registerWidget: function (path) {
        var instance = this;
        var widgetHtmlId = "w_" + Math.random().toString(36).substring(7);
        instance._portal.widgets[widgetHtmlId] = new Jahia.Portal.Widget(widgetHtmlId, path, instance);
    }
};

/**
 * Portal widget object
 * @param id
 * @param path
 * @param area
 * @constructor
 */
Jahia.Portal.Widget = function (id, path, area) {
    this._id = id;
    this._path = path;
    this._area = area;
    this._portal = area._portal;

    this.load();
};

Jahia.Portal.Widget.prototype = {
    load: function () {
        var instance = this;
        instance._portal._debug("Load widget: " + instance._path);

        var wrapper = "<div id='" + instance._id + "' class='" + Jahia.Portal.constants.PORTAL_WIDGET_CLASS + "'></div>";
        $("#" + instance._area._id).append(wrapper);
        $.ajax(instance._portal.urlBase + instance._path + ".view.html.ajax").done(function (data) {
            $("#" + instance._id).html(data);

            instance._portal.initDragDrop();
            instance._portal._debug("widget " + instance._path + " loaded successfully");
        });
    },

    performMove: function (toArea, failCallBack) {
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
            url: instance._portal.urlBase + instance._portal.portalTabPath + ".moveWidget.do",
            data: data
        }).done(function (newPositionInfo) {
                instance._portal._debug("Widget " + instance._path + " successfully moved to " + newPositionInfo.path);
                instance._path = newPositionInfo.path;
                instance._area = instance._portal.getAreaByColName(newPositionInfo.col);
            }).fail(function () {
                if (failCallBack) {
                    failCallBack();
                }
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
            url: instance._portal.urlBase + instance._path
        }).done(function(){
                instance._portal._debug("Widget " + instance._path + " successfully deleted");
                // delete from html
                $("#" + instance._id).remove();
                // delete from portal
                delete instance._portal.widgets[instance._id];
            });
    },

    sayHello: function(){
        console.log("blblblblbl");
    }
};

var portal = new Jahia.Portal();



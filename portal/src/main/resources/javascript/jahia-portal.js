// dependencies: Jquery, Jquery-ui, bootstrap-tabs

if (!Jahia) {
    var Jahia = [];
}

Jahia.Utils = {
    getObjectSize: function(obj){
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
    this.widgets = false;

    // TODO execute this, when the page is fully loaded
    this._init();
};

Jahia.Portal.constants = {
    WIDGETS_PORTAL_VIEW: ".widgets.json",
    ADD_WIDGET_ACTION: ".addWidget.do"
};

Jahia.Portal.default = {
    debug: true,
    sortable_options: {
        connectWith: ".tab-column",
        handle: ".portlet-header",
        revert: true
    }
};

Jahia.Portal.prototype = {
    _init: function () {
        var instance = this;

        //init areas
        var $tab_columns = $(".tab-column");
        instance.conf.sortable_options.update = function (event, ui) {
            /*
             var $col = ui.item.parent(".tab-column");
             var col = instance._cols[$col.attr("id")];

             alert("- Component " + ui.item.attr("id") + " has been dropped in col " + col._id + ". \n" +
             "- JCR path for the col: " + col._path + ". \n" +
             "- Index: " + ui.item.index())
             */
        };
        $tab_columns.sortable(instance._sortable_options);
        $tab_columns.disableSelection();

        //register portal in the window object
        window.portal = this;
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

    setPortalTabPath: function(path) {
        var instance = this;
        instance.portalTabPath = path;
    },

    setUrlBase: function (base) {
        var instance = this;
        instance.urlBase = base;
    },

    getWidgets: function (callback) {
        var instance = this;
        $.ajax(instance.urlBase + instance.portalPath + Jahia.Portal.constants.WIDGETS_PORTAL_VIEW).done(function (data) {
            instance._debug(data.length + " widgets loaded");
            callback(data);
        });
    },

    addWidget: function (nodetype, name) {
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
        }).done(function () {
                instance._debug("widget added");
            });
    },

    registerArea: function (urlBase, portalPath, portalTabPath, htmlID) {
        var instance = this;

        // init portals relative paths
        if(!instance.activated){
            instance.activated = true;
            instance.portalPath = portalPath;
            instance.portalTabPath = portalTabPath;
            instance.urlBase = urlBase;
        }

        instance.areas[htmlID] = new Jahia.Portal.Area(htmlID, "col-" + Jahia.Utils.getObjectSize(instance.areas), instance);
    },

    getArea: function(htmlID) {
        var instance = this;
        return instance.areas[htmlID];
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
    this._name = name;
    this._portal = portal;
    this.widgets = [];

    this.load();
};

Jahia.Portal.Area.prototype = {
    load: function(){
        var instance = this;

        instance._portal._debug("Load widgets for col: " + instance._name);

        $.ajax(instance._portal.urlBase + instance._portal.portalTabPath + "/" + instance._name + ".widgets.json").done(function(data){
            instance._portal._debug(data.length + " widgets found");

            data.forEach(function(widget){
                var widgetHtmlId = instance._name + "_w" + Jahia.Utils.getObjectSize(instance.widgets);
                instance.widgets[widgetHtmlId] = new Jahia.Portal.Area.Widget(widgetHtmlId, widget.path, instance);
            });
        });
    },

    getWidget: function(htmlId) {
        var instance = this;
        return instance.widgets[htmlId];
    }
};

Jahia.Portal.Area.Widget = function(id, path, area){
    this._id = id;
    this._path = path;
    this._area = area;
    this._portal = area._portal;

    this.load();
};

Jahia.Portal.Area.Widget.prototype = {
    load: function() {
        var instance = this;
        instance._portal._debug("Load widget: " + instance._path);

        var wrapper = "<div id='" + instance._id + "'></div>";
        $("#" + instance._area._id).append(wrapper);
        $.ajax(instance._portal.urlBase + instance._path + ".view.html.ajax").done(function(data){
            $("#" + instance._id).html(data);
        });
    }
};

var portal = new Jahia.Portal();



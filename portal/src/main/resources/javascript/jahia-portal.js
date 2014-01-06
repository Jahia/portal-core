// dependencies: Jquery, Jquery-ui, bootstrap-tabs

if (!Jahia) {
    var Jahia = [];
}

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

    registerArea: function (htmlID, jcrPath) {
        var instance = this;
        instance.areas[htmlID] = new Jahia.Portal.Area(htmlID, jcrPath, instance);
    }
};

Jahia.Portal.Area = function (id, path, portal) {
    this._id = id;
    this._portal = portal;
    this._path = path;
};

Jahia.Portal.Area.prototype = {

};

var portal = new Jahia.Portal();



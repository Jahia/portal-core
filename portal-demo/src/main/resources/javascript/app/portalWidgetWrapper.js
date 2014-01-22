Jahia.Portal.AdvancedWidgetWrapper = function (widgetId, editable) {
    this._minimize = true;
    this.widget = {};
    this.$widget = {};
    this.editable = editable;

    this.init(widgetId);
};

Jahia.Portal.AdvancedWidgetWrapper.prototype = {
    init: function (widgetId) {
        var instance = this;
        instance.widget = portal.getCurrentWidget(widgetId);
        instance.$widget = instance.widget.getjQueryWidget();
        if(instance.editable){
            instance.switchEditListener();
            instance.deleteListener();
        }
        instance.minimizeListener();
    },

    switchEditListener: function() {
        var instance = this;
        instance.$widget.find(".edit_switch").on("click", function(){
            if (instance.widget.state != "edit") {
                instance.widget.load("edit");
            } else {
                instance.widget.load();
            }
        });
    },

    deleteListener: function () {
        var instance = this;
        instance.$widget.find(".delete_action").on("click", function(){
            portal.deleteWidget(instance.widget);
        });
    },

    minimizeListener: function () {
        var instance = this;
        instance.$widget.find(".minimize_action").on("click", function(){
            instance._minimize = !instance._minimize;
            if(instance._minimize){
                $(this).removeClass("icon-minus");
                $(this).addClass("icon-plus");
            }else {
                $(this).removeClass("icon-plus");
                $(this).addClass("icon-minus");
            }
            instance.$widget.find(".widget-content").toggle();
        });
    }
};



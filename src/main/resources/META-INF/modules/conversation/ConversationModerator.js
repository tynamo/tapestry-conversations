define([ 'jquery' ], function() {

    var ConversationModerator = function(baseURI, defaultURIparameters,
            keepAlive, endOnClose, idleCheckSeconds, warnBeforeSeconds,
            warnBeforeHandler, endedHandler) {

        var vars = {
            baseURI : null,
            defaultURIparameters : null,
            keepAlive : null,
            endOnClose : null,
            idleCheckSeconds : null,
            warnBeforeSeconds : null,
            warnBeforeHandler : null,
            endedHandler : null,
            idleCheckId : null
        };

        var myroot = this;

        this.construct = function(baseURI, defaultURIparameters, keepAlive,
                endOnClose, idleCheckSeconds, warnBeforeSeconds,
                warnBeforeHandler, endedHandler) {
            vars.baseURI = baseURI;
            vars.defaultURIparameters = defaultURIparameters;
            vars.keepAlive = keepAlive;
            vars.endOnClose = endOnClose;
            vars.idleCheckSeconds = idleCheckSeconds;
            vars.warnBeforeSeconds = warnBeforeSeconds;
            vars.warnBeforeHandler = warnBeforeHandler;
            vars.endedHandler = endedHandler;
        };

        this.checkIdle = function() {
            $.ajax({
                url : vars.baseURI + "checkidle" + vars.defaultURIparameters
                        + vars.keepAlive + "&warn=" + vars.warnBeforeSeconds
                        + "&timestamp=" + (new Date()).getTime(),
                dataType : 'json',
                success : function(response) {
                    myroot.handleIdleCheckResult(response.nextCheck,
                            response.warnFor);
                },
                type : 'GET'
            });
        };

        this.end = function() {
            if (!vars.endOnClose) {
                return;
            }

            $.ajax({
                url : vars.baseURI + "end" + vars.defaultURIparameters + false,
                type : 'GET'
            });
        };

        this.refresh = function() {
            $.ajax({
                url : vars.baseURI + "refresh" + vars.defaultURIparameter
                        + 'true',
                type : 'GET'
            });
        };

        this.checkIdleNext = function(nextCheck) {
            if (typeof (nextCheck) == 'undefined' || nextCheck <= 0) {
                return;
            }

            if (vars.idleCheckId != null) {
                clearTimeout(vars.idleCheckId);
            }

            vars.idleCheckId = setTimeout(this.checkIdle.bind(this),
                    nextCheck * 1000);
        };

        this.callHandler = function(handlerName, arg) {
            // handlerName should be a string identifier of form
            // "obj.property.function"
            var pos = handlerName.lastIndexOf('.');
            var context = null;

            if (pos > 0) {
                context = eval(handlerName.substring(0, pos));
            }

            if (handlerName.substr(handlerName.length - 2, 2) == '()') {
                handlerName = handlerName.substring(0, handlerName.length - 2);
            }

            var operation = eval(handlerName);
            // FIXME should log something if operation doesn't exist
            if (typeof (operation) == 'function') {
                if (context == null) {
                    operation(arg);
                } else {
                    operation.bind(context)(arg);
                }
            }
        };

        this.warnOfEnd = function(inSeconds) {
            if (vars.warnBeforeHandler != null) {
                this.callHandler(vars.warnBeforeHandler, inSeconds);
            } else {
                alert('The page will become idle soon...');
            }
        };

        this.handleIdleCheckResult = function(nextCheck, warnFor) {
            if (isNaN(nextCheck))
                nextCheck = -1;

            if (nextCheck <= 0) {
                if (vars.endedHandler != null) {
                    this.callHandler(vars.endedHandler);
                }
                return;
            }

            if (!isNaN(warnFor)) {
                if (warnFor > 0) {
                    this.warnOfEnd(warnFor);
                }
            }

            this.checkIdleNext(nextCheck);
        };

        // construct the class and pass all options
        this.construct(baseURI, defaultURIparameters, keepAlive, endOnClose,
                idleCheckSeconds, warnBeforeSeconds, warnBeforeHandler,
                endedHandler);
    }

    this.moderator = null;

    this.startConversation = function(baseURI, defaultURIparameters, keepAlive,
            endOnClose, idleCheckSeconds, warnBeforeSeconds, warnBeforeHandler,
            endedHandler) {
        moderator = new ConversationModerator(baseURI, defaultURIparameters,
                keepAlive, endOnClose, idleCheckSeconds, warnBeforeSeconds,
                warnBeforeHandler, endedHandler);

        if (idleCheckSeconds != null && idleCheckSeconds > 0) {
            moderator.checkIdleNext(idleCheckSeconds);
        }
    }

    return {
        moderator : moderator,
        startConversation : startConversation
    }
})

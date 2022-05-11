var exec = require('cordova/exec');

exports.addButton = function (configs,success, error) {
    exec(success, error, 'NativeViewAdder', 'addButton', [configs]);
};

exports.clearViews = function (success, error) {
    exec(success, error, 'NativeViewAdder', 'clearViews', [configs]);
};



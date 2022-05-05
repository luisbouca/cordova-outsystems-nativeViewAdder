var exec = require('cordova/exec');

exports.addButton = function (configs,success, error) {
    exec(success, error, 'NativeViewAdder', 'addButton', [configs]);
};



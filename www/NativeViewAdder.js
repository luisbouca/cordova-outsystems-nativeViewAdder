var exec = require('cordova/exec');

exports.addButton = function (title,colorOrimage,success, error) {
    exec(success, error, 'NativeViewAdder', 'addButton', [title,colorOrimage]);
};



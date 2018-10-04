var replace = require('replace-in-file');
var packageFile = require("./package.json");
var buildVersion = packageFile.version;
const options = {
    files: 'src/environments/*.ts',
    from: /version: '(.*)'/g,
    to: "version: '"+ buildVersion + "'",
    allowEmptyPaths: false,
};

const optionsHtml = {
    files: 'src/index.html',
    from: /app-version - (.*)/g,
    to: 'app-version - v' + buildVersion + '">',
    allowEmptyPaths: false,
}
 
try {
    console.log('Build version: ' + buildVersion);
    let changedFiles = replace.sync(options);
    if (changedFiles == 0) {
        throw "Please make sure that file '" + options.files + "' has \"version: ''\"";
    }
    console.log('Build version set in environment files');

    changedFiles = replace.sync(optionsHtml);
    console.log("changedFiles = "+changedFiles);
    if (changedFiles == 0) {
        throw "Please make sure that file '" + optionsHtml.files + "' has \"meta tag with content app-version - v\"";
    }

    console.log('Build version set in index.html file');
}
catch (error) {
    console.error('Error occurred:', error);
    throw error
}
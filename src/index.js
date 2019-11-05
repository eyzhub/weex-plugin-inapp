/* globals alert */
const weexPluginInapp = {
  show () {
    alert('Module weexPluginInapp is created sucessfully ');
  }
};

const meta = {
  weexPluginInapp: [{
    lowerCamelCaseName: 'show',
    args: []
  }]
};

function init (weex) {
  weex.registerModule('weexPluginInapp', weexPluginInapp, meta);
}

export default {
  init: init
};

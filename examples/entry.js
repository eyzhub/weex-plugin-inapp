import Vue from 'vue';

import weex from 'weex-vue-render';

import WeexPluginInapp from '../src/index';

weex.init(Vue);

weex.install(WeexPluginInapp)

const App = require('./index.vue');
App.el = '#root';
new Vue(App);

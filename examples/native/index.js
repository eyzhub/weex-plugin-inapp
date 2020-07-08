// { "framework": "Vue" } 

/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 0);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ (function(module, exports, __webpack_require__) {

var __vue_exports__, __vue_options__
var __vue_styles__ = []

/* styles */
__vue_styles__.push(__webpack_require__(1)
)

/* script */
__vue_exports__ = __webpack_require__(2)

/* template */
var __vue_template__ = __webpack_require__(3)
__vue_options__ = __vue_exports__ = __vue_exports__ || {}
if (
  typeof __vue_exports__.default === "object" ||
  typeof __vue_exports__.default === "function"
) {
if (Object.keys(__vue_exports__).some(function (key) { return key !== "default" && key !== "__esModule" })) {console.error("named exports are not supported in *.vue files.")}
__vue_options__ = __vue_exports__ = __vue_exports__.default
}
if (typeof __vue_options__ === "function") {
  __vue_options__ = __vue_options__.options
}
__vue_options__.__file = "/Users/dseeker/eyzmedia/weex-plugin-inapp/examples/index.vue"
__vue_options__.render = __vue_template__.render
__vue_options__.staticRenderFns = __vue_template__.staticRenderFns
__vue_options__._scopeId = "data-v-13fd3750"
__vue_options__.style = __vue_options__.style || {}
__vue_styles__.forEach(function (module) {
  for (var name in module) {
    __vue_options__.style[name] = module[name]
  }
})
if (typeof __register_static_styles__ === "function") {
  __register_static_styles__(__vue_options__._scopeId, __vue_styles__)
}

module.exports = __vue_exports__
module.exports.el = 'true'
new Vue(module.exports)


/***/ }),
/* 1 */
/***/ (function(module, exports) {

module.exports = {
  "wrapper": {
    "justifyContent": "center",
    "alignItems": "center"
  },
  "logo": {
    "width": "424",
    "height": "200"
  },
  "greeting": {
    "textAlign": "center",
    "marginTop": "20",
    "lineHeight": "80",
    "fontSize": "36",
    "color": "#41B883"
  },
  "message": {
    "fontSize": "26",
    "color": "#727272"
  },
  "button": {
    "marginTop": "20",
    "marginRight": "20",
    "marginBottom": "20",
    "marginLeft": "20",
    "paddingTop": "20",
    "paddingRight": "20",
    "paddingBottom": "20",
    "paddingLeft": "20",
    "backgroundColor": "#1ba1e2",
    "color": "#ffffff"
  },
  "red": {
    "backgroundColor": "#FF0000"
  },
  "yellow": {
    "backgroundColor": "#808080"
  }
}

/***/ }),
/* 2 */
/***/ (function(module, exports) {

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//

const weexPluginInapp = weex.requireModule('weexPluginInapp');
module.exports = {
	data: {
		logo: 'http://img1.vued.vanthink.cn/vued08aa73a9ab65dcbd360ec54659ada97c.png',
		response: {},
		productIds: ['android.test.purchased', 'com.company.app.inapp.id'],
		products: []
	},
	methods: {
		showDialog: function () {
			// weexPluginInapp.show();
			weexPluginInapp.show(JSON.stringify({ title: 'In App Purchase', message: weex.config.env.osName + ' Module weexPluginInapp was created sucessfully' }));
		},
		buy: function () {
			// weexPluginInapp.buy(JSON.stringify({id : this.products[0].productId}), (data) => {
			let productId = weex.config.env.osName == 'android' ? 'android.test.purchased' : this.products[0].productId;
			weexPluginInapp.buy(productId, data => {
				console.log('-> weexPluginInapp buy', data);
				this.response = data;
			});
		},
		manageSubscriptions: function () {
			weexPluginInapp.manageSubscriptions();
		},
		restorePurchases: function () {
			weexPluginInapp.restorePurchases(JSON.stringify({}), purchases => {
				console.log('-> weexPluginInapp restorePurchases', purchases);
				this.response = purchases;
			});
		},
		getProductInfo: function () {
			weexPluginInapp.getProductInfo(JSON.stringify({ list: this.productIds }), data => {
				console.log('-> weexPluginInapp info', data);
				this.products = data.result.products;
				this.response = data;
			});
		},
		getReceipt: function () {
			//weexPluginInapp.display();
			//weexPluginInapp.show(JSON.stringify({num:Math.random()}));
			//weexPluginInapp.info(JSON.stringify({num:Math.random()}), (data) => {
			weexPluginInapp.getReceipt(JSON.stringify({}), data => {
				console.log('-> weexPluginInapp getReceipt', data);
				this.response = data;
			});
		},
		testFailure: function () {
			weexPluginInapp.buy('android.test.canceled', data => {
				console.log('-> weexPluginInapp testFailure', data);
				this.response = data;
			});
		},
		testUnavailable: function () {
			weexPluginInapp.buy('android.test.item_unavailable', data => {
				console.log('-> weexPluginInapp testUnavailable', data);
				this.response = data;
			});
		}
	}
};

/***/ }),
/* 3 */
/***/ (function(module, exports) {

module.exports={render:function (){var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;
  return _c('div', {
    staticClass: ["wrapper"]
  }, [_c('image', {
    staticClass: ["logo"],
    attrs: {
      "src": _vm.logo
    }
  }), _c('text', {
    staticClass: ["greeting"]
  }, [_vm._v("The plugin develop environment is ready!")]), _c('div', {
    staticStyle: {
      display: "flex",
      flexDirection: "row"
    }
  }, [_c('div', {
    staticClass: ["button"],
    on: {
      "click": _vm.showDialog
    }
  }, [_c('text', {
    staticStyle: {
      color: "#fff"
    }
  }, [_vm._v("Show Dialog")])]), _c('div', {
    staticClass: ["button"],
    on: {
      "click": _vm.manageSubscriptions
    }
  }, [_c('text', {
    staticStyle: {
      color: "#fff"
    }
  }, [_vm._v("manageSubscriptions")])])]), _c('div', {
    staticStyle: {
      display: "flex",
      flexDirection: "row"
    }
  }, [_c('div', {
    staticClass: ["button"],
    on: {
      "click": _vm.getProductInfo
    }
  }, [_c('text', {
    staticStyle: {
      color: "#fff"
    }
  }, [_vm._v("getProductInfo")])]), _c('div', {
    staticClass: ["button"],
    on: {
      "click": _vm.restorePurchases
    }
  }, [_c('text', {
    staticStyle: {
      color: "#fff"
    }
  }, [_vm._v("restorePurchases")])])]), _c('div', {
    staticStyle: {
      display: "flex",
      flexDirection: "row"
    }
  }, [_c('div', {
    staticClass: ["button", "red"],
    on: {
      "click": _vm.testFailure
    }
  }, [_c('text', {
    staticStyle: {
      color: "#fff"
    }
  }, [_vm._v("Buy (Fail)")])]), _c('div', {
    staticClass: ["button"],
    on: {
      "click": _vm.buy
    }
  }, [_c('text', {
    staticStyle: {
      color: "#fff"
    }
  }, [_vm._v("BUY")])]), _c('div', {
    staticClass: ["button", "yellow"],
    on: {
      "click": _vm.testUnavailable
    }
  }, [_c('text', {
    staticStyle: {
      color: "#fff"
    }
  }, [_vm._v("Buy (Unavailable)")])])]), _c('text', {
    staticClass: ["message"]
  }, [_vm._v(_vm._s(_vm.response))]), (_vm.products.length) ? _c('text', {
    staticClass: ["message"]
  }, [_vm._v(_vm._s(_vm.products[0].title) + ": " + _vm._s(_vm.products[0].price) + " | " + _vm._s(_vm.products[0].productId))]) : _vm._e()])
},staticRenderFns: []}
module.exports.render._withStripped = true

/***/ })
/******/ ]);
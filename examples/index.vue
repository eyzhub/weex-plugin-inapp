<template>
	<div class="wrapper">
	<image :src="logo" class="logo" />
	<text class="greeting">The plugin develop environment is ready!</text>
	<!-- <text class="message">Let's develop a weex plugin!</text> -->

	<div style="display: flex; flex-direction: row;">
		<div @click="showDialog" class="button">
			<text style="color:#fff">Show Dialog</text>
		</div>
		<div @click="manageSubscriptions" class="button">
			<text style="color:#fff">manageSubscriptions</text>
		</div>
	</div>
	<div style="display: flex; flex-direction: row;">
		<div @click="getProductInfo" class="button">
			<text style="color:#fff">getProductInfo</text>
		</div>
		<div @click="restorePurchases" class="button">
			<text style="color:#fff">restorePurchases</text>
		</div>
	</div>

	<div style="display: flex; flex-direction: col;">
		<div @click="buy(id)" class="button" v-for="id in productIds">
			<text style="color:#fff; text-align: center">{{ id }}</text>
		</div>
	</div>

	<div style="display: flex; flex-direction: row;">
		<div @click="testFailure" class="button red">
			<text style="color:#fff">Buy (Fail)</text>
		</div>
		<div @click="buy" class="button">
			<text style="color:#fff">BUY</text>
		</div>
		<div @click="testUnavailable" class="button yellow">
			<text style="color:#fff">Buy (Unavailable)</text>
		</div>
		<div @click="subscribe" class="button">
			<text style="color:#fff">Subscribe</text>
		</div>
	</div>
	<text class="message">{{response}}</text>
	<text class="message" v-if="products.length">{{products[0].title}}: {{products[0].price}} | {{products[0].productId}}</text>
  </div>
</template>

<script>
	const weexPluginInapp = weex.requireModule('weexPluginInapp');
	module.exports = {
		data: {
			logo: 'http://img1.vued.vanthink.cn/vued08aa73a9ab65dcbd360ec54659ada97c.png',
			response: {},
			productIds: ['android.test.purchased','sooner.de_svod.30d.14t.795'],
			products: [{productId: "test.sub"}]
		},
		methods: {
			showDialog: function() {
				// weexPluginInapp.show();
				weexPluginInapp.show(JSON.stringify({title: 'In App Purchase', message: weex.config.env.osName+' Module weexPluginInapp was created sucessfully'}));
			},
			buy: function(id) {
				// weexPluginInapp.buy(JSON.stringify({id : this.products[0].productId}), (data) => {
				// let productId = (weex.config.env.osName == 'android') ? 'android.test.purchased' : this.products[0].productId
				let productId = id ? id : this.productIds[0]
				weexPluginInapp.buy(productId, (data) => {
					console.log('-> weexPluginInapp buy', data)
					this.response = data
				})
			},
			subscribe: function(id) {
				weexPluginInapp.show(JSON.stringify({title: 'subscribe', message: id}));
				// weexPluginInapp.buy(JSON.stringify({id : this.products[0].productId}), (data) => {
				let productId = id ? id : this.productIds[0]
				weexPluginInapp.subscribe(productId, (data) => {
					console.log('-> weexPluginInapp subscribe', data)
					this.response = data
				})
			},
			manageSubscriptions: function() {
				weexPluginInapp.manageSubscriptions()
			},
			restorePurchases: function() {
				weexPluginInapp.restorePurchases(JSON.stringify({}), (purchases) => {
					console.log('-> weexPluginInapp restorePurchases', purchases)
					this.response = purchases
				})
			},
			getProductInfo: function() {
				weexPluginInapp.getProductInfo(JSON.stringify({list : this.productIds}), (data) => {
					console.log('-> weexPluginInapp info', data)
					this.products = data.result.products
					this.response = data
				})
			},
			getReceipt: function() {
				//weexPluginInapp.display();
				//weexPluginInapp.show(JSON.stringify({num:Math.random()}));
				//weexPluginInapp.info(JSON.stringify({num:Math.random()}), (data) => {
				weexPluginInapp.getReceipt(JSON.stringify({}), (data) => {
					console.log('-> weexPluginInapp getReceipt', data)
					this.response = data
				})
			},
			testFailure: function() {
				weexPluginInapp.buy('android.test.canceled', (data) => {
					console.log('-> weexPluginInapp testFailure', data)
					this.response = data
				})
			},
			testUnavailable: function() {
				weexPluginInapp.buy('android.test.item_unavailable', (data) => {
					console.log('-> weexPluginInapp testUnavailable', data)
					this.response = data
				})
			},
		}
	}
</script>

<style>
	.wrapper {
		justify-content: center;
		align-items: center;
	}
	.logo {
		width: 424px;
		height: 200px;
	}
	.greeting {
		text-align: center;
		margin-top: 20px;
		line-height: 80px;
		font-size: 36px;
		color: #41B883;
	}
	.message {
		font-size: 26px;
		color: #727272;
	}
	.button {
		margin: 20px;
		padding:20px;
		background-color:#1ba1e2;
		color:#fff;
	}
	.red {
		background-color: red;
	}
	.yellow {
		background-color: gray;
	}
</style>
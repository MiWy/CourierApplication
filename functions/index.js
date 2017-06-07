var functions = require('firebase-functions');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');
admin.initializeApp(functions.config().firebase);

const gmailEmail = encodeURIComponent(functions.config().gmail.email);
const gmailPassword = encodeURIComponent(functions.config().gmail.password);
const mailTransport = nodemailer.createTransport(
    `smtps://${gmailEmail}:${gmailPassword}@smtp.gmail.com`);
const APP_NAME = 'Courier App';

exports.sendNotificationToCouriers = functions.database.ref('/orders/{$projectId}')
	.onWrite(event => {
		let order = event.data.current.val();
		let orderStateChanged = false;
		let orderCreated = false;
		let msg = 'Order status changed'
		let typeOfOrder = order.type;

		// Do things if order didn't exist before
		if(!event.data.previous.exists()) {
			orderCreated = true;
		}

		// Do things if order existed before (but was updated or sth)
		if(!orderCreated && event.data.changed()) {
			orderStateChanged = true;
		}

		// Do when ordered has been updated
		if(orderStateChanged) {
			if(typeOfOrder === "taken") {	
				// Do when the order is accepted
				var customerToken = order.userToken;
				var timestamp = order.timeStamp;
				msg = 'Courier ' + order.whoDeliversName + ' accepted your order.';
				let payloadChanged = {
					data: {
						title: "Order status changed",
						mainTxt: msg,
						type: typeOfOrder,
						timeStamp: timestamp.toString()
					}
				};
				const options = {
			    		priority: "high",
			    		timeToLive: 60*60*2
			   		};
				return admin.messaging().sendToDevice(customerToken, payloadChanged, options)
					.then(function(response) {
	   					 console.log("Successfully sent message:", response);
	  				})
	  				.catch(function(error) {
	    				console.log("Error sending message:", error);
	  				});

			} else if(typeOfOrder === "canceled") {
				// Do when order is cancelled

				var customerToken = order.userToken;
				msg = 'Your order was canceled.';
				let payloadCanceled = {
					data: {
						title: "Order status changed",
						mainTxt: msg,
						type: typeOfOrder
					}
				};
				const options = {
					priority: "high",
					timeToLive: 60*60*2
				};
				return admin.messaging().sendToDevice(customerToken, payloadCanceled, options)
					.then(function(response) {
						console.log("Successfully sent message: ", response);
					})
					.catch(function(error) {
						console.log("Error sending message: ", error);
					});
			
			} else if(typeOfOrder === "finished") {
				// Do when order is finished

				var customerToken = order.userToken;
				msg = 'Your order was delivered.';
				let payloadCanceled = {
					data: {
						title: "Order status changed",
						mainTxt: msg,
						type: typeOfOrder
					}
				};
				const options = {
					priority: "high",
					timeToLive: 60*60*2
				};
				sendFinishedEmail(order);
				return admin.messaging().sendToDevice(customerToken, payloadCanceled, options)
					.then(function(response) {
						console.log("Successfully sent message: ", response);
					})
					.catch(function(error) {
						console.log("Error sending message: ", error);
					});


			}
		}

		//Do if order was created
		if(orderCreated) {
			msg = 'From: '+ order.from + '\nTo: ' + order.to + '\nDistance: ' + order.distance + '.';
			return loadCouriers().then(users => {
				let tokens = [];
				for(let user of users) {
					tokens.push(user.instanceId);
				}
				var timestamp = order.timeStamp;
				let payload = {
					data: {
						title: "New order",
						mainTxt: msg,
						type: typeOfOrder,
						timeStamp: timestamp.toString()
						}
				};
				const options = {
		    		priority: "high",
		    		timeToLive: 60*60*2
		   		};

				return admin.messaging().sendToDevice(tokens, payload, options).then(response => {
		      		// For each message check if there was an error.
		      		const tokensToRemove = [];
		      		response.results.forEach((result, index) => {
		       			const error = result.error;
		        		if (error) {
		          			console.error('Failure sending notification to', tokens[index], error);
		          			// Cleanup the tokens who are not registered anymore.
		          			if (error.code === 'messaging/invalid-registration-token' ||
		              			error.code === 'messaging/registration-token-not-registered') {
		          			}
		      	  		}
		     		});
		      		return Promise.all(tokensToRemove);
		    	});
			});
		}
	})

function loadCouriers() {
	let dbRef = admin.database().ref('/users').orderByChild('role').equalTo('courier');
	let defer = new Promise((resolve, reject) => {
		dbRef.once('value', (snap) => {
			let data = snap.val();
			let users = [];
			for(var property in data) {
				users.push(data[property]);
			}
			resolve(users);
		}, (err) => {
			reject(err);
		});
	});
	return defer;
}

// Change e-mail address.
function sendFinishedEmail(order) {
	let orderId = order.userTimeStamp;
	let headermsg = 'Order ' + orderId.toString() + ' was delivered.';
	var customerUid = order.customersUid;
	var courierNick = order.whoDeliversName;
	var phonevar = order.phoneNumber;
	var timestamp = order.timeStamp;
	var distance = order.distance;
	var dateOfCreation = order.date;
	var start = order.from;
	var end = order.to;
	const mailOptions = {
		from: '"Courier App" <noreply@firebase.com>',
		to: 'courierapptestexample@examplemail.com'
	};
	mailOptions.subject = headermsg;
	mailOptions.text = 'User\'s ' + customerUid + ' order was delivered. Details:' +
		'\n Starting point: ' + start + '\n Delivery to: ' + end + '\n Distance: ' + distance +
		'\n Courier name: ' + courierNick;
	return mailTransport.sendMail(mailOptions).then(() => {
		console.log('E-mail about finished order sent to: ', 'owner.');
	});
}
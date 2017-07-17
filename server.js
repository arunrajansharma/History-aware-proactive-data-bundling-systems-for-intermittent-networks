/*
File Name: server.js
Deployement : AWS EC2 instance
Purpose : The Nodejs server 
Course  : CSE 534 - Fundamental of Computer Networks.
*/


const express = require('express');
const http = require('http');
const url = require('url');
const opn = require('opn');
const WebSocket = require('ws');
const bodyParser = require('body-parser');
const app = express();
const fs = require('fs');
const mkdirp = require('mkdirp');

app.use(express.static('public'));
app.use(bodyParser.urlencoded({extended:false}));
app.use(bodyParser.json())

const server = http.createServer(app);
const wss = new WebSocket.Server({server});
var i = 0;
CLIENTS = {};
userCounter = 1;

wss.on('connection', function connection(ws) {
	if(ws.upgradeReq.connection.remoteAddress in CLIENTS) {
		//we have the history. Simply send zip file.
		// means, the user is already registered with the system.
		console.log("Sending file to "+ws.upgradeReq.connection.remoteAddress);
		var fileName = "./userFolder/"+ws.upgradeReq.connection.remoteAddress+"/bundle.zip";
		fs.exists(fileName, function(exists) {
			  if (exists) {
			    fs.stat(fileName, function(error, stats) {
			      fs.open(fileName, "r", function(error, fd) {
			        var buffer = new Buffer(stats.size);
			        console.log(Object.prototype.toString.apply(buffer)); 
					console.log(Object.prototype.toString.apply(ws.options));
			        fs.read(fd, buffer, 0, buffer.length, null, function(error, bytesRead, buffer) {
			          //var data = buffer.toString("utf8", 0, buffer.length);
			           ws.send(buffer);
			          //console.log(data);
			           fs.close(fd);
			        });
			      });
			    });
			  }
			});
	} 
	else{
		// new user, Add the user to the dictionary of all registered users.
		CLIENTS[ws.upgradeReq.connection.remoteAddress] = ws.upgradeReq.connection.remoteAddress;
	}
	// run the below code whenever the server gets any message on the websocket.
	// We get user's browsing history as a websocket message
	ws.on('message', function incoming(message) { 				
 			console.log('received: %d %s', ++i, message);
 			mkdirp("./userFolder/"+ws.upgradeReq.connection.remoteAddress, function(err) { 
 				if(err) {
 					console.log(err);
 					return;
 				}
				fs.writeFile("./userFolder/"+ws.upgradeReq.connection.remoteAddress+"/training_config.json",message, function(err){
					if(err){
						 console.log(err);
						 return;
					}
					// otherwise file got saved
					console.log("file got saved");
					
					// This is where we spawn the python process to get url and save them on the disk
					var spawn = require("child_process").spawn;
					var process = spawn('python',["history_anaysis.py",ws.upgradeReq.connection.remoteAddress]);
					process.stdout.on('data', function (data){
						console.log("Python script returned : %s", data)
								console.log(Object.prototype.toString.apply(data)); 
								console.log("Sending file to "+ws.upgradeReq.connection.remoteAddress);
								
								var fileName = "./userFolder/"+ws.upgradeReq.connection.remoteAddress+"/bundle.zip";
								fs.exists(fileName, function(exists) {
									  if (exists) {
									    fs.stat(fileName, function(error, stats) {
									      fs.open(fileName, "r", function(error, fd) {
									        var buffer = new Buffer(stats.size);
									        console.log(Object.prototype.toString.apply(buffer)); 
											console.log(Object.prototype.toString.apply(ws.options));
											
									        fs.read(fd, buffer, 0, buffer.length, null, function(error, bytesRead, buffer) {
									          //var data = buffer.toString("utf8", 0, buffer.length);
									           ws.send(buffer);
									          //console.log(data);
									          fs.close(fd);
									        });
									      });
									    });
									  }
									});	
					}); // process stdout ends here
				});
		});//mkdir ends
		});

		ws.on('close', function xyz(data) {
			console.log("Code returned is "+ data);
			console.log("close is being executed  "+ws.upgradeReq.connection.remoteAddress);
			console.log("Removing "+ ws.upgradeReq.connection.remoteAddress+" from active users");
			//delete CLIENTS[ws.upgradeReq.connection.remoteAddress];

		});
}); // wss ends here
/*
  # the 'fast refresh' method
 This function runs every T seconds (60 seconds here) and for all the active users, It spawns the python code 
 for each active users, that in turn, refreshes the data on disk.
*/
function updateData() {
	var value;
	wss.clients.forEach(function each(client) {
		value = client.upgradeReq.connection.remoteAddress
  		console.log(client.upgradeReq.connection.remoteAddress);
  		var spawn = require("child_process").spawn;
		var process = spawn('python',["history_anaysis.py",value]);
  		process.stdout.on('data', function (data){
			console.log("Python script returned : %s", data)
			console.log(Object.prototype.toString.apply(data));
			console.log("Sending file to "+value);
					
			var fileName = "./userFolder/"+value+"/bundle.zip";
			fs.exists(fileName, function(exists) {
				  if (exists) {
				    fs.stat(fileName, function(error, stats) {
				      fs.open(fileName, "r", function(error, fd) {
				        var buffer = new Buffer(stats.size);
				        console.log(Object.prototype.toString.apply(buffer)); 
						console.log(Object.prototype.toString.apply(client.options));
						
				        fs.read(fd, buffer, 0, buffer.length, null, function(error, bytesRead, buffer) {
				          //var data = buffer.toString("utf8", 0, buffer.length);
				           client.send(buffer);
				          //console.log(data);
				          fs.close(fd);
				        });
				      });
				    });
				}
			});
	
		}); // process stdout ends here
 	});
}

//This is the timeout for calling function updateData. This function refreshes the data for all connected users and sends it to them
setInterval(updateData, 60000);


/*
  # the 'slow refresh' method
 This function runs every T seconds (10 minutes here) and for all the registered users, It spawns the python code 
 for each active users, that in turn, refreshes the data on disk.
*/
function updateBackgroundData() {
	var value;
	Object.keys(CLIENTS).forEach(function(key) {
    	value = CLIENTS[key];
    	console.log("Refreshing data for registered client "+value);
    	var spawn = require("child_process").spawn;
		var process = spawn('python',["history_anaysis.py",value]);
	});
}
//This is the timeout for calling function updateBackgroundData. This function refreshes the data for all registered users
setInterval(updateBackgroundData, 600000);


/*
The code below will not get hit by websocket connections. It is for standard http calls.
*/


function downloadFile(url, callback){
	console.log("Inside downloadFile function");
	var options = {
                        host: 'http://www.allthingsdistributed.com/2016/03/10-lessons-from-10-years-of-aws.html',
                        port: 80
                };

                http.get(options, function(res) {
                        console.log("Got response: " + res.statusCode);
                        var file = fs.createWriteStream('./serverFolder/');
                        res.pipe(file)
                }).on('error', function(e) {
                        console.log("Got error: " + e.message);
                });

	callback();
	return;
}



var multer = require('multer');
var upload = multer({ dest: './uploads' });


app.get('/', function (req, res) {
   res.sendFile(__dirname+"/"+"index.html");
})

app.get('/store', function(req, res){
	res.sendFile(__dirname+"/public/imges/"+"store.jpg");
})

/*

	This method process the JSON request from the client and save the URL sent in the request as files 
*/
app.post('/processJSON', function(req, res){
	console.log("Reaching here")
	
    console.log(req.body);
    console.log(req.body.URL_1);
    console.log(req.body.URL_2);
     
    
    console.log("Trying to download the files");
    //console.log(req.body.URL_1);
    var file = fs.createWriteStream('./uploads/url1_data');
    var request = http.get(req.body.URL_1,function(response){
    				response.pipe(file);
    				file.on('finish',function(){

    				});

    });

    res.end("Success");
})


app.post('/process_get', function(req, res){
	response = {
				fname:req.body.first_name, lname:req.body.last_name
				};
	console.log(response);
	res.end(JSON.stringify(response));
})


app.post('/file_upload', upload.single('sample'), function(req, res){		
	
	 console.log("Reachine file_upload function");
	 var tmp_path = req.file.path;
	  var target_path = './uploads/' + req.file.originalname;

	  /** A better way to copy the uploaded file. **/
	  var src = fs.createReadStream(tmp_path);
	  var dest = fs.createWriteStream(target_path);
	  src.pipe(dest);
	  src.on('end', function() { console.log("Success") });
	  src.on('error', function(err) { console.log('error'); });
	  fs.unlink(tmp_path);
	  res.end("Success");
})

app.post('/download_file', function(req,res){
	console.log("Reaching download_file code");
	//console.log(req.body.image);
	//console.log(req.body.report);
	//res.send(JSON.stringify(response));
	 var file = __dirname+'/public/imges/'+req.body.image;
	 var file1  = __dirname+'/uploads/'+req.body.report;
	 res.download(file,file1);
	 //res.download(file1);
})


/*
This code below starts the server at the mentioned ip and port number.
*/
server.listen(8086,"127.0.0.1", function listening(){
   console.log('Listening on %s %d',server.address().address, server.address().port);
});
